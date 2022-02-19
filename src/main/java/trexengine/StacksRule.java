package trexengine;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Geometry;
import trex.common.*;
import trex.packets.PubPkt;
import trex.packets.RulePkt;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static trex.common.Consts.CompKind.FIRST_WITHIN;
import static trex.common.Consts.CompKind.LAST_WITHIN;
import static trex.common.Consts.MAX_RULE_FIELDS;
import static trex.common.Consts.StateType.NEG;
import static trex.common.Consts.StateType.STATE;
import static trexengine.Functions.*;

/**
 * Created by sony on 2/6/2020.
 *
 * Represents a single detection sequence: it is represented by a set of stacks
 * with optional negations and parameters.
 *
 */
public class StacksRule {

    /**
     *  The id of the rule
     */
    Integer ruleId;

    RulePkt rulePkt;

    /**
     *  Stacks in the rule (stack id -> data structure)
     */
    Map<Integer, Stack> stacks = new Hashtable<>();

    /**
     *  Set of parameters to check at the end
     */
    Set<Parameter> endStackParameters = new HashSet<>();


    Map<Integer, Set<ComplexParameter>> branchStackComplexParameters = new HashMap<>();

    // Parameters in the rule to check in the meantime
    // (stack id -> data structure)
    // std::map<int, std::set<Parameter *> > branchStackParameters;
    // Parameters in the rule to check in the meantime
    // (negation id -> data structure)
    // std::map<int, std::set<Parameter *> > negationParameters;

    /**
     * Parameters in the rule to check in the meantime
     *(negation id -> data structure)
    **/
    Map<Integer, Set<ComplexParameter>> negationComplexParameters = new HashMap<>();

    /**
     *  Parameters in the rule to check in the meantime
     *  (aggregate id -> data structure)
    **/
    Map<Integer, Set<ComplexParameter>> aggregateComplexParameters= new HashMap<>();

    /**
     *  Aggregate in the rule (aggregate id -> data structure)
     */
    Map<Integer, TAggregate> aggregates= new HashMap<>();

    /**
     *  Negations in the rule (negation id -> data structure)
     */
    Map<Integer, Negation> negations= new HashMap<>();

    /**
     *  Number of stacks in the rule
     */
    Integer stacksNum;

    /**
     *  Number of aggregates in the rule
     */
    Integer aggrsNum;

    /**
     *  Number of negations in the rule
     */
    Integer negsNum;

    /**
     *  Stack id -> state it refers to in the rule
     */
    Map<Integer, Integer> referenceState = new HashMap<>();

    /**
     *  Number of pkts stored for each stack in the rule
     */
    Integer stacksSize[] = new Integer[MAX_RULE_FIELDS];

    /**
     *  Number of pkts stored for each negation in the rule
     */
    Integer negsSize[] = new Integer[MAX_RULE_FIELDS];

    /**
     *  Number of pkts stored for each aggregate in the sequence
     */
    Integer aggsSize[] = new Integer[MAX_RULE_FIELDS];

    /**
     *  Aggregate index -> set of all matching PubPkt
     */
    Map<Integer,CopyOnWriteArrayList<PubPkt>> receivedAggs = new  Hashtable<>();

    /**
     *  Stack index -> set of all matching PubPkt
     */
    Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedPkts = new Hashtable<>();

    /**
     *  Negation index -> set of all matching PubPkt
     */
    Map<Integer, CopyOnWriteArrayList<PubPkt>> receivedNegs = new Hashtable<>();

    /**
     *  Indexes of events in the consuming clause (set of stack ids)
     */
    Set<Integer> consumingIndexes = new HashSet<>();

    /**
     *  Used to generate composite event attributes (if any)
     */
    CompositeEventGenerator eventGenerator = new CompositeEventGenerator();

    /**
     *  Used to generate a composite event id no attributes are defined
     */
    Integer compositeEventId;

    /**
     * A logger to log T-Rex related info.
     */
    final static org.apache.log4j.Logger lOGGER = org.apache.log4j.Logger.getLogger(StacksRule.class);

