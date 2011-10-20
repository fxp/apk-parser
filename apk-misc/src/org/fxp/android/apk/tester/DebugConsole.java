package org.fxp.android.apk.tester;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;

class DebugConsole extends JDialog {
	DebugConsole instance;
	public static Object[] possibilities = { "Software", "Game" };

	public static String ADB = "bin\\adb";

	public static String ADB_SHELL = "adb.exe shell ";
	public static String UNINSTALL_APK = "adb.exe uninstall ";
	public static String INSTALL_Apk_CMD = "adb.exe install ";
	public static String EXE_APK = "am start -n ";

	public static String workspaceFolder = "Z:\\apkWorkspace\\";
	public static String SDK_PATH = workspaceFolder + "android-sdk\\";
	public static File apkFolder = new File(workspaceFolder + "raw\\");
	public static File successFolder = new File(workspaceFolder + "finish\\");
	public static File failedFolder = new File(workspaceFolder + "failed\\");;
	public static File errorFolder = new File(workspaceFolder + "error\\");;
	public static List<ApkBean> apksToTest = new ArrayList<ApkBean>();

	static AndroidDebugBridge adb;

	Thread t;

	public static void setIn() {

	}

	/*
	 * public static void main(String[] args) { DebugConsole dc = new
	 * DebugConsole(); dc.setVisible(true);
	 * dc.setDefaultCloseOperation(EXIT_ON_CLOSE); File[] files = (new
	 * File("z:\\apkWorkspace\\raw")).listFiles(); for (File file : files) {
	 * doApk(file.getAbsolutePath()); return; } }
	 */
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

	public  boolean doApk(String apkPath) {
		if (!(new File(SDK_PATH)).isDirectory())
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
		int runRet = runApk(apk);
		if (runRet < 0) {
			// Failed
			FileUtilsExt.movefile(
					apk.getApkLocalPath(),
					failedFolder.getAbsolutePath() + "\\"
							+ apk.getApkFileChecksum());
			return false;
		} else if (runRet == 1) {
			// Success
			String newDir = screenshot(apk);
			FileUtilsExt.movefile(apk.getApkLocalPath(),
					newDir + apk.getVersionName() + ".apk");
		}else if (runRet == 2) {
			uninstallApk(apk);
			this.dispose();
			return false;
		}
		
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
			while (true) {

				int yn = JOptionPane.showConfirmDialog(null, "Run?(Y/N)", "",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (yn == JOptionPane.YES_OPTION) {
					System.out.println("Roger!");
					// Install and run successfully
					process.destroy();
					return 1;
				} else if (yn == JOptionPane.NO_OPTION) {
					System.out.println("Bad boy, kick out!");
					// Install and run failed
					process.destroy();
					return -1;
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

		String s = (String) JOptionPane.showInputDialog(null, null, "App Name",
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (!s.equals("")) {
			apk.marketBean.marketAppName = s;
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
		IDevice[] devices = adb.getDevices();

		int count = 1;
		while (devices.length != 1) {
			System.out.println("Device num:" + devices.length);
			System.out.println("Please check devices connected");
			devices = adb.getDevices();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (IDevice device : devices) {

			while (true) {
				int yn = JOptionPane.showConfirmDialog(null,
						"Take a shot?(Y/N)", "", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (yn == JOptionPane.YES_OPTION) {
					takeShot(device, newFolder, count);
				} else
					break;
				count++;
			}

		}
		return newFolder;
	}

	public static void takeShot(IDevice device, String dirPath, int count) {
		try {
			RawImage raw;
			raw = device.getScreenshot();
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

	public DebugConsole(List<ApkBean> apks) {
		instance = this;
		apksToTest = apks;
		setUndecorated(true);
		setTitle("Testing");
		setSize(496, 401);

		AndroidDebugBridge.init(false);
		adb = AndroidDebugBridge.createBridge();
		Container contentPane = getContentPane();

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.NORTH);
		contentPane.add(scrollPane);

		addWindowListener(new WindowCloser());

		outputArea = new JTextArea();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		out = new DebugOutputStream(outputArea);
		scrollPane.setViewportView(outputArea);

		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				instance.dispose();
			}
		});
		getContentPane().add(btnNewButton, BorderLayout.SOUTH);

		System.setOut(new PrintStream(out));
		ToCenter();
		// Create a new, second thread
		setVisible(true);
		// t = new Thread(this, "ApkTester Thread");
		// System.out.println("Child thread: " + t);

		doTest();
		// t.start(); // Start the thread
	}

	private void doTest() {
		for (ApkBean apk : this.apksToTest) {
			doApk(apk.getApkLocalPath());
		}
		this.dispose();
	}

	private JTextField inputField;
	private JTextArea outputArea;
	private static DebugOutputStream out;

	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}

	public void ToCenter() {
		// 设置窗口居中
		int windowWidth = this.getWidth();
		int windowHeight = this.getHeight();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		this.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2
				- windowHeight / 2);
	}
}

class DebugOutputStream extends OutputStream {
	public DebugOutputStream(JTextArea area) {
		textArea = area;
	}

	public void write(int b) {
		char c = (char) b;
		textArea.append("" + c);
	}

	private JTextArea textArea;
}
