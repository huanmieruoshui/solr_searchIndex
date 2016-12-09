package com.bmtech.utils.segment;

public class ReverseMinStr extends MinStr{
	public ReverseMinStr(String str){
		this(str,str.length());
	}
	public ReverseMinStr(String str,int len){
		super(str.toCharArray());
		if(str.length() == 0){
			this.finished = true;
		}else{
			this.len = len;
			this.offset = totalLen - 1;
		}
		hash();
	}

	/**
	 * inflat this buffer,move nexted.
	 * if  no buffered, return null
	 * @return
	 */
	public boolean inflat(){
		this.len++;
		if(offset >= (len-1)){
			return true;
		}else{
			len--;
			return false;
		}
	}

	public boolean forward(){
		this.offset -= len;
		this.len = 1;
		if(this.offset < 0){
			this.finished = true;
			return false;	

		}else{
			this.len = 1;
			return true;
		}
	}
	/**
	 * caculate hash code. this method is compatiable with String.hashcode
	 * @return
	 */
	protected void hash(){
		int start = offset - len + 1;
		int end = offset + 1;
		this.hash = hash(start, end);
	}
	public String toString(){
		return String.copyValueOf(str, offset - len + 1, len);
	}
	
}