    /**
     * Constructor: sets the ruleID and build stacks
     * from RulePkt passed as parameter
     */
    public StacksRule(RulePkt pkt){
        // Initializes the rule identifier
        ruleId = pkt.getRuleId();
        rulePkt = pkt;
        eventGenerator =
                new CompositeEventGenerator(pkt.getEventTemplate());
        if (pkt.getEventTemplate().getAttributesNum() +
                pkt.getEventTemplate().getStaticAttributesNum() ==
                0) {
            compositeEventId = pkt.getEventTemplate().getEventType();
        } else {
            compositeEventId = -1;
        }
        stacksNum = 0;
        aggrsNum = 0;
        negsNum = 0;
        // Initialize stacks map with predicate and fills it with references
        for (int i = 0; i < pkt.getPredicatesNum(); i++) {
            stacksSize[i] = 0;
            Stack tmpStack =
                    new Stack(pkt.getPredicates(i).getRefersTo(), pkt.getPredicates(i).getWin(),
                            pkt.getPredicates(i).getKind());
            stacks.put(stacksNum, tmpStack);
            //List<PubPkt> emptySet = Collections.synchronizedList(new ArrayList<PubPkt>());// new ArrayList<>();
            CopyOnWriteArrayList<PubPkt> emptySet = new CopyOnWriteArrayList<PubPkt>();
            synchronized (receivedPkts.values()) {
                receivedPkts.put(stacksNum, emptySet);
            }
            stacksNum++;
            int refersTo = pkt.getPredicates(i).getRefersTo();
            if (refersTo != -1) {
                stacks.get(refersTo).addLookBackTo(stacksNum -1);
                referenceState.put(i, refersTo);
            }
        }
        // Initialize negations and fills stacks with references
        for (int i = 0; i < pkt.getNegationsNum(); i++) {
            negsSize[i] = 0;
            addNegation(pkt.getNegation(i).getEventType(), (Constraint[]) pkt.getNegation(i).getConstraints().toArray(),
                    pkt.getNegation(i).constraintsNum(), pkt.getNegation(i).getLowerId(),
                    pkt.getNegation(i).getLowerTime(), pkt.getNegation(i).getUpperId());
        }
        // Initialize aggregates belonging to the rule
        for (int i = 0; i < pkt.getAggregatesNum(); i++) {
            aggsSize[i] = 0;
            addAggregate(
                    pkt.getAggregate(i).getEventType(), (Constraint[])pkt.getAggregate(i).getConstraintsInArray() /*(Constraint[])pkt.getAggregate(i).getConstraints().toArray() */,
                    pkt.getAggregate(i).getConstraints().size(), pkt.getAggregate(i).getLowerId(),
                    pkt.getAggregate(i).getLowerTime(), pkt.getAggregate(i).getUpperId(),
                    pkt.getAggregate(i).getName(), pkt.getAggregate(i).getFun());
        }
        // Initialize parameters belonging to the rule
        //Parameter[] values = pkt.getParameters().values().toArray(new Parameter[pkt.getParametersNum()]);
        for (int i = 0; i < pkt.getParametersNum(); i++) {
//            addParameter(values[i].getEvIndex2(), values[i].getName2(),
//                    values[i].getEvIndex1(), values[i].getName1(),
//                    values[i].getType(), pkt);
            addParameter(pkt.getParameter(i).getEvIndex2(), pkt.getParameter(i).getName2()
            ,pkt.getParameter(i).getEvIndex1(), pkt.getParameter(i).getName1(),
                    pkt.getParameter(i).getType(),pkt);
        }

        for (Integer i = 0; i < pkt.getComplexParametersNum(); i++) {
            addComplexParameter(pkt.getComplexParameters().get(i).getOperation(),
                    pkt.getComplexParameters().get(i).getLeftTree(),
                    pkt.getComplexParameters().get(i).getRightTree(),
                    pkt.getComplexParameters().get(i).getLastIndex(),
                    pkt.getComplexParameters().get(i).getsType(),
                    pkt.getComplexParameters().get(i).getValueType());
        }
        // Initialize the set of consuming indexes
        Collection<Integer> cons = pkt.getConsuming();
        for (Integer it :
                cons) {
            consumingIndexes.add(it);
        }
    }


    /**
     * Returns the rule id
     */
    public Integer getRuleId() { return ruleId; }

    /**
     * Adds the received packet to the aggregate with the given index
     */
    public void addToAggregateStack(PubPkt pkt, Integer index){
//        ArrayList<PubPkt>[] values = receivedAggs.values().toArray(new ArrayList[receivedAggs.size()]);
//        parametricAddToStack(pkt, aggsSize[index], values[index]);
        synchronized (receivedAggs.get(index)) {
            parametricAddToStack(pkt, aggsSize[index], receivedAggs.get(index));
            aggsSize[index]++;
        }
    }

