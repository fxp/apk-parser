package org.fxp.android.market.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;

public class MarketHiapk extends Market{
	public static BufferedWriter logWriter;
	public static ApkFileManager apkFilemanager;
	
	private  void logApk(String type, String info) {
		try {
			logWriter.write(type + "," + info);
			logWriter.newLine();
			logWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  void init() throws IOException {
		logWriter=new BufferedWriter(new FileWriter("hiapk.downloader.log"));
		apkFilemanager=ApkFileManager.GetInstance();
	}

	public  ApkBean[] downloadAll(ApkBean[] apks) {
		ArrayList<ApkBean> apksDownloaded=new ArrayList<ApkBean>();
		for (ApkBean apk : apks){
			try {
				ApkBean apkDownloaded=doGet(apk,
						"http://himarket.sj.91.com/iPhoneSoft/Download.aspx?f_id="
								+ apk.marketBean.marketPid + "&mt=4");
				if(apkDownloaded!=null)
					apksDownloaded.add(apkDownloaded);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return apksDownloaded.toArray(new ApkBean[apksDownloaded.size()]);
	}

	public ApkBean doGet(ApkBean apk, String getUrl) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getUrl);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			if(entity.getContentLength()<=18){
				logApk("NoResource",apk.marketBean.marketPid);
				return null;
			}
			saveApkFile(apk, instream);
			instream.close();
			ApkBean apkDownloaded=apkFilemanager.testAndAddApk(apk);
			if((new File(apkDownloaded.apkLocalPath)).isFile())
				return apkDownloaded;
			else
				return null;
		}
		return null;
	}

	public  void doPost() {

	}

	public  void doDownload(String downloadUrl) {

		try {
			URL url = new URL(downloadUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",
					"Dalvik/1.2.0 (Linux; U; Android 2.1; sdk Build/FRF91)");

			InputStream in = conn.getInputStream();

			int bufSize = 1024 * 64;
			byte byt[] = new byte[bufSize];

			int i;
			long l;
			for (l = 0L; (i = in.read(byt)) != -1; l += i) {
//				buffer.write(byt, 0, i);
				System.out.print("|");
			}
			System.out.println(" saved(" + (l / 1024) + "k)");
			in.close();
//			buffer.close();

		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public ApkBean[] searchAndDownload(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ApkBean[] downloadApks(ApkBean[] apks) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected ApkBean[] doApkFetch(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void initIdLib() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ApkBean downloadApk(ApkBean apk) {
		// TODO Auto-generated method stub
		return null;
	}
}
