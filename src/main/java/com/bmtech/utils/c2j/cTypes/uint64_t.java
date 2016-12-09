package com.bmtech.utils.c2j.cTypes;

import com.bmtech.utils.c2j.SizedNumber;

//FIXME x64 are all not corrent becouse we only use low 8byte as a number
public class uint64_t extends SizedNumber{
	private long value;
	public uint64_t(long value){
		this.setValue(value);
	}
	public static int size(){
		return 8;
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
		byte[]bs=new byte[8];
		bs[0]=(byte) (value&0xFF);
		bs[1]=(byte)((value>>8)&0xFF);
		bs[2]=(byte)((value>>16)&0xFF);
		bs[3]=(byte)((value>>24)&0xFF);
		bs[4]=(byte)((value>>32)&0xFF);
		bs[5]=(byte)((value>>40)&0xFF);
		bs[6]=(byte)((value>>48)&0xFF);
		bs[7]=(byte)((value>>56)&0xFF);
		return bs;
	}
	@Override
	public void fromBytes(byte[] bs, int from) {
		value=bs[from+7]&0xFF;
		value<<=8;
		value+=bs[from+6]&0xFF;
		value<<=8;
		value+=bs[from+5]&0xFF;
		value<<=8;
		value+=bs[from+4]&0xFF;	
		value<<=8;
		value+=bs[from+3]&0xFF;	
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