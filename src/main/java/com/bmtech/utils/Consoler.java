package com.bmtech.utils;

import java.io.IOException;

public class Consoler {
	/**
	 * read a int from console, if the format is not correctly,continue, if no input or only Enter/whitspace
	 * entered,return defaultValue
	 * @param input
	 * @param default is nothing is inputted, use this as the return
	 * @return
	 */
	public static int readInt(String input,int defaultValue){
		while(true){
			byte[]bs=new byte[32];
			if(input!=null){
				System.out.print(input);
			}
			try {
				System.in.read(bs);
			} catch (IOException e) {
			}

			try{
				String str=new String(bs).trim();
				if(str.length()==0)
					return defaultValue;
				return Integer.parseInt(str);
			}catch(Exception e){	
			}
		}
	}
	public static int readInt(int defaultValue){
		return readInt(":",defaultValue);
	}
	public static String readString(String input){
		while(true){
			byte[]bs=new byte[4096];
			if(input!=null){
				System.out.print(input);
			}
			try {
				System.in.read(bs);
			} catch (IOException e) {
			}
			return new String(bs).trim();
		}
	}
	public static boolean confirm(String input){
		while(true) {
			byte[]bs=new byte[1024];
			if(input!=null){
				System.out.print(input);
			}
			try {
				System.in.read(bs);
				String str = new String(bs).trim();
				if(str.length() == 1) {
					if(str.compareToIgnoreCase("y") == 0)
						return true;
					else if(str.compareToIgnoreCase("n") == 0) {
						return false;
					}
				}else {
					if(str.compareToIgnoreCase("yes") == 0)
						return true;
					else if(str.compareToIgnoreCase("no") == 0) {
						return false;
					}
				}
			} catch (IOException e) {
			}
		}
	}
}
