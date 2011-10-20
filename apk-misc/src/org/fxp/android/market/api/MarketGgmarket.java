package org.fxp.android.market.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.fxp.android.apk.SigUtil;
import org.fxp.tools.EncodingToolkit;

public class MarketGgmarket {
	public static final int Connenction_cmnet = 0;
	public static final int Connenction_cmwap = 1;
	public static final int Connenction_wifi = 2;
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final int DEFAULT_TIMEOUT = 60000;
	public static final String HOST = "http://androidapp.3g.cn/ReciveDataBytes.aspx";
	public static final String REQUEST_SOFTLIST_FAILURE = "softlist failure";
	public static final String WAP_URL = "10.0.0.172";
	private static final Object sig_lock = new Object();
	protected int ResponseDesireHeaderCommandType;
	protected int ResponseHeader;
	protected ByteArrayOutputStream baopt;
	private HttpClient client;
	protected DataOutputStream daopt;
	protected HttpPost httpPost;
	protected Random random;
	protected int requestCommandType;
	protected BasicHttpEntity requestEntity;

	private static String strEncoded = "AAIxMAAAAD8AAAAIAANzZGsAA3NkawAPMDAwMDAwMDAwMDAwMDAwAAABQAAAAeAAAzk5OQAFMi4wLjEAKDc2MWUyMThmY2MwZTgyMzMxYzA1MmFmODE0MDJkNjVjMjEzMDA2MTcAAAEuQhmregAgYjdlYWNlOTRmZjk2ZDFlODFiOGQ3ZDIwYmQ0Njc2ODMAAAAA";

	private static BufferedReader nameReader;
	private static BufferedWriter log;
	private static String logName;
	private String searchKey;

