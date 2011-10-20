package org.fxp.android.apk.tester;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

public class AutoScreenshot extends JDialog  {
	AdbClient adbClient;
	List<ApkBean> apksToTest = new ArrayList<ApkBean>();
	public void setAdbClient(AdbClient adbClient) {
		this.adbClient = adbClient;
	}

	public void setApksToTest(List<ApkBean> apksToTest) {
		this.apksToTest = apksToTest;
	}

	public int askForRun() {
		return 0;
	}

	public void close() {
		if (adbClient != null)
			adbClient.close();
		dispose();
	}
	
	public void run() {
		for (ApkBean apk : apksToTest) {
			try {
				System.out.println( "Testing " + apk.getApkLocalPath());
				adbClient.doScreenshot(apk);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		close();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		try {
			List<ApkBean> apks = ApkFileManager.getAllApk("C:\\apkworkspace\\raw");
			AutoScreenshot dialog = new AutoScreenshot();
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAdbClient(new AdbClient(null, new FileManager(
					"C:\\apkworkspace")));
			dialog.setApksToTest(apks);

			dialog.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
