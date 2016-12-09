package com.bmtech.utils;

public class ByteToInt {
	public static final int[]bais=new int[]{1,
											10,
											100,
											1000,
											10000,
											100000,
											1000000,
											10000000,
											100000000};
	
	public static int toInt(byte[]bs,int from,int len) throws NumberFormatException{
		int ret=0,t;
		int pos=0;
		for(int i=len+from-1;i>=from;i--){
			t=bs[i]-'0';
			if(t<0||t>9)
				throw new NumberFormatException();
			ret+=t*bais[pos++];
		}
		return ret;
	}
	

}
