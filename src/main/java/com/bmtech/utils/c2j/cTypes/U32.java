package com.bmtech.utils.c2j.cTypes;
public class U32 extends uint32_t{

	public U32(long value) {
		super(value);
	}

	public U32() {
		super(0);
	}
	
	public void fromIp(byte[]bs){
		super.setValue(				
		((bs[0]&0xFF)<<24)|
		((bs[1]&0xFF)<<16)|
		((bs[2]&0xFF)<<8)|
		((bs[3]&0xFF))		
		);
	}
	
}