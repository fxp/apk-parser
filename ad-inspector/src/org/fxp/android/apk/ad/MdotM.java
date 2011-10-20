package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public class MdotM extends AdPattern {

	@Override
	public boolean isExistZipEntry(JarEntry jarEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistAndroidManifest(ApkInfo apkInfo) {
		if (apkInfo.rawAndroidManifest != null
				&& apkInfo.rawAndroidManifest.toLowerCase().contains(
						"com.mdotm.android"))
			return true;
		return false;
	}

	@Override
	public boolean isExistXml(ApkInfo apkInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistClass(ApkInfo apkInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistUrl(ApkInfo apkInfo) {
		// TODO Auto-generated method stub
		return false;
	}

}
