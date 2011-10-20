package org.fxp.android.apk.tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

import com.android.monkeyrunner.MonkeyRunner;

public class ApkTester1 {

	public static String INSTALL_Apk_CMD = "adb.exe install ";
	public static String EXT_APK = "adb.exe shell ";
	public static String UNINSTALL_APK = "adb.exe uninstall ";
	public static String SCREENSHOT = "adb pull /dev/graphics/fb0 fb0";
	public static String SCREENSHOT2 = "monkeyrunner.bat ";
	public static File screenshotScript;
	public static String CONVERT_SCREENSHOT = "ffmpeg.exe -vframes 1 -vcodec rawvideo -f rawvideo -pix_fmt rgb565 -s 320x480 -i fb0 -f image2 -vcodec png ";

	public static String workspaceFolder;
	public static String rawFolder;
	public static String failedFolder;
	public static String successFolder;
	public static String errorFolder;
	public static String sdkFolder;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out
					.println("Usage: apk_tester_assistant [apkFolder] [successFolder] [failedFolder]");
			return;
		}
		rawFolder = args[0];
		successFolder = args[1];
		failedFolder = args[2];

		screenshotScript = new File(args[3]);
		if (!screenshotScript.isFile())
			return;

		File apkDir = new File(rawFolder);
		File successDir = new File(successFolder);
		File failedDir = new File(failedFolder);
		if (!apkDir.isDirectory() || !successDir.isDirectory()
				|| !failedDir.isDirectory() || !rawFolder.endsWith("\\")
				|| !successFolder.endsWith("\\")
				|| !failedFolder.endsWith("\\")) {
			System.out
					.println("Usage: apk_tester_assistant [apkFolder] [successFolder] [failedFolder]");
			return;
		}

		ApkFileManager.GetInstance();
		if (apkDir.isDirectory()) {
			File[] files = apkDir.listFiles();
			for (File file : files) {
				ApkTester1.installAndRunApk(file.getAbsolutePath());
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean installAndRunApk(String apkFile) {
		System.out.println();
		System.out.println("Checking " + apkFile);
		String installApkCmd = INSTALL_Apk_CMD + apkFile;
		ApkBean apk = ApkFileManager.unzipApk(apkFile);
		if (apk == null || apk.packageName == null || apk.marketBean.marketAppName == null) {
			System.out.println("BadApk " + apkFile);
			return false;
		}

		System.out.println("Installing " + apk.packageName);
		System.out.println("VersionCode " + apk.versionCode);
		System.out.println("Path " + apk.apkLocalPath);
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
			if (process.waitFor() == 0) {
				System.out.println("Installed " + apk.packageName);
				if (runApk(apk))
					return true;
				else
					return false;
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
		Process process = Runtime.getRuntime().exec(EXT_APK);
		StreamGobbler errorGobbler = new StreamGobbler(
				process.getErrorStream(), "ERROR");
		errorGobbler.start();
		StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(),
				"STDOUT");
		outGobbler.start();

		String startApkCmd = "am start -a android.intent.action.MAIN -n "
				+ apk.packageName + "/" + apk.mainName;

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				process.getOutputStream()));

		bw.write(startApkCmd + "\n");
		bw.flush();
		Scanner sc = new Scanner(System.in);
		System.out.println("Package name: " + apk.packageName);
		System.out.println("Version code: " + apk.versionCode);
		System.out.println("Version name: " + apk.versionName);
		String cmd = null;
		while (true) {
			System.out.println("Run?(Y/N/Q)");
			cmd = sc.nextLine();
			if (cmd.equalsIgnoreCase("y")) {
				System.out.println("Roger!");
				// Install and run successfully
				process.destroy();
				saveScreenShot(apk);
				// FileToolkits.movefile(apk.apkLocalPath, successFolder
				// + apk.apkAppName + "\\" + apk.apkVersionName + ".apk");
				break;
			} else if (cmd.equalsIgnoreCase("n")) {
				System.out.println("Bad boy, kick out!");
				// Install and run failed
				process.destroy();
				// FileToolkits.movefile(apk.apkLocalPath, failedFolder
				// + apk.apkPackageName + "." + apk.apkVersionCode + ".apk");
				break;
			} else if (cmd.equalsIgnoreCase("q")) {
				System.out.println("Bye lalala!");
				// Install and run failed
				process.destroy();
				uninstallApk(apk);
				System.exit(0);
			}
		}
		if (process.waitFor() == 0) {
			System.out.println("Run success");
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

	public static boolean createNewApkDir(ApkBean apk) {
		System.out.println("Is '" + apk.marketBean.marketAppName
				+ "' right app name?(ENTER/[Name])");
		Scanner sc = new Scanner(System.in);
		String cmd = sc.nextLine();
		if (!cmd.equals("")) {
			apk.marketBean.marketAppName = cmd;
		}

		boolean success = (new File(successFolder + apk.marketBean.marketAppName)).mkdir();
		if (success) {
			System.out.println("Directory: " + apk.marketBean.marketAppName + " created");
			return true;
		} else {
			System.out.println("Directory: " + apk.marketBean.marketAppName
					+ " existed or create failed");
			return false;
		}

	}

	public static void saveScreenShot(ApkBean apk) {
		if (!createNewApkDir(apk))
			return;

		System.out.println("Begin to take screen shot, press S for one kacha!");
		while (true) {
			System.out
					.println("Press 'q' to finish. (Press any key to take another screen shot)");

			Process process;
			try {
				process = Runtime.getRuntime().exec(
						SCREENSHOT2 + screenshotScript.getAbsolutePath() + " "
								+ successFolder + apk.marketBean.marketAppName);
				// process = Runtime.getRuntime().exec(SCREENSHOT);

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
					System.out.println("Waiting?");
					cmd = sc.nextLine();
					if (cmd.equals("q"))
						break;
					bw.write(cmd + "\n");
					bw.flush();
				}

				process.waitFor();
				return;
				/*
				 * process = Runtime.getRuntime().exec( CONVERT_SCREENSHOT +"\""
				 * +fileName+"\""); errorGobbler = new
				 * StreamGobbler(process.getErrorStream(), "ERROR");
				 * errorGobbler.start(); outGobbler = new
				 * StreamGobbler(process.getInputStream(), "STDOUT");
				 * outGobbler.start();
				 * 
				 * process.waitFor();
				 */} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
