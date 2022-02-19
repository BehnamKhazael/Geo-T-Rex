package trexengine;

import trex.common.Attribute;
import trex.common.Constraint;
import trex.common.Consts;
import trex.packets.PubPkt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static trex.common.Consts.ConstraintOp.*;
import static trex.common.Consts.ValType.INT;

/**
 * Created by sony on 2/5/2020.
 *
 * Represents the index for constraints of integer values.
 *
 */
public class IntConstraintIndex extends AbstractConstraintIndex {

    /**
     * Represents an integer constraint stored in the index table
     */
    class IntTableConstraint{

        /**
         * Attribute name
         */
        //char name[] = new char[NAME_LEN];
        String name;
        /**
         * Operator
         */
        Consts.ConstraintOp op;

        /**
         * Attribute value
         */
        int val;
        /**
         * Set of predicates using the constraint
         */
        Set<TablePred> connectedPredicates = new HashSet<>();
    }

    /**
     * Contains a Value -> Constraint index for each defined operator
     */
    class IntOperatorsTable{
        /**
         * Value -> equality constraint
         */
        Map<Integer, IntTableConstraint> eq = new HashMap<>();

        /**
         *  Value -> less than constraint
         */
        Map<Integer, IntTableConstraint> lt = new HashMap<>();

        /**
         *  Value -> greater then constraint
         */
        Map<Integer, IntTableConstraint> gt = new HashMap<>();

        /**
         *  Value -> different from constraint
         */
        Map<Integer, IntTableConstraint> ne = new HashMap<>();

        /**
         *  Value -> less than or equal to constraint
         */
        Map<Integer, IntTableConstraint> le = new HashMap<>();

        /**
         *  Value -> greater than or equal to constraint
         */
        Map<Integer, IntTableConstraint> ge = new HashMap<>();

        // TODO Overriding
        //bool operator<(const IntOperatorsTable& table) const { return eq < table.eq; }

    }

    /**
     * Creates or gets the IntTableConstraint C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    public void installConstraint(Constraint constraints, TablePred predicate){
        /**
         *  Looks if the same constraint is already installed in the table
         */
        IntTableConstraint itc = getConstraint(constraints);
        if (itc == null) {
            /**
             *  If the constraint is not found, it creates a new one ...
             */
            itc = createConstraint(constraints);
            /**
             *  ... and installs it
             */
            installConstraint(itc);
        }
        /**
         *  In both cases connects the predicate with the constraint
         */
        itc.connectedPredicates.add(predicate);

    }

    /**
     * Processes the given message, using the partial results stored in predCount.
     * It updates predCount and fills mh with the matching states.
     */
    void processMessage(PubPkt pkt, MatchingHandler mh,
                        Map<TablePred, Integer> predCount){
        for (Attribute i : pkt.getAttributes()
                ) {
            if (i.getValType() != INT)
                continue;
            String name = i.getName();
            Integer val = i.getIntVal();
            if (indexes.get(name) == null)
                continue;

            /**
             * Equality constraints
             */
            if (indexes.get(name).eq.get(val) != null) {
                IntTableConstraint itc = indexes.get(name).eq.get(val);
                processConstraint(itc, mh, predCount);
            }

            // Less than constraints (iterating in descending order)
            for (Map.Entry<Integer, IntTableConstraint> rit : indexes.get(name).lt.entrySet()) {
                if (rit.getKey() < val)
                    break;
                IntTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            // Less than or equal to constraints (iterating in descending order)
            for (Map.Entry<Integer, IntTableConstraint> rit : indexes.get(name).le.entrySet()) {
                if (rit.getKey() <= val)
                    break;
                IntTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            // Greater than constraints (iterating in ascending order)
            for (Map.Entry<Integer, IntTableConstraint> rit : indexes.get(name).gt.entrySet()) {
                if (rit.getKey() > val)
                    break;
                IntTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }

            // Greater than or equal to constraints (iterating in ascending order)
            for (Map.Entry<Integer, IntTableConstraint> rit : indexes.get(name).ge.entrySet()) {
                if (rit.getKey() >= val)
                    break;
                IntTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }

            // Different from constraints (iterating in ascending order)
            for (Map.Entry<Integer, IntTableConstraint> rit : indexes.get(name).ne.entrySet()) {
                if (rit.getKey() == val)
                    continue;
                IntTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);
            }
        }

    };
    /**
     *  Name -> indexes for that name
     */
    Map<String, IntOperatorsTable> indexes = new HashMap<>();

    /**
     *  Set of all constraints used in the table
     */
    Set<IntTableConstraint> usedConstraints = new HashSet<>();

    /**
     * Checks if there already exists an IntTableConstraints which is
     * compatible with the constraint c.
     * If it finds a valid IntTableConstraints, return a pointer to it,
     * otherwise returns null.
     */
    IntTableConstraint getConstraint(Constraint c){
        for (IntTableConstraint it : usedConstraints
                ) {
            IntTableConstraint itc = it;
            if (itc.op != c.getOp())
                continue;
            if (itc.val != c.getIntVal())
                continue;
            if (itc.name.equals(c.getName()))
                continue;
            return (itc);
        }
        return null;
    }

    /**
     * Creates a new IntTableConstraint using the information stored in the
     * parameter constraint
     */
    IntTableConstraint createConstraint(Constraint c){
        IntTableConstraint itc = new IntTableConstraint();
        //itc.name = c.getName().toCharArray();
        itc.name = c.getName();
        itc.op = c.getOp();
        itc.val = c.getIntVal();
        return itc;
    };

    /**
     * Installs the given constraint to the appropriate table
     */
    void installConstraint(IntTableConstraint c){
        usedConstraints.add(c);
        //String s = Arrays.toString(c.name);
        String s = c.name;
        if (indexes.get(s) == null) {
            IntOperatorsTable emptyTable = new IntOperatorsTable();
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
    };

    /**
     * Processes the given constraint by updating the predCount
     * and, if needed, the mh structures
     */
    void processConstraint(IntTableConstraint c, MatchingHandler mh,
                                  Map<TablePred, Integer> predCount){
        for (TablePred it : c.connectedPredicates
                ) {
            /**
             *  If satisfied for the first time, sets count to 1
             */
            if (predCount.get(it) == null)
                predCount.put(it, 1);
            else
                predCount.put(it, predCount.get(it) + 1);
            /**
             *  Otherwise increases count by one
             */
            if (predCount.get(it) == it.getConstraintsNum())
                addToMatchingHandler(mh, it);

        }
    }
}

