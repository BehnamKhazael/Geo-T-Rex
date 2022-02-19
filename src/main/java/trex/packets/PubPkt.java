//
// This file is part of T-Rex, a Complex Event Processing Middleware.
// See http://home.dei.polimi.it/margara
//
// Authors: Alessandro Margara, Francesco Feltrinelli
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

package trex.packets;

import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Geometry;
import trex.common.Attribute;
import trex.common.Consts;
import trexengine.CompositeEventGenerator;
import trexengine.StacksRule;

import java.util.*;


/**
 * A publication packet. It is used by sources to send events to the T-Rex engine.
 */
public class PubPkt implements TRexPkt {
	private int eventType;
	private long timeStamp;
	protected Collection<Attribute> attributes = new ArrayList<>();

	/**
	 *  Number of attributes
	 */
	Integer attributesNum = new Integer(0);

	/**
	 *  Number of references to the packet
	 */
	Integer referenceCount = new Integer(0);;

	/**
	 *  Name -> index of the attribute holding that name
	 */
	Map<String, Integer> contentMap = new Hashtable<>();
	
	public PubPkt(int eventType) {
		this.eventType = eventType;
		this.setTimeStamp(0);
		attributes = new ArrayList<Attribute>();
	}

	public PubPkt() {
		Map<String, Integer> contentMap = new HashMap<>();
	}

	public PubPkt(int eventType, long timeStamp) {
		this.eventType = eventType;
		this.setTimeStamp(timeStamp);
		attributes = new ArrayList<Attribute>();
	}
	
	public PubPkt(int eventType, long timeStamp, Collection<Attribute> attr) {
		this.eventType = eventType;
		this.setTimeStamp(timeStamp);
		//attributes = new ArrayList<Attribute>();
		int j = attr.size();
		int k = 0;
		for (Attribute i :
				attr) {
			//while(k<j) {
				String name = i.getName();
				contentMap.put(name,k );
				attributes.add(i);
				k++;
			//}
		}
//		Attribute[] foos = attr.toArray(new Attribute[attr.size()]);
//		for (int i = 0; i < attributesNum; i++) {
//			foos[i] = foos[i];
//			String name = foos[i].getName();
//			contentMap.put(name, i);
//		}
		attributesNum = attr.size();
		referenceCount = 1;
	}

	public PubPkt(PubPkt trexPubPkt) {
		this(trexPubPkt.getEventType(), trexPubPkt.getTimeStamp(), trexPubPkt.getAttributes());
	}

	public PubPkt(int parEventType, Attribute parAttributes[],
				  int parAttributesNum){
		timeStamp = System.currentTimeMillis();
		eventType = parEventType;
		attributesNum = parAttributesNum;
		attributes = new ArrayList<Attribute>();
		Attribute[] i = attributes.toArray(new Attribute[parAttributesNum]);
		//Attribute[] foos = attributes.toArray(new Attribute[parAttributesNum]);
		for (int j = 0; j < attributesNum; j++) {
			i[j] = new Attribute();
			i[j] = parAttributes[j];
			String name = i[j].getName();
			attributes.add(i[j]);
			contentMap.put(name, j);
		}
		referenceCount = 1;
	}
	
	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Collection<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	int getAttributesNum() { return attributesNum; }


	Attribute getAttribute(int attNum) {
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		return foos[attNum]; }

	/**
	 * Fills index and type with the index and type
	 * of the attribute having the given name, if any.
	 * Otherwise returns false.
	 */
	public boolean getAttributeIndexAndType(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {

				return false;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeStringValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeStringValue =foos[index].getValType();

			return true;
		}
		return false;
	}

	/**
	 * Fills index and type with the index and type
	 * of the attribute having the given name, if any.
	 * Otherwise returns false.
	 */
	public boolean getAttributeIndexAndType1(String name, Integer index, Consts.ValType type, StacksRule.CheckParam checkParam){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {

				return false;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			checkParam.checkParameterindex1 = index;
			//checkParameterindex1 = entry.getValue();
			type = foos[index].getValType();
			checkParam.checkParametertype1 = type;
			//checkParametertype1 =foos[index].getValType();

			return true;
		}
		return false;
	}

	/**
	 * Fills index and type with the index and type
	 * of the attribute having the given name, if any.
	 * Otherwise returns false.
	 */
	public boolean getAttributeIndexAndType2(String name, Integer index, Consts.ValType type, StacksRule.CheckParam checkParam){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {

				return false;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			checkParam.checkParameterindex2 = index;
			//checkParameterindex2 = entry.getValue();
			type = foos[index].getValType();
			checkParam.checkParametertype2 = type;
			//checkParametertype2 =foos[index].getValType();

			return true;
		}
		return false;
	}

	public boolean getAttributeIndexAndTypeInt(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {
				continue;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeIntValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeIntValue =foos[index].getValType();
			return true;
		}
		return false;
	}

	public boolean getAttributeIndexAndBool(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {

				continue;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeBoolValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeBoolValue =foos[index].getValType();
			return true;
		}
		return false;
	}

	public boolean getAttributeIndexAndTypeFloat(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {

				continue;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeFloatValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeFloatValue =foos[index].getValType();
			return true;
		}
		return false;
	}

