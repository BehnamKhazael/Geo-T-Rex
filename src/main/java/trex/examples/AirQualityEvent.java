package trex.examples;

import trex.common.*;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.StateType.AGG;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.FLOAT;
import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;

/**
 * Created by sony on 3/18/2020.
 */
public class AirQualityEvent {

//    /**
//     *  * Rule R1:
//     *
//     * define	Fire(area: string, measuredTemp: int)
//     * from		Smoke(area=$a) and
//     * 			each Temp(area=$a and value>45) within 5 min. from Smoke
//     * where	area=Smoke.area and measuredTemp=Temp.value
//     */
//
//
    public static int EVENT_CO_PPM = 14;
    public static int EVENT_CO_PPM_1 = 19;
    public static int EVENT_CO_POISONING_ALARM = 16;

    public RulePkt buildRule(){
        RulePkt rule= new RulePkt(false);

        int indexPredCo_1= 0;
        int indexPredCo_2= 1;

        Long fiveMin = 50000L; //1000L*60L*5L;

        // Smoke root predicate
        // Fake constraint as a temporary workaround to an engine's bug
        // FIXME remove workaround when bug fixed
        Constraint fakeConstr[] = new Constraint[1];
        fakeConstr[0] = new Constraint();
//        fakeConstr[1] = new Constraint();
        fakeConstr[0].setName("area");
        fakeConstr[0].setValType(STRING);
        fakeConstr[0].setOp(EQ);
        fakeConstr[0].setStringVal("street_a");

//        fakeConstr[1] = new Constraint();
//        fakeConstr[1].setName("value");
//        fakeConstr[1].setValType(INT);
//        fakeConstr[1].setOp(GT);
//        fakeConstr[1].setIntVal(20);
        rule.addRootPredicate(EVENT_CO_PPM_1, fakeConstr, 1);


        // Temp predicate
        // Constraint: Temp.value > 45
        Constraint fakeConstr2[] = new Constraint[2];
        fakeConstr2[0] = new Constraint();
//        fakeConstr[1] = new Constraint();
        fakeConstr2[0].setName("area");
        fakeConstr2[0].setValType(STRING);
        fakeConstr2[0].setOp(EQ);
        fakeConstr2[0].setStringVal("street_a");

        fakeConstr2[1] = new Constraint();
        fakeConstr2[1].setName("value");
        fakeConstr2[1].setValType(INT);
        fakeConstr2[1].setOp(GT);
        fakeConstr2[1].setIntVal(20);



        TAggregate tAggregate = new TAggregate(EVENT_CO_PPM,500000L,indexPredCo_2, Consts.AggregateFun.AVG,"CoConcentration");
        //tAggregate.addConstraint(fakeConstr2);
        //tAggregate.addConstraint(fakeConstr2);
        rule.addAggregate(tAggregate);
        //rule.addParameterForAggregate(EVENT_CO_PPM,"value",EVENT_CO_PPM,"value");
       rule.addPredicate(EVENT_CO_PPM, fakeConstr2, 2, indexPredCo_1, fiveMin, EACH_WITHIN);
        ComplexParameter parameter = new ComplexParameter();
        parameter.setOperation(GT);
        parameter.setsType(AGG);
        parameter.setType(INT);
        parameter.setLastIndex(0);
        OpTree OpTree= new OpTree(new RulePktValueReference(indexPredCo_2, STATE, "area"), STRING);
        parameter.setRightTree(OpTree);
        OpTree OpTree1= new OpTree(new RulePktValueReference(indexPredCo_2, STATE, "area"), STRING);
        parameter.setLeftTree(OpTree1);
        rule.addComplexParameter(parameter);

        // Parameter: Smoke.area=Temp.area
        rule.addParameterBetweenStates(indexPredCo_1, "area", indexPredCo_2, "area");


        // Fire template
        EventTemplate CoTemplate= new EventTemplate(EVENT_CO_POISONING_ALARM);
//        EventTemplateAttr eventTemplateAttr = new EventTemplateAttr("CoConcentration",new OpTree(new OpTree(new RulePktValueReference(indexPredCo_2, STATE, "value"), INT),
//                ));
        // Area attribute in template
        OpTree areaOpTree= new OpTree(new RulePktValueReference(indexPredCo_2, STATE, "area"), STRING);
        CoTemplate.addAttribute("area", areaOpTree);
        // MeasuredTemp attribute in template
        //OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(indexPredCo_2, AGG, "value"), INT);

        OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(1, AGG, "value"), INT);
//
//        CoTemplate.addAttribute(new EventTemplateAttr("CoConcentration", ));

        CoTemplate.addAttribute("CoConcentration", measuredTempOpTree);



        rule.setEventTemplate(CoTemplate);

