package com.bmtech.utils;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * tokenlizes a string, and offers a Iterator interface to
 * access the tokens
 * maybe we should consider to use java.util.StringTokenlizer
 * @author beiming
 *
 */
public class StringToken implements Iterator<String>{

	String[]lines;
	int pos=0;

	/**
	 * get token's from string s.s must be tokenlized using " " ,"\n","\t"
	 * @param s
	 * the tokenlized string which will used to get token 
	 */
	public StringToken(String s){
		if((s==null)||(s.trim()==null)||(s.trim().length()==0)){
			lines=new String[0];
			return;
		}
		int ass=s.length()/16;
		if(ass<8)
			ass=8;
		ArrayList<String>list=new ArrayList<String>(ass);
		//{'\n','\t',' ',	'\r','¡¡'};

		boolean started=false;
		int stPos=0,len=s.length();
		int i=0;
		for(;i<len;i++){
			char c=s.charAt(i);
			if(' '==c||'\n'==c||'\t'==c||'\r'==c||'¡¡'==c){
				if(started){
					started=false;
					list.add(s.substring(stPos,i));
					//sb.setLength(0);
				}
			}else{
				if(!started){
					started=true;
					stPos=i;
				}
				//sb.append(c);

			}
		}
		if(started)
			list.add(s.substring(stPos,i));
		lines=new String[list.size()];
		list.toArray(lines);
	}
	/**
	 * tokenlize string s,will seperator char sep
	 * @param s
	 * @param sep
	 */
	public StringToken(String s,char sep){
		if((s==null)||(s.trim()==null)||(s.trim().length()==0)){
			lines=new String[0];
			return;
		}
		int ass=s.length()/16;
		if(ass<8)
			ass=8;
		ArrayList<String>list=new ArrayList<String>(ass);
		//{'\n','\t',' ',	'\r','¡¡'};

		boolean started=false;
		int stPos=0,len=s.length();
		int i=0;
		for(;i<len;i++){
			char c=s.charAt(i);
			if(sep==c){
				if(started){
					started=false;
					list.add(s.substring(stPos,i));
					//sb.setLength(0);
				}
			}else{
				if(!started){
					started=true;
					stPos=i;
				}
				//sb.append(c);

			}
		}
		if(started)
			list.add(s.substring(stPos,i));
		lines=new String[list.size()];
		list.toArray(lines);
	}
	
	public String[] getTokens(){
		return this.lines;
	}
	//	/**
	//	 * get a token from the tokened string
	//	 * when we get a string using get() method,the pointer pointing the current token
	//	 * will move to the next 
	//	 * for example string=11 22 33 44 55
	//	 * get()=11
	//	 * get()=22
	//	 * get()=33
	//	 * @return
	//	 * the token's contents
	//	 */
	//	public String get(){
	//		String ret=null;
	//		if(m_str==null)
	//			return ret;
	//		int pos=m_str.indexOf(" ");
	//		if(pos==-1){
	//			ret=new String(m_str);
	//			m_str=null;
	//		}
	//		else{
	//			ret=m_str.substring(0,pos);
	//			m_str=m_str.substring(pos+1);
	//		}
	//
	//		return ret;
	//	}
	//
	//	/**
	//	 * set the position from where we get the next token
	//	 * @param num
	//	 * the position .for example,num=3,means that we can get the third 
	//	 * token
	//	 */
	//	public void setPos(int num){
	//		for(int i=1;i<num;i++)
	//			this.get();
	//	}
	//	
	////	/**
	////	 * get the tokenStream's count
	////	 * @return the number of the all token in current token Stream
	////	 */
	////	public long getTokenCnt(){
	////		return this.m_len;
	////	}



	public boolean hasNext() {
		if(pos<lines.length)
			return true;
		return false;
	}

	public String next() {
		return lines[pos++];
	}
	public void rewind(){
		pos=0;
	}
	@Deprecated
	public void remove() {
		//itr.remove();	
	}
}


