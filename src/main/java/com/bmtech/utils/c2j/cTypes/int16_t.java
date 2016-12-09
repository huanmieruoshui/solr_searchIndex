package com.bmtech.utils.c2j.cTypes;

import com.bmtech.utils.c2j.SizedNumber;


public  class int16_t extends SizedNumber{
	private int value;	
	public int16_t(long value){
		this.setValue(value);
	}
	public static short size(){
		return 2;
	}
	@Override
	public long getValue() {
		return value&0xFFFF;
	}
	@Override
	public void setValue(long value) {
		this.value=(int) value;
		
	}
	@Override
	public byte[] toBytes() {
		byte[]bs=new byte[2];
		bs[0]=(byte) (value&0xFF);
		bs[1]=(byte)((value>>8)&0xFF);
		return bs;
	}
	@Override
	public void fromBytes(byte[] bs, int from) {
		value=bs[from+1]&0xFF;
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