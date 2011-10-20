package org.fxp.android.apk.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;

public class ApkEditorCmd {
	static BufferedReader in;

	public static void printUsage() {
		System.out.println("ApkEditorCmd apkFile");
		System.out.println("ApkEditorCmd apkDirectory");
	}

	public static void processApks(File baseDir) {
		File[] files = baseDir.listFiles();
		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk != null) {
				processApk(apk);
			}
		}
	}

	public static void processApk(ApkBean apk) {
		try {
			// Display all apk info
			System.out.println(apk.toString());
			
			ApkManDao mysqlDao = ApkManDao.GetInstance();
			
			if (mysqlDao.fillApk(apk) != null) {
				// No such apk in mysql
				System.out.println("应用汇中名称： "+apk.getMarketBean().getMarketAppName());
				System.out.println("应用汇中描述： "+apk.getMarketBean().getMarketDescription());
			}else
				System.out.println("应用汇中不存在此Apk");								

			if (ApkEditorDAO.getApk(apk) != null) {				
				// No such apk in objectdb
				for(CertBean cert:apk.getCerts()){
					System.out.println("呀!有note已经存在!");					
					System.out.println(cert.getNote());
				}
			}else{
				System.out.println("这是个尚未Note过的应用");												
			}
			
			// Enter note and other stuffs
			System.out.println("输入新的Note吧：");
			String note = in.readLine();
			for(CertBean cert:apk.getCerts()){
				cert.setNote(note);			
			}
			
			System.out.println("保存？ 'y'=yes, 其他任何键（除了回车）=cancel");
			if(in.readLine().toLowerCase().equals("y")){
				ApkEditorDAO.putApk(apk);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		File checkFile = new File(args[0]);
		in = new BufferedReader(new InputStreamReader(System.in));
		ApkEditorDAO.init();

		if (checkFile.isDirectory()) {
			// Check every apk in this directory
			processApks(checkFile);
		} else if (checkFile.isFile()) {
			// Check this apk
			ApkBean apk=ApkFileManager.unzipApk(checkFile.getAbsolutePath());
			if(apk!=null)
				processApk(apk);
		} else {
			printUsage();
			return;
		}
	}

}
