package org.fxp.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.fxp.android.apk.SigUtil;

public class MD5
{
  private static final String ALGORITHM = "MD5";
  private static MD5 instance = null;
  private static MessageDigest sDigest;

  private MD5()
  {
    try
    {
      sDigest = MessageDigest.getInstance("MD5");
      return;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      while (true)
        localNoSuchAlgorithmException.printStackTrace();
    }
  }

  public static MD5 getInstance()
  {
    if (instance == null)
      instance = new MD5();
    return instance;
  }

  public final String encode(String paramString)
  {
    byte[] arrayOfByte = paramString.getBytes();
    return SigUtil.toHexString(sDigest.digest(arrayOfByte));
  }
}