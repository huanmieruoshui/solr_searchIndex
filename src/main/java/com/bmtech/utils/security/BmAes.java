package com.bmtech.utils.security;

import java.io.UnsupportedEncodingException;

/**
 * encrypt and decrypt message.<br>
 * 
 * @author liying1
 *
 */
public class BmAes {
	public static final int MAXLEN = 16;
	public static class aes_ctx {
		int key_length;
		int []E=new int[60];
		int []D=new int[60];
	}

	private static int []pow_tab=new int[256];
	private static int []log_tab=new int[256];
	private static int []sbx_tab=new int[256];
	private static int []isb_tab=new int[256];
	private static int []rco_tab=new int[10];
	private static int [][]ft_tab=new int[4][256];
	private static int [][]it_tab=new int[4][256];
	private static final int b8=8;
	private static final int b16=16;
	private static final int b24=24;
	private static final byte stuff = 58;
	public static final String encoder = "utf8";
	private static int[][] fl_tab=new int[4][256];
	private static int[][] il_tab=new int[4][256];
	static{//init table
		gen_tabs();
	}
	private static  int generic_rotr32 (int x, int bits){
		int n = (bits&0xff) % 32;
		return (x >>> n) | (x << (32 - n));
	}

	private static 	int generic_rotl32 (int x, int bits){
		int n = (bits&0xff) % 32;
		return (x << n) | (x >>> (32 - n));
	}
	private static int ff_mult(int a, int b){
		return (a>0 && b>0 ? f_mult(a, b) : 0);
	}

	private static  int f_mult (int a, int b){
		int aa = log_tab[a&0xff], cc = (aa + log_tab[b&0xff])&0xff;
		return pow_tab[cc + (cc < aa ? 1 : 0)];
	}
	private static void gen_tabs (){
		int i, t;
		int p, q;

		for (i = 0, p = 1; i < 256; ++i) {
			pow_tab[i] = p;
			log_tab[p] = i;
			p ^= (p << 1) ^ ((p & 0x80)>0 ? 0x01b : 0);
			p&=0xff;
		}

		log_tab[1] = 0;

		for (i = 0, p = 1; i < 10; ++i) {
			rco_tab[i] = p;
			p =  ((p << 1) ^ ((p & 0x80)>0 ? 0x01b : 0))&0xff;
		}

		for (i = 0; i < 256; ++i) {
			p = (i >0? pow_tab[255 - log_tab[i]] : 0);
			q = (((p >>> 7) | (p << 1)) ^ ((p >>> 6) | (p << 2)))&0xff;
			p ^= 0x63 ^ q ^ ((q >>> 6) | (q << 2));
			p&=0xff;
			sbx_tab[i] = p;
			isb_tab[p] =   i;
		}

		for (i = 0; i < 256; ++i) {
			p = sbx_tab[i];

			t = p;
			fl_tab[0][i] = t;
			fl_tab[1][i] = generic_rotl32 (t, b8);
			fl_tab[2][i] = generic_rotl32 (t, b16);
			fl_tab[3][i] = generic_rotl32 (t, b24);

			t = ff_mult (2, p)|p << 8 |p << 16 |ff_mult (3, p) << 24;
			ft_tab[0][i] = t;
			ft_tab[1][i] = generic_rotl32 (t, b8);
			ft_tab[2][i] = generic_rotl32 (t, b16);
			ft_tab[3][i] = generic_rotl32 (t, b24);

			p = isb_tab[i];
			t = p;
			il_tab[0][i] = t;
			il_tab[1][i] = generic_rotl32 (t, b8);
			il_tab[2][i] = generic_rotl32 (t, b16);
			il_tab[3][i] = generic_rotl32 (t, b24);

			t = ff_mult(14, p)|ff_mult(9, p) << 8|ff_mult(13, p) << 16|ff_mult(11, p) << 24;
			it_tab[0][i] = t;
			it_tab[1][i] = generic_rotl32(t, b8);
			it_tab[2][i] = generic_rotl32(t, b16);
			it_tab[3][i] = generic_rotl32(t, b24);
		}

	}

