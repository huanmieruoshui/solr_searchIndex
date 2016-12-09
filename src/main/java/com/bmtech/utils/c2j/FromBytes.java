package com.bmtech.utils.c2j;

/**
 * this class can init the message
 */
public class FromBytes{
	private byte[]bss;
	private int from=0;
	public FromBytes(byte[]bss){
		this.bss=bss;
		from=0;
	}
	/**
	 * get the bytes has not used
	 * @return
	 */
	public byte[]getBytesLeft(){
		byte[]ret=new byte[bss.length-from];
		System.arraycopy(bss, from,ret, 0, ret.length);
		from=bss.length;
		return ret;
	}

	public void next(SizedNumber num){
		num.fromBytes(bss, from);
		from+=num.sizeOf();
	}
	public void next(Bytable num){
		byte []bs=new byte[num.sizeOf()];
		System.arraycopy(bss, from, bs, 0, bs.length);
		num.fromBytes(bs);
		from+=bs.length;
	}

	public void next(char[] cs){
		for(int i=0;i<cs.length;i++){
			cs[i]=(char) bss[from++];
		}
	}
	public void next(byte[] cs){
		for(int i=0;i<cs.length;i++){
			cs[i]= bss[from++];
		}
	}
	public void next(SizedNumber[]nums){
		for(int i=0;i<nums.length;i++){
			this.next(nums[i]);
		}
	}
	
	
}