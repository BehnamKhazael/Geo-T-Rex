package trex.examples;

import trex.marshalling.BufferedPacketUnmarshaller;
import trex.packets.RulePkt;
import trex.ruleparser.TRexRuleParser;

import java.io.IOException;

/**
 * Created by sony on 1/28/2020.
 */
public class RuleTester {
    static String teslaRule;
    private BufferedPacketUnmarshaller unmarshaller;

    public static void main(String[] args) throws IOException {
        teslaRule = "Assign 2000=>Smoke," +
                "2001=>Temp," +
                "2100=>Fire " +
                "Define Fire(area:GEOMETRY,measuredTemp:Float)" +
                "From Smoke(area=>$a)" +
                "and Each Temp( [ GEOMETRY ] area=$a,value INTERSECT \"POLYGON((30 10, 40 40, 20 40, 10 20, 30 10)))\") within" +
                " 300000from Smoke" +
                " Where area := Smoke.area,measuredTemp := Temp.value;";
//        teslaRule = "Assign 2000=>Smoke," +
//                "" +
//                "2100=>Fire " +
//                "Define Fire(area:string)" +
//                "From Smoke(area=\"$a\")" +
//                "" +
//                "" +
//                " Where area:=\"$a\";";
        RulePkt rule = TRexRuleParser.parse(teslaRule, 2100);
//        try {
//            rule = TRexRuleParser.parse(teslaRule, 0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        byte[] bytes;
//        bytes = Marshaller.marshalRule(rule, Consts.EngineType.CPU);
//        RulePkt unMarshalled = (RulePkt) Unmarshaller.unmarshal(bytes, new MutableInt(0));
//        System.out.println(unMarshalled.toString());
//        unMarshalled.getPredicates().values().stream()
//                .forEach(System.out::println);
        //System.out.println(Arrays.asList(unMarshalled.getPredicates()));
    }


}
