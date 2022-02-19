package trex.examples;

import trex.common.*;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;

import java.util.ArrayList;

import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;



/**
 * @author Behnam Khazael
 */

public class FireEvent {

    public static int EVENT_SMOKE = 10;
    public static int EVENT_TEMP = 11;
    public static int EVENT_FIRE = 12;
    public RulePkt buildRule(){
        RulePkt rule= new RulePkt(false);

        int indexPredSmoke= 0;
        int indexPredTemp= 1;

        Long fiveMin = 5L; //1000L*60L*5L;

        // Smoke root predicate
        // Fake constraint as a temporary workaround to an engine's bug
        // FIXME remove workaround when bug fixed
        Constraint fakeConstr[] = new Constraint[1];
        fakeConstr[0] = new Constraint();
//        fakeConstr[1] = new Constraint();
        fakeConstr[0].setName("area");
        fakeConstr[0].setValType(STRING);
        fakeConstr[0].setOp(Consts.ConstraintOp.EQ);
        fakeConstr[0].setStringVal("office");
        rule.addRootPredicate(EVENT_SMOKE, fakeConstr, 1);


        // Temp predicate
        // Constraint: Temp.value > 45
        Constraint tempConstr[] = new Constraint[1];
        tempConstr[0] = new Constraint();
        tempConstr[0].setName("value");
        tempConstr[0].setValType(INT);
        tempConstr[0].setOp(GT);
        tempConstr[0].setIntVal(45);
        rule.addPredicate(EVENT_TEMP, tempConstr, 1, indexPredSmoke, fiveMin, EACH_WITHIN);

        // Parameter: Smoke.area=Temp.area
        rule.addParameterBetweenStates(indexPredSmoke, "area", indexPredTemp, "area");

        // Fire template
        EventTemplate fireTemplate= new EventTemplate(EVENT_FIRE);
        // Area attribute in template
        OpTree areaOpTree= new OpTree(new RulePktValueReference(indexPredSmoke, STATE, "area"), STRING);
        fireTemplate.addAttribute("area", areaOpTree);
        // MeasuredTemp attribute in template
        OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(indexPredTemp, STATE, "value"), INT);
        fireTemplate.addAttribute("measuredtemp", measuredTempOpTree);

        rule.setEventTemplate(fireTemplate);

        return rule;
    }

    public SubPkt buildSubscription() {
        Constraint constr[] = new Constraint[1];
        // Area constraint
        constr[0] = new Constraint();
        constr[0].setName("area");
        constr[0].setValType(STRING);
        constr[0].setOp(EQ);
        constr[0].setStringVal("office");

        return new SubPkt(EVENT_FIRE, constr, 1);
    }

    public ArrayList<PubPkt> buildPublication(){

        // Temp event
        Attribute tempAttr[] = new Attribute[2];
        tempAttr[0] = new Attribute();
        tempAttr[1] = new Attribute();
        // Value attribute
        tempAttr[0].setName("value");
        tempAttr[0].setValType(INT);
        tempAttr[0].setIntVal(50);
        // Area attribute
        tempAttr[1].setName("area");
        tempAttr[1].setValType(STRING);
        tempAttr[1].setStringVal("office");
        PubPkt tempPubPkt= new PubPkt(EVENT_TEMP, tempAttr, 2);

        // Smoke event
        // Area attribute
        Attribute smokeAttr[] = new Attribute[1];
        smokeAttr[0] = new Attribute();
        smokeAttr[0].setName("area");
        smokeAttr[0].setValType(STRING);
        smokeAttr[0].setStringVal("office");
        PubPkt smokePubPkt= new PubPkt(EVENT_SMOKE, smokeAttr, 1);
        smokePubPkt.setTimeStamp(System.currentTimeMillis()+3);

        ArrayList<PubPkt> pubPkts = new ArrayList<>();


        pubPkts.add(tempPubPkt);
        pubPkts.add(smokePubPkt);

        return pubPkts;
    }

}
