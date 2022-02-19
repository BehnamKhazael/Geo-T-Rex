package trex.examples;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTReader;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryCollection;
import mil.nga.sf.GeometryEnvelope;
import mil.nga.sf.GeometryType;
import mil.nga.sf.util.GeometryPrinter;
import mil.nga.sf.util.GeometryUtils;
import mil.nga.sf.util.filter.GeometryFilter;
import mil.nga.sf.wkt.GeometryReader;
import mil.nga.sf.wkt.GeometryWriter;

import java.io.IOException;

import com.vividsolutions.*;

/**
 * Created by sony on 9/18/2020.
 */
public class Test {

    public static void main (String[] args) throws IOException {
        // build polygon p1
        LinearRing p1 = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(0,0), new Coordinate(0,10), new Coordinate(10,10), new Coordinate(10,0), new Coordinate(0,0)});
// build polygon p2
        LinearRing p2 = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(5,5), new Coordinate(15,5), new Coordinate(15,15), new Coordinate(5,15), new Coordinate(5,5)});
// calculate intersecting points
        com.vividsolutions.jts.geom.Geometry intersectingPoints = p1.intersection(p2);
// print result
        for(Coordinate c : intersectingPoints.getCoordinates()){
            System.out.println(c.toString());
        }

        com.vividsolutions.jts.geom.Geometry geometry1 = new GeometryFactory().createGeometry(new GeometryFactory().createPolygon(new Coordinate[]{new Coordinate(0,0), new Coordinate(0,10), new Coordinate(10,10), new Coordinate(10,0), new Coordinate(0,0)}));

        com.vividsolutions.jts.geom.Geometry geometry2 = new GeometryFactory().createGeometry(new GeometryFactory().createPolygon(new Coordinate[]{new Coordinate(5,5), new Coordinate(15,5), new Coordinate(15,15), new Coordinate(5,15), new Coordinate(5,5)}));

        com.vividsolutions.jts.geom.Geometry intersect = geometry1.intersection(geometry2);

        System.out.println(intersect.toText());

        WKTReader rdr = new WKTReader();
        com.vividsolutions.jts.geom.Geometry[] geom = new com.vividsolutions.jts.geom.Geometry[3];
        try {
            com.vividsolutions.jts.geom.Geometry geometry3 = rdr.read("POLYGON ((10 20, 30 10, 20 40, 10 20))");
            com.vividsolutions.jts.geom.Geometry geometry4 = rdr.read("POLYGON ((20 20, 40 20, 40 40, 30 40, 20 20))");
            com.vividsolutions.jts.geom.Geometry intersect2 = geometry3.intersection(geometry4);
            System.out.println(intersect2.toText());
        }catch (Exception e){
            e.printStackTrace();
        }

//
//        Geometry geometry = null;
//        Geometry geometry2 = null;
//        GeometryEnvelope geometry3 = null;
//        try {
//            geometry = GeometryReader.readGeometry("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))");
//            geometry2 = GeometryReader.readGeometry("POLYGON ((20 20, 30 20, 30 30, 10 30, 20 20))");
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//        System.out.println("getString: " + GeometryPrinter.getGeometryString(geometry));
//        System.out.println("GeometryWriter.writeGeometry: " + GeometryWriter.writeGeometry(geometry));
//        GeometryType geometryType = geometry.getGeometryType();
//        System.out.println(geometryType);
//        System.out.println(geometry.getCentroid().getX());
//        System.out.println(geometry.getEnvelope().getMaxX());
//        System.out.println(geometry.getEnvelope().contains(geometry2.getEnvelope()));
//        System.out.println(geometry.getEnvelope().intersects (geometry2.getEnvelope()));
//        geometry3 = geometry.getEnvelope().overlap(geometry2.getEnvelope());
//        System.out.println(geometry3);
    }
}
