package org.fxp.android.apk.tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.fxp.android.apk.ApkBean;

public class ApkOdsManager {
	public static final String NEW_LINE = System.getProperty("line.separator");
	private BufferedWriter odsWriter;

	public void init(String odsFile) throws IOException {
		odsWriter = new BufferedWriter(new FileWriter(odsFile));
	}

	public void putApk(ApkBean apk, ApkOdsItem odsItem) throws IOException {
		odsWriter.write(odsItem.toString());
		odsWriter.write(NEW_LINE);
	}
}
