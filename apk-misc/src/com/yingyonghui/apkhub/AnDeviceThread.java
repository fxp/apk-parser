package com.yingyonghui.apkhub;

import java.util.Hashtable;
import java.util.Set;

import org.fxp.android.apk.ApkFileManager;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceState;
import com.android.ddmlib.InstallException;

public class AnDeviceThread implements Runnable {

	// For displaying to user
	String tagName;
	// Device unique name
	String devName;
	// Whether is
	boolean adbInit = false;
	
	IDevice iDev;
	DeviceState deviceStatus;
	// Apks to be installed or have been installed
	Hashtable<String, Integer> apkLib = new Hashtable<String, Integer>();

	public AnDeviceThread(IDevice iDev) {
		this.iDev = iDev;
	}

	public void close() {
		
	}

	synchronized public void insertApk(String apkFilePath) {
		apkLib.put(apkFilePath, ApkInstallStatus.INIT);
	}

	synchronized public void removeAllApk() {
		apkLib.clear();
	}

	private boolean installApk(String apkFilePath) {
		System.out
				.println("Installing@" + iDev.getSerialNumber() + apkFilePath);
		if (!iDev.isOnline()) {
			return false;
		}
		String ret = null;
		try {
			ret = iDev.installPackage(apkFilePath, true);
			if (ret == null) {
				System.out.println("Install complete@" + iDev.getSerialNumber()
						+ apkFilePath);
				return true;
			}
		} catch (InstallException e) {
			// e.printStackTrace();
			System.err.println("InstallErr@" + iDev.getSerialNumber() + "#MSG:"
					+ e.getCause().getMessage() + ",#RET:" + ret);
		}
		return false;
	}

	public boolean uninstallPackage(String packageName) {
		System.out.println("Uninstalling " + packageName);
		if (!iDev.isOnline()) {
			return false;
		}
		String ret = null;
		try {
			ret = iDev.uninstallPackage(packageName);
			if (ret == null) {
				System.out.println("Uninstall complete@"
						+ iDev.getSerialNumber() + packageName);
				return true;
			}
		} catch (InstallException e) {
			// e.printStackTrace();
			System.err.println("UninstallErr@" + iDev.getSerialNumber()
					+ "#MSG:" + e.getCause().getMessage() + ",#RET:" + ret);
		}
		return false;
	}

	public void getPackageList() {

	}

	public static int RETRY_INTERVAL_TIMER = 5000;

	@Override
	public void run() {
		getPackageList();
		// Install new apks
		System.out.println(apkLib.size() + " apks to install");
		long t1 = System.currentTimeMillis();
		Set<String> apkToInstall = apkLib.keySet();
		for (String apkFilePath : apkToInstall) {
			System.out.println("ToInstall@" + iDev.getSerialNumber() + ",#Apk:"
					+ apkFilePath);
			long t1_1 = System.currentTimeMillis();

			while (!uninstallPackage(ApkFileManager.unzipApk(apkFilePath)
					.getPackageName())) {
				try {
					Thread.sleep(RETRY_INTERVAL_TIMER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long t1_2 = System.currentTimeMillis();
			// System.out.println("Uninstall(" + (t1_2 - t1_1) + "ms)");
			long t2_1 = System.currentTimeMillis();
			while (!installApk(apkFilePath)) {
				try {
					Thread.sleep(RETRY_INTERVAL_TIMER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long t2_2 = System.currentTimeMillis();
			// System.out.println("Install(" + (t2_2 - t2_1) + "ms)");
		}
		long t2 = System.currentTimeMillis();
		System.out.println("TotalComplete@" + iDev.getSerialNumber()
				+ ",#cost:" + (t2 - t1) + "ms");
		// Disconnect
		close();
	}

}
