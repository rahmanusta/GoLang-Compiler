package com.oracle.app.nodes.types;

import java.util.LinkedHashMap;
import java.util.Map;

import com.oracle.app.nodes.GoArrayExprNode;
import com.oracle.app.nodes.expression.GoKeyValueNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Handles only a single field assignment per field.
 * Not available - Multiple identifiers to a field type
 * 				   Anonymous identifiers/no type
 * @author Trevor
 *
 */
public class GoStruct extends GoNonPrimitiveType{
    protected Map<String, FieldNode> symbolTable;
    protected int size;
    //protected boolean incomplete;

    public GoStruct(){
        this.symbolTable = new LinkedHashMap<>();
        size = 0;
    }

    public Object read(String key){
        return this.symbolTable.get(key).read();
    }

    public void write(String key, Object value){
        this.symbolTable.get(key).insert(value);
    }

    public void insertField(String key, FieldNode node){
        this.symbolTable.put(key, node);
        size++;
    }

    public Object executeGeneric(VirtualFrame frame){
        return this.deepCopy();
    }

	public Object fillCompositeFields(VirtualFrame frame, GoArrayExprNode elts) {
		Object[] vals = elts.gatherResults(frame);
		//TO-DO Add case for Key value expressions, when the fields are named
		if(vals.length != 0){
			if(vals[0] instanceof GoKeyValueNode){
				for(int i = 0; i < vals.length; i++){
					write(((GoKeyValueNode) vals[i]).getKey(), ((GoKeyValueNode) vals[i]).getResult());
				}
			}
			else{
				if(vals.length != size){
					System.out.println("Too few values in struct initializer");
					return null;
				}
				int index = 0;
				for(FieldNode node : symbolTable.values()){
					node.insert(vals[index++]);
				}
			}
		}
		return this;
	}
}