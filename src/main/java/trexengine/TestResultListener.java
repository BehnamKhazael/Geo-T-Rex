package trexengine;

import trex.packets.PubPkt;
import trex.packets.SubPkt;

import java.util.Set;

/**
 * Created by sony on 2/8/2020.
 */
public class TestResultListener {

    public TestResultListener(SubPkt subscription){
//        subscription(subscription);
//        id(lastId++);
    };


    void handleResult(Set<PubPkt> genPkts, double procTime){
        for (PubPkt i :
                genPkts) {
            PubPkt pubpkt = i;
            printMessage("New Complex event created:");
            printMessage(pubpkt.toString());
//            if (matches(subscription, pubPkt)){
//                printMessage("My subscription is matched");
//            } else {
//                printMessage("My subscription is not matched");
//            }
        }
    }
    int getId() {return id;}
    SubPkt subscription;
    int id;
    void printMessage(String msg){
        System.out.println("TestResultListener" + id + " > " + msg);
    };
}
