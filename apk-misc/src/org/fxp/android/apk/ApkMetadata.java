package org.fxp.android.apk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.fxp.crawler.bean.CertBean;
import org.fxp.tools.Hash;
import org.fxp.tools.axml.AXMLPrinter;

public class ApkMetadata {
	public static String AXMLFILE_EXT = "AndroidManifest.xml";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ZipFile zipFile = null;
		ApkBean apk = null;
		try {
			zipFile = new ZipFile(args[0]);
			getApkPacInfo(zipFile, args[0]);
			zipFile.close();
		} catch (IOException e) {
			apk = null;
			// e.printStackTrace();
		}
	}

	private static ApkBean getApkPacInfo(ZipFile zipFile, String zipName)
			throws IOException {

		ZipEntry en = zipFile.getEntry(AXMLFILE_EXT);

		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				"c:\\users\\admin\\test.zip"));

		ZipEntry zipEntry = new ZipEntry("d:\\MarketList.txt");
		zipEntry.setComment("Comment fo ");

		zos.putNextEntry(zipEntry);
		FileInputStream fis = new FileInputStream("c:\\users\\admin\\test.zip");
		int c;
		while ((c = fis.read()) != -1) {
			zos.write(c);
		}
		fis.close();
		zos.close();
		/*
		 * ZipEntry en = zipFile.getEntry("MarketList.txt");
		 * System.out.println("Before write. Extra=" + new
		 * String(en.getExtra())); System.out.println("Before write. comment=" +
		 * en.getComment());
		 * System.out.println("Before write. ExternalAttributes=" +
		 * en.getComment()); en.setExtra("lala".getBytes());
		 * en.setComment("lala"); // en.setExternalAttributes(123);
		 * System.out.println("After write. comment=" + en.getComment());
		 * System.out.println("After write. Extra=" + new
		 * String(en.getExtra())); //
		 * System.out.println("After write. ExternalAttributes="
		 * +en.getExternalAttributes()); zipFile.close();
		 * 
		 * return null;
		 */

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			try {
//				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				System.out.println(zipEntry.getComment());
				/*
				 * if (zipEntry.getName().equals(AXMLFILE_EXT)) { byte[] extra =
				 * zipEntry.getExtra();
				 * System.out.println("Before write. Extra=" +
				 * zipEntry.getExtra()); if (extra != null) {
				 * zipEntry.setExtra("lal3a".getBytes()); }
				 * System.out.println("After write. Extra=" +
				 * zipEntry.getExtra()); System.exit(0); // Open
				 * AndroidManifest.xml return null; }
				 */} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return null;

	}
}
