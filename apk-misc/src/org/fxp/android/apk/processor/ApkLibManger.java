package org.fxp.android.apk.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

public class ApkLibManger {

	private static String apkLib = null;

	Hashtable<String, String> PacknamePathTable = new Hashtable<String, String>();
	static List<Integer> versions = new ArrayList<Integer>();

	public static void main(String[] args) {
		apkLib = args[0];
		File libFolder = new File(apkLib);
		File errFolder = new File(".." + File.separator + "err");
		File dupFolder=new File(".." + File.separator + "dup");
		errFolder.mkdir();
		dupFolder.mkdir();
		
		List<File> files = FileUtilsExt.getAllFiles(
				libFolder.getAbsolutePath(), null);

		for (File file : files) {
			if(file.isDirectory())
				continue;
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
					FileUtilsExt.movefile(file.getAbsolutePath(),errFolder.getAbsolutePath() + File.separator
									+ file.getName());
				System.out.println("BadApk," + file.getAbsolutePath());
				continue;
			}else{
				if (!versions.contains(apk.getVersionCode())){
					FileUtilsExt.movefile(file.getAbsolutePath(),libFolder.getAbsolutePath() + File.separator
							+ apk.getVersionCode()+".apk");
				}else{
					FileUtilsExt.movefile(file.getAbsolutePath(),dupFolder.getAbsolutePath() + File.separator
							+ file.getName());
				}
			}
			System.out.println("|");
		}
	}
}
