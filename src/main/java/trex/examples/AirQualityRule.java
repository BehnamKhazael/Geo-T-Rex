package trex.examples;

import com.vividsolutions.jts.io.WKTReader;
import trex.common.*;
import trex.packets.RulePkt;

import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.ConstraintOp.INTERSECT;
import static trex.common.Consts.ConstraintOp.LT;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.GEOMETRY;
import static trex.common.Consts.ValType.INT;

/**
 * Created by sony on 12/1/2020.
 * Assign 1=> O3,2=> NO2,3=> PM25, AirQuality=>4
 * Define AirQualityIndex(area:Geometry,measuredO3:double,measuredNO2:double, measuredPM25:double)
 * From O3(area=>$a)
 * and Each NO2([Geometry]area INTERSECT $a,value>45)
 * within 300000 from O3
 * and Each PM25([Geometry]area INTERSECT $a,value>45)
 * within 300000 from NO2
 * Where area:=O3.area INTERSECTION NO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value;
 */
public class AirQualityRule {

    private static int EVENT_O3 = 1;
    private static int EVENT_NO2 = 2;
    private static int EVENT_PM2_5 = 3;
    private static int EVENT_AIR_QUALITY = 4;

    public RulePkt buildRule() {
        RulePkt rule= new RulePkt(false);

        int indexPredO3= 0;
        int indexPredNO2= 1;
        int indexPredPM2_5= 2;

        WKTReader rdr = new WKTReader();

        Long fiveMin = 1000L*60L*5L;
        Constraint fakeConstr[] = new Constraint[1];
        fakeConstr[0] = new Constraint();
        fakeConstr[0].setName("area");
        fakeConstr[0].setValType(GEOMETRY);
        fakeConstr[0].setOp(INTERSECT);
        try {
            fakeConstr[0].setGeometryVal(rdr.read("POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        rule.addRootPredicate(EVENT_O3, fakeConstr, 1);


        // Temp predicate
        // Constraint: Temp.value > 45
        Constraint tempConstr[] = new Constraint[1];
        tempConstr[0] = new Constraint();
        tempConstr[0].setName("value");
        tempConstr[0].setValType(INT);
        tempConstr[0].setOp(GT);
        tempConstr[0].setIntVal(1);
        rule.addPredicate(EVENT_NO2, tempConstr, 2, indexPredO3, fiveMin, EACH_WITHIN);

        // Parameter: Smoke.area=Temp.area
        OpTree areaOpTreeSmoke= new OpTree(new RulePktValueReference(indexPredO3, STATE, "area"), GEOMETRY);
        OpTree areaOpTreeTemp= new OpTree(new RulePktValueReference(indexPredNO2, STATE, "area"), GEOMETRY);
        rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeTemp ,areaOpTreeSmoke);

        // Fire template
        EventTemplate fireTemplate= new EventTemplate(EVENT_AIR_QUALITY);

        // Area attribute in template
        OpTree areaOpTree= new OpTree(areaOpTreeSmoke, areaOpTreeTemp, Consts.Op.INTERSECT, GEOMETRY);
        areaOpTree.changeValueReference(new RulePktValueReference(0));
        fireTemplate.addAttribute("area", areaOpTree);

        // MeasuredTemp attribute in template
        OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(indexPredNO2, STATE, "value"), INT);
        fireTemplate.addAttribute("measuredtemp", measuredTempOpTree);

        rule.setEventTemplate(fireTemplate);

        return rule;
    }
}
