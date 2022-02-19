package trex.examples;


import com.vividsolutions.jts.io.WKTReader;
import trex.common.*;
import trex.marshalling.Marshaller;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;

import java.io.IOException;
import java.util.ArrayList;

import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.CompKind.FIRST_WITHIN;
import static trex.common.Consts.ConstraintOp.*;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.*;

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
public class RuleR4 {
    private static int EVENT_O3 = 10;
    private static int EVENT_NO2 = 11;
    private static int EVENT_PM2_5 = 12;
    private static int EVENT_AIR_QUALITY = 13;
    public RulePkt buildRule() {
        RulePkt rule= new RulePkt(false);

        int indexPredO3  = 0;
        int indexPredNO2 = 1;
        int indexPredPM2_5 = 2;

        int indexPredO3Value = 0;
        int indexPredO3Area = 1;

        int indexPredNO2Value = 0;
        int indexPredNO2Area = 1;

        int indexPredPM2_5Value = 0;
        int indexPredPM2_5Area = 1;

        WKTReader rdr = new WKTReader();

        Long fiveMin = 1000L*60L*5L;


        // O3 predicate
        // Area constraint
        Constraint o3Constr[] = new Constraint[2];
        o3Constr[0] = new Constraint();
        o3Constr[1] = new Constraint();
        o3Constr[0].setName("area");
        o3Constr[0].setValType(GEOMETRY);
        o3Constr[0].setOp(CONTAINS);
        try {
            o3Constr[0].setGeometryVal(rdr.read("POLYGON ((-10000 -10000, -10000 10000, 10000 10000, 10000 -10000, -10000 -10000))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        o3Constr[1].setName("value");
        o3Constr[1].setValType(INT);
        o3Constr[1].setOp(GT);
        o3Constr[1].setIntVal(120);
        rule.addRootPredicate(EVENT_O3, o3Constr, 2);


        // NO2 predicate
        // Constraint: NO2.value > 100
        Constraint no2Constr[] = new Constraint[1];
        no2Constr[0] = new Constraint();
        no2Constr[0].setName("value");
        no2Constr[0].setValType(INT);
        no2Constr[0].setOp(GT);
        no2Constr[0].setIntVal(100);
        rule.addPredicate(EVENT_NO2, no2Constr, 1, indexPredO3, fiveMin, EACH_WITHIN);
        //rule.addPredicate(EVENT_NO2, no2Constr, 1, indexPredO3, fiveMin, FIRST_WITHIN);



        // PM2_5 Event
        // Constraint: PM2_5.value > 30
        Constraint pm2_5Constr[] = new Constraint[1];
        pm2_5Constr[0] = new Constraint();
        pm2_5Constr[0].setName("value");
        pm2_5Constr[0].setValType(INT);
        pm2_5Constr[0].setOp(GT);
        pm2_5Constr[0].setIntVal(30);

        //rule.addPredicate(EVENT_PM2_5, pm2_5Constr, 1, indexPredNO2, fiveMin, EACH_WITHIN);
        rule.addPredicate(EVENT_PM2_5, pm2_5Constr, 1, indexPredNO2, fiveMin, FIRST_WITHIN);


        // Parameter: O3.area INTERSECT NO2.area INTERSECT PM2_5.area
        OpTree areaOpTreeO3= new OpTree(new RulePktValueReference(0, STATE, "area"), GEOMETRY);
        OpTree areaOpTreeNO2= new OpTree(new RulePktValueReference(1, STATE, "area"), GEOMETRY);
        OpTree areaOpTreePM2_5 = new OpTree(new RulePktValueReference(2, STATE, "area"), GEOMETRY);

        OpTree O3_NO2_AreaOpTree = new OpTree(areaOpTreeO3,areaOpTreeNO2, Consts.Op.INTERSECT,GEOMETRY);

        rule.addComplexParameter(INTERSECT, GEOMETRY ,O3_NO2_AreaOpTree, areaOpTreePM2_5);

        //rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeO3 ,areaOpTreeNO2);
        //rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeNO2, areaOpTreePM2_5);

        // AirQuality template
        EventTemplate airQualityTemplate= new EventTemplate(EVENT_AIR_QUALITY);

        // MeasuredO3 attribute in template
        OpTree measuredO3OpTree= new OpTree(new RulePktValueReference(0, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredO3", measuredO3OpTree);


        // MeasuredNO2 attribute in template
        OpTree measuredNO2OpTree= new OpTree(new RulePktValueReference(1, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredNO2", measuredNO2OpTree);


        // MeasuredPM2_5 attribute in template
        OpTree measuredPM2_5OpTree= new OpTree(new RulePktValueReference(2, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredPM2_5", measuredPM2_5OpTree);

        // Area attribute in template
        //OpTree areaOpTree = new OpTree(new RulePktValueReference(1, STATE, "area"), GEOMETRY);
        // Complex location
        OpTree locationTree= new OpTree(new RulePktValueReference(0, STATE, "complex"), STRING);
        airQualityTemplate.addAttribute("complex", locationTree);


        OpTree areaOpTreeJoin= new OpTree(areaOpTreeO3, areaOpTreeNO2, Consts.Op.INTERSECT, GEOMETRY);
        //areaOpTreeJoin.changeValueReference(new RulePktValueReference(3));
        OpTree areaOpTree= new OpTree(areaOpTreeJoin, areaOpTreePM2_5, Consts.Op.INTERSECT, GEOMETRY);
        //areaOpTree.changeValueReference(new RulePktValueReference(4));

        airQualityTemplate.addAttribute("area", areaOpTree);








        rule.setEventTemplate(airQualityTemplate);

        return rule;
    }







    public RulePkt buildRule2() {
        RulePkt rule= new RulePkt(false);

        int indexPredO3  = 0;
        int indexPredNO2 = 1;
        int indexPredPM2_5 = 2;

        int indexPredO3Value = 0;
        int indexPredO3Area = 1;

        int indexPredNO2Value = 0;
        int indexPredNO2Area = 1;

        int indexPredPM2_5Value = 0;
        int indexPredPM2_5Area = 1;

        WKTReader rdr = new WKTReader();

        Long fiveMin = 1000L*60L*5L;


        // O3 predicate
        // Area constraint
        Constraint o3Constr[] = new Constraint[1];
        o3Constr[0] = new Constraint();
        o3Constr[0].setName("value");
        o3Constr[0].setValType(INT);
        o3Constr[0].setOp(GT);
        o3Constr[0].setIntVal(120);
        rule.addRootPredicate(EVENT_O3, o3Constr, 1);


        // NO2 predicate
        // Constraint: NO2.value > 100
        Constraint no2Constr[] = new Constraint[1];
        no2Constr[0] = new Constraint();
        no2Constr[0].setName("value");
        no2Constr[0].setValType(INT);
        no2Constr[0].setOp(GT);
        no2Constr[0].setIntVal(100);
        rule.addPredicate(EVENT_NO2, no2Constr, 1, indexPredO3, fiveMin, /*EACH_WITHIN*/ FIRST_WITHIN);



        // PM2_5 Event
        // Constraint: PM2_5.value > 30
        Constraint pm2_5Constr[] = new Constraint[1];
        pm2_5Constr[0] = new Constraint();
        pm2_5Constr[0].setName("value");
        pm2_5Constr[0].setValType(INT);
        pm2_5Constr[0].setOp(GT);
        pm2_5Constr[0].setIntVal(30);

        rule.addPredicate(EVENT_PM2_5, pm2_5Constr, 1, indexPredNO2 , fiveMin, /*EACH_WITHIN*/ FIRST_WITHIN);


        // Parameter: O3.area INTERSECT NO2.area INTERSECT PM2_5.area
//        OpTree areaOpTreeO3= new OpTree(new RulePktValueReference(0, STATE, "area"), GEOMETRY);
//        OpTree areaOpTreeNO2= new OpTree(new RulePktValueReference(1, STATE, "area"), GEOMETRY);
//        OpTree areaOpTreePM2_5 = new OpTree(new RulePktValueReference(2, STATE, "area"), GEOMETRY);
//
//        OpTree O3_NO2_AreaOpTree = new OpTree(areaOpTreeO3,areaOpTreeNO2, Consts.Op.INTERSECT,GEOMETRY);
//
//        rule.addComplexParameter(INTERSECT, GEOMETRY ,O3_NO2_AreaOpTree, areaOpTreePM2_5);

        //rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeO3 ,areaOpTreeNO2);
        //rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeNO2, areaOpTreePM2_5);

        // AirQuality template
        EventTemplate airQualityTemplate= new EventTemplate(EVENT_AIR_QUALITY);

        // MeasuredO3 attribute in template
        OpTree measuredO3OpTree= new OpTree(new RulePktValueReference(0, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredO3", measuredO3OpTree);


        // MeasuredNO2 attribute in template
        OpTree measuredNO2OpTree= new OpTree(new RulePktValueReference(1, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredNO2", measuredNO2OpTree);


        // MeasuredPM2_5 attribute in template
        OpTree measuredPM2_5OpTree= new OpTree(new RulePktValueReference(2, STATE, "value"), INT);
        airQualityTemplate.addAttribute("measuredPM2_5", measuredPM2_5OpTree);

        // Area attribute in template
        //OpTree areaOpTree = new OpTree(new RulePktValueReference(1, STATE, "area"), GEOMETRY);
        // Complex location
        OpTree locationTree= new OpTree(new RulePktValueReference(0, STATE, "complex"), STRING);
        airQualityTemplate.addAttribute("complex", locationTree);


//        OpTree areaOpTreeJoin= new OpTree(areaOpTreeO3, areaOpTreeNO2, Consts.Op.INTERSECT, GEOMETRY);
//        //areaOpTreeJoin.changeValueReference(new RulePktValueReference(3));
//        OpTree areaOpTree= new OpTree(areaOpTreeJoin, areaOpTreePM2_5, Consts.Op.INTERSECT, GEOMETRY);
//        //areaOpTree.changeValueReference(new RulePktValueReference(4));
//
//        airQualityTemplate.addAttribute("area", areaOpTree);








        rule.setEventTemplate(airQualityTemplate);

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

        return new SubPkt(EVENT_AIR_QUALITY, constr, 1);
    }

    public ArrayList<byte[]> buildPublication() throws IOException {
        WKTReader rdr = new WKTReader();

        // O3 event
        Attribute o3Attr[] = new Attribute[2];
        o3Attr[0] = new Attribute();
        o3Attr[1] = new Attribute();
        // Area attribute
        o3Attr[0].setName("area");
        o3Attr[0].setValType(GEOMETRY);
        try {
            o3Attr[0].setGeometryVal(rdr.read("POLYGON ((70 70, 80 80, 80 60, 70 60, 70 70))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
                // Value attribute
        o3Attr[1].setName("value");
        o3Attr[1].setValType(INT);
        o3Attr[1].setIntVal(121);
        PubPkt o3PubPkt= new PubPkt(EVENT_O3, o3Attr, 2);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // NO2 event
        Attribute no2Attr[] = new Attribute[2];
        no2Attr[0] = new Attribute();
        no2Attr[1] = new Attribute();
        // Value attribute
        no2Attr[0].setName("value");
        no2Attr[0].setValType(INT);
        no2Attr[0].setIntVal(101);
        // Area attribute
        no2Attr[1].setName("area");
        no2Attr[1].setValType(GEOMETRY);
        try {
            no2Attr[1].setGeometryVal(rdr.read("POLYGON ((70 70, 90 70, 90 80, 80 80, 70 70))"));
            //no2Attr[1].setGeometryVal(rdr.read("POLYGON ((500 500, 700 500, 700 600, 600 600, 500 500))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt no2PubPkt= new PubPkt(EVENT_NO2, no2Attr, 2);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // PM2_5 event
        Attribute PM2_5Attr[] = new Attribute[3];
        PM2_5Attr[0] = new Attribute();
        PM2_5Attr[1] = new Attribute();
        // Value attribute
        PM2_5Attr[0].setName("value");
        PM2_5Attr[0].setValType(INT);
        PM2_5Attr[0].setIntVal(50);
        PM2_5Attr[2] = new Attribute();
        // Value attribute
        PM2_5Attr[1].setName("value");
        PM2_5Attr[1].setValType(INT);
        PM2_5Attr[1].setIntVal(50);
        // Area attribute
        PM2_5Attr[2].setName("area");
        PM2_5Attr[2].setValType(GEOMETRY);
        try {
            PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((70 75, 90 70, 90 80, 80 80, 70 75))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt= new PubPkt(EVENT_PM2_5, PM2_5Attr, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ArrayList<PubPkt> pubPkts = new ArrayList<>();
        ArrayList<byte[]> pubPktsm = new ArrayList<>();
        pubPktsm.add(Marshaller.marshal(o3PubPkt));
        pubPktsm.add(Marshaller.marshal(no2PubPkt));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt));

        pubPkts.add(o3PubPkt);
        pubPkts.add(no2PubPkt);
        pubPkts.add(pm2_5PubPkt);


        return pubPktsm;
    }



    public ArrayList<byte[]> buildPublication2() throws IOException {
        WKTReader rdr = new WKTReader();

        // O3 event
        Attribute o3Attr1[] = new Attribute[3];
        o3Attr1[0] = new Attribute();
        o3Attr1[1] = new Attribute();
        o3Attr1[2] = new Attribute();
        // Area attribute
        o3Attr1[0].setName("area");
        o3Attr1[0].setValType(GEOMETRY);
        try {
            o3Attr1[0].setGeometryVal(rdr.read("POLYGON ((146.97121841173472 506.7974499156785, 146.01048243189626 516.5519660164849, 143.16519503729907 525.931621533933, 138.544699026862 534.5759615666586, 132.3265574710621 542.1527889750059, 124.74973006271483 548.3709305308058, 116.10539002998922 552.9914265412428, 106.72573451254114 555.8367139358401, 96.97121841173472 556.7974499156785, 87.21670231092831 555.8367139358401, 77.83704679348024 552.9914265412428, 69.19270676075463 548.3709305308058, 61.615879352407354 542.1527889750059, 55.39773779660746 534.5759615666586, 50.777241786170386 525.931621533933, 47.9319543915732 516.5519660164849, 46.971218411734725 506.7974499156785, 47.9319543915732 497.0429338148721, 50.777241786170386 487.66327829742403, 55.39773779660745 479.01893826469836, 61.61587935240734 471.4421108563511, 69.19270676075462 465.22396930055123, 77.83704679348021 460.60347329011415, 87.2167023109283 457.75818589551693, 96.97121841173471 456.7974499156785, 106.72573451254114 457.75818589551693, 116.10539002998922 460.60347329011415, 124.74973006271482 465.2239693005512, 132.3265574710621 471.44211085635106, 138.544699026862 479.01893826469836, 143.16519503729904 487.663278297424, 146.01048243189624 497.0429338148721, 146.97121841173472 506.7974499156785))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Value attribute
        o3Attr1[1].setName("value");
        o3Attr1[1].setValType(INT);
        o3Attr1[1].setIntVal(121);
        // Value attribute
        o3Attr1[2].setName("complex");
        o3Attr1[2].setValType(STRING);
        o3Attr1[2].setStringVal("Air Pollution 1-1607336455006-POLYGON ((146.97121841173472 506.7974499156785, 146.01048243189626 516.5519660164849, 143.16519503729907 525.931621533933, 138.544699026862 534.5759615666586, 132.3265574710621 542.1527889750059, 124.74973006271483 548.3709305308058, 116.10539002998922 552.9914265412428, 106.72573451254114 555.8367139358401, 96.97121841173472 556.7974499156785, 87.21670231092831 555.8367139358401, 77.83704679348024 552.9914265412428, 69.19270676075463 548.3709305308058, 61.615879352407354 542.1527889750059, 55.39773779660746 534.5759615666586, 50.777241786170386 525.931621533933, 47.9319543915732 516.5519660164849, 46.971218411734725 506.7974499156785, 47.9319543915732 497.0429338148721, 50.777241786170386 487.66327829742403, 55.39773779660745 479.01893826469836, 61.61587935240734 471.4421108563511, 69.19270676075462 465.22396930055123, 77.83704679348021 460.60347329011415, 87.2167023109283 457.75818589551693, 96.97121841173471 456.7974499156785, 106.72573451254114 457.75818589551693, 116.10539002998922 460.60347329011415, 124.74973006271482 465.2239693005512, 132.3265574710621 471.44211085635106, 138.544699026862 479.01893826469836, 143.16519503729904 487.663278297424, 146.01048243189624 497.0429338148721, 146.97121841173472 506.7974499156785))");
        PubPkt o3PubPkt1= new PubPkt(EVENT_O3, o3Attr1, 3);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // O3 event
        Attribute o3Attr2[] = new Attribute[3];
        o3Attr2[0] = new Attribute();
        o3Attr2[1] = new Attribute();
        o3Attr2[2] = new Attribute();
        // Area attribute
        o3Attr2[0].setName("area");
        o3Attr2[0].setValType(GEOMETRY);
        try {
            o3Attr2[0].setGeometryVal(rdr.read("POLYGON ((149.8085883901153 484.8513696174309, 148.8478524102768 494.60588571823735, 146.00256501567964 503.9855412356854, 141.38206900524256 512.629881268411, 135.16392744944267 520.2067086767582, 127.5871000410954 526.4248502325581, 118.94276000836977 531.0453462429953, 109.5631044909217 533.8906336375925, 99.80858839011529 534.8513696174309, 90.05407228930888 533.8906336375925, 80.6744167718608 531.0453462429953, 72.0300767391352 526.4248502325581, 64.45324933078791 520.2067086767584, 58.23510777498803 512.629881268411, 53.61461176455096 503.9855412356854, 50.76932436995377 494.60588571823735, 49.8085883901153 484.8513696174309, 50.76932436995377 475.0968535166245, 53.61461176455095 465.71719799917645, 58.23510777498802 457.07285796645084, 64.45324933078791 449.49603055810354, 72.03007673913518 443.27788900230365, 80.67441677186078 438.6573929918666, 90.05407228930886 435.8121055972694, 99.80858839011528 434.8513696174309, 109.5631044909217 435.8121055972694, 118.94276000836979 438.6573929918666, 127.58710004109538 443.27788900230365, 135.16392744944267 449.49603055810354, 141.38206900524256 457.0728579664508, 146.0025650156796 465.7171979991764, 148.8478524102768 475.0968535166245, 149.8085883901153 484.8513696174309))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Value attribute
        o3Attr2[1].setName("value");
        o3Attr2[1].setValType(INT);
        o3Attr2[1].setIntVal(121);
        // Value attribute
        o3Attr2[2].setName("complex");
        o3Attr2[2].setValType(STRING);
        o3Attr2[2].setStringVal("Air Pollution 2-1607336455006-POLYGON ((149.8085883901153 484.8513696174309, 148.8478524102768 494.60588571823735, 146.00256501567964 503.9855412356854, 141.38206900524256 512.629881268411, 135.16392744944267 520.2067086767582, 127.5871000410954 526.4248502325581, 118.94276000836977 531.0453462429953, 109.5631044909217 533.8906336375925, 99.80858839011529 534.8513696174309, 90.05407228930888 533.8906336375925, 80.6744167718608 531.0453462429953, 72.0300767391352 526.4248502325581, 64.45324933078791 520.2067086767584, 58.23510777498803 512.629881268411, 53.61461176455096 503.9855412356854, 50.76932436995377 494.60588571823735, 49.8085883901153 484.8513696174309, 50.76932436995377 475.0968535166245, 53.61461176455095 465.71719799917645, 58.23510777498802 457.07285796645084, 64.45324933078791 449.49603055810354, 72.03007673913518 443.27788900230365, 80.67441677186078 438.6573929918666, 90.05407228930886 435.8121055972694, 99.80858839011528 434.8513696174309, 109.5631044909217 435.8121055972694, 118.94276000836979 438.6573929918666, 127.58710004109538 443.27788900230365, 135.16392744944267 449.49603055810354, 141.38206900524256 457.0728579664508, 146.0025650156796 465.7171979991764, 148.8478524102768 475.0968535166245, 149.8085883901153 484.8513696174309))");
        PubPkt o3PubPkt2= new PubPkt(EVENT_O3, o3Attr2, 3);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // O3 event
        Attribute o3Attr3[] = new Attribute[3];
        o3Attr3[0] = new Attribute();
        o3Attr3[1] = new Attribute();
        o3Attr3[2] = new Attribute();
        // Area attribute
        o3Attr3[0].setName("area");
        o3Attr3[0].setValType(GEOMETRY);
        try {
            o3Attr3[0].setGeometryVal(rdr.read("POLYGON ((153.08256561326934 380.5141278138384, 152.12182963343088 390.2686439146448, 149.27654223883368 399.64829943209287, 144.6560462283966 408.29263946481854, 138.43790467259672 415.8694668731658, 130.86107726424945 422.08760842896567, 122.21673723152384 426.70810443940275, 112.83708171407575 429.5533918339999, 103.08256561326934 430.5141278138384, 93.32804951246293 429.5533918339999, 83.94839399501485 426.70810443940275, 75.30405396228925 422.08760842896567, 67.72722655394196 415.8694668731658, 61.50908499814207 408.29263946481854, 56.888588987705 399.64829943209287, 54.04330159310781 390.2686439146448, 53.08256561326934 380.5141278138384, 54.04330159310781 370.759611713032, 56.888588987705 361.37995619558393, 61.509084998142065 352.7356161628583, 67.72722655394196 345.158788754511, 75.30405396228923 338.94064719871113, 83.94839399501483 334.32015118827405, 93.32804951246291 331.4748637936769, 103.08256561326932 330.5141278138384, 112.83708171407575 331.4748637936769, 122.21673723152384 334.32015118827405, 130.86107726424945 338.94064719871113, 138.43790467259672 345.158788754511, 144.6560462283966 352.73561616285826, 149.27654223883366 361.3799561955839, 152.12182963343085 370.759611713032, 153.08256561326934 380.5141278138384))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Value attribute
        o3Attr3[1].setName("value");
        o3Attr3[1].setValType(INT);
        o3Attr3[1].setIntVal(121);
        // Value attribute
        o3Attr3[2].setName("complex");
        o3Attr3[2].setValType(STRING);
        o3Attr3[2].setStringVal("Air Pollution 3-1607336455006-POLYGON ((153.08256561326934 380.5141278138384, 152.12182963343088 390.2686439146448, 149.27654223883368 399.64829943209287, 144.6560462283966 408.29263946481854, 138.43790467259672 415.8694668731658, 130.86107726424945 422.08760842896567, 122.21673723152384 426.70810443940275, 112.83708171407575 429.5533918339999, 103.08256561326934 430.5141278138384, 93.32804951246293 429.5533918339999, 83.94839399501485 426.70810443940275, 75.30405396228925 422.08760842896567, 67.72722655394196 415.8694668731658, 61.50908499814207 408.29263946481854, 56.888588987705 399.64829943209287, 54.04330159310781 390.2686439146448, 53.08256561326934 380.5141278138384, 54.04330159310781 370.759611713032, 56.888588987705 361.37995619558393, 61.509084998142065 352.7356161628583, 67.72722655394196 345.158788754511, 75.30405396228923 338.94064719871113, 83.94839399501483 334.32015118827405, 93.32804951246291 331.4748637936769, 103.08256561326932 330.5141278138384, 112.83708171407575 331.4748637936769, 122.21673723152384 334.32015118827405, 130.86107726424945 338.94064719871113, 138.43790467259672 345.158788754511, 144.6560462283966 352.73561616285826, 149.27654223883366 361.3799561955839, 152.12182963343085 370.759611713032, 153.08256561326934 380.5141278138384))");
        PubPkt o3PubPkt3 = new PubPkt(EVENT_O3, o3Attr3, 3);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // O3 event
        Attribute o3Attr4[] = new Attribute[3];
        o3Attr4[0] = new Attribute();
        o3Attr4[1] = new Attribute();
        o3Attr4[2] = new Attribute();
        // Area attribute
        o3Attr4[0].setName("area");
        o3Attr4[0].setValType(GEOMETRY);
        try {
            o3Attr4[0].setGeometryVal(rdr.read("POLYGON ((207.29691635149422 461.8793079738841, 206.33618037165576 471.63382407469055, 203.49089297705856 481.0134795921386, 198.87039696662148 489.65781962486426, 192.6522554108216 497.2346470332115, 185.07542800247433 503.4527885890114, 176.43108796974872 508.07328459944847, 167.05143245230065 510.91857199404564, 157.29691635149422 511.8793079738841, 147.54240025068782 510.91857199404564, 138.16274473323972 508.07328459944847, 129.5184047005141 503.4527885890114, 121.94157729216684 497.2346470332115, 115.72343573636695 489.65781962486426, 111.10293972592987 481.0134795921386, 108.2576523313327 471.63382407469055, 107.29691635149422 461.8793079738841, 108.2576523313327 452.1247918730777, 111.10293972592987 442.74513635562965, 115.72343573636695 434.10079632290405, 121.94157729216684 426.52396891455675, 129.5184047005141 420.30582735875686, 138.1627447332397 415.6853313483198, 147.5424002506878 412.8400439537226, 157.29691635149422 411.8793079738841, 167.05143245230065 412.8400439537226, 176.43108796974872 415.6853313483198, 185.07542800247433 420.30582735875686, 192.6522554108216 426.52396891455675, 198.87039696662148 434.100796322904, 203.49089297705854 442.7451363556296, 206.33618037165573 452.1247918730777, 207.29691635149422 461.8793079738841))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Value attribute
        o3Attr4[1].setName("value");
        o3Attr4[1].setValType(INT);
        o3Attr4[1].setIntVal(121);
        // Value attribute
        o3Attr4[2].setName("complex");
        o3Attr4[2].setValType(STRING);
        o3Attr4[2].setStringVal("Air Pollution 4-1607336455006-POLYGON ((207.29691635149422 461.8793079738841, 206.33618037165576 471.63382407469055, 203.49089297705856 481.0134795921386, 198.87039696662148 489.65781962486426, 192.6522554108216 497.2346470332115, 185.07542800247433 503.4527885890114, 176.43108796974872 508.07328459944847, 167.05143245230065 510.91857199404564, 157.29691635149422 511.8793079738841, 147.54240025068782 510.91857199404564, 138.16274473323972 508.07328459944847, 129.5184047005141 503.4527885890114, 121.94157729216684 497.2346470332115, 115.72343573636695 489.65781962486426, 111.10293972592987 481.0134795921386, 108.2576523313327 471.63382407469055, 107.29691635149422 461.8793079738841, 108.2576523313327 452.1247918730777, 111.10293972592987 442.74513635562965, 115.72343573636695 434.10079632290405, 121.94157729216684 426.52396891455675, 129.5184047005141 420.30582735875686, 138.1627447332397 415.6853313483198, 147.5424002506878 412.8400439537226, 157.29691635149422 411.8793079738841, 167.05143245230065 412.8400439537226, 176.43108796974872 415.6853313483198, 185.07542800247433 420.30582735875686, 192.6522554108216 426.52396891455675, 198.87039696662148 434.100796322904, 203.49089297705854 442.7451363556296, 206.33618037165573 452.1247918730777, 207.29691635149422 461.8793079738841))");
        PubPkt o3PubPkt4 = new PubPkt(EVENT_O3, o3Attr4, 3);
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // NO2 event
        Attribute no2Attr1[] = new Attribute[2];
        no2Attr1[0] = new Attribute();
        no2Attr1[1] = new Attribute();
        // Value attribute
        no2Attr1[0].setName("value");
        no2Attr1[0].setValType(INT);
        no2Attr1[0].setIntVal(101);
        // Area attribute
        no2Attr1[1].setName("area");
        no2Attr1[1].setValType(GEOMETRY);
        try {
            no2Attr1[1].setGeometryVal(rdr.read("POLYGON ((198.5014945043398 359.8167644381945, 197.5407585245013 369.57128053900095, 194.69547112990415 378.950936056449, 190.07497511946707 387.5952760891746, 183.85683356366718 395.1721034975219, 176.2800061553199 401.3902450533218, 167.6356661225943 406.01074106375887, 158.25601060514623 408.85602845835604, 148.5014945043398 409.8167644381945, 138.7469784035334 408.85602845835604, 129.3673228860853 406.01074106375887, 120.72298285335971 401.3902450533218, 113.14615544501243 395.1721034975219, 106.92801388921254 387.5952760891746, 102.30751787877546 378.950936056449, 99.46223048417828 369.57128053900095, 98.5014945043398 359.8167644381945, 99.46223048417828 350.0622483373881, 102.30751787877546 340.68259281994006, 106.92801388921254 332.03825278721445, 113.14615544501243 324.46142537886715, 120.7229828533597 318.24328382306726, 129.36732288608528 313.6227878126302, 138.74697840353338 310.777500418033, 148.5014945043398 309.8167644381945, 158.25601060514623 310.777500418033, 167.6356661225943 313.6227878126302, 176.28000615531988 318.24328382306726, 183.85683356366718 324.46142537886715, 190.07497511946707 332.03825278721445, 194.69547112990412 340.68259281994, 197.5407585245013 350.0622483373881, 198.5014945043398 359.8167644381945))"));
            //no2Attr[1].setGeometryVal(rdr.read("POLYGON ((500 500, 700 500, 700 600, 600 600, 500 500))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt no2PubPkt1 = new PubPkt(EVENT_NO2, no2Attr1, 2);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // NO2 event
        Attribute no2Attr2[] = new Attribute[2];
        no2Attr2[0] = new Attribute();
        no2Attr2[1] = new Attribute();
        // Value attribute
        no2Attr2[0].setName("value");
        no2Attr2[0].setValType(INT);
        no2Attr2[0].setIntVal(101);
        // Area attribute
        no2Attr2[1].setName("area");
        no2Attr2[1].setValType(GEOMETRY);
        try {
            no2Attr2[1].setGeometryVal(rdr.read("POLYGON ((183.12031709513482 368.10296415793584, 182.15958111529636 377.85748025874227, 179.31429372069917 387.2371357761903, 174.6937977102621 395.881475808916, 168.4756561544622 403.4583032172632, 160.89882874611493 409.6764447730631, 152.25448871338932 414.2969407835002, 142.87483319594125 417.14222817809735, 133.12031709513482 418.10296415793584, 123.36580099432841 417.14222817809735, 113.98614547688034 414.2969407835002, 105.34180544415473 409.6764447730631, 97.76497803580745 403.4583032172632, 91.54683648000756 395.881475808916, 86.92634046957048 387.2371357761903, 84.0810530749733 377.85748025874227, 83.12031709513482 368.10296415793584, 84.0810530749733 358.3484480571294, 86.92634046957048 348.9687925396814, 91.54683648000756 340.32445250695577, 97.76497803580745 332.74762509860847, 105.34180544415472 326.5294835428086, 113.98614547688031 321.9089875323715, 123.3658009943284 319.06370013777433, 133.12031709513482 318.10296415793584, 142.87483319594125 319.06370013777433, 152.25448871338932 321.9089875323715, 160.89882874611493 326.5294835428086, 168.4756561544622 332.74762509860847, 174.6937977102621 340.3244525069557, 179.31429372069914 348.9687925396813, 182.15958111529633 358.3484480571294, 183.12031709513482 368.10296415793584))"));
            //no2Attr[1].setGeometryVal(rdr.read("POLYGON ((500 500, 700 500, 700 600, 600 600, 500 500))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt no2PubPkt2 = new PubPkt(EVENT_NO2, no2Attr2, 2);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // NO2 event
        Attribute no2Attr3[] = new Attribute[2];
        no2Attr3[0] = new Attribute();
        no2Attr3[1] = new Attribute();
        // Value attribute
        no2Attr3[0].setName("value");
        no2Attr3[0].setValType(INT);
        no2Attr3[0].setIntVal(101);
        // Area attribute
        no2Attr3[1].setName("area");
        no2Attr3[1].setValType(GEOMETRY);
        try {
            no2Attr3[1].setGeometryVal(rdr.read("POLYGON ((210.5292614132371 409.5349700325611, 209.56852543339863 419.2894861333675, 206.72323803880144 428.66914165081556, 202.10274202836436 437.3134816835412, 195.88460047256447 444.89030909188847, 188.3077730642172 451.10845064768836, 179.6634330314916 455.72894665812544, 170.28377751404352 458.5742340527226, 160.5292614132371 459.5349700325611, 150.7747453124307 458.5742340527226, 141.3950897949826 455.72894665812544, 132.750749762257 451.10845064768836, 125.17392235390972 444.89030909188847, 118.95578079810983 437.3134816835412, 114.33528478767275 428.66914165081556, 111.48999739307557 419.2894861333675, 110.5292614132371 409.5349700325611, 111.48999739307557 399.78045393175466, 114.33528478767275 390.4007984143066, 118.95578079810983 381.756458381581, 125.17392235390972 374.1796309732337, 132.750749762257 367.9614894174338, 141.39508979498257 363.34099340699674, 150.77474531243067 360.4957060123996, 160.5292614132371 359.5349700325611, 170.28377751404352 360.4957060123996, 179.6634330314916 363.34099340699674, 188.3077730642172 367.9614894174338, 195.88460047256447 374.1796309732337, 202.10274202836436 381.75645838158096, 206.7232380388014 390.40079841430656, 209.5685254333986 399.78045393175466, 210.5292614132371 409.5349700325611))"));
            //no2Attr[1].setGeometryVal(rdr.read("POLYGON ((500 500, 700 500, 700 600, 600 600, 500 500))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt no2PubPkt3 = new PubPkt(EVENT_NO2, no2Attr3, 2);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // PM2_5 event
        Attribute PM2_5Attr1[] = new Attribute[3];
        PM2_5Attr1[0] = new Attribute();
        PM2_5Attr1[1] = new Attribute();
        // Value attribute
        PM2_5Attr1[0].setName("value");
        PM2_5Attr1[0].setValType(INT);
        PM2_5Attr1[0].setIntVal(50);
        PM2_5Attr1[2] = new Attribute();
        // Value attribute
        PM2_5Attr1[1].setName("value");
        PM2_5Attr1[1].setValType(INT);
        PM2_5Attr1[1].setIntVal(50);
        // Area attribute
        PM2_5Attr1[2].setName("area");
        PM2_5Attr1[2].setValType(GEOMETRY);
        try {
            PM2_5Attr1[2].setGeometryVal(rdr.read("POLYGON ((183.20365230866048 417.6207490300231, 182.242916328822 427.3752651308295, 179.39762893422483 436.75492064827756, 174.77713292378775 445.39926068100317, 168.55899136798786 452.97608808935047, 160.9821639596406 459.19422964515036, 152.33782392691498 463.81472565558744, 142.9581684094669 466.6600130501846, 133.20365230866048 467.6207490300231, 123.44913620785407 466.6600130501846, 114.069480690406 463.81472565558744, 105.42514065768039 459.19422964515036, 97.8483132493331 452.97608808935047, 91.63017169353321 445.39926068100317, 87.00967568309613 436.75492064827756, 84.16438828849896 427.3752651308295, 83.20365230866048 417.6207490300231, 84.16438828849896 407.86623292921666, 87.00967568309613 398.4865774117686, 91.63017169353321 389.842237379043, 97.8483132493331 382.2654099706957, 105.42514065768037 376.0472684148958, 114.06948069040597 371.42677240445875, 123.44913620785405 368.5814850098616, 133.20365230866048 367.6207490300231, 142.9581684094669 368.5814850098616, 152.33782392691498 371.42677240445875, 160.98216395964056 376.0472684148958, 168.55899136798786 382.2654099706957, 174.77713292378775 389.842237379043, 179.3976289342248 398.48657741176856, 182.242916328822 407.86623292921666, 183.20365230866048 417.6207490300231))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt1= new PubPkt(EVENT_PM2_5, PM2_5Attr1, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // PM2_5 event
        Attribute PM2_5Attr2[] = new Attribute[3];
        PM2_5Attr2[0] = new Attribute();
        PM2_5Attr2[1] = new Attribute();
        // Value attribute
        PM2_5Attr2[0].setName("value");
        PM2_5Attr2[0].setValType(INT);
        PM2_5Attr2[0].setIntVal(50);
        PM2_5Attr2[2] = new Attribute();
        // Value attribute
        PM2_5Attr2[1].setName("value");
        PM2_5Attr2[1].setValType(INT);
        PM2_5Attr2[1].setIntVal(50);
        // Area attribute
        PM2_5Attr2[2].setName("area");
        PM2_5Attr2[2].setValType(GEOMETRY);
        try {
            PM2_5Attr2[2].setGeometryVal(rdr.read("POLYGON ((111.55241249924103 481.20061923866206, 110.59167651940257 490.9551353394685, 107.74638912480538 500.3347908569166, 103.1258931143683 508.9791308896422, 96.9077515585684 516.5559582979895, 89.33092415022115 522.7740998537894, 80.68658411749553 527.3945958642264, 71.30692860004746 530.2398832588236, 61.552412499241036 531.2006192386621, 51.79789639843463 530.2398832588236, 42.418240880986545 527.3945958642264, 33.773900848260936 522.7740998537894, 26.197073439913666 516.5559582979895, 19.97893188411377 508.9791308896422, 15.358435873676697 500.3347908569166, 12.513148479079511 490.9551353394685, 11.552412499241036 481.20061923866206, 12.513148479079511 471.44610313785563, 15.358435873676697 462.0664476204076, 19.978931884113763 453.4221075876819, 26.197073439913652 445.8452801793347, 33.77390084826093 439.6271386235348, 42.41824088098652 435.0066426130977, 51.7978963984346 432.1613552185005, 61.55241249924103 431.20061923866206, 71.30692860004746 432.1613552185005, 80.68658411749553 435.0066426130977, 89.33092415022114 439.62713862353473, 96.9077515585684 445.8452801793346, 103.1258931143683 453.4221075876819, 107.74638912480536 462.06644762040753, 110.59167651940255 471.44610313785563, 111.55241249924103 481.20061923866206))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt2= new PubPkt(EVENT_PM2_5, PM2_5Attr2, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // PM2_5 event
        Attribute PM2_5Attr3[] = new Attribute[3];
        PM2_5Attr3[0] = new Attribute();
        PM2_5Attr3[1] = new Attribute();
        // Value attribute
        PM2_5Attr3[0].setName("value");
        PM2_5Attr3[0].setValType(INT);
        PM2_5Attr3[0].setIntVal(50);
        PM2_5Attr3[2] = new Attribute();
        // Value attribute
        PM2_5Attr3[1].setName("value");
        PM2_5Attr3[1].setValType(INT);
        PM2_5Attr3[1].setIntVal(50);
        // Area attribute
        PM2_5Attr3[2].setName("area");
        PM2_5Attr3[2].setValType(GEOMETRY);
        try {
            PM2_5Attr3[2].setGeometryVal(rdr.read("POLYGON ((160.27695728265473 336.75716630552574, 159.31622130281625 346.51168240633217, 156.47093390821908 355.8913379237802, 151.850437897782 364.5356779565059, 145.6322963419821 372.1125053648531, 138.05546893363484 378.330646920653, 129.41112890090923 382.9511429310901, 120.03147338346115 385.79643032568725, 110.27695728265473 386.75716630552574, 100.52244118184832 385.79643032568725, 91.14278566440025 382.9511429310901, 82.49844563167464 378.330646920653, 74.92161822332736 372.1125053648531, 68.70347666752747 364.5356779565059, 64.08298065709039 355.8913379237802, 61.23769326249321 346.51168240633217, 60.276957282654735 336.75716630552574, 61.23769326249321 327.0026502047193, 64.08298065709039 317.6229946872713, 68.70347666752747 308.97865465454566, 74.92161822332736 301.40182724619837, 82.49844563167463 295.1836856903985, 91.14278566440022 290.5631896799614, 100.52244118184831 287.71790228536423, 110.27695728265472 286.75716630552574, 120.03147338346115 287.71790228536423, 129.41112890090923 290.5631896799614, 138.0554689336348 295.1836856903985, 145.6322963419821 301.40182724619837, 151.850437897782 308.9786546545456, 156.47093390821905 317.6229946872712, 159.31622130281625 327.0026502047193, 160.27695728265473 336.75716630552574))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt3 = new PubPkt(EVENT_PM2_5, PM2_5Attr3, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // PM2_5 event
        Attribute PM2_5Attr4[] = new Attribute[3];
        PM2_5Attr4[0] = new Attribute();
        PM2_5Attr4[1] = new Attribute();
        // Value attribute
        PM2_5Attr4[0].setName("value");
        PM2_5Attr4[0].setValType(INT);
        PM2_5Attr4[0].setIntVal(50);
        PM2_5Attr4[2] = new Attribute();
        // Value attribute
        PM2_5Attr4[1].setName("value");
        PM2_5Attr4[1].setValType(INT);
        PM2_5Attr4[1].setIntVal(50);
        // Area attribute
        PM2_5Attr4[2].setName("area");
        PM2_5Attr4[2].setValType(GEOMETRY);
        try {
            PM2_5Attr4[2].setGeometryVal(rdr.read("POLYGON ((140.5840720064931 439.8648689492014, 139.6233360266546 449.61938505000785, 136.77804863205745 458.9990405674559, 132.15755262162037 467.64338060018156, 125.93941106582048 475.2202080085288, 118.36258365747321 481.4383495643287, 109.7182436247476 486.05884557476577, 100.33858810729951 488.90413296936293, 90.5840720064931 489.8648689492014, 80.82955590568669 488.90413296936293, 71.44990038823862 486.05884557476577, 62.80556035551301 481.4383495643287, 55.22873294716573 475.2202080085288, 49.010591391365836 467.64338060018156, 44.39009538092876 458.9990405674559, 41.54480798633158 449.61938505000785, 40.5840720064931 439.8648689492014, 41.54480798633158 430.110352848395, 44.39009538092876 420.73069733094695, 49.01059139136583 412.08635729822134, 55.22873294716572 404.50952988987405, 62.805560355512995 398.29138833407416, 71.44990038823859 393.6708923236371, 80.82955590568668 390.8256049290399, 90.58407200649309 389.8648689492014, 100.33858810729951 390.8256049290399, 109.7182436247476 393.6708923236371, 118.3625836574732 398.29138833407416, 125.93941106582048 404.50952988987405, 132.15755262162037 412.0863572982213, 136.77804863205742 420.7306973309469, 139.6233360266546 430.110352848395, 140.5840720064931 439.8648689492014))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt4= new PubPkt(EVENT_PM2_5, PM2_5Attr4, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // PM2_5 event
        Attribute PM2_5Attr5[] = new Attribute[3];
        PM2_5Attr5[0] = new Attribute();
        PM2_5Attr5[1] = new Attribute();
        // Value attribute
        PM2_5Attr5[0].setName("value");
        PM2_5Attr5[0].setValType(INT);
        PM2_5Attr5[0].setIntVal(50);
        PM2_5Attr5[2] = new Attribute();
        // Value attribute
        PM2_5Attr5[1].setName("value");
        PM2_5Attr5[1].setValType(INT);
        PM2_5Attr5[1].setIntVal(50);
        // Area attribute
        PM2_5Attr5[2].setName("area");
        PM2_5Attr5[2].setValType(GEOMETRY);
        try {
            PM2_5Attr5[2].setGeometryVal(rdr.read("POLYGON ((223.30863113959606 363.76097945443973, 222.34789515975757 373.51549555524616, 219.5026077651604 382.8951510726942, 214.88211175472333 391.53949110541987, 208.66397019892344 399.1163185137671, 201.08714279057617 405.334460069567, 192.44280275785056 409.9549560800041, 183.0631472404025 412.80024347460125, 173.30863113959606 413.76097945443973, 163.55411503878966 412.80024347460125, 154.17445952134156 409.9549560800041, 145.53011948861595 405.334460069567, 137.95329208026868 399.1163185137671, 131.7351505244688 391.53949110541987, 127.11465451403171 382.8951510726942, 124.26936711943453 373.51549555524616, 123.30863113959606 363.76097945443973, 124.26936711943453 354.0064633536333, 127.11465451403171 344.62680783618526, 131.7351505244688 335.98246780345966, 137.95329208026868 328.40564039511236, 145.53011948861595 322.18749883931247, 154.17445952134153 317.5670028288754, 163.55411503878963 314.7217154342782, 173.30863113959606 313.76097945443973, 183.0631472404025 314.7217154342782, 192.44280275785056 317.5670028288754, 201.08714279057614 322.18749883931247, 208.66397019892344 328.40564039511236, 214.88211175472333 335.9824678034596, 219.50260776516038 344.6268078361852, 222.34789515975757 354.0064633536333, 223.30863113959606 363.76097945443973))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt5 = new PubPkt(EVENT_PM2_5, PM2_5Attr5, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // PM2_5 event
        Attribute PM2_5Attr6[] = new Attribute[3];
        PM2_5Attr6[0] = new Attribute();
        PM2_5Attr6[1] = new Attribute();
        // Value attribute
        PM2_5Attr6[0].setName("value");
        PM2_5Attr6[0].setValType(INT);
        PM2_5Attr6[0].setIntVal(50);
        PM2_5Attr6[2] = new Attribute();
        // Value attribute
        PM2_5Attr6[1].setName("value");
        PM2_5Attr6[1].setValType(INT);
        PM2_5Attr6[1].setIntVal(50);
        // Area attribute
        PM2_5Attr6[2].setName("area");
        PM2_5Attr6[2].setValType(GEOMETRY);
        try {
            PM2_5Attr6[2].setGeometryVal(rdr.read("POLYGON ((83.59908117537447 459.7353820721635, 82.63834519553599 469.4898981729699, 79.79305780093881 478.86955369041794, 75.17256179050173 487.5138937231436, 68.95442023470184 495.09072113149085, 61.37759282635458 501.30886268729074, 52.73325279362896 505.9293586977278, 43.35359727618088 508.774646092325, 33.599081175374465 509.7353820721635, 23.844565074568056 508.774646092325, 14.464909557119977 505.9293586977278, 5.820569524394369 501.30886268729074, -1.7562578839529053 495.09072113149085, -7.974399439752801 487.5138937231436, -12.594895450189874 478.86955369041794, -15.44018284478706 469.4898981729699, -16.400918824625535 459.7353820721635, -15.44018284478706 449.98086597135705, -12.594895450189874 440.601210453909, -7.974399439752808 431.9568704211834, -1.7562578839529195 424.3800430128361, 5.820569524394358 418.1619014570362, 14.464909557119949 413.54140544659913, 23.84456507456803 410.69611805200196, 33.59908117537446 409.7353820721635, 43.35359727618088 410.69611805200196, 52.733252793628964 413.54140544659913, 61.37759282635456 418.1619014570362, 68.95442023470184 424.3800430128361, 75.17256179050173 431.95687042118334, 79.79305780093878 440.60121045390895, 82.63834519553598 449.98086597135705, 83.59908117537447 459.7353820721635))"));
            //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PubPkt pm2_5PubPkt6 = new PubPkt(EVENT_PM2_5, PM2_5Attr6, 3);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ArrayList<PubPkt> pubPkts = new ArrayList<>();
        ArrayList<byte[]> pubPktsm = new ArrayList<>();

        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt1));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt2));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt3));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt4));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt5));
        pubPktsm.add(Marshaller.marshal(pm2_5PubPkt6));


        pubPktsm.add(Marshaller.marshal(no2PubPkt1));
        pubPktsm.add(Marshaller.marshal(no2PubPkt2));
        pubPktsm.add(Marshaller.marshal(no2PubPkt3));



        pubPktsm.add(Marshaller.marshal(o3PubPkt1));
        pubPktsm.add(Marshaller.marshal(o3PubPkt2));
        pubPktsm.add(Marshaller.marshal(o3PubPkt3));
        pubPktsm.add(Marshaller.marshal(o3PubPkt4));






        pubPkts.add(o3PubPkt1);
        pubPkts.add(o3PubPkt2);
        pubPkts.add(o3PubPkt3);
        pubPkts.add(o3PubPkt4);



        pubPkts.add(no2PubPkt1);
        pubPkts.add(no2PubPkt2);
        pubPkts.add(no2PubPkt3);


        pubPkts.add(pm2_5PubPkt1);
        pubPkts.add(pm2_5PubPkt2);
        pubPkts.add(pm2_5PubPkt3);
        pubPkts.add(pm2_5PubPkt4);
        pubPkts.add(pm2_5PubPkt5);
        pubPkts.add(pm2_5PubPkt6);


        return pubPktsm;
    }
}
