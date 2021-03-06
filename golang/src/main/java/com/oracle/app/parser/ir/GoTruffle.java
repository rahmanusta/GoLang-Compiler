package com.oracle.app.parser.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.oracle.app.GoException;
import com.oracle.app.GoLanguage;
import com.oracle.app.nodes.GoArrayExprNode;
import com.oracle.app.nodes.GoExprNode;
import com.oracle.app.nodes.GoExpressionNode;
import com.oracle.app.nodes.GoFileNode;
import com.oracle.app.nodes.GoIdentNode;
import com.oracle.app.nodes.GoRootNode;
import com.oracle.app.nodes.GoStatementNode;
import com.oracle.app.nodes.SpecDecl.GoImportSpec;
import com.oracle.app.nodes.SpecDecl.GoSelectorExprNodeGen;
import com.oracle.app.nodes.call.GoFieldNode;
import com.oracle.app.nodes.call.GoFuncTypeNode;
import com.oracle.app.nodes.call.GoInvokeNode;
import com.oracle.app.nodes.controlflow.GoBlockNode;
import com.oracle.app.nodes.controlflow.GoBreakNode;
import com.oracle.app.nodes.controlflow.GoCaseClauseNode;
import com.oracle.app.nodes.controlflow.GoContinueNode;
import com.oracle.app.nodes.controlflow.GoForNode;
import com.oracle.app.nodes.controlflow.GoFunctionBodyNode;
import com.oracle.app.nodes.controlflow.GoIfStmtNode;
import com.oracle.app.nodes.controlflow.GoReturnNode;
import com.oracle.app.nodes.controlflow.GoSwitchNode;
import com.oracle.app.nodes.expression.GoAddNodeGen;
import com.oracle.app.nodes.expression.GoBinaryLeftShiftNodeGen;
import com.oracle.app.nodes.expression.GoBinaryRightShiftNodeGen;
import com.oracle.app.nodes.expression.GoBitwiseAndNodeGen;
import com.oracle.app.nodes.expression.GoBitwiseComplementNodeGen;
import com.oracle.app.nodes.expression.GoBitwiseOrNodeGen;
import com.oracle.app.nodes.expression.GoBitwiseXORNodeGen;
import com.oracle.app.nodes.expression.GoCompositeLitNode;
import com.oracle.app.nodes.expression.GoCompositeLitNodeGen;
import com.oracle.app.nodes.expression.GoDivNodeGen;
import com.oracle.app.nodes.expression.GoEqualNodeGen;
import com.oracle.app.nodes.expression.GoGreaterOrEqualNodeGen;
import com.oracle.app.nodes.expression.GoGreaterThanNodeGen;
import com.oracle.app.nodes.expression.GoKeyValueNode;
import com.oracle.app.nodes.expression.GoLessOrEqualNodeGen;
import com.oracle.app.nodes.expression.GoLessThanNodeGen;
import com.oracle.app.nodes.expression.GoLogicalAndNode;
import com.oracle.app.nodes.expression.GoLogicalNotNodeGen;
import com.oracle.app.nodes.expression.GoLogicalOrNode;
import com.oracle.app.nodes.expression.GoMapTypeExprNode;
import com.oracle.app.nodes.expression.GoModNodeGen;
import com.oracle.app.nodes.expression.GoMulNodeGen;
import com.oracle.app.nodes.expression.GoNegativeSignNodeGen;
import com.oracle.app.nodes.expression.GoNotEqualNodeGen;
import com.oracle.app.nodes.expression.GoPositiveSignNodeGen;
import com.oracle.app.nodes.expression.GoSliceExprNode;
import com.oracle.app.nodes.expression.GoStarExpressionNode;
import com.oracle.app.nodes.expression.GoStructTypeExprNode;
import com.oracle.app.nodes.expression.GoSubNodeGen;
import com.oracle.app.nodes.expression.GoUnaryAddressNode;
import com.oracle.app.nodes.local.GoArrayReadNode;
import com.oracle.app.nodes.local.GoArrayReadNodeGen;
import com.oracle.app.nodes.local.GoReadArgumentsNode;
import com.oracle.app.nodes.local.GoReadLocalVariableNode;
import com.oracle.app.nodes.local.GoReadLocalVariableNodeGen;
import com.oracle.app.nodes.local.GoStructPropertyWriteNodeGen;
import com.oracle.app.nodes.local.GoWriteLocalVariableNode;
import com.oracle.app.nodes.local.GoWriteLocalVariableNodeGen;
import com.oracle.app.nodes.types.GoArray;
import com.oracle.app.nodes.types.GoFloat32Node;
import com.oracle.app.nodes.types.GoFloat64Node;
import com.oracle.app.nodes.types.GoFunctionLiteralNode;
import com.oracle.app.nodes.types.GoIntNode;
import com.oracle.app.nodes.types.GoStringNode;
import com.oracle.app.parser.GoTypeCheckingVisitor;
import com.oracle.app.parser.ir.nodes.GoIRArrayListExprNode;
import com.oracle.app.parser.ir.nodes.GoIRArrayTypeNode;
import com.oracle.app.parser.ir.nodes.GoIRAssignmentStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRBinaryExprNode;
import com.oracle.app.parser.ir.nodes.GoIRBlockStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRBranchStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRCaseClauseNode;
import com.oracle.app.parser.ir.nodes.GoIRCompositeLitNode;
import com.oracle.app.parser.ir.nodes.GoIRDeclStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRExprNode;
import com.oracle.app.parser.ir.nodes.GoIRExprStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRFieldListNode;
import com.oracle.app.parser.ir.nodes.GoIRFieldNode;
import com.oracle.app.parser.ir.nodes.GoIRFileNode;
import com.oracle.app.parser.ir.nodes.GoIRFloat32Node;
import com.oracle.app.parser.ir.nodes.GoIRFloat64Node;
import com.oracle.app.parser.ir.nodes.GoIRForNode;
import com.oracle.app.parser.ir.nodes.GoIRFuncDeclNode;
import com.oracle.app.parser.ir.nodes.GoIRFuncTypeNode;
import com.oracle.app.parser.ir.nodes.GoIRGenDeclNode;
import com.oracle.app.parser.ir.nodes.GoIRIdentNode;
import com.oracle.app.parser.ir.nodes.GoIRIfStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRImportSpecNode;
import com.oracle.app.parser.ir.nodes.GoIRIncDecStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRIndexNode;
import com.oracle.app.parser.ir.nodes.GoIRIntNode;
import com.oracle.app.parser.ir.nodes.GoIRInvokeNode;
import com.oracle.app.parser.ir.nodes.GoIRKeyValueNode;
import com.oracle.app.parser.ir.nodes.GoIRMapTypeNode;
import com.oracle.app.parser.ir.nodes.GoIRObjectNode;
import com.oracle.app.parser.ir.nodes.GoIRReturnStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRSelectorExprNode;
import com.oracle.app.parser.ir.nodes.GoIRSliceExprNode;
import com.oracle.app.parser.ir.nodes.GoIRStarNode;
import com.oracle.app.parser.ir.nodes.GoIRStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRStringNode;
import com.oracle.app.parser.ir.nodes.GoIRStructTypeNode;
import com.oracle.app.parser.ir.nodes.GoIRSwitchStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRTypeSpecNode;
import com.oracle.app.parser.ir.nodes.GoIRUnaryNode;
import com.oracle.app.parser.ir.nodes.GoTempIRNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.source.Source;


