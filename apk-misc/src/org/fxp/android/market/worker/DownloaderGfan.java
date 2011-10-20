package org.fxp.android.market.worker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.api.MarketGfan;
import org.fxp.android.market.worker.frame.master.ApkDownloadService;

public class DownloaderGfan {

	private static ApkLib apkLib = null;
	private static MarketGfan gfan = null;
	private static String marketName = "gfan";
	/**
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws SecurityException,
			IOException, NotBoundException, ConfigurationException {
		
		//downloadClient(args);
		downloadAll(args);
	}

	public static void downloadClient(String[] args)
			throws MalformedURLException, RemoteException, NotBoundException {
		apkLib = ApkLib.GetInstance();
		gfan = MarketGfan.getInstance(marketName);
		ApkDownloadService client = (ApkDownloadService) Naming
				.lookup("rmi://localhost:2221/ApkDownloadService");
		ApkBean[] apks = null;
		int transfered = 0;
		if (args[1] == "init")
			client.resetAllLock(marketName);
		while ((apks = client.getNextId(marketName)) != null) {
			String[] ids = new String[apks.length];
			for (int i = 0; i < apks.length; i++)
				ids[i] = apks[i].marketBean.marketPid;

			client.setId(marketName, ids, true);
			for (ApkBean apk : apks) {
				ApkBean[] apkTmp = new ApkBean[1];
				apkTmp[0] = apk;
				ApkBean[] apkDownloaded = gfan.downloadApks(apkTmp);
				try {
					if (apkDownloaded.length > 0
							&& apkLib.putApk(apkDownloaded[0]) == 0)
						transfered++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			client.setId(marketName, ids, false);

			System.out.println("Success executed = " + transfered);
		}
	}

	public static void searchAndDownload(String keyword) {
		int transfered = 0;
		ApkBean[] apkDownloaded = gfan.searchAndDownload(keyword);
		for (ApkBean apk : apkDownloaded) {
			try {
				if (apkLib.putApk(apk) == 0)
					transfered++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out
				.println("Keyword / Downloaded / Success executed = \""
						+ keyword + "\" / " + apkDownloaded.length + " / "
						+ transfered);
	}

	public static void downloadAll(String[] args) {

//		apkLib = ApkLib.GetInstance();
//		gfan = MarketGfan.getInstance("gfan");

		if (args.length < 3)
			return;
		int apkStart = Integer.valueOf(args[0]);
		int apkEnd = Integer.valueOf(args[1]);
		int apkNumStep = Integer.valueOf(args[2]);
		int transfered = 0;

		int j = 0;
		for (int i = apkStart; i < apkEnd; i += apkNumStep) {
			ApkBean[] apks = new ApkBean[apkNumStep];
			for (j = 0; j < apkNumStep; j++) {
				apks[j] = new ApkBean();
				apks[j].marketBean.marketPid = String.valueOf(i + j);
			}

			MarketGfan gfan = MarketGfan.getInstance("gfan");
			ApkBean[] apkDownloaded = gfan.downloadApks(apks);
			for (ApkBean apk : apkDownloaded) {
				try {
					if (apkLib.putApk(apk) == 0)
						transfered++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Downloaded / Searched / Success executed = "
					+ apkDownloaded.length + " / " + apkNumStep + " / "
					+ transfered);
		}
	}
}
