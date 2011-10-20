package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public abstract class AdPattern {
	String name = this.getClass().getSimpleName();

	public String getName() {
		return name;
	}

	public boolean isExist(ApkInfo apkInfo) {
		if (isExistAndroidManifest(apkInfo))
			return true;
		if (isExistXml(apkInfo))
			return true;
		if (isExistClass(apkInfo))
			return true;
		if (isExistAndroidManifest(apkInfo))
			return true;
		if (isExistUrl(apkInfo))
			return true;
		return false;
	}

	public abstract boolean isExistZipEntry(JarEntry jarEntry);

	public abstract boolean isExistAndroidManifest(ApkInfo apkInfo);

	public abstract boolean isExistXml(ApkInfo apkInfo);

	public abstract boolean isExistClass(ApkInfo apkInfo);

	public abstract boolean isExistUrl(ApkInfo apkInfo);
}
