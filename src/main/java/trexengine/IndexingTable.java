package trexengine;

import trex.common.Constraint;
import trex.common.EventPredicate;
import trex.common.Negation;
import trex.common.TAggregate;
import trex.packets.PubPkt;
import trex.packets.RulePkt;

import java.util.*;

import static trex.common.Consts.StateType.*;

/**
 * Created by @author Behnam Khazael on 2/6/2020.
 */

/**
 * Indexes the information about the states used in the given automaton
 *
 * An IndexingTable indexes automaton states, making it more efficient to
 * identify states that may be interested in a message.
 *
 * The table indexes positive states as well as negations.
 * It contains a different indexing structure for each type of value allowed in
 * messages; this means that adding a new type needs the definition of a new
 * indexing structure.
 *
 * The indexing table only considers "static" constraints, i.e. constraints that
 * only depend on the information present in the state at deploy time. Timing
 * constraints and parameters (which depend on the history of messages received)
 * are not indexed since they would need a high indexing cost at runtime.
 *
 */
public class IndexingTable {


    public void installRulePkt(RulePkt pkt){
        int numPreds = pkt.getPredicatesNum();
        // Iterating on rule predicates (states)
        for (int state = 0; state < numPreds; state++) {
            EventPredicate pred = pkt.getPredicates(state);
            TablePred tp = new TablePred();
            int eventType = pred.getEventType();
            //TODO check Rule packet class we may change it!
            tp.setRuleId(pkt.getRuleId());;
            tp.setStateId(state);
            tp.setConstraintsNum(pred.getConstraints().size());
            tp.setStateType(STATE);
            installTablePredicate(eventType, pred.getConstraints(), pred.getConstraints().size(), tp);
        }
        // Iterating on rule aggregates
        int aggregatesNum = pkt.getAggregatesNum();
        for (int aggId = 0; aggId < aggregatesNum; aggId++) {
            TAggregate agg = pkt.getAggregate(aggId);
            TablePred tp = new TablePred();
            tp.setRuleId(pkt.getRuleId());
            tp.setStateId(aggId);
            //TODO check if getConstraints().size is equal to ConstraintsNum semantically
            // We may add this to TAggregate!!
            tp.setConstraintsNum(agg.getConstraints().size());
            tp.setStateType(AGG);
            int eventType = agg.getEventType();
            installTablePredicate(eventType, agg.getConstraints(), agg.getConstraints().size(), tp);
        }
        // Iterating on rule negations
        int negationsNum = pkt.getNegationsNum();
        for (int negId = 0; negId < negationsNum; negId++) {
            Negation neg = pkt.getNegation(negId);
            TablePred tp = new TablePred();
            tp.setRuleId(pkt.getRuleId());
            tp.setStateId(negId);
            tp.setConstraintsNum(neg.getConstraints().size());
            tp.setStateType(NEG);
            int eventType = neg.getEventType();
            installTablePredicate(eventType, neg.getConstraints(), neg.constraintsNum(), tp);
        }
    }

    /**
     * Fills mh with the set of all matching states
     */
    public void processMessage(PubPkt pkt, MatchingHandler mh){
        int eventType = pkt.getEventType();
        // predCount stores intermediate results, it will be useful when more types
        // will be available
        Map<TablePred, Integer> predCount = new HashMap<>();
        if (noIndex.get(eventType) != null)
            noIndex.get(eventType).processMessage(pkt, mh, predCount);
        if (intIndex.get(eventType) != null)
            intIndex.get(eventType).processMessage(pkt, mh, predCount);
        if (floatIndex.get(eventType) != null)
            floatIndex.get(eventType).processMessage(pkt, mh, predCount);
        if (boolIndex.get(eventType) != null)
            boolIndex.get(eventType).processMessage(pkt, mh, predCount);
        if (stringIndex.get(eventType) != null)
            stringIndex.get(eventType).processMessage(pkt, mh, predCount);
        if (geometryIndex.get(eventType) != null)
            geometryIndex.get(eventType).processMessage(pkt, mh, predCount);
    }

    /**
     *  EventType -> Index for constraints on integer values
     */
    Map<Integer, IntConstraintIndex> intIndex = new HashMap<>();

    /*
     EventType -> Index for constraints on float values
     */
    Map<Integer, FloatConstraintIndex> floatIndex = new HashMap<>();

    /**
     *  EventType -> Index for constraints on boolean values
     */
    Map<Integer, BoolConstraintIndex> boolIndex = new HashMap<>();

    /**
     *  EventType -> Index for constraints on string values
     */
    Map<Integer, StringConstraintIndex> stringIndex = new HashMap<>();

    /**
     *  EventType -> Index for constraints on string values
     */
    Map<Integer, GeometryConstraintIndex> geometryIndex = new HashMap<>();

    /**
     *  EventType -> Index of "no" constraints
     */
    Map<Integer, NoConstraintIndex> noIndex = new HashMap<>();

    /**
     * Predicates installed in tables
     */
    Set<TablePred> usedPreds = new HashSet<>();

    /**
     * Installs the given TablePred with the specified eventType and constraints.
     * It uses the two functions below to install event type and constraints.
     */
    void installTablePredicate(int eventType, Collection<Constraint> constraint,
                               int numConstraints, TablePred tp){
        usedPreds.add(tp);
        if (numConstraints == 0) {
            installNoConstraint(eventType, tp);
        }

        for (Constraint i: constraint
             ) {
            installConstraint(eventType, i, tp);
        }

    }

    void installNoConstraint(int eventType, TablePred tp){
        if (noIndex.get(eventType) == null) {
            NoConstraintIndex emptyIndex = new NoConstraintIndex();
            noIndex.put(eventType, emptyIndex);
        }
        noIndex.get(eventType).installPredicate(tp);

    }

    void installConstraint(int eventType, Constraint c,
                                  TablePred tp){
        // Installing on the index of the appropriate type
        switch (c.getValType()) {
            case INT:
                if (intIndex.get(eventType) == null) {
                    IntConstraintIndex emptyIndex = new IntConstraintIndex();
                    intIndex.put(eventType, emptyIndex);
                }
                intIndex.get(eventType).installConstraint(c, tp);
                break;
            case FLOAT:
                if (floatIndex.get(eventType) == null) {
                    FloatConstraintIndex emptyIndex = new FloatConstraintIndex();
                    floatIndex.put(eventType, emptyIndex);
                }
                floatIndex.get(eventType).installConstraint(c, tp);
                break;
            case BOOL:
                if (boolIndex.get(eventType) == null) {
                    BoolConstraintIndex emptyIndex = new BoolConstraintIndex();
                    boolIndex.put(eventType, emptyIndex);
                }
                boolIndex.get(eventType).installConstraint(c, tp);
                break;
            case STRING:
                if (stringIndex.get(eventType) == null) {
                    StringConstraintIndex emptyIndex = new StringConstraintIndex();
                    stringIndex.put(eventType, emptyIndex);
                }
                stringIndex.get(eventType).installConstraint(c, tp);
                break;
            case GEOMETRY:
                if (geometryIndex.get(eventType) == null) {
                    GeometryConstraintIndex emptyIndex = new GeometryConstraintIndex();
                    geometryIndex.put(eventType, emptyIndex);
                }
                geometryIndex.get(eventType).installConstraint(c, tp);
                break;
        }
    }

}
