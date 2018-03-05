package com.oracle.app.parser.ir.nodes;

import java.util.ArrayList;

import com.oracle.app.parser.ir.GoBaseIRNode;
import com.oracle.app.parser.ir.GoIRVisitor;

public class GoIRForNode extends GoBaseIRNode {
	
	private GoBaseIRNode init;
	
	private GoBaseIRNode cond;
	
	private GoBaseIRNode post;
	
	private GoBaseIRNode body;
	
	public GoIRForNode(GoBaseIRNode init, GoBaseIRNode cond, GoBaseIRNode post, GoBaseIRNode body) {
		super("For Loop");
		this.init = init;
		this.cond = cond;
		this.post = post;
		this.body = body;
	}
	
	@Override
	public void setChildParent() {
		init.setParent(this);
		cond.setParent(this);
		post.setParent(this);
		body.setParent(this);
	}
	
	@Override
	public ArrayList<GoBaseIRNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GoBaseIRNode getInit() { return init; }
	
	public GoBaseIRNode getCond() { return cond; }
	
	public GoBaseIRNode getPost() { return post; }
	
	public GoBaseIRNode getBody() { return body; }
	
	@Override
	public Object accept(GoIRVisitor visitor) { 
		return visitor.visitForLoop(this); 
	}

}