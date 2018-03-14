package com.oracle.app.parser.ir;

import com.oracle.app.parser.ir.nodes.*;

public class GoVisitor implements GoIRVisitor {



	@Override
	public Object visitObject(GoBaseIRNode node) {
		System.out.println("Temp node: " + node.toString());
		for(GoBaseIRNode child : node.getChildren())
			if(child != null)
				child.accept(this);
		return null;
	}

	@Override
	public Object visitIdent(GoIRIdentNode node) {
		System.out.println("Ident node: " + node.toString());
		if(node.getChild() != null)
			node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitBinaryExpr(GoIRBinaryExprNode node) {
		System.out.println("Binary node: " + node.toString());
		node.getLeft().accept(this);
		node.getRight().accept(this);
		return null;
	}

	@Override
	public Object visitInvoke(GoIRInvokeNode node) {
		System.out.println("Call node: " + node.toString());
		node.getFunctionNode().accept(this);
		if(node.getArgumentNode() != null){
			node.getArgumentNode().accept(this);
		}
		node.getDispatchNode().accept(this);
		return null;
	}

	@Override
	public Object visitGenericDispatch(GoIRGenericDispatchNode node) {
		System.out.println("Dispatch node: " + node.toString());
		return null;
	}

	@Override
	public Object visitFuncDecl(GoIRFuncDeclNode node) {
		System.out.println("FuncDecl node: " + node.toString());
		node.getName().accept(this);
		if(node.getReceiver() != null){
			node.getReceiver().accept(this);
		}
		node.getType().accept(this);
		if(node.getBody() != null){
			node.getBody().accept(this);
		}
		return null;
	}

	@Override
	public Object visitDecl(GoIRDeclNode node) {
		System.out.println("Decl node: " + node.toString());
		for(GoBaseIRNode child : node.getChildren())
			if(child != null)
				child.accept(this);
		return null;
	}

	@Override
	public Object visitArrayListExpr(GoIRArrayListExprNode node) {
		System.out.println("ArrayListExpr node: " + node.toString());
		for(GoBaseIRNode child : node.getChildren())
			if(child != null)
				child.accept(this);
		return null;
	}

	@Override
	public Object visitBlockStmt(GoIRBlockStmtNode node) {
		System.out.println("Block node: " + node.toString());
		if(node.getChild() != null){
				node.getChild().accept(this);
		}
		return null;
	}

	@Override
	public Object visitExprStmt(GoIRExprStmtNode node) {
		System.out.println("ExprStmt node: " + node.toString());
		node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitExpr(GoIRExprNode node) {
		System.out.println("Expr[] node: " + node.toString());
		node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitStmt(GoIRStmtNode node) {
		System.out.println("Stmt[] node: " + node.toString());
		for(GoBaseIRNode child : node.getChildren())
			if(child != null)
				child.accept(this);
		return null;
	}

	@Override
	public Object visitUnary(GoIRUnaryNode node) {
		System.out.println("Unary node: " + node.toString());
		node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitDeclStmt(GoIRDeclStmtNode node) {
		System.out.println("DeclStmt node: " + node.toString());
		node.getChild().accept(this);
		return null;
	}
	
	public Object visitGenDecl(GoIRGenDeclNode node){
		System.out.println("GenDecl node: "+ node.toString());
		node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitValueSpec(GoIRValueSpecNode node) {
		System.out.println("ValueSpec node: "+ node.toString());
		node.getNames().accept(this);
		if(node.getType() != null){
			node.getType().accept(this);
		}
		if(node.getExpr() != null){
			node.getExpr().accept(this);
		}
		return null;
	}

	@Override
	public Object visitCaseClause(GoIRCaseClauseNode node) {
		System.out.println("CaseClause node: " + node.toString());
		if(node.getBody() != null){
			node.getBody().accept(this);
		}
		if(node.getList() != null){
			node.getList().accept(this);
		}
		return null;
	}

	public Object visitForLoop(GoIRForNode node) {
		System.out.println("For node: "+ node.toString());
		if(node.getInit() != null)
			node.getInit().accept(this);
		if(node.getCond() != null)
			node.getCond().accept(this);
		if(node.getPost() != null)
			node.getPost().accept(this);
		node.getBody().accept(this);

		return null;
	}

	@Override
	public Object visitSwitchStmt(GoIRSwitchStmtNode node) {
		System.out.println("SwitchStmt node: " + node.toString());
		if(node.getInit() != null){
			node.getInit().accept(this);
		}
		if(node.getTag() != null){
			node.getTag().accept(this);
		}
		if(node.getBody() != null){
			node.getBody().accept(this);
		}
		return null;
	}

	@Override
	public Object visitIfStmt(GoIRIfStmtNode node) {
		System.out.println("IfStmt node: "+ node.toString());
		if(node.getInit() != null){
			node.getInit().accept(this);
		}
		if(node.getCond() != null){
			node.getCond().accept(this);
		}
		if(node.getBody() != null){
			node.getBody().accept(this);
		}
		if(node.getElse() != null){
			node.getElse().accept(this);
		}
		return null;
	}

	public Object visitIncDecStmt(GoIRIncDecStmtNode node) {
		System.out.println("IncDec node: "+ node.toString());
		node.getChild().accept(this);
		return null;
	}

	@Override
	public Object visitBranchStmt(GoIRBranchStmtNode node) {
		System.out.println("BranchStmt node: "+ node.toString());
		if(node.getChild() != null)
			node.getChild().accept(this);
		return null;
	}


}
