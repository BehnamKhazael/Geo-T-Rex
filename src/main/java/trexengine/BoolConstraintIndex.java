package trexengine;

import trex.common.Attribute;
import trex.common.Constraint;
import trex.common.Consts;
import trex.packets.PubPkt;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.NAME_LEN;
import static trex.common.Consts.ValType.BOOL;

/**
 * Created by sony on 2/3/2020.
 * <p>
 * Represents the index for constraints of boolean values.
 */
public class BoolConstraintIndex extends AbstractConstraintIndex {

    /**
     * Represents a boolean constraint stored in the index table
     */
    class BoolTableConstraintStruct {
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
        boolean val;

        /**
         * Set of predicates using the constraint
         */
        Set<TablePred> connectedPredicates;
    }

    BoolTableConstraintStruct BoolTableConstraint;

    /**
     * Contains a Value -> Constraint index for each defined operator
     */
    class BoolOperatorsTable {
        /**
         * Value -> equality constraint
         */
        TreeMap<Boolean, BoolTableConstraintStruct> eq;
        // Value -> different from constraint Overriding
        TreeMap<Boolean, BoolTableConstraintStruct> df;

        //TODO how to override a operator in Java, I need to create a Method and use it like isLessThan()

        /**
         * From c++:
         * bool operator<(const BoolOperatorsTable& table) const {
         * return eq < table.eq;
         * }
         *
         * @param table
         * @return
         */
        boolean operator(BoolOperatorsTable table) {
            return eq.equals(table.eq);
        }
    }

    BoolOperatorsTable BoolOps;


    /**
     * Creates or gets the BoolTableConstraintStruct C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    public void installConstraint(Constraint constraints, TablePred predicate) {
        // Looks if the same constraint is already installed in the table
        BoolTableConstraintStruct itc = getConstraint(constraints);
        if (itc == null) {
            // If the constraint is not found, it creates a new one ...
            itc = createConstraint(constraints);
            // ... and installs it
            installConstraint(itc);
        }
        // In both cases connects the predicate with the constraint
        itc.connectedPredicates.add(predicate);
    }

    ;

    /**
     * Processes the given message, using the partial results stored in predCount.
     * It updates predCount and fills mh with the matching states.
     */
    public void processMessage(PubPkt pkt, MatchingHandler mh,
                               Map<TablePred, Integer> predCount) {
        for (Attribute i : pkt.getAttributes()
                ) {
            if (i.getValType() != BOOL)
                continue;
            String name = i.getName();
            Boolean val = i.getBoolVal();
            if (!indexes.keySet().contains(name)) {
                continue;
            }
            /**
             * Equality constraints
             */
            if (indexes.get(name).eq.get(val) != null) {
                BoolTableConstraintStruct itc = indexes.get(name).eq.get(val);
                processConstraint(itc, mh, predCount);
            }

            /**
             * Different from constraints
             */
            if (indexes.get(name).df.get(!val) != null) {
                BoolTableConstraintStruct itc = indexes.get(name).eq.get(val);
                processConstraint(itc, mh, predCount);
            }
        }
    }

    /**
     * Name -> indexes for that name
     */
    private Map<String, BoolOperatorsTable> indexes;


    /**
     * Set of all constraints used in the table
     */
    private Set<BoolTableConstraintStruct> usedConstraints;

    /**
     * Checks if there already exists an BoolTableConstraints which is
     * compatible with the constraint c.
     * If it finds a valid BoolTableConstraints, return a pointer to it,
     * otherwise returns null.
     */
    private BoolTableConstraintStruct getConstraint(Constraint c) {
        for (BoolTableConstraintStruct itc : usedConstraints
                ) {
            if (!itc.op.equals(c.getOp())) {
                continue;
            }
            if (itc.val != c.getBoolVal()) {
                continue;
            }
            if (!itc.name.equals(c.getName())) {
                continue;
            }
            return itc;
        }
        return null;
    }

    /**
     * Creates a new BoolTableConstraintStruct using the information stored in the
     * parameter constraint
     */
    private BoolTableConstraintStruct createConstraint(Constraint c) {
        BoolTableConstraintStruct itc = new BoolTableConstraintStruct();
        itc.name = c.getName().toCharArray();
        itc.op = c.getOp();
        itc.val = c.getBoolVal();
        return itc;
    }

    /**
     * Installs the given constraint to the appropriate table
     */
    private void installConstraint(BoolTableConstraintStruct c) {
        usedConstraints.add(c);
        String s = c.name.toString();
        if (indexes.get(s) == null) {
            BoolOperatorsTable emptyTable = null;
            indexes.put(s, emptyTable);
        }
        if (c.op == EQ)
            indexes.get(s).eq.put(c.val, c);
        else
            indexes.get(s).df.put(c.val, c);

    }

    /**
     * Processes the given constraint by updating the predCount and, if needed,
     * the mh structures
     */
    private void processConstraint(BoolTableConstraintStruct c, MatchingHandler mh,
                                   Map<TablePred, Integer> predCount) {
        for (TablePred it : c.connectedPredicates
                ) {
            // If satisfied for the first time, sets count to 1
            if (predCount.get(it) == null)
                predCount.put(it, 1);
                // Otherwise increases count by one
            else
                predCount.put(it, predCount.get(it) + 1);
            // Otherwise increases count by one
            if (predCount.get(it) == it.getConstraintsNum())
                addToMatchingHandler(mh, it);
            ;
        }
    }
}
