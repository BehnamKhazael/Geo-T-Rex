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

/**
 * Specifies a single attribute in an EventTemplate 
 */

/**
 * @author Behnam Khazael
 */

public class EventTemplateAttr {
	private String name;
	private OpTree value;
	
	public EventTemplateAttr(String name, OpTree value) {
		this.setName(name);
		this.setValue(value);
	}
	
	public void setOpTree(OpTree tree) {
		this.setValue(tree);
	}

	public String getName() {
		return name;
	}

	public OpTree getValue() {
		return value;
	}

	EventTemplateAttr dup() {
		return new EventTemplateAttr(name, value.dup());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (! (obj instanceof EventTemplateAttr)) return false;
		EventTemplateAttr other = (EventTemplateAttr) obj;
		if (! getName().equals(other.getName())) return false;
		if (! getValue().equals(other.getValue())) return false;
		return true;
	}

	@Override
	public String toString(){
		return "Event Template Attributes: {" +
				" Name: " + this.getName() +
				", Value: " + this.getValue().toString() +
				"}";
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(OpTree value) {
		this.value = value;
	}
}
