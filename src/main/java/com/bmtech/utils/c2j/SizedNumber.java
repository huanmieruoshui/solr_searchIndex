package com.bmtech.utils.c2j;

public abstract class SizedNumber implements Bytable{
	/**
	 * change the bytes to c standard order.
	 * low byte first
	 * @return
	 */
	public abstract byte[] toBytes();
	public abstract void fromBytes(byte[]bs,int from);
	public abstract long getValue();
	public abstract void setValue(long value);

	public abstract int sizeOf();
	/**
	 * int value of the {@link #getValue()}
	 * @return
	 */
	public int intValue() {
		return (int) getValue();
	}
	/**
	 * type maps from one type to another
	 * @param num
	 */
	public void setValue(SizedNumber num){
		this.setValue(num.getValue());
	}
	public String toString(){
		return this.getValue()+"";
	}
	public final boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o instanceof SizedNumber) {
			return ((SizedNumber) o).getValue() == this.getValue();
		}
		if(o instanceof Byte) {
			return this.getValue() == (Byte)o;
		}
		if(o instanceof Integer) {
			return this.getValue() == (Integer)o;
		}
		if(o instanceof Long) {
			return this.getValue() == (Long)o;
		}
		if(o instanceof Short) {
			return this.getValue() == (Short)o;
		}
		return false;
	}
	public byte[] htonl(){
		byte[]bs=this.toBytes();
		int len=bs.length;
		byte[]ret=new byte[len];
		for(int i=0;i<len;i++){
			ret[i]=bs[len-i-1];
		}
		return ret;
	}
	

}