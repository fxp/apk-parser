package org.fxp.android.apk;

import java.security.SecureRandom;

import org.fxp.tools.MD5;

public class SigUtil {
	private static final String SECURITY_KEY = "3GBUbuild431*(&^^&";
	private static String rd;
	private static final SecureRandom sRandom = new SecureRandom();
	private static long t = 65535L;

	static {
		rd = null;
	}

	public static final String genApiSig() {
		t = System.currentTimeMillis();
		StringBuilder localStringBuilder1 = new StringBuilder();
		rd = genNonce();
		String str1 = rd;
		StringBuilder localStringBuilder2 = localStringBuilder1.append(str1);
		long l = t;
		StringBuilder localStringBuilder3 = localStringBuilder1.append(l);
		String str2 = "000000000000000";
		StringBuilder localStringBuilder4 = localStringBuilder1.append(str2);
		StringBuilder localStringBuilder5 = localStringBuilder1
				.append("3GBUbuild431*(&^^&");
		String str3 = localStringBuilder1.toString();
		return MD5.getInstance().encode(str3);
	}

	public static final String genNonce() {
		byte[] arrayOfByte = new byte[20];
		sRandom.nextBytes(arrayOfByte);
		return toHexString(arrayOfByte);
	}

	public static String getRd() {
		return rd;
	}

	public static long getTimeStamp() {
		return t;
	}

	public static final String toHexString(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length * 2;
    StringBuffer localStringBuffer1 = new StringBuffer(i);
    int j = 0;
    while (true)
    {
      int k = paramArrayOfByte.length;
      if (j >= k)
        return localStringBuffer1.toString().toLowerCase();
      int m = paramArrayOfByte[j] & 0xFF;
      if (m < 16)
         localStringBuffer1.append(48);
      String str = Integer.toHexString(m);
      StringBuffer localStringBuffer3 = localStringBuffer1.append(str);
      j += 1;
    }
  }
}

/*
 * Location:
 * F:\Reverse\Workspace\HiApk\ggmarket_02.00.05_build101119_SDK4.apk.dex2jar.jar
 * Qualified Name: com.jiubang.market.utils.SigUtil JD-Core Version: 0.6.0
 */