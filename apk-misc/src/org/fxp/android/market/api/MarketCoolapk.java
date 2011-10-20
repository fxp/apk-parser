package org.fxp.android.market.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class MarketCoolapk {
	// The configuration items
	private static String userName = "fxp007";
	private static String password = "1q2w3e4r";
	private static String redirectURL = "http://www.coolapk.com/download/apk/3717/com.almalence.hdr_plus/";

	// Don't change the following URL
	private static String renRenLoginURL = "http://www.coolapk.com/do.php?ac=login";

	// The HttpClient is used in one session
	private HttpResponse response;
	private DefaultHttpClient httpclient = new DefaultHttpClient();

	private boolean login() {
		HttpPost httpost = new HttpPost(renRenLoginURL);
		// All the parameters post to the web site
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("formhash", "c289d9c6"));
		// nvps.add(new BasicNameValuePair("formName", ""));
		// nvps.add(new BasicNameValuePair("method", ""));
		nvps.add(new BasicNameValuePair("submit", "登录"));
		nvps.add(new BasicNameValuePair("login", userName));
		nvps.add(new BasicNameValuePair("pwd", password));
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = httpclient.execute(httpost);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			httpost.abort();
		}
		return true;
	}

	private String getRedirectLocation() {
		Header locationHeader = response.getFirstHeader("Location");
		if (locationHeader == null) {
			return null;
		}
		return locationHeader.getValue();
	}

	private String getText(String redirectLocation) {
		HttpGet httpget = new HttpGet(redirectLocation);
		// Create a response handler
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = "";
		try {
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody = null;
		} finally {
			httpget.abort();
			httpclient.getConnectionManager().shutdown();
		}
		return responseBody;
	}

	public void printText() {
		if (login()) {
			String redirectLocation = getRedirectLocation();
			System.out.println(getText(redirectURL));
		}
	}

	public static void main(String[] args) {
		MarketCoolapk market = new MarketCoolapk();
		market.printText();
	}
}
