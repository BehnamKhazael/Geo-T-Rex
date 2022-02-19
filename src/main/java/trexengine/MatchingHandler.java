package trexengine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sony on 2/3/2020.
 */
public class MatchingHandler {
    private Map<Integer, Set<Integer>> matchingStates = new HashMap<>();
    private Map<Integer, Set<Integer>> matchingAggregates = new HashMap<>();
    private Map<Integer, Set<Integer>> matchingNegations= new HashMap<>();

    public Map<Integer, Set<Integer>> getMatchingStates() {
        return matchingStates;
    }

    public void setMatchingStates(Map<Integer, Set<Integer>> matchingStates) {
        this.matchingStates.putAll(matchingStates);
    }

    public Map<Integer, Set<Integer>> getMatchingAggregates() {
        return matchingAggregates;
    }

    public void setMatchingAggregates(Map<Integer, Set<Integer>> matchingAggregates) {
        this.matchingAggregates.putAll(matchingAggregates);
    }

    public Map<Integer, Set<Integer>> getMatchingNegations()
    {
        return matchingNegations;
    }

    public void setMatchingNegations(Map<Integer, Set<Integer>> matchingNegations) {
        this.matchingNegations.putAll(matchingNegations);
    }

//    public MatchingHandler(MatchingHandler mh){
//        this.matchingAggregates = mh.matchingAggregates;
//        this.matchingStates = mh.matchingStates;
//        this.matchingNegations = mh.matchingNegations;
//    }
    //public MatchingHandler(){};
}
