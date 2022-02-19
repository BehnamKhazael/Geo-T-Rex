package trexengine;

import trex.common.Attribute;
import trex.common.Constraint;
import trex.common.Consts;
import trex.packets.PubPkt;

import java.util.Map;
import java.util.Set;

import static trex.common.Consts.ConstraintOp.*;
import static trex.common.Consts.NAME_LEN;
import static trex.common.Consts.ValType.FLOAT;

/**
 * Created by sony on 2/4/2020.
 * Represents the index for constraints of float values.
 */
public class FloatConstraintIndex extends AbstractConstraintIndex {

    /**
     * Represents a float constraint stored in the index table
     */
    public class FloatTableConstraint {
        /**
         * Attribute name
         */
        char name[] = new char[NAME_LEN];
        /**
         * Operator
         */
        Consts.ConstraintOp op;
        /**
         * Attribute value
         */
        float val;
        /**
         * Set of predicates using the constraint
         */
        Set<TablePred> connectedPredicates;
    }


    /**
     * Contains a Value -> Constraint index for each defined operator
     */
    public class FloatOperatorsTable {
        /**
         * Value -> equality constraint
         */
        Map<Float, FloatTableConstraint> eq;

        /**
         * Value -> less than constraint
         */
        Map<Float, FloatTableConstraint> lt;

        /**
         * Value -> greater then constraint
         */
        Map<Float, FloatTableConstraint> gt;

        /**
         * Value -> different from constraint
         */
        Map<Float, FloatTableConstraint> ne;

        /**
         * Value -> less than or equal to constraint
         */
        Map<Float, FloatTableConstraint> le;

        /**
         * Value -> greater than or equal to constraint
         */
        Map<Float, FloatTableConstraint> ge;

        // TODO !! Overriding
        //bool operator<(const FloatOperatorsTable& table) const {
        //    return eq < table.eq;
        //}
    }

    FloatOperatorsTable FloatOps;

    /**
     * Creates or gets the FloatTableConstraint C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    public void installConstraint(Constraint constraints, TablePred predicate) {
        // Looks if the same constraint is already installed in the table
        FloatTableConstraint itc = getConstraint(constraints);
        if (itc == null) {
            // If the constraint is not found, it creates a new one ...
            itc = createConstraint(constraints);
            // ... and installs it
            installConstraint(itc);
        }
        // In both cases connects the predicate with the constraint
        itc.connectedPredicates.add(predicate);
    }

    /**
     * Processes the given message, using the partial results stored in predCount.
     * It updates predCount and fills mh with the matching states.
     */
    public void processMessage(PubPkt pkt, MatchingHandler mh,
                               Map<TablePred, Integer> predCount) {
        for (Attribute i : pkt.getAttributes()
                ) {
            if (i.getValType() != FLOAT)
                continue;
            String name = i.getName();
            Float val = i.getFloatVal();
            if (indexes.get(name) == null)
                continue;

            /**
             * Equality constraints
             */
            if (indexes.get(name).eq.get(val) != null) {
                FloatTableConstraint itc = indexes.get(name).eq.get(val);
                processConstraint(itc, mh, predCount);
            }

            // Less than constraints (iterating in descending order)
            for (Map.Entry<Float, FloatTableConstraint> rit : indexes.get(name).lt.entrySet()) {
                if (rit.getKey() < val)
                    break;
                FloatTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            // Less than or equal to constraints (iterating in descending order)
            for (Map.Entry<Float, FloatTableConstraint> rit : indexes.get(name).le.entrySet()) {
                if (rit.getKey() <= val)
                    break;
                FloatTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            // Greater than constraints (iterating in ascending order)
            for (Map.Entry<Float, FloatTableConstraint> rit : indexes.get(name).gt.entrySet()) {
                if (rit.getKey() > val)
                    break;
                FloatTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }

            // Greater than or equal to constraints (iterating in ascending order)
            for (Map.Entry<Float, FloatTableConstraint> rit : indexes.get(name).ge.entrySet()) {
                if (rit.getKey() >= val)
                    break;
                FloatTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }

            // Different from constraints (iterating in ascending order)
            for (Map.Entry<Float, FloatTableConstraint> rit : indexes.get(name).ne.entrySet()) {
                if (rit.getKey() == val)
                    continue;
                FloatTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }
        }

    }

    // Name -> indexes for that name
    Map<String, FloatOperatorsTable> indexes;
    // Set of all constraints used in the table
    Set<FloatTableConstraint> usedConstraints;

    /**
     * Checks if there already exists an FloatTableConstraints which is
     * compatible with the constraint c.
     * If it finds a valid FloatTableConstraints, return a pointer to it,
     * otherwise returns null.
     */
    FloatTableConstraint getConstraint(Constraint c) {
        for (FloatTableConstraint it : usedConstraints
                ) {
            FloatTableConstraint itc = it;
            if (itc.op != c.getOp())
                continue;
            if (itc.val != c.getFloatVal())
                continue;
            if (itc.name.equals(c.getName()))
                continue;
            return (itc);
        }
        return null;
    }

    /**
     * Creates a new FloatTableConstraint using the information stored in the
     * parameter constraint
     */
    FloatTableConstraint createConstraint(Constraint c) {
        FloatTableConstraint itc = new FloatTableConstraint();
        itc.name = c.getName().toCharArray();
        itc.op = c.getOp();
        itc.val = c.getFloatVal();
        return itc;
    }

    /**
     * Installs the given constraint to the appropriate table
     */
    void installConstraint(FloatTableConstraint c) {
        usedConstraints.add(c);
        String s = c.name.toString();
        if (indexes.get(s) == null) {
            FloatOperatorsTable emptyTable = null;
            indexes.put(s, emptyTable);
        }
        if (c.op == EQ)
            indexes.get(s).eq.put(c.val, c);
        else if (c.op == LT)
            indexes.get(s).lt.put(c.val, c);
        else if (c.op == GT)
            indexes.get(s).gt.put(c.val, c);
        else if (c.op == LE)
            indexes.get(s).le.put(c.val, c);
        else if (c.op == GE)
            indexes.get(s).ge.put(c.val, c);
        else
            indexes.get(s).ne.put(c.val, c);
    }


    /**
     * Processes the given constraint by updating the predCount
     * and, if needed, the mh structures
     */
    void processConstraint(FloatTableConstraint c, MatchingHandler mh,
                           Map<TablePred, Integer> predCount) {
        for (TablePred it : c.connectedPredicates
                ) {
            // If satisfied for the first time, sets count to 1
            if (predCount.get(it) == null)
                predCount.put(it, 1);
            else
                predCount.put(it, predCount.get(it) + 1);
            // Otherwise increases count by one
            if (predCount.get(it) == it.getConstraintsNum())
                addToMatchingHandler(mh, it);

        }
    }

}
