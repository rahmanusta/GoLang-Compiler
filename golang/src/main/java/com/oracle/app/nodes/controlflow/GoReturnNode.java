package com.oracle.app.nodes.controlflow;

import com.oracle.app.nodes.GoArrayExprNode;
import com.oracle.app.nodes.GoExpressionNode;
import com.oracle.app.nodes.GoStatementNode;
import com.oracle.app.runtime.GoNull;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "return", description = "The node implementing a return statement")
public final class GoReturnNode extends GoStatementNode {

    @Child private GoExpressionNode valueNode;

    public GoReturnNode(GoExpressionNode valueNode) {
        this.valueNode = valueNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object result;
        if (valueNode != null) {
        	if( ((GoArrayExprNode) valueNode).getArguments().length ==1) {
        		result = ((GoArrayExprNode) valueNode).getArguments()[0].executeGeneric(frame);
        	}
        	else {
        		System.out.println("__________");
        		System.out.println(((GoArrayExprNode) valueNode).getArguments());
        		result = ((GoArrayExprNode) valueNode).executeGeneric(frame);
        	}
        } else {
            /*
             * Return statement that was not followed by an expression, so return the null value.
             */
            result = GoNull.SINGLETON;
        }
        //this throw is what makes it actualy return
        throw new GoReturnException(result);
    }
}