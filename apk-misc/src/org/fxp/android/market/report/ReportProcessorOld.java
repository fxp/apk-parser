package org.fxp.android.market.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.fxp.tools.EncodingToolkit;
import org.fxp.tools.CsvReader;
import org.fxp.tools.windows.PlatformWindows;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ReportProcessorOld {

	// Reports location
	public static String reportPath = null;
	private static int importedReport = 0;
	private static int updateGlobalReport = 0;
	private static int importedYingyonghuiReport = 0;
	private static int importedRawReport = 0;

	private static Connection conn = null;
	private static CsvReader csvReader = null;

	private static String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	private static String DB_URL = "jdbc:mysql://10.18.135.200:3306/fengxiaoping";
	private static String DB_USER = "fengxiaoping";
	private static String DB_PASSWORD = "fengxiaoping";

	private static String[] splitApkName(String apkName) {
		apkName = apkName.trim();
		ArrayList<String> nameList = new ArrayList<String>();
		int tmpPosStart = 0;
		int tmpPosEnd = apkName.indexOf(' ');
		while (tmpPosEnd != -1) {
			if (EncodingToolkit.isChinese(apkName.charAt(tmpPosEnd - 1))
					&& !EncodingToolkit
							.isChinese(apkName.charAt(tmpPosEnd + 1))) {
				nameList.add(apkName.substring(tmpPosStart, tmpPosEnd));
				tmpPosStart = tmpPosEnd + 1;
			}
			tmpPosEnd = apkName.indexOf(' ', tmpPosEnd + 1);
		}
		nameList.add(apkName.substring(tmpPosStart, apkName.length()));
		return (String[]) nameList.toArray(new String[nameList.size()]);
	}

	private static boolean isPackageName(String name) {
		String[] segNames = name.split("\\.");
		if (segNames.length < 2)
			return false;
		for (int i = 0; i < segNames.length; i++) {
			for (int j = 0; j < segNames[i].length(); j++) {
				char ch = segNames[i].charAt(j);
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
						|| (ch >= '0' && ch <= '9'))
					continue;
				else
					return false;
			}
		}
		return true;
	}

	public static void insertYingyonghui(String[] allNames, Calendar cal,
			String packageName, String ourVersion) {
		try {
			PreparedStatement pstmt;

			for (int i = 0; i < allNames.length; i++) {
				if (allNames[i] != null && !allNames[i].trim().equals("")
						&& !isPackageName(allNames[i])) {
					pstmt = (PreparedStatement) conn
							.prepareStatement("INSERT INTO market_yingyonghui(update_time, package_name, app_name, version_code)"
									+ " VALUE(?,?,?,?)");

					pstmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
					pstmt.setString(2, packageName.trim());
					pstmt.setString(3, allNames[i].trim());
					pstmt.setString(4, ourVersion.trim());

					try {
						pstmt.executeUpdate();
						importedYingyonghuiReport++;
					} catch (SQLException e) {
						if (e.getErrorCode() == 1062) {
							continue;
						}
						e.printStackTrace();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void updateApkInfo(String packageName, String newestVersion,
			String userNum, String newestUserNum) {
		PreparedStatement pstmt;
		try {
			String insertQuery = "INSERT INTO apk_info(package_name, version_code_newest, user_number,user_number_newest)"
					+ " VALUES(?,?,?,?);";
			pstmt = (PreparedStatement) conn.prepareStatement(insertQuery);
			pstmt.setString(1, packageName.trim());
			pstmt.setString(2, newestVersion.trim());
			pstmt.setString(3, userNum.trim());
			pstmt.setString(4, newestUserNum.trim());

			pstmt.execute();
			updateGlobalReport++;
		} catch (SQLException e) {
			// If this record exist
			if (e.getErrorCode() == 1062) {
				try {
					String query = "UPDATE apk_info SET "
						+"version_code_newest="+newestVersion
						+",user_number="+userNum
						+",user_number_newest="+newestUserNum
						+" WHERE package_name = ? and version_code_newest < ?";
					pstmt = (PreparedStatement) conn.prepareStatement(query);
					pstmt.setString(1, packageName.trim());
					pstmt.setString(2, newestVersion.trim());
					pstmt.executeUpdate();
					updateGlobalReport++;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return;
			}
			e.printStackTrace();
		}
	}

	private static void insertApkLog(Calendar cal, String packageName,
			String[] allNames, String ourVersion, String newestVersion,
			String userNum, String newestUserNum) {
		try {
			String query = "INSERT INTO apk_log_info"
					+ "(update_time, package_name, app_name, version_code_newest, user_number, user_number_newest) "
					+ "VALUE(?,?,?,?,?,?);";
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(query);
			pstmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
			pstmt.setString(2, packageName.trim());
			pstmt.setString(4, newestVersion.trim());
			pstmt.setString(5, userNum.trim());
			pstmt.setString(6, newestUserNum.trim());

			for (int i = 0; i < allNames.length; i++) {
				pstmt.setString(3, allNames[i]);
				pstmt.execute();
				importedRawReport++;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				return;
			}
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// Import report into database
		if (args.length < 1) {
			System.out.println("Usage: ReportProcessor [report file name]");
			return;
		}
		reportPath = args[0];
		try {
			csvReader = new CsvReader(new InputStreamReader(
					new FileInputStream(reportPath), Charset.forName("UTF-8")));

			csvReader.readHeaders();

			Class.forName(DB_CLASS_NAME);
			String url = DB_URL;
			conn = (Connection) DriverManager.getConnection(url, DB_USER,
					DB_PASSWORD);
		} catch (Exception e) {
			System.out.println("Cannot find report file " + reportPath);
			e.printStackTrace();
			return;
		}

		// Open csv file, read file create date
		try {
			while (csvReader.readRecord()) {
				// Get Record detail
				String appName = csvReader.get("");
				String appNameExt = csvReader.get("");
				String packageName = csvReader.get("");
				String ourVersion = csvReader.get("");
				String newestVersion = csvReader.get("");
				String userNum = csvReader.get("");
				String newestUserNum = csvReader.get("");

				String[] appNames = splitApkName(appName);
				String[] extNames = appNameExt.trim().split("/");
				String[] allNames = new String[appNames.length
						+ extNames.length];

				System.arraycopy(appNames, 0, allNames, 0, appNames.length);
				System.arraycopy(extNames, 0, allNames, appNames.length,
						extNames.length);

				// Get file created date
				String createdTime = PlatformWindows
						.getFileCreateDate(new File(reportPath));
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy/MM/dd");
				Date processDate = sdf.parse(createdTime);
				cal.setTime(processDate);

				// Insert record data into raw log database
				insertApkLog(cal, packageName, allNames, ourVersion,
						newestVersion, userNum, newestUserNum);

				// Insert record data into yingyonghui database
				insertYingyonghui(allNames, cal, packageName, ourVersion);

				// Update info to apk_simple_info
				updateApkInfo(packageName, newestVersion, userNum,
						newestUserNum);

				importedReport++;
				System.out.print("|");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Imported " + importedReport + " records");
		System.out.println("Updated global " + updateGlobalReport + " records");
		System.out.println("Imported Yingyonghui " + importedYingyonghuiReport + " records");
		System.out.println("Imported Raw " + importedRawReport + " records");
	}

}
