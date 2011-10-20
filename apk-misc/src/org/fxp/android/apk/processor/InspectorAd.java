package org.fxp.android.apk.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.android.apk.ad.AdPattern;
import org.fxp.android.apk.ad.AdWhirl;
import org.fxp.android.apk.ad.Admob;
import org.fxp.android.apk.ad.Appmedia;
import org.fxp.android.apk.ad.Jiashi;
import org.fxp.android.apk.ad.Umeng;
import org.fxp.android.apk.ad.Umengclick;
import org.fxp.android.apk.ad.Wiyun;
import org.fxp.android.apk.ad.Wooboo;
import org.fxp.android.apk.ad.Youmi;
import org.fxp.tools.FileUtilsExt;
import org.fxp.tools.OnlyExt;

import pxb.android.dex2jar.v3.Dex2Jar;

public class InspectorAd {

	static BufferedWriter logWriter;
	static List<AdPattern> adPatterns = new ArrayList<AdPattern>();
	static boolean isCheckEntry = true;
	static boolean isCheckManifest = true;
	static boolean isCheckClass = true;

	public static void loadAdPatterns() {
		adPatterns.add(new Admob());
		adPatterns.add(new AdWhirl());
		adPatterns.add(new Umeng());
		adPatterns.add(new Umengclick());
		adPatterns.add(new Wiyun());
		adPatterns.add(new Jiashi());
		adPatterns.add(new Wooboo());
		adPatterns.add(new Youmi());
		adPatterns.add(new Appmedia());
	}

	/**
	 * @param args
	 * @throws IOException
	 */

	public static void writeLog(String key, String body) throws IOException {
		if (logWriter != null) {
			logWriter.write(key + "," + body + "\r\n");
			logWriter.flush();
		} else
			System.out.println(key + "," + body + "\r\n");
	}

	public static void main(String[] args) throws IOException {
		String basePath = "/home/fxp/apktest/";
		// String apkPath =
		// "/home/fxp/workspace/adInspector/sdk/Umeng/iw.avatar.1305279536729.apk";
		// String apkPath =
		// "/home/fxp/workspace/adInspector/sdk/Youmeng/112899_exc_1.apk";
		loadAdPatterns();

//		logWriter = new BufferedWriter(
//				new FileWriter("apk_ad_appmedia.6-7.cvs"));

		inspectApks(basePath);
		// inspectApk(ApkFileManager.unzipApk(apkPath));

//		logWriter.close();

	}

	public static List<String> inspectApks(String folderName) {
		File baseFile = new File(folderName);
//		File[] files = baseFile.listFiles();
		File[] files= FileUtilsExt.getAllFiles(baseFile, new OnlyExt("apk"));

		int total = files.length;
		List<String> validApks = new ArrayList<String>();
		int scanCount = 0;
		int validCount = 0;
		int adCount = 0;
		for (File file : files) {
			scanCount++;

			// if(validCount>1000)
			// break;
			System.out.print(file.getAbsolutePath()+":");
			try {
				ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
				if (apk != null) {
					validCount++;
					validApks.add(apk.getApkLocalPath());
					List<String> ads = inspectApk(apk);

					if (ads.size() > 0) {
						String strAds = "";
						for (String ad : ads) {
							strAds += ad + ",";
						}
						writeLog(apk.getApkLocalPath(), strAds);
						adCount++;
						System.out.println(scanCount + "\t");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
		System.out.println("Total " + total + ";Valid " + validCount + ";Ad "
				+ adCount);
		return null;
	}

	public static List<String> inspectApk(ApkBean apk) throws IOException {
		Hashtable<String, String> ads = new Hashtable<String, String>();

		// Check all entries
		if (isCheckEntry) {
			for (JarEntry entry : apk.jarEntries) {
				for (AdPattern adPattern : adPatterns) {
					if (adPattern.isExistEntry(entry))
						ads.put(adPattern.getName(), entry.getName());
				}
			}
		}

		// Check manifest
		if (isCheckManifest) {
			for (AdPattern adPattern : adPatterns) {
				if (adPattern.isExistManifest(apk))
					ads.put(adPattern.getName(), "MANIFEST");
			}
		}

		// Decompile all class
		if (isCheckClass) {
			Dex2Jar.doApk(apk.getApkLocalPath(), apk);
			for (AdPattern adPattern : adPatterns) {
				if (adPattern.isExistClass(apk))
					ads.put(adPattern.getName(), "CLASS");
			}
		}

		Set<String> keys = ads.keySet();
		List<String> adsArray = new ArrayList<String>();
		for (String key : keys) {
			adsArray.add(key);
			System.out.println(key);
		}

		return adsArray;
	}
}
