//
// This file is part of T-Rex, a Complex Event Processing Middleware.
// See http://home.dei.polimi.it/margara
//
// Authors: Alessandro Margara
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.
//

package trex.common;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import trex.common.Consts.ValType;

import java.io.IOException;

/**
 * Defines an attribute-value couple. Used to define the content of published events.
 *
 * @author Behnam Khazael
 */


public class Attribute {

    private String name;
    private ValType valType;
    private int intVal;
    private float floatVal;
    private boolean boolVal;
    private String stringVal;
    private Geometry geometryVal;

    public Attribute() {
        this.intVal = 0;
        this.boolVal = false;
        this.floatVal = 0;
        this.stringVal = "";
        this.geometryVal = new GeometryFactory().createGeometry(null);
    }

    public Attribute(String name, int val) {
        this();
        this.name = name;
        this.intVal = val;
        this.valType = ValType.INT;
    }

    public Attribute(String name, float val) {
        this();
        this.name = name;
        this.floatVal = val;
        this.valType = ValType.FLOAT;
    }

    public Attribute(String name, boolean val) {
        this();
        this.name = name;
        this.boolVal = val;
        this.valType = ValType.BOOL;
    }

    public Attribute(String name, String val) {
        this();
        this.name = name;
        this.stringVal = val;
        this.valType = ValType.STRING;
    }

    public Attribute(String name, Geometry val) {
        this();
        this.name = name;
        this.geometryVal = val;
        this.valType = ValType.GEOMETRY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValType getValType() {
        return valType;
    }

    public void setValType(ValType valType) {
        this.valType = valType;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public float getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(float floatVal) {
        this.floatVal = floatVal;
    }

    public boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(boolean boolVal) {
        this.boolVal = boolVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public Geometry getGeometryVal() {
        return geometryVal;
    }

    public void setGeometryVal(Geometry geometryVal) {
        this.geometryVal = geometryVal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Attribute)) return false;
        Attribute other = (Attribute) obj;
        if (valType != other.valType) return false;
        if (!name.equals(other.name)) return false;
        if (valType == ValType.INT) if (intVal != other.intVal) return false;
        else if (valType == ValType.FLOAT) if (floatVal != other.floatVal) return false;
        else if (valType == ValType.BOOL) if (boolVal != other.boolVal) return false;
        else if (valType == ValType.STRING) if (!stringVal.equals(other.stringVal)) return false;
        else if (valType == ValType.GEOMETRY) if (!geometryVal.equals(other.geometryVal)) return false;
        return true;
    }

    @Override
    public String toString() {
            return " Attribute:{" +
                    " Name:" + this.getName() +
                    ",Value_Type:" + this.getValType().toString() +
                    ",Int_value:" + this.getIntVal() +
                    ",Float value:" + this.getFloatVal() +
                    ",Bool value:" + this.getBoolVal() +
                    ",String value:" + this.getStringVal() +
                    ",GEOMETRY value:" + (this.getGeometryVal() == null ? " " :  this.getGeometryVal().toText()) +
                    "}";
    }
}
