package org.fxp.android.market.worker.frame.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.api.ApkDAO;
import org.fxp.crawler.bean.CertBean;
import org.fxp.mode.SingletonException;
import org.fxp.tools.FileUtilsExt;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ApkManagerDao {
	private static ApkManagerDao self = null;
	private static boolean instance_flag = false;

	// Database
	private String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	private String DB_URL = "jdbc:mysql://192.168.22.12:3306/apks";
	private String DB_USER = "apkmanager";
	private String DB_PASSWORD = "11q22w33e";
	private Connection conn = null;

	private String SQL_NEXT_CERT_ID = "SELECT max(id) FROM `apks`.`apk_cert`";
	private String SQL_INSERT_APK = "INSERT INTO apk("
			+ "package,version_code,apk_hash,market,cert_id,file_name,download_url,create_time,download_time,import_time,ref,apk_file) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,NOW(),?,?); ";
	private String SQL_INSERT_CERT = "INSERT INTO apk_cert("
			+ "id,public_key,issuer,public_key_algo,sign_algo,cert_body)"
			+ "VALUES(?,?,?,?,?,?);";
	private String SQL_GET_APK = "SELECT * FROM apk"
			+ "WHERE package=?,version_code,apk_hash,market,cert_id,file_name,download_url,create_time,download_time,import_time,ref,apk_file";

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

	public static ApkManagerDao GetInstance() {
		if (self == null) {
			self = new ApkManagerDao();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public static ApkManagerDao resetDb() {
		instance_flag = false;
		self = null;
		return GetInstance();
	}

	public void fillApk(ApkBean apk) {
		if (apk.packageName == null)
			apk.packageName = "";
		if (apk.marketBean.marketName == null)
			apk.marketBean.marketName = "";
		if (apk.apkLocalPath == null)
			apk.apkLocalPath = "";
		if (apk.apkFileChecksum == null)
			apk.apkFileChecksum = "";
		if (apk.marketBean.marketDownloadUrl == null)
			apk.marketBean.marketDownloadUrl = "";
		if (apk.marketBean.downloadTime == null)
			apk.marketBean.downloadTime = new java.sql.Date(Calendar
					.getInstance().getTime().getTime());
		if (apk.apkCreateTime == null)
			apk.apkCreateTime = new java.sql.Date(Calendar.getInstance()
					.getTime().getTime());
		if (apk.marketBean.ref == null)
			apk.marketBean.ref = "";
	}

	// Clean up all database
	public int clearDatabase() {
		return 0;
	}

	public int getNextCertId() {
		PreparedStatement pstmt;
		ResultSet rs;
		int nextId = -1;

		try {
			pstmt = (PreparedStatement) conn.prepareStatement(SQL_NEXT_CERT_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				nextId = rs.getInt(1) + 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return nextId;
		}
		return nextId;
	}

	// Insert apk info into database
	public int insertApk(ApkBean apk) {
		fillApk(apk);
		int ret = -1;
		if (apk.marketBean.marketName.equals("") || apk.packageName.equals("")
				|| apk.versionCode == 0 || apk.apkLocalPath.equals("")
				|| apk.apkFileChecksum.equals(""))
			return ret;

		String insertQuery = SQL_INSERT_APK;
		try {

			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(insertQuery);
			pstmt.setString(1, apk.packageName);
			pstmt.setInt(2, Integer.valueOf(apk.versionCode));
			pstmt.setString(3, new String(apk.apkFileChecksum));
			pstmt.setString(4, apk.marketBean.marketName);
			// apk.cert_id = getNextCertId();
			// TODO
			// Modify apk_id
			pstmt.setInt(5, 0);
			pstmt.setString(6, apk.apkLocalPath);
			pstmt.setString(7, apk.marketBean.marketDownloadUrl);
			pstmt.setDate(8, apk.apkCreateTime);
			pstmt.setDate(9, apk.marketBean.downloadTime);
			pstmt.setString(10, apk.marketBean.ref);
			File file = new File(apk.apkLocalPath);
			// pstmt.setBinaryStream(11, new FileInputStream(file),
			// (int) file.length());
			pstmt.setBytes(11, "".getBytes());

			pstmt.execute();
			ret = ApkManagerLog.SUCCESS;
		} catch (SQLException e) {
			if (e.getSQLState().equals("23000"))
				System.out
						.println("You may have inserted duplicated search history in a short time. Please be patient.");
			else
				e.printStackTrace();
			ret = ApkManagerLog.INSERT_APK_FAILED;
		}/*
		 * catch (FileNotFoundException e) { e.printStackTrace(); }
		 */
		return ret;
	}

	// Delete apk info into database
	public int deleteApk(ApkBean apk) {
		try {
			String sql2 = "DELETE FROM apk WHERE package=? && version_code=? && market='unknown'";
			PreparedStatement pstmt;
			pstmt = (PreparedStatement) conn.prepareStatement(sql2);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				int count = resultSet.getInt(1);
			}
			conn.close();
			return ApkManagerLog.SUCCESS;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ApkManagerLog.DB_CONN_FAILED;
	}

	public FileOutputStream[] getApk() {
		/*
		 * String sql2 = "SELECT name, description, image FROM pictures ";
		 * PreparedStatement stmt2 = conn.prepareStatement(sql2); ResultSet
		 * resultSet = stmt2.executeQuery(); while (resultSet.next()) { String
		 * name = resultSet.getString(1); String description =
		 * resultSet.getString(2); File image2 = new File("D:\\jason.jpg");
		 * FileOutputStream fos = new FileOutputStream(image2);
		 * 
		 * byte[] buffer = new byte[1]; InputStream is =
		 * resultSet.getBinaryStream(3);
		 * 
		 * while (is.read(buffer) > 0) { fos.write(buffer); } fos.close(); }
		 * conn.close();
		 */return null;
	}

	// Insert certification info into database
	public int insertCert(ApkBean apk) {
		fillApk(apk);
		int ret = -1;
		if (apk.certs.size() == 0 || apk.apkLocalPath.equals(""))
			return ret;

		String insertQuery = SQL_INSERT_CERT;
		for (CertBean cert : apk.certs) {
			try {
				PreparedStatement pstmt = (PreparedStatement) conn
						.prepareStatement(insertQuery);
				// TODO modify apk_id
				// pstmt.setInt(1, apk.apk_id);
				pstmt.setInt(1, 0);
				pstmt.setBytes(2, cert.certificate.getPublicKey().getEncoded());
				pstmt.setString(3, ((X509Certificate) cert.certificate)
						.getIssuerX500Principal().getName());
				pstmt.setString(4, cert.certificate.getPublicKey()
						.getAlgorithm());
				pstmt.setString(5,
						((X509Certificate) cert.certificate).getSigAlgName());
				pstmt.setBytes(6,
						((X509Certificate) cert.certificate).getEncoded());
				pstmt.execute();
				ret = ApkManagerLog.SUCCESS;
			} catch (SQLException e) {
				System.out
						.println("You may have inserted duplicated search history in a short time. Please be patient.");
				e.printStackTrace();
				ret = ApkManagerLog.INSERT_CERT_FAILED;
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			}
			return ret;
		}
		return ret;
	}

	// Delete certification info into database
	public int deleteCert(ApkBean apk) {
		return 0;
	}
}
