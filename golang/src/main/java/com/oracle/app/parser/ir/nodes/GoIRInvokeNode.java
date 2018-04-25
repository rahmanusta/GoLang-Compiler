package com.oracle.app.parser.ir.nodes;

import com.oracle.app.parser.ir.GoBaseIRNode;
import com.oracle.app.parser.ir.GoIRVisitable;
import com.oracle.app.parser.ir.GoIRVisitor;

public class GoIRInvokeNode extends GoBaseIRNode implements GoIRVisitable {

	GoBaseIRNode functionNode;
	GoIRArrayListExprNode argumentNodes;
	String lparen;
	String ellipsis;
	String rparen;
	int numReturns;// the number of returns expect, like from example: var x,y = vals() expects 2
	
	public GoIRInvokeNode(GoBaseIRNode functionNode, GoIRArrayListExprNode argumentNodes,String lparen,String ellipsis,String rparen) {
		super("Call Expr (Invoke)");
		this.functionNode = functionNode;
		this.argumentNodes = argumentNodes;
		this.lparen = lparen;
		this.ellipsis = ellipsis;
		this.rparen = rparen;
	}
	
	public GoBaseIRNode getFunctionNode() {
		return functionNode;
	}
	
	public int getEndPos(){
		int endpos = Integer.parseInt(rparen.split(":")[2]);
		return endpos;
	}
	
	public void setNumReturns(int a) {
		numReturns = a;
	}
	
	public int getNumReturns() {
		return numReturns;
	}
	/*
	 * Maybe not needed
	 */
	public GoIRArrayListExprNode getArgumentNode(){
		return argumentNodes;
	}
	
	public int getArgumentsSize(){
		return argumentNodes.getSize();
	}
	
	@Override
	public Object accept(GoIRVisitor visitor) { 
		return visitor.visitInvoke(this); 
	}

}
