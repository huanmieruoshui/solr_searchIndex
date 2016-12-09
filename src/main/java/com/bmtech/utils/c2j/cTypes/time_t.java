package com.bmtech.utils.c2j.cTypes;

import com.bmtech.utils.c2j.SizedNumber;


public class time_t extends SizedNumber{
	long time;
	
	public time_t(long l) {
		this.time=l;
	}

	public time_t() {
		time=0;
	}

	public static int size(){
		return 8;
	}

	@Override
	public long getValue() {
		return time;
	}

	@Override
	public void setValue(long value) {
		this.time=value;
	}

	@Override
	public byte[] toBytes() {
		byte[]bs=new byte[8];
		bs[7]=(byte) (time&0xFF);
		bs[6]=(byte)((time>>8)&0xFF);
		bs[5]=(byte)((time>>16)&0xFF);
		bs[4]=(byte)((time>>24)&0xFF);
		bs[3]=(byte)((time>>32)&0xFF);
		bs[2]=(byte)((time>>40)&0xFF);
		bs[1]=(byte)((time>>48)&0xFF);
		bs[0]=(byte)((time>>56)&0xFF);
		return bs;
	}
	@Override
	public void fromBytes(byte[] bss) {
		fromBytes(bss,0);		
	}
	@Override
	public void fromBytes(byte[] bs, int from) {
		long value;
		value=bs[from]&0xFF;
		value<<=8;
		value+=bs[from+1]&0xFF;
		value<<=8;
		value+=bs[from+2]&0xFF;
		value<<=8;
		value+=bs[from+3]&0xFF;	
		value<<=8;
		value+=bs[from+4]&0xFF;	
		value<<=8;
		value+=bs[from+5]&0xFF;	
		value<<=8;
		value+=bs[from+6]&0xFF;	
		value<<=8;
		value+=bs[from+7]&0xFF;	
		this.time=value;
	}
	@Override
	public int sizeOf() {
		return 8;		
	}
	public static time_t now(){
		return new time_t(System.currentTimeMillis()/1000);
	}

}
