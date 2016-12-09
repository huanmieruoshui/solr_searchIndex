package com.bmtech.utils.http.fileDump;

import java.util.Iterator;

public class NumberParaGen implements Iterator<String>{
	final int start;
	final int end;
	final int step;
	protected int now;
	public NumberParaGen(int start, int end) {
		this(start, end, 1);
	}
	public NumberParaGen(int start, int end,
			int step) {
		this.start = start;
		this.end = end;
		this.step = step;
		this.now = start - step;
	}
	@Override
	public String next() {
		now += step;
		return "" + now;
	}
	@Override
	public synchronized boolean hasNext() {
		return  now + step <= end;
	}
	@Override
	public void remove() {
		throw new RuntimeException("method remove unsupport!");
		
	}
	
}
