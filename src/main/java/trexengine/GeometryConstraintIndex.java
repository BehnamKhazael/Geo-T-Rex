package trexengine;

import com.vividsolutions.jts.geom.Geometry;
import trex.common.Attribute;
import trex.common.Constraint;
import trex.common.Consts;
import trex.packets.PubPkt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static trex.common.Consts.ConstraintOp.INTERIORPOINT;
import static trex.common.Consts.ConstraintOp.DISTANCE;
import static trex.common.Consts.ConstraintOp.CONTAINS;
import static trex.common.Consts.ConstraintOp.INTERSECT;
import static trex.common.Consts.ValType.GEOMETRY;

/**
 * Created by sony on 9/28/2020.
 */
public class GeometryConstraintIndex extends AbstractConstraintIndex {

    /**
     * Represents a GEOMETRY constraint stored in the index table
     */
    class GeometryTableConstraint {
        /**
         * Attribute name
         */
        String name = new String();

        /**
         * Operator
         */
        Consts.ConstraintOp op;

        /**
         * Attribute value
         */
        Geometry val;

        /**
         * Set of predicates using the constraint
         */
        Set<TablePred> connectedPredicates = new HashSet<>();
    }

    /**
     * Contains a Value -> Constraint index for each defined operator
     */
    class GeometryOperatorsTable {

        /**
         * Value -> INTERIORPOINT constraint
         */
        Map<Geometry, GeometryConstraintIndex.GeometryTableConstraint> INTERIORPOINT = new HashMap<>();

        /**
         * Value -> DISTANCE constraint
         */
        Map<Geometry, GeometryConstraintIndex.GeometryTableConstraint> DISTANCE = new HashMap<>();

        /**
         * Value -> CONTAINS constraint
         */
        Map<Geometry, GeometryConstraintIndex.GeometryTableConstraint> CONTAINS = new HashMap<>();

        /**
         * Value -> INTERSECT constraint
         */
        Map<Geometry, GeometryConstraintIndex.GeometryTableConstraint> INTERSECT = new HashMap<>();


    }

    /**
     * Creates or gets the GeometryTableConstraint C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    public void installConstraint(Constraint constraints, TablePred predicate) {
        /**
         *  Looks if the same constraint is already installed in the table
         */
        GeometryConstraintIndex.GeometryTableConstraint itc = getConstraint(constraints);
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
                               Map<TablePred, Integer> predCount) {
        for (Attribute i : pkt.getAttributes()
                ) {
            if (i.getValType() != GEOMETRY)
                continue;
            String name = i.getName();
            Geometry val = i.getGeometryVal();
            if (indexes.get(name) == null)
                continue;

            /**
             * CONTAINS constraints
             */
            for (Map.Entry<Geometry, GeometryTableConstraint> rit : indexes.get(name).CONTAINS.entrySet()) {
//                if (rit.getKey().getEnvelope().contains(val.getEnvelope()))
//                    break;
                GeometryTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            /**
             * INTERSECT constraints
             */
            for (Map.Entry<Geometry, GeometryTableConstraint> rit : indexes.get(name).INTERSECT.entrySet()) {
//                if (rit.getKey().getEnvelope().intersects(val.getEnvelope()))
//                    break;
                GeometryTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }

            /**
             * INTERIORPOINT constraints
             */
            for (Map.Entry<Geometry, GeometryTableConstraint> rit : indexes.get(name).INTERIORPOINT.entrySet()) {
//                if (rit.getKey().getEnvelope().intersects(val.getEnvelope()))
//                    break;
                GeometryTableConstraint itc = rit.getValue();
                processConstraint(itc, mh, predCount);

            }



        }
    }

    // Name -> indexes for that name
    Map<String, GeometryConstraintIndex.GeometryOperatorsTable> indexes = new HashMap<>();
    // Set of all constraints used in the table
    Set<GeometryConstraintIndex.GeometryTableConstraint> usedConstraints = new HashSet<>();

    /**
     * Checks if there already exists an GeometryTableConstraints which is
     * compatible with the constraint c.
     * If it finds a valid GeometryTableConstraints, return a pointer to it,
     * otherwise returns null.
     */
    GeometryConstraintIndex.GeometryTableConstraint getConstraint(Constraint c) {
        for (GeometryConstraintIndex.GeometryTableConstraint it : usedConstraints
                ) {
            GeometryConstraintIndex.GeometryTableConstraint itc = it;
            if (itc.op != c.getOp())
                continue;
            if (itc.val.equals(c.getGeometryVal()))
                continue;
            if (itc.name.equals(c.getName()))
                continue;
            return (itc);
        }
        return null;
    }

    /**
     * Creates a new GeometryTableConstraint using the information stored in the
     * parameter constraint
     */
    GeometryConstraintIndex.GeometryTableConstraint createConstraint(Constraint c) {
        GeometryConstraintIndex.GeometryTableConstraint itc = new GeometryConstraintIndex.GeometryTableConstraint();
        itc.name = c.getName();
        itc.op = c.getOp();
        itc.val = c.getGeometryVal();
        return itc;
    }

    /**
     * Installs the given constraint to the appropriate table
     */
    void installConstraint(GeometryConstraintIndex.GeometryTableConstraint c) {
        usedConstraints.add(c);
        String s = c.name;
        Geometry val = c.val;
        if (indexes.get(s) == null) {
            GeometryConstraintIndex.GeometryOperatorsTable emptyTable = new GeometryConstraintIndex.GeometryOperatorsTable();
            indexes.put(s, emptyTable);
        }
        if (c.op == INTERIORPOINT)
            indexes.get(s).INTERIORPOINT.put(val, c);
        else if (c.op == DISTANCE)
            indexes.get(s).DISTANCE.put(val, c);
        else if (c.op == CONTAINS)
            indexes.get(s).CONTAINS.put(val, c);
        else if (c.op == INTERSECT)
            indexes.get(s).INTERSECT.put(val, c);
    }

    /**
     * Processes the given constraint by updating the predCount and, if needed,
     * the mh structures
     */
    void processConstraint(GeometryConstraintIndex.GeometryTableConstraint c, MatchingHandler mh,
                           Map<TablePred, Integer> predCount) {
        for (TablePred it : c.connectedPredicates
                ) {
            /**
             *  If satisfied for the first time, sets count to 1
             */
            if (predCount.get(it) == null) {
                predCount.put(it, 1);
            } else {
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
