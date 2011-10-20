package org.fxp.android.market.api;

import java.sql.SQLException;

import org.fxp.android.market.worker.ApkLibExt;

public class Yingyonghui {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		ApkLibExt apkLib = new ApkLibExt();
		String[] allPackageNames=apkLib.getPackageNames();
		System.out.println("Transfer new apk "+apkLib.transferDiffApk());
		
	}
}
