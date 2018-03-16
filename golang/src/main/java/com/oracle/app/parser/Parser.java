package com.oracle.app.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oracle.app.GoLanguage;
import com.oracle.app.nodes.GoRootNode;
import com.oracle.app.parser.ir.GoBaseIRNode;
import com.oracle.app.parser.ir.GoTruffle;
import com.oracle.app.parser.ir.nodes.GoIRArrayListExprNode;
import com.oracle.app.parser.ir.nodes.GoIRArrayTypeNode;
import com.oracle.app.parser.ir.nodes.GoIRAssignmentStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRBasicLitNode;
import com.oracle.app.parser.ir.nodes.GoIRIntNode;
import com.oracle.app.parser.ir.nodes.GoIRBinaryExprNode;
import com.oracle.app.parser.ir.nodes.GoIRBlockStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRBranchStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRCaseClauseNode;
import com.oracle.app.parser.ir.nodes.GoIRDeclNode;
import com.oracle.app.parser.ir.nodes.GoIRDeclStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRDefaultValues;
import com.oracle.app.parser.ir.nodes.GoIRExprStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRForNode;
import com.oracle.app.parser.ir.nodes.GoIRFuncDeclNode;
import com.oracle.app.parser.ir.nodes.GoIRGenDeclNode;
import com.oracle.app.parser.ir.nodes.GoIRIdentNode;
import com.oracle.app.parser.ir.nodes.GoIRIfStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRIncDecStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRIndexNode;
import com.oracle.app.parser.ir.nodes.GoIRInvokeNode;
import com.oracle.app.parser.ir.nodes.GoIRStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRSwitchStmtNode;
import com.oracle.app.parser.ir.nodes.GoIRUnaryNode;
import com.oracle.app.parser.ir.nodes.GoIRWriteIndexNode;
import com.oracle.app.parser.ir.nodes.GoTempIRNode;
import com.oracle.truffle.api.source.Source;

/**
 * 
 * Parses an ast file generated by the GoLang and creates
 * a 1-1 mapping of Go source code into our own IR representation.
 *
 */
public class Parser {
	//Magic number eraser
	private final int stringAttr  = 1;
	private final int stringVal   = 2;
	private final int regularAttr = 3;
	private final int regularVal  = 4;
	
	private final String file; // the file we open
	private GoLanguage language; // language we are passed
	private Source source;
	private BufferedReader reader; // used to read file
	private String currentLine; // String of the current line we are on
	private Matcher matchedTerm; // used for regex/parsing of file
	private Pattern astPattern = Pattern.compile("\\[\\]\\*ast\\.\\w+|\\.\\w+"); //for getting the type of node
	private Pattern nodeTypePattern = Pattern.compile("\\w+:|\"\\w+\"");
	private Pattern attrPattern= Pattern.compile("(\\w+): \"(.+)\"|(\\w+): (.+)"); //for getting the attributes
	

	/**
	 * 
	 * @param language
	 * @param source
	 * @throws FileNotFoundException
	 */
	public Parser(GoLanguage language, Source source) throws FileNotFoundException {
		this.file = source.getName();
		this.language = language;
		this.source = source;
		reader = new BufferedReader(new FileReader(this.file));
	}

	
	/**
	 * The starting point for the parse function. Initiates the call
	 * for the {@link GoTruffle.class} visitor.
	 * @return A Hashmap containing all function definitions
	 * @throws IOException
	 */
	public Map<String, GoRootNode> beginParse() throws IOException{
		String type;
		GoBaseIRNode k = null;
		while((currentLine = reader.readLine()) != null){
			matchedTerm = astPattern.matcher(currentLine);
			if(matchedTerm.find()){
			
				type = matchedTerm.group().substring(1);
				k = recParse(type);
			}
		}
		
		//Tree visitor for printing out the tree
		//GoVisitor visitor = new GoVisitor();
		//k.accept(visitor);
		
		GoTruffle truffleVisitor = new GoTruffle(language, source);
		k.accept(truffleVisitor);
		
		return truffleVisitor.getAllFunctions();
	}
	
	/**
	 * Depth first traversal of the ast file to recursively create the IRTree from the ast file. 
	 * Every occurence of an opening brace indicates a new node in the GoLang, so call recParse on the new node
	 * Every occurence of a closing brace indicates the ending of the current node so call getIRNode to generate the IRNode
	 * If no braces occur then fill in the attribute Map for  the current node
	 * @param currNode The current node of interest
	 * @return The new IRNode after gathering all the type information on it
	 * @throws IOException
	 */
	private GoBaseIRNode recParse(String currNode) throws IOException {
		Map<String, GoBaseIRNode> body = new HashMap<>();
		Map<String, String> attrs = new HashMap<>();
		String nodeName = currNode;
		
		//while statement reading the file
		
		while((currentLine = reader.readLine()) != null) {
			if(currentLine.indexOf('}') != -1) {
				
	    		//creating itself, going up
				return getIRNode(nodeName,attrs,body);

	    	}
	    	else if(currentLine.indexOf('{') >= 0) {
	    		//going deeper into the tree creating children first
	    		matchedTerm = astPattern.matcher(currentLine);
	    		if(matchedTerm.find()){
	    			String nodeType = matchedTerm.group().substring(1);
	    			matchedTerm = nodeTypePattern.matcher(currentLine);
	    			matchedTerm.find();
	    			String type = matchedTerm.group();
	    			type = type.substring(0,type.length()-1);
	    			body.put(type, recParse(nodeType));
	    			
	    		}
	    	}
	    	else {
	    		//adding attributes
	    		matchedTerm = attrPattern.matcher(currentLine);
	    		if(matchedTerm.find()){
	    			//TO-DO: Maybe shouldnt be hardcoded?????
	    			if(matchedTerm.group(stringAttr) == null){
	    				attrs.put(matchedTerm.group(regularAttr), matchedTerm.group(regularVal));
	    			}
	    			else{
	    				attrs.put(matchedTerm.group(stringAttr), matchedTerm.group(stringVal));
	    			}
	    		}
	    	}
		}
		
		return null;
	}
	
	/**
	 * Create the IRNode.
	 * Match the nodetype to a switch case to create the specific IRNode that corresponds to
	 * the name and pass in the necessary attributes and children nodes in the constructor.
	 * @param nodeType The name of the node to make
	 * @param attrs The attributes associated with the node
	 * @param body The body nodes to insert for the new node
	 * @return The new IRNode
	 */
	public GoBaseIRNode getIRNode(String nodeType, Map<String,String> attrs, Map<String,GoBaseIRNode> body) {
		switch(nodeType) {
			case "ArrayType":
				//If has a len
				if(body.containsKey("Len")) {
					return new GoIRArrayTypeNode(body.get("Len"),body.get("Elt"));
				}
				else {
					return new GoIRArrayTypeNode(body.get("Elt"), true);
				}
			case "AssignStmt":
				GoIRArrayListExprNode lhs = (GoIRArrayListExprNode) body.get("Lhs");
				GoIRArrayListExprNode rhs = (GoIRArrayListExprNode) body.get("Rhs");
				String assigntype = attrs.get("Tok");
				switch(assigntype){
				case "=":
				case ":=":
					//Might need to distinguish between = and :=. 
					//= is only for defined variables
					//:= can be used for defined or undefined variables
					return createAssignment(lhs,rhs);
				case "+=":
				case "-=":
				case "*=":
				case "/=":
				case "%=":
					assigntype = assigntype.substring(0,1);
					return assignNormalize(assigntype,lhs,rhs);
				
				default:
					System.out.println("Error unknown assignment: " + assigntype);
				}
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "BasicLit":
				return GoIRBasicLitNode.createBasicLit(attrs.get("Kind"),attrs.get("Value"));
				
			case "BinaryExpr":
				return new GoIRBinaryExprNode(attrs.get("Op"),body.get("X"),body.get("Y"));
				
			case "BranchStmt":
				return new GoIRBranchStmtNode(attrs.get("Tok"),body.get("Label"));
				
			case "UnaryExpr":
				return new GoIRUnaryNode(attrs.get("Op"),body.get("X"));
				
			case "BlockStmt":
				return new GoIRBlockStmtNode((GoIRStmtNode) body.get("List"));
				
			case "CallExpr":
				GoBaseIRNode functionNode = body.get("Fun");
				GoIRArrayListExprNode args = (GoIRArrayListExprNode) body.get("Args");
				return new GoIRInvokeNode(functionNode,args);

			case "CaseClause":
				return new GoIRCaseClauseNode((GoIRArrayListExprNode) body.get("List"), (GoIRStmtNode) body.get("Body"));
				
			case "DeclStmt":
				return new GoIRDeclStmtNode(body.get("Decl"));
				
			case "Decl":
				ArrayList<GoBaseIRNode> list = new ArrayList<>();
				for(GoBaseIRNode child : body.values()){
					list.add(child);
				}
				return new GoIRDeclNode(list);
				
			case "Expr":
				
				ArrayList<GoBaseIRNode> list1 = new ArrayList<>();
				for(GoBaseIRNode child : body.values()){
					list1.add(child);
				}
				
				return new GoIRArrayListExprNode(list1);
				
			case "ExprStmt":
				return new GoIRExprStmtNode(body.get("X"));
				
			case "FieldList":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "File":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "ForStmt":
				GoBaseIRNode init = body.get("Init");
				GoBaseIRNode cond = body.get("Cond");
				GoBaseIRNode post = body.get("Post");
				GoBaseIRNode for_body = body.get("Body");
				return new GoIRForNode(init,cond,post,for_body);
				
			case "FuncDecl"://(GoBaseIRNode receiver, GoBaseIRNode name, GoBaseIRNode type, GoBaseIRNode body)
				GoBaseIRNode recv = body.get("Recv");
				GoBaseIRNode name = body.get("Name");
				GoBaseIRNode type = body.get("Type");
				GoBaseIRNode func_body = body.get("Body");
				return new GoIRFuncDeclNode(recv,name,type,func_body);
				
			case "FuncType":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "GenDecl":
				return new GoIRGenDeclNode(attrs.get("Tok"),(GoIRArrayListExprNode) body.get("Specs"));
				
			case "]*ast.Ident":
				ArrayList<GoBaseIRNode> identlist = new ArrayList<>();
				for(GoBaseIRNode child : body.values()){
					identlist.add(child);
				}
				
				return new GoIRArrayListExprNode(identlist);
				
			case "Ident":
				GoBaseIRNode obj = body.get("Obj");
				return new GoIRIdentNode(attrs.get("Name"),obj);
			case "IndexExpr":
				//return new GoIRBinaryExprNode("IndexExpr", body.get("X"),body.get("Index"));
				return new GoIRIndexNode((GoIRIdentNode) body.get("X"),body.get("Index"));

			case "IfStmt":
				GoBaseIRNode ifinit = body.get("Init");
				GoBaseIRNode ifcond = body.get("Cond");
				GoBaseIRNode ifblock = body.get("Body");
				GoBaseIRNode elseblock = body.get("Else");
				return new GoIRIfStmtNode(ifinit,ifcond, ifblock, elseblock);
			case "ImportSpec":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "IncDecStmt":
				return new GoIRIncDecStmtNode(attrs.get("Tok"),body.get("X"));
				
			case "Object":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "ParenExpr":
				return new GoIRExprStmtNode(body.get("X"));
				
			case "Scope":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "SelectorExpr":
				return new GoTempIRNode(nodeType,attrs,body);
				
			case "Spec":
				ArrayList<GoBaseIRNode> speclist = new ArrayList<>();
				for(GoBaseIRNode child : body.values()){
					speclist.add(child);
				}
				
				return new GoIRArrayListExprNode(speclist);
				
			case "Stmt":
				ArrayList<GoBaseIRNode> stmtlist = new ArrayList<>();
				for(GoBaseIRNode children : body.values()){
					stmtlist.add(children);
				}
				return new GoIRStmtNode(stmtlist);

			case "SwitchStmt":
				GoIRStmtNode switchinit = (GoIRStmtNode) body.get("Init");
				GoBaseIRNode tag = body.get("Tag");
				GoIRBlockStmtNode switchbody = (GoIRBlockStmtNode) body.get("Body");
				return new GoIRSwitchStmtNode(switchinit, tag, switchbody);
				
			case "ValueSpec":
				GoIRArrayListExprNode names = (GoIRArrayListExprNode) body.get("Names");
				GoBaseIRNode valuetype = body.get("Type");
				GoIRArrayListExprNode values = (GoIRArrayListExprNode) body.get("Values");
				if(values == null){
					return createAssignment(names, valuetype);
				}
				else{
					return createAssignment(names,valuetype,values);
				}
				//return new GoIRValueSpecNode(names,valuetype,values);
				
			default:
				System.out.println("Error, in default: " + nodeType);
				
		}
		return new GoTempIRNode(nodeType,attrs,body);
	}