/**
 * Constructs the Truffle tree using a visitor pattern to visit
 * every node in the IRTree and translate the information into Truffle
 * Source sections for Truffle nodes are currently on hold and are currently commented out
 */
public class GoTruffle implements GoIRVisitor {
	/**
	 * LexicalScope holds the symbol table information when creating the Truffle tree
	 *
	 */
    public static class LexicalScope {
        protected final LexicalScope outer;
        protected final Set<String> unusedVars;
        public final Map<String, TypeInfo> locals;

        LexicalScope(LexicalScope outer) {
        	//Sets the outerscope to be the calling scope
            this.outer = outer;
            //Creates the local scope
            this.locals = new HashMap<>();
            this.unusedVars = new HashSet<>();
            
            //If there is an outerscope then put all the variables in there
            //into this scope
            /*
            if (outer != null) {
                locals.putAll(outer.locals);
            } 
            */
        }
        
        public boolean checkForUnusedVars(){
        	if(outer == null){
        		return true;
        	}
        	return unusedVars.isEmpty();
        }
        
        public void put(String name, TypeInfo slot){
        	unusedVars.add(name);
        	locals.put(name, slot);
        }
        
        public TypeInfo get(String name){
        	Map<String, TypeInfo> templocals = locals;
        	Set<String> tempset = unusedVars;
        	TypeInfo result = templocals.get(name);
        	if(result != null){
        		tempset.remove(name);
        		return result;
        	}
        	LexicalScope tempouter = outer;
        	while(tempouter != null){
        		templocals = tempouter.locals;
        		tempset = tempouter.unusedVars;
        		result = templocals.get(name);
        		if(result != null){
        			tempset.remove(name);
        			return result;
        		}
        		else{
        			tempouter = tempouter.outer;
        		}
        	}
        	return null;
        }
    }
    
    public static class TypeInfo {
    	String name;
    	String type;
    	boolean isConst;
    	FrameSlot slot;
    	TypeInfo(String name, String type, boolean isConst, FrameSlot slot) {
    		this.name = name;
    		this.type = type;
    		this.isConst = isConst;
    		this.slot = slot;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public String getType() {
    		return type;
    	}
    	
    	public boolean getConst() {
    		return isConst;
    	}
    	
    	public FrameSlot getSlot() {
    		return slot;
    	}
    }
    
	GoLanguage language;
	
    private final Source source;
    private final Map<String, GoRootNode> allFunctions;
    private FrameDescriptor frameDescriptor;
    private LexicalScope global;
    private LexicalScope lexicalscope;
    
    //the order of appearance of functions from parser
    public static LinkedList<GoIRFuncDeclNode> funcOrder = new LinkedList<GoIRFuncDeclNode>();
    public static Map<String,GoIRFuncTypeNode> IRFunctions = new HashMap<>();
    GoIRFuncDeclNode curFunctionDecl;
    int returnCounter = 0;
	
    //Can create a global function block and append writes to the top most node
	public GoTruffle(GoLanguage language, Source source) {
		this.language = language;
		this.source = source;
        this.allFunctions = new HashMap<>();
        frameDescriptor = new FrameDescriptor();
    }
	
