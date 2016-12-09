package com.bmtech.utils.c2j;


public  class ToBytes{
	private byte[]bss;
	int from=0;
	/**
	 * convert a Struct  to bytes 
	 * @param size the size of the buffer to fill
	 */
	public ToBytes(int size){		
		bss=new byte[size];
	}
	public void next(SizedNumber num){
		byte[] arrayOfByte = num.toBytes();
		next(arrayOfByte);
	}
	public void next(Bytable num){
		byte[] arrayOfByte = num.toBytes();
		next(arrayOfByte);
	}
	public void next(char ch){
		bss[from++]=(byte) ch;
	}

	public void next(SizedNumber []num){
		int f=from;
		for(int i=0;i<num.length;i++){			
			next(num[i]);
		}
		from=f+num.length*num[0].sizeOf();
	}
	public void next(char[]cs){
		int f=from;
		for(int i=0;i<cs.length;i++){			
			next(cs[i]);
		}
		from=f+cs.length;
	}
	/**
	 * get the bytes to trans
	 * @return
	 */
	public byte[]next(){
		return this.bss;
	}
	//	public void next(DFSMessage msg) {
	//		try {
	//			byte[]bs=msg.toBytes();
	//			System.arraycopy(bs, 0, this.bss,from, bs.length);
	//			from+=bs.length;
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			return;
	//		}
	//		
	//	}
	public void next(byte[] bytes) {
		int f=from;
		System.arraycopy(bytes, 0, this.bss, from, bytes.length);
		from=f+bytes.length;

	}
}