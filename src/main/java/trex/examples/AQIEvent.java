package trex.examples;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import trex.common.Constraint;
import trex.packets.RulePkt;

import static trex.common.Consts.ConstraintOp.EQ;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.ValType.GEOMETRY;
import static trex.common.Consts.ValType.INT;

/**
 * Created by sony on 6/8/2021.
 */
public class AQIEvent {

    /**
     * Rule R1:
     * define	AQI(area: Geometry, measuredPM2_5Value: Int, measuredNO2Value: Int, measuredO3Value: Int)
     * from		PM2_5(area=$apm2_5), NO2(area=$ano2), C3(area=$ao3)
     * and last PM2_5(area=$a and value>31) within 5 min. from PM2_5 and last NO2(area=$ano2 and value>101) within 5 min. from NO2 and last O3(area=$ao3 and value>121) within 5 min. from O3
     * where	area=Smoke.area and measuredTemp=Temp.value
     *
     * Define GrowingTemp(old: float, new: float)
     * From Temp() as T1 and
     * last Tamp(value > T1.value) as T2 within 60000 from T1
     * Where old := T1.value, new := T2.value;
     **/

    public static int EVENT_AQI = 30;
    public static int EVENT_PM2_5 = 20;
    public static int EVENT_NO2 = 21;
    public static int EVENT_O3 = 22;

    public RulePkt buildRule() throws ParseException{

        RulePkt rule= new RulePkt(false);


        int indexPredicatePM2_5= 0;
        int indexPredicateNO2= 1;
        int indexPredicateO3= 2;

        WKTReader rdr = new WKTReader();

        Long fiveMin = 50000L; //1000L*60L*5L;

        // PM2_5 root predicate
        Constraint PM_2_5Constraint[] = new Constraint[2];
        // Area
        PM_2_5Constraint[0] = new Constraint();
        PM_2_5Constraint[0].setName("area");
        PM_2_5Constraint[0].setValType(GEOMETRY);
        PM_2_5Constraint[0].setOp(EQ);
        PM_2_5Constraint[0].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        // Value
        PM_2_5Constraint[1] = new Constraint();
        PM_2_5Constraint[1].setName("value");
        PM_2_5Constraint[1].setValType(INT);
        PM_2_5Constraint[1].setOp(GT);
        PM_2_5Constraint[1].setIntVal(31);
        // Add predicate
        rule.addRootPredicate(EVENT_PM2_5, PM_2_5Constraint, 2);

        //rule.addPredicate(EVENT_PM2_5, tempConstr, 2, indexPredO3, fiveMin, EACH_WITHIN);

        // NO2 root predicate
        Constraint NO2Constraint[] = new Constraint[2];
        // Area
        NO2Constraint[0] = new Constraint();
        NO2Constraint[0].setName("area");
        NO2Constraint[0].setValType(GEOMETRY);
        NO2Constraint[0].setOp(EQ);
        NO2Constraint[0].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        // Value
        NO2Constraint[1] = new Constraint();
        NO2Constraint[1].setName("value");
        NO2Constraint[1].setValType(INT);
        NO2Constraint[1].setOp(GT);
        NO2Constraint[1].setIntVal(101);
        // Add predicate
        rule.addRootPredicate(EVENT_NO2, NO2Constraint, 2);



        // O3 root predicate
        Constraint O3Constraint[] = new Constraint[2];
        // Area
        O3Constraint[0] = new Constraint();
        O3Constraint[0].setName("area");
        O3Constraint[0].setValType(GEOMETRY);
        O3Constraint[0].setOp(EQ);
        O3Constraint[0].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
        // Value
        O3Constraint[1] = new Constraint();
        O3Constraint[1].setName("value");
        O3Constraint[1].setValType(INT);
        O3Constraint[1].setOp(GT);
        O3Constraint[1].setIntVal(121);
        // Add predicate
        rule.addRootPredicate(EVENT_O3, O3Constraint, 2);

        //rule.addPredicate(EVENT_NO2, tempConstr, 2, indexPredO3, fiveMin, EACH_WITHIN);

        // Parameter: Smoke.area=Temp.area
        //OpTree areaOpTreeSmoke= new OpTree(new RulePktValueReference(indexPredO3, STATE, "area"), GEOMETRY);
        //OpTree areaOpTreeTemp= new OpTree(new RulePktValueReference(indexPredNO2, STATE, "area"), GEOMETRY);
        //rule.addComplexParameter(INTERSECT, GEOMETRY, areaOpTreeTemp ,areaOpTreeSmoke);


        return rule;
    }
}
