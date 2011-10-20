package org.fxp.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Decode {

	public static String ENCODED_STRING = "AAIxMAAAAAMAAAAIAANzZGsAA3NkawAPMDAwMDAwMDAwMDAwMDAwAAABQAAAAeAAAzk5OQAFMi4wLjEAKDg5ZDU4ODg0YjFhNzdlNWIxMDRhZmRjMTk0YjU4NmEwZjBiZGY3YzMAAAEuOG-CTgAgNjA5MDkyNWFjODc0Zjc1ZmU3Y2Q2MzUyOTVjMTFjYWQAAAAAAAAe6A==";

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		byte[] btyDecoded = Base64.decodeBase64(ENCODED_STRING);
		String strdecoded = new String(btyDecoded, "utf8");
		String str = new String(btyDecoded).replace("-", "+").replace("_", "/");
		int i = 0;
	}
/*
	protected void setRequestBody() {
		try {
			DataOutputStream localDataOutputStream = this.daopt;
			String str = this.wPackage;
			localDataOutputStream.writeUTF(str);
			return;
		} catch (IOException localIOException) {
			while (true)
				localIOException.printStackTrace();
		}
	}
	*/
}