	/**
	 * The global scope needs to be initialized with default values before execution
	 */
	public void initialize(){
        startFunction();
        FrameSlot frameSlot;
        frameSlot = frameDescriptor.addFrameSlot("int",FrameSlotKind.Int);
		lexicalscope.put("int", new TypeInfo("int", "int", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("float64", FrameSlotKind.Double);
		lexicalscope.put("float64", new TypeInfo("float64", "float64", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("float32", FrameSlotKind.Float);
		lexicalscope.put("float32", new TypeInfo("float32", "float32", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("bool", FrameSlotKind.Boolean);
		lexicalscope.put("bool", new TypeInfo("bool", "bool", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("true", FrameSlotKind.Boolean);
		lexicalscope.put("true", new TypeInfo("true", "true", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("false", FrameSlotKind.Boolean);
		lexicalscope.put("false", new TypeInfo("false", "false", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("string", FrameSlotKind.Object);
		lexicalscope.put("string", new TypeInfo("string", "string", false, frameSlot));
		frameSlot = frameDescriptor.addFrameSlot("_");
		lexicalscope.put("_", new TypeInfo("_", "_", false, frameSlot));
		global = lexicalscope;
	}

    public Map<String, GoRootNode> getAllFunctions() {
        return allFunctions;
    }
    
    public void startBlock(){
    	lexicalscope = new LexicalScope(lexicalscope);
    }
    
    public void startFunction(){
    	startBlock();
    }
    
    public void finishBlock(){
    	//TODO Does not specify the variable that is declared and only outputs one error
    	if(!lexicalscope.checkForUnusedVars()){
    		throw new GoException("Declared variable and not used");
    	}
    	lexicalscope = lexicalscope.outer;
    }
    
    
	@Override
	public Object visitObject(GoTempIRNode node) {
		for(GoBaseIRNode child : node.getChildren())
			if(child != null)
				child.accept(this);
		return null;
	}

	@Override
	public GoFileNode visitFile(GoIRFileNode node){
		String name = node.getName().getIdentifier();
		GoArrayExprNode decls = (GoArrayExprNode) node.getDecls().accept(this);
		GoImportSpec imports = null;
		if(node.getImports() != null){
			imports = (GoImportSpec) node.getImports().accept(this);
		}
		//Subject to change because what if unit testing doesnt have a main file or something like that :(
		GoRootNode functionstart = allFunctions.get("main");
		GoFileNode result = new GoFileNode(language,frameDescriptor,decls,imports,functionstart,allFunctions,name);
		frameDescriptor = null;
		return result;
	}
	
	@Override
	public Object visitIdent(GoIRIdentNode node) {
		String name = node.getIdentifier();
		GoExpressionNode result = null;
		//System.out.println(name+" "+lexicalscope.locals);
		final TypeInfo info = lexicalscope.get(name);

	    if (info != null) {
	            /* Read of a local variable. */
	    	result = (GoExpressionNode)GoReadLocalVariableNodeGen.create(info.getSlot());
	    } else {
	    	result = new GoIdentNode(name, result);
	    }

	    if(node.getChild() != null) {
			GoIRObjectNode child = (GoIRObjectNode) node.getChild();
			String kind = child.getKind();
			if(kind.equals("func")) {
				child.accept(this);
			}
		}

	    //result.setSourceSection(node.getSource(source));
	    return result;
	}

	@Override
	public Object visitBinaryExpr(GoIRBinaryExprNode node) {
		//GoTypeCheckingVisitor typevisitor = new GoTypeCheckingVisitor(lexicalscope);
		//node.accept(typevisitor);//type check children before making a truffle node
		GoExpressionNode rightNode = (GoExpressionNode) node.getRight().accept(this);
		GoExpressionNode leftNode = (GoExpressionNode) node.getLeft().accept(this);
		String op = node.getOp();
		final GoExpressionNode result;
		switch(op){
		case "+":
			result = GoAddNodeGen.create(leftNode, rightNode);
			break;
		case "-":
			result = GoSubNodeGen.create(leftNode, rightNode);
			break;
		case "*":
			result = GoMulNodeGen.create(leftNode, rightNode);
			break;
		case "/":
			result = GoDivNodeGen.create(leftNode, rightNode);
			break;
		case"%":
			result = GoModNodeGen.create(leftNode, rightNode);
			break;
		case"<":
			result = GoLessThanNodeGen.create(leftNode, rightNode);
			break;
		case"<=":
			result = GoLessOrEqualNodeGen.create(leftNode, rightNode);
			break;
		case"==":
			result = GoEqualNodeGen.create(leftNode, rightNode);
			break;
		case">":
			result = GoGreaterThanNodeGen.create(leftNode, rightNode);
			break;
		case">=":
			result = GoGreaterOrEqualNodeGen.create(leftNode, rightNode);
			break;
		case"!=":
			result = GoNotEqualNodeGen.create(leftNode, rightNode);
			break;
		case"&&":
			result = new GoLogicalAndNode(leftNode, rightNode);
			break;
		case"||":
			result = new GoLogicalOrNode(leftNode, rightNode);
			break;
		case"<<":
			result = GoBinaryLeftShiftNodeGen.create(leftNode, rightNode);
			break;
		case">>":
			result = GoBinaryRightShiftNodeGen.create(leftNode, rightNode);
			break;
		case"&":
			result = GoBitwiseAndNodeGen.create(leftNode, rightNode);
			break;
		case"|":
			result = GoBitwiseOrNodeGen.create(leftNode, rightNode);
			break;
		case"^":
			result = GoBitwiseXORNodeGen.create(leftNode, rightNode);
			break;
		default:
			throw new GoException("Unexpected Operation: "+op);
		}
		//int start = leftNode.getSourceSection().getCharIndex();
		//int end = rightNode.getSourceSection().getCharEndIndex() - start;
		//result.setSourceSection(source.createSection(start,end));
		return result;
	}

	public Object visitIRIntNode(GoIRIntNode node){
		GoIntNode result = new GoIntNode(node.getValue());
		//result.setSourceSection(node.getSource(source));
		return result;
	}

	public Object visitIRFloat32Node(GoIRFloat32Node node) { 
		return new GoFloat32Node(node.getValue());
	}

	public Object visitIRFloat64Node(GoIRFloat64Node node) { 
		return new GoFloat64Node(node.getValue()); 
	}
	
	public Object visitIRStringNode(GoIRStringNode node){
		GoStringNode result = new GoStringNode(node.getValue());
		//result.setSourceSection(node.getSource(source));
		return result;
	}
	
	/* Each invoke, aka call expr, must check the arguments passed match the function signature
	 * 
	 */
	@Override
	public Object visitInvoke(GoIRInvokeNode node) {
		
		
		GoExpressionNode functionNode = (GoExpressionNode) node.getFunctionNode().accept(this);
		if(functionNode instanceof GoReadLocalVariableNode || functionNode instanceof GoIdentNode){
			functionNode = new GoFunctionLiteralNode(language,functionNode.getName());
		}
		/*
		//Type Checking
		GoTypeCheckingVisitor typevisitor = new GoTypeCheckingVisitor(lexicalscope);
		//can only check call expr that arent builtins
		GoIRFuncTypeNode f = IRFunctions.get(node.getFunctionNode().getIdentifier());
		if(f!=null) {
			String side1 = "";//types of the function signature
			String side2 = "";//type of arguments passed in
			side1 = (String) f.getParams().accept(typevisitor);
			GoIRArrayListExprNode child = node.getArgumentNode();
			if(child!= null) {
				side2 = (String)child.accept(typevisitor);
			}
			GoException error = GoTypeCheckingVisitor.Compare(side1, side2,"gotruffle, visitInvoke (" + side1 + "||||" + side2 + ")");
			if(error!=null) {
				throw error;
			}
		}
		//end type checking
		*/

		GoArrayExprNode arguments = null;
		if(node.getArgumentNode() != null){
			arguments = (GoArrayExprNode) node.getArgumentNode().accept(this);
		}
		else{
			arguments = new GoArrayExprNode(new GoExpressionNode[0]);
		}
		GoInvokeNode result = new GoInvokeNode(functionNode, arguments.getArguments());
		//int start = functionNode.getSourceSection().getCharIndex();
		//int end = arguments.getSourceSection().getCharEndIndex() + 1 - start;
		//result.setSourceSection(source.createSection(start,end));
		return result;
	}
	
	public GoFieldNode appendReceiver(GoIRFuncDeclNode node, GoFuncTypeNode typeNode){
		GoFieldNode receiver = handleStructFieldList((GoIRFieldListNode) node.getReceiver())[0];
		//Receivers will only have one field. Multiple fields are not allowed.
		//TODO error check on multiple receivers
		FrameSlot slot = frameDescriptor.addFrameSlot(receiver.getName());
		lexicalscope.put(receiver.getName(),new TypeInfo(receiver.getName(),"struct class TODO" ,false,slot));
		int structargcount;
		if(typeNode.getParams() == null){
			structargcount = 0;
		}
		else{
			structargcount = typeNode.getParams().getSize();
		}
		GoReadArgumentsNode value = new GoReadArgumentsNode(structargcount);
		GoWriteLocalVariableNode structToAppend = GoWriteLocalVariableNodeGen.create(value, slot);
		typeNode.appendReceiverStruct(structToAppend);
		return receiver;
	}

	@Override
	public Object visitFuncDecl(GoIRFuncDeclNode node) {
		funcOrder.push(node);
		String name = node.getIdentifier();
		FrameSlot slot = frameDescriptor.findOrAddFrameSlot(name);
		lexicalscope.put(name, new TypeInfo(name , "FuncLiteral",false, slot));
		startFunction();
		
		GoFuncTypeNode typeNode = (GoFuncTypeNode) node.getType().accept(this);
		GoFieldNode receiver = null;
		if(node.isReceiver()){
			receiver = appendReceiver(node,typeNode);
		}
		GoBlockNode blockNode = (GoBlockNode) node.getBody().accept(this);
		GoFunctionBodyNode bodyNode = new GoFunctionBodyNode(blockNode);
		funcOrder.pop();
		//int start = nameNode.getSourceSection().getCharIndex();
		//int end = blockNode.getSourceSection().getCharEndIndex();
		//SourceSection section = source.createSection(start, end);
		//System.out.println(section);
		GoRootNode root = new GoRootNode(language,frameDescriptor,typeNode,bodyNode,null,name);
		allFunctions.put(name,root);
		finishBlock();

		GoFunctionLiteralNode funcLit = new GoFunctionLiteralNode(language, name);

		if(node.isReceiver()){
			boolean createProperty = true;
			return GoStructPropertyWriteNodeGen.create(createProperty, receiver.getType(), funcLit, name);
		}
		return GoWriteLocalVariableNodeGen.create(funcLit,slot);
	}
	
	@Override
	public Object visitFuncType(GoIRFuncTypeNode node) {
		GoArrayExprNode params = null;
		if(node.getParams() != null) {
			params = (GoArrayExprNode) node.getParams().accept(this);
		}
		String[] results = null;
		if(node.getResults() != null) {
			ArrayList<GoBaseIRNode> children = ((GoIRFieldListNode) node.getResults()).getFields().getChildren();
			results = new String[children.size()];
			for(int i = 0; i < children.size(); i++) {
				// put return names in 2d array
				results[i] = ((GoIRFieldNode) children.get(i)).getTypeName();
			}
			
		}
		return new GoFuncTypeNode(params, results);
	}
	
	@Override
	public Object visitField(GoIRFieldNode node){
		GoReadLocalVariableNode typename = (GoReadLocalVariableNode) node.getType().accept(this);
		GoFieldNode[] result = null;
		if(node.getNames() != null) {
            ArrayList<GoBaseIRNode> children = node.getNames().getChildren();
            result = new GoFieldNode[children.size()];
            for (int i = 0; i < children.size(); i++) {
                String name = children.get(i).getIdentifier();
                FrameSlot slot = frameDescriptor.findOrAddFrameSlot(name);
                lexicalscope.put(name, new TypeInfo(name, node.getTypeName(), false, slot));
                result[i] = new GoFieldNode(name,typename);
            }
        }
		
		// TODO 
		// return types like "int". However they're ident nodes, and int is already in hashmap
		// so accept ident returns read node but can't cast to ident
		//GoIdentNode type = (GoIdentNode) node.getType().accept(this);
		//String typeName = node.getTypeName();
		return new GoArrayExprNode(result);
	}
	/*
	 * Each returnstmt must check function signature for matching return types/length
	 * This creates a GoReturnNode
	 * 
	 * (non-Javadoc)
	 * @see com.oracle.app.parser.ir.GoIRVisitor#visitReturnStmt(com.oracle.app.parser.ir.nodes.GoIRReturnStmtNode)
	 */
	@Override
	public Object visitReturnStmt(GoIRReturnStmtNode node){
		curFunctionDecl = funcOrder.getFirst();//top of stack is the first element, who knew
		/*
		//type checking
		GoIRFieldListNode r = (GoIRFieldListNode) ((GoIRFuncTypeNode) curFunctionDecl.getType()).getResults();
		GoTypeCheckingVisitor typevisitor = new GoTypeCheckingVisitor(lexicalscope);
		String side1 = (String) r.accept(typevisitor);
		String side2 = (String) typevisitor.visitReturnStmt(node);
		GoException error = GoTypeCheckingVisitor.Compare(side1,side2,"gotruffle, visitReturnStmt (" + side1 + "|||" + side2 +")");
		if(error!=null) {
			throw error;
		}
		//end of type checking
		*/
		return new GoReturnNode((GoExpressionNode)node.getChild().accept(this));	
	}

	@Override
	public Object visitArrayListExpr(GoIRArrayListExprNode node) {

		int argumentsize = node.getSize();
		GoExpressionNode[] arguments = new GoExpressionNode[argumentsize];
		ArrayList<GoBaseIRNode> children = node.getChildren();
		for(int i = 0; i < argumentsize; i++){
			arguments[i] = (GoExpressionNode) children.get(i).accept(this);
		}
		GoArrayExprNode result = new GoArrayExprNode(arguments);
		/*
		if(argumentsize > 0 && arguments[0] != null){
			int start = arguments[0].getSourceSection().getCharIndex();
			int end = arguments[argumentsize-1].getSourceSection().getCharEndIndex() - start;
			result.setSourceSection(source.createSection(start,end));
		}*/
		return result;
	}

	@Override
	public Object visitBlockStmt(GoIRBlockStmtNode node) {
		GoStatementNode[] body = null;
		if(node.getChild() != null){
			body = (GoStatementNode[]) node.getChild().accept(this);
		}
		GoBlockNode result = new GoBlockNode(body);
		/*
		if(body.length > 0){
			int start = node.getLbrace();
			int end = node.getRbrace();
			result.setSourceSection(source.createSection(start, end));
		}*/
		return result;
	}

	@Override
	public Object visitExprStmt(GoIRExprStmtNode node) {
		GoExprNode result = new GoExprNode( (GoExpressionNode) node.getChild().accept(this));
		//result.setSourceSection(source.createUnavailableSection());
		return result;
	}

	@Override
	public Object visitExpr(GoIRExprNode node) {
		GoExprNode result = new GoExprNode( (GoExpressionNode) node.getChild().accept(this));
		//result.setSourceSection(source.createUnavailableSection());
		return result;
	}

	@Override
	public Object visitStmt(GoIRStmtNode node) {
		int argumentsize = node.getSize();
		GoStatementNode[] arguments = new GoStatementNode[argumentsize];
		ArrayList<GoBaseIRNode> children = node.getChildren();
		for(int i = 0; i < argumentsize; i++){
			arguments[i] = (GoStatementNode) children.get(i).accept(this);
		}
		return arguments;
	}

	@Override
	public Object visitUnary(GoIRUnaryNode node) {
		GoExpressionNode child = (GoExpressionNode) node.getChild().accept(this);
		String op = node.getOp();
		final GoExpressionNode result;
		switch(op) {
			case"!":
				result = GoLogicalNotNodeGen.create(child);
				break;
			case"^":
				result = GoBitwiseComplementNodeGen.create(child);
				break;
			case"+":
				result = GoPositiveSignNodeGen.create(child);
				break;
			case"-":
				result = GoNegativeSignNodeGen.create(child);
				break;
				
			case "&":
				if(child instanceof GoArrayReadNode){
					FrameSlot array = ((GoArrayReadNode)child).getSlot();
					GoExpressionNode index = ((GoArrayReadNode)child).getIndex();
					result = new GoUnaryAddressNode(array,index);
				}else{
					result = new GoUnaryAddressNode(((GoReadLocalVariableNode) child).getSlot());
				}
				break;
			default:
				throw new RuntimeException("Unexpected Operation: "+op);
		}
		//int start = node.getOpTok();
		//int end = child.getSourceSection().getCharEndIndex() - start;
		//result.setSourceSection(source.createSection(start,end));
		return result;
	}
	
    
	//Skips over itself and returns the child node to the parent of this node
	@Override
	public Object visitDeclStmt(GoIRDeclStmtNode node) {
		if(node.getChild() != null){
			return node.getChild().accept(this);
		}
		return null;
	}

	@Override
	public Object visitGenDecl(GoIRGenDeclNode node) {
		String type = node.getToken();
		GoArrayExprNode result;
		switch(type){
		case "var":
			result = (GoArrayExprNode) node.getChild().accept(this);
			break;
		case "type":
			result = (GoArrayExprNode) node.getChild().accept(this);
			break;
		case "const":
			System.out.println("GenDecl Token: CONST needs implementation");
			result = null;
			break;
		case "import":
			result = (GoArrayExprNode) node.getChild().accept(this);
			break;
		default:
			System.out.println("GenDecl Error Checking Implementation");
			return null;
		}

		GoArrayExprNode results = new GoArrayExprNode(result.getArguments());
		//int start = node.getTokPos();
		//int end = result.getSourceSection().getCharEndIndex() - start;
		//result.setSourceSection(source.createSection(start,end));
		return results;
	}

	public Object visitAssignment(GoIRAssignmentStmtNode node) {	
		GoBaseIRNode child = node.getLHS();
		
		GoWriteVisitor miniVisitor = new GoWriteVisitor(lexicalscope,this,frameDescriptor,node,allFunctions);
		GoExpressionNode result = (GoExpressionNode) miniVisitor.visit(child);
		return result;
		
	}
	
	public Object visitStarNode(GoIRStarNode node){
		GoStarExpressionNode result = new GoStarExpressionNode((GoExpressionNode) node.getChild().accept(this));
		return result;
	}
	
	/**
	 * Only called when needing to read from an arraylike object so return a read.
	 */
	@Override
	public Object visitIndexNode(GoIRIndexNode node){
		//FrameSlot slot = frameDescriptor.findFrameSlot(node.getIdentifier());
		GoExpressionNode expr = (GoExpressionNode) node.getName().accept(this);
		GoExpressionNode index = (GoExpressionNode) node.getIndex().accept(this);
		GoArrayReadNode read = GoArrayReadNodeGen.create(index,(GoReadLocalVariableNode) expr);
		//GoReadArrayNode read = GoReadArrayNodeGen.create(index, slot);
		//int start = node.getLBrack();
		//int startLine = node.getLineNumber();
		//int length = node.getSourceSize();
		//read.setSourceSection(source.createSection(startLine,start,length));
		return read;
	}
	
	/**
	 * return - An undefined GoArray
	 */
	@Override
	public Object visitArrayType(GoIRArrayTypeNode node){

		GoExpressionNode length;
		if(node.getLength() == null) {
			length = new GoIntNode(0);
		}
		else {
			length = (GoExpressionNode) node.getLength().accept(this);
		}
		GoExpressionNode type = (GoExpressionNode) node.getType().accept(this);
		//String type = node.getType().getIdentifier();
		//Catch error where length is not an int node or possibly an int const
		GoArray result = new GoArray((GoIntNode) length,type);

		return result;
	}
	
	@Override
	public Object visitSliceExpr(GoIRSliceExprNode node){
		GoReadLocalVariableNode expr = (GoReadLocalVariableNode) node.getExpr().accept(this);
		GoExpressionNode low = null;
		if(node.getLow() != null){
			low = (GoExpressionNode) node.getLow().accept(this);
		}
		GoExpressionNode high = null;
		if(node.getHigh() != null){
			high = (GoExpressionNode) node.getHigh().accept(this);
		}
		GoExpressionNode max = null;
		if(node.isSlice3()){
			max = (GoExpressionNode) node.getMax().accept(this);
		}
		GoSliceExprNode result = new GoSliceExprNode(expr,low,high,max);

		//String lbrack = node.getSource();
		//int startLine = Integer.parseInt(lbrack.split(":")[1]);
		//int start = Integer.parseInt(lbrack.split(":")[2]);
		//int len = type.getSourceSection().getEndColumn();
		//int len = 3;
		//result.setSourceSection(source.createSection(startLine, start, len));
		return result;
	}
	
	@Override
	public Object visit(GoIRCompositeLitNode node){
		GoExpressionNode type = null;
		if(node.getExpr() != null){
			type = (GoExpressionNode) node.getExpr().accept(this);
		}
		GoArrayExprNode elts = (GoArrayExprNode) node.getElts().accept(this);
		GoCompositeLitNode result = GoCompositeLitNodeGen.create(elts, type);
		return result;
	}
	
	@Override
	public Object visitCaseClause(GoIRCaseClauseNode node) {
		GoArrayExprNode list = null;
		GoStatementNode[]  body = null;

		if (node.getList() != null){
			list = (GoArrayExprNode) node.getList().accept(this);
		}
		if (node.getBody() != null) {
			body = (GoStatementNode[]) node.getBody().accept(this);
		}
		GoCaseClauseNode result = new GoCaseClauseNode(list, body);
		//int startLine = node.getSourceLine();
		//int start = node.getCaseStart();
		//int length = node.getSourceLength();
		//result.setSourceSection(source.createSection(startLine,start,length));
		return result;
	}

	@Override
	public Object visitSwitchStmt(GoIRSwitchStmtNode node) {
		GoStatementNode init = null;
		GoExpressionNode tag = null;
		GoBlockNode body = null;

		if (node.getInit() != null){
			init = (GoStatementNode) node.getInit().accept(this);
		}
		if (node.getTag() != null){
			tag = (GoExpressionNode) node.getTag().accept(this);
		}
		if (node.getBody() != null){
			body = (GoBlockNode) node.getBody().accept(this);
		}

		GoSwitchNode result = new GoSwitchNode(init, tag, body);
		//int line = node.getSourceLine();
		//result.setSourceSection(source.createSection(line));
		return result;
	}

	public Object visitForLoop(GoIRForNode node) {
		startBlock();
		
		GoExpressionNode init = null;
		GoExpressionNode cond = null;
		GoExpressionNode post = null;
		GoStatementNode body;
		if(node.getInit() != null)
			init = (GoExpressionNode) node.getInit().accept(this);
		if(node.getCond() != null)
			cond = (GoExpressionNode) node.getCond().accept(this);
		if(node.getPost() != null)
			post = (GoExpressionNode) node.getPost().accept(this);
		body = (GoStatementNode) node.getBody().accept(this);
		
		finishBlock();
		
		GoForNode result = new GoForNode(init, cond, post, body);
		//result.setSourceSection(source.createSection(node.getSourceLine()));
		return result;
	}

	@Override
	public Object visitIncDecStmt(GoIRIncDecStmtNode node) {
		
		final GoExpressionNode result;
		GoIRIdentNode ident = (GoIRIdentNode) node.getChild();
		GoIRIntNode one = new GoIRIntNode(1, node.getTokPos());
		
		String op = node.getOp();
		final GoIRBinaryExprNode binary_expr = new GoIRBinaryExprNode(op.substring(0,1), ident, one, null);

		GoIRAssignmentStmtNode res = new GoIRAssignmentStmtNode(ident,binary_expr,null);
		result = (GoExpressionNode) res.accept(this);
		return result;
	}
	
	/**
	 * TODO Fix scoping of if statements to only create new scopes per condition block not over the entire
	 * if statement
	 */
	@Override
	public Object visitIfStmt(GoIRIfStmtNode node){
		startBlock();
		GoStatementNode Init = null;
		GoExpressionNode CondNode = null;
		GoStatementNode Body = null;
		GoStatementNode Else = null;
		
		if(node.getInit() != null)
			Init = (GoStatementNode) node.getInit().accept(this);
		
		CondNode = (GoExpressionNode) node.getCond().accept(this);
		startBlock();
		Body = (GoStatementNode)node.getBody().accept(this);
		finishBlock();
		if(node.getElse() != null)
			Else = (GoStatementNode)node.getElse().accept(this);
		finishBlock();
		GoIfStmtNode result = new GoIfStmtNode(Init,CondNode,Body,Else);
		//result.setSourceSection(source.createSection(node.getSourceLine()));
		return result;
	}

	@Override
	public Object visitBranchStmt(GoIRBranchStmtNode node) {
		
		//The child is for the goto implementation
		//TODO
		
		GoExpressionNode child;
		if(node.getChild() != null)
			child = (GoExpressionNode) node.getChild().accept(this);
		
		String type = node.getType();
		GoStatementNode result = null;
		switch(type) {
			case "break":
				result = new GoBreakNode();
				break;
			case "continue":
				result = new GoContinueNode();
				break;
			case "goto":
				System.out.println("BranchStmt Token: GOTO needs implementation");
				break;
			case "fallthrough":
				System.out.println("BranchStmt Token: FALLTHROUGH needs implementation");
				break;
			default:
				throw new RuntimeException("Unexpected BranchStmt: " + type);
		}
		//result.setSourceSection(source.createSection(node.getSourceLine()));
		return result;
	}

	@Override
	public Object visitImportSpec(GoIRImportSpecNode goIRImportSpecNode){
		GoStringNode namenode = (GoStringNode) goIRImportSpecNode.getChild().accept(this);
		String name = (String) namenode.executeGeneric(null);
		FrameSlot frameSlot = frameDescriptor.findOrAddFrameSlot(name);
		//TO DO fix typeInfo
		//TODO
		lexicalscope.put(name, new TypeInfo(name, name, false, frameSlot));
		
		GoStringNode ident = (GoStringNode) goIRImportSpecNode.getChild().accept(this);
		return new GoImportSpec(ident, language, frameSlot);
	}

	@Override
	public Object visitSelectorExpr(GoIRSelectorExprNode goIRSelectorExprNode){
		GoExpressionNode expr = (GoExpressionNode) goIRSelectorExprNode.getExpr().accept(this);
		GoStringNode name = new GoStringNode(goIRSelectorExprNode.getName().getIdentifier());
		return GoSelectorExprNodeGen.create(expr, name);
	}
	
	@Override
	public Object visitTypeSpec(GoIRTypeSpecNode node){
		String name = node.getIdentifier();
		FrameSlot slot = frameDescriptor.addFrameSlot(name);
		lexicalscope.put(name,new TypeInfo(name, node.getTypeName(), false, slot));
		GoExpressionNode type = (GoExpressionNode) node.getType().accept(this);
		
		GoWriteLocalVariableNode result = GoWriteLocalVariableNodeGen.create(type,slot);
		return result;
	}
	
	@Override
	public Object visitStructType(GoIRStructTypeNode node){
		//GoArrayExprNode fields = (GoArrayExprNode) node.getFieldListNode().accept(this);
		//Time to abuse specifications
		GoFieldNode[] fields = handleStructFieldList(node.getFieldListNode());
		GoStructTypeExprNode result = new GoStructTypeExprNode(fields);
		return result;
	}

	/**
	 * This is a seperate fieldlist visitor then the normal one because we do not write this fieldlist into the framedescriptor
	 * The fieldlist needs to be returned and written inside a struct type.
	 * TODO I think this covers for multiple fields on a line
	 * @param node
	 * @return A list of field nodes
	 */
	public GoFieldNode[] handleStructFieldList(GoIRFieldListNode node){
		ArrayList<GoBaseIRNode> fields = node.getFields().getChildren();
		ArrayList<GoFieldNode> tempresult = new ArrayList<>();
		for(int i = 0; i < fields.size(); i++){
			GoFieldNode[] field = handleStructField((GoIRFieldNode) fields.get(i));
			for(GoFieldNode child : field){
				tempresult.add(child);
			}
		}
		GoFieldNode[] result = new GoFieldNode[tempresult.size()];
		for(int i = 0; i < result.length; i++){
			result[i] = tempresult.get(i);
		}
		return result;
	}
	
	//Temporary function. Struct fields are not supposed to be added to the frame descriptor.
	//This function is basically the same as the original visit without adding to the frame descriptor
	//TODO Remove this function for a proper visit? Also cover for blank identifier fields
	public GoFieldNode[] handleStructField(GoIRFieldNode node){
		GoReadLocalVariableNode typename = (GoReadLocalVariableNode) node.getType().accept(this);
		GoFieldNode[] result = null;
		if(node.getNames() != null) {
            ArrayList<GoBaseIRNode> children = node.getNames().getChildren();
            result = new GoFieldNode[children.size()];
            for(int i = 0; i < result.length; i++){
            	String name = children.get(i).getIdentifier();
            	result[i] = new GoFieldNode(name,typename);
            }
        }
		return result;
	}
	
	/**
	 * Runs through the entire field list parameters and assigns them to a write variable by reading
	 * from the argument list given by a function call. Has a lot of nesting going on due to fields being able to 
	 * contain multiple idents and there are many fields in a fieldlist. So we need to go through every field
	 * then every ident contained in a field. For every ident it finds it assigns a frameslot and a write from the readarguments.
	 * 
	 * TODO This does not run through the return results yet.....
	 */
	@Override
	public Object visitFieldList(GoIRFieldListNode node){
		if(node.getFields() != null){
			ArrayList<GoWriteLocalVariableNode> result = new ArrayList<>();
			GoExpressionNode[] fields =  ((GoArrayExprNode) node.getFields().accept(this)).getArguments();
			//GoExpressionNode[] fields = (GoExpressionNode[]) args.getArguments();
			int argumentindex = 0;
			for(GoExpressionNode child : fields){

				GoArrayExprNode boxedFields = (GoArrayExprNode) child;
				for(GoExpressionNode unboxedfield : boxedFields.getArguments()){
					String fieldname = unboxedfield.getName();
					FrameSlot slot = lexicalscope.locals.get(fieldname).getSlot();
					GoReadArgumentsNode value = new GoReadArgumentsNode(argumentindex++);
					result.add(GoWriteLocalVariableNodeGen.create(value, slot));
				}
			}
			GoExpressionNode[] temp = new GoExpressionNode[result.size()];
			for(int i = 0; i < temp.length; i++){
				temp[i] = result.get(i);
			}
			GoArrayExprNode results = new GoArrayExprNode(temp);
			return results;
		}
		return null;
	}

	@Override
	public Object visitObjectNode(GoIRObjectNode node) {
		GoBaseIRNode functionNode = node.getFunctionNode();
		if(functionNode != null) {
			//Lexical scope issue when calling a function not yet inserted
			LexicalScope tempLex = lexicalscope;
			lexicalscope = global;
			
            functionNode.accept(this);
            lexicalscope = tempLex;
        }
		return null;
	}

	/**
	 * Currently only works for structs. Assumes the key is going to be an identifier, but cannot search
	 * through the lexical scope for it, else it gets a readlocalvariable node if the variable exists.
	 */
	public Object visitKeyValue(GoIRKeyValueNode node){
		GoExpressionNode key = (GoExpressionNode) node.getKey().accept(this);
		GoExpressionNode value = (GoExpressionNode) node.getValue().accept(this);
		GoKeyValueNode result = new GoKeyValueNode(key,value);
		return result;
	}

	public Object visitMapType(GoIRMapTypeNode node){
		//Get the types of the map (key, value)
		GoExpressionNode keyType = (GoExpressionNode) node.getKey().accept(this);
		GoExpressionNode valueType = (GoExpressionNode) node.getValue().accept(this);
		GoMapTypeExprNode result = new GoMapTypeExprNode(keyType, valueType);
		return result;
	}
}
