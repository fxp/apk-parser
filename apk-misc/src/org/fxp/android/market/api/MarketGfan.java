package org.fxp.android.market.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.worker.ApkLib;
import org.fxp.tools.EncodingToolkit;
import org.fxp.tools.ProxyProvider;

public class MarketGfan extends Market {
	protected static MarketGfan self = null;
	public int searchedNum = 0;
	public int downloadedNum = 0;
	public int DOWNLOAD_TIMEOUT = 1200;

	public static int APP_NAME_TYPE = 1;

	ApkLib apkLib = null;
	ProxyProvider proxys = null;

	public static Logger log = Logger.getLogger("gfan.downloader.log");

	public static synchronized MarketGfan getInstance(String marketName) {
		if (self == null) {
			self = new MarketGfan();
			self.init(marketName);
		}
		return self;
	}

	private boolean init(String marketName) {
		market_name = marketName;
		proxys = ProxyProvider.GetInstance();
//		apkLib = ApkLib.GetInstance();

		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(marketName);
			fileHandler.setLevel(Level.INFO);
			log.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}

		MarketGfan.self = this;
		return false;
	}

	/**
	 * @param keyword
	 * @return The absolute file path of the Apk searched with keyword and has
	 *         been downloaded.
	 */
	public ApkBean[] searchAndDownload(String keyword) {
		return doApkFetch(keyword);
	}

	protected ApkBean[] doApkFetch(String keyword) {
		ApkBean[] apks;
		try {
			// Search market with keyword
			apks = searchApk(keyword);
			// Get p_id from search response
			// Download each apk with p_id
			if (apks == null)
				return null;
			return apks = downloadApks(apks);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ApkBean[] searchApk(String keyword) throws ClientProtocolException,
			IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		String baseUrl = "http://apk.gfan.com/MobileAPI/Search.ashx";
		String xmlcontent = "<request version=\"1\"><size>10</size><start_position>0</start_position><platform>8</platform><screen_size>320#480</screen_size><orderby>2</orderby><keyword>"
				+ keyword + "</keyword></request>";

		StringEntity entity = new StringEntity(xmlcontent, "UTF-8");
		entity.setContentType("text/xml; charset=UTF-8");

		HttpPost httppost = new HttpPost(baseUrl);
		httppost.setEntity(entity);

		HttpResponse result = httpclient.execute(httppost);
		HttpEntity entityRet = result.getEntity();

		searchedNum++;

		if (entityRet != null) {
			InputStream instream = entityRet.getContent();
			String apkInfo = EncodingToolkit.convertStreamToString(instream);
			instream.close();
			return processApkInfo(keyword, apkInfo);
		}
		return null;
	}

	public ApkBean[] processApkInfo(String keyword, String apkInfo) {
		// Get how many product found
		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		String regex = "total_size=\"(\\d+?)\"";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(apkInfo);
		if (matcher.find()) {
			// Find Apk id
			int apkCount = Integer.valueOf(matcher.group(1));
			ApkBean apk = new ApkBean();
			apk.marketBean.marketName = this.market_name;
			apk.searchKeyword = keyword;
			apk.searchResult = apkInfo;
			apk.searchResultNum = apkCount;
			apkLib.putSearchHistory(apk);

			if (apkCount > 0) {

				regex = "<product\\s+" + "p_id=\"(\\d+?)\"\\s+"
						+ "product_type=\"\\S+\"\\s+" + "name=\"([^\"]+)\"\\s+"
						+ "price=\"(\\S+)\"\\s+"
						+ "pay_category=\"(\\S+)\"\\s+"
						+ "app_size=\"(\\d+)\"\\s+"
						+ "icon_url=\"([^\"]+?)\"\\s+"
						+ "icon_md5=\"([^\"]+?)\"\\s+"
						+ "short_description=\"([^\"]+?)\"\\s+"
						+ "rating=\"(\\d+)\"\\s+" + "/>";
				p = Pattern.compile(regex);
				matcher = p.matcher(apkInfo);
				int foundApkInfo = 0;
				while (matcher.find()) {
					apk = new ApkBean();
					apk.marketBean.marketName = this.market_name;
					apk.searchKeyword = keyword;
					apk.marketBean.marketPid = matcher.group(1);
					apk.marketBean.marketAppName = matcher.group(2);
					apk.marketBean.marketDescription = matcher.group(8);
					// Add more info to apk
					apks.add(apk);
					foundApkInfo++;
				}
				if (foundApkInfo != apkCount) {
					System.err.println("APKINFO: " + apkInfo);
					System.err.println("REGEX: " + regex);
				}
				// TODO insert search history into database
				return apks.toArray(new ApkBean[foundApkInfo]);
			}
		}
		return null;
	}

	/**
	 * Download all apks by p_id Unstable Incoming apk must be fill up with p_id
	 * 
	 * @return
	 */
	public ApkBean[] downloadApks(ApkBean[] apks) {
		ArrayList<ApkBean> downloadedApks = new ArrayList<ApkBean>();
		if (apks == null) {
			return null;
		}

		// Get download URL
		for (ApkBean apk : apks) {
			apk.marketBean.marketName = "GFan";
			apk.apkLocalPath = null;
			try {
				String downloadUrl = getDownloadUrl(apk.marketBean.marketPid);
				if (downloadUrl == "") {
					System.out.println("ID " + apk.marketBean.marketPid + " can not be found");
				} else {
					apk.marketBean.marketDownloadUrl = downloadUrl;
					// Get Apk
					apk.apkLocalPath = downloadAPK(apk, downloadUrl);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.err.println("Cannot save tmp file");
			} catch (Exception e) {
				if (e.getMessage().contains("HTTP response code: 403"))
					System.err.println("Forbidden response received!");
				else if (e.getMessage().contains("HTTP response code: 400"))
					System.err.println("Cannot connect to server");
				else if (e.getMessage().contains("socket closed"))
					System.err.println("Download time out");
				else
					e.printStackTrace();
				apk.apkLocalPath = null;
			} finally {
				if (apk.apkLocalPath != null) {
					log.log(Level.INFO, "Downloaded:" + apk.marketBean.marketPid);
					downloadedApks.add(apk);
				} else
					log.log(Level.WARNING, "Failed:" + apk.marketBean.marketPid);
			}
		}
		return downloadedApks.toArray(new ApkBean[downloadedApks.size()]);
	}

	private String downloadAPK(ApkBean apk, String downloadUrl)
			throws FileNotFoundException, UnsupportedEncodingException,
			MalformedURLException, FileNotFoundException, ConnectException,
			IOException {
		Proxy proxy = null;

		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = null;
			if (proxys == null) {
				conn = (HttpURLConnection) url.openConnection();
				System.out.print("Via no proxy\t");
			} else {
				proxy = proxys.getProxy();
				conn = (HttpURLConnection) url.openConnection(proxy);
				System.out.print("Via "
						+ proxy.address().toString().substring(1) + "\t");
			}

			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",
					"Dalvik/1.2.0 (Linux; U; Android 2.1; sdk Build/FRF91)");

			InputStream inputstream = conn.getInputStream();
			downloadedNum++;

			return saveApkFile(apk, inputstream);
		} catch (ConnectException e) {
			log.log(Level.WARNING, "ChangeProxy:"
					+ proxy.address().toString().substring(1));
			return downloadAPK(apk, downloadUrl);
		}
	}

	class DownloadTimeoutTask extends TimerTask {
		InputStream in = null;
		int seconds = 0;
		Timer timer = null;

		public DownloadTimeoutTask(InputStream in, int seconds, Timer timer) {
			super();
			this.in = in;
			this.seconds = seconds;
			this.timer = timer;
		}

		public void run() {
			try {
				System.out.println("Time's up!");
				in.close();
				// timer.cancel();
				this.cancel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getDownloadUrl(String p_id) throws ClientProtocolException,
			IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpHost proxy = null;
		if (proxys == null) {
			System.out.print("Via no proxy\t");
		} else {
			proxy = proxys.getHost();
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			System.out.print("Via " + proxy.toHostString() + "\t");
		}
		System.out.println("ID " + p_id + "\tGet download url");

		String baseUrl = "http://api.gfan.com/market/api/private/getDownloadUrl";
		String xmlcontent = "<request version=\"1\"><p_id>" + p_id
				+ "</p_id><uid>-1</uid></request>";

		StringEntity entity = new StringEntity(xmlcontent, "UTF-8");
		entity.setContentType("text/xml; charset=UTF-8");

		HttpPost httppost = new HttpPost(baseUrl);
		httppost.setHeader("User-Agent", "sdk/2.2/aMarket2.0/0.2.8");
		httppost.setEntity(entity);

		try {
			HttpResponse result = httpclient.execute(httppost);
			HttpEntity entityRet = result.getEntity();
			if (entityRet != null) {
				InputStream instream = entityRet.getContent();
				String apkInfo = EncodingToolkit
						.convertStreamToString(instream);
				instream.close();
				return processDownloadResponse(apkInfo);
			}
		} catch (HttpHostConnectException e) {
			log.log(Level.WARNING, "ChangeProxy:" + proxy.toHostString());
			return getDownloadUrl(p_id);
		}
		return null;
	}

	private String processDownloadResponse(String apkInfo) {
		String regex = "url=\"([^\"]+?)\" filemd5";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(apkInfo);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	public void initIdLib() {
		int MaxID = 70000;
		String[] ids = new String[MaxID];
		for (int i = 0; i < MaxID; i++)
			ids[i] = String.valueOf(i);
		apkLib.initIdLib("gfan", ids);
	}

	@Override
	public ApkBean downloadApk(ApkBean apk) {
		// TODO Auto-generated method stub
		return null;
	}

}