	private static void le32_to_cpu(int x,byte[]array,int from){
		array[from+3]=(byte) (x & 0xff);
		array[from+2]=(byte) ((x>>>8) & 0xff);
		array[from+1]=(byte) ((x>>>16) & 0xff);
		array[from+0]=(byte) ((x>>>24) & 0xff);

	}
	private static int le32_to_cpu_array(byte[]array,int x){
		return ((array[x]&0xff)<<24)|((array[x+1]&0xff)<<16)|((array[x+2]&0xff)<<8)|((array[x+3]&0xff));
	}
	private static byte[] aes_encrypt_core(aes_ctx ctx,  byte []in){

		int []b0=new int[4];
		int []b1=new int[4];
		int kp = 4;// + 4;

		b0[0] = le32_to_cpu_array (in,0) ^ ctx.E[0];
		b0[1] = le32_to_cpu_array (in , 4) ^ ctx.E[1];
		b0[2] = le32_to_cpu_array (in , 8) ^ ctx.E[2];
		b0[3] = le32_to_cpu_array (in , 12) ^ ctx.E[3];


		byte []out=new byte[16];

		kp=f_nround (b1, b0, kp,ctx.E);
		kp=f_nround (b0, b1, kp,ctx.E);
		kp=f_nround (b1, b0, kp,ctx.E);
		kp=f_nround (b0, b1, kp,ctx.E);
		kp=f_nround (b1, b0, kp,ctx.E);
		kp=f_nround (b0, b1, kp,ctx.E);
		kp=f_nround (b1, b0, kp,ctx.E);
		kp=f_nround (b0, b1, kp,ctx.E);
		kp=f_nround (b1, b0, kp,ctx.E);
		f_lround (b0, b1, kp,ctx.E);


		le32_to_cpu(b0[0],out,0);
		le32_to_cpu(b0[1],out,4);
		le32_to_cpu(b0[2],out,8);
		le32_to_cpu(b0[3],out,12);
		return out;
	}
	private static int f_nround(int bo[], int bi[], int k,int []array){
		bo[0] =  
			ft_tab[0][(bi[0])&0xff] ^
			ft_tab[1][(bi[1]>>>8)&0xff] ^
			ft_tab[2][(bi[2]>>>16)&0xff] ^		 
			ft_tab[3][(bi[3]>>>24)&0xff] ^ array[k];
		bo[1] =  
			ft_tab[0][(bi[1])&0xff] ^
			ft_tab[1][(bi[2]>>>8)&0xff] ^
			ft_tab[2][(bi[3]>>>16)&0xff] ^		 
			ft_tab[3][(bi[0]>>>24)&0xff] ^ array[k+1];
		bo[2] =  
			ft_tab[0][(bi[2])&0xff] ^
			ft_tab[1][(bi[3]>>>8)&0xff] ^
			ft_tab[2][(bi[0]>>>16)&0xff] ^		 
			ft_tab[3][(bi[1]>>>24)&0xff] ^ array[k+2];
		bo[3] =  
			ft_tab[0][(bi[3])&0xff] ^
			ft_tab[1][(bi[0]>>>8)&0xff] ^
			ft_tab[2][(bi[1]>>>16)&0xff] ^		 
			ft_tab[3][(bi[2]>>>24)&0xff] ^ array[k+3];
		k += 4;
		return k;
	}

	private static void f_lround(int []bo, int []bi,int  k,int[]array){
		bo[0] =  
			fl_tab[0][bi[0]&0xff] ^			 
			fl_tab[1][(bi[1]>>>8)&0xff] ^		 
			fl_tab[2][(bi[2]>>>16)&0xff] ^		 
			fl_tab[3][(bi[3]>>>24)&0xff] ^ array[k];
		bo[1] =  
			fl_tab[0][bi[1]&0xff] ^			 
			fl_tab[1][(bi[2]>>>8)&0xff] ^		 
			fl_tab[2][(bi[3]>>>16)&0xff] ^		 
			fl_tab[3][(bi[0]>>>24)&0xff] ^ array[k+1];
		bo[2] =  
			fl_tab[0][bi[2]&0xff] ^			 
			fl_tab[1][(bi[3]>>>8)&0xff] ^		 
			fl_tab[2][(bi[0]>>>16)&0xff] ^		 
			fl_tab[3][(bi[1]>>>24)&0xff] ^ array[k+2];
		bo[3] =  
			fl_tab[0][bi[3]&0xff] ^			 
			fl_tab[1][(bi[0]>>>8)&0xff] ^		 
			fl_tab[2][(bi[1]>>>16)&0xff] ^		 
			fl_tab[3][(bi[2]>>>24)&0xff] ^ array[k+3];
	}

