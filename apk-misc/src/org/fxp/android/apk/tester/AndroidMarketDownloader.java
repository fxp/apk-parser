package org.fxp.android.apk.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

public class AndroidMarketDownloader {

	static String PULL_APK_CMD = "adb pull ";
	static String DEL_APK_CMD = "adb uninstall ";
	static String APK_DOWN_PATH = "downloaded";
	static String[] apkPreffixs = { "/data/data/", "/data/app/" };
	static String[] apkSuffixs = { "-1.apk", "-2.apk", "-1.zip", "-2.zip",
			"-3.apk", "-4.apk", "-3.zip", "-4.zip" };

	public static void main(String[] args) {
		try {
			(new File(APK_DOWN_PATH)).mkdir();
			Boolean end = false;
			ServerSocket ss = new ServerSocket(22222);
			while (!end) {
				Socket s = ss.accept();
				BufferedReader input = new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				PrintWriter output = new PrintWriter(s.getOutputStream(), true);
				String echo = input.readLine();
				System.out.println(echo);
				output.println("Echo from server:" + echo);
				String pacName = echo.split("\t")[1];
				s.close();
				String apkPath = pullApk(pacName);
				if (apkPath == null)
					continue;
				ApkBean apk = ApkFileManager.unzipApk(apkPath);
				uninstallApk(apk.getPackageName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String pullApk(String packageName) {
		for (int j = 0; j < apkPreffixs.length; j++) {
			for (int i = 0; i < apkSuffixs.length; i++) {
				String pullApkCmd = PULL_APK_CMD + apkPreffixs[j] + packageName
						+ apkSuffixs[i] + " " + APK_DOWN_PATH + "\\"
						+ packageName;
				System.out.println(pullApkCmd);
				Process process;
				try {
					process = Runtime.getRuntime().exec(pullApkCmd);
					StreamGobbler errorGobbler = new StreamGobbler(
							process.getErrorStream(), "ERROR");
					errorGobbler.start();
					StreamGobbler outGobbler = new StreamGobbler(
							process.getInputStream(), "STDOUT");
					outGobbler.start();

					int result = process.waitFor();
					if (result == 0) {
						System.out.println("Pulling success");
						return APK_DOWN_PATH + "\\" + packageName;
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.err.println("Pulling failed" + packageName);
		return null;
	}

	public static boolean uninstallApk(String packageName) {
		System.out.println("Uninstall " + packageName);
		String pullApkCmd = DEL_APK_CMD + packageName;
		Process process;
		try {
			process = Runtime.getRuntime().exec(pullApkCmd);
			StreamGobbler errorGobbler = new StreamGobbler(
					process.getErrorStream(), "ERROR");
			errorGobbler.start();
			StreamGobbler outGobbler = new StreamGobbler(
					process.getInputStream(), "STDOUT");
			outGobbler.start();

			int result = process.waitFor();
			if (result == 0) {
				System.out.println("Uninstall success");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Uninstall failed");
		return false;
	}
}