    /**
     * Adds the received packet to the negation with the given index
     */
    public void addToNegationStack(PubPkt pkt, Integer index){
        //ArrayList<PubPkt>[] values = receivedNegs.values().toArray(new ArrayList[receivedNegs.size()]);
        //parametricAddToStack(pkt, negsSize[index], values[index]);
        synchronized (receivedNegs.get(index)) {
            parametricAddToStack(pkt, negsSize[index], receivedNegs.get(index));
            negsSize[index]++;
        }
    }

    /**
     * Adds the packet to the given index.
     * Index must be different from 0
     */
    public void addToStack(PubPkt pkt, Integer index){
//        ArrayList<PubPkt>[] values = receivedPkts.values().toArray(new ArrayList[receivedPkts.size()]);
//        parametricAddToStack(pkt, stacksSize[index], values[index]);
        synchronized (receivedPkts.get(index)) {
            parametricAddToStack(pkt, stacksSize[index], receivedPkts.get(index));
            stacksSize[index]++;
        }
    };

    /**
     * Adds the given packet to stack 0 and starts
     * the computation of composite events.
     */
    synchronized public void startComputation(PubPkt pkt, Set<PubPkt> results) {
        // Adds the terminator to the last stack
        pkt.incRefCount();
        synchronized (receivedPkts.get(0)) {
                synchronized (receivedPkts.get(0)) {
                    receivedPkts.get(0).add(pkt);
                    //}
                    //ArrayList<PubPkt>[] receivedPktsList = receivedPkts.values().toArray(new ArrayList[receivedPkts.size()]);
                    //receivedPktsList[0].add(pkt);
                    stacksSize[0] = 1;
                    // Removes expired events from stacks
                    //clearStacks();
                    // Gets partial results (patterns)
                    CopyOnWriteArrayList<PartialEvent> partialResults = getPartialResults(pkt);
                    // Checks parameters and removes invalid results from collected ones

                    removePartialEventsNotMatchingParameters(partialResults, endStackParameters);

                    // Creates complex events and adds them to the results
                    createComplexEvents(partialResults, results);
                    // Deletes consumed events
                    removeConsumedEvent(partialResults);
                    // Deletes partial results
                    deletePartialEvents(partialResults);
                    // Removes the terminator from the last stack

                    receivedPkts.get(0).clear();

                    //receivedPktsList[0].clear();
                    if (pkt.decRefCount())
                        pkt = null;
                    stacksSize[0] = 0;
                }
            }
        }


    /**
     * Process pkt: used only for testing purpose
     */
    public void processPkt(PubPkt pkt, MatchingHandler mh, Set<PubPkt> results,
                    Integer index){
        Set<Integer> aggIt = mh.getMatchingAggregates().get(index);
        Integer[] aggItArray = new Integer[aggIt.size()];
        if( aggIt != null){
            for (int it = 0 ; it <= aggItArray.length; it++){
                int aggIndex = it;
                addToAggregateStack(pkt, aggIndex);
            }
        }
        Set<Integer> negIt = mh.getMatchingNegations().get(index);
        Integer[] negItArray = new Integer[negIt.size()];
        if( negIt != null){
            for (int it = 0 ; it <= negItArray.length; it++){
                int negIndex = it;
                addToAggregateStack(pkt, negIndex);
            }
        }
        Set<Integer> stateIt = mh.getMatchingStates().get(index);
        Integer[] stateItArray = new Integer[stateIt.size()];
        if( stateIt != null){
            Boolean lastStack = false;
            for (int it = 0 ; it <= stateItArray.length; it++){
                int stateIndex = it;
                if(stateIndex != 0){
                    addToStack(pkt, stateIndex);
                } else {
                    lastStack = true;
                }
                if (lastStack)
                    startComputation(pkt, results);
            }
        }
    }

