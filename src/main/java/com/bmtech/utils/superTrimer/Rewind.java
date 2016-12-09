package com.bmtech.utils.superTrimer;

import java.util.ArrayList;
/**
 * rewind is a superTrimer that can be used as trimerGroup starts
 * in define file.
 * <br>
 * Rewind mainly used to split DefineStr to different TrimerGroup
 * 
 * @author Fisher@Beiming
 *
 */
public class Rewind extends SuperTrimer{
public static final String NAME = "_rewind_";
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setArgs(ArrayList<String> paras) throws Exception {
	}

	@Override
	public String toDefineStr() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(toDefineString(this.getName()));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		return input;
	}

}
