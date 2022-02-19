package trexengine;

import trex.common.Consts;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sony on 2/3/2020.
 */
public class Stack {
    private int refersTo;

    private Long win;

    private Consts.CompKind kind;

    private Set<Integer> lookBackTo;

    private Set<Integer> linkedNegations;

    public final Object lock = new Object();


    /**
     * Constructor
     */
    public Stack(int refersTo, Long win, Consts.CompKind kind) {
        this.setRefersTo(refersTo);
        this.setKind(kind);
        this.setWin(win);
        setLookBackTo(new HashSet<Integer>());
        setLinkedNegations(new HashSet<Integer>());
    }

    /**
     * Get referred Stacks
     */
    public int getRefersTo() {
        return refersTo;
    }

    public void setRefersTo(int refersTo) {
        this.refersTo = refersTo;
    }

    /**
     * The maximum time window to look back in the previous column
     */
    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    /**
     * The kind of composition
     */
    public Consts.CompKind getKind() {
        return kind;
    }

    public void setKind(Consts.CompKind kind) {
        this.kind = kind;
    }

    /**
     * Get referred Stacks
     */
    public Set<Integer> getLookBackTo() {
        return lookBackTo;
    }

    public void setLookBackTo(Set<Integer> lookBackTo) {
        this.lookBackTo = lookBackTo;
    }


    /**
     * Get referred Negations
     */
    public Set<Integer> getLinkedNegations() {
        return linkedNegations;
    }

    public void setLinkedNegations(Set<Integer> linkedNegations) {
        this.linkedNegations = linkedNegations;
    }


    /**
     * Add a new referred Negation
     */
    void addLinkedNegation(int reference) {
        linkedNegations.add(reference);
    }

    /**
     * Add a new referred Stack
     */
    void addLookBackTo(int reference) {
        lookBackTo.add(reference);
    }
}
