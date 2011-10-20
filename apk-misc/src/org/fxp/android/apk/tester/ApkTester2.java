package org.fxp.android.apk.tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

public class ApkTester2 {
	public static XMLConfiguration config;

	public static String ADB = "platform-tools\\adb";
	public static String MONKEYRUNNER = "tools\\monkeyrunner.bat";
	public static String screenshotScript = "screenshot.script";

	public static String ADB_SHELL = "adb.exe shell ";
	public static String UNINSTALL_APK = "adb.exe uninstall ";
	public static String INSTALL_Apk_CMD = "adb.exe install ";
	public static String EXE_APK = "am start -n ";

	public static String workspaceFolder;
	public static String SDK_PATH;
	public static File apkFolder;
	public static File successFolder;
	public static File failedFolder;
	public static File errorFolder;

	public static void printUsage() {

	}

	public static void main(String[] args) throws ConfigurationException,
			IOException, InterruptedException {
		// Read parameters
		if (!init(args[0], args[1])) {
			printUsage();
			return;
		}

		// Read every apk file in APK_FOLDER
		File[] allFiles = apkFolder.listFiles();

		for (File file : allFiles) {
			// Parse apk info
			File lock=new File(file.getAbsoluteFile()+".lock");
			if(!lock.createNewFile())
				continue;
			ApkBean apk = parseApk(file.getAbsolutePath());
			

			// If success, run install.py to install an apk into phone
			// Or move it to FAILED_FOLDER
			if (apk == null)
				continue;
			if (apk.mainName == null) {
				moveApk(errorFolder, apk);
				continue;
			}
			installAndRunApk(apk);

		}
	}

	public static void moveApk(File folder, ApkBean apk) {
		FileUtilsExt.movefile(apk.apkLocalPath, folder.getAbsolutePath() + "\\"
				+ (new Date()).toString().replace(":", "-") + "."
				+ apk.packageName + "." + apk.versionName + ".apk");
	}

