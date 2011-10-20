package org.fxp.android.apk.ad;

import java.util.List;
import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public class Inmobi extends AdPattern {

	@Override
	public boolean isExistZipEntry(JarEntry jarEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistAndroidManifest(ApkInfo apkInfo) {
		if (apkInfo.rawAndroidManifest != null
				&& apkInfo.rawAndroidManifest.toLowerCase().contains(
						"com.inmobi.androidsdk"))
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
		List<String> classNames=apkInfo.dexClassName;
		for(String className:classNames){
			if(className.contains("com.inmobi.androidsdk"))
				return true;
		}
		return false;
	}

	@Override
	public boolean isExistUrl(ApkInfo apkInfo) {
		// TODO Auto-generated method stub
		return false;
	}

}
