package apkReader;


import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class Hash {

	public static String ALGO_DEFAULT = "SHA1";
	public static String algo = ALGO_DEFAULT;

	public static void setAlgo(String algo) {
		try {
			MessageDigest.getInstance(algo);
			Hash.algo = algo;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MessageDigest createFileDigest(String filename, String method) {
		MessageDigest ret = null;
		try {
			InputStream fis = new FileInputStream(filename);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance(method);
			int numRead = 0;
			while (numRead != -1) {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			}
			fis.close();
			ret = complete;
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static String createFileHash(String filename, String method) {
		String ret = null;
		try {
			InputStream fis = new FileInputStream(filename);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance(method);
			int numRead = 0;
			while (numRead != -1) {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			}
			fis.close();
			ret = asHex(complete.digest());
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static byte[] createHashFromString(String text, String method) {
		byte[] ret = null;
		try {
			byte[] b = text.getBytes();
			MessageDigest algorithm = MessageDigest.getInstance(method);
			algorithm.reset();
			algorithm.update(b);
			byte messageDigest[] = algorithm.digest();
			ret = messageDigest;
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static String getFileHash(String filename) {
		String ret = null;
		try {
			byte[] b = createFileDigest(filename, algo).digest();
			ret = asHex(b);
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static String getHash(String text, String digestAlgorithm) {
		String ret = null;
		try {
			byte[] b = createHashFromString(text, digestAlgorithm);
			ret = asHex(b);
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static String getHash(byte[] data, String digestAlgorithm) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
			ret = asHex(md.digest(data));
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	public static String asHex(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
