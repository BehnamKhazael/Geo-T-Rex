package trexengine;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import trex.common.*;
import trex.common.Consts.Op;
import trex.packets.PubPkt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static trex.common.Consts.AggregateFun.*;
import static trex.common.Consts.Op.*;
import static trex.common.Consts.OpTreeType.LEAF;
import static trex.common.Consts.StateType.AGG;
import static trex.common.Consts.StateType.NEG;
import static trex.common.Consts.ValType.*;
import static trexengine.Functions.*;

/**
 * Created by sony on 2/6/2020.
 * <p>
 * A composite event generator is used to generate a new composite event
 * starting from its template and from an automaton (set of sequences)
 * satisfying all constraints in the detecting rule.
 */
public class CompositeEventGenerator {

    public class AttAndCompute {
        public Integer attrIndexcomputeStringValue;
        public Consts.ValType typecomputeStringValue;

        public String resultcomputeStringValue;
        public Geometry resultcomputeGeometryValue;

        public Integer attrIndexcomputeIntValue;
        public Consts.ValType typecomputeIntValue;


        public Integer attrIndexcomputeBoolValue;
        public Consts.ValType typecomputeBoolValue;

        public Integer attrIndexcomputeFloatValue;
        public Consts.ValType typecomputeFloatValue;

        public Integer attrIndexcomputeGeometryValue;
        public Consts.ValType typecomputeGeometryValue;

        public String StringResultgenerateCompositeEvent;

    }


    /**
     * Constructor: defines the template to be used for composite event generation
     */
    CompositeEventGenerator(EventTemplate parCeTemplate) {
        ceTemplate = new EventTemplate(parCeTemplate.getEventType());
        for (EventTemplateAttr attribute : parCeTemplate.getAttributes()
                ) {
            ceTemplate.addAttribute(attribute);
        }
        for (EventTemplateStaticAttr staticAttributes : parCeTemplate.getStaticAttributes()
                ) {
            ceTemplate.addStaticAttribute(staticAttributes);
        }

    }

    public CompositeEventGenerator() {
    }

