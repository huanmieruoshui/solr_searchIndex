package com.bmtech.utils.c2j.cTypes;


public class SmallKit {

	public static char[]U32IpCharArray(U32 u){
		byte[]bs=u.toBytes();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<bs.length;i++){
			sb.append((bs[i]&0xFF)+".");
		}
		char []chksvr=new char[16];
		char[]src=sb.toString().toCharArray();
		System.arraycopy(src, 0, chksvr, 0, src.length-1);
		return chksvr;
	}
	public static String U32_Ip(U32 u){
		byte[]bs=u.toBytes();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<bs.length;i++){
			sb.append((bs[i]&0xFF)+".");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

//	void encrypt_msg(security_hdr_t scrt_hdr, byte[]buf){
//		try{
//			MessageDigest  digest = MessageDigest.getInstance("MD5");
//			digest.update(buf);
//			
//		}catch(Exception e){
//			e.printStackTrace()
//		}
//	}
}
