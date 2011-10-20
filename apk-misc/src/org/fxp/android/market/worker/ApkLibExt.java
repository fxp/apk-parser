package org.fxp.android.market.worker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.fxp.android.apk.ApkBean;
import org.fxp.tools.ChineseConvert;
import org.fxp.tools.axml.AXMLBasicInfo;
import org.fxp.tools.axml.AXMLPrinter;
import org.fxp.tools.windows.PlatformWindows;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ApkLibExt {
	private static String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://10.18.135.200:3306/fengxiaoping";
	private static String DB_USER = "fengxiaoping";
	private static String DB_PASSWORD = "fengxiaoping";
	// DON'T forget '\' at end of paths
	private static String NORMAL_BASE_PATH = "Y:\\apk_file\\";
	private static String ERR_BASE_PATH = "Y:\\apk_err_file\\";
	private static String AXML_BASE_PATH = "Y:\\apk_xml\\";
	private static String DIFF_BASE_PATH = "Y:\\apk_diff_file\\";
	public static String AXMLFILE_EXT = "AndroidManifest.xml";

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private static String DISTINCT_APP_NAME_QUERY = "Select distinct app_name,check_times from (select app_name,check_times from market_gfan union select app_name,check_times from market_yingyonghui) alias order by check_times asc";
	private static String DIFF_APK_QUERY = "SELECT DISTINCT `fengxiaoping`.`market_yingyonghui`.`package_name` FROM `fengxiaoping`.`market_gfan`,`fengxiaoping`.`market_yingyonghui` WHERE `market_gfan`.`package_name`=`market_yingyonghui`.`package_name` and `market_gfan`.`version_code`>`market_yingyonghui`.`version_code`;";
	private static String DISTINCT_PACKAGE_NAME_QUERY = "SELECT DISTINCT package_name FROM (SELECT package_name from `fengxiaoping`.`apk_info` union SELECT package_name from  `fengxiaoping`.`market_gfan` union SELECT package_name FROM `fengxiaoping`.`market_yingyonghui`) alias";

	private static ArrayList<String> diffPackageNames = new ArrayList<String>();

	private static Connection conn = null;

	public boolean init() {
		try {
			Class.forName(DB_CLASS_NAME);
			String url = DB_URL;
			conn = (Connection) DriverManager.getConnection(url, DB_USER,
					DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public boolean isActive() {
		try {
			boolean dbStatus = conn.isValid(5);
			// TODO
			boolean fsStatus = true;
			return dbStatus && fsStatus;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private class OnlyExt implements FilenameFilter {
		String ext;

		public OnlyExt(String ext) {
			this.ext = "." + ext;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith(ext);
		}
	}

	public int transferDiffApk() {
		getDiffPackageName();
		File directoryOfPdfs = new File(NORMAL_BASE_PATH);
		// FilenameFilter onlyApk = new OnlyExt("");
		int trasferedApkCount = 0;
		if (directoryOfPdfs.isDirectory()) {
			// For all these files
			String filenames[] = directoryOfPdfs.list();
			ZipFile zipFile = null;
			ApkBean apkBean = null;

			for (int i = 0; i < filenames.length; i++) {
				try {
					zipFile = new ZipFile(NORMAL_BASE_PATH + filenames[i]);
					apkBean = getApkBasicInfo(zipFile);
					zipFile.close();
				} catch (IOException e1) {
					if (apkBean == null) {
						System.err.println("Error transfer " + NORMAL_BASE_PATH
								+ filenames[i]);
						continue;
					}
				}

				if (diffPackageNames.contains(apkBean.packageName)) {
					System.out.println("Transfering " + filenames[i]);
/*					PlatformWindows.copyfile(NORMAL_BASE_PATH + filenames[i],
							DIFF_BASE_PATH + filenames[i]);
*/					trasferedApkCount++;
				}
			}
		}
		return trasferedApkCount;
	}

	private String[] getDiffPackageName() {
		String query = DIFF_APK_QUERY;
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				diffPackageNames.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getSearchKeyWords() {
		PreparedStatement pstmt;
		ResultSet rs;
		ArrayList<String> appNames = new ArrayList<String>();
		String query = DISTINCT_APP_NAME_QUERY;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				appNames.add(rs.getString(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			ChineseConvert converter = new ChineseConvert();
			for (String apkName : appNames) {
				String tmp = apkName;
				StringBuffer from = new StringBuffer();
				from.append(apkName);
				converter.translate(from, "fan2Jian");
				if (!tmp.equals(from.toString()))
					appNames.add(from.toString());
			}
		} catch (Exception e) {
			return appNames.toArray(new String[appNames.size()]);
		}

		return appNames.toArray(new String[appNames.size()]);
	}

	public String[] getPackageNames() {
		PreparedStatement pstmt;
		ResultSet rs;
		ArrayList<String> allPackageNames = new ArrayList<String>();
		String query = DISTINCT_PACKAGE_NAME_QUERY;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				allPackageNames.add(rs.getString(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allPackageNames.toArray(new String[allPackageNames.size()]);
	}

	public boolean insertApk(String market, ApkBean info) {
		return false;
	}

	public void putSearchHistory(String marketName, String searchKeyword,
			String result, int resultNum) {
		// Date date=new Date();
		String insertQuery = "INSERT INTO market_search_history("
				+ "market_name," + "search_keyword," + "update_time,"
				+ "result," + "result_num) " + "VALUES(?,?,NOW(),?,?); ";
		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(insertQuery);
			pstmt.setString(1, marketName);
			pstmt.setString(2, searchKeyword);
			pstmt.setString(3, result);
			pstmt.setInt(4, resultNum);
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void updateMarketDb(ApkBean apk) {
		// Update market database
		try {
			PreparedStatement pstmt = null;
			String marketUpdateQuery = "INSERT INTO market_"
					+ apk.marketBean.marketName.toLowerCase()
					+ "(package_name,"
					+ "version_code,"
					+ "update_time,"
					+ "app_name,"
					+ "download_url,"
					+ "p_id)"
					+ " VALUES( ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE check_times= check_times+1";
			pstmt = (PreparedStatement) conn
					.prepareStatement(marketUpdateQuery);
			pstmt.setString(1, apk.packageName.trim());
			pstmt.setInt(2, apk.versionCode);
			java.util.Date today = new java.util.Date();
			pstmt.setDate(3, new java.sql.Date(today.getTime()));
			pstmt.setString(4, apk.marketBean.marketAppName.trim());
			pstmt.setString(5, apk.marketBean.marketDownloadUrl.trim());
			pstmt.setString(6, apk.marketBean.marketPid.trim());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static ApkBean unzipApk(String zipFileName) {
		ZipFile zipFile = null;
		ApkBean apkBean = null;

		try {
			zipFile = new ZipFile(zipFileName);
			apkBean = getApkBasicInfo(zipFile);
			zipFile.close();
		} catch (IOException e1) {
		}

		File f = new File(zipFileName);
		System.out.println("Delete file " + f.delete());

		return apkBean;
	}

	private static ApkBean getApkBasicInfo(ZipFile zipFile) {
		Enumeration enumeration = zipFile.entries();
		AXMLBasicInfo axmlBasicInfo = null;

		while (enumeration.hasMoreElements()) {
			try {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				if (zipEntry.getName().equals(AXMLFILE_EXT)) {
					String[] tmp = zipFile.getName().split("\\\\");
					String fileName = tmp[tmp.length - 1];
					String axmlFilePathName = AXML_BASE_PATH + fileName + "_"
							+ zipEntry.getName();
					copyInputStream(zipFile.getInputStream(zipEntry),
							new BufferedOutputStream(new FileOutputStream(
									axmlFilePathName)));

					// Open AndroidManifest.xml
//					axmlBasicInfo = AXMLPrinter.axmlBasicInfo(axmlFilePathName);

					// Use AXMLPrinter to get apk basic info
					if (axmlBasicInfo != null) {
						// System.out.print(zipFile.getName() + "|");
						ApkBean apkBean = new ApkBean();
//						apkBean.apkVersionCode = axmlBasicInfo.versionCode;
						apkBean.packageName = axmlBasicInfo.packageName;
						return apkBean;
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}
}
