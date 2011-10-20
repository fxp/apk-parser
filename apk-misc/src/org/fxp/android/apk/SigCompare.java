package org.fxp.android.apk;

// Must be encoded with GBK

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.fxp.tools.EncodingToolkit;
import org.fxp.tools.windows.PlatformWindows;

public class SigCompare {

	String apkFile1 = "";
	String apkFile2 = "";

	String certFile1 = "";
	String certFinger1 = "";
	String certFile2 = "";
	String certFinger2 = "";

	String APK_VERIFY_CMD = "jarsigner -verify -verbose -certs ";
	String CERT_VERIFY_CMD = "keytool -printcert -v -file ";

	public static int err_code = -1;
	public static String[] ERR_REASONS = { "Certification verified successed.",
			"Certification verified failed.",
			"Cannot open all certification files.",
			"Cannot open all apk files." };

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public SigCompare(String file1, String file2) {
		apkFile1 = file1;
		apkFile2 = file2;
	}

	public static void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	private static String unzipCertFile(String apkFile) {
		String certPathName = null;
		try {
			ZipFile zipFile = new ZipFile(apkFile);
			Enumeration enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String zipEntryName = zipEntry.getName();
				if (zipEntryName.length() > 8
						&& zipEntryName.substring(0, 8).equals("META-INF")
						&& zipEntryName.substring(zipEntryName.length() - 3,
								zipEntryName.length()).equals("RSA")) {
					certPathName = EncodingToolkit
							.convertStreamToString(zipFile
									.getInputStream(zipEntry));
					// certPathName = apkFile + ".tmpcert";
					// copyInputStream(zipFile.getInputStream(zipEntry),
					// new BufferedOutputStream(new FileOutputStream(
					// certPathName)));
				}
			}
			zipFile.close();
		} catch (ZipException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return certPathName;
	}

	public static String getCert(String apkFileName) {
		String cert = null;
		cert = unzipCertFile(apkFileName);

		return cert;
	}

	public int getResult() {
		// Verify jar file
		try {
			certFinger1 = PlatformWindows.executeFile(APK_VERIFY_CMD + apkFile1);
			certFinger2 = PlatformWindows.executeFile(APK_VERIFY_CMD + apkFile2);
		} catch (IOException e) {
			System.out.println("Cannot open apk");
			err_code = 3;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String retTmp1 = certFinger1.substring(certFinger1.length() - 8,
				certFinger1.length());
		String retTmp2 = certFinger1.substring(certFinger1.length() - 8,
				certFinger1.length());
		if (retTmp1.equals("jar ����֤��")) {
			// System.out.println(apkFile1 + " verified successed");
		} else {
			System.out.println(apkFile1 + " verified failed");
			return 3;
		}
		if (retTmp2.equals("jar ����֤��")) {
			// System.out.println(apkFile2 + " verified successed");
		} else {
			System.out.println(apkFile1 + " verified failed");
			return 3;
		}

		certFile1 = unzipCertFile(apkFile1);
		certFile2 = unzipCertFile(apkFile2);

		// Compare certification
		try {
			if (certFinger1 == null || certFinger2 == null)
				return 2;
			certFinger1 = PlatformWindows.executeFile(CERT_VERIFY_CMD + certFile1);
			certFinger2 = PlatformWindows.executeFile(CERT_VERIFY_CMD + certFile2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (certFinger1.equals(certFinger2))
			err_code = 0;
		else
			err_code = 1;

		File file = new File(certFile1);
		file.delete();
		file = new File(certFile2);
		file.delete();

		return err_code;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("USAGE: command apk1.apk apk2.apk");
			return;
		}
		SigCompare sigCompare = new SigCompare(args[0], args[1]);
		System.out.println(SigCompare.ERR_REASONS[sigCompare.getResult()]);
	}

}
