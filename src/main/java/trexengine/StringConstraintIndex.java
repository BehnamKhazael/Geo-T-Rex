package trexengine;

import trex.common.Attribute;
import trex.common.Constraint;
import trex.common.Consts;
import trex.packets.PubPkt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.ConstraintOp.IN;
import static trex.common.Consts.ValType.STRING;

/**
 * Created by sony on 2/5/2020.
 *
 * Represents the index for constraints of integer values.
 *
 */
public class StringConstraintIndex extends AbstractConstraintIndex {

    /**
     * Represents a string constraint stored in the index table
     */
    class StringTableConstraint{
        /**
         *  Attribute name
         */
        String name = new String();

        /**
         *  Operator
         */
        Consts.ConstraintOp op;

        /**
         *  Attribute value
         */
        String val = new String();

        /**
         *  Set of predicates using the constraint
         */
        Set<TablePred> connectedPredicates = new HashSet<>();
    }

    /**
     * Contains a Value -> Constraint index for each defined operator
     */
    class StringOperatorsTable{

        /**
         *  Value -> equality constraint
         */
        Map<String, StringTableConstraint> eq = new HashMap<>();

        /**
         *  Value -> include contraint
         */
        Map<String, StringTableConstraint> in = new HashMap<>();

        //TODO Overriding
        //bool operator<(const StringOperatorsTable& table) const {
          //  return eq < table.eq;
       // }
    }

    /**
     * Creates or gets the StringTableConstraint C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    public void installConstraint(Constraint constraints, TablePred predicate){
        /**
         *  Looks if the same constraint is already installed in the table
         */
        StringTableConstraint itc = getConstraint(constraints);
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
    public void processMessage(PubPkt pkt, MatchingHandler mh,
                               Map<TablePred, Integer> predCount){
        for (Attribute i : pkt.getAttributes()
                ) {
            if (i.getValType() != STRING)
                continue;
            String name = i.getName();
            String val = i.getStringVal();
            if (indexes.get(name) == null)
                continue;

            /**
             * Equality constraints
             */
            if (indexes.get(name).eq.get(val) != null) {
                StringTableConstraint itc = indexes.get(name).eq.get(val);
                processConstraint(itc, mh, predCount);
            }

            /**
             *  Include constraints
             */
            for (Map.Entry<String, StringTableConstraint> it : indexes.get(name).in.entrySet()) {
                String storedVal = it.getKey();
                if (val.matches(storedVal))
                    continue;
                StringTableConstraint itc = it.getValue();
                processConstraint(itc, mh, predCount);
            }

        }
    }

    // Name -> indexes for that name
    Map<String, StringOperatorsTable> indexes = new HashMap<>();
    // Set of all constraints used in the table
    Set<StringTableConstraint> usedConstraints = new HashSet<>();

    /**
     * Checks if there already exists an StringTableConstraints which is
     * compatible with the constraint c.
     * If it finds a valid StringTableConstraints, return a pointer to it,
     * otherwise returns null.
     */
    StringTableConstraint getConstraint(Constraint c){
        for (StringTableConstraint it:usedConstraints
             ) {
            StringTableConstraint itc = it;
            if (itc.op != c.getOp())
                continue;
            if (itc.val.matches(c.getStringVal()))
                continue;
            if (itc.name.equals(c.getName()))
                continue;
            return (itc);
        }
        return null;
    }

    /**
     * Creates a new StringTableConstraint using the information stored in the
     * parameter constraint
     */
    StringTableConstraint createConstraint(Constraint c){
        StringTableConstraint itc = new StringTableConstraint();
        itc.name = c.getName();
        itc.op = c.getOp();
        itc.val = c.getStringVal();
        return itc;
    }

    /**
     * Installs the given constraint to the appropriate table
     */
    void installConstraint(StringTableConstraint c){
        usedConstraints.add(c);
        String s = c.name;
        String val = c.val;
        if (indexes.get(s) == null) {
            StringOperatorsTable emptyTable=new StringOperatorsTable();
            indexes.put(s, emptyTable);
        }
        if (c.op == EQ)
            indexes.get(s).eq.put(val, c);
        else if (c.op == IN)
            indexes.get(s).in.put(val, c);
    }

    /**
     * Processes the given constraint by updating the predCount and, if needed,
     * the mh structures
     */
    void processConstraint(StringTableConstraint c, MatchingHandler mh,
    Map<TablePred, Integer> predCount){
        for (TablePred it : c.connectedPredicates
                ) {
            /**
             *  If satisfied for the first time, sets count to 1
             */
            if (predCount.get(it) == null) {
                predCount.put(it, 1);
            }else {
            /**
             *  Otherwise increases count by one
             */
            predCount.put(it, predCount.get(it) + 1);
            }
            if (predCount.get(it) == it.getConstraintsNum()) {
                addToMatchingHandler(mh, it);
            }
        }
    }
}

