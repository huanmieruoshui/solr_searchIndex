package com.bmtech.utils.counter;


public class NumCount {
	private int count;
	public NumCount(){
		this.count = 1;
	}
	NumCount(int count){
		this.count = count;
	}
	
	public boolean equals(Object o){
		if(o == null || !(o instanceof Counter<?>))			
			return false;
		return count == ((NumCount)o).count;
	}
	
	public int hashCode(){
		return count;
	}
	public void increaseCount(){
		count ++;
	}
	public void increaseCount(int num){
		count += num;
	}
	public String toString(){
		return String.valueOf(count);
	}
	public int intValue(){
		return this.count;
	}
}
