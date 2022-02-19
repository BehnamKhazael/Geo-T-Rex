package trex.examples;

import trex.packets.RulePkt;
import trex.ruleparser.TRexRuleParser;
import trexengine.TRexEngine;

/**
 * Created by sony on 2/27/2021.
 */
public class check {
    public static void main (String args[]){
        TRexEngine engine = new TRexEngine(1);
        String rulePacketString = "Assign 2000=>Smoke,2001=>Temp,2100=>Fire Define Fire(area:String,mTemp:Float) From Smoke(area=>$a)and Each Temp([String]area=$a,value>45)within 5from Smoke Where area:=Smoke.area,mTemp:=Temp.value;";
        RulePkt rule = TRexRuleParser.parse(rulePacketString, 0);
        engine.processRulePkt(rule);
        engine.finalizing();
    }
}
