package trexengine;


import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import trex.packets.PubPkt;
import trex.packets.RulePkt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Thread.sleep;
import static trex.common.Consts.MAX_RECURSION_DEPTH;

/**
 * Created by sony on 2/7/2020.
 * <p>
 * This is the engine of T-Rex. It creates new automata starting from received
 * rule packets,
 * and processes incoming publications to detect complex events.
 */
public class TRexEngine {

    final static Logger logger = Logger.getLogger(TRexEngine.class);


    /**
     * System total run time with respect to setuptime. in seconds
     */
    private int totalReceivedPackets = 0;

    class processor extends Thread {
        SharedStruct s;
        Integer lowerBound;
        Integer upperBound;
        Boolean first;
        String name;
        private final Object lock = new Object();

        processor(SharedStruct parShared, String name1) {
            s = parShared;
            lowerBound = s.lowerBound;
            upperBound = s.upperBound;
            first = true;
            name = name1;
        }

        //SharedStruct s = (SharedStruct)parShared;
//        Integer lowerBound = s.lowerBound;
//        Integer upperBound = s.upperBound;
//        Boolean first = true;
        @Override
        public void run() {
            synchronized (s) {
                synchronized (lock) {
                    while (true) {
                        try {


                            if (first) {
                                //pthread_mutex_lock(s -> processMutex);
                                first = false;
                            }
                            //pthread_cond_wait(s -> processCond, s -> processMutex);
                            // End processing
                            if (s.finish) {
                                //pthread_mutex_unlock(s -> processMutex);
                                return;
                            }
                            //}
                            // synchronized (s) {
                            /**
                             *  typedef struct MatchingHandlerStruct {
                             std::map<int, std::set<int>> matchingStates;
                             std::map<int, std::set<int>> matchingAggregates;
                             std::map<int, std::set<int>> matchingNegations;
                             } MatchingHandler;

                             **/

                            //pthread_mutex_unlock(s->processMutex);
                            MatchingHandler mh = s.mh;
                            Set<PubPkt> generatedPkts = new HashSet<>();
                            for (int i = lowerBound; i < upperBound; i++) {


                                /**
                                 *Adds Negation to Negation Stack
                                 **/
                                Set<Integer> negsIt = mh.getMatchingNegations().get(i);
                                if (negsIt != null && !negsIt.isEmpty()) {
                                    for (Integer it :
                                            negsIt) {
                                        Integer index = it;
                                        synchronized (s.stacksRule.StacksRules.get(i)) {
                                            s.stacksRule.StacksRules.get(i).addToNegationStack(s.pkt, index);
                                        }
                                    }
                                }

                                /**
                                 * Behnam:
                                 * Adds Aggregations to Aggregate Stack
                                 **/
                                Set<Integer> aggsIt = mh.getMatchingAggregates().get(i);
                                if (aggsIt != null && !aggsIt.isEmpty()) {
                                    for (Integer it :
                                            aggsIt) {
                                        Integer index = it;
                                        synchronized (s.stacksRule.StacksRules.get(i)) {
                                            s.stacksRule.StacksRules.get(i).addToAggregateStack(s.pkt, index);
                                        }
                                    }
                                }

                                /**
                                 * Behnam:
                                 * Adds States to States Stack
                                 **/
                                Set<Integer> statesIt = mh.getMatchingStates().get(i);
                                if (statesIt != null && !statesIt.isEmpty()) {
                                    Boolean lastState = false;
                                    for (Integer it :
                                            statesIt) {
                                        Integer index = it;
                                        if (index != 0) {
                                            synchronized (s.stacksRule.StacksRules.get(i)) {
                                                s.stacksRule.StacksRules.get(i).addToStack(s.pkt, index);
                                            }
                                            //s.stacksRule.get(i).addToStack(s.pkt, index);
                                        } else {
                                            lastState = true;
                                        }
                                    }
                                    /**
                                     * Behnam:
                                     * Start computation if it is the last State
                                     **/
                                    if (lastState) {
                                        synchronized (s.stacksRule.StacksRules.get(i)) {
                                            s.stacksRule.StacksRules.get(i).startComputation(s.pkt, s.result);
                                        }
                                    }

                                }
                            }
                            try {
                                s.wait();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

//                    pthread_mutex_lock(s->processMutex);
//                    pthread_mutex_lock(s->resultMutex);
//                    (s.stillProcessing) = (s.stillProcessing) - 1;
//                    if ((s.stillProcessing) == 0)
//                    pthread_cond_signal(s->resultCond);
//                    pthread_mutex_unlock(s->resultMutex);
    }
    //pthread_exit(NULL); };

    //void processor(){}


    /**
     * Constructor
     */


    public TRexEngine(int parNumProc) {
        numProc = parNumProc;
        Thread threads[] = new Thread[numProc];
        stacksRules = new StacksRules();
        shared = new SharedStruct[numProc];
        for (int i = 0; i < numProc; i++) {
            shared[i] = new SharedStruct();
        }
        recursionNeeded = false;
        final Long time = System.currentTimeMillis();
    }

    /**
     * Creates data structures needed for processing
     */
    public void finalizing() {
        // Creates shared memory and initializes threads
        Integer stillProcessing;
        stillProcessing = numProc;
        int size = stacksRules.getSize() / numProc + 1;
//        pthread_mutex_t* resultMutex = new pthread_mutex_t;
//        pthread_cond_t* resultCond = new pthread_cond_t;
//        pthread_mutex_init(resultMutex, NULL);
//        pthread_cond_init(resultCond, NULL);

        for (int i = 0; i < numProc; i++) {
            shared[i] = new SharedStruct();
            shared[i].lowerBound = size * i;
            shared[i].upperBound = size * (i + 1);
            shared[i].finish = false;
//            shared[i].processMutex = new pthread_mutex_t;
//            shared[i].processCond = new pthread_cond_t;
//            shared[i].resultMutex = resultMutex;
//            shared[i].resultCond = resultCond;
            shared[i].stacksRule = stacksRules;
            shared[i].stillProcessing = stillProcessing;
//            pthread_mutex_init(shared[i].processMutex, NULL);
//            pthread_cond_init(shared[i].processCond, NULL);
            processor thread = new processor(shared[i], "Thread " + i);
            thread.start();
            //pthread_create(&threads[i], NULL, processor, (void*)&shared[i]);
        }
        try {
            sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ;

    /**
     * Processes the given rule pkt
     */
    public void processRulePkt(RulePkt pkt) {
        System.out.println(pkt.toString());
        final Long time = System.currentTimeMillis();
        setRecursionNeeded(pkt);
        StacksRule stacksRule = new StacksRule(pkt);
        stacksRules.StacksRules.put(stacksRule.getRuleId(), stacksRule);
        indexingTable.installRulePkt(pkt);
        //pkt = null;
    }

    ;

    /**
     * Processes the given publication pkt
     */
    public void processPubPkt(PubPkt pkt) {
        processPubPkt(pkt, false);
    }

    /**
     * Processes the given publication pkt
     */
    public JsonObject processPubPktTime(PubPkt pkt) {
        return processPubPkt(pkt, false);
    }

    ;

    /**
     * Adds a new ResultHandler
     */
    public void addResultListener(ResultListener resultListener) {
        resultListeners.add(resultListener);
    }

    /**
     * Removes the given ResultHandler
     */
    public void removeResultListener(ResultListener resultListener) {
        resultListeners.remove(resultListener);
    }

    /**
     * Processes the given publication pkt, but this is called from the engine
     * itself when the pubPkt is needed by other rules
     */

    MatchingHandler mh = new MatchingHandler();

    private static int counter = 0;
    public JsonObject processPubPkt(PubPkt pkt, Boolean recursion) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("PublicationPacket", pkt.toString());
        if (recursion == false)
            recursionDepth = 0;
        else
            recursionDepth++;
        /**
         * Behnam:
         * Logger can added here for computing the process time.
         **/
        // Data structures used to compute mean processing time
//        timeval tValStart, tValEnd;
//        gettimeofday(&tValStart, NULL);
        counter++;

        totalReceivedPackets = totalReceivedPackets + 1;
        Long start = System.nanoTime();

        // Obtains the set of all interested sequences and states
        MatchingHandler mh = new MatchingHandler();

        indexingTable.processMessage(pkt, mh);
        Set<PubPkt> result = new HashSet<>();

        // Installs information in shared memory
        for (int i = 0; i < numProc; i++) {
            synchronized (shared[i]) {
                //pthread_mutex_lock(shared[i].processMutex);

                shared[i].mh = mh;
                shared[i].mh.setMatchingAggregates(mh.getMatchingAggregates());
                shared[i].mh.setMatchingNegations(mh.getMatchingNegations());
                shared[i].mh.setMatchingStates(mh.getMatchingStates());

                //#if MP_MODE == MP_COPY
                //shared[i].pkt = new PubPkt(pkt);
                //#elif MP_MODE == MP_LOCK
                shared[i].pkt = pkt;

                //#endif
                //pthread_cond_signal(shared[i].processCond);
                //pthread_mutex_unlock(shared[i].processMutex);
                //synchronized (shared[i]) {
                shared[i].notifyAll();
            }
            //synchronized(shared[i].mh) { shared[i].mh.notifyAll(); }
        }

        // Waits until all processes finish
        //pthread_mutex_lock(shared[0].resultMutex);
        // If not all thread have finished, wait until last one
        synchronized (shared[0]) {
            if ((shared[0].stillProcessing) != 0)
                //pthread_cond_wait(shared[0].resultCond, shared[0].resultMutex);
                //pthread_mutex_unlock(shared[0].resultMutex);
                shared[0].stillProcessing = numProc;
        }
        // Collects results
        synchronized (shared[0]) {
            for (int i = 0; i < numProc; i++) {
                for (PubPkt resIt :
                        shared[i].result) {
                    PubPkt resPkt = resIt;
                    result.add(resPkt);
                }

                shared[i].result.clear();
                //#if MP_MODE == MP_COPY
                if (shared[i].pkt.decRefCount()) ;
                //shared[i].pkt = null;
                //#endif
            }
            // }
            // Deletes used packet
            if (pkt.decRefCount())
                pkt = null;
            mh = null;

/**
 * Behnam:
 * Logger can added here for computing the process time.
 **/
//        gettimeofday(&tValEnd, NULL);
//        double duration = (tValEnd.tv_sec - tValStart.tv_sec) * 1000000 +
//                tValEnd.tv_usec - tValStart.tv_usec;
            //logger.info("Engine Mote: " + nodeAddress + " computing End time: " + System.currentTimeMillis());
            Long end = System.nanoTime();
            Long time = System.currentTimeMillis();
            jsonObject.addProperty("packetNumber", counter);
            jsonObject.addProperty("processTime", (end - start));
            jsonObject.addProperty("Action", "process_time");
            //logger.info("{\"packetNumber\":" + counter + ", \"processTime\":" + (end - start) + "}");
            //logger.info(jsonObject.toString());
            // Notifies results to listeners
            for (ResultListener it :
                    resultListeners) {
                ResultListener listener = it;
                //listener.handleResult(result, System.nanoTime());
                listener.handleResult(result, time);
            }

            for (PubPkt it :
                    result) {
                PubPkt pkt2 = it;
                if (recursionNeeded && recursionDepth < MAX_RECURSION_DEPTH)
                    processPubPkt(new PubPkt(pkt2), true);
                try {
                    if (pkt != null) {
                        if (pkt.decRefCount()) {
                            pkt = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * This is called when adding a new rule, so that the recursion is enabled
     * only if needed, i.e. if the output of some any rule is taken as input by
     * some other rule
     */
    public void setRecursionNeeded(RulePkt pkt) {
        // If this is already true there's nothing to do here
        if (recursionNeeded == true)
            return;

        for (int i = 0; i < pkt.getPredicatesNum(); i++)
            inputEvents.add(pkt.getPredicates(i).getEventType());
        outputEvents.add(pkt.getEventTemplate().getEventType());
        for (Integer it :
                inputEvents) {
            if (outputEvents.contains(it))
                recursionNeeded = true;
            return;
        }
    }

    ;

    /**
     * This is used to limit the recursion depth, mainly to avoid infinite loop in
     * case of cycles in the events graph
     */
    Integer recursionDepth;
    Boolean recursionNeeded;

    /**
     * Set of events used as input by all the rules installed
     */
    Set<Integer> inputEvents = new HashSet<>();

    /**
     * Set of all the events possibly generated by all the rules installed
     */
    Set<Integer> outputEvents = new HashSet<>();

    // Stored stacks rules, sorted per rule id
    StacksRules stacksRules = new StacksRules();
    // The indexing table to speed up matching
    IndexingTable indexingTable = new IndexingTable();
    // Result listeners associated with the processing engine
    Set<ResultListener> resultListeners = new HashSet<>();
    // Array of threads to use
    //pthread_t threads;
    // Shared Memory
    SharedStruct shared[];
    // Number of processors to use
    Integer numProc = new Integer(0);


    class SharedStruct {

        //    pthread_cond_t processCond;
//    pthread_cond_t resultCond;
//    pthread_mutex_t processMutex;
//    pthread_mutex_t resultMutex;
        Integer stillProcessing;
        Boolean finish;
        Integer lowerBound = new Integer(0);
        Integer upperBound = new Integer(0);
        Set<PubPkt> result = new HashSet<>();
        MatchingHandler mh = new MatchingHandler();
        StacksRules stacksRule = new StacksRules();
        PubPkt pkt = new PubPkt();
    }

    class StacksRules {
        Map<Integer, StacksRule> StacksRules = new HashMap<>();

        int getSize() {
            return StacksRules.size();
        }
    }


}
