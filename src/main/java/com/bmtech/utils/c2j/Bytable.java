package com.bmtech.utils.c2j;
/**
 * bytable means a class can convert from/to byte array, which is usually used to
 * send to network
 * @author liying1
 *
 */

public interface Bytable {
	/**
	 * get the bytes to present this class
	 * @return
	 * @throws Exception 
	 */
	public byte[]toBytes();
	/**
	 * reInit this class using bytes. This method should 
	 * reset the members
	 * @param bss
	 * @throws Exception 
	 */
	public void fromBytes(byte[]bss);
	
	/**
	 * byte size 
	 * @return
	 * @throws Exception 
	 */
	public int sizeOf();
}
