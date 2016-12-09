package com.bmtech.utils.c2j.cTypes;

import com.bmtech.utils.c2j.SizedNumber;


public class int8_t extends SizedNumber{
	private byte value;
	public int8_t(){
		this.setValue(value);
	}
	public static final short size(){
		return 1;
	}
	public byte byteValue(){
		return this.value;
	}
	@Override
	public long getValue() {
		return value;
	}
	@Override
	public void setValue(long value) {
		this.value=(byte) value;		
	}
	@Override
	public byte[] toBytes() {
		return new byte[]{value};
	}
	@Override
	public void fromBytes(byte[] bs, int from) {
//		value=(byte) (bs[from]&0xFF);	
		value=bs[from];
	}
	public int sizeOf(){
		return size();
	}
	@Override
	public void fromBytes(byte[] bss) {
		fromBytes(bss,0);		
	}
}