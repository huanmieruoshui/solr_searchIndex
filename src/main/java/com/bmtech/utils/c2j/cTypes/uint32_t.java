package com.bmtech.utils.c2j.cTypes;

import com.bmtech.utils.c2j.SizedNumber;


public class uint32_t extends SizedNumber{
	private long value;
	
	public uint32_t(long value){
		this.setValue(value);
	}
	public static int size(){
		return 4;
	}
	@Override
	public long getValue() {
		return value;
	}
	@Override
	public void setValue(long value) {
		this.value=value;
		
	}
	@Override
	public byte[] toBytes() {
		byte[]bs=new byte[4];
		bs[0]=(byte) (value&0xFF);
		bs[1]=(byte)((value>>8)&0xFF);
		bs[2]=(byte)((value>>16)&0xFF);
		bs[3]=(byte)((value>>24)&0xFF);
		return bs;
	}
	@Override
	public void fromBytes(byte[] bs, int from) {
		value=bs[from+3]&0xFF;
		value<<=8;
		value+=bs[from+2]&0xFF;
		value<<=8;
		value+=bs[from+1]&0xFF;
		value<<=8;
		value+=bs[from]&0xFF;	
		
	}
	public int sizeOf(){
		return size();
	}
	@Override
	public void fromBytes(byte[] bss) {
		fromBytes(bss,0);		
	}
}