package org.fxp.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.fxp.android.apk.ApkBean;

public class SiteLang {

	static Pattern emailPattern = Pattern
			.compile("(\\b[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,5})");
	static Pattern charsetPattern = Pattern.compile("charset=([^\\\"]+)");
	static Pattern contactPattern = Pattern
			.compile("<a\\shref=\"([^\\\"]+?)\">[^\\\"]+[Cc]ontact.+?>");
	static Pattern contactCnPattern = Pattern
			.compile("<a\\shref=\"([^\\\"]+?)\">联系.+?>");
	static Pattern aboutCnPattern = Pattern
			.compile("<a\\shref=\"([^\\\"]+?)\">关于.+?>");
	// <a href="http://home.baidu.com">关于百度</a>
	static Pattern aboutEnPattern = Pattern
			.compile("<a\\shref=\"([^\\\"]+?)\">[Aa]bout.+?>");
	static Pattern langPattern = Pattern.compile("lang=([^\\\"])");

	static BufferedWriter log;

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"officialSiteWithApk.txt")));
		log = new BufferedWriter(new FileWriter("DevContact.csv"));
		String line;

		int count=0;
		while ((line = reader.readLine()) != null) {
			count++;
			if(count<3643){
				continue;
			}
			System.out.println(line);
			try {
				FetchSite(line, 3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getPattern(Pattern pattern, String url, String type,
			String content) {
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			System.out.println(type + "," + url + "," + matcher.group(1));
			try {
				log.write(type + "," + url + "," + matcher.group(1));
				log.write("\r\n");
				return matcher.group(1);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static void doAllMatch(String url, String content, int deep)
			throws ClientProtocolException, IOException {
		getPattern(charsetPattern, url, "charset", content);
		getPattern(langPattern, url, "Lang", content);

		String ret = null;
		getPattern(emailPattern, url, "email", content);
		if ((ret = getPattern(contactPattern, url, "contact", content)) != null) {

			if (url.endsWith("/"))
				FetchSite(url + ret, deep - 1);
			else
				FetchSite(url + "/" + ret, deep - 1);
		}
		if ((ret = getPattern(contactCnPattern, url, "联系", content)) != null) {
			FetchSite(ret, deep - 1);
		}
		if ((ret = getPattern(aboutEnPattern, url, "about", content)) != null) {

			if (url.endsWith("/"))
				FetchSite(url + ret, deep - 1);
			else
				FetchSite(url + "/" + ret, deep - 1);
		}
		if ((ret = getPattern(aboutCnPattern, url, "关于", content)) != null) {
			FetchSite(ret, deep - 1);
		}
	}

	public static String FetchSite(String url, int deep)
			throws ClientProtocolException, IOException {
		if (deep == 0)
			return null;
		try {
			url = url.replace("&amp;", "&").replace("&lt;", "<")
					.replace("&gt;", ">").replace("&quot;", "\"")
					.replace("&apos;", "'");
			new URL(url);
		} catch (Exception e) {
			return null;
		}
		System.out.println(url);
		HttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		if (entity != null) {

			String strSite = EntityUtils.toString(entity);
			doAllMatch(url, strSite, deep);
		}
		return null;
	}
}
