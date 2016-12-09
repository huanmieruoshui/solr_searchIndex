package com.bmtech.utils.http;

import java.io.IOException;

import com.bmtech.utils.ZipUnzip;

public class WebInflate {

	public static byte[] inflate(byte[]input) throws IOException {
		WebInflate ins = new WebInflate();
		return ins.tryInflate(input);
	}
	private WebInflate() {

	}
	private static final byte dummy_head[] = new byte[]     {
		0x8 + 0x7 * 0x10,
		(((0x8 + 0x7 * 0x10) * 0x100 + 30) / 31 * 31) & 0xFF,

	};
	byte[] tryInflate(byte[] input) throws IOException {
		boolean isAppended = false;
		if(!(input[0] == dummy_head[0] && input[1] == dummy_head[1])) {

			byte[]bs2 = new byte[dummy_head.length + input.length];

			System.arraycopy(dummy_head, 0, bs2, 0, dummy_head.length);
			System.arraycopy(input, 0, bs2, dummy_head.length, input.length);
			input = bs2;
			isAppended = true;
		}
		try {
			byte[] bs3 = ZipUnzip.inflate(input);
			return bs3;
		}catch(IOException e) {
			if(isAppended) {
				byte[]bs2 = new byte[dummy_head.length + input.length];

				System.arraycopy(dummy_head, 0, bs2, 0, dummy_head.length);
				System.arraycopy(input, 0, bs2, dummy_head.length, input.length);
				input = bs2;
				isAppended = true;
				return ZipUnzip.inflate(input);
			}else {
				throw e;
			}
		}
	}
}
