package org.fxp.android.market.worker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;
import org.fxp.tools.OnlyExt;

public class ApkImporter {

	private static String LibBasePath = "D:\\apk\\";
	private static String TempBasePath = "D:\\apk_file\\";

	private static Map<String, ApkBean> map = new HashMap<String, ApkBean>();
	private static int libApkNum = 0;
	private static int checkedApkNum = 0;
	private static boolean moveToLib=false;

	private static BufferedWriter out = null;

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Usage: ApkImporter libPath tempPath moveToLib");
			System.out.println("libPath: libray directory");
			System.out.println("tempPath: import directory");
			System.out.println("moveToLib: [true/false] true means to move apk to libray, false means just to copy");
			return;
		}
		LibBasePath = args[0];
		TempBasePath = args[1];
		moveToLib=Boolean.valueOf(args[2]);

//		loadLibApks();
//		System.out.println("Number of apk from lib:" + map.size());
		doImport();
		System.out.println("Number of apk checked:" + checkedApkNum);
	}

	private static void logApk(String type, String info) throws IOException {
		if (out == null)
			out = new BufferedWriter(new FileWriter("apk_import.log"));

		try {
			out.write(type + "," + info);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(type + "," + info);
	}

	private static void loadLibApks() throws IOException {
		// FilenameFilter ff = new OnlyExt("apk");

		// File[] files = FileToolkits.getAllFiles(new File(LibBasePath), ff);
		File[] files = FileUtilsExt.getAllFiles(new File(LibBasePath), null);
		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
				logApk("FAILED_READ_LIB", file.getAbsolutePath());
				System.out.print("|");
				continue;
			}
			map.put(apk.packageName, apk);
			libApkNum++;
		}
	}

	private static void doImport() throws IOException {
		File tempDir = new File(TempBasePath);
		File[] tempFiles = tempDir.listFiles();

		for (File file : tempFiles) {
			checkedApkNum++;

			ApkBean apkTmp = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apkTmp == null) {
				logApk("FAILED_READ_TEMP", file.getAbsolutePath());
				continue;
			}
			
			// ApkBean apkLib = map.get(apkTmp.packageName);
			String apkLibFileName = LibBasePath + apkTmp.packageName + "."
			+ apkTmp.versionCode + ".apk";
			ApkBean apkLib = ApkFileManager.unzipApk(apkLibFileName);
			
			if (apkLib == null) {
				if(moveToLib)
					FileUtilsExt.movefile(apkTmp.apkLocalPath, LibBasePath
						+ ApkFileManager.getApkFileName(apkTmp, "LIB", false));
				else
					FileUtilsExt.copyfile(apkTmp.apkLocalPath, LibBasePath
							+ ApkFileManager.getApkFileName(apkTmp, "LIB", false));
				logApk("NEW_APK", file.getAbsolutePath());
				continue;
			}

			try {
				// TODO verify every certificate
				X509Certificate certLib = (X509Certificate) apkLib.certs.get(0).certificate;
				X509Certificate certTmp = (X509Certificate) apkTmp.certs.get(0).certificate;
				if(certTmp==null||certLib==null){
					logApk("APK_CERT_ERR", file.getAbsolutePath());
					continue;
				}
				if (!certLib.getIssuerX500Principal().equals(
						certTmp.getIssuerX500Principal())
						|| !certLib.getPublicKey().equals(
								certTmp.getPublicKey())) {
					logApk("APK_CERT_CONFLICT", apkTmp.apkLocalPath);
					
					if (apkTmp.certs.get(0).certificate.getPublicKey() != apkLib.certs.get(0).certificate
							.getPublicKey()) {
						logApk("SAME_APK_CONFLICT_PUBKEY", apkTmp.apkLocalPath+","+apkLib.apkLocalPath);
					}
					
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			File libApk = new File(apkLibFileName);
			if (libApk.isFile()) {
				if (FileUtilsExt.compareFile(apkLibFileName, apkTmp.apkLocalPath)) {
					logApk("SAME_APK", apkTmp.apkLocalPath);
					FileUtilsExt.forceDelete(new File(apkTmp.apkLocalPath));
				} else {
					logApk("SAME_APK_CONFLICT_DIFFSIZE", apkTmp.apkLocalPath);
				}
			} else {
				if(moveToLib){
				FileUtilsExt.movefile(apkTmp.apkLocalPath, LibBasePath
						+ apkLibFileName);
				}else{
					FileUtilsExt.copyfile(apkTmp.apkLocalPath, LibBasePath
							+ apkLibFileName);
				}
				logApk("NEW_APK_VERSION", apkTmp.apkLocalPath);
			}
		}
	}
}
