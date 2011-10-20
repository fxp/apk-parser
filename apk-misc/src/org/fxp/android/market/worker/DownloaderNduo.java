package org.fxp.android.market.worker;

import java.util.ArrayList;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.api.MarketNduo;

public class DownloaderNduo {
/*
	public static void main(String[] args) {
		// Generate download url
		String baseUrl = "http://www.nduoa.com/web/default/apk/id/";
		int startNum = Integer.valueOf(args[0]);
		int endNum = Integer.valueOf(args[1]);
		int stepNum = Integer.valueOf(args[2]);

		MarketNduo marketApi = new MarketNduo("nduo");

		marketApi.db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
				marketApi.market_name + ".db");

		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();

		for (int i = 0; i < (endNum - startNum) / stepNum; i++) {
			for (int j = 0; j < stepNum; j++) {
				ApkBean apk = new ApkBean();
				apk.marketBean.marketDownloadUrl = baseUrl + (i * stepNum + startNum + j);
				apk.marketBean.marketPid = String.valueOf(i * stepNum + startNum + j);
				apk.marketBean.marketName = "Nduo";
				apks.add(apk);
				marketApi.db.store(apk);
			}
		}
		marketApi.db.commit();
		ApkBean[] apkDownloaded = marketApi.downloadApks(apks
				.toArray(new ApkBean[apks.size()]));
		System.out.println(apkDownloaded.length);
	}*/
}
