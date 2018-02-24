package com.oracle.app.parser.ir.nodes;

import java.util.ArrayList;

import com.oracle.app.parser.ir.GoBaseIRNode;
import com.oracle.app.parser.ir.GoTruffle;
import com.oracle.app.parser.ir.GoVisitor;

public class GoIRBinaryExprNode extends GoBaseIRNode {
	
	private String op;
	
	private GoBaseIRNode left;
	private GoBaseIRNode right;
	
	public GoIRBinaryExprNode(String op, GoBaseIRNode left, GoBaseIRNode right) {
		super("BinaryExpr");
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public void setChildParent() {
		left.setParent(this);
		right.setParent(this);
	}
	
	@Override
	public ArrayList<GoBaseIRNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getOp() {
		return op;
	}
	
	@Override
	public void accept(GoVisitor visitor) { 
		visitor.visitBinaryExpr(this); 
	}
	
	@Override
	public void accept(GoTruffle visitor) { 
		visitor.visitBinaryExpr(this); 
	}
}
