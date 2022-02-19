//
// This file is part of T-Rex, a Complex Event Processing Middleware.
// See http://home.dei.polimi.it/margara
//
// Authors: Alessandro Margara, Daniele Rogora
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

import java.util.*;

import trex.common.*;


import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.OpTreeType.LEAF;
import static trex.common.Consts.StateType.AGG;
import static trex.common.Consts.StateType.NEG;
import static trex.common.Consts.StateType.STATE;

/**
 * @author Behnam Khazael
 */

/**
 * Defines a RulePkt, used to send a rule to the T-Rex engine
 * A RulePkt contains the definition of a composite event.
 * More in particular, it contains the pattern that must be detected
 * and the template of the composite event to generate.
 * The pattern is expressed through a set of predicates, a set of negations,
 * a set of aggregates, and a set of parameters.
 */
public class RulePkt implements TRexPkt {
	// Last used rule identifier
	private static int lastId;
	// Identifier of the rule
	private Integer ruleId;
	// Array of event predicates
	private Map<Integer, EventPredicate> predicates = new HashMap<>();
	private Map<Integer, ComplexParameter> complexParameters = new HashMap<>();
	private Map<Integer, Negation> negations = new HashMap<>();
	private Map<Integer, TAggregate> aggregates = new HashMap<>();
	Set<Integer> consuming = new HashSet<>();
	EventTemplate eventTemplate = new EventTemplate();
	ArrayList<Parameter> parameters = new ArrayList<>();
	//ArrayList<GPUParameter> complexGPUParameters;

	public RulePkt(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
		predicates = new HashMap<Integer, EventPredicate>();
		complexParameters = new HashMap<Integer, ComplexParameter>();
		negations = new HashMap<Integer, Negation>();
		aggregates = new HashMap<Integer, TAggregate>();
		consuming = new HashSet<Integer>();
	}
	
	public RulePkt(RulePkt trexRulePkt) {
		this(trexRulePkt.getEventTemplate());
	}

	public static int getLastId() {
		return lastId;
	}

	public static void setLastId(int lastId) {
		RulePkt.lastId = lastId;
	}

	public int getPredicatesNum() {
		return predicates.size();
	}

	public EventPredicate getPredicates(int index) {
		return predicates.get(index);
	}

	public void addPredicate(EventPredicate predicate) {
		predicates.put(predicates.size(), predicate);
	}

	public int getParametersNum() {
		return parameters.size();
	}

	public ComplexParameter getComplexParameter(int index) {
		return complexParameters.get(index);
	}

	public void addComplexParameter(ComplexParameter parameter) {
		complexParameters.put(complexParameters.size(), parameter);
	}

	public void addParameter(Parameter parameter){
		parameters.add(parameter);
	}

	public boolean addParameter(int index1, char[] name1, int index2, char[] name2,
							 Consts.StateType type){
		int numPredicates = predicates.size();
		int numAggregates = aggregates.size();
		int numNegations = negations.size();
		if (index1 < 0 || index1 >= numPredicates) {
			return false;
		}
		if (index2 < 0) {
			return false;
		}
		if (type == STATE && index2 >= numPredicates) {
			return false;
		}
		if (type == AGG && index2 >= numAggregates) {
			return false;
		}
		if (type == NEG && index2 >= numNegations) {
			return false;
		}
		Parameter p = new Parameter();
		p.setEvIndex1(index1);
		p.setEvIndex2(index2);
		p.setType(type);
		p.setName1(name1.toString());
		p.setName2(name2.toString());

		parameters.add(p);
		return true;
	}
	
	public int getNegationsNum() {
		if (negations == null)
			return 0;
		return negations.size();
	}

	public Negation getNegation(int index) {
		return negations.get(index);
	}

	public void addNegation(Negation negation) {
		negations.put(negations.size(), negation);
	}
	
	public int getAggregatesNum() {
		if (aggregates == null)
			return 0;
		return aggregates.size();
	}

	public TAggregate getAggregate(int index) {
		return aggregates.get(index);
	}

	public void addAggregate(TAggregate aggregate) {
		aggregates.put(aggregates.size(), aggregate);
	}
	
	public Collection<Integer> getConsuming() {
		return consuming;
	}
	
	public void addConsuming(int consumingIndex) {
		consuming.add(consumingIndex);
	}
	
	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}
	
	public void setEventTemplate(EventTemplate et) {
		this.eventTemplate = et;
	}
	
	public Map<Integer, EventPredicate> getPredicates() {
		return predicates;
	}

	public Map<Integer, ComplexParameter> getComplexParameters() {
		return complexParameters;
	}

	public Map<Integer, Negation> getNegations() {
		return negations;
	}

	public Map<Integer, TAggregate> getAggregates() {
		return aggregates;
	}



	/**
	 * Adds the root predicate.
	 * Returns false if an error occurs
	 */
	public Boolean addRootPredicate(Integer eventType, Constraint constraints[], Integer constrLen){
		if (predicates != null) {
			if (parameters.size() > 0)
			return false;
		}
		EventPredicate p = new EventPredicate();
		p.setEventType(eventType);
		p.setRefersTo(-1);
		p.setWin(0);
		p.setKind(EACH_WITHIN);
		p.setConstraintsNum(constrLen);
		Collection<Constraint> list = Arrays.asList(constraints);
		p.setConstraints(list);
		Constraint[] foos = p.getConstraints().toArray(new Constraint[p.getConstraints().size()]);
		for (int i = 0; i < constrLen; ++i) {
			foos[i] = constraints[i];
		}
		predicates.put(predicates.size(), p);
		return true;
	}

	/**
	 * Adds a predicate; root predicate must be already defined.
	 * Returns false if an error occurs.
	 */
	public Boolean addPredicate(Integer eventType, Constraint constraints[], Integer constrLen,
					  Integer refersTo, Long win, Consts.CompKind kind){
		int numPredicates = predicates.size();
		if (numPredicates <= 0 || refersTo >= numPredicates) {
			return false;
		}
		EventPredicate p = new EventPredicate();
		p.setEventType(eventType);
		p.setRefersTo(refersTo);
		p.setWin(win);
		p.setKind(kind);
		p.setConstraintsNum(constrLen);
		Collection<Constraint> list = Arrays.asList(constraints);
		p.setConstraints(list);
		Constraint[] foos = p.getConstraints().toArray(new Constraint[p.getConstraints().size()]);
		for (int i = 0; i < constrLen; ++i) {
			foos[i] = constraints[i];
		}
		predicates.put(predicates.size(), p);
		return true;
	}

	/**
	 * Adds a new time based negation.
	 * It asks no events of type eventType satisfying all constraints to occur
	 * within win from the state with id referenceId.
	 * Returns false if an error occurs.
	 */
	Boolean addTimeBasedNegation(Integer eventType, Constraint constraints[],
							  Integer constrLen, Integer referenceId, Long win){
		return addNegation(eventType, constraints, constrLen, -1, win, referenceId);
	}

	/**
	 * Adds a negation between two states.
	 * It asks no events of type eventType satisfying all constraints to occur
	 * between the states with ids id1 and id2.
	 * Returns false if an error occurs.
	 */
	Boolean addNegationBetweenStates(Integer eventType, Constraint constraints[],
								  Integer constrLen, Integer id1, Integer id2){
		if (id1 < id2) {
			Integer temp = id1;
			id1 = id2;
			id2 = temp;
		}
		return addNegation(eventType, constraints, constrLen, id1, System.nanoTime(), id2);
	}

	/**
	 * Adds a new parameter between states.
	 * It asks the value of the attribute having name name1 in the event used for
	 * id id1 to be equal to the value of the attribute having name name2 in the
	 * event used for id id2.
	 */
	public Boolean addParameterBetweenStates(Integer id1, String name1, Integer id2, String name2){
		if (id1 < id2) {
			Integer temp = id1;
			id1 = id2;
			id2 = temp;
			String tempname = name1;
			name1 = name2;
			name2 = tempname;
		}
		return addParameter(id1, name1, id2, name2, STATE);
	}

	/**
	 * Add a new parameter between states
	 * It asks that the comparison pOperation is satisfied when checked between
	 * the leftTree and the rightTree; these trees may involve any state of the
	 * sequence or static value
	 */
	public Boolean addComplexParameter(Consts.ConstraintOp pOperation, Consts.ValType type, OpTree leftTree,
								OpTree rightTree){
		ComplexParameter p = new ComplexParameter();
		p.setOperation(pOperation);
		p.setLeftTree(leftTree);
		p.setRightTree(rightTree);
		p.setType(type);
		p.setsType(STATE);
		int lIndex = Math.max(findLastState(leftTree), findLastState(rightTree));
		if (lIndex < 0) {
			return false;
		}
		p.setLastIndex(lIndex);
		complexParameters.put(complexParameters.size(), p);

//		int depth = 2;
//		ComplexParameter gp;
//		gp.aggIndex = lIndex;
//		// Inversion for the GPU
//		gp.lastIndex = -1;
//		depth = std::max(findDepth(leftTree, depth), findDepth(rightTree, depth));
//		gp.depth = depth;
//		gp.sType = AGG;
//		gp.vType = type;
//		gp.operation = pOperation;
//		serializeTrees(leftTree, rightTree, depth, gp);
//		complexGPUParameters.push_back(std::move(gp));
		return true;
	}

	/**
	 * Adds a new parameter that is analyzed when searching for a possible
	 * negation event
	 * It asks that the comparison pOperation is satisfied when checked between
	 * the leftTree and the rightTree; these trees may NOT involve any state of
	 * the sequence (read below). They can also refer to static values and 1, and
	 * only one, negation.
	 * IMPORTANT: for this to work as expected on the GPU engine the parameter
	 * MUST NOT refer to any state that comes later than the last state to which
	 * the negation refers in the sequence
	 */
	public Boolean addComplexParameterForNegation(Consts.ConstraintOp pOperation, Consts.ValType type,
										   OpTree leftTree, OpTree rightTree){
		ComplexParameter p = new ComplexParameter();
		p.setOperation(pOperation);
		p.setLeftTree(leftTree);
		p.setRightTree(rightTree);
		p.setType(type);
		p.setsType(NEG);
		int lIndex = Math.max(findNegation(leftTree), findNegation(rightTree));
		if (lIndex < 0) {
			return false;
		}
		p.setLastIndex(lIndex);
		complexParameters.put(complexParameters.size(), p);

//		int depth = 2;
//		GPUParameter gp;
//		gp.negIndex = lIndex;
//		// Inversion for the GPU
//		gp.lastIndex = this->getPredicatesNum() -
//				std::max(findLastState(leftTree), findLastState(rightTree)) -
//				1;
//		depth = std::max(findDepth(leftTree, depth), findDepth(rightTree, depth));
//		gp.depth = depth;
//		gp.sType = NEG;
//		gp.vType = type;
//		gp.operation = pOperation;
//		serializeTrees(leftTree, rightTree, depth, gp);
//		complexGPUParameters.push_back(std::move(gp));
		return true;
	}

	/**
	 * Add a new parameter between that is analyzed when computing an aggregate.
	 * It asks that the comparison pOperation is satisfied when checked between
	 * the leftTree and the rightTree; these trees may involve any state of the
	 * sequence or static value and the aggregate event that is being analyzed
	 */
	public Boolean addComplexParameterForAggregate(Consts.ConstraintOp pOperation, Consts.ValType type,
											OpTree leftTree, OpTree rightTree){
		ComplexParameter p = new ComplexParameter();
		p.setOperation(pOperation);
		p.setLeftTree(leftTree);
		p.setRightTree(rightTree);
		p.setType(type);
		p.setsType(AGG);
		int lIndex = Math.max(findNegation(leftTree), findNegation(rightTree));
		if (lIndex < 0) {
			return false;
		}
		p.setLastIndex(lIndex);
		complexParameters.put(complexParameters.size(), p);

//		int depth = 2;
//		GPUParameter gp;
//		gp.aggIndex = lIndex;
//		// Inversion for the GPU
//		gp.lastIndex = -1;
//		depth = std::max(findDepth(leftTree, depth), findDepth(rightTree, depth));
//		gp.depth = depth;
//		gp.sType = AGG;
//		gp.vType = type;
//		gp.operation = pOperation;
//		serializeTrees(leftTree, rightTree, depth, gp);
//		complexGPUParameters.push_back(std::move(gp));
		return true;
	}

	/**
	 * Adds a new parameter between a state and a negation.
	 * It asks the value of the attribute having name name in the event used for
	 * id to be equal to the value of the attribute having name negName in the
	 * event used for negation negId.
	 */
	public Boolean addParameterForNegation(Integer id, String name, Integer negId, String negName){
		return addParameter(id, name, negId, negName, NEG);
	}

	/**
	 * Adds a new parameter between a state and an aggregate.
	 * It asks the value of the attribute having name name in the event used for
	 * id to be equal to the value of the attribute having name aggName in the
	 * event used for aggregate aggId.
	 */
	public Boolean addParameterForAggregate(Integer id, String name, Integer aggId, String aggName){
		return addParameter(id, name, aggId, aggName, AGG);
	}

	/**
	 * Adds a new time based aggregate to compute the given function fun using a
	 * set of values.
	 * In particular, it uses all the values of attributes having the given name
	 * coming from events having the given eventType and satisfying all
	 * constraints, occurring within win from the event used for the referenceId.
	 */
	public Boolean addTimeBasedAggregate(Integer eventType, Constraint constraints[],
							   Integer constrLen, Integer referenceId,  Long win,
							   String name, Consts.AggregateFun fun){
		return addAggregate(eventType, constraints, constrLen, -1, win, referenceId,
				name, fun);
	}

	/**
	 * Adds a new aggregate to compute the given function fun using a set of
	 * values.
	 * In particular, it uses all the values of attributes having the given name
	 * coming from events having the given eventType and satisfying all
	 * constraints, occurring within the events used for the ids id1 and id2.
	 */
	public Boolean addAggregateBetweenStates(Integer eventType, Constraint constraints[],
								   Integer constrLen, Integer id1, Integer id2, String name,
								   Consts.AggregateFun fun){
		if (id1 < id2) {
			Integer temp = id1;
			id1 = id2;
			id2 = temp;
		}
		return addAggregate(eventType, constraints, constrLen, id1, System.nanoTime(), id2,
				name, fun);
	}

	/**
	 * Adds a consuming clause for the given eventIndex.
	 * Returns false if an error occurs.
	 */
	public Boolean addConsuming(Integer eventIndex){
		Integer numPredicates = predicates.size();
		if (eventIndex < 0 || eventIndex >= numPredicates) {
			return false;
		}
		consuming.add(eventIndex);
		return true;
	}

	/**
	 * Sets the template for the composite event generated by the rule
	 */
	public void setCompositeEventTemplate(EventTemplate parCeTemplate) {
		eventTemplate = parCeTemplate;
	}

	/**
	 * Fills leaves with the set of indexes that are leaves in the ordering graph
	 */
	Set<Integer> getLeaves(){
		Set<Integer> leaves = null;
		Integer[] values = getReferenceCount().values().toArray(new Integer[getReferenceCount().size()]);
		for (int i = 0; i <= getReferenceCount().size() ; i++) {
			if (values[i] == 0) {
				leaves.add(i);
			}
		}
		return leaves;
	}

	/**
	 * @deprecated Use `std::set<int> getLeaves() const;` instead
	 */
	public void getLeaves(Set<Integer> leaves) { leaves = getLeaves(); }

	/**
	 * Fills joinPoints with the set of indexes that are shared
	 * among more than one sequence
	 */
	public Set<Integer> getJoinPoints(){
		Set<Integer> joinPoints = null;
		Integer[] values = getReferenceCount().values().toArray(new Integer[getReferenceCount().size()]);
		for (int i = 0; i <= getReferenceCount().size() ; i++) {
			if (values[i] > 0) {
				joinPoints.add(i);
			}
		}
		return joinPoints;
	};
	/**
	 * @deprecated Use `std::set<int> getJoinPoints() const;` instead
	 */
	public void getJoinPoints(Set<Integer> joinPoints) {
		joinPoints = getJoinPoints();
	}

	/**
	 * Returns the number of predicates in the rule
	 */
	//Already Defined in the scope!
	// Integer getPredicatesNum() { return predicates.size(); }

	/**
	 * Returns the number of parameters in the rule
	 */
	//Already Defined in the scope!
	//Integer getParametersNum() { return parameters.size(); }

	// Is always the same as GPUComplexParameters, no need for another method
	public Integer getComplexParametersNum() {
		if (complexParameters == null)
			return 0;
		return complexParameters.size(); }

	/**
	 * Returns the number of negations in the rule
	 */
	//Already Defined in the scope!
	//Integer getNegationsNum() { return negations.size(); }

	/**
	 * Returns the number of aggregates in the rule
	 */
	//Already Defined in the scope!
	//Integer getAggregatesNum()  { return aggregates.size(); }

	/**
	 * Returns the predicate with the given index
	 */
	public EventPredicate getPredicate(int index) { return predicates.get(index); }

	/**
	 * Returns the parameter with the given index
	 */
	public Parameter getParameter(int index) { return parameters.get(index); }

	/**
	 * Returns the complex parameter for the CPU with the given index
	 */
//	CPUParameter getComplexParameter(int index) {
//		return parameters.get(index);
//	}

	/**
	 * Returns the complex parameter for the GPU with the given index
	 */
//	GPUParameter& getComplexGPUParameter(int index) {
//		return complexGPUParameters[index];
//	}

	/**
	 * Returns the negation with the given index
	 */
	//Already Defined in the scope!
	//Negation getNegation(int index) { return negations.get(index); }

	/**
	 * Returns the aggregate with the given index
	 */
	//Already Defined in the scope!
	//TAggregate getAggregate(int index) { return aggregates.get(index); }

	/**
	 * Returns the set of consuming indexes
	 */
	//Already Defined in the scope!
	//Set<Integer> getConsuming() { return consuming; }

	/**
	 * Returns the composite event template
	 */
	public EventTemplate getCompositeEventTemplate() {
		return eventTemplate;
	}

	/**
	 * Returns the rule id
	 */
	//Already Defined in the scope!
	//Integer getRuleId() { return ruleId; }

	/**
	 * Returns true if the subscription contains at least
	 * a predicate with the given eventType
	 */
	public Boolean containsEventType(Integer eventType, Boolean includeNegations){
		EventPredicate[] it = predicates.values().toArray(new EventPredicate[predicates.size()]);
		for (int i = 0; i <= predicates.size() ; i++ ){
			if (it[i].getEventType() == eventType)
				return true;
		}
		EventPredicate[] itn = negations.values().toArray(new EventPredicate[negations.size()]);
		for (int i = 0; i <= negations.size() ; i++ ){
			if (itn[i].getEventType() == eventType && includeNegations)
				return true;
		}
		return false;
	}

	/**
	 * Returns the set of all contained event types
	 */
	public Set<Integer> getContainedEventTypes(){
		Set<Integer> eventTypes = null;
		for (EventPredicate it : predicates.values()) {
			eventTypes.add(it.getEventType());
		}
		return eventTypes;
	}

	/**
	 * Get the maximum time window between the two events
	 * Requires lowerId < upperId
	 * && lowerId < getConstraintNum()
	 * && upperId < getConstraintNum()
	 * TODO output and timeBetween change to Long from TimeMs
	 */
	public Long getWinBetween(int lowerId, int upperId) {
		int currentIndex = upperId;
		Long timeBetween = 0L;
		EventPredicate[] it = predicates.values().toArray(new EventPredicate[predicates.size()]);
		while (currentIndex != lowerId) {
			timeBetween += it[currentIndex].getWin();
			currentIndex = it[currentIndex].getRefersTo();
		}
		return timeBetween;
	};

	/**
	 * Returns the maximum length of a sequence defined in the subscription
	 * TODO output and timeBetween change to Long from TimeMs
	 */
	public Long getMaxWin() {
		Long returnTime = 0L;
		for (Integer leaf : getLeaves()) {
			long currentTime = getWinBetween(0, leaf);
			if (currentTime > returnTime) {
				returnTime = currentTime;
			}
		}
		return returnTime;
	}

	/**
	 * Returns true if id1 is directly defined through id2, or viceversa
	 */
	public Boolean isDirectlyConnected(Integer id1, Integer id2){
		int numPred = predicates.size();
		if (id1 == id2 || id1 < 0 || id2 < 0 || id1 >= numPred || id2 >= numPred) {
			return false;
		}
		EventPredicate[] it = predicates.values().toArray(new EventPredicate[predicates.size()]);
		return (it[id2].getRefersTo() == id1 || it[id1].getRefersTo() == id2);
	}

	/**
	 * Returns true if id1 is defined through id2, or viceversa
	 */
	public Boolean isInTheSameSequence(Integer id1, Integer id2){
		int numPred = predicates.size();
		if (id1 == id2 || id1 < 0 || id2 < 0 || id1 >= numPred || id2 >= numPred) {
			return false;
		}
		Integer min = Math.min(id1, id2);
		Integer max = Math.max(id1,id2);
		EventPredicate[] it = predicates.values().toArray(new EventPredicate[predicates.size()]);
		while (max > 0) {
			max = it[max].getRefersTo();
			if (max == min) {
				return true;
			}
		}
		return false;
	};

	/**
	 * Overloading
	 */
	//Boolean operator<(RulePkt pkt){};
	//Boolean operator==(RulePkt pkt){};
	//Boolean operator!=(RulePkt pkt) {};


	/**
	 * Adds a new negation negation.
	 * Predicates identified by lowerId and upperId must be already defined.
	 * If lowerId < 0, then the lowerTime bound is used.
	 * Returns false if an error occurs
	 */
	Boolean addNegation(Integer eventType, Constraint constraints[], Integer constrLen,
					 Integer lowerId, Long lowerTime, Integer upperId){
		int numPredicates = predicates.size();
		if (lowerId >= numPredicates) {
			return false;
		}
		if (upperId < 0 || upperId >= numPredicates) {
			return false;
		}
		if (lowerId >= 0 && lowerId <= upperId) {
			return false;
		}
		Negation n = null;
		n.setEventType(eventType);
		n.setLowerId(lowerId);
		n.setLowerTime(lowerTime);
		n.setUpperId(upperId);
		n.setConstraintsNum(constrLen);
		constraints = new Constraint[constrLen];
		for (int i = 0; i < constrLen; ++i) {
			n.addConstraint(constraints[i]);
		}
		negations.put(negations.size(), n);
		return true;
	};

	/**
	 * Adds the parameter with the given attributes
	 * The type determines whether the second index refers to a normal state,
	 * to a negation, or to an aggregate.
	 */
	Boolean addParameter(Integer index1, String name1, Integer index2, String name2,
					  Consts.StateType type){
		int numPredicates = predicates.size();
		int numAggregates = aggregates.size();
		int numNegations = negations.size();
		if (index1 < 0 || index1 >= numPredicates) {
			return false;
		}
		if (index2 < 0) {
			return false;
		}
		if (type == STATE && index2 >= numPredicates) {
			return false;
		}
		if (type == AGG && index2 >= numAggregates) {
			return false;
		}
		if (type == NEG && index2 >= numNegations) {
			return false;
		}
		Parameter p = new Parameter();
		p.setEvIndex1(index1);
		p.setEvIndex2(index2);
		p.setType(type);
		p.setName1(name1);
		p.setName2(name2);
		parameters.add(p);
		return true;
	}

	/**
	 * Adds a new aggregate.
	 * Predicates identified by lowerId and upperId must be already defined.
	 * If lowerId < 0, then the lowerTime bound is used.
	 * Returns false if an error occurs
	 */
	Boolean addAggregate(Integer eventType, Constraint constraints[], Integer constrLen,
					  Integer lowerId, Long lowerTime, Integer upperId,
					  String name, Consts.AggregateFun fun){
		int numPredicates = predicates.size();
		if (lowerId >= numPredicates) {
			return false;
		}
		if (upperId < 0 || upperId >= numPredicates) {
			return false;
		}
		if (lowerId >= 0 && lowerId <= upperId) {
			return false;
		}
		TAggregate a = null;
		a.setEventType(eventType);
		a.setLowerId(lowerId);
		a.setLowerTime(lowerTime);
		a.setUpperId(upperId);
		a.setConstraintsNum(constrLen);
		constraints = new Constraint[constrLen];
		for (int i = 0; i < constrLen; ++i) {
			a.addConstraint(constraints[i]);
		}
		a.setName(name);
		a.setFun(fun);
		aggregates.put(aggregates.size(), a);
		return true;
	}

	/**
	 * Fills referenceCount with the number of times each index is referenced
	 */
	Map<Integer, Integer> getReferenceCount(){
		Map<Integer, Integer> referenceCount = null;
		int numPredicates = predicates.size();
		Object[] objectArray = referenceCount.entrySet().toArray();
		for (int i = 0; i < numPredicates; ++i) {
			objectArray[i] = 0;
		}
		Integer[] objectArray2 = referenceCount.entrySet().toArray(new Integer[20]);
		EventPredicate[] eventp = referenceCount.entrySet().toArray(new EventPredicate[10]);
		for (EventPredicate ep: eventp
			 ) {
			++(objectArray2)[ep.getRefersTo()];
		}
		return referenceCount;
	}

	/**
	 * Returns the index of the last state in a given tree
	 */
	Integer findLastState(OpTree tree){
		if (tree.getType() == LEAF) {
			OpValueReference reference = tree.getValueRef();
			RulePktValueReference pktReference =
					(RulePktValueReference)(reference);
			if (pktReference == null) {
				return -1;
			} else {
				if (pktReference.isAggIndex() || pktReference.isNegIndex()) {
					return -1;
				}
				return pktReference.getIndex();
			}
		} else {
			return Math.max(findLastState(tree.getLeftTree()),
					findLastState(tree.getRightTree()));
		}
	}

	/**
	 * Returns the index of the unique negation in the given tree
	 */
	Integer findNegation(OpTree tree){
		if (tree.getType() == LEAF) {
			OpValueReference reference = tree.getValueRef();
			RulePktValueReference pktReference =
					(RulePktValueReference)(reference);
			if (pktReference == null) {
				return -1;
			} else {
				if (pktReference.isNegIndex()) {
					return pktReference.getIndex();
				}
				return -1;
			}
		} else {
			return Math.max(findNegation(tree.getLeftTree()),
					findNegation(tree.getRightTree()));
		}
	};

	/**
	 * Returns the index of the unique aggregate in the given tree
	 */
	Integer findAggregate(OpTree tree){
		if (tree.getType() == LEAF) {
			OpValueReference reference = tree.getValueRef();
			RulePktValueReference pktReference =
					(RulePktValueReference)(reference);
			if (pktReference == null) {
				return -1;
			} else {
				if (pktReference.isAggIndex()) {
					return pktReference.getIndex();
				}
				return -1;
			}
		} else {
			return Math.max(findAggregate(tree.getLeftTree()),
					findAggregate(tree.getRightTree()));
		}
	};

	/**
	 * Returns the depth of the given tree
	 */
	Integer findDepth(OpTree tree, Integer depth){
		if (tree.getType() == LEAF) {
			return depth;
		} else {
			return Math.max(findDepth(tree.getLeftTree(), depth + 1),
					findDepth(tree.getRightTree(), depth + 1));
		}
	};

	/**
	 * Serializes the trees passed as parameters and writes the results in the
	 * given GPUComplexParameter structure
	 */
//	void serializeTrees(OpTree ltree, OpTree rtree, Integer depth,
//						GPUParameter gp){};

	/**
	 * Does the actual job for the serializeTrees function;
	 * operates on one tree at a time
	 */
//	void serializeNode(OpTree tree, Integer idx, Node serialized[]) const;

	public RulePkt copy(){
		RulePkt copy = new RulePkt(false);
		EventPredicate root = this.getPredicate(0);
		copy.addRootPredicate(root.getEventType(), (Constraint[]) root.getConstraints().toArray(), root.getConstraints().size());
		for (int i = 1; i < this.getPredicatesNum(); ++i) {
			EventPredicate predicate = this.getPredicate(i);
			copy.addPredicate(predicate.getEventType(), (Constraint[])predicate.getConstraints().toArray(),
					predicate.getConstraints().size(), predicate.getRefersTo(),
					predicate.getWin(), predicate.getKind());
		}
		copy.setCompositeEventTemplate(this.getCompositeEventTemplate());
		return copy;
	}

	public RulePkt(Boolean resetCount) {
		if (resetCount) {
			lastId = 0;
		}
		ruleId = lastId++;
		eventTemplate = null;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof RulePkt)) return false;
		RulePkt other = (RulePkt) obj;
		if (predicates.size() != other.predicates.size()) return false;
		for (Integer key : predicates.keySet()) {
			if (! other.predicates.containsKey(key)) return false;
			if (! other.predicates.get(key).equals(predicates.get(key))) return false;
		}
		if (aggregates.size() != other.aggregates.size()) return false;
		for (Integer key : aggregates.keySet()) {
			if (! other.aggregates.containsKey(key)) return false;
			if (! other.aggregates.get(key).equals(aggregates.get(key))) return false;
		}
		if (negations.size() != other.negations.size()) return false;
		for (Integer key : negations.keySet()) {
			if (! other.negations.containsKey(key)) return false;
			if (! other.negations.get(key).equals(negations.get(key))) return false;
		}
		if (complexParameters.size() != other.complexParameters.size()) return false;
		for (Integer key : complexParameters.keySet()) {
			if (! other.complexParameters.containsKey(key)) return false;
			if (! other.complexParameters.get(key).equals(complexParameters.get(key))) return false;
		}
		if (consuming.size() != other.consuming.size()) return false;
		if (! consuming.containsAll(other.consuming)) return false;
		if (! eventTemplate.equals(other.eventTemplate)) return false;
		return true;
	}

	@Override
	public String toString(){

		//// TODO: 1/30/2020
		return "Event Rule: {" +
				"Predicates: {" + this.getPredicates().toString() + "}" +
				", Parameters: {" + this.getComplexParameters().toString() + "}" +
				", Negations: {" +this.getNegations().toString() + "}" +
				", Aggregates: {" + this.getAggregates().toString() + "}" +
				", Consuming: " + this.getConsuming().toString() +
				", Event Template: {" + this.getEventTemplate().toString() + "}" +
				"}";
	}

//	teslaRule = "Assign 2000=>Smoke," +
//			"2001=>Temp," +
//			"2100=>Fire " +
//			"Define Fire(area:string,measuredTemp:float)" +
//			"From Smoke(area=>$a)" +
//			"and each Temp( [ string ] area=$a,value>45)within" +
//			" 300000from Smoke" +
//			" Where area := Smoke.area,measuredTemp := Temp.value;";

	public String toEventString(){

		//// TODO: 1/30/2020
		return "Event Rule: {" +
				"Predicates: {" + this.getPredicates().toString() + "}" +
				", Parameters: {" + this.getComplexParameters().toString() + "}" +
				", Negations: {" +this.getNegations().toString() + "}" +
				", Aggregates: {" + this.getAggregates().toString() + "}" +
				", Consuming: " + this.getConsuming().toString() +
				", Event Template: {" + this.getEventTemplate().toString() + "}" +
				"}";
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
}
