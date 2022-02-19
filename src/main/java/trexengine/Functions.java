package trexengine;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import trex.common.*;
import trex.packets.PubPkt;

import java.util.concurrent.CopyOnWriteArrayList;

import static trex.common.Consts.*;
import static trex.common.Consts.ConstraintOp.*;
import static trex.common.Consts.Op.*;
import static trex.common.Consts.OpTreeType.LEAF;
import static trex.common.Consts.STRING_VAL_LEN;
import static trex.common.Consts.StateType.*;
import static trex.common.Consts.ValType.*;
import static trex.common.Consts.ConstraintOp.INTERSECT;
import static trex.common.Consts.ConstraintOp.CONTAINS;


/**
 * Created by @author Behnam Khazael on 2/2/2020.
 * From Trex Project
 * This Class contains all the common functions used during event computation.
 */
public final class Functions {

    private final static Object lock = new Object();

    /**
     * Returns the id of the first element in the given column having a
     * value greater than minTimeStamp.
     * Returns -1 if such an element cannot be found.
     * The search is performed in logarithmic time, using a binary search.
     */
    synchronized public static int getFirstValidElement(CopyOnWriteArrayList<PubPkt> column, int columnSize, long minTimeStamp) {
        try {
            synchronized (lock) {
                if (column.size() <= 0 | columnSize <= 0)
                    return -1;
                int minValue = 0;
                int maxValue = columnSize - 1;
                if (column.get(maxValue).getTimeStamp() <= minTimeStamp)
                    return -1;
                while (maxValue - minValue > 1) {
                    int midPoint = minValue + (maxValue - minValue) / 2;
                    if (column.get(midPoint).getTimeStamp() <= minTimeStamp) {
                        minValue = midPoint;
                    } else {
                        maxValue = midPoint;
                    }
                }
                if (maxValue - minValue == 0)
                    return minValue;
                if (column.get(minValue).getTimeStamp() > minTimeStamp)
                    return minValue;
                return maxValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Returns the id of the last element in the given column having a
     * value smaller than maxTimeStamp and an index greater than minIndex.
     * Returns -1 if such an element cannot be found.
     * The search is performed in logarithmic time, using a binary search.
     */
    public static int getLastValidElement(CopyOnWriteArrayList<PubPkt> column, int columnSize, long maxTimeStamp, int minIndex) {
        synchronized (lock) {
            int minValue = minIndex;
            int maxValue = columnSize - 1;
            if (minIndex == -1)
                return -1;
            if (column.get(minIndex).getTimeStamp() >= maxTimeStamp)
                return -1;
            while (maxValue - minValue > 1) {
                int midPoint = minValue + (maxValue - minValue) / 2;
                if (column.get(midPoint).getTimeStamp() >= maxTimeStamp) {
                    maxValue = midPoint;
                } else {
                    minValue = midPoint;
                }
            }
            if (maxValue - minValue == 0)
                return minValue;
            if (column.get(maxValue).getTimeStamp() < maxTimeStamp)
                return maxValue;
            return minValue;
        }
    }

    /**
     * Returns the new size of the column.
     */
    synchronized public static int deleteInvalidElements(CopyOnWriteArrayList<PubPkt> column, int columnSize, long minTimeStamp) {
        synchronized (lock) {
            if (columnSize == 0)
                return columnSize;
            if (column.get(columnSize - 1).getTimeStamp() == minTimeStamp)
                return 1;
            int firstValidElement = getFirstValidElement(column, columnSize, minTimeStamp);
            if (firstValidElement < 0)
                return 0;
            else
                return columnSize - firstValidElement;
        }
    }

    public static boolean checkComplexParameter(PubPkt pkt, PartialEvent partialEvent, ComplexParameter parameter, int index,
                                                StateType sType) {
        ConstraintOp operation = parameter.getOperation();
        // TODO: what if types don't match?
        ValType type = parameter.getValueType();
        if (type == INT) {
            int left = computeIntValueForParameters(pkt, partialEvent, parameter.getLeftTree(), index, sType);
            int right = computeIntValueForParameters(pkt, partialEvent, parameter.getRightTree(), index, sType);
            if (operation.equals(EQ))
                return left == right;
            if (operation.equals(GT))
                return left > right;
            if (operation.equals(LT))
                return left < right;
            if (operation.equals(NE))
                return left != right;
            if (operation.equals(LE))
                return left <= right;
            if (operation.equals(GE))
                return left >= right;
        } else if (type == FLOAT) {
            float left = computeFloatValueForParameters(pkt, partialEvent, parameter.getLeftTree(), index, sType);
            float right = computeFloatValueForParameters(pkt, partialEvent, parameter.getRightTree(), index, sType);
            if (operation.equals(EQ))
                return left == right;
            if (operation.equals(GT))
                return left > right;
            if (operation.equals(LT))
                return left < right;
            if (operation.equals(NE))
                return left != right;
            if (operation.equals(LE))
                return left <= right;
            if (operation.equals(GE))
                return left >= right;
        } else if (type == BOOL) {
            boolean leftValue = computeBoolValueForParameters(pkt, partialEvent, parameter.getLeftTree(), index, sType);
            boolean rightValue = computeBoolValueForParameters(pkt, partialEvent, parameter.getRightTree(), index, sType);
            if (operation.equals(EQ))
                return leftValue && rightValue;
            if (operation.equals(NE))
                return leftValue || rightValue;
        } else if (type == STRING) {
            char strLeft[] = new char[STRING_VAL_LEN];
            char strRight[] = new char[STRING_VAL_LEN];
            computeStringValueForParameters(pkt, partialEvent, parameter.getLeftTree(), index, sType, strLeft);
            computeStringValueForParameters(pkt, partialEvent, parameter.getRightTree(), index, sType, strRight);
            if (operation.equals(EQ))
                return (strLeft.equals(strRight) == true);
            if (operation.equals(NE))
                return (strLeft.equals(strRight) == false);
        } else if (type == GEOMETRY) {
            //TODO update the way that you calculate the GEOMETRY operation
            Geometry geoLeft = new GeometryFactory().createGeometry(null);
            Geometry geoRight = new GeometryFactory().createGeometry(null);
            geoLeft = computeGeometryValueForParameters(pkt, partialEvent, parameter.getLeftTree(), index, sType);
            geoRight = computeGeometryValueForParameters(pkt, partialEvent, parameter.getRightTree(), index, sType);

            if (operation.equals(INTERSECT))
                return geoLeft.getEnvelope().intersects(geoRight.getEnvelope());

            if (operation.equals(CONTAINS))
                return geoLeft.getEnvelope().contains(geoRight.getEnvelope());


        }
        return false;
    }

    private static boolean computeBoolValueForParameters(PubPkt pkt, PartialEvent partialEvent, OpTree opTree, int index, StateType sType) {
        // TODO: 2/3/2020
        OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference = (RulePktValueReference) (reference);
            if (pktReference == null) {
                StaticValueReference sReference = (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    //return sReference.getIntVal();
                    return (sReference.getIntVal() == 0) ? false : true;
                else if (sReference.getType() == FLOAT)
                    //return sReference.getFloatValue();
                    return (sReference.getIntVal() == 0) ? false : true;
                else if (sReference.getType() == BOOL)
                    return sReference.getBoolVal();
            }
            int refIndex = pktReference.getIndex();

            PubPkt cPkt;
            // are we checking the current state packet?
            if ((refIndex == index && pktReference.isAggIndex() == false && pktReference.isNegIndex() == false && sType == STATE) ||
                    (pktReference.isNegIndex() && sType == NEG) ||
                    (pktReference.isAggIndex() && sType == AGG)) {
                cPkt = pkt;
            } else {
                cPkt = partialEvent.getIndexes(refIndex);
            }
            //TODO attrIndex is an int val but this method is sending it and try to change it on the method
            // attrIndex is not usable :$
            Attribute attrIndex;
            ValType vType;
            attrIndex = getAttributeIndexAndType(cPkt, pktReference.getName());

            if (attrIndex == null)
                return false;
            vType = attrIndex.getValType();

            //FIXME shouldn't it be INT??? on C++ code it is bool!!
            if (vType == BOOL)
                return (attrIndex.getIntVal() == 0) ? false : true;
            else if (vType == FLOAT)
                return (attrIndex.getFloatVal() == 0) ? false : true;
            else if (vType == BOOL)
                return attrIndex.getBoolVal();
        } else {
            // Integer can only be obtained from integer: assume this is ensured at rule
            // deployment time
            boolean leftValue = computeBoolValueForParameters(
                    pkt, partialEvent, opTree.getLeftTree(), index, sType);
            boolean rightValue = computeBoolValueForParameters(
                    pkt, partialEvent, opTree.getRightTree(), index, sType);

            Op op = opTree.getOp();
            if (op == AND)
                return leftValue && rightValue;
            if (op == OR)
                return leftValue || rightValue;
        }
        return false;
    }

    private static float computeFloatValueForParameters(PubPkt pkt, PartialEvent partialEvent, OpTree opTree, int index, StateType sType) {
        //TODO what the hell is this? casting a boolean to float!!!!
        OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference = (RulePktValueReference) (reference);
            if (pktReference == null) {
                StaticValueReference sReference = (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    return sReference.getIntVal();
                else if (sReference.getType() == FLOAT)
                    return sReference.getFloatVal();
                else if (sReference.getType() == BOOL)
                    return (sReference.getBoolVal() == true) ? 1 : 0;
            }
            int refIndex = pktReference.getIndex();
            PubPkt cPkt;
            // are we checking the current state packet?
            if ((refIndex == index && pktReference.isAggIndex() == false &&
                    pktReference.isNegIndex() == false && sType == STATE) ||
                    (pktReference.isNegIndex() && sType == NEG) ||
                    (pktReference.isAggIndex() && sType == AGG)) {
                cPkt = pkt;
            } else {
                cPkt = partialEvent.getIndexes(refIndex);
            }
            //TODO attrIndex is an int val but this method is sending it and try to change it on the method
            // attrIndex is not usable :$
            Attribute attrIndex;
            ValType vType;
            attrIndex = getAttributeIndexAndType(cPkt, pktReference.getName());
            if (attrIndex == null)
                return 0;
            vType = attrIndex.getValType();
            if (vType == INT)
                return attrIndex.getIntVal();
            else if (vType == FLOAT)
                return attrIndex.getFloatVal();
            else if (vType == BOOL)
                return (attrIndex.getBoolVal() == true) ? 1 : 0;
        } else {
            // Integer can only be obtained from integer: assume this is ensured at rule
            // deployment time
            float leftValue = computeFloatValueForParameters(
                    pkt, partialEvent, opTree.getLeftTree(), index, sType);
            float rightValue = computeFloatValueForParameters(
                    pkt, partialEvent, opTree.getRightTree(), index, sType);

            Op op = opTree.getOp();
            if (op == ADD)
                return leftValue + rightValue;
            if (op == SUB)
                return leftValue - rightValue;
            if (op == MUL)
                return leftValue * rightValue;
            if (op == DIV)
                return leftValue / rightValue;
        }
        return 0F;
    }

    private static int computeIntValueForParameters(PubPkt pkt, PartialEvent partialEvent, OpTree opTree, int index, StateType sType) {
        //TODO
        OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference = (RulePktValueReference) (reference);
            if (pktReference == null) {
                StaticValueReference sReference = (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    return sReference.getIntVal();
                else if (sReference.getType() == FLOAT)
                    return (int) sReference.getFloatVal();
                else if (sReference.getType() == BOOL)
                    return (sReference.getBoolVal() == true) ? 1 : 0;
            }
            int refIndex = pktReference.getIndex();
            PubPkt cPkt;
            // are we checking the current state packet?
            if ((refIndex == index && pktReference.isAggIndex() == false &&
                    pktReference.isNegIndex() == false && sType == STATE) ||
                    (pktReference.isNegIndex() && sType == NEG) ||
                    (pktReference.isAggIndex() && sType == AGG)) {
                cPkt = pkt;
            } else {
                cPkt = partialEvent.getIndexes(refIndex);
            }
            //TODO attrIndex is an int val but this method is sending it and try to change it on the method
            // attrIndex is not usable :$
            Attribute attrIndex;
            ValType vType;
            attrIndex = getAttributeIndexAndType(cPkt, pktReference.getName());
            if (attrIndex == null)
                return 0;
            vType = attrIndex.getValType();
            if (vType == INT)
                return attrIndex.getIntVal();
            else if (vType == FLOAT)
                return (int) attrIndex.getFloatVal();
            else if (vType == BOOL)
                return (attrIndex.getBoolVal() == true) ? 1 : 0;
        } else {
            // Integer can only be obtained from integer: assume this is ensured at rule
            // deployment time
            int leftValue = computeIntValueForParameters(
                    pkt, partialEvent, opTree.getLeftTree(), index, sType);
            int rightValue = computeIntValueForParameters(
                    pkt, partialEvent, opTree.getRightTree(), index, sType);
            Op op = opTree.getOp();
            if (op == ADD)
                return leftValue + rightValue;
            if (op == SUB)
                return leftValue - rightValue;
            if (op == MUL)
                return leftValue * rightValue;
            if (op == DIV)
                return leftValue / rightValue;
        }
        return 0;
    }

    private static void computeStringValueForParameters(PubPkt pkt, PartialEvent partialEvent, OpTree opTree, int index, StateType sType, char[] res) {
        //TODO
        OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference =
                    (RulePktValueReference) (reference);
            if (pktReference == null) {
                StaticValueReference sReference =
                        (StaticValueReference) (reference);
                if (sReference.getType() == STRING) {
                    sReference.setStringVal(res.toString());
                    return;
                }
            }
            int refIndex = pktReference.getIndex();

            PubPkt cPkt;
            // are we checking the current state packet?
            if ((refIndex == index && pktReference.isAggIndex() == false &&
                    pktReference.isNegIndex() == false && sType == STATE) ||
                    (pktReference.isNegIndex() && sType == NEG) ||
                    (pktReference.isAggIndex() && sType == AGG)) {
                cPkt = pkt;
            } else {
                cPkt = partialEvent.getIndexes(refIndex);
            }

            //TODO attrIndex is an int val but this method is sending it and try to change it on the method
            // attrIndex is not usable :$
            Attribute attrIndex;
            ValType vType;
            attrIndex = getAttributeIndexAndType(cPkt, pktReference.getName());
            if (attrIndex == null) {
                res = "".toCharArray();
                return;
            }
            vType = attrIndex.getValType();
            if (vType == STRING) {
                attrIndex.setStringVal(res.toString());
                return;
            }
        } else {
            // Binary tree operations are not supported for strings
            return;
        }
    }

    private static Geometry computeGeometryValueForParameters(PubPkt pkt, PartialEvent partialEvent, OpTree opTree, int index, StateType sType) {
        Geometry res = new GeometryFactory().createGeometry(null);
        try {
            OpTreeType type = opTree.getType();
            if (type == LEAF) {
                OpValueReference reference = opTree.getValueRef();
                RulePktValueReference pktReference =
                        (RulePktValueReference) (reference);
                if (pktReference == null) {
                    StaticValueReference sReference =
                            (StaticValueReference) (reference);
                    if (sReference.getType() == GEOMETRY) {
                        res = sReference.getGeometryVal();
                        return res;
                    }
                }
                int refIndex = pktReference.getIndex();
                PubPkt cPkt;
                if ((refIndex == index && pktReference.isAggIndex() == false &&
                        pktReference.isNegIndex() == false && sType == STATE) ||
                        (pktReference.isNegIndex() && sType == NEG) ||
                        (pktReference.isAggIndex() && sType == AGG)) {
                    cPkt = pkt;
                } else {
                    cPkt = partialEvent.getIndexes(refIndex);
                }
                Attribute attrIndex;
                ValType vType;
                attrIndex = getAttributeIndexAndType(cPkt, pktReference.getName());
                if (attrIndex == null) {
                    res = new GeometryFactory().createGeometry(null);
                    return res;
                }
                vType = attrIndex.getValType();
                if (vType == GEOMETRY) {
                    res = attrIndex.getGeometryVal();
                    return res;
                }
            } else {
                Geometry leftValue = computeGeometryValueForParameters(
                        pkt, partialEvent, opTree.getLeftTree(), index, sType);
                Geometry rightValue = computeGeometryValueForParameters(
                        pkt, partialEvent, opTree.getRightTree(), index, sType);
                Op op = opTree.getOp();
                if (op == Consts.Op.INTERSECT)
                    return leftValue.intersection(rightValue);
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Behnam TODO check the name of an attribute if it match then provide true else false
     */
    static Attribute getAttributeIndexAndType(PubPkt pubPkt, String name) {
        for (Attribute attribute : pubPkt.getAttributes()
                ) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }

}
