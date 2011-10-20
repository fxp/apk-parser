package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public class Jiashi extends AdPattern {

	@Override
	public boolean isExistZipEntry(JarEntry jarEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistAndroidManifest(ApkInfo apk) {
		if (apk.rawAndroidManifest != null
				&& apk.rawAndroidManifest.contains("com.casee.adsdk"))
			return true;
		return false;
	}

	@Override
	public boolean isExistClass(ApkInfo apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistXml(ApkInfo apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistUrl(ApkInfo apkInfo) {
		// TODO Auto-generated method stub
		return false;
	}

}
