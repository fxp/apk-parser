package org.fxp.android.apk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.security.cert.Certificate;

import org.fxp.crawler.bean.CertBean;
import org.fxp.mode.SingletonException;
import org.fxp.tools.FileUtilsExt;
import org.fxp.tools.Hash;
import org.fxp.tools.axml.AXMLPrinter;

public class ApkFileManager {
	private static ApkFileManager self = null;
	private static boolean instance_flag = false;
	/*
	 * Apk info inside Apk file naming rule:
	 * 
	 * Temporary file name: duplicatedCount.marketName.pId.versionCode.TEMP with
	 * extra information attachment:
	 * duplicatedCount.marketName.pId.versionCode.TEMP.INFO
	 */
	private static String TEMP_FILE_NAME = "%s.%s.%s.TEMP";
	private static String TEMP_FILE_ATT_NAME = "%s.%s.%s.TEMP.INFO";

	/*
	 * Temporary error file name: duplicatedCount.marketName.pId.versionCode.ERR
	 * with error information attachment:
	 * duplicatedCount.marketName.pId.versionCode.ERR.INFO
	 */
	private static String TEMP_ERR_FILE_NAME = "%s.%s.%s.ERR";
	private static String TEMP_ERR_FILE_ATT_NAME = "%s.%s.%s.ERR.INFO";

	/*
	 * Lib file name: packageName.versionCode.apk with extra information
	 * attachment: packageName.versionCode.apk.INFO
	 */
	private static String LIB_FILE_NAME = "%s.%s.apk";
	private static String LIB_FILE_ATT_NAME = "%s.%s.apk.INFO";

	/*
	 * Lib conflicted file name: packageName.versionCode.CONFLICT with conflict
	 * information attachment: packageName.versionCode.CONFLICT.INFO
	 */
	private static String LIB_CONFLICT_FILE_NAME = "%s.%s.CONFLICT.%s";
	private static String LIB_CONFLICT_FILE_ATT_NAME = "%s.%s.CONFLICT.%s.INFO";

	// For apk files
	// DON'T forget '\' at end of paths
	private static String BASE_PATH = "apk\\";
	public static String AXMLFILE_EXT = "AndroidManifest.xml";

	private ApkFileManager() {
		if (instance_flag)
			throw new SingletonException("Only one instance allowed");
		else
			instance_flag = true;
	};

	private void init() {
		File file = new File(BASE_PATH);
		// TODO Add more test
		if (!file.isDirectory())
			instance_flag = false;
		instance_flag = true;

		if (FlushRemainingApk() != 0)
			System.out.println("Flusing remain apks in local folder failed");
	}

	private int FlushRemainingApk() {
		return 0;
	}

