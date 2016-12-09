package com.bmtech.utils.segment;



public class NoReverseMinStr extends MinStr{

	public NoReverseMinStr(String str){
		this(str,str.length());
	}
	public NoReverseMinStr(String str,int len){
		super(str.toCharArray());
		if(str.length() == 0){
			this.finished = true;
		}else{
			this.len = len;
			this.offset = 0;
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
		if(offset + len <= totalLen){
			return true;
		}else{
			len--;
			return false;
		}
	}

	public boolean forward(){
		this.offset += len;
		this.len = 1;
		if(this.offset >= totalLen){
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
		this.hash = hash(offset, offset + len);
	}

	public String toString(){
		return String.copyValueOf(str, offset, len);
	}

}
