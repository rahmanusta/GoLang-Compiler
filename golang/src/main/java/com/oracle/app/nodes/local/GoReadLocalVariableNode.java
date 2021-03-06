package com.oracle.app.nodes.local;

import com.oracle.app.nodes.GoExpressionNode;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class GoReadLocalVariableNode extends GoExpressionNode {
	
	@Override
	public String getName(){
		return (String) getSlot().getIdentifier();
	}
	
    @Override
	public String toString() {
		return "GoReadLocalVariableNode [ "+getSlot()+" ]";
	}
    
    @Override
    public int hashCode(){
    	return getSlot().hashCode();
    }

    //Called by GoUnaryAddress to allow pointers to reference a frameslot...
	public abstract FrameSlot getSlot();
    
    @Specialization(guards = "isInt(frame)")
    protected int readInt(VirtualFrame frame){
    	return FrameUtil.getIntSafe(frame, getSlot());
    }
    
    @Specialization(guards = "isFloat(frame)")
    protected float readFloat(VirtualFrame frame){
    	return FrameUtil.getFloatSafe(frame, getSlot());
    }

    @Specialization(guards = "isDouble(frame)")
    protected double readDouble(VirtualFrame frame) { 
    	return FrameUtil.getDoubleSafe(frame, getSlot());
    }

    @Specialization(guards = "isLong(frame)")
    protected long readLong(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    @Specialization(guards = "isBoolean(frame)")
    protected boolean readBoolean(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, getSlot());
    }
    
    @Specialization(guards = "isString(frame)")
    protected Object readString(VirtualFrame frame) {
        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    @Specialization(replaces = {"readInt", "readFloat","readDouble", "readLong", "readBoolean", "readString"})
    protected Object readObject(VirtualFrame frame) {
        if (!frame.isObject(getSlot())) {
            CompilerDirectives.transferToInterpreter();
            Object result = frame.getValue(getSlot());
            frame.setObject(getSlot(), result);	
            return result;
        }

        return FrameUtil.getObjectSafe(frame, getSlot());
    }
    
    protected boolean isInt(VirtualFrame frame){
    	return getSlot().getKind() == FrameSlotKind.Int;
    }
    
    protected boolean isFloat(VirtualFrame frame){
    	return getSlot().getKind() == FrameSlotKind.Float;
    }

    protected boolean isDouble(VirtualFrame frame) { 
    	return getSlot().getKind() == FrameSlotKind.Double;
    }
    
    protected boolean isLong(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Long;
    }

    protected boolean isBoolean(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean;
    }
    
    protected boolean isString( VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Object;
    }
   
}