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
/**
 * @author Behnam Khazael
 */

package trex.common;

import java.util.Collection;

import trex.common.Consts.CompKind;


/**
 * Represents a basic event predicate. 
 */
public class EventPredicate {
	private int eventType;
	private Collection<Constraint> constraints;
	private int refersTo;
	private long win;
	private CompKind kind;
	private Integer constraintsNum;
	
	/** Creates the root predicate */
	public EventPredicate(int eventType, Collection<Constraint> constraints) {
		this(eventType, constraints, -1, 0, CompKind.EACH_WITHIN);
	}

	public EventPredicate() {

	}
	
	public EventPredicate(int eventType, Collection<Constraint> constraints, int refersTo, long win, CompKind kind) {
		this.setEventType(eventType);
		this.setConstraints(constraints);
		this.setRefersTo(refersTo);
		this.setWin(win);
		this.setKind(kind);
	}

	public int getEventType() {
		return eventType;
	}

	public Collection<Constraint> getConstraints() {
		return constraints;
	}

	public int getRefersTo() {
		return refersTo;
	}

	public long getWin() {
		return win;
	}

	public CompKind getKind() {
		return kind;
	}

	public Integer getConstraintsNum(){
		return constraints.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (! (obj instanceof EventPredicate)) return false;
		EventPredicate other = (EventPredicate) obj;
		if (! getConstraints().containsAll(other.getConstraints())) return false;
		if (! other.getConstraints().containsAll(getConstraints())) return false;
		if (getEventType() != other.getEventType()) return false;
		if (getKind() != other.getKind()) return false;
		if (getRefersTo() != other.getRefersTo()) return false;
		if (getWin() != other.getWin()) return false;
		return true;
	}

	@Override
	public String toString(){
		return " EventPredicate : {" +
				"Event Type (Better to be a name): " + this.getEventType() +
				", Constraints: {" + this.getConstraints().toString() + "}" +
				", Refers To (Actually an event!): " + this.getRefersTo() +
				", Time Window: " + this.getWin() +
				", Kinds of composition: " + this.getKind().toString() +
				"}";
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public void setConstraints(Collection<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void setRefersTo(int refersTo) {
		this.refersTo = refersTo;
	}

	public void setWin(long win) {
		this.win = win;
	}

	public void setKind(CompKind kind) {
		this.kind = kind;
	}

	public void setConstraintsNum(Integer constraintsNum) {
		this.constraintsNum = constraintsNum;
	}
}
