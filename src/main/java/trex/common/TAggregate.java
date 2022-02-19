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

import java.util.*;

import trex.common.Consts.AggregateFun;

/**
 * @author Behnam Khazael
 */

/**
 * Defines an aggregate, i.e. a function computed on a set of values.
 * It is used inside a RulePkt to specify which events should be stored for the computation.
 */


public class TAggregate {
	private int eventType;

	private Collection<Constraint> constraints;
	private int lowerId;				// lowerId<0 represents a time-based aggregate
	private long lowerTime;			// Considered only if lowerId<0
	private int upperId;
	private AggregateFun fun;
	private String name;
	private Integer constraintsNum;
	
	private TAggregate(int eventType, int upperId, AggregateFun fun, String name) {
		this.eventType = eventType;
		this.setConstraints(new ArrayList<Constraint>());
		this.upperId = upperId;
		this.fun = fun;
		this.name = name;
		this.lowerId = -1;
		this.lowerTime = 0;
		this.constraintsNum = getConstraints().size();
	}

	public TAggregate(){}
	
	public TAggregate(int eventType, int lowerId, int upperId, AggregateFun fun, String name) {
		this(eventType, upperId, fun, name);
		this.lowerId = lowerId;
	}
	
	public TAggregate(int eventType, long lowerTime, int upperId, AggregateFun fun, String name) {
		this(eventType, upperId, fun, name);
		this.lowerId = -1;
		this.lowerTime = lowerTime;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public Collection<Constraint> getConstraints() {
		return constraints;
	}

	public Constraint[] getConstraintsInArray() {
		Constraint[] con = constraints.toArray(new Constraint[constraints.size()]);
		return con;
	}



	public void addConstraint(Constraint constraint) {
		getConstraints().add(constraint);
	}

	public void addConstraint(Constraint constraint[]) {
		constraints = new ArrayList<>();
		Collections.addAll(constraints, constraint);
//		for (Constraint c :
//				constraint) {
//			getConstraints().add(c);
//		}
	}



	public int getLowerId() {
		return lowerId;
	}

	public void setLowerId(int lowerId) {
		this.lowerId = lowerId;
	}

	public long getLowerTime() {
		return lowerTime;
	}

	public void setLowerTime(long lowerTime) {
		this.lowerTime = lowerTime;
	}

	public int getUpperId() {
		return upperId;
	}

	public void setUpperId(int upperId) {
		this.upperId = upperId;
	}

	public AggregateFun getFun() {
		return fun;
	}

	public void setFun(AggregateFun fun) {
		this.fun = fun;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof TAggregate)) return false;
		TAggregate other = (TAggregate) obj;
		if (getConstraints().size()!= other.getConstraints().size()) return false;
		if (! getConstraints().containsAll(other.getConstraints())) return false;
		if (eventType != other.eventType) return false;
		if (fun != other.fun) return false;
		if (lowerId != other.lowerId) return false;
		if (lowerTime != other.lowerTime) return false;
		if (! name.equals(other.name)) return false;
		if (upperId != other.upperId) return false;
		return true;
	}

	@Override
	public String toString(){
		return " Aggregate: {" +
				" Event Type: " + this.getEventType() +
				", Constraints: {" + this.getConstraints().toString() +"}" +
				", lower Id: " + this.getLowerId() +
				", lower Time: " + this.getLowerTime() +
				", upper Id: " + this.getUpperId() +
				", Aggregate Function: " + this.getFun().toString() +
				", name: " +this.getName() +
				"}";
	}

	public Integer getConstraintsNum() {
		return getConstraints().size();
	}

	public void setConstraintsNum(Integer constraintsNum) {
		this.constraintsNum = constraintsNum;
	}

	public void setConstraints(Collection<Constraint> constraints) {
		this.constraints = constraints;
	}
}
