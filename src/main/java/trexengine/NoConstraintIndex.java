package trexengine;

import trex.packets.PubPkt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sony on 2/5/2020.
 *
 * Represents the index for predicated without constraints of integer values.
 *
 */
public class NoConstraintIndex extends AbstractConstraintIndex {

    /**
     * Creates or gets the IntTableConstraint C representing the constraint
     * given as parameter. Then it installs the predicate in C.
     */
    void installPredicate(TablePred predicate){predicates.add(predicate);}

    /**
     * Processes the given message, using the partial results stored in predCount.
     * It updates predCount and fills mh with the matching states.
     */
    void processMessage(PubPkt pkt, MatchingHandler mh,
                        Map<TablePred, Integer> predCount){
        for (TablePred it: predicates
             ) {
            TablePred pred = it;
            addToMatchingHandler(mh, pred);
        }
    }

    Set<TablePred> predicates = new HashSet<TablePred>();
}