	public boolean getAttributeIndexAndTypeString(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {
				continue;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeStringValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeStringValue =foos[index].getValType();
			return true;
		}
		return false;
	}


	public boolean getAttributeIndexAndTypeGeometry(String name, Integer index, Consts.ValType type, CompositeEventGenerator.AttAndCompute attAndCompute){



		// Get the iterator over the HashMap
		Iterator<Map.Entry<String, Integer> >
				iterator = contentMap.entrySet().iterator();

		// Iterate over the HashMap
		while (iterator.hasNext()) {

			// Get the entry at this iteration
			Map.Entry<String, Integer>
					entry
					= iterator.next();

			// Check if this key is the required key
			if (!name.equals(entry.getKey())) {
				continue;
			}
			Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
			index = new Integer(entry.getValue());
			attAndCompute.attrIndexcomputeGeometryValue = entry.getValue();
			type = foos[index].getValType();
			attAndCompute.typecomputeGeometryValue =foos[index].getValType();
			return true;
		}
		return false;
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be INT.
	 */
	public int getIntAttributeVal(int index){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		return foos[index].getIntVal();
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be FLOAT.
	 */
	public float getFloatAttributeVal(int index){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		return foos[index].getFloatVal();
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be BOOL.
	 */
	public boolean getBoolAttributeVal(int index){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		return foos[index].getBoolVal();
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * The result is stored in the memory pointed by the result parameter.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be STRING.
	 */
	public String getStringAttributeVal(int index, String result){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		//result = (()attributes.toArray()[index])
		//resultcomputeStringValue = foos[index].getStringVal();
		result = foos[index].getStringVal();
		return result;
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * The result is stored in the memory pointed by the result parameter.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be STRING.
	 */
	public Geometry getGeometryAttributeVal(int index, Geometry result){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		//result = (()attributes.toArray()[index])
		//resultcomputeStringValue = foos[index].getStringVal();
		result = foos[index].getGeometryVal();
		return result;
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * The result is stored in the memory pointed by the result parameter.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be STRING.
	 */
	public String getStringAttributeVal(int index, String result, CompositeEventGenerator.AttAndCompute attAndCompute){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		//result = (()attributes.toArray()[index])
		attAndCompute.resultcomputeStringValue = foos[index].getStringVal();
		result = foos[index].getStringVal();
		return result;
	}

	/**
	 * Returns the value of the attribute at the given index.
	 * The result is stored in the memory pointed by the result parameter.
	 * Requires index to be a valid attribute index.
	 * Requires the type of the attribute to be STRING.
	 */
	public Geometry getGeometryAttributeVal(int index, Geometry result, CompositeEventGenerator.AttAndCompute attAndCompute){
		Attribute[] foos = attributes.toArray(new Attribute[attributes.size()]);
		//result = (()attributes.toArray()[index])
		attAndCompute.resultcomputeGeometryValue = foos[index].getGeometryVal();
		result = foos[index].getGeometryVal();
		return result;
	}

	/**
	 * Increases reference count
	 * - Synchronized method -
	 */
	public void incRefCount(){
		referenceCount++;
	}

	/**
	 * Decreases reference count and return true if it becomes 0
	 * - Synchronized method -
	 */
	public boolean decRefCount(){
		boolean returnValue;
		returnValue = (--referenceCount == 0);
		return returnValue;
	}

//	public PubPkt copy() {
//		PubPkt copy = new PubPkt(eventType, attributes, attributesNum);
//		copy.timeStamp = timeStamp;
//		return copy;
//	}

	@Override
	public String toString(){
		JsonObject event = new JsonObject();
		JsonObject innerEvent = new JsonObject();



		//// TODO: 1/30/2020
		String a= "";
		for (Attribute b :
				attributes) {
			switch (b.getValType()){
				case INT:
					innerEvent.addProperty(b.getName(), b.getIntVal());
					break;
				case FLOAT:
					innerEvent.addProperty(b.getName(), b.getFloatVal());
					break;
				case BOOL:
					innerEvent.addProperty(b.getName(), b.getBoolVal());
					break;
				case STRING:
					innerEvent.addProperty(b.getName(), b.getStringVal());
					break;
				case GEOMETRY:
					innerEvent.addProperty(b.getName(), b.getGeometryVal().toText());
					break;
			}
			a = a + (b.toString());
		}
		event.addProperty("timeStamp", this.timeStamp);
		event.addProperty("eventType", this.eventType);
		event.add("eventAttributes", innerEvent);
		return event.toString();
//		return "Public_Packet:{" +
//				"Event_Type:{" + this.eventType + "}" +
//				",Timestamp:{" + this.timeStamp + "}" +
//				",Attributes:{" + a + "}" +
//				"}";




	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (! (obj instanceof PubPkt)) return false;
		PubPkt other = (PubPkt) obj;
		if (eventType != other.eventType) return false;
		if (getTimeStamp() != other.getTimeStamp()) return false;
		if (! attributes.containsAll(other.attributes)) return false;
		if (! other.attributes.containsAll(attributes)) return false;
		return true;
	}
}