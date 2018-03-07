package com.oracle.app.nodes.types;

import java.util.Arrays;

import com.oracle.truffle.api.frame.VirtualFrame;

public class GoStringArray extends GoArray{

	public String[] array;
	//string array default value is null... thats fun
	
	
	public GoStringArray(int size) {
		array = new String[size];
		Arrays.fill(array, "");
	}
	
	@Override
	public Object readArray(int index) {
		return array[index];
	}

	@Override
	public GoArray executeGoArray(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public GoArray executeGeneric(VirtualFrame frame) {
		// TODO Auto-generated method stub
		return this;
	}

}