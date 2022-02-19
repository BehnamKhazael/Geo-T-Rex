package trex.examples;


import com.vividsolutions.jts.io.WKTReader;
import trex.common.*;
import trex.marshalling.Marshaller;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.ConstraintOp.*;
import static trex.common.Consts.OpTreeType.INNER;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValRefType.RULEPKT;
import static trex.common.Consts.ValType.*;
import static trex.examples.RuleR0.EVENT_FIRE;
import static trex.examples.RuleR0.EVENT_SMOKE;
import static trex.examples.RuleR0.EVENT_TEMP;

/**
 * Created by sony on 2/10/2020.
 *
 *
 * Rule R1:
 *
 * define	Fire(area: string, measuredTemp: int)
 * from		Smoke(area=$a) and
 * 			each Temp(area=$a and value>45) within 5 min. from Smoke
 * where	area=Smoke.area and measuredTemp=Temp.value
 *
 * char RuleR0::ATTR_TEMPVALUE[]= "value";
 char RuleR0::ATTR_AREA[]= "area";
 char RuleR0::ATTR_MEASUREDTEMP[]= "measuredTemp";

 char RuleR0::AREA_GARDEN[]= "garden";
 char RuleR0::AREA_OFFICE[]= "office";
 char RuleR0::AREA_TOILET[]= "toilet";
 *
 */
public class RuleR2 {
    public RulePkt buildRule() {
        RulePkt rule= new RulePkt(false);

        int indexPredSmoke= 0;
        int indexPredTemp= 1;

        WKTReader rdr = new WKTReader();

        Long fiveMin = 1000L*60L*5L;

        // Smoke root predicate
        // Fake constraint as a temporary workaround to an engine's bug
        // FIXME remove workaround when bug fixed
        Constraint fakeConstr[] = new Constraint[1];
        fakeConstr[0] = new Constraint();
//        fakeConstr[1] = new Constraint();
        fakeConstr[0].setName("area");
        fakeConstr[0].setValType(GEOMETRY);
        fakeConstr[0].setOp(INTERSECT);
        try {
            fakeConstr[0].setGeometryVal(rdr.read("POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))"));
            //fakeConstr[0].setGeometryVal(new Polygon());
        } catch (Exception e) {
            e.printStackTrace();
        }
        rule.addRootPredicate(EVENT_SMOKE, fakeConstr, 1);


        // Temp predicate
        // Constraint: Temp.value > 45
        Constraint tempConstr[] = new Constraint[2];
        tempConstr[0] = new Constraint();
        tempConstr[0].setName("value");
        tempConstr[0].setValType(INT);
        tempConstr[0].setOp(GT);
        tempConstr[0].setIntVal(20);
        tempConstr[1] = new Constraint();
        tempConstr[1].setName("accuracy");
        tempConstr[1].setValType(INT);
        tempConstr[1].setOp(LT);
        tempConstr[1].setIntVal(5);
        rule.addPredicate(EVENT_TEMP, tempConstr, 2, indexPredSmoke, fiveMin, EACH_WITHIN);

        // Parameter: Smoke.area INTERSECT Temp.area
        OpTree areaOpTreeSmoke= new OpTree(new RulePktValueReference(indexPredSmoke, STATE, "area"), GEOMETRY);
        OpTree areaOpTreeTemp= new OpTree(new RulePktValueReference(indexPredTemp, STATE, "area"), GEOMETRY);
        rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeTemp ,areaOpTreeSmoke);

        // Fire template
        EventTemplate fireTemplate= new EventTemplate(EVENT_FIRE);
        // Area attribute in template
        OpTree areaOpTree= new OpTree(areaOpTreeSmoke, areaOpTreeTemp, Consts.Op.INTERSECT, GEOMETRY);
        areaOpTree.changeValueReference(new RulePktValueReference(0));
        fireTemplate.addAttribute("area", areaOpTree);
        // MeasuredTemp attribute in template
        OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(indexPredTemp, STATE, "value"), INT);
        fireTemplate.addAttribute("measuredtemp", measuredTempOpTree);

        rule.setEventTemplate(fireTemplate);

        return rule;
    }

    public SubPkt buildSubscription() {
        WKTReader rdr = new WKTReader();
        Constraint constr[] = new Constraint[1];
        // Area constraint
        constr[0] = new Constraint();
        constr[0].setName("area");
        constr[0].setValType(GEOMETRY);
        constr[0].setOp(CONTAINS);
        try {
            constr[0].setGeometryVal(rdr.read("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SubPkt(EVENT_FIRE, constr, 1);
    }

    public ArrayList<byte[]> buildPublication() throws IOException {

        WKTReader rdr = new WKTReader();
        // Temp event
        Attribute tempAttr[] = new Attribute[3];
        tempAttr[0] = new Attribute();
        tempAttr[1] = new Attribute();
        tempAttr[2] = new Attribute();
        // Value attribute
        tempAttr[0].setName("value");
        tempAttr[0].setValType(INT);
        tempAttr[0].setIntVal(44);
        // Area attribute
        tempAttr[1].setName("area");
        tempAttr[1].setValType(GEOMETRY);
        try {
            tempAttr[1].setGeometryVal(rdr.read("POLYGON ((20 20, 40 20, 40 30, 30 30, 20 20))"));
            //tempAttr[1].setGeometryVal(readGeometry("POLYGON ((50 50, 70 50, 70 60, 60 60, 50 50))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempAttr[2].setName("accuracy");
        tempAttr[2].setValType(INT);
        tempAttr[2].setIntVal(1);
        PubPkt tempPubPkt= new PubPkt(EVENT_TEMP, tempAttr, 3);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Smoke event
        // Area attribute
        Attribute smokeAttr[] = new Attribute[1];
        smokeAttr[0] = new Attribute();
        smokeAttr[0].setName("area");
        smokeAttr[0].setValType(GEOMETRY);
        try {
            smokeAttr[0].setGeometryVal(rdr.read("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt smokePubPkt= new PubPkt(EVENT_SMOKE, smokeAttr, 1);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Temp event
        Attribute tempAttr2[] = new Attribute[3];
        tempAttr2[0] = new Attribute();
        tempAttr2[1] = new Attribute();
        tempAttr2[2] = new Attribute();
        // Value attribute
        tempAttr2[0].setName("value");
        tempAttr2[0].setValType(INT);
        tempAttr2[0].setIntVal(50);
        // Area attribute
        tempAttr2[1].setName("area");
        tempAttr2[1].setValType(GEOMETRY);
        try {
            tempAttr2[1].setGeometryVal(rdr.read("POLYGON ((20 20, 40 20, 40 30, 30 30, 20 20))"));
            //tempAttr2[1].setGeometryVal(readGeometry("POLYGON ((50 50, 70 50, 70 60, 60 60, 50 50))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempAttr2[2].setName("accuracy");
        tempAttr2[2].setValType(INT);
        tempAttr2[2].setIntVal(1);
        PubPkt tempPubPkt2= new PubPkt(EVENT_TEMP, tempAttr2, 3);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        // Temp event
        Attribute tempAttr3[] = new Attribute[3];
        tempAttr3[0] = new Attribute();
        tempAttr3[1] = new Attribute();
        tempAttr3[2] = new Attribute();
        // Value attribute
        tempAttr3[0].setName("value");
        tempAttr3[0].setValType(INT);
        tempAttr3[0].setIntVal(80);
        // Area attribute
        tempAttr3[1].setName("area");
        tempAttr3[1].setValType(GEOMETRY);
        try {
            tempAttr3[1].setGeometryVal(rdr.read("POLYGON ((20 20, 40 20, 40 30, 30 30, 20 20))"));
            //tempAttr3[1].setGeometryVal(readGeometry("POLYGON ((50 50, 70 50, 70 60, 60 60, 50 50))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempAttr3[2].setName("accuracy");
        tempAttr3[2].setValType(INT);
        tempAttr3[2].setIntVal(1);
        PubPkt tempPubPkt3= new PubPkt(EVENT_TEMP, tempAttr3, 3);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Temp event
        Attribute tempAttr4[] = new Attribute[3];
        tempAttr4[0] = new Attribute();
        tempAttr4[1] = new Attribute();
        tempAttr4[2] = new Attribute();
        // Value attribute
        tempAttr4[0].setName("value");
        tempAttr4[0].setValType(INT);
        tempAttr4[0].setIntVal(88);
        // Area attribute
        tempAttr4[1].setName("area");
        tempAttr4[1].setValType(GEOMETRY);
        try {
            tempAttr4[1].setGeometryVal(rdr.read("POLYGON ((20 20, 40 20, 40 30, 30 30, 20 20))"));
            //tempAttr4[1].setGeometryVal(readGeometry("POLYGON ((50 50, 70 50, 70 60, 60 60, 50 50))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempAttr4[2].setName("accuracy");
        tempAttr4[2].setValType(INT);
        tempAttr4[2].setIntVal(1);
        PubPkt tempPubPkt4= new PubPkt(EVENT_TEMP, tempAttr4, 3);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Smoke event
        // Area attribute
        Attribute smokeAttr2[] = new Attribute[1];
        smokeAttr2[0] = new Attribute();
        smokeAttr2[0].setName("area");
        smokeAttr2[0].setValType(GEOMETRY);
        try {
            smokeAttr2[0].setGeometryVal(rdr.read("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt smokePubPkt2= new PubPkt(EVENT_SMOKE, smokeAttr2, 1);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Smoke event
        // Area attribute
        Attribute smokeAttr3[] = new Attribute[1];
        smokeAttr2[0] = new Attribute();
        smokeAttr2[0].setName("area");
        smokeAttr2[0].setValType(GEOMETRY);
        try {
            smokeAttr2[0].setGeometryVal(rdr.read("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt smokePubPkt3= new PubPkt(EVENT_SMOKE, smokeAttr2, 1);


        ArrayList<PubPkt> pubPkts = new ArrayList<>();



        ArrayList<byte[]> pubPktsm = new ArrayList<>();
        //pubPktsm.add(Marshaller.marshal(smokePubPkt));
        pubPktsm.add(Marshaller.marshal(tempPubPkt));
        pubPktsm.add(Marshaller.marshal(tempPubPkt2));
        pubPktsm.add(Marshaller.marshal(smokePubPkt));
        pubPktsm.add(Marshaller.marshal(tempPubPkt3));
        pubPktsm.add(Marshaller.marshal(smokePubPkt3));
        pubPktsm.add(Marshaller.marshal(tempPubPkt4));
        pubPktsm.add(Marshaller.marshal(smokePubPkt2));

        pubPkts.add(smokePubPkt);
        pubPkts.add(tempPubPkt);
        pubPkts.add(tempPubPkt2);
        pubPkts.add(smokePubPkt);
        pubPkts.add(tempPubPkt3);
        pubPkts.add(tempPubPkt4);
        pubPkts.add(smokePubPkt2);

        return pubPktsm;
    }
}
