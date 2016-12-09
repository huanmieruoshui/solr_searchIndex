package com.bmtech.utils.tcp;

import com.bmtech.utils.c2j.Bytable;
import com.bmtech.utils.c2j.FromBytes;
import com.bmtech.utils.c2j.Struct;
import com.bmtech.utils.c2j.ToBytes;
import com.bmtech.utils.c2j.cTypes.U64;
import com.bmtech.utils.c2j.cTypes.U8;

public class UDPHeader extends Struct implements Bytable{
	private U8 ver=new U8();//
	private U8 []padding=new U8[7];
	private U64 seq=new U64();
	protected U8 []reserved=new U8[8];
	private static final int size=U8.size()+U8.size()*7+U64.size()+U8.size()*8;


	private UDPHeader(){		
		init();
	}
	private static UDPHeader ins = new UDPHeader();
	private Long position=0l;
	/**
	 * get a UDPHeader with seq different from others
	 * @param ver
	 * @return
	 */
	public UDPHeader next(int ver){
		UDPHeader header=new UDPHeader();
		header.ver.setValue(ver);
		synchronized(position){
			header.seq.setValue(position++);
		}
		return header;
	}
	public static UDPHeader instance(){
		return ins;
	}
	/**
	 * make a UDPHeader using byte array <code>bs</code>
	 * @param bs
	 * @return
	 */
	public static UDPHeader newInstance(byte[]bs){
		UDPHeader ins=new UDPHeader();
		ins.fromBytes(bs);
		return ins;
	}
	public static final int size(){
		return size;
	}
	@Override
	public void init() {
		for(int i=0;i<7;i++){
			padding[i]=new U8();
		}
	}
	@Override
	public void fromBytes(byte[] bss) {
		FromBytes byter=new FromBytes(bss);
		byter.next(ver);
		byter.next(padding);
		byter.next(seq);
	}
	@Override
	public int sizeOf() {
		return size;
	}
	@Override
	public byte[] toBytes() {
		ToBytes byter=new ToBytes(size);
		byter.next(ver);
		byter.next(padding);
		byter.next(seq);
		return byter.next();
	}
	/**
	 * get the seq of the header
	 * @return
	 */
	public long getSeq(){
		return this.seq.getValue();
	}
	/**
	 * check if this two head is in the same session
	 * @param header
	 * @return
	 */
	public boolean match(UDPHeader header){
		return this.seq.getValue()==header.seq.getValue();
	}
	
}
