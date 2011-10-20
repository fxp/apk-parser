package com.iw.apk.inspector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fxp.android.apk.ad.*;
import pxb.android.dex2jar.v3.Dex2Jar;
import apkReader.ApkInfo;
import apkReader.ApkReader;

public class AdInspector {

	static BufferedWriter logWriter;

	static List<AdPattern> adPatterns = new ArrayList<AdPattern>();

	public static void loadAdPatterns() {
		adPatterns.add(new Adchina());
		adPatterns.add(new Admob());
		adPatterns.add(new Adsmogo());
		adPatterns.add(new Adtouch());
		adPatterns.add(new Adview());
		adPatterns.add(new Adwo());
		adPatterns.add(new Airad());
		adPatterns.add(new Appmedia());
		adPatterns.add(new Baidumobad());
		adPatterns.add(new Domob());
		adPatterns.add(new Greystripe());
		adPatterns.add(new Guohe());
		adPatterns.add(new Inmobi());
		adPatterns.add(new Jiashi());
		adPatterns.add(new Lmmob());
		adPatterns.add(new MdotM());
		adPatterns.add(new Millennial());
		adPatterns.add(new Vpon());
		adPatterns.add(new Wabang());
		adPatterns.add(new Wanpu());
		adPatterns.add(new Weiyun());
		adPatterns.add(new Winad());
		adPatterns.add(new Youmi());
	}

	public static void main(String[] args) throws IOException {
		loadAdPatterns();

		logWriter = new BufferedWriter(new FileWriter("apk_ad_10-9.cvs"));

		MonitorDB db = new MonitorDB("apkinfo");
		
		File dst = new File(args[0]);
		if (dst.isDirectory()) {
			File[] files = dst.listFiles();
			// File[] files = (new File("/home/fxp/apktest/")).listFiles();

			for (File file : files) {
				if (!file.isFile())
					return;
				// Check every apk here
				try {
					ApkInfo apkInfo = new ApkInfo();
					AdResult result = inspectApk(file.getAbsolutePath(),apkInfo);
					printResult(result);
					db.insertMonitorAppItem(apkInfo);
				} catch (Exception e) {
					System.err
							.println("WrongParsing=" + file.getAbsolutePath());
					// e.printStackTrace();
				}
			}
		} else {
			doApk(dst.getAbsolutePath());
		}
		
		db.close();

		logWriter.close();
	}

	public static void doApk(String apkPath) {

		File file = new File(apkPath);
		if (!file.isFile())
			return;
		// Check every apk here
		try {
			ApkInfo apkInfo = new ApkInfo();
			AdResult result = inspectApk(file.getAbsolutePath(),apkInfo);
			printResult(result);
		} catch (Exception e) {
			System.err.println("WrongParsing=" + file.getAbsolutePath());
			// e.printStackTrace();
		}
	}

	private static void printResult(AdResult result) throws IOException {
		List<AdPattern> ads = result.getDetectedAds();
		// if (ads.size() == 0)
		// return;

		logWriter.append(result.getApkPath() + ",");
		System.out.print(result.getApkPath() + ",");
		for (AdPattern ad : ads) {
			logWriter.append(ad.getName() + ",");
			System.out.print(ad.getName() + ",");
		}
		logWriter.append("\r\n");
		System.out.println();
		logWriter.flush();
	}

	public static AdResult inspectApk(String apkPath,ApkInfo apkInfo) {
		AdResult ret = new AdResult(apkPath);
		ApkReader apkReader = new ApkReader();
		try {
			apkReader.read(apkPath, apkInfo);
		} catch (Exception e) {
		}

		try {
			Dex2Jar.doApk(apkPath, apkInfo);
		} catch (Exception e) {
		}

		for (AdPattern adPattern : adPatterns) {
			if (adPattern.isExist(apkInfo))
				ret.addDetectedAd(adPattern);
		}

		return ret;
	}
}