    /**
     * Adds the packet to the given stack (can be a normal stack, or a stack for
     * negations or aggregates)
     * TODO TimeMs timeStamp -> Long
     */
    void parametricAddToStack(PubPkt pkt, Integer parStacksSize,
                              CopyOnWriteArrayList<PubPkt> parReceived){
        Long timeStamp = pkt.getTimeStamp();
        int i = getFirstValidElement(parReceived, parStacksSize, timeStamp);
        if (i == -1) {
            parStacksSize++;
            parReceived.add(pkt);
            pkt.incRefCount();
        } else {
            parStacksSize++;
            //ArrayList<PubPkt> vecIt = parReceived;
            //TODO check what happens!!!
            //PubPkt[] vecIt = new PubPkt[parReceived.size()];
            parReceived.add(i, pkt); //insert(vecIt + i, pkt);
            pkt.incRefCount();
        }
    };

    /**
     * Adds a new parameter constraint
     */
    void addParameter(Integer index1, String name1, Integer index2, String name2,
                      Consts.StateType type, RulePkt pkt){
        Parameter tmp = new Parameter();
        tmp.setEvIndex1(index1);
        tmp.setEvIndex2(index2);
        tmp.setType(type);
        tmp.setName1(name1);
        tmp.setName2(name2);
        if (type == STATE) {
            endStackParameters.add(tmp);
        }
    }

    /**
     * Adds a new complex parameter constraint
     */
    void addComplexParameter(Consts.ConstraintOp pOperation, OpTree lTree, OpTree rTree,
                             int lastIdx, Consts.StateType type, Consts.ValType vtype){
        //CPUParameter* tmp = new CPUParameter;
        ComplexParameter tmp = new ComplexParameter();
        tmp.setOperation(pOperation);
        tmp.setLeftTree(lTree.dup());
        tmp.setRightTree(rTree.dup());
        tmp.setLastIndex(lastIdx);
        tmp.setType(vtype);
        tmp.setsType(type);
        switch (type) {
            case STATE:
                branchStackComplexParameters.put(lastIdx,new HashSet<ComplexParameter>());
                branchStackComplexParameters.get(lastIdx).add(tmp);
                break;
            case NEG:
                negationComplexParameters.get(lastIdx).add(tmp);
                break;
            case AGG:
                aggregateComplexParameters.get(lastIdx).add(tmp);
                break;
        }
    };


    //TODO implementation not found!
//    Integer computeIntValue(PubPkt pkt, PartialEvent partialEvent, OpTree opTree,
//                        Integer index, Boolean isNeg){};

    /**
     * Adds a new negation to negations map
     */
    void addNegation(Integer eventType, Constraint constraints[], Integer constrLen,
                            Integer lowIndex, Long lowTime, Integer highIndex){
        negations.put(negsNum, new Negation());
        negations.get(negsNum).setEventType(eventType);
        negations.get(negsNum).setConstraintsNum(constrLen);
        negations.get(negsNum).addConstraint(constraints);
        negations.get(negsNum).setLowerId(lowIndex);
        negations.get(negsNum).setLowerTime(lowTime);
        negations.get(negsNum).setUpperId(highIndex);
        CopyOnWriteArrayList<PubPkt> emptyVec = new CopyOnWriteArrayList<PubPkt>();
        //List<PubPkt> emptyVec = Collections.synchronizedList(new ArrayList<PubPkt>());
        receivedNegs.put(negsNum, emptyVec);
        if (lowIndex < 0)
            stacks.get(highIndex).addLinkedNegation(negsNum);
        else
        stacks.get(lowIndex).addLinkedNegation(negsNum);
        negsNum++;
    }

    /**
     * Adds a new aggregate to aggregates map
     */
    void addAggregate(Integer eventType, Constraint constraints[],
                             Integer constrLen, Integer lowIndex, Long lowTime,
                             Integer highIndex, String parName, Consts.AggregateFun fun){
        aggregates.put(aggrsNum, new TAggregate());
        aggregates.get(aggrsNum).setEventType(eventType);
        aggregates.get(aggrsNum).setConstraintsNum(constrLen);
        aggregates.get(aggrsNum).addConstraint(constraints);
        aggregates.get(aggrsNum).setLowerId(lowIndex);
        aggregates.get(aggrsNum).setLowerTime(lowTime);
        aggregates.get(aggrsNum).setUpperId(highIndex);
        aggregates.get(aggrsNum).setFun(fun);
        aggregates.get(aggrsNum).setName(parName);
        CopyOnWriteArrayList<PubPkt> emptyVec = new CopyOnWriteArrayList<>();
        //List<PubPkt> emptyVec = Collections.synchronizedList(new ArrayList<PubPkt>());
        synchronized (receivedAggs) {
            receivedAggs.put(aggrsNum, emptyVec);
            aggrsNum++;
        }
    };