	private  static aes_ctx aes_set_key(byte []in_key){
		if(in_key.length != 16) {
			if(in_key.length < 16) {
				byte [] bs = new byte[16];
				System.arraycopy(in_key, 0, bs, 0, in_key.length);
				in_key = bs;
			}else {
				byte [] bs = new byte[16];
				System.arraycopy(in_key, 0, bs, 0, 16);
				in_key = bs;
			}
		}
		aes_ctx ctx = new aes_ctx();//初始化一个int key_length, int E数组长度60，int D数组长度60。
		int i, t, u, v, w;

		ctx.key_length = 16;

		ctx.E[0] = le32_to_cpu_array(in_key,0);
		ctx.E[1] = le32_to_cpu_array(in_key,4);	
		ctx.E[2] = le32_to_cpu_array(in_key,8);	
		ctx.E[3] = le32_to_cpu_array(in_key,12);	

		t = ctx.E[3];
		for (i = 0; i < 10; ++i){
			t = generic_rotr32(t,  8);
			t= fl_tab[0][(t)&(0xff)]
			             ^fl_tab[1][(t >>>8)&(0xff)]
			                        ^fl_tab[2][(t >>>16)&(0xff)]
			                                   ^fl_tab[3][(t >>>24)&(0xff)]
			                                              ^ rco_tab[i];

			t ^= ctx.E[4 * i];     ctx.E[4 * i + 4] = t;     
			t ^= ctx.E[4 * i + 1]; ctx.E[4 * i + 5] = t;    
			t ^= ctx.E[4 * i + 2]; ctx.E[4 * i + 6] = t;    
			t ^= ctx.E[4 * i + 3]; ctx.E[4 * i + 7] = t;   
		}

		ctx.D[0] = ctx.E[0];
		ctx.D[1] = ctx.E[1];
		ctx.D[2] = ctx.E[2];
		ctx.D[3] = ctx.E[3];

		for (i = 4; i < 40; ++i) {
			u   = (((ctx.E[i]) & 0x7f7f7f7f) << 1) ^ ((((ctx.E[i]) & 0x80808080) >>> 7) * 0x1b);//star_x(ctx.E[i]);
			v=(((u) & 0x7f7f7f7f) << 1) ^ ((((u) & 0x80808080) >>> 7) * 0x1b);
			w=(((v) & 0x7f7f7f7f) << 1) ^ ((((v) & 0x80808080) >>> 7) * 0x1b);

			t   = w ^ (ctx.E[i]);          
			(ctx.D[i])  = u ^ v ^ w;        
			(ctx.D[i]) ^= generic_rotr32(u ^ t,  8) ^ 
			generic_rotr32(v ^ t, 16) ^ 
			generic_rotr32(t,24);
		}
		return ctx;
	}

