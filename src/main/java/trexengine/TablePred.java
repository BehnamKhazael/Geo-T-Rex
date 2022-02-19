package trexengine;

import trex.common.Consts;

/**
 * Created by sony on 2/3/2020.
 *
 * Represents a predicate stored in the index table
 *
 */
public class TablePred {
    private int ruleId;

    private int stateId;

    private int constraintsNum;

    private Consts.StateType stateType;

    /**
     *  Id of the sequence in the automaton
     */
    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * Index of the state in the sequence
     */
    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    /**
     *  Number of constraints in the predicate
     */
    public int getConstraintsNum() {
        return constraintsNum;
    }

    public void setConstraintsNum(int constraintsNum) {
        this.constraintsNum = constraintsNum;
    }

    /**
     *  State, negation, or aggregate
     */
    public Consts.StateType getStateType() {
        return stateType;
    }

    public void setStateType(Consts.StateType stateType) {
        this.stateType = stateType;
    }
}
