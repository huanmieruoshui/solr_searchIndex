package com.bmtech.utils.segment;

import java.util.ArrayList;

public class TokenHandler{
	private ArrayList<String>strs = new ArrayList<String>();
	private MinStr str;
	private int cursor;
	private int size = 0;
	private boolean reverse = false;
	TokenHandler sub;
	public void finish(){
		size = strs.size();
	}
	public void combine(TokenHandler sub) {
		if(this.sub == null) {
			this.sub = sub;
		}else {
			this.sub.combine(sub);
		}
	}
	public TokenHandler(MinStr str){
		if(str instanceof ReverseMinStr) {
			this.reverse = true;
		}
		this.str = str;
	}
	public boolean hasNext(){
		if(cursor >= size) {
			if(sub != null) {
				return sub.hasNext();
			}else {
				return false;
			}
		}else {
			return true;
		}
	}
	public synchronized String next(){
		int index;
		if(cursor >= size) {
			if(sub != null) {
				return sub.next();
			}else
				return null;
		}else {
			index = cursor;
			cursor ++;

			if(this.reverse) {
				return strs.get(this.size - index - 1);
			}else {
				return strs.get(index);
			}

		}
	}

	public void put() {
		this.strs.add(str.toString());
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(!this.reverse) {
			for(int i = 0; i < this.size; i ++){
				if(i != 0){
					sb.append(Segment.separator);
				}
				sb.append(this.strs.get(i));
			}
		}else {
			for(int i = this.size -1 ; i >= 0; i--){
				sb.append(this.strs.get(i));
				if(i != 0){
					sb.append(Segment.separator);
				}
			}
		}
		if(this.sub != null) {
			sb.append(Segment.separator);
			sb.append(sub.toString());
		}
		return sb.toString();
	}
}
