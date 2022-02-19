package trex.common;

import trex.packets.PubPkt;

import java.util.Arrays;
import java.util.List;

import static trex.common.Consts.MAX_RULE_FIELDS;


/**
 *
 * @author Behnam Khazael
* Partial sequence of events, as used during processing
 *
 */
public class PartialEvent {
    private PubPkt[] indexes = new PubPkt[MAX_RULE_FIELDS];

    public PubPkt getIndexes(int index) {
        return getIndexes()[index];
    }

    public PubPkt[] getIndexes() {
        return indexes;
    }

    public void setIndexesCp(PubPkt[] indexes) {
        PubPkt[] tmp = new PubPkt[MAX_RULE_FIELDS];
        for (int i=0; i<indexes.length; i++)
            tmp[i] = indexes[i];
        this.indexes = tmp;
    }

    public void setIndexes(PubPkt[] indexes) {
        this.indexes = indexes;
    }

    public void addToIndex(PubPkt pkt, int index){
        indexes[index] = pkt;
//        List<PubPkt> list = Arrays.asList(indexes);
//        list.add(index, pkt);
    }
}