	private static byte[] aes_decrypt_core(aes_ctx ctx, byte []in){
		int []b0=new int[4];
		int []b1=new int[4];
		final int key_len = ctx.key_length;
		int kp = key_len + 20;

		b0[0] = le32_to_cpu_array (in,0) ^ ctx.E[key_len + 24];
		b0[1] = le32_to_cpu_array (in, 4) ^ ctx.E[key_len + 25];
		b0[2] = le32_to_cpu_array (in, 8) ^ctx.E[key_len + 26];
		b0[3] = le32_to_cpu_array (in,12) ^ ctx.E[key_len + 27];

		kp=i_nround (b1, b0, kp,ctx.D);
		kp=i_nround (b0, b1, kp,ctx.D);
		kp=i_nround (b1, b0, kp,ctx.D);
		kp=i_nround (b0, b1, kp,ctx.D);
		kp=i_nround (b1, b0, kp,ctx.D);
		kp=i_nround (b0, b1, kp,ctx.D);
		kp=i_nround (b1, b0, kp,ctx.D);
		kp=i_nround (b0, b1, kp,ctx.D);
		kp=i_nround (b1, b0, kp,ctx.D);

		b0[0] =
			il_tab[0][(b1[0]&0xff)] ^				 
			il_tab[1][(b1[(0 + 3) & 3]>>>8)&0xff] ^		 
			il_tab[2][(b1[(0 + 2) & 3]>>>16)&0xff] ^		 
			il_tab[3][(b1[(0 + 1) & 3]>>>24)&0xff] ^ ctx.D[kp+ 0];

		b0[1] =  
			il_tab[0][(b1[1]&0xff)] ^				 
			il_tab[1][(b1[(1 + 3) & 3]>>>8)&0xff] ^		 
			il_tab[2][(b1[(1 + 2) & 3]>>>16)&0xff] ^		 
			il_tab[3][(b1[(1 + 1) & 3]>>>24)&0xff] ^ ctx.D[kp+ 1];

		b0[2] =  
			il_tab[0][(b1[2]&0xff)] ^				 
			il_tab[1][(b1[(2 + 3) & 3]>>>8)&0xff] ^		 
			il_tab[2][(b1[(2 + 2) & 3]>>>16)&0xff] ^		 
			il_tab[3][(b1[(2 + 1) & 3]>>>24)&0xff] ^ ctx.D[kp+ 2];

		b0[3] =  
			il_tab[0][(b1[3]&0xff)] ^				 
			il_tab[1][(b1[(3 + 3) & 3]>>>8)&0xff] ^		 
			il_tab[2][(b1[(3 + 2) & 3]>>>16)&0xff] ^		 
			il_tab[3][(b1[(3 + 1) & 3]>>>24)&0xff] ^ ctx.D[kp+ 3];


		byte []out=new byte[16];
		le32_to_cpu(b0[0],out,0);
		le32_to_cpu(b0[1],out,4);
		le32_to_cpu(b0[2],out,8);
		le32_to_cpu(b0[3],out,12);
		return out;
	}
	private static int i_nround(int[]bo, int[]bi, int k,int[]array){

		bo[0] =  
			it_tab[0][(bi[0])&0xff] ^				
			it_tab[1][(bi[3]>>>8)&0xff] ^		
			it_tab[2][(bi[2]>>>16)&0xff] ^		
			it_tab[3][(bi[1]>>>24)&0xff] ^ array[k+0];

		bo[1] =  
			it_tab[0][(bi[1])&0xff] ^				
			it_tab[1][(bi[0]>>>8)&0xff] ^		
			it_tab[2][(bi[3]>>>16)&0xff] ^		
			it_tab[3][(bi[2]>>>24)&0xff] ^ array[k+1];


		bo[2] =  
			it_tab[0][(bi[2])&0xff] ^				
			it_tab[1][(bi[1]>>>8)&0xff] ^		
			it_tab[2][(bi[0]>>>16)&0xff] ^		
			it_tab[3][(bi[3]>>>24)&0xff] ^ array[k+2];

		bo[3] =  
			it_tab[0][(bi[3])&0xff] ^				
			it_tab[1][(bi[2]>>>8)&0xff] ^		
			it_tab[2][(bi[1]>>>16)&0xff] ^		
			it_tab[3][(bi[0]>>>24)&0xff] ^ array[k+3];   
		return k-4;
	}

