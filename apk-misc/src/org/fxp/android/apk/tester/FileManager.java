package org.fxp.android.apk.tester;

import java.io.File;

import javax.swing.JOptionPane;

import org.fxp.android.apk.ApkBean;
import org.fxp.tools.FileUtilsExt;

public class FileManager {

	private static String FINISH_FOLDER = "finish";
	private static String FAILED_FOLDER = "failed";
	private static String ERROR_FOLDER = "error";
	private File finishFolder;
	private File failedFolder;
	private File errorFolder;

	public FileManager(String workspace) {
		File wsFile = new File(workspace);
		workspace = wsFile.getAbsolutePath();

		finishFolder = new File(workspace + File.separator + FINISH_FOLDER);
		failedFolder = new File(workspace + File.separator + FAILED_FOLDER);
		errorFolder = new File(workspace + File.separator + ERROR_FOLDER);
		finishFolder.mkdir();
		failedFolder.mkdir();
		errorFolder.mkdir();
	}

	public boolean putToFinish(ApkBean apk, String dstName) {
		return putToFolder(apk, finishFolder.getAbsolutePath(), dstName);
	}

	public boolean putToFailed(ApkBean apk, String dstName) {
		return putToFolder(apk, failedFolder.getAbsolutePath(), dstName);
	}

	public boolean putToError(ApkBean apk, String dstName) {
		return putToFolder(apk, errorFolder.getAbsolutePath(), dstName);
	}

	private boolean putToFolder(ApkBean apk, String folderPath, String dstName) {
		folderPath = folderPath + File.separatorChar;
		String src = apk.getApkLocalPath();
		String dst = folderPath + dstName + ".apk";
		FileUtilsExt.movefile(src, dst);
		if ((new File(src).isFile()))
			return false;
		else if (!(new File(dst).isFile()))
			return false;
		return true;
	}

	public String createNewDir(ApkBean apk,String appName) {
		
		apk.getMarketBean().setMarketAppName(appName);
		String newDir = finishFolder.getAbsoluteFile() + File.separator
				+ appName;
		(new File(newDir)).mkdir();

		return newDir + File.separator;
	}
}