        return rule;
    }

    public SubPkt buildSubscription() {
        Constraint constr[] = new Constraint[1];
        // Area constraint
        constr[0] = new Constraint();
        constr[0].setName("area");
        constr[0].setValType(STRING);
        constr[0].setOp(EQ);
        constr[0].setStringVal("street_a");

        return new SubPkt(EVENT_CO_POISONING_ALARM, constr, 1);
    }

    public ArrayList<PubPkt> buildPublication(){

        // Temp event
        Attribute tempAttr[] = new Attribute[2];
        tempAttr[0] = new Attribute();
        //tempAttr[1] = new Attribute();
        // Value attribute
//        tempAttr[1].setName("value");
//        tempAttr[1].setValType(INT);
//        tempAttr[1].setIntVal(25);
        // Area attribute
        tempAttr[0].setName("area");
        tempAttr[0].setValType(STRING);
        tempAttr[0].setStringVal("street_a");
        PubPkt CoSaturation_1= new PubPkt(EVENT_CO_PPM_1, tempAttr, 1);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Temp event
        Attribute tempAttr2[] = new Attribute[2];
        tempAttr2[0] = new Attribute();
        tempAttr2[1] = new Attribute();
        // Value attribute
        tempAttr2[1].setName("value");
        tempAttr2[1].setValType(INT);
        tempAttr2[1].setIntVal(35);
        // Area attribute
        tempAttr2[0].setName("area");
        tempAttr2[0].setValType(STRING);
        tempAttr2[0].setStringVal("street_a");
        PubPkt CoSaturation_2= new PubPkt(EVENT_CO_PPM, tempAttr2, 2);



        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Temp event
        Attribute tempAttr3[] = new Attribute[2];
        tempAttr3[0] = new Attribute();
        tempAttr3[1] = new Attribute();
        // Value attribute
        tempAttr3[1].setName("value");
        tempAttr3[1].setValType(INT);
        tempAttr3[1].setIntVal(35);
        // Area attribute
        tempAttr3[0].setName("area");
        tempAttr3[0].setValType(STRING);
        tempAttr3[0].setStringVal("street_a");
        PubPkt CoSaturation_3= new PubPkt(EVENT_CO_PPM, tempAttr2, 2);



        ArrayList<PubPkt> pubPkts = new ArrayList<>();


        pubPkts.add(CoSaturation_2);
        pubPkts.add(CoSaturation_3);
        pubPkts.add(CoSaturation_1);

        return pubPkts;
    }

//    // Event types
//    public static int EVENT_CO_PPM = 14;
//    public static int EVENT_CO_POISONING_ALARM = 16;
//
//
//    public SubPkt buildSubscription() {
//        Constraint constr[] = new Constraint[1];
//        // Area constraint
//        constr[0] = new Constraint();
//        constr[0].setName("area");
//
//        constr[0].setValType(STRING);
//        constr[0].setOp(EQ);
//        constr[0].setStringVal("street_a");
//
//
//        return new SubPkt(EVENT_CO_POISONING_ALARM);
//    }
//
//    ;
//
//    ArrayList<PubPkt> buildPublication() {
//        Attribute attr[] = new Attribute[2];
//        // Value attribute
//        attr[0] = new Attribute();
//        attr[0].setName("value");
//        //
//        attr[0].setValType(INT);
//        attr[0].setIntVal(25);
//        // Area attribute
//        attr[1] = new Attribute();
//        attr[1].setName("area");
//
//        attr[1].setValType(STRING);
//        attr[1].setStringVal("street_a");
//
//        PubPkt pubPkt= new PubPkt(EVENT_CO_PPM, attr, 2);
//
//        ArrayList<PubPkt> pubPkts = new ArrayList<>();
//        pubPkts.add(pubPkt);
//        pubPkts.add(pubPkt);
//        return pubPkts;
//    }
//
//
//    public RulePkt buildRule() {
//        RulePkt rule = new RulePkt(true);
//
//        int indexPredTemp = 0;
//
//        // Temp root predicate
//        Constraint CoConstr[] = new Constraint[1];
//        CoConstr[0] = new Constraint();
//        CoConstr[0].setName("value");
//        CoConstr[0].setValType(INT);
//        CoConstr[0].setOp(GT);
//        CoConstr[0].setIntVal(24);
//        rule.addRootPredicate(EVENT_CO_PPM, CoConstr, 1);
//
//
//        // Fire template
//        EventTemplate CoConcentrationTemplate = new EventTemplate(EVENT_CO_POISONING_ALARM);
//
//        // Area attribute in template
//        OpTree areaOpTree = new OpTree(new RulePktValueReference(indexPredTemp, STATE, "area"), STRING);
//        CoConcentrationTemplate.addAttribute("area", areaOpTree);
//        // MeasuredTemp attribute in template
//        OpTree measuredCoConcentrationOpTree = new OpTree(new RulePktValueReference(indexPredTemp, STATE, "value"), INT);
//        CoConcentrationTemplate.addAttribute("CoConcentration", measuredCoConcentrationOpTree);
//
//        rule.setEventTemplate(CoConcentrationTemplate);
//
//        return rule;
//    }

}
