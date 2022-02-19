package trex.examples;

import trex.common.Consts;
import trex.marshalling.Marshaller;
import trex.marshalling.Unmarshaller;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.IOException;


/**
 * Created by sony on 3/27/2020.
 */
public class SeparateEngine {
    static TRexEngine RexEngine;

    public static void main(String args[]) throws IOException {
        //TRexEngine tRexEngine = new TRexEngine(1, new NodeAddress("0.0"));
        TRexEngine tRexEngine = SingleEngine.getInstance();

    }
    public static TRexEngine getEngine(){
        return RexEngine;
    }

    public static void proccess(PubPkt p){
        RexEngine.processPubPkt(p);
    }
}