	public static ApkFileManager GetInstance() {
		if (self == null) {
			self = new ApkFileManager();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public ApkBean testAndAddApk(ApkBean apk) throws FileNotFoundException,
			IOException {
		// Test zip
		ApkBean apkTmp = unzipApk(apk.apkLocalPath);

		if (apkTmp == null) {
			FileUtilsExt.movefile(apk.apkLocalPath,
					getApkFileName(apk, "TEMPERR", true));
			ApkToolkits.saveApkToFile(apk,
					getApkFileName(apk, "TEMPERRATT", true));
			return null;
		}

		// Test existance
		ApkBean apkExist = unzipApk(getApkFileName(apkTmp, "LIB", true));
		if (apkExist != null) {
			// If they are same, return as normal
			// Including file hash, rechecking file availability,
			if (FileUtilsExt.compareFile(apk.apkLocalPath,
					getApkFileName(apkTmp, "LIB", true))) {
				System.out.println("Downloaded apk is the same with ours ");
				// ApkToolkits.printApk(apk);
			} else {
				FileUtilsExt.movefile(apk.apkLocalPath,
						getApkFileName(apkTmp, "LIBCONFLICT", true));
				System.err
						.println("Downloaded apk is different with ours. Has been copy to lib with a [ApkPackageName].CONFLICT name");
				// ApkToolkits.printApk(apk);
			}
		} else {
			boolean isMoved = FileUtilsExt.movefile(apk.apkLocalPath,
					getApkFileName(apkTmp, "LIB", true));
			System.out
					.println("A new apk has been copy to lib, move operation result "
							+ isMoved);
			// ApkToolkits.printApk(apk);
		}
		apk.packageName = apkTmp.packageName;
		apk.versionCode = apkTmp.versionCode;
		return apk;
	}

	public static String getApkFileName(ApkBean apk, String phrase,
			boolean absolutePath) {
		String ret = null;
		if (phrase.equals("TEMP") || phrase.equals("TEMPATT")) {
			// duplicatedCount.marketName.pId.versionCode.TEMP.INFO
			if (apk.marketBean.marketPid == null
					|| apk.marketBean.marketName == null)
				return null;
			int duplicatedTime = 0;
			String tmpFileName = String.format(TEMP_FILE_NAME, duplicatedTime,
					apk.marketBean.marketName, apk.marketBean.marketPid);
			File file = null;
			do {
				file = new File(tmpFileName);
				duplicatedTime++;
				tmpFileName = String.format(TEMP_FILE_NAME, duplicatedTime,
						apk.marketBean.marketName, apk.marketBean.marketPid);
			} while (file.exists());
			if (phrase.equals("TEMP"))
				ret = String.format(TEMP_FILE_NAME, duplicatedTime - 1,
						apk.marketBean.marketName, apk.marketBean.marketPid);
			else
				ret = String.format(TEMP_FILE_ATT_NAME, duplicatedTime - 1,
						apk.marketBean.marketName, apk.marketBean.marketPid);
		} else if (phrase.equals("TEMPERR") || phrase.equals("TEMPERRATT")) {
			// duplicatedCount.marketName.pId.versionCode.ERR
			if (apk.marketBean.marketPid == null
					|| apk.marketBean.marketName == null)
				return null;
			int duplicatedTime = 0;
			String tmpFileName = String.format(TEMP_ERR_FILE_NAME,
					duplicatedTime, apk.marketBean.marketName,
					apk.marketBean.marketPid, apk.versionCode);
			File file = null;
			do {
				file = new File(tmpFileName);
				duplicatedTime++;
				tmpFileName = String.format(TEMP_ERR_FILE_NAME, duplicatedTime,
						apk.marketBean.marketName, apk.marketBean.marketPid,
						apk.versionCode);
			} while (file.exists());
			if (phrase.equals("TEMPERR"))
				ret = String.format(TEMP_ERR_FILE_NAME, duplicatedTime - 1,
						apk.marketBean.marketName, apk.marketBean.marketPid,
						apk.versionCode);
			else
				ret = String.format(TEMP_ERR_FILE_ATT_NAME, duplicatedTime - 1,
						apk.marketBean.marketName, apk.marketBean.marketPid,
						apk.versionCode);
		} else if (phrase.equals("LIB") || phrase.equals("LIBATT")) {
			// packageName.versionCode.apk
			if (apk.packageName == null || apk.versionCode == 0)
				return null;
			if (phrase.equals("LIB"))
				ret = String.format(LIB_FILE_NAME, apk.packageName,
						apk.versionCode);
			else
				ret = String.format(LIB_FILE_ATT_NAME, apk.packageName,
						apk.versionCode);
		} else if (phrase.equals("LIBCONFLICT")
				|| phrase.equals("LIBCONFLICTATT")) {
			// packageName.versionCode.CONFLICT
			if (apk.packageName == null || apk.versionCode == 0)
				return null;
			int duplicatedTime = 0;
			String tmpFileName = String.format(LIB_CONFLICT_FILE_NAME,
					apk.packageName, apk.versionCode, duplicatedTime);
			File file = null;
			do {
				file = new File(tmpFileName);
				duplicatedTime++;
				tmpFileName = String.format(LIB_CONFLICT_FILE_NAME,
						apk.packageName, apk.versionCode, duplicatedTime - 1);
			} while (file.exists());
			if (phrase.equals("LIBCONFLICT"))
				ret = String.format(LIB_CONFLICT_FILE_NAME, apk.packageName,
						apk.versionCode, duplicatedTime - 1);
			else
				ret = String.format(LIB_CONFLICT_FILE_ATT_NAME,
						apk.packageName, apk.versionCode, duplicatedTime);
		} else {
			System.out.println("Error type: " + phrase);
			return null;
		}
		if (absolutePath)
			return BASE_PATH + ret;
		return ret;
	}

	public static ApkBean unzipApk(String zipFileName) {
		ZipFile zipFile = null;
		ApkBean apk = null;
		try {
			zipFile = new ZipFile(zipFileName);
			apk = getApkPacInfo(zipFile);
			zipFile.close();
		} catch (IOException e) {
			System.err.println("Open zip file failed." + zipFileName);
			apk = null;
			e.printStackTrace();
		}
		return apk;
	}

	/*
	 * @Deprecated private static ApkBean getApkPacInfoDepricated(ZipFile
	 * zipFile) { Enumeration<? extends ZipEntry> enumeration =
	 * zipFile.entries();
	 * 
	 * while (enumeration.hasMoreElements()) { try { ZipEntry zipEntry =
	 * (ZipEntry) enumeration.nextElement(); if
	 * (zipEntry.getName().equals(AXMLFILE_EXT)) { // Open AndroidManifest.xml
	 * ApkBean apk = new ApkBean(); apk.apkLocalPath = zipFile.getName();
	 * List<Certificate> certs = ApkPacInfo .getJarCerts(apk.apkLocalPath); for
	 * (Certificate cert : certs) { CertBean c = new CertBean(); c.certificate =
	 * cert;
	 * 
	 * c.certificateHash = Hash.getHash( ((X509Certificate) cert).getPublicKey()
	 * .getEncoded(), "SHA-512"); apk.certs.add(c); } apk.apkFileChecksum =
	 * Hash.createFileHash(apk.apkLocalPath, "SHA-256");
	 * AXMLPrinter.apkSpy(zipFile.getInputStream(zipEntry), apk);
	 * 
	 * return apk; } } catch (Exception e) {
	 * System.err.println(zipFile.getName()); e.printStackTrace(); continue; } }
	 * return null; }
	 */
	public static List<Certificate> getJarCerts(String apkFileName, ApkBean apk)
			throws IOException {

		JarFile jf = new JarFile(apkFileName, true);
		Enumeration<JarEntry> entries = jf.entries();
		Vector<JarEntry> entriesVec = new Vector<JarEntry>();
		List<Certificate> certs = new ArrayList<Certificate>();

		try {
			byte[] buffer = new byte[8192];
			while (entries.hasMoreElements()) {
				JarEntry je = entries.nextElement();
				apk.jarEntries.add(je);

				entriesVec.addElement(je);
				InputStream is = null;

				is = jf.getInputStream(je);
				while ((is.read(buffer, 0, buffer.length)) != -1) {
				}

				if (is != null) {
					is.close();
				}
			}
			Enumeration<JarEntry> e = entriesVec.elements();
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (jf.getManifest() != null
						&& jf.getManifest().getEntries()
								.containsKey(je.getName())) {
					for (Certificate cert : je.getCertificates()) {
						certs.add(cert);
					}
					return certs;
				}
			}
		} catch (Exception e) {
			System.err.println(apkFileName);
			e.printStackTrace();
		} finally {
			jf.close();
		}
		return certs;
	}

	private static ApkBean getApkPacInfo(ZipFile zipFile) {
		ZipEntry zipEntry = zipFile.getEntry(AXMLFILE_EXT);
		if (zipEntry == null)
			return null;

		try {
			// Open AndroidManifest.xml
			ApkBean apk = new ApkBean();
			apk.apkLocalPath = zipFile.getName();
			List<Certificate> certs = getJarCerts(apk.apkLocalPath, apk);
			for (Certificate cert : certs) {
				CertBean c = new CertBean();
				c.certificate = cert;
				c.certificateHash = Hash.getHash(cert.getPublicKey()
						.getEncoded(), "SHA-512");
				apk.certs.add(c);
			}
			apk.apkFileChecksum = Hash.createFileHash(apk.apkLocalPath,
					"SHA-256");
			// Get all entries in AndroidManifest.xml
			AXMLPrinter.apkSpy(zipFile.getInputStream(zipEntry), apk);

			if (apk.getPackageName() != null)
				return apk;

		} catch (Exception e) {
			System.err.println(zipFile.getName());
			e.printStackTrace();

		}

		return null;
	}

	public static List<ApkBean> getAllApk(String path) {
		List<File> files = FileUtilsExt.getAllFiles(path, null);
		List<ApkBean> apks = new ArrayList<ApkBean>();
		for (File file : files) {
			ApkBean apk = unzipApk(file.getAbsolutePath());
			if (apk != null)
				apks.add(apk);
		}
		return apks;
	}
}