	/**
	 * decrypt <code>in</code> using private key <code>key</code>
	 * @param in
	 * @param key key.length must be 16
	 * @return the encrypted data
	 */
	public static byte[] decrypt (byte []key, byte[]in, int offset, int len)throws RuntimeException{
		aes_ctx ctx_arg = aes_set_key(key);
		if(len == 0 || len % 16 != 0)
			throw new RuntimeException("ERROR code = 0");
		int tot = len / 16 -1;
		byte []tmp = new byte[16];

		System.arraycopy(in, tot * 16 + offset, tmp, 0, 16);
		byte[] bLen = aes_decrypt_core(ctx_arg, tmp);
		String sLen = new String(bLen).trim();
		int iLen ;
		try {
			iLen = Integer.parseInt(sLen);
		}catch(Exception e) {
			throw new RuntimeException("ERROR code = 1");
		}
		if(iLen > (len - 16)) {
			throw new RuntimeException("ERROR code = 2");
		}
		if(iLen < (len - 16 - 16)) {
			throw new RuntimeException("ERROR code = 3");
		}
		byte[]ret = new byte[iLen];
		tot = iLen / 16;
		for(int i = 0; i < tot; i ++) {
			System.arraycopy(in, i * 16 + offset, tmp, 0, 16);
			byte [] t = aes_decrypt_core(ctx_arg, tmp);
			System.arraycopy(t, 0, ret, i * 16, 16);
		}
		int mod = iLen % 16;
		if(mod != 0) {
			System.arraycopy(in, tot * 16 + offset, tmp, 0, 16);
			byte [] t = aes_decrypt_core(ctx_arg, tmp);
			for(int i = mod; i < 16; i ++) {
				if(t[i] != stuff) {
					throw new RuntimeException("ERROR code = 4");
				}
			}
			System.arraycopy(t, 0, ret, tot * 16, mod);
		}
		return ret;
	}
	public static byte[] decrypt (byte []key, byte[]in) {
		return decrypt(key, in, 0, in.length);
	}
	public static String decrypt(String key, byte []in){
		return decrypt(key, in, 0, in.length);
	}
	public static String decrypt(String key, byte []in, int offset, int len){
		byte[] bIn = null, bKey = null;
		try {
			bKey = key.getBytes(encoder);//utf8
			bIn = decrypt(bKey, in, offset, len);
			return new String(bIn, encoder);
		}catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * encrypt using <code>key</code> as private key,the data to encrypt is <code>in</code>
	 * 
	 * @param in data to encrypt
	 * @param key the private key,key.length must be 16
	 * @return
	 */
	public static byte[] encrypt(byte []key, byte []in){
		return encrypt(key, in, 0, in.length);
	}
	
	public static byte[] encrypt(byte []key, byte []in, int offset, int len){
		aes_ctx ctx_arg;
		ctx_arg=aes_set_key(key);


		byte[] bLen = String.format("%016d", len).getBytes();
		int hi = len / 16;
		int lw = len % 16;
		byte[] ret = new byte[16 * (1 + hi + (lw == 0 ? 0 : 1))];
		int st = 0;
		byte []block = new byte[16];
		byte []out;
		for(int i = 0; i < hi; i ++) {
			System.arraycopy(in, 16 * st + offset, block, 0, 16);
			out = aes_encrypt_core(ctx_arg, block);
			System.arraycopy(out, 0, ret, 16 * st, 16);
			st ++;
		}
		if(lw != 0) {
			System.arraycopy(in, 16 * st + offset, block, 0, lw);

			for(int i = lw; i < 16; i ++) {
				block[i] = stuff;
			}
			out = aes_encrypt_core(ctx_arg, block);
			System.arraycopy(out, 0, ret, 16 * st, 16);
			st++;
		}
		out = aes_encrypt_core(ctx_arg, bLen);
		System.arraycopy(out, 0, ret, 16 * st, 16);

		return ret;
	}
	
	public static byte[] encrypt(String key, String in){
		byte[] bIn = null, bKey = null;
		try {
			//[70, 111, 114, 99, 101, 32, 83, 116, 111, 112]
			bIn = in.getBytes(encoder);//encoder=utf8
			//[76, 51, 52, 51, 79, 73, 78, 74, 83, 51, 48, 75, 70, 77, 65]
			bKey = key.getBytes(encoder);
			
		}catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return encrypt(bKey, bIn, 0, bIn.length);
	}
	public static byte[] encrypt(String key, byte []in) {
		return encrypt(key, in, 0, in.length);
	}
	public static byte[] encrypt(String key, byte []in, int offset, int len){
		byte[] bKey;
		try {
			bKey = key.getBytes(encoder);
			return encrypt(bKey, in, offset, len);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
	}
	public static byte[] encrypt(byte []key, String in){
		byte[] bIn = null;
		try {
			bIn = in.getBytes(encoder);
		}catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return encrypt(key, bIn, 0, bIn.length);
	}

}
