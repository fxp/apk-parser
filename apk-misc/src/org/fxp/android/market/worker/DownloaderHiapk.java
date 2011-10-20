package org.fxp.android.market.worker;

import java.io.IOException;
import java.util.ArrayList;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.api.MarketHiapk;

public class DownloaderHiapk {

	/**
	 * @param args
	 * @throws IOException
	 */
	
	private static String USAGE_STATEMENT="Usage: hiapk_downloader startNum endNum";
	public static void main(String[] args) throws IOException {
		if(args.length!=2){
			System.out.println(USAGE_STATEMENT);
			return ;
		}
		int startNum;
		int endNum;
		try{
			// 4000000
			startNum=Integer.valueOf(args[0]);
			endNum=Integer.valueOf(args[1]);	
		}catch(Exception e){
			System.out.println(USAGE_STATEMENT);
			return ;
		}
		MarketHiapk marketApi=new MarketHiapk();
		marketApi.init();

		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		for (int id = startNum; id < endNum; id++) {
			ApkBean apk = new ApkBean();
			apk.marketBean.marketPid = String.valueOf(id);
			apk.marketBean.marketName = "Hiapk";
			apks.add(apk);
		}

		ApkBean[] apksDownloaded=marketApi.downloadAll(apks.toArray(new ApkBean[apks.size()]));
		System.out.println("Downloaded apk "+apksDownloaded.length);
	}
}
