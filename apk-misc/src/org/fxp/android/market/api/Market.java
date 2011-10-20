package org.fxp.android.market.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.EncodingToolkit;
import org.fxp.tools.ProxyProvider;

public abstract class Market {
	public String market_name;
	public String version;

	ProxyProvider proxys = null;

	public static Logger log;

	public abstract ApkBean[] searchAndDownload(String keyword);

	public abstract ApkBean[] downloadApks(ApkBean[] apks);

	public abstract ApkBean downloadApk(ApkBean apk);

	protected abstract ApkBean[] doApkFetch(String keyword);

	public String saveApkFile(ApkBean apk, InputStream in) throws IOException {
		int bufSize = 1024 * 64;
		// Save Apk file to a temp file.
		File file = new File(ApkFileManager.getApkFileName(apk, "TEMP", false));

		apk.apkLocalPath = file.getAbsolutePath();
		
		BufferedOutputStream buffer = new BufferedOutputStream(
				new FileOutputStream(apk.apkLocalPath));

		byte byt[] = new byte[bufSize];
		System.out.print("ID " + apk.marketBean.marketPid + "\tdownloading ");

		int i;
		long l;
		for (l = 0L; (i = in.read(byt)) != -1; l += i) {
			buffer.write(byt, 0, i);
			System.out.print("|");
		}
		System.out.println(" saved(" + (l / 1024) + "k)");
		buffer.close();
		in.close();

		return apk.apkLocalPath;
	}

	public abstract void initIdLib();

}
