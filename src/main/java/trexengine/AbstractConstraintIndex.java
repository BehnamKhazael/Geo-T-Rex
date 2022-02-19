package trexengine;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static trex.common.Consts.StateType.*;

/**
 * Created by sony on 2/3/2020.
 *
 * Represents a generic constraint index.
 * It includes all common functions used by constraint indexes
 * specialized for a given type.
 */
public abstract class AbstractConstraintIndex {

    /**
     * Adds the information of tp in mh
     */
    void addToMatchingHandler(MatchingHandler mh, TablePred tp){
        // Predicate refers to states
        if (tp.getStateType() == STATE) {
            Map<Integer, Set<Integer>>  it = mh.getMatchingStates();
            if (!it.containsKey(tp.getRuleId())) {
                Set<Integer> states = new TreeSet<>();
                states.add(tp.getStateId());
                mh.getMatchingStates().put(tp.getRuleId(), states);
            } else {
                it.keySet().add(tp.getStateId());
            }
        }
        // Predicate refers to aggregates
        else if (tp.getStateType() == AGG) {
            Map<Integer, Set<Integer>> it = mh.getMatchingAggregates();
            if (!it.containsKey(tp.getRuleId())) {
                Set<Integer> aggs = new TreeSet<>();;
                aggs.add(tp.getStateId());
                mh.getMatchingAggregates().put(tp.getRuleId(), aggs);
            } else {
                it.keySet().add(tp.getStateId());
            }
        }
        // Predicate refers to negations
        else if (tp.getStateType() == NEG) {
            Map<Integer, Set<Integer>> it = mh.getMatchingNegations();
            if (!it.containsKey(tp.getRuleId())) {
                Set<Integer> negs = new TreeSet<>();
                negs.add(tp.getStateId());
                mh.getMatchingNegations().put(tp.getRuleId(), negs);
            } else {
                it.keySet().add(tp.getStateId());
            }
        }
    }
}
