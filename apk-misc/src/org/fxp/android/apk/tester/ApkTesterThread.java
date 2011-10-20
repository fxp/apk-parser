package org.fxp.android.apk.tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

public class ApkTesterThread implements Runnable{
	public static String ADB = "platform-tools\\adb";
	public static String MONKEYRUNNER = "tools\\monkeyrunner.bat";
	public static String screenshotScript = "screenshot.script";

	public static String ADB_SHELL = "adb.exe shell ";
	public static String UNINSTALL_APK = "adb.exe uninstall ";
	public static String INSTALL_Apk_CMD = "adb.exe install ";
	public static String EXE_APK = "am start -n ";

	public static String workspaceFolder = "Z:\\apkWorkspace\\";
	public static String SDK_PATH = workspaceFolder+"android-sdk\\";
	public static File apkFolder = new File(workspaceFolder+"raw\\");
	public static File successFolder = new File(workspaceFolder+"finish\\");
	public static File failedFolder = new File(workspaceFolder+"failed\\");;
	public static File errorFolder = new File(workspaceFolder+"error\\");;

	public static String apkPath;
	
	public static String getApkPath() {
		return apkPath;
	}

	public static void setApkPath(String apkPath) {
		ApkTesterThread.apkPath = apkPath;
	}
	Thread t;
	
	
	public static void setIn(){
		
	}
	
	public ApkTesterThread(){
	      // Create a new, second thread
	      t = new Thread(this, "ApkTester Thread");
	      System.out.println("Child thread: " + t);
	      t.start(); // Start the thread
	}
	
	public static void main(String[]  args){
		File[] files=(new File("z:\\apkWorkspace\\raw")).listFiles();
		for(File file:files){
			doApk(file.getAbsolutePath());
		}
	}
	
	public static ApkBean parseApk(String apkPath) {
		ApkBean apk = null;
		try {
			apk = ApkFileManager.unzipApk(apkPath);
		} catch (Exception e) {
			e.printStackTrace();
			apk = null;
		}
		return apk;
	}

	public static boolean doApk(String apkPath) {
		if(!(new File(SDK_PATH)).isDirectory())
			return false;
		apkFolder.mkdir();
		successFolder.mkdir();
		failedFolder.mkdir();
		errorFolder.mkdir();
		ApkBean apk = parseApk(apkPath);
		if (apk == null)
			return false;
		// Install apk
		if (!installApk(apk))
			return false;
		// Run apk
		int runRet=runApk(apk);
		if (runRet<0)
			// Failed
			return false;
		else if(runRet==1)
			// Success
			screenshot(apk);
		else if(runRet==2)
			// Quit
			uninstallApk(apk);
		
		// Uninstall apk
		uninstallApk(apk);
		return true;
	}

	public static boolean installApk(final ApkBean apk) {
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

	public static int runApk(ApkBean apk) {
		try {
			System.out.println("TryingRun " + apk.packageName);
			Process process;
			process = Runtime.getRuntime().exec(
					ADB_SHELL + EXE_APK + apk.packageName + "/" + apk.mainName);

			StreamGobbler errorGobbler = new StreamGobbler(
					process.getErrorStream(), "ERROR");
			errorGobbler.start();
			StreamGobbler outGobbler = new StreamGobbler(
					process.getInputStream(), "STDOUT");
			outGobbler.start();

			System.out.println("Version code: " + apk.versionCode);
			System.out.println("Version name: " + apk.versionName);
			System.out.println("MainActivity " + apk.mainName);
			String cmd = null;
			process.waitFor();
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.println("Run?(Y/N/Q)");
				JOptionPane.showMessageDialog(null,
						"Opps, a bad apk...", "Error",
						JOptionPane.ERROR_MESSAGE);
				
				cmd = sc.nextLine();
				if (cmd.equalsIgnoreCase("y")) {
					System.out.println("Roger!");
					// Install and run successfully
					process.destroy();
					return 1;
//					screenshot(apk);
//					FileUtilsExt.movefile(apk.apkLocalPath, successFolder
//							+ "\\" + apk.marketBean.marketAppName + "\\"
//							+ apk.versionName + ".apk");
//					(new File(apk.apkLocalPath + ".lock")).delete();
//					break;
				} else if (cmd.equalsIgnoreCase("n")) {
					System.out.println("Bad boy, kick out!");
					// Install and run failed
					process.destroy();
					return -1;
//					FileUtilsExt.movefile(apk.apkLocalPath, failedFolder
//							+ "\\"
//							+ (new Date()).toString().replace(":", "-")
//									.replace(" ", "_") + "_" + apk.packageName
//							+ "." + apk.versionCode + ".apk");
//					(new File(apk.apkLocalPath + ".lock")).delete();
//					break;
				} else if (cmd.equalsIgnoreCase("q")) {
					System.out.println("Bye lalala!");
					// Install and run failed
					process.destroy();
					return 2;
//					uninstallApk(apk);
//					(new File(apk.apkLocalPath + ".lock")).delete();
//					System.exit(0);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	public static boolean uninstallApk(ApkBean apk) {
		try {
			System.out.println("Unistalling " + apk.apkLocalPath);
			Process process;
			process = Runtime.getRuntime()
					.exec(UNINSTALL_APK + apk.packageName);
			StreamGobbler errorGobbler = new StreamGobbler(
					process.getErrorStream(), "ERROR");
			errorGobbler.start();
			StreamGobbler outGobbler = new StreamGobbler(
					process.getInputStream(), "STDOUT");
			outGobbler.start();
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			System.out.println("Directory: " + apk.marketBean.marketAppName
					+ " created");
			return successFolder.getAbsoluteFile() + "\\"
					+ apk.marketBean.marketAppName + "\\";
		} else {
			System.out.println("Directory: " + apk.marketBean.marketAppName
					+ " existed or create failed");
			return successFolder.getAbsoluteFile() + "\\"
					+ apk.marketBean.marketAppName + "\\";
		}
	}

	public static String screenshot(ApkBean apk) {
		String newFolder = createNewApkDir(apk);

		String installApkCmd = SDK_PATH + "\\" + MONKEYRUNNER
				+ " " + workspaceFolder + screenshotScript + " " + "\""
				+ newFolder + "\"\\";

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

	@Override
	public void run() {
		if(apkPath!=null)
			doApk(apkPath);
	}
}