    /**
     * Returns the events that satisfy the stack window with the given from the
     * given time stamp
     */
    synchronized void getWinEvents(List<PartialEvent> results, Integer index,
                      Long tsUp, Consts.CompKind mode,
                      PartialEvent partialEvent) {
        Boolean useComplexParameters = false;
        if (stacksSize[index] == 0) {
            return;
        }
        // Extracts the minimum and maximum element to process.
        // Returns immediately if they cannot be found.
        Long minTimeStamp = tsUp - stacks.get(index).getWin();
        synchronized (stacksSize[index]){
        synchronized (receivedPkts.get(index)) {
            int index1 = getFirstValidElement(receivedPkts.get(index), stacksSize[index],
                    minTimeStamp);

            if (index1 < 0)
                return;
            //fixme there was a bug on c++ code!
//        if (receivedPkts[index][index1]->getTimeStamp() >= tsUp)
//        return;
            int index2 =
                    getLastValidElement(receivedPkts.get(index), stacksSize[index], tsUp, index1);
            if (index2 < 0)
                index2 = index1;


            Set<ComplexParameter> itComplex = new HashSet<ComplexParameter>();
            itComplex = branchStackComplexParameters.get(index);
            if (itComplex != null) {
                if (!itComplex.isEmpty())
                    useComplexParameters = true;
            }
            // Computes the indexes for processing
            int count = 0;
            int endCount = index2 - index1;
            // In the case of a LAST_WITHIN semantics, reverses processing order
            if (mode == LAST_WITHIN) {
                count = index2 - index1;
                endCount = 0;
            }
            // Starts processing
            while (true) {
                Boolean usable = true;
                PubPkt tmpPkt = receivedPkts.get(index).get(index1 + count);
                if (useComplexParameters)
                    usable = checkComplexParameters(tmpPkt, partialEvent, itComplex, index,
                            STATE);
                if (usable) {
                    PartialEvent newPartialEvent = new PartialEvent();
                    newPartialEvent.setIndexesCp(partialEvent.getIndexes());
                    newPartialEvent.addToIndex(tmpPkt, index);
                    // Check negations
                    Boolean invalidatedByNegations = false;

                    Set<Integer> negation = stacks.get(index).getLinkedNegations();
                    if (!negation.isEmpty()) {
                        for (int i = 0; i <= negation.size(); i++) {
                            int neg = i;
                            if (checkNegation(neg, newPartialEvent)) {
                                invalidatedByNegations = true;
                                break;
                            }
                        }
                    }


                    // If it is not invalidated by events, add the new partial event to
                    // results, otherwise delete it
                    if (!invalidatedByNegations) {
                        results.add(newPartialEvent);
                        if (mode == LAST_WITHIN || mode == FIRST_WITHIN)
                            break;
                    } else {
                        newPartialEvent = null;
                    }
                }
                // Updates index (increasing or decreasing, depending from the semantics)
                // and check termination condition
                if (mode == LAST_WITHIN) {
                    count--;
                    if (count < endCount)
                        return;
                } else {
                    count++;
                    if (count > endCount)
                        return;
                }
            }
        }
    }
    }

    /**
     * Return true for the presence of negation (according to parameters)
     */
    Boolean checkNegation(Integer negIndex, PartialEvent partialResult){
        Negation neg = negations.get(negIndex);
        // No negations: return false
        if (negsSize[negIndex] == 0)
            return false;
        // Extracts timestamps and indexes
        Long maxTS = partialResult.getIndexes(neg.getUpperId()).getTimeStamp();
        Long minTS;
        if (neg.getLowerId() < 0) {
            minTS = maxTS - neg.getLowerTime();
        } else {
            minTS = partialResult.getIndexes(neg.getLowerId()).getTimeStamp();
        }
        int index1 =
                getFirstValidElement(receivedNegs.get(negIndex), negsSize[negIndex], minTS);
        // TODO: Aggiungere la seguente riga per avere uguaglianza semantica
        // con TRex nel test Rain.
        // if (receivedNegs[negIndex][0]->getTimeStamp()<=maxTS &&
        // receivedNegs[negIndex][0]->getTimeStamp()>=minTS) return true;
        if (index1 < 0)
            return false;
        // maxTS and minTS negation events are not valid; Jan 2015
        if (receivedNegs.get(negIndex).get(index1).getTimeStamp() >= maxTS)
        return false;
        int index2 = getLastValidElement(receivedNegs.get(negIndex), negsSize[negIndex],
                maxTS, index1);
        if (index2 < 0)
            index2 = index1;

        Set<ComplexParameter> itComplex = negationComplexParameters.get(negIndex);
        if (itComplex.isEmpty())
            return true;
        // Iterates over all negations and over all parameters.
        // If a negation can be found that satisfies all parameters,
        // then return true, otherwise return false
        for (int count = 0; count <= index2 - index1; count++) {
            PubPkt tmpPkt = receivedNegs.get(negIndex).get(index1 + count);
            if (checkComplexParameters(tmpPkt, partialResult, itComplex, negIndex,
                    NEG))
                return true;
        }
        return false;
    };

