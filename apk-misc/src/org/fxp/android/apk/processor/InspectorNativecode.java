package org.fxp.android.apk.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

public class InspectorNativecode {

	static BufferedWriter logWriter;
	
	public static void writeLog(String key, String body) throws IOException {
		logWriter.write(key + "," + body + "\r\n");
		logWriter.flush();
		System.out.println(key + "," + body + "\r\n");
	}
	public static void main(String[] args) throws IOException {
		String basePath = "/home/fxp/apktest";
		logWriter = new BufferedWriter(new FileWriter("apk_native.6-7.cvs"));
		
		inspectApks(basePath);

		logWriter.close();
	}

	public static List<String> inspectApks(String folderName) {
		File baseFile = new File(folderName);
		File[] files = baseFile.listFiles();

		int total = files.length;
		List<String> validApks = new ArrayList<String>();
		int scanCount=0;
		int validCount = 0;
		int nativeCount = 0;
		for (File file : files) {
			scanCount++;
			System.out.print("|");
			try {
				ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
				if (apk != null) {
					System.out.print("*");
					validCount++;
					validApks.add(apk.getApkLocalPath());
					List<String> natives = inspectApk(apk);

					if (natives.size() > 0) {
						String strAds = "";
						for (String ad : natives) {
							strAds += ad + ",";
						}
						writeLog(apk.getApkLocalPath(), strAds);
						nativeCount++;
						System.out.println(scanCount+"\t");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}
	public static List<String> inspectApk(ApkBean apk) throws IOException {
		Hashtable<String, String> natives = new Hashtable<String, String>();
		for(JarEntry entry:apk.jarEntries){
			if(entry.getName().endsWith(".so"))
				natives.put( entry.getName(),apk.getApkLocalPath());
		}
		
		Set<String> keys = natives.keySet();
		List<String> nativesArray = new ArrayList<String>();
		for(String key:keys){
			nativesArray.add(key);
			System.out.println(key);
		}
		
		return nativesArray;
	}
}
