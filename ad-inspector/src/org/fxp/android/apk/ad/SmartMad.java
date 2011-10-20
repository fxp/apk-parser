package org.fxp.android.apk.ad;

import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public class SmartMad extends AdPattern {

	@Override
	public boolean isExistZipEntry(JarEntry jarEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistAndroidManifest(ApkInfo apkInfo) {
		if (apkInfo.rawAndroidManifest != null
				&& apkInfo.rawAndroidManifest.toLowerCase().contains(
						"com.adchina.android"))
			return true;
		return false;
	}

	@Override
	public boolean isExistXml(ApkInfo apkInfo) {
		Map<String, String> layoutMap = apkInfo.layoutStrings;
		if (layoutMap == null)
			return false;

		Set<String> layoutFiles = layoutMap.keySet();
		for (String layoutFile : layoutFiles) {
			// System.out.println("layout:"+layoutMap.get(layoutFile));
			if (layoutMap.get(layoutFile).contains("com.madhouse.android"))
				return true;
		}
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
