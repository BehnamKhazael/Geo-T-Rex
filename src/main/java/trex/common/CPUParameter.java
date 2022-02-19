package trex.common;

/**
 * Created by sony on 2/2/2020.
 *
 * Represents a complex parameter for the CPU engine. It asks operation to be
 * verified between the values (of type vtype) computed analyzing the trees
 *
 */

/**
 * @author Behnam Khazael
 */

public class CPUParameter {
        private Consts.Op operation;
        // Decides whether the parameter should be used during the computation of a
        // normal state, to an aggregate, or to a negation
        private Consts.StateType type;
        private Consts.ValType vtype;
        private OpTree leftTree;
        private OpTree rightTree;
        private int lastIndex;

        public Consts.Op getOperation() {
                return operation;
        }

        public void setOperation(Consts.Op operation) {
                this.operation = operation;
        }

        public Consts.StateType getType() {
                return type;
        }

        public void setType(Consts.StateType type) {
                this.type = type;
        }

        public Consts.ValType getVtype() {
                return vtype;
        }

        public void setVtype(Consts.ValType vtype) {
                this.vtype = vtype;
        }

        public OpTree getLeftTree() {
                return leftTree;
        }

        public void setLeftTree(OpTree leftTree) {
                this.leftTree = leftTree;
        }

        public OpTree getRightTree() {
                return rightTree;
        }

        public void setRightTree(OpTree rightTree) {
                this.rightTree = rightTree;
        }

        public int getLastIndex() {
                return lastIndex;
        }

        public void setLastIndex(int lastIndex) {
                this.lastIndex = lastIndex;
        }
}
