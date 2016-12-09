/**
 * this package is some utility dealing with some usual problem
 * attri :get the special attribute from certain tag
 */
package com.bmtech.utils.io;


import java.net.URL;

public class Xtrim {
	public  static String attri(String name,String str){
		if((str==null)||(name==null))
			return null;
		str=str.toLowerCase();
		name=name.toLowerCase();
		int pos=str.indexOf(name);
		if((pos==-1)||(pos==str.length()-1))
			return null;
		String s=str.substring(pos+name.length()).trim();
		if((s.charAt(0)!='=')||(s.length()==0))
			return null;

		s=s.substring(1).trim();
		if(s==null)return null;

		if(s.charAt(0)=='\"'){//begin with "
			int j=s.indexOf("\"",1);
			if(j!=-1)
				return s.substring(1,j);
			else {
				int j0=s.indexOf(" ");
				if(j0==-1)
					return s.substring(1);
				else return s.substring(1,j0);
			}
		}

		if(s.charAt(0)=='\'')//begin with '
		{
			int j=s.indexOf("\'",1);
			if(j!=-1)
				return s.substring(1,j);
			else {
				int j0=s.indexOf(" ");
				if(j0==-1)
					return s.substring(1);
				else return s.substring(1,j0);
			}
		}

		int cnt=0,p=-1;
		while(true)
		{
			p=str.indexOf("\"",p+1);
			if((p>pos)||(p==-1))
				break;
			cnt++;
		}

		if(cnt%2==1)
		{
			if(p!=-1)
			{
				p=s.indexOf("\"");//截取没有关闭标签内部内容
				s=s.substring(0,p);	
			}
		}
		int k0=s.indexOf(" ");
		if(k0!=-1)
			s=s.substring(0,k0);
		while(s!=null)
		{
			char c=s.charAt(s.length()-1);
			if((c=='\'')||(c=='\"')||(c==';')||(c=='.')||(c==','))
				s=s.substring(0,s.length()-1);
			else break;
		}
		return s;
	}

	public static String Url2Filetring(URL url)
	{
		return Url2FileString(url.toString());
	}
	public static String Url2FileString(String url)
	{
		if(url==null)return null;
		url=url.replace('/','\\');
		url=url.replace(':','.');
		url=url.replace('?','.');
		url=url.replace(':','.');
		url=url.replace('<','.');
		url=url.replace('>','.');
		url=url.replace('*','.');
		url=url.replace('"','.');
		url=url.replace('|','.');
		return url;
	}

	private static String trimSpace(String s0){
		if((s0==null)||(s0.length()==0))
			return null;
		String s=new String(s0);
		boolean bl=false;
		s=s.trim();
		s=s.replaceAll("\n"," ");

		if((s==null)||(s.length()==0))
			return null;
		StringBuffer sbf=new StringBuffer(s);
		for(int i=0;i<sbf.length();i++){
			if(bl){
				if(sbf.charAt(i)==' ')
				{
					sbf.deleteCharAt(i);
					i--;
				}
				else bl=!bl;
			}else{
				if(sbf.charAt(i)==' ')
					bl=!bl;
			}
		}
		return sbf.toString().trim();
	}
	public static String trim(String st){
		if((st==null)||(st.length()==0))
			return null;
		String str=trimSpace(st);
		if((str==null)||(str.length()==0))
			return null;

		str=str.replaceAll("&lt;","<");
		str=str.replaceAll("&gt;",">");
		str=str.replaceAll("&mp;","&");
		str=str.replaceAll("&quot;","\"");
		str=str.replaceAll("&reg;"," ");
		str=str.replaceAll("&copy;"," ");
		str=str.replaceAll("&trade;"," ");
		str=str.replaceAll("&ensp;"," ");
		str=str.replaceAll("&emsp;"," ");
		str=str.replaceAll("&nbsp;"," ");
		return str;


	}
	public static int trimedLen(String s){

		String s1=trim(s);
		if(s1==null)
			return 0;
		int len=0;
		for(int i=0;i<s1.length();i++)
			if(s1.charAt(i)>127)
				len+=2;
				else len++;
		return len;
	}

}
