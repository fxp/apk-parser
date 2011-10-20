package org.fxp.android.apk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.fxp.tools.FileUtilsExt;

public class ApkManager {
	private static String marketLib;
	private static String unknownDir;

	public void init() throws IOException, ConfigurationException {
		XMLConfiguration config = new XMLConfiguration();
		File file = new File("apkmanager_config.xml");
		if (!file.isFile()) {
			// Create new configuration file
			file.createNewFile();
			config.setFile(file);
			config.setProperty("lib", "lib");
			config.setProperty("unknow_lib", "unknown");
			config.save();
		}
		config.setFile(file);
		config.load();
		marketLib = config.getString("lib", "lib");
		unknownDir = config.getString("unknow_lib", "unknown");
	}

	protected String getApkName(ApkBean apk) {
		if (apk == null) {
			return unknownDir + "/" + UUID.randomUUID();

		} else {
			int conflictCount = 0;
			String fileName = marketLib + "/" + apk.packageName + "."
					+ apk.versionCode;
			File file = new File(fileName + "." + conflictCount);
			while (file.isFile()) {
				file = new File(fileName + "." + (conflictCount++));
			}
			return file.getAbsolutePath();
		}
	}

	public boolean saveApk(ApkBean apk, InputStream fileData) {
		try {
			String fileName=getApkName(apk);
			FileUtilsExt.writeToFile(fileName, fileData, true);
			System.out.println("File saved "+fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void setMarketLib(String marketLib) {
		ApkManager.marketLib = marketLib;
	}

	public static String getMarketLib() {
		return marketLib;
	}

}