	public static void readRequest() throws IOException {
		System.out.println("Decoded:"
				+ new String(Base64.decodeBase64(strEncoded.getBytes())));

		InputStream is = new ByteArrayInputStream(
				Base64.decodeBase64(strEncoded.getBytes()));
		BufferedInputStream bos = new BufferedInputStream(is);
		DataInputStream oin = new DataInputStream(bos);

		System.out.println("Header");
		System.out.println(oin.readUTF());
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());
		System.out.println(oin.readUTF());
		System.out.println(oin.readUTF());
		System.out.println(oin.readUTF());
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());
		System.out.println(oin.readUTF());
		System.out.println(oin.readUTF());
		System.out.println(oin.readUTF());
		System.out.println(oin.readLong());
		System.out.println(oin.readUTF());

		System.out.println();
		System.out.println("Body");
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());
		System.out.println(oin.readUTF());
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());
		System.out.println(oin.readInt());

		System.out.println();
		System.out.println("Remain");
		System.out.println(oin.available() + " bytes");
	}

	public static File searchKeywords(String keywordFile) throws Exception {
		// DataInputStream ins=new DataOutputStream();
		// readRequest();
		if(!(new File(keywordFile)).isFile())
			return null;
		nameReader = new BufferedReader(new FileReader(keywordFile));
		logName=keywordFile+".3g_response.log";
		log= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(logName)),"UTF8"));
		String appNameExt;
		
		Set<String> set = new HashSet<String>();
		while ((appNameExt = nameReader.readLine()) != null){
			String[] appNames=appNameExt.split(" ");
			for(String appName:appNames){
				if(appName.equals(""))
					continue;
				set.add(appName);
			}
		}
		String[] allNames = new String[set.size()];
		set.toArray(allNames);
		
		for(String appName:allNames) {
				String res=null;
				try{
					do{
						MarketGgmarket client = new MarketGgmarket(appName);
						log.write("<SEARCH,"+appName+">");
						log.newLine();
						log.write("<TIMEBEGIN,"+(new Date()).toString()+">");
						log.newLine();
						res=client.getRespInputStream(appName);
						log.write("<TIMEEND,"+(new Date()).toString()+">");
						log.newLine();
						
						if(res.equals("验证失败!")){
							System.out.println("Search "+appName+" verification failed");
						}
						log.write("<RESPONSE,"+res+">");
						log.write("<SEARCHEND>");
						log.newLine();
						log.flush();
						
						Thread.sleep((long) (new Random().nextFloat()*5000));
					}while(false);
//				}while(res.equals("验证失败!"));
					if(res.length()==36+appName.length())
						System.out.println("Search "+appName+" no result");
					else
						System.out.println("Search "+appName+" success");
				}catch(Exception e){
					System.out.println("Search timeout");
					Thread.sleep(5000);
				}
				Thread.sleep(5000);
			}
		return new File(logName);
	}

	public MarketGgmarket(String searchKey) throws IOException {
		Random localRandom = new Random();
		this.random = localRandom;
		BasicHttpEntity localBasicHttpEntity = new BasicHttpEntity();
		this.requestEntity = localBasicHttpEntity;
		this.httpPost = null;
		this.daopt = null;
		this.baopt = null;
		DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
		this.client = localDefaultHttpClient;
		this.baopt = new ByteArrayOutputStream();
		DataOutputStream localDataOutputStream = new DataOutputStream(
				this.baopt);
		this.daopt = localDataOutputStream;
		this.requestCommandType = 1;
		this.ResponseDesireHeaderCommandType = 2;
		this.searchKey=searchKey;
	}

	public static String getHost(String paramString) {
		int i = paramString.indexOf("/", 13);
		if (i > 13)
			;
		for (String str = paramString.substring(7, i);; str = paramString
				.substring(7)) {
			i = str.indexOf(':', 6);
			if (i > 0)
				str = str.substring(0, i);
			return str;
		}
	}

	public String getRespInputStream(String appName) throws Exception {
		String str = getHost("http://androidapp.3g.cn/ReciveDataBytes.aspx");
		HttpHost localHttpHost1 = new HttpHost(str);
		HttpPost localHttpPost1 = new HttpPost(
				"http://androidapp.3g.cn/ReciveDataBytes.aspx");
		this.httpPost = localHttpPost1;
		this.httpPost.addHeader("Content-Type", "application/octet-stream");
		this.httpPost.addHeader("User-Agent",
				"Apache-HttpClient/UNAVAILABLE (java 1.4)");
		setRequestHead();
		setRequestBody(appName);
		setContent();

		HttpParams localHttpParams1 = this.client.getParams();
		localHttpParams1.setParameter("http.socket.timeout",
				Integer.valueOf(10000));
		localHttpParams1.setParameter("http.connection.timeout",
				Integer.valueOf(10000));
		int j;
		String res=null;
		HttpClient localHttpClient = this.client;
		HttpPost localHttpPost2 = this.httpPost;
		HttpResponse localHttpResponse = localHttpClient.execute(
				localHttpHost1, localHttpPost2);
		j = localHttpResponse.getStatusLine().getStatusCode();
		if (j == 200) {
			HttpEntity localHttpEntity = localHttpResponse.getEntity();
			InputStream localInputStream = localHttpEntity.getContent();
			res = EncodingToolkit.convertStreamToString(localInputStream);
			// return localInputStream;
		}
		return res;
	}

	protected void setContent() {
		try {
			this.baopt.flush();
			this.daopt.close();
			this.baopt.close();
			byte[] arrayOfByte1 = Base64.encodeBase64(this.baopt.toByteArray());
			String str = new String(arrayOfByte1).replace("+", "-").replace(
					"/", "_");
			BasicHttpEntity localBasicHttpEntity1 = this.requestEntity;
			byte[] arrayOfByte2 = str.getBytes();
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
					arrayOfByte2);
			localBasicHttpEntity1.setContent(localByteArrayInputStream);
			this.requestEntity.setChunked(true);
			HttpPost localHttpPost = this.httpPost;
			localHttpPost.setEntity(this.requestEntity);
			this.daopt = null;
			this.baopt = null;
			return;
		} catch (IOException localIOException) {
			while (true)
				localIOException.printStackTrace();
		}
	}

	protected void setRequestBody(String appName) {
		try {
			DataOutputStream localDataOutputStream1 = this.daopt;
			int i = 1;
			localDataOutputStream1.writeInt(i);
			int j = 0;
			localDataOutputStream1.writeInt(j);
			String str = appName;
			localDataOutputStream1.writeUTF(str);
			int k = 1;
			localDataOutputStream1.writeInt(k);
			int m = 0;
			localDataOutputStream1.writeInt(m);
			int n = 1;
			localDataOutputStream1.writeInt(n);
			int i1 = 50;
			localDataOutputStream1.writeInt(i1);
			return;
		} catch (IOException localIOException) {
			while (true)
				localIOException.printStackTrace();
		}
	}

	protected void setRequestHead() {
		try {
			String str1 = "10";
			this.daopt.writeUTF(str1);
			int i = 1;
			this.daopt.writeInt(i);
			int j = 8;
			this.daopt.writeInt(j);
			String str2 = "sdk";
			this.daopt.writeUTF(str2);
			String str3 = "sdk";
			this.daopt.writeUTF(str3);
			String str4 = "000000000000000";
			this.daopt.writeUTF(str4);
			int k = 320;
			this.daopt.writeInt(k);
			int m = 480;
			this.daopt.writeInt(m);
			String str5 = "999";
			this.daopt.writeUTF(str5);
			String str6 = "2.0.1";
			this.daopt.writeUTF(str6);
			synchronized (sig_lock) {
				String str7 = SigUtil.genApiSig();
				String str8 = SigUtil.getRd();
				this.daopt.writeUTF(str8);
				long l = SigUtil.getTimeStamp();
				this.daopt.writeLong(l);
				this.daopt.writeUTF(str7);
				return;
			}
		} catch (IOException localIOException) {
			while (true)
				localIOException.printStackTrace();
		}
	}

	/*
	 * public Response toResponseData(InputStream paramInputStream) throws
	 * Exception { DataInputStream localDataInputStream = new
	 * DataInputStream(paramInputStream); readHead(localDataInputStream); return
	 * readBody(localDataInputStream); }
	 */
}

/*
 * Location:
 * F:\Reverse\Workspace\HiApk\ggmarket_02.00.05_build101119_SDK4.apk.dex2jar.jar
 * Qualified Name: com.jiubang.market.net.common.ApacheHttpNetClient JD-Core
 * Version: 0.6.0
 */