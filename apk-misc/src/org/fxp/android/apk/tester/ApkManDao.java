package org.fxp.android.apk.tester;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.android.market.yingyonghui.dao.DbKeyReader;
import org.fxp.crawler.bean.CertBean;
import org.fxp.crawler.bean.MarketBean;
import org.fxp.mode.SingletonException;
import org.fxp.tools.FileUtilsExt;
import org.fxp.tools.OnlyExt;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ApkManDao {
	private static ApkManDao self = null;
	private static boolean instance_flag = false;

	// For database
	private String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	private String DB_URL = "jdbc:mysql://10.18.136.66:3306/apk";
	private String DB_USER = "root";
	private String DB_PASSWORD = "miaozhijian";
	private Connection conn = null;

	private String UPDATE_CERT_CMD = "UPDATE `apk`.`cert_yingyonghui` set site=?,note=? where certhash=?";
	private String GET_APP_INFO_CMD = "SELECT name, description, batchdevname from `apk`.`apk_yingyonghui` where packagename=?";
	private String GET_CERT_INFO_CMD = "SELECT issuer, site, note from `apk`.`cert` where  certhash=?";

	private String INSERT_APK_CMD = "INSERT IGNORE INTO `apk`.`apk_yingyonghui`(application_id,packagename) VALUES(?,?)";
	private String GET_NEXT_ID_CMD = "SELECT application_id FROM `apk`.`apk_yingyonghui`";

	private String INSERT_CERT_CMD = "INSERT INTO `apk`.`cert_yingyonghui`(certhash,iss,certbrief) VALUES (?,?,?) ON DUPLICATE KEY UPDATE site=?,note=?";

	private String UPDATE_PACKAGE_NAME_CMD = "UPDATE `apk`.`apk_yingyonghui` set packagename=? where application_id=?";

	private ApkManDao() {
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

	public static ApkManDao GetInstance() {
		if (self == null) {
			self = new ApkManDao();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	// Put a apk into database
	// If exist, insert it into apk table and cert table
	// If not, return generated id of new apk
	public ApkBean fillApk(ApkBean apk) {
		// If the package name of apk exist
		// Return apkInfo
		if (apk.marketBean == null)
			apk.marketBean = new MarketBean();
		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(GET_APP_INFO_CMD);
			pstmt.setString(1, apk.packageName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				apk.marketBean.marketAppName = rs.getString("name");
				apk.marketBean.marketDescription = rs.getString("description");
				apk.marketBean.marketDeveloper = rs.getString("batchdevname");
				return apk;
			} else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	// If process wrongly, return 0
	public int getNextAppId() {
		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(GET_NEXT_ID_CMD);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("min(application_id)") - 1;
			} else
				return 0;
		} catch (SQLException e) {
			System.out
					.println("You may have inserted duplicated search history in a short time. Please be patient.");
			e.printStackTrace();
		}
		return 0;
	}

	public int updatePackageName(ApkBean apk) {
		int ret = -1;

		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(UPDATE_PACKAGE_NAME_CMD);
			pstmt.setString(1, apk.packageName);
			pstmt.setInt(2, Integer.valueOf(apk.misc));
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

	public static void updatePackageNames() {
		// Insert all apk's package name into apk_yingyonghui table
		ApkManDao dao = ApkManDao.GetInstance();

		File[] files = FileUtilsExt.getAllFiles(new File(
				"E:\\apk_yingyonghui\\marketapps"), new OnlyExt("apk"));
		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
				System.out.println("BadApk " + file.getAbsolutePath());
				continue;
			}
			try {
				String parentDir = file.getParentFile().getName();
				apk.misc = Integer.valueOf(
						parentDir.substring(0, parentDir.indexOf('.')))
						.toString();
				dao.updatePackageName(apk);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			// dao.insertPackageName(apk);
		}
	}

	public int updateCert(ApkBean apk) {
		int ret = -1;

		for (CertBean cert : apk.certs) {
			try {
				PreparedStatement pstmt = (PreparedStatement) conn
						.prepareStatement(UPDATE_CERT_CMD);
				pstmt.setString(1, cert.officialSite);
				pstmt.setString(2, cert.note);
				pstmt.setString(3, cert.certificateHash);
				pstmt.execute();
				ret = 0;
			} catch (SQLException e) {
				System.out
						.println("You may have inserted duplicated search history in a short time. Please be patient.");
				e.printStackTrace();
				ret = -2;
			}
		}
		return ret;
	}

	public int insertCert(ApkBean apk) {
		int ret = -1;

		for (CertBean cert : apk.certs) {
			try {
				PreparedStatement pstmt = (PreparedStatement) conn
						.prepareStatement(INSERT_CERT_CMD);
				pstmt.setString(1, cert.certificateHash);
				pstmt.setString(2, ((X509Certificate) cert.certificate)
						.getIssuerX500Principal().toString());
				pstmt.setString(3, cert.certificate.toString());
				pstmt.setString(4, cert.officialSite);
				pstmt.setString(5, cert.note);
				pstmt.execute();
				ret = 0;
			} catch (SQLException e) {
				System.out
						.println("You may have inserted duplicated search history in a short time. Please be patient.");
				e.printStackTrace();
				ret = -2;
			}
		}
		return ret;
	}
}