    /**
     * Computes partial results and returns them as a list of PartialEvent.
     */
    synchronized CopyOnWriteArrayList<PartialEvent> getPartialResults(PubPkt pkt){
        CopyOnWriteArrayList<PartialEvent> prevEvents =  new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<PartialEvent> currentEvents = new CopyOnWriteArrayList<>();
        PartialEvent last = new PartialEvent();
        last.addToIndex(pkt ,0);
        prevEvents.add(last);
        // Checks negations on the first state
        Set<Integer> negation = stacks.get(0).getLinkedNegations();
        if (!negation.isEmpty()){
            for (int it = 0; it < negation.size() ; it++) {
            int neg = it;
                if (checkNegation(neg, last)){
                    last = null;
                    prevEvents = null;
                    return currentEvents;
                }
            }
        }

        // Iterates over all states
        for (int state = 1; state < stacksNum; state++) {
            Stack stack = stacks.get(state);
            // Iterates over all previously generated events
            List<PartialEvent> listIt = prevEvents;
            //PartialEvent[] listItArray = listIt.toArray(new PartialEvent[listIt.size()]);
            for (PartialEvent pe :
                    listIt) {
                PartialEvent event = pe;
                // Extract events for next iteration
                int refState = referenceState.get(state);
                Long maxTimeStamp = event.getIndexes(refState).getTimeStamp();
                Consts.CompKind kind = stack.getKind();
                getWinEvents(currentEvents, state, maxTimeStamp, kind, event);
            }
            // Swaps current and previous results and deletes previous ones
            for (PartialEvent it :
                    prevEvents) {
                PartialEvent pe = it;
                //pe =null;
            }
            prevEvents.clear();
            CopyOnWriteArrayList<PartialEvent> tmp = prevEvents;
            prevEvents = currentEvents;
            currentEvents = tmp;
            if (prevEvents.isEmpty())
                break;
        }
        currentEvents = null;
        return prevEvents;
    }

    /**
     * Computes complex events and adds them to the results set
     */
    void createComplexEvents(List<PartialEvent> partialEvents,
                                    Set<PubPkt> results){
        List<PartialEvent> it = partialEvents;
        for (PartialEvent pe:
             it) {
            PartialEvent pe1 = pe;
            PubPkt genPkt = new PubPkt();
            if (compositeEventId >= 0) {
            genPkt = new PubPkt(compositeEventId, null, 0);
                synchronized (receivedPkts.get(0)){
            //genPkt.setTimeStamp(receivedPkts.get(0).get(0).getTimeStamp());
                    genPkt.setTimeStamp(System.currentTimeMillis());
                }
        } else{
                synchronized (receivedAggs) {
                    genPkt = eventGenerator.generateCompositeEvent(
                            pe1, aggregates, aggsSize, receivedPkts, receivedAggs,
                            aggregateComplexParameters);
                }
        }
            results.add(genPkt);
            final Long time = System.currentTimeMillis();
            //System.out.println("Complex Event is: " + genPkt.toString());
            JsonObject event = new JsonObject();
            event.add("event", new JsonParser().parse(genPkt.toString()));
            //lOGGER.info(event.toString());
        }
    }

