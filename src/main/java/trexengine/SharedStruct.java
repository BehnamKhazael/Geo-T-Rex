package trexengine;

import trex.packets.PubPkt;

import java.util.Map;
import java.util.Set;

/**
 * Created by sony on 2/7/2020.
 */

public class SharedStruct {
    Map<Integer, StacksRule> StacksRules;
//    pthread_cond_t processCond;
//    pthread_cond_t resultCond;
//    pthread_mutex_t processMutex;
//    pthread_mutex_t resultMutex;
    Integer stillProcessing;
    Boolean finish;
    Integer lowerBound;
    Integer upperBound;
    Set<PubPkt> result;
    MatchingHandler mh;
    Map<Integer, StacksRule> stacksRule = StacksRules;
    PubPkt pkt;
}
