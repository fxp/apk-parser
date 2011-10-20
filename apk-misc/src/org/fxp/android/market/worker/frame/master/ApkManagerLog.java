package org.fxp.android.market.worker.frame.master;

public class ApkManagerLog {
	public static int SUCCESS = 0;
	public static int LIB_DONT_EXIST = -1;
	public static int LOG_CREATE_FAILED= -2;
	public static int DB_CONN_FAILED = -3;
	public static int INSERT_APK_FAILED = -4;
	public static int INSERT_CERT_FAILED = -5;

	public static int PARSE_APK_FAILED = -101;

	public static String genLog(int type, String message) {
		return type + "," + message;
	}
}
