//
// This file is part of T-Rex, a Complex Event Processing Middleware.
// See http://home.dei.polimi.it/margara
//
// Authors: Daniele Rogora
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

/**
 * @author Behnam Khazael
 */


package trex.common;

import trex.common.Consts.ConstraintOp;
import trex.common.Consts.StateType;
import trex.common.Consts.ValType;

public class ComplexParameter {
	private ValType type;
	private OpTree leftTree;
	private OpTree rightTree;
	private ConstraintOp operation;
	private StateType sType;
	private Integer lastIndex;

	
	public ComplexParameter(ConstraintOp op, StateType pSType, ValType vType, OpTree pLeftTree, OpTree pRightTree) {
		this.setOperation(op);
		this.setsType(pSType);
		this.setType(vType);
		this.setLeftTree(pLeftTree);
		this.setRightTree(pRightTree);
	}

	public ComplexParameter(){}
	
	public ValType getValueType() {
		return getType();
	}
	
	public StateType getStateType() {
		return getsType();
	}

	public OpTree getLeftTree() {
		return leftTree;
	}

	public OpTree getRightTree() {
		return rightTree;
	}

	public ConstraintOp getOperation() {
		return operation;
	}

	@Override
	public String toString(){
		return " ComplexParameter: {" +
				"Type of the value: " + this.getValueType() +
				", Left Tree: " + this.getLeftTree().toString() +
				", Right Tree: " + this.getRightTree().toString() +
				", Operation: " + this.getOperation().toString() +
				", Type of the state: " + this.getStateType() +
				"}";
	}

	public ValType getType() {
		return type;
	}

	public void setType(ValType type) {
		this.type = type;
	}

	public void setLeftTree(OpTree leftTree) {
		this.leftTree = leftTree;
	}

	public void setRightTree(OpTree rightTree) {
		this.rightTree = rightTree;
	}

	public void setOperation(ConstraintOp operation) {
		this.operation = operation;
	}

	public StateType getsType() {
		return sType;
	}

	public void setsType(StateType sType) {
		this.sType = sType;
	}

	public Integer getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(Integer lastIndex) {
		this.lastIndex = lastIndex;
	}
}
