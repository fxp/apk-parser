package org.fxp.android.apk.processor;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

public class YingyonghuiLibTest {

	private static String yingyonghuiLib = "G:\\apkDownload\\yingyonghui\\apks";

	public static void main(String[] args) {
		List<File> files = FileUtilsExt.getAllFiles(yingyonghuiLib, null);
		Hashtable<String, File> hashtable = new Hashtable<String, File>();

		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
				System.out.println("BadApk," + file.getAbsolutePath());
				continue;
			}
			if (hashtable.get(apk.getPackageName()) != null) {
				File fileTmp = hashtable.get(apk.getPackageName());
				try {
					int existId = Integer.valueOf(fileTmp.getName().substring(
							0, fileTmp.getName().indexOf('_')));
					int newId = Integer.valueOf(file.getName().substring(0,
							file.getName().indexOf('_')));
					System.out.println(existId + "," + newId);
					if (((existId - 100000) / 100000.0)
							* ((newId - 100000) / 100000.0) < 0)
						System.out.println("Conflict," + apk.getApkLocalPath()
								+ "," + fileTmp.getAbsoluteFile());
				} catch (Exception e) {
					System.err.println(file.getAbsolutePath());
					continue;
				}
			} else {
				hashtable.put(apk.getPackageName(), file);
			}
		}
	}

}
