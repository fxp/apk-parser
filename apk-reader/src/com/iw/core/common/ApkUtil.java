package com.iw.core.common;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtil {
	private static final Logger log = Logger.getLogger(ApkUtil.class.getName());

	public static int BUFFER = 2048;

	private static String MANIFEST = "AndroidManifest.xml";
	private static String RESOURCE = "resources.arsc";
	private static String DEX = "classes.dex";
	private static String CERTIFICATE = "META-INF/MANIFEST.MF";

	/*
	 * If this apk file is a zip which contains one or more REAL apks, it should
	 * extract some or all of them TODO enhance it for more complex condition
	 * (e.g. check the header of the file)
	 */
	public synchronized static List<String> extractApks(String apkPath) {
		List<String> ret = new ArrayList<String>();
		try {
			ZipFile zip = new ZipFile(apkPath);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".apk")) {
					// Get a new tmp file
					String tmpFilePath = FileUtil
							.getTempFile("apktmp_", ".apk");
					FileOutputStream fos = new FileOutputStream(tmpFilePath);
					BufferedInputStream is = new BufferedInputStream(
							zip.getInputStream(entry));
					// Get it out of the box
					int count = 0;
					byte[] buf = new byte[BUFFER];
					count = is.read(buf, 0, BUFFER);
					while ((count = is.read(buf, 0, BUFFER)) != -1) {
						fos.write(buf, 0, count);
					}
					fos.flush();
					fos.close();

					// If the zip contains AndroidManifest.xml, basically it's
					// an apk
					JarFile jar = new JarFile(tmpFilePath);
					if (jar.getEntry("AndroidManifest.xml") != null)
						ret.add(tmpFilePath);
					jar.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static synchronized boolean validateApk(String apkPath) {
		boolean ret = false;
		JarFile apkJar = null;
		try {
			apkJar = new JarFile(apkPath, true);
			if (apkJar.getJarEntry(MANIFEST) == null
					|| (apkJar.getJarEntry(RESOURCE) == null)
					|| (apkJar.getJarEntry(DEX) == null)
					|| (apkJar.getJarEntry(CERTIFICATE) == null))
				ret = false;
			else
				ret = true;
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			if (apkJar != null) {
				try {
					apkJar.close();
				} catch (IOException e) {
					log.log(Level.WARNING, e.getCause().getMessage());
					e.printStackTrace();
				}
				apkJar = null;
			}
		}
		return ret;
	}
}
