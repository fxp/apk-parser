package org.fxp.android.market.api;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxp.android.apk.ApkBean;
import org.fxp.crawler.bean.CertBean;
import org.fxp.mode.SingletonException;
import org.fxp.tools.ChineseConvert;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ApkDAO {
	private static ApkDAO self = null;
	private static boolean instance_flag = false;

	// For database
	private String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	// private String DB_URL = "jdbc:mysql://10.18.135.200:3306/fengxiaoping";
	//private String DB_URL = "jdbc:mysql://192.168.22.12:3306/apks";
	private String DB_URL = "jdbc:mysql://10.18.134.66:3306/apk";
	private String DB_USER = "root";
	private String DB_PASSWORD = "miaozhijian";
	private Connection conn = null;

	// TODO change it to database view and add more dynamic to
	// DISTINCT_PACKAGE_NAME_QUERY
	private String DISTINCT_APP_NAME_QUERY = "Select distinct app_name,check_times from (select app_name,check_times from market_gfan union select app_name,check_times from market_yingyonghui) alias order by check_times asc";
	private String DIFF_APK_QUERY = "SELECT DISTINCT `market_yingyonghui`.`package_name` FROM `market_gfan`,`market_yingyonghui` WHERE `market_%s`.`package_name`=`market_yingyonghui`.`package_name` and `market_%s`.`version_code`>`market_yingyonghui`.`version_code`;";
	private String DISTINCT_PACKAGE_NAME_QUERY = "SELECT DISTINCT package_name FROM (SELECT package_name from `apk_info` union SELECT package_name from  `market_gfan` union SELECT package_name FROM `market_yingyonghui`) alias";
	private String DISTINCT_MARKET_NAME_QUERY = "SELECT market_name from market`";

	private String INIT_ID_LIB_QUERY = "INSERT INTO id_walker(market_name,p_id) VALUES(?,?)";
	private String EMPTY_ID_LIB_QUERY = "DELETE FROM id_walker WHERE market_name=?";
	private String GET_IDS_QUERY = "SELECT p_id FROM id_walker WHERE market_name=? and check_time=(SELECT min(check_time) FROM id_walker) and `lock`=false limit ?;";
	private String LOCK_IDS_QUERY = "UPDATE id_walker SET `lock`=? WHERE market_name=? and p_id=?;";
	private String RESET_ID_LOCK_QUERY = "UPDATE id_walker SET `lock`=false WHERE market_name=?;";

	// For regular expressions
	private String REGEX_SPLIT = "[-\\.�?]";
	private String REGEX_VERIFY_PACKAGE_NAME = "^[a-zA-Z_][^\\.]*(\\.[a-zA-Z_].*)*";

	private ApkDAO() {
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

	public static ApkDAO GetInstance() {
		if (self == null) {
			self = new ApkDAO();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public void reconnectDb() {
		instance_flag = false;
		init();
	}

	public int insertSearchHistory(ApkBean apk) {
		int ret = -1;
		if (apk.marketBean.marketName == null || apk.searchKeyword == null
				|| apk.searchResult == null || apk.searchResultNum < 0)
			return ret;

		String insertQuery = "INSERT INTO market_search_history("
				+ "market_name," + "search_keyword," + "update_time,"
				+ "result," + "result_num) " + "VALUES(?,?,NOW(),?,?); ";
		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(insertQuery);
			pstmt.setString(1, apk.marketBean.marketName);
			pstmt.setString(2, apk.searchKeyword);
			pstmt.setString(3, apk.searchResult);
			pstmt.setInt(4, apk.searchResultNum);
			pstmt.execute();
			ret = 0;
		} catch (SQLException e) {
			System.out
					.println("You may have inserted duplicated search history in a short time. Please be patient.");
			e.printStackTrace();
			ret = -2;
		}
		return ret;
	}

	// TODO
	public int insertApk(ApkBean apk) {
		int ret = -1;
		if (apk.marketBean.marketName == null || apk.packageName == null
				|| apk.packageName == null || apk.marketBean.marketAppName == null
				|| apk.versionCode == 0 || apk.versionName == null)
			return ret;
		// Update market database
		try {
			PreparedStatement pstmt = null;
			String marketUpdateQuery = "INSERT INTO market_"
					+ apk.marketBean.marketName.trim().toLowerCase()
					+ "(package_name,"
					+ "version_code,"
					+ "update_time,"
					+ "app_name,"
					+ "download_url,"
					+ "p_id)"
					+ " VALUES( ?, ?, CURDATE(), ?, ?, ?) ON DUPLICATE KEY UPDATE check_times= check_times+1";
			pstmt = (PreparedStatement) conn
					.prepareStatement(marketUpdateQuery);
			pstmt.setString(1, apk.packageName.trim());
			pstmt.setInt(2, apk.versionCode);
			pstmt.setString(3, apk.marketBean.marketAppName.trim());
			pstmt.setString(4, apk.marketBean.marketDownloadUrl.trim());
			pstmt.setString(5, apk.marketBean.marketPid.trim());

			pstmt.executeUpdate();
			ret = 0;
		} catch (SQLException e) {
			e.printStackTrace();
			ret = -2;
		}
		return ret;
	}
	

	// TODO
	public int insertCert(ApkBean apk) {
		int ret = -1;
		for(CertBean cert:apk.certs){
			try {
				PreparedStatement pstmt = null;
				String marketUpdateQuery = "INSERT INTO cert "
						+ "(issuer,certhash,certBrief)"
						+ " VALUES(?, ?,?) ";
				pstmt = (PreparedStatement) conn
						.prepareStatement(marketUpdateQuery);
				pstmt.setString(1, ((X509Certificate) cert.certificate).getIssuerX500Principal().toString());
				pstmt.setString(2, cert.certificateHash);
				pstmt.setString(3, apk.certInfo());

				pstmt.executeUpdate();
				ret = 0;
			} catch (SQLException e) {
				if(e.getErrorCode()==1062){
					return 0;
				}
				e.printStackTrace();
				ret = -2;
			}
		}

		return ret;
	}

	/**
	 * Get all search key word and process them into more reasonable words
	 * 
	 * @return
	 */
	public String[] getSearchKeywords() {
		PreparedStatement pstmt;
		ResultSet rs;
		ArrayList<String> appNames = new ArrayList<String>();

		try {
			pstmt = (PreparedStatement) conn
					.prepareStatement(DISTINCT_APP_NAME_QUERY);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1) != null)
					appNames.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return processKeywords(appNames.toArray(new String[appNames.size()]));
	}

	private String[] processKeywords(String[] keywords) {
		ArrayList<String> appNames = new ArrayList<String>();

		ChineseConvert convertor = null;
		try {
			convertor = new ChineseConvert();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (String name : keywords) {
			if (name.equals(""))
				continue;
			// For name like "App1ã€� App2ã€� App3"
			String[] splitedNames = name.split(REGEX_SPLIT);
			if (splitedNames.length > 1) {
				for (String splitedName : splitedNames)
					if (!splitedName.equals("") && splitedName.length() > 1)
						appNames.add(splitedName.trim());
			} else if (name.length() > 1)
				appNames.add(name);

			if (convertor != null) {
				String tmp = name;
				StringBuffer from = new StringBuffer();
				from.append(name);
				convertor.translate(from, "fan2Jian");
				if (!tmp.equals(from.toString()))
					appNames.add(from.toString());
			}
		}
		return appNames.toArray(new String[appNames.size()]);
	}

	public String[] getPackageNames() {
		PreparedStatement pstmt;
		ResultSet rs;
		ArrayList<String> allPackageNames = new ArrayList<String>();
		String query = DISTINCT_PACKAGE_NAME_QUERY;
		Pattern p = Pattern.compile(REGEX_VERIFY_PACKAGE_NAME);
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Matcher m = p.matcher(rs.getString(1));
				if (m.find())
					allPackageNames.add(rs.getString(1));
				else
					System.out.println("PackageName verification err: "
							+ rs.getString(1));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allPackageNames.toArray(new String[allPackageNames.size()]);
	}

	public ApkBean[] getDiffApks() {
		ArrayList<ApkBean> diffApk = new ArrayList<ApkBean>();
		String[] marketNames = getMarketNames();

		for (String marketName : marketNames) {
			String query = String
					.format(DIFF_APK_QUERY, marketName, marketName);
			PreparedStatement pstmt;
			try {
				pstmt = (PreparedStatement) conn.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					ApkBean apk = new ApkBean();
					apk.packageName = String.valueOf(rs
							.getString("package_name"));
					apk.marketBean.marketName = "Gfan";
					diffApk.add(apk);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return diffApk.toArray(new ApkBean[diffApk.size()]);
	}

	private String[] getMarketNames() {
		ArrayList<String> diffMarket = new ArrayList<String>();
		String query = DISTINCT_MARKET_NAME_QUERY;
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				diffMarket.add(rs.getString("market_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return diffMarket.toArray(new String[diffMarket.size()]);
	}

	public static void main(String[] argv) throws InterruptedException {
		// String[] rt = ApkDAO.GetInstance().getPackageNames();
		// System.out.println(rt.length);

		/*
		 * for (int i = 0; i < 10; i++) { ApkBean apk = new ApkBean();
		 * apk.marketBean.marketName = "TEST"; apk.searchKeyword = "TEST";
		 * apk.searchResultNum = i; apk.searchResult = "TEST";
		 * ApkDAO.GetInstance().insertSearchHistory(apk); Thread.sleep(1000); }
		 */
		/*
		 * ApkBean apk=new ApkBean(); apk.packageName="com.TEST.TEST";
		 * apk.marketBean.marketName="Gfan"; apk.versionCode="-1";
		 * 
		 * ApkDAO.GetInstance().insertApk(apk);
		 */
		ApkBean[] apks = ApkDAO.GetInstance().getDiffApks();
		System.out.println(apks.length);
	}

	public void initIdLib(String marketName, String[] ids) {
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn
					.prepareStatement(EMPTY_ID_LIB_QUERY);
			pstmt.setString(1, marketName);
			pstmt.execute();

			pstmt = (PreparedStatement) conn
					.prepareStatement(INIT_ID_LIB_QUERY);
			for (String id : ids) {
				pstmt.setString(1, marketName);
				pstmt.setString(2, id);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void resetLocks(String marketName) {
		try {
			PreparedStatement pstmt;
			pstmt = (PreparedStatement) conn
					.prepareStatement(RESET_ID_LOCK_QUERY);
			pstmt.setString(1, marketName);
			pstmt.execute();

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String[] getIds(String marketName, int limitNum) {
		ArrayList<String> ids=new ArrayList<String>();
		try {
			PreparedStatement pstmt;
			pstmt = (PreparedStatement) conn.prepareStatement(GET_IDS_QUERY);
			pstmt.setString(1, marketName);
			pstmt.setInt(2, limitNum);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ids.add(String.valueOf(rs.getInt("p_id")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ids.toArray(new String[ids.size()]);
	}
	
	public void lockIds(String marketName,String[] ids,boolean isLocked){
		try {
			PreparedStatement pstmt;
			pstmt = (PreparedStatement) conn
					.prepareStatement(LOCK_IDS_QUERY);
			for(String id:ids){
				pstmt.setBoolean(1, isLocked);
				pstmt.setString(2, marketName);
				pstmt.setString(3, id);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