	public static boolean init(String workspacePath, String cfgFile) {
		try {
			config = new XMLConfiguration(new File(cfgFile));

			if (workspacePath.endsWith("\\"))
				workspaceFolder = workspacePath;
			else
				workspaceFolder = workspacePath + "\\";
			apkFolder = new File(workspaceFolder
					+ config.getString("folder-criteria.raw"));
			(new File(config.getString("folder-criteria.finish"))).mkdir();
			successFolder = new File(workspaceFolder
					+ config.getString("folder-criteria.finish"));
			(new File(config.getString("folder-criteria.failed"))).mkdir();
			failedFolder = new File(workspaceFolder
					+ config.getString("folder-criteria.failed"));
			(new File(config.getString("folder-criteria.error"))).mkdir();
			errorFolder = new File(workspaceFolder
					+ config.getString("folder-criteria.error"));
			SDK_PATH = config.getString("folder-criteria.sdk");

			if (!apkFolder.isDirectory() || !successFolder.isDirectory()
					|| !failedFolder.isDirectory()
					|| !errorFolder.isDirectory() || SDK_PATH == null)
				return false;

		} catch (ConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ApkBean parseApk(String apkPath) {
		ApkBean apk = null;
		try {
			apk = ApkFileManager.unzipApk(apkPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return apk;
	}

	public static String decorateParameter(String param) {
		return " " + param + " ";
	}

	public static boolean installAndRunApk(final ApkBean apk) {
		System.out.println();
		System.out.println("Checking " + apk.apkLocalPath);
		String installApkCmd = INSTALL_Apk_CMD + "\"" + apk.apkLocalPath + "\"";

		// launch EXE and grab stdin/stdout and stderr
		Process process;
		try {
			process = Runtime.getRuntime().exec(installApkCmd);
			StreamGobbler errorGobbler = new StreamGobbler(
					process.getErrorStream(), "ERROR");
			errorGobbler.start();
			StreamGobbler outGobbler = new StreamGobbler(
					process.getInputStream(), "STDOUT");
			outGobbler.start();

			int result = process.waitFor();
			if (result == 0) {
				System.out.println("Installed " + apk.packageName);
				if (runApk(apk))
					return true;
				else
					return true;
			} else {
				System.out.println("InstallFailed " + apk.packageName);
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean runApk(ApkBean apk) throws IOException,
			InterruptedException {
		System.out.println("TryingRun " + apk.packageName);
		Process process = Runtime.getRuntime().exec(
				ADB_SHELL + EXE_APK + apk.packageName + "/"
						+ apk.mainName);
		StreamGobbler errorGobbler = new StreamGobbler(
				process.getErrorStream(), "ERROR");
		errorGobbler.start();
		StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(),
				"STDOUT");
		outGobbler.start();

		Scanner sc = new Scanner(System.in);
		System.out.println("Version code: " + apk.versionCode);
		System.out.println("Version name: " + apk.versionName);
		System.out.println("MainActivity " + apk.mainName);
		String cmd = null;
		process.waitFor();
		while (true) {
			System.out.println("Run?(Y/N/Q)");
			cmd = sc.nextLine();
			if (cmd.equalsIgnoreCase("y")) {
				System.out.println("Roger!");
				// Install and run successfully
				process.destroy();
				screenshot(apk);
				FileUtilsExt.movefile(apk.apkLocalPath, successFolder + "\\"
						+ apk.marketBean.marketAppName + "\\" + apk.versionName + ".apk");
				(new File(apk.apkLocalPath+".lock")).delete();
				break;
			} else if (cmd.equalsIgnoreCase("n")) {
				System.out.println("Bad boy, kick out!");
				// Install and run failed
				process.destroy();
				FileUtilsExt.movefile(
						apk.apkLocalPath,
						failedFolder
								+ "\\"
								+ (new Date()).toString().replace(":", "-")
										.replace(" ", "_") + "_"
								+ apk.packageName + "." + apk.versionCode
								+ ".apk");
				(new File(apk.apkLocalPath+".lock")).delete();
				break;
			} else if (cmd.equalsIgnoreCase("q")) {
				System.out.println("Bye lalala!");
				// Install and run failed
				process.destroy();
				uninstallApk(apk);
				(new File(apk.apkLocalPath+".lock")).delete();
				System.exit(0);
			}
		}

		uninstallApk(apk);
		return false;
	}

	public static boolean uninstallApk(ApkBean apk) throws IOException,
			InterruptedException {
		System.out.println("Unistalling " + apk.apkLocalPath);
		Process process = Runtime.getRuntime().exec(
				UNINSTALL_APK + apk.packageName);
		StreamGobbler errorGobbler = new StreamGobbler(
				process.getErrorStream(), "ERROR");
		errorGobbler.start();
		StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(),
				"STDOUT");
		outGobbler.start();

		process.waitFor();
		return false;
	}

	public static String createNewApkDir(ApkBean apk) {
		System.out.println("App name(default " + apk.packageName
				+ ")?(ENTER/[Name])");
		apk.marketBean.marketAppName = apk.packageName;

		Scanner sc = new Scanner(System.in);
		String cmd = sc.nextLine();
		if (!cmd.equals("")) {
			apk.marketBean.marketAppName = cmd;
		}

		boolean success = (new File(successFolder.getAbsoluteFile() + "\\"
				+ apk.marketBean.marketAppName)).mkdir();
		if (success) {
			System.out.println("Directory: " + apk.marketBean.marketAppName + " created");
			return successFolder.getAbsoluteFile() + "\\" + apk.marketBean.marketAppName
					+ "\\";
		} else {
			System.out.println("Directory: " + apk.marketBean.marketAppName
					+ " existed or create failed");
			return successFolder.getAbsoluteFile() + "\\" + apk.marketBean.marketAppName
					+ "\\";
		}
	}

	public static String screenshot(ApkBean apk) {
		String newFolder = createNewApkDir(apk);

		String installApkCmd = workspaceFolder + SDK_PATH + "\\" + MONKEYRUNNER
				+ " " + workspaceFolder + screenshotScript + " " + "\""+newFolder+"\"\\";

		// launch EXE and grab stdin/stdout and stderr
		Process process;
		try {
			process = Runtime.getRuntime().exec(installApkCmd);
			StreamGobbler errorGobbler = new StreamGobbler(
					process.getErrorStream(), "ERROR");
			errorGobbler.start();
			StreamGobbler outGobbler = new StreamGobbler(
					process.getInputStream(), "STDOUT");
			outGobbler.start();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					process.getOutputStream()));

			Scanner sc = new Scanner(System.in);
			String cmd;
			while (true) {

				cmd = sc.nextLine();
				bw.write(cmd + "\n");
				bw.flush();
				if (cmd.equals("q"))
					break;
			}

			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return newFolder;
	}
	/*
	 * public static boolean allInOne(ApkBean apk) { String installApkCmd =
	 * SCREENSHOT + decorateParameter(apkFolder.getAbsolutePath() + "\\");
	 * 
	 * installApkCmd += decorateParameter(successFolder.getAbsolutePath() +
	 * "\\"); installApkCmd += decorateParameter(failedFolder.getAbsolutePath()
	 * + "\\"); installApkCmd +=
	 * decorateParameter(apk.apkLocalPath.replace("\\", "\\\\")); installApkCmd
	 * += decorateParameter(apk.apkPackageName); installApkCmd +=
	 * decorateParameter(apk.apkMainName); installApkCmd +=
	 * decorateParameter(apk.apkVersionName);
	 * 
	 * // launch EXE and grab stdin/stdout and stderr Process process; try {
	 * process = Runtime.getRuntime().exec(installApkCmd); StreamGobbler
	 * errorGobbler = new StreamGobbler( process.getErrorStream(), "ERROR");
	 * errorGobbler.start(); StreamGobbler outGobbler = new StreamGobbler(
	 * process.getInputStream(), "STDOUT"); outGobbler.start();
	 * 
	 * BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
	 * process.getOutputStream()));
	 * 
	 * Scanner sc = new Scanner(System.in); String cmd; while (true) { cmd =
	 * sc.nextLine(); if (cmd.equals("q")) break; bw.write(cmd + "\n");
	 * bw.flush(); }
	 * 
	 * if (process.waitFor() == 0) {
	 * 
	 * } else { System.out.println("InstallFailed " + apk.apkPackageName);
	 * return false; }
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } catch
	 * (InterruptedException e) { e.printStackTrace(); } return false; }
	 */
}
