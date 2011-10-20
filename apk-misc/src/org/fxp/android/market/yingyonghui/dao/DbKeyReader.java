package org.fxp.android.market.yingyonghui.dao;

import java.sql.DriverManager;

import org.fxp.android.market.api.ApkDAO;
import org.fxp.mode.SingletonException;

import com.mysql.jdbc.Connection;

public class DbKeyReader {
	private static DbKeyReader self = null;
	private static boolean instance_flag = false;

	// For database
	private String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	// private String DB_URL = "jdbc:mysql://10.18.135.200:3306/fengxiaoping";
	private String DB_URL = "jdbc:mysql://192.168.22.12:3306/android_market";
	private String DB_USER = "apkcrawler";
	private String DB_PASSWORD = "11q22w33e";
	private Connection conn = null;

	private DbKeyReader() {
		if (instance_flag)
			throw new SingletonException("Only one instance allowed");
		else
			instance_flag = true;
	};

	private void init() {
		try {
			Class.forName(DB_CLASS_NAME);
			String url = DB_URL;
			conn = (Connection) DriverManager.getConnection(url, DB_USER,
					DB_PASSWORD);
			instance_flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			instance_flag = false;
		}
	}

	public static DbKeyReader GetInstance() {
		if (self == null) {
			self = new DbKeyReader();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public static void main(String[] args) {
		
	}

}
