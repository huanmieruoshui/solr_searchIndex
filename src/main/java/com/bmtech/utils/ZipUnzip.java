package com.bmtech.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class ZipUnzip {
	public static byte[] unGzip(byte[] input) throws IOException {
		return unGzip(new ByteArrayInputStream(input));
	}
	//	public static byte[] gzip(InputStream ips) throws IOException {
	//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//		GZIPOutputStream ops = new GZIPOutputStream(bos);
	//		byte[]buf = new byte[64 * 1024];
	//		while(true){
	//			int readed = ips.read(buf);
	//			if(readed == -1){
	//				break;
	//			}
	//			ops.write(buf, 0, readed);
	//		}
	//		ops.finish();
	//		ops.close();
	//		return bos.toByteArray();
	//	}
	public static byte[] gzip(byte[]input) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream ops = new GZIPOutputStream(bos);
		ops.write(input);
		ops.finish();
		ops.close();
		return bos.toByteArray();
	}
	public static byte[] unGzip(InputStream input) throws IOException{
		GZIPInputStream ips = new GZIPInputStream(input);
		ByteArrayOutputStream bios = new ByteArrayOutputStream();
		byte[]bs =new byte[4096];
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				bios.write(bs, 0, count);
			}else{
				bios.close();
				ips.close();
				break;
			}
		}
		return bios.toByteArray();
	}
	public static void unGzip(InputStream source, OutputStream os, int[]retArr) throws IOException{
		retArr[0] = 0;
		GZIPInputStream ips = new GZIPInputStream(source);
		byte[]bs =new byte[2048];
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				os.write(bs, 0, count);
				retArr[0] += count;
			}else{
				os.close();
				ips.close();
				break;
			}
		}
	}
	public static int unGzip(InputStream source, OutputStream os) throws IOException{
		GZIPInputStream ips = new GZIPInputStream(source);
		byte[]bs =new byte[4096];
		int readed = 0;
		while(true){
			int count = ips.read(bs);
			if(count != -1){
				os.write(bs, 0, count);
				readed += count;
			}else{
				os.close();
				ips.close();
				break;
			}
		}
		return readed;
	}

	public static byte[] deflate(byte[]input) {
		byte[] result = new byte[4096];
		int totDone = 3000;
		Deflater compresser = new Deflater();
		compresser.setInput(input);
		compresser.finish();
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		while(true) {
			int resultLength = compresser.deflate(result);
			if(resultLength < 1) {
				break;
			}
			bais.write(result, 0, resultLength);
			totDone -- ;
			if(totDone < 0) {
				System.out.println("ERROR totDone match!");
			}
		}
		return bais.toByteArray();
	}
	public static byte [] inflate(byte[]input) throws IOException{
		try {
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			// Decompress the bytes
			Inflater decompresser = new Inflater();
			decompresser.setInput(input);
			byte[] result = new byte[4096];
			int totDone = 3000;
			while(true) {
				int resultLength = decompresser.inflate(result);
				if(resultLength < 1) {
					break;
				}
				bais.write(result, 0, resultLength);
				totDone -- ;
				if(totDone < 0) {
					System.out.println("ERROR totDone match!");
				}
			}
			decompresser.end();

			return bais.toByteArray();
		}catch(Exception e) {
			throw new IOException(e);
		}
	}

	public static void main(String[]a) throws IOException{
		File dir = new File("G:/chinabidding/htmls");
		File fs[] = dir.listFiles();
		File outDir = new File("/tmp/testGZ/");
		if(!outDir.exists()){
			outDir.mkdirs();
		}
		for(File f : fs){
			System.out.println(f);
			File outFile = new File(outDir, f.getName() + ".gz");
			ZipFileOutputStream zfos = new ZipFileOutputStream(outFile);
			FileInputStream fis = new FileInputStream(f);
			byte[] bs = new byte[4096];

			while(true){
				int r = fis.read(bs);
				if(r == -1){
					break;
				}
				zfos.write(bs, 0, r);
			}
			zfos.close();
		}
	}
}
