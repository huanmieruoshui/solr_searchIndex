package com.bmtech.utils.c2j.cTypes;

import java.io.IOException;
import java.io.InputStream;


public class U8InputStream extends InputStream{
	U8[]array;
	int len;
	int readed=0;
	public U8InputStream(U8[]array){
		this.array=array;
		len=array.length;
	}
	@Override
	public int read() throws IOException {
		if(readed>=len)
			return -1;
		return (int) array[readed++].getValue();
	}
	
}
