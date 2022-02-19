package trexengine;

import trex.packets.PubPkt;
import trex.packets.RulePkt;

/**
 * Created by sony on 2/3/2020.
 */
public interface Engine {
    void processPubPkt(PubPkt event);
    void processRulePkt(RulePkt rule);
    void addResultListener(ResultListener resultListener);
    void removeResultListener(ResultListener resultListener);
}
