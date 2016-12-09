package com.bmtech.utils.segment;

import java.util.HashMap;


public abstract class MinStr {
	public static final short Number  =  0x02;   	//0000 0010
	public static final short Forbidded  =  0x01; //0000 0001
	public static final short Letter  =  0x10;  	//0001 0000
	public static final short Eng  =  0x14;       //0001 0100
	public static final short Cn  =  0x18;        //0001 1000	

	public final char[] str;
	int offset = 0;
	int len;
	int hash = 0;
	final int totalLen;
	boolean finished = false;

	protected MinStr(char[]arr) {
		this.str = arr;
		this.totalLen = str.length;
	}

	public int hashCode(){
		return hash;
	}
	/**
	 * we assume Object o must be char[]
	 */
	public final boolean equals(Object o){
		if(o == null)
			return false;
		String tmp = this.toString();
		if((o instanceof String)|| 
				(o instanceof MinStr))
			return tmp.equals(o);
		return false;
	}
	/**
	 * inflat this buffer,move nexted.
	 * if  no buffered, return null
	 * @return
	 */
	protected abstract boolean inflat();

	protected abstract boolean forward();
	/**
	 * caculate hash code. this method is compatiable with String.hashcode
	 * @return
	 */
	protected abstract void hash();

	protected final int hash(int start, int end) {
		int h = 0;
		for (int i = start; i < end ; i++) {
			h  =  (h << 5) - h + str[i];
		}
		return h;
	}

	public abstract String toString();
	/**
	 * find next word and cause this classs point to it.
	 * @return
	 */

	protected final boolean nextWord(HashMap<String, Short>map, boolean reverse){
		if(this.finished)
			return false;
		if(this.offset < 0 || this.offset > this.str.length) {
			this.finished = true;
			return false;
		}
		short lastType;
		while(true) {//get a accept word
			lastType  =  Segment.type(this.str[this.offset]);
			if(lastType == Forbidded) {
				if(reverse) {
					if((-- offset) < 0) {
						this.finished = true;
						return false;
					}
				}else {
					if((++ offset) >= totalLen) {
						this.finished = true;
						return false;
					}
				}
			}else {
				break;
			}
		}

		boolean stop = false;
		int crt = this.offset;
		int bakedLen = 1;
		lastType  =  Segment.type(this.str[crt]);
		short crtType = lastType;
		while(true){
			if(reverse) {
				if((-- crt) < 0) {
					if(lastType == Cn) {
						if(len != bakedLen){
							len = bakedLen;
						}
					}
					stop  =  true;
					break;
				}
			}else {
				if((++ crt) >= totalLen) {
					if(lastType == Cn) {
						if(len != bakedLen){
							len = bakedLen;
						}
					}
					stop  =  true;
					break;
				}
			}

			crtType  =  Segment.type(this.str[crt]);
			switch(lastType){
			case Cn:{	
				switch(crtType){
				case Cn:{
					this.inflat();
					this.hash();
					Short isInLex = map.get(this);
					if(isInLex == null){//not in lexicon, last is ok
						len = bakedLen;
						stop = true;
					}else if(isInLex == Segment.state_is){//now is a word! we must stop
						stop = true;
					}else if(isInLex == Segment.state_can){//find a ok point, may have longger match
						bakedLen = len;
						continue;
					}else if(isInLex == Segment.state_may){//maybe a word
						continue;
					}
					break;
				}default:{
					if(len != bakedLen){
						len = bakedLen;
						lastType  =  Cn;
					}
					stop  =  true;
					break;
				}
				}
				break;
			}
			case Eng:
			case Number:{
				switch(crtType){
				case Eng:
				case Number:{
					this.len++;
					break;
				}
				case Cn:{
					stop  =  true;
					break;
				}default:{//forbidded char
					stop  =  true;
					break;
				}
				}

				break;
			}default:{//last is a forbidded char, we may not copy this character to buffer @FIXME
				len  =  0;//accept onthing
				if(crtType !=  Forbidded){
					lastType  =  crtType;
					this.offset  =  crt;//moveto current
					len  =  1;
				}else continue;
				//					stp  =  true;
				break;
			}
			}
			lastType = crtType;
			if(stop)
				break;
		}
		lastType  =  crtType;
		if(reverse) {
			if(len  ==  0||(crt < 0 && lastType  ==  Forbidded))
				return false;
		}else {
			if(len  ==  0||(crt >= totalLen && lastType  ==  Forbidded))
				return false;
		}
		return true;
	}
}