    /**
     * Removes events that have been consumed
     */
    void removeConsumedEvent(List<PartialEvent> partialEvents) {
        if (consumingIndexes.isEmpty())
            return;
        for (int i = 1; i < stacksNum; i++) {
            if (consumingIndexes.contains(i))
                continue;
            Set<PubPkt> pktsToRemove = null;
            for (PartialEvent it :
                    partialEvents) {
                PartialEvent pe = it;
                PubPkt pkt = pe.getIndexes(i);
                if (!pktsToRemove.contains(pkt)) {
                    pktsToRemove.add(pkt);
                }
            }
            synchronized (receivedPkts.get(i)) {
                List<PubPkt> mapIt = receivedPkts.get(i);

                for (PubPkt it :
                        mapIt) {
                    PubPkt pkt = it;
                    if (pktsToRemove.contains(pkt)) {
                        it = null;
                        if (pkt.decRefCount())
                            pkt = null;
                        stacksSize[i]--;
                    }
                }
            }
        }
    }
    /**
     * Deletes partial events
     */
    void deletePartialEvents(List<PartialEvent> partialEvents){
        for (PartialEvent it :
                partialEvents) {
            PartialEvent pe = it;
            pe = null;
        }
        partialEvents = null;
    }

//    public static Consts.ValType checkParametertype1 = null, checkParametertype2 = null;
//    public static Integer checkParameterindex1 = null, checkParameterindex2 = null;


    public class CheckParam{
        public  Consts.ValType checkParametertype1, checkParametertype2;
        public  Integer checkParameterindex1 = new Integer(0), checkParameterindex2= new Integer(0);
        public String result1 = new String();// = new char[STRING_VAL_LEN];
        public String result2 = new String();// = new char[STRING_VAL_LEN];

        public  Consts.ValType checkParametertype3, checkParametertype4;
        public  Integer checkParameterindex3 = new Integer(0), checkParameterindex4= new Integer(0);
        public Geometry result3;// = new char[STRING_VAL_LEN];
        public Geometry result4;// = new char[STRING_VAL_LEN];
    }

    /**
     * Returns true if the parameter is satisfied by the packet
     */
    Boolean checkParameter(PubPkt pkt, PartialEvent partialEvent,
                               Parameter parameter){
        Integer indexOfReferenceEvent = parameter.getEvIndex1();
        PubPkt receivedPkt = partialEvent.getIndexes(indexOfReferenceEvent);
//        Consts.ValType type1 = null, type2 = null;
//        Integer index1 = null, index2 = null;
        CheckParam checkParam = new CheckParam();
        if (!receivedPkt.getAttributeIndexAndType2(parameter.getName2(), checkParam.checkParameterindex2, checkParam.checkParametertype2, checkParam))
            return false;
        if (!pkt.getAttributeIndexAndType1(parameter.getName1(), checkParam.checkParameterindex1, checkParam.checkParametertype1, checkParam))
            return false;
        if (checkParam.checkParametertype1 != checkParam.checkParametertype2)
            return false;
        switch (checkParam.checkParametertype1) {
            case INT:
                return receivedPkt.getIntAttributeVal(checkParam.checkParameterindex2) ==
                        pkt.getIntAttributeVal(checkParam.checkParameterindex1);
            case FLOAT:
                return receivedPkt.getFloatAttributeVal(checkParam.checkParameterindex2) ==
                        pkt.getFloatAttributeVal(checkParam.checkParameterindex1);
            case BOOL:
                return receivedPkt.getBoolAttributeVal(checkParam.checkParameterindex2) ==
                        pkt.getBoolAttributeVal(checkParam.checkParameterindex1);
            case STRING:
                String result1 = "";// = new char[STRING_VAL_LEN];
                String result2 = "";// = new char[STRING_VAL_LEN];
                result1 = receivedPkt.getStringAttributeVal(checkParam.checkParameterindex2, checkParam.result2);
                result2 = pkt.getStringAttributeVal(checkParam.checkParameterindex1, checkParam.result1);

                return result1.equals(result2);
            case GEOMETRY:
                Geometry result3;// = new char[STRING_VAL_LEN];
                Geometry result4;// = new char[STRING_VAL_LEN];
                result3 = receivedPkt.getGeometryAttributeVal(checkParam.checkParameterindex4, checkParam.result4);
                result4 = pkt.getGeometryAttributeVal(checkParam.checkParameterindex3, checkParam.result3);

                return result3.equals(result4);
            default:
                return false;
        }
    }

    // inline bool checkComplexParameter(PubPkt *pkt, PartialEvent
    // *partialEvent, CPUParameter *parameter, int index, bool isNeg);

