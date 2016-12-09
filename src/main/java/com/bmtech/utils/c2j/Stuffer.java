package com.bmtech.utils.c2j;
/**
 * this is a class to stuff the bytes array
 * @author liying1
 *
 */
public class Stuffer implements Bytable{
	byte[]bs;
	public Stuffer(int size, Bytable b){
		int mySize = size - b.sizeOf();
		bs = new byte[mySize];
	}
	@Override
	public void fromBytes(byte[] bss) {
		System.arraycopy(bss, 0, bs, 0, bs.length);

	}

	@Override
	public int sizeOf() {
		return bs.length;
	}

	@Override
	public byte[] toBytes() {
		return bs;
	}

}
