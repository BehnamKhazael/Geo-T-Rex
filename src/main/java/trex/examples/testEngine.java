package trex.examples;


import trex.common.Attribute;
import trex.marshalling.Marshaller;
import trex.marshalling.Unmarshaller;
import trex.packets.*;
import trex.ruleparser.TRexRuleParser;
import trex.utils.MutableInt;
import trexengine.ResultListener;
import trexengine.TRexEngine;
import trex.common.Consts.EngineType.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static trex.common.Consts.EngineType.CPU;
import static java.lang.Thread.sleep;
import trex.common.Consts;


/**
 * Created by sony on 2/8/2020.
 */
public class testEngine {
    static String teslaRule;
    public static void main(String args[]) throws IOException {
        testEngine();
    }
    static void testEngine() throws IOException {
//
//
//        UnPubPkt test = new UnPubPkt(11);
//        test.addAttribute(new Attribute("Area", "a"));
//        test.addAttribute(new Attribute("Tmp", 0));
//        test.addAttribute(new Attribute("hhumidity", 0F));
//        TRexPkt pkt  = (TRexPkt) test;
//        byte[] texy =  Marshaller.marshal(pkt);
//
//        TRexPkt ds = Unmarshaller.unmarshal(texy);
////
////        System.out.println(((UnPubPkt)ds).toString());
//
//
//
//
        TRexEngine engine = new TRexEngine(1);
        //RuleR0 testRule = new RuleR0();

       // RuleR1 testRule = new RuleR1();

        RuleR2 testRule = new RuleR2();

        //AirQualityEvent testRule = new AirQualityEvent();

//        String rulePacketString = "Assign 2000=>Smoke," +
//                "2001=>Temp," +
//                "2100=>Fire " +
//                "Define Fire(area:string,measuredTemp:float)" +
//                "From Smoke(area=>$a)" +
//                "and each Temp( [ string ] area=$a,value>45)within" +
//                " 300000from Smoke" +
//                " Where area := Smoke.area,measuredTemp := Temp.value;";

//        //String rulePacketString = "Assign 2000=>Smoke,2001=>Temp,2100=>Fire Define Fire(area:string,mTemp:float) From Smoke(area=>$a)and each Temp([string]area=$a,value>45)within 5from Smoke Where area:=Smoke.area,mTemp:=Temp.value;";
////
        //RulePkt rule = TRexRuleParser.parse(rulePacketString, 2000);
//        RulePkt rule  = testRule.buildRule();
//
//        System.out.println("1111111111111111111111111" +rule.toString());
//        byte[] bytes;
//        bytes = Marshaller.marshalRule(rule, CPU);
//////
//        RulePkt decodeRulePkt2 = (RulePkt) Unmarshaller.unmarshal(bytes);
//        System.out.println("2222222222222222222222222222222222"  + decodeRulePkt2.toString());

////        RulePkt rulePkt =testRule.buildRule();
////        byte[] marshalRule =  Marshaller.marshalRule(rulePkt, CPU);
////
////        MutableInt anInt = new MutableInt(marshalRule.length);
////
////        RulePkt decodeRulePkt = (RulePkt) Unmarshaller .unmarshal(marshalRule, new MutableInt(0));
//
//        SubPkt s = testRule.buildSubscription();
//        byte[] marshalsub = Marshaller.marshal(s);
//
//      //  SubPkt ds = (SubPkt) Unmarshaller.unmarshal(marshalsub);
//
//
//

//        byte[] a = Marshaller.marshalRule(testRule.buildRule(), Consts.EngineType.CPU);
//        engine.processRulePkt((RulePkt) Unmarshaller.unmarshal(a));
        engine.processRulePkt(testRule.buildRule());
        engine.finalizing();

        ResultListener listener= new ResultListener(testRule.buildSubscription());
        engine.addResultListener(listener);
        System.out.println(testRule.buildRule());

        int i = 0;
        ArrayList<byte[]> pubPkts= testRule.buildPublication();
        for (int c = 0; i< 10 ; c++) {
            for (byte[] it :
                    pubPkts) {
                PubPkt pubPkt = (PubPkt) Unmarshaller.unmarshal(it);
                System.out.println("Sending Packet #" + i);
                System.out.println(pubPkt.toString());
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pubPkt.setTimeStamp(System.currentTimeMillis());
                engine.processPubPkt(pubPkt);
                i++;
            }
        }

	/* Expected output: complex event should be created by T-Rex and published
	 * to the TestResultListener, which should print it to screen.
	 */

//        teslaRule = "Assign 2000=>Smoke," +
//                "2001=>Temp," +
//                "2100=>Fire " +
//                "Define Fire(area:string,measuredTemp:float)" +
//                "From Smoke(area=>$a)" +
//                "and each Temp( [ string ] area=$a,value>45)within" +
//                " 300000from Smoke" +
//                " Where area := Smoke.area,measuredTemp := Temp.value;";
//        teslaRule = "Assign 2000=>Smoke," +
//                "" +
//                "2100=>Fire " +
//                "Define Fire(area:string)" +
//                "From Smoke(area=\"$a\")" +
//                "" +
//                "" +
//                " Where area:=\"$a\";";
//        RulePkt rule = null;
//        try {
//            rule = TRexRuleParser.parse(teslaRule, 0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        RuleR1 r1 = new RuleR1();
//
//        RulePkt rule = r1.buildRule();
//
//
//
//
//        System.out.println(rule.toString());
//
//
//
//
//        byte[] bytes;
//        bytes = Marshaller.marshalRule(rule, Consts.EngineType.CPU);
//        RulePkt unMarshalled = (RulePkt) Unmarshaller.unmarshal(bytes);//, new MutableInt(0));
//        System.out.println(unMarshalled.toString());
    }
}