    /**
     * Returns true if all parameters are satisfied by the packet
     * CPUParameter -> Complex Parameter
     */
    Boolean checkComplexParameters(PubPkt pkt, PartialEvent partialEvent,
                                Set<ComplexParameter> complexParameters,
                                Integer index, Consts.StateType sType){
        for (ComplexParameter it :
                complexParameters) {
            ComplexParameter par = it;
            if (!checkComplexParameter(pkt, partialEvent, par, index, sType))
                return false;
        }
        return true;
    }

    /**
     * Removes partial events that do not match parameters
     */
    synchronized void removePartialEventsNotMatchingParameters(
            CopyOnWriteArrayList<PartialEvent> partialEvents,
            Set<Parameter> parameters) {
            for (PartialEvent it :
                    partialEvents) {
                Boolean valid = true;
                for (Parameter it2 :
                        parameters) {
                    Parameter par = it2;
                    Integer indexOfReferenceEvent = par.getEvIndex2();
                    PartialEvent partialEvent = it;
                    PubPkt receivedPkt = partialEvent.getIndexes(indexOfReferenceEvent);
                    if (!checkParameter(receivedPkt, partialEvent, par)) {
                        valid = false;
                        break;
                    }
                }
                if (!valid)
                    partialEvents.remove(it);
            }
        }


    /**
     * Remove packets invalidated by timing constraints.
     */
    synchronized void clearStacks() {
        for (int stack = 1; stack < stacksNum; stack++) {
            synchronized (stacks.get(stack)){
            int refersToStack = stacks.get(stack).getRefersTo();
            if (stacksSize[refersToStack] == 0)
                continue;
            synchronized (stacksSize[stack]) {
                synchronized (receivedPkts.get(stack)) {
                    synchronized (receivedPkts.get(stack)) {
                        synchronized (receivedPkts.get(refersToStack)) {
                            Long minTS = receivedPkts.get(refersToStack).get(0).getTimeStamp() -
                                    stacks.get(stack).getWin();
                            removeOldPacketsFromStack(minTS, stacksSize[stack], receivedPkts.get(stack));
                        }
                    }
                }
            }
        }
    }
        for (int negIndex = 0; negIndex < negsNum; negIndex++) {
            Negation neg = negations.get(negIndex);
            int refersToStack = neg.getUpperId();
            if (stacksSize[refersToStack] == 0)
                continue;
            Long win;
            if (neg.getLowerId() < 0) {
                win = neg.getLowerTime();
            } else {
                int secondIndex = neg.getLowerId();
                win = stacks.get(secondIndex).getWin();
            }
            synchronized (receivedPkts.get(refersToStack)){
                synchronized (receivedNegs.get(negIndex)) {
                    Long minTS = receivedPkts.get(refersToStack).get(0).getTimeStamp() - win;
                    removeOldPacketsFromStack(minTS, negsSize[negIndex],
                            receivedNegs.get(negIndex));
                }
        }
        }
        for (int aggIndex = 0; aggIndex < aggrsNum; aggIndex++) {
            TAggregate agg = aggregates.get(aggIndex);
            int refersToStack = agg.getUpperId();
            if (stacksSize[refersToStack] == 0)
                continue;
            Long win = agg.getLowerTime();
            if (win < 0) {
                int secondIndex = agg.getLowerId();
                win = stacks.get(secondIndex).getWin();
            }
            synchronized (receivedPkts.get(refersToStack)) {
                Long minTS = receivedPkts.get(refersToStack).get(0).getTimeStamp() - win;

                synchronized (receivedAggs) {
                    removeOldPacketsFromStack(minTS, aggsSize[aggIndex],
                            receivedAggs.get(aggIndex));
                }
            }
        }
    };

    /**
     * Removes all packets that are older than minTS from the given stack.
     * The stack can be a normal stack, or a stack for negations or aggregates.
     */
    synchronized void removeOldPacketsFromStack(Long minTS, Integer parStacksSize,
                                   CopyOnWriteArrayList<PubPkt> parReceived) {
        if (parStacksSize == 0)
            return;
        try {
            int newSize = deleteInvalidElements(parReceived, parStacksSize, minTS);
            if (newSize == parStacksSize)
                return;
            for (PubPkt it : parReceived) {
                PubPkt pkt = it;
                if (pkt.decRefCount())
                    pkt = null;
                parReceived.remove(it);
                if (parReceived.size() == 0)
                    break;
            }
            parStacksSize = newSize;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