	/*
	 * Should throw an error when either side have unbalanced arrays or if either is empty
	 */
	public GoIRArrayListExprNode createAssignment(GoIRArrayListExprNode lhs, GoIRArrayListExprNode rhs){
		if(lhs.getSize() != rhs.getSize()){
			System.out.println("Parse error uneven assignment");
			return null;
		}
		ArrayList<GoBaseIRNode> result = new ArrayList<>();
		int size = lhs.getSize();
		GoBaseIRNode writeto;
		for(int i = 0; i < size;i++){
			writeto = lhs.getChildren().get(i);
			if(writeto instanceof GoIRIndexNode){
				writeto = GoIRWriteIndexNode.createIRWriteIndex((GoIRIndexNode) writeto);
			}
			result.add(new GoIRAssignmentStmtNode(writeto,rhs.getChildren().get(i) ));
		}
		return new GoIRArrayListExprNode(result);
	}
	
	/*
	 * Given no right hand side, set default values of the type to each ident
	 */
	public GoIRArrayListExprNode createAssignment(GoIRArrayListExprNode lhs, GoBaseIRNode type){
		ArrayList<GoBaseIRNode> result = new ArrayList<>();
		GoBaseIRNode value = null;
		if(type instanceof GoIRIdentNode){
			value = GoIRDefaultValues.createDefaultBasicLits((GoIRIdentNode)type);
		}
		else if(type instanceof GoIRArrayTypeNode){
			value = type;
		}
		for(GoBaseIRNode node : lhs.getChildren()){
			result.add(new GoIRAssignmentStmtNode(node,value));
		}
		return new GoIRArrayListExprNode(result);
	}
	
	/*
	 * Given a type and a right hand side, the right hand side should match the type given
	 * Assuming that the type matches the right hand side always for now
	 */
	public GoIRArrayListExprNode createAssignment(GoIRArrayListExprNode lhs, GoBaseIRNode type, GoIRArrayListExprNode rhs){
		if(lhs.getSize() != rhs.getSize()){
			//Throw Error
			System.out.println("Variable Declaration Error");
			return null;
		}
		ArrayList<GoBaseIRNode> result = new ArrayList<>();
		int size = lhs.getSize();
		for(int i = 0; i < size;i++){
			result.add(new GoIRAssignmentStmtNode(lhs.getChildren().get(i),rhs.getChildren().get(i) ));
		}
		return new GoIRArrayListExprNode(result);
	}
	
	/**
	 * Assignment operators assumed to only have one child in lhs and rhs
	 * else there is an error
	 * @param op
	 * @param l
	 * @param r
	 * @return
	 */
	public GoIRArrayListExprNode assignNormalize(String op,GoIRArrayListExprNode l, GoIRArrayListExprNode r){
		GoBaseIRNode temp = new GoIRBinaryExprNode(op,l.getChildren().get(0),r.getChildren().get(0));
		r.getChildren().set(0, temp);
		return createAssignment(l,r);
	}
	
	/**
	 * The starting point of the parse function called from GoLanguage
	 * @param language
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static Map<String, GoRootNode> parseGo(GoLanguage language, Source source) throws IOException{
		Parser parser = new Parser(language, source);
		return parser.beginParse();
	}
	
}