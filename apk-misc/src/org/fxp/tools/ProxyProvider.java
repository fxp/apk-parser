package org.fxp.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.fxp.mode.SingletonException;

public class ProxyProvider {
	private static ProxyProvider self = null;
	private static boolean instance_flag = false;

	private static String PROXY_FILE_NAME = "ProxyList";
	private ArrayList<Proxy> proxys = new ArrayList<Proxy>();
	private ArrayList<HttpHost> hosts = new ArrayList<HttpHost>();
	private int proxyCursor = 0;

	private ProxyProvider() {
		if (instance_flag)
			throw new SingletonException("Only one instance allowed");
		else
			instance_flag = true;
	};

	public static ProxyProvider GetInstance() {
		if (self == null) {
			self = new ProxyProvider();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public Proxy getProxy() {
		if (self == null)
			return null;
		return proxys.get((proxyCursor++) % proxys.size());
	}

	public HttpHost getHost() {
		if (self == null)
			return null;
		return hosts.get((proxyCursor++) % hosts.size());
	}

	private void init() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					PROXY_FILE_NAME));
			String proxyInfo = null;
			while ((proxyInfo = in.readLine()) != null) {
				String[] proxyInfoParts = proxyInfo.split(":");
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxyInfoParts[0], Integer.valueOf(proxyInfoParts[1])));
				HttpHost host = new HttpHost(proxyInfoParts[0],
						Integer.valueOf(proxyInfoParts[1]), "http");

				hosts.add(host);
				proxys.add(proxy);
			}
		} catch (FileNotFoundException e) {
			self = null;
		} catch (IOException e) {
			e.printStackTrace();
			self = null;
		}
	}
}
