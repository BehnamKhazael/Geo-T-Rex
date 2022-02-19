package trex.examples;

import trex.common.*;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;


import java.util.ArrayList;


import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;

/**
 * Created by sony on 2/8/2020.
 */
public class RuleR0 {
    // Event types
    public static int EVENT_POSITION = 1;
    public static int EVENT_STOPPEDPOSITION = 2;
    public static int EVENT_GATHERING = 3;
    public static int EVENT_SMOKE = 10;
    public static int EVENT_TEMP = 11;
    public static int EVENT_FIRE = 12;


    public SubPkt buildSubscription() {
        Constraint constr[] = new Constraint[1];
        // Area constraint
        constr[0] = new Constraint();
        constr[0].setName("area");

        constr[0].setValType(STRING);
        constr[0].setOp(EQ);
        constr[0].setStringVal("office");


        return new SubPkt(EVENT_FIRE);
    }

    ;

   public ArrayList<PubPkt> buildPublication() {
        Attribute attr[] = new Attribute[2];
        // Value attribute
        attr[0] = new Attribute();
        attr[0].setName("value");
        //
        attr[0].setValType(INT);
        attr[0].setIntVal(55);
        // Area attribute
        attr[1] = new Attribute();
        attr[1].setName("area");

        attr[1].setValType(STRING);
        attr[1].setStringVal("office");

        PubPkt pubPkt= new PubPkt(EVENT_TEMP, attr, 2);

        ArrayList<PubPkt> pubPkts = new ArrayList<>();
        pubPkts.add(pubPkt);
        return pubPkts;
    }

    ;

    // Attribute names
    static char ATTR_TEMPVALUE[] = "value".toCharArray();
    static char ATTR_AREA[] = "area".toCharArray();
    static char ATTR_MEASUREDTEMP[] = "measuredTemp".toCharArray();

    // Possible values for attribute "area"
    static char AREA_GARDEN[] = "garden".toCharArray();
    static char AREA_OFFICE[] = "office".toCharArray();
    static char AREA_TOILET[] = "toilet".toCharArray();

    public RulePkt buildRule() {
        RulePkt rule = new RulePkt(true);

        int indexPredTemp = 0;

        // Temp root predicate
        Constraint tempConstr[] = new Constraint[1];
        tempConstr[0] = new Constraint();
        tempConstr[0].setName("value");
        tempConstr[0].setValType(INT);
        tempConstr[0].setOp(GT);
        tempConstr[0].setIntVal(45);
        rule.addRootPredicate(11, tempConstr, 1);

        // Fire template
        EventTemplate fireTemplate = new EventTemplate(12);

        // Area attribute in template
        OpTree areaOpTree = new OpTree(new RulePktValueReference(indexPredTemp, STATE, "area"), STRING);
        fireTemplate.addAttribute("area", areaOpTree);
        // MeasuredTemp attribute in template
        OpTree measuredTempOpTree = new OpTree(new RulePktValueReference(indexPredTemp, STATE, "value"), INT);
        fireTemplate.addAttribute("value", measuredTempOpTree);

        rule.setEventTemplate(fireTemplate);

        return rule;
    }
}
