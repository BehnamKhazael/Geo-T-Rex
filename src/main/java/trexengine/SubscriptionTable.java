package trexengine;

import trex.packets.PubPkt;

import java.util.Set;

/**
 * Created by sony on 2/10/2020.
 */
public class SubscriptionTable {
    Set<PubPktListener> listeners;

    void addListener(PubPktListener listener) {
        listeners.add(listener);
    }

    void removeListener(PubPktListener listener) {
        listeners.remove(listener);
    }

    void processPublication(PubPkt pkt) {

//        for (set<PubPktListener*>::iterator it = listeners.begin();
//        it != listeners.end();
//        ++it){
//            PubPktListener  list =it;
//            list.processPubPkt(pkt);
//        }


    }
    class PubPktListener{}
}

