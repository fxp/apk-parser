package org.fxp.android.apk.tester;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import com.android.ddmlib.testrunner.TestIdentifier;

public class AdbClient {
	public static int RETRY_INTERVAL = 2000;

	static boolean adbInit = false;
	String serialNumber;
	FileManager fileManager;
	AndroidDebugBridge adb;
	IDevice controlDevice;
	private InstrumentationResultParser mParser;

	public List<ApkBean> apksToTest = new ArrayList<ApkBean>();

	public void retryConnectDevice() throws InterruptedException {
		boolean isDeviceFound = false;
		do {
			adb = AndroidDebugBridge.createBridge("bin\\adb.exe", false);
			IDevice[] devices = adb.getDevices();
			for (IDevice device : devices) {
				// TODO
				// System.out.println("Device SN " + device.getSerialNumber());
				// if (!device.getSerialNumber().equals(serialNumber))
				// continue;
				isDeviceFound = true;
				controlDevice = device;
				break;
			}
			System.out.println("Retry connecting adb server");
			Thread.sleep(RETRY_INTERVAL);
		} while (isDeviceFound == false);
	}

	public AdbClient(String serialNumber, FileManager fileManager)
			throws InterruptedException {
		this.serialNumber = serialNumber;
		this.fileManager = fileManager;
		if (adbInit == false) {
			AndroidDebugBridge.init(false);
			adbInit = true;
		}
		retryConnectDevice();
	}

	public boolean installApk(ApkBean apk) throws InstallException {
		controlDevice.installPackage(apk.getApkLocalPath(), true);
		return false;
	}

	private void runApk(ApkBean apk) {
		String runCaseCommandStr = String.format("am start -W -n %s/%s",
				apk.getPackageName(), apk.getMainName());
		CollectingTestRunListener listener = new CollectingTestRunListener();

		mParser = new InstrumentationResultParser(runCaseCommandStr, listener);
		try {
			controlDevice.executeShellCommand(runCaseCommandStr, mParser);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShellCommandUnresponsiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private void uninstallApk(ApkBean apk) throws InstallException {
		controlDevice.uninstallPackage(apk.getPackageName());
		return;
	}

	public void close() {
		System.out.println("Teminating");
		AndroidDebugBridge.disconnectBridge();
		// AndroidDebugBridge.terminate();
		adb = null;
		controlDevice = null;
		fileManager = null;
		serialNumber = null;
		apksToTest = null;
	}

	public void doScreenshot(ApkBean apk) throws InterruptedException {
		try {
			installApk(apk);
			runApk(apk);
			String newDir=fileManager.createNewDir(apk, apk.getPackageName());
			takeShot(newDir, 1);
			fileManager.putToFinish(apk, apk.getPackageName() + File.separator
					+ apk.getVersionName());

			uninstallApk(apk);
		} catch (InstallException e) {
			e.printStackTrace();
		}

	}

	public void doTest(ApkBean apk, ApkTestDiag diag)
			throws InterruptedException {
		try {
			installApk(apk);
			runApk(apk);
			if (diag != null && diag.askForRun() != 0) {
				fileManager.putToFailed(apk, apk.getApkFileChecksum());
			} else {
				screenshot(apk);
				fileManager.putToFinish(apk,
						apk.getMarketBean().getMarketAppName() + File.separator
								+ apk.getVersionName());
			}
			uninstallApk(apk);
		} catch (InstallException e) {
			e.printStackTrace();
			// retryConnectDevice();
		}
	}

	public void doTest(List<ApkBean> apks, ApkTestDiag diag)
			throws InterruptedException {
		for (ApkBean apk : apks) {
			doTest(apk, diag);
		}
	}

	public static void TEST() throws InterruptedException {
		AdbClient adbClient = new AdbClient("HT05EPL07220", new FileManager(
				"C:\\apkworkspace"));
		List<ApkBean> apks = ApkFileManager.getAllApk("C:\\apkworkspace");
		adbClient.doTest(apks, null);
		adbClient.close();
		return;
	}

	public String screenshot(ApkBean apk) {
		String appName = (String) JOptionPane.showInputDialog(null,
				"App name(default " + apk.packageName + ")?(ENTER/[Name])",
				"App Name", JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (appName.equals(""))
			appName = apk.getPackageName();

		String newFolder = fileManager.createNewDir(apk, appName);

		int count = 1;
		while (true) {
			int yn = JOptionPane
					.showConfirmDialog(null, "Take a shot?(Y/N)", "",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);

			if (yn == JOptionPane.YES_OPTION) {
				takeShot(newFolder, count);
			} else
				break;
			count++;
		}

		return newFolder;
	}

	public void takeShot(String dirPath, int count) {
		try {
			RawImage raw;
			raw = controlDevice.getScreenshot();
			PaletteData palette = new PaletteData(raw.getRedMask(),
					raw.getGreenMask(), raw.getBlueMask());
			ImageData image = new ImageData(raw.width, raw.height, raw.bpp,
					palette, 1, raw.data);

			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image };
			loader.save(dirPath + "s" + count + ".png", SWT.IMAGE_PNG);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// For collecting results from running device tests
	private static class CollectingTestRunListener implements ITestRunListener {

		@Override
		public void testEnded(TestIdentifier arg0, Map<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testFailed(TestFailure arg0, TestIdentifier arg1,
				String arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testRunEnded(long arg0, Map<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testRunFailed(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testRunStarted(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testRunStopped(long arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void testStarted(TestIdentifier arg0) {
			// TODO Auto-generated method stub

		}

	}
}