    /**
     * Creates a new composite event starting from the stored template and
     * from the set of events given as input parameter.
     */
    public PubPkt generateCompositeEvent(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters) {
        Integer eventType = ceTemplate.getEventType();
        Integer attributesNum = ceTemplate.getAttributes().size();
        Integer staticAttributesNum = ceTemplate.getStaticAttributes().size();
        Attribute attributes[] = new Attribute[attributesNum + staticAttributesNum];
        for (int i = 0; i < attributesNum + staticAttributesNum; i++)
            attributes[i] = new Attribute();
        for (int i = 0; i < attributesNum; i++) {
            attributes[i].setName(ceTemplate.getAttributeName(i));
            Consts.ValType valType = ceTemplate.getAttributeTree(i).getValType();
            attributes[i].setValType(valType);
            if (valType == INT)
                attributes[i].setIntVal(computeIntValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, ceTemplate.getAttributeTree(i)));
            else if (valType == FLOAT)
                attributes[i].setFloatVal(computeFloatValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, ceTemplate.getAttributeTree(i)));
            else if (valType == BOOL)
                attributes[i].setBoolVal(computeBoolValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, ceTemplate.getAttributeTree(i)));
            else if (valType == STRING) {
                attributes[i].setStringVal(computeStringValue(partialEvent, aggregates, aggsSize, receivedPkts,
                        receivedAggs, aggregateParameters,
                        ceTemplate.getAttributeTree(i),
                        attributes[i].getStringVal()));
            }
            //TODO rectify the null pointer exception in Event Template!
            else if (valType == GEOMETRY) {
                attributes[i].setGeometryVal(computeGeometryValue(partialEvent, aggregates, aggsSize, receivedPkts,
                        receivedAggs, aggregateParameters,
                        ceTemplate.getAttributeTree(i),
                        attributes[i].getGeometryVal()));
            }
        }
        for (int i = 0; i < staticAttributesNum; i++) {
            ceTemplate.getStaticAttribute(attributes[i + attributesNum], i);
        }
        int size = attributes.length;
        List<Attribute> list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(attributes[i]);
        }

        PubPkt result = new PubPkt(eventType, attributes, attributesNum + staticAttributesNum);

        result.setTimeStamp(partialEvent.getIndexes(0).getTimeStamp());
        return result;
    }

    int loccount;
    ArrayList<PubPkt> arr;

    void quickSort(int l, int r) {
    }

    ;

    int partition(int l, int r) {
        return 1;
    }

    ;

    int selectKth(int k, float min, float max) {
        return 1;
    }

    ;

    // Template for the composite event TODO CompositeEventTemplate => EventTemplate???
    EventTemplate ceTemplate = new EventTemplate();

    /**
     * Computes the value of an attribute using the given sequences
     * Requires the type to be INT
     */
    int computeIntValue(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters,
            OpTree opTree) {
        Consts.OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference =
                    (RulePktValueReference) (reference);
            if (pktReference == null) {
                StaticValueReference sReference =
                        (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    return sReference.getIntVal();
                else if (sReference.getType() == FLOAT)
                    return (int) sReference.getFloatVal();
                else if (sReference.getType() == BOOL)
                    return (sReference.getBoolVal() == true) ? 1 : 0;
            }
            Integer index = pktReference.getIndex();
            Boolean refersToAgg = pktReference.isAggIndex();
            if (!refersToAgg) {
                PubPkt pkt = partialEvent.getIndexes(index);
                AttAndCompute attAndCompute = new AttAndCompute();
//                Integer attrIndex = new Integer(0); //attrIndexcomputeIntValue
//                Consts.ValType valtype = INT; //typecomputeIntValue
                if (pkt.getAttributeIndexAndTypeInt(pktReference.getName(), attAndCompute.attrIndexcomputeIntValue, attAndCompute.typecomputeIntValue, attAndCompute) == false)
                    return 0;
                if (/*typecomputeIntValue*/ attAndCompute.typecomputeIntValue == INT)
                    return pkt.getIntAttributeVal(/*attrIndexcomputeIntValue*/ attAndCompute.attrIndexcomputeIntValue);
                else if (attAndCompute.typecomputeFloatValue == FLOAT)
                    return (int) pkt.getFloatAttributeVal(/*attrIndexcomputeIntValue*/ attAndCompute.attrIndexcomputeIntValue);
            } else {
                return (int) computeAggregate(index, partialEvent, aggregates, aggsSize,
                        receivedPkts, receivedAggs, aggregateParameters);
            }
        } else {
            // Integer can only be obtained from integer:
            // assume this is ensured at rule deployment time
            int leftValue = computeIntValue(
                    partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                    aggregateParameters, opTree.getLeftTree());
            int rightValue = computeIntValue(
                    partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                    aggregateParameters, opTree.getRightTree());
            Op op = opTree.getOp();
            System.out.println("CPU not leaf " + leftValue + "; " + rightValue + ". OP " + op);
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

    ;

    /**
     * Computes the value of an attribute using the given sequences
     * Requires the type to be FLOAT
     */
    float computeFloatValue(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters,
            OpTree opTree) {
        Consts.OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference =
                    (RulePktValueReference) (reference);
            if (pktReference == null) {
                // this is a static value
                StaticValueReference sReference =
                        (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    return sReference.getIntVal();
                else if (sReference.getType() == FLOAT)
                    return sReference.getFloatVal();
                else if (sReference.getType() == BOOL)
                    return (sReference.getBoolVal() == true) ? 1 : 0;
            }
            Integer index = pktReference.getIndex();
            Boolean refersToAgg = pktReference.isAggIndex();
            if (!refersToAgg) {
                PubPkt pkt = partialEvent.getIndexes(index);
//                Integer attrIndex = 0;
//                Consts.ValType ValType = FLOAT;
                AttAndCompute attAndCompute = new AttAndCompute();
                if (pkt.getAttributeIndexAndTypeFloat(pktReference.getName(), attAndCompute.attrIndexcomputeFloatValue,
                        attAndCompute.typecomputeFloatValue, attAndCompute) == false)
                    return 0;
                if (attAndCompute.typecomputeFloatValue == INT)
                    return pkt.getIntAttributeVal(attAndCompute.attrIndexcomputeFloatValue);
                else if (attAndCompute.typecomputeFloatValue == FLOAT)
                    return pkt.getFloatAttributeVal(attAndCompute.attrIndexcomputeFloatValue);
            } else {
                return computeAggregate(index, partialEvent, aggregates, aggsSize,
                        receivedPkts, receivedAggs, aggregateParameters);
            }
        } else {
            // Floats can only be obtained from integer and float:
            // assume this is ensured at rule deployment time
            float leftValue;
            if (opTree.getLeftTree().getValType() == INT)
                leftValue = computeIntValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getLeftTree());
            else
                leftValue = computeFloatValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getLeftTree());
            float rightValue;
            if (opTree.getRightTree().getValType() == INT)
                rightValue = computeIntValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getRightTree());
            else
                rightValue = computeFloatValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getRightTree());
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

    /**
     * Computes the value of an attribute using the given sequences
     * Requires the type to be BOOL
     */
    boolean computeBoolValue(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters,
            OpTree opTree) {
        Consts.OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference =
                    (RulePktValueReference) (reference);
            if (pktReference == null) {
                // this is a static value
                StaticValueReference sReference =
                        (StaticValueReference) (reference);
                if (sReference.getType() == INT)
                    return (sReference.getIntVal() == 0) ? false : true;
                else if (sReference.getType() == FLOAT)
                    return (sReference.getFloatVal() == 0) ? false : true;
                else if (sReference.getType() == BOOL)
                    return sReference.getBoolVal();
            }
            Integer index = pktReference.getIndex();
            Boolean refersToAgg = pktReference.isAggIndex();
            if (!refersToAgg) {
                PubPkt pkt = partialEvent.getIndexes(index);
//                Integer attrIndex = 0;
//                Consts.ValType Valtype = BOOL;
                AttAndCompute attAndCompute = new AttAndCompute();
                if (pkt.getAttributeIndexAndBool(pktReference.getName(), attAndCompute.attrIndexcomputeBoolValue,
                        attAndCompute.typecomputeBoolValue, attAndCompute) == false)
                    return false;
                return pkt.getBoolAttributeVal(attAndCompute.attrIndexcomputeBoolValue);
            } else {
                // Aggregates not defines for type bool, up to now
                return false;
            }
        } else {
            // Booleans can only be obtained from booleans:
            // assume this is ensured at rule deployment time
            boolean leftValue = computeBoolValue(
                    partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                    aggregateParameters, opTree.getLeftTree());
            boolean rightValue = computeBoolValue(
                    partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                    aggregateParameters, opTree.getRightTree());
            Op op = opTree.getOp();
            if (op == AND)
                return leftValue && rightValue;
            if (op == OR)
                return leftValue || rightValue;
        }
        return false;
    }


    /**
     * Computes the value of an attribute using the given sequences
     * Requires the type to be STRING
     */
    String computeStringValue(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters,
            OpTree opTree, String result) {
        // No operator is defined for strings: type can only be LEAF
        OpValueReference reference = opTree.getValueRef();
        RulePktValueReference pktReference =
                (RulePktValueReference) (reference);
        if (pktReference == null) {
            // this is a static value
            StaticValueReference sReference =
                    (StaticValueReference) (reference);
            result = sReference.getStringVal();
            return result;
        }
        int index = pktReference.getIndex();
        boolean refersToAgg = pktReference.isAggIndex();
        if (!refersToAgg) {
            PubPkt pkt = partialEvent.getIndexes(index);
//            Integer attrIndex = new Integer(0); //attrIndexcomputeStringValue
//            Consts.ValType type = STRING; //typecomputeStringValue
//            result =  ""; //resultcomputeStringValue
            AttAndCompute attAndCompute = new AttAndCompute();
            if (pkt.getAttributeIndexAndTypeString(pktReference.getName(), attAndCompute.attrIndexcomputeStringValue, attAndCompute.typecomputeStringValue, attAndCompute) == false)
                return "";
            return pkt.getStringAttributeVal(attAndCompute.attrIndexcomputeStringValue, attAndCompute.resultcomputeStringValue, attAndCompute);
            //StringResultgenerateCompositeEvent = /*resultcomputeStringValue*/ result;
            //return result;
        } else {
            // Aggregates not defines for type string, up to now
        }
        return "";
    }


    /**
     * Computes the value of an attribute using the given sequences
     * Requires the type to be GEOMETRY
     */
    Geometry computeGeometryValue(
            PartialEvent partialEvent, Map<Integer, TAggregate> aggregates,
            Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters,
            OpTree opTree, Geometry result) {
        Consts.OpTreeType type = opTree.getType();
        if (type == LEAF) {
            OpValueReference reference = opTree.getValueRef();
            RulePktValueReference pktReference =
                    (RulePktValueReference) (reference);
            if (pktReference == null) {
                // this is a static value
                StaticValueReference sReference =
                        (StaticValueReference) (reference);
                if (sReference.getType() != GEOMETRY)
                    return new GeometryFactory().createGeometry(null);
                result = sReference.getGeometryVal();
                return result;
            }
            Integer index = pktReference.getIndex();
            Boolean refersToAgg = pktReference.isAggIndex();
            if (!refersToAgg) {
                PubPkt pkt = partialEvent.getIndexes(index);
//                Integer attrIndex = 0;
//                Consts.ValType ValType = FLOAT;
                AttAndCompute attAndCompute = new AttAndCompute();
                if (pkt.getAttributeIndexAndTypeGeometry(pktReference.getName(), attAndCompute.attrIndexcomputeGeometryValue,
                        attAndCompute.typecomputeGeometryValue, attAndCompute) == false)
                    return new GeometryFactory().createGeometry(null);
                if (attAndCompute.typecomputeGeometryValue != GEOMETRY)
                    return new GeometryFactory().createGeometry(null);
                else
                    return computeAggregateForGeometry(index, partialEvent, aggregates, aggsSize,
                            receivedPkts, receivedAggs, aggregateParameters);

            }
        }
            else {
                Geometry leftValue = new GeometryFactory().createGeometry(null);
                leftValue = computeGeometryValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getLeftTree(),leftValue);
                Geometry rightValue = new GeometryFactory().createGeometry(null);;
                rightValue = computeGeometryValue(
                        partialEvent, aggregates, aggsSize, receivedPkts, receivedAggs,
                        aggregateParameters, opTree.getRightTree(), rightValue);
                Op op = opTree.getOp();
                if (op == INTERSECT) {
                    return leftValue.intersection(rightValue);
                }
                if (op == CONTAINS)
                    if (leftValue.contains(rightValue))
                    return leftValue.intersection(result);
                else return new GeometryFactory().createGeometry(null);
                if (op == DISTANCE)
                    return new GeometryFactory().createGeometry(null);;
                if (op == DIV)
                    return new GeometryFactory().createGeometry(null);;
            }
        return new GeometryFactory().createGeometry(null);
    }

    /**
     * Returns the value of the aggregate with the given index.
     * Requires the index of one of the aggregates and a list packets.
     * Returns always 0 in case no events have been stored
     * for computing the aggregate.
     */

    private Geometry computeAggregateForGeometry(
            Integer index,
            PartialEvent partialEvent,
            Map<Integer, TAggregate> aggregates,
            Integer[] aggsSize, Map<Integer,
            CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters) {
        if (aggregates.isEmpty()){
            return partialEvent.getIndexes(index).getGeometryAttributeVal(index, new GeometryFactory().createGeometry(null));
        }
        return new GeometryFactory().createGeometry(null);
    }

    /**
     * Returns the value of the aggregate with the given index.
     * Requires the index of one of the aggregates and a list packets.
     * Returns always 0 in case no events have been stored
     * for computing the aggregate.
     */
    float computeAggregate(
            int index, PartialEvent partialEvent,
            Map<Integer, TAggregate> aggregates, Integer aggsSize[],
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts,
            Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedAggs,
            Map<Integer, Set<ComplexParameter>> aggregateParameters) {
        TAggregate agg = aggregates.get(index);
        Long maxTS = partialEvent.getIndexes(agg.getUpperId()).getTimeStamp();
        Long minTS = 0L;
        if (agg.getLowerId() < 0) {
            minTS = maxTS - agg.getLowerTime();
        } else {
            minTS = partialEvent.getIndexes(agg.getLowerId()).getTimeStamp();
        }
        int index1 =
                getFirstValidElement(receivedAggs.get(index), aggsSize[index], minTS);
        if (index1 < 0) {
            if (agg.getFun() == COUNT) return 0;
            else return Float.NaN;
        }
        int index2 =
                getLastValidElement(receivedAggs.get(index), aggsSize[index], maxTS, index1);
        if (index2 < 0) {
            if (agg.getFun() == COUNT) return 0;
            else return Float.NaN;
        }
        Consts.AggregateFun fun = agg.getFun();
        String name = agg.getName();
        float sum = 0;
        int count = 0;
        float min = 0;
        float max = 0;
        int attIndex;
        Consts.ValType type;
        boolean checkParams = false;
        boolean firstValue = true;
        for (Map.Entry<Integer, Set<ComplexParameter>> paramIt : aggregateParameters.entrySet()
                ) {
            if (paramIt != null)
                checkParams = true;
            for (int i = index1; i <= index2; i++) {
                PubPkt pkt = receivedAggs.get(index).get(i);
                if (checkParams) {
                    if (!checkParameters(pkt, partialEvent, paramIt.getValue()))
                        continue;
                    // if (! checkComplexParameter(pkt, partialEvent,
                    // paramIt->second, -1, false)) continue;
                }
                float val = 0;
                type = ((Attribute) pkt.getAttributes().toArray()[index]).getValType();
                AttAndCompute attAndCompute = new AttAndCompute();
                if (pkt.getAttributes().contains(name) && type == INT) {
                    val = pkt.getIntAttributeVal(attAndCompute.attrIndexcomputeIntValue);
                } else if (pkt.getAttributeIndexAndType(name, index, type, attAndCompute) &&
                        type == FLOAT) {
                    val = pkt.getFloatAttributeVal(attAndCompute.attrIndexcomputeFloatValue);
                }
                count++;
                sum += val;
                // First value
                if (firstValue) {
                    min = val;
                    max = val;
                    firstValue = false;
                    continue;
                }
                // Following values
                if (val < min)
                    min = val;
                else if (val > max)
                    max = val;
            }
        }
        if (fun == SUM)
            return sum;
        if (fun == MAX)
            return max;
        if (fun == MIN)
            return min;
        if (fun == COUNT)
            return count;
        if (fun == AVG)
            return sum / count;
        return 0;
    }

    ;

    /**
     * Returns true if the packet satisfies all parameters, and false otherwise
     */
    boolean checkParameters(PubPkt pkt, PartialEvent partialEvent,
                            Set<ComplexParameter> parameters) {
        for (ComplexParameter it : parameters) {
            // cout << "Agg par" << endl;
            if (!checkComplexParameter(pkt, partialEvent, it, -1, AGG))
                return false;
        }
        return true;
    }
}
