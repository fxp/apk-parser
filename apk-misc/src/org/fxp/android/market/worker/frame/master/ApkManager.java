package org.fxp.android.market.worker.frame.master;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.OnlyExt;

public class ApkManager {

	public static String LIB_PATH = "d:\\apk";
	public static String LOG_NAME = "apkmanager.v1.log";
	public static Logger log = Logger.getLogger(LOG_NAME);
	public static ApkManagerDao dao;
	public static File dir;

	public int init(String libPath) {
		dao = ApkManagerDao.GetInstance();
		if (dao == null) {
			log.log(Level.SEVERE, ApkManagerLog.genLog(
					ApkManagerLog.DB_CONN_FAILED, (new Date()).toString()));
			return ApkManagerLog.DB_CONN_FAILED;
		}
		dir = new File(libPath);
		if (!dir.isDirectory()) {
			log.log(Level.SEVERE,
					ApkManagerLog.genLog(ApkManagerLog.LIB_DONT_EXIST, libPath));
			return ApkManagerLog.LIB_DONT_EXIST;
		}

		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(LOG_NAME);
			fileHandler.setLevel(Level.ALL);
			log.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
			return ApkManagerLog.LOG_CREATE_FAILED;
		}

		return 0;
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ApkManager manager = new ApkManager();
		int ret = manager.init(args[0]);
		if (ret < 0) {
			System.out.println("Error code" + ret);
			return;
		}

		try {
			// Reconstruct database
			ret = manager.recontructDatabase();
			if (ret < 0)
				System.out.println("Error code" + ret);
			else
				System.out.println("Reconstruct database. Items " + ret);

		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Error");
			ApkManagerDao.resetDb();
			while (manager.init(args[0]) < 0) {
				Thread.sleep(10000);
			}
		}

		return;
	}

	public int recontructDatabase() {
		FilenameFilter ff = new OnlyExt("apk");
		File[] files = dir.listFiles(ff);

		// WARRNING: Clear up the whole database

		int count = 0;
		ApkBean apk;
		for (File file : files) {
			System.out.println(++count);
			if (count < 37368)
				continue;
			apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null || apk.certs.size() == 0) {
				log.log(Level.WARNING, ApkManagerLog.genLog(
						ApkManagerLog.PARSE_APK_FAILED, file.getAbsolutePath()));
				continue;
			}
			apk.marketBean.marketName = "unknown";

			// Insert apk info and certification info into database
			if (dao.insertApk(apk) < 0) {
				log.log(Level.WARNING,
						ApkManagerLog.genLog(ApkManagerLog.INSERT_APK_FAILED,
								file.getAbsolutePath()));
				continue;
			}
			if (dao.insertCert(apk) < 0) {
				log.log(Level.WARNING, ApkManagerLog.genLog(
						ApkManagerLog.INSERT_CERT_FAILED,
						file.getAbsolutePath()));
				dao.deleteApk(apk);
				continue;
			}

			log.log(Level.INFO,
					ApkManagerLog.genLog(ApkManagerLog.SUCCESS,
							file.getAbsolutePath()));
		}
		return 0;
	}

}
