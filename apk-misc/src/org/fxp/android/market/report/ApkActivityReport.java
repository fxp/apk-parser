package org.fxp.android.market.report;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.android.apk.manifest.*;

public class ApkActivityReport {
	public static String logEntry = "";

	public static void addField(Object newField) {
		if (logEntry.equals(""))
			logEntry = newField.toString();
		else
			logEntry += "," + newField;
	}

	public static void newEntry() {
		System.out.println(logEntry);
		logEntry = "";
	}

	public static void main(String[] args) {
		ApkBean apk = ApkFileManager.unzipApk(args[0]);
		// Get all application.activity
		for (AXMLActivity activity : apk.apkManifest.application.activities) {
			addField(apk.apkLocalPath);
			addField(apk.packageName);
			addField(apk.versionCode);
			addField("activity");
			addField(activity.name);
			newEntry();
		}

		for (AXMLMetaData metaData : apk.apkManifest.application.metaDatas) {
			addField(apk.apkLocalPath);
			addField(apk.packageName);
			addField(apk.versionCode);
			addField("meta-data");
			addField(metaData.name);
			newEntry();
		}
		for (AXMLActivity activity : apk.apkManifest.application.activities) {
			addField(apk.apkLocalPath);
			addField(apk.packageName);
			addField(apk.versionCode);
			addField("activity");
			addField(activity.name);
			newEntry();
			for (AXMLMetaData metaData : activity.metaDatas) {
				addField(apk.apkLocalPath);
				addField(apk.packageName);
				addField(apk.versionCode);
				addField("meta-data");
				addField(metaData.name);
			}
		}
		for (AXMLService service : apk.apkManifest.application.services) {
			addField(apk.apkLocalPath);
			addField(apk.packageName);
			addField(apk.versionCode);
			addField("service");
			addField(service.name);
			newEntry();
		}
		for (AXMLReciever reciever : apk.apkManifest.application.receivers) {
			addField(apk.apkLocalPath);
			addField(apk.packageName);
			addField(apk.versionCode);
			addField("reciever");
			addField(reciever.name);
			newEntry();
		}
	}
}
