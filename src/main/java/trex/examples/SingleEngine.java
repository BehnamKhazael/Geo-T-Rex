package trex.examples;

import trex.common.Consts;
import trex.marshalling.Marshaller;
import trex.marshalling.Unmarshaller;
import trex.packets.RulePkt;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.IOException;


/**
 * Created by sony on 3/27/2020.
 */
public class SingleEngine {
    // static variable single_instance of type Singleton
    private static TRexEngine single_instance = null;

    // variable of type String
    public String s;

    // private constructor restricted to this class itself
    private SingleEngine()
    {
        s = "Hello I am a string part of Singleton class";
    }

    // static method to create instance of Singleton class
    public static TRexEngine getInstance() throws IOException {
        if (single_instance == null) {
            single_instance = new TRexEngine(1);
            RuleR1 testRule = new RuleR1();
            byte[] a = Marshaller.marshalRule(testRule.buildRule(), Consts.EngineType.CPU);
            single_instance.processRulePkt((RulePkt) Unmarshaller.unmarshal(a));
            //engine.processRulePkt(testRule.buildRule());
            single_instance.finalizing();

            ResultListener listener= new ResultListener(testRule.buildSubscription());
            single_instance.addResultListener(listener);
        }

        return single_instance;
    }
}
