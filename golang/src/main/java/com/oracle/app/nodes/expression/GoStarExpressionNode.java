package com.oracle.app.nodes.expression;

import com.oracle.app.nodes.GoExpressionNode;
import com.oracle.app.nodes.local.GoReadLocalVariableNode;
import com.oracle.app.nodes.types.GoPointerNode;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Should only be called on {@link GoReadLocalVariableNode}s that contain
 * a pointer in them. This is only used to print the value a pointer is pointing
 * at. Did not see a way to directly execute the pointer object without calling a
 * specific method inside {@link GoPointerNode}
 * @author Trevor
 *
 */
public class GoStarExpressionNode extends GoExpressionNode {

	private GoReadLocalVariableNode ptr;
	
	public GoStarExpressionNode(GoReadLocalVariableNode ptr){
		this.ptr = ptr;
	}
	
	@Override
	public Object executeGeneric(VirtualFrame frame) {
		// TODO Auto-generated method stub
		GoPointerNode node = (GoPointerNode) FrameUtil.getObjectSafe(frame, ptr.getSlot());
		return node.executeStar(frame);
	}

}
