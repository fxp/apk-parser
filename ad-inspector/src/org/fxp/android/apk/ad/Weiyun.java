package org.fxp.android.apk.ad;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import apkReader.ApkInfo;

public class Weiyun extends AdPattern {

	@Override
	public boolean isExistZipEntry(JarEntry jarEntry) {

		return false;
	}

	@Override
	public boolean isExistAndroidManifest(ApkInfo apk) {
		return false;
	}

	@Override
	public boolean isExistClass(ApkInfo apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistXml(ApkInfo apk) {
//		Map<String, String> layoutMap = apk.layoutStrings;
//		if (layoutMap == null)
//			return false;
//
//		Set<String> layoutFiles = layoutMap.keySet();
//		for (String layoutFile : layoutFiles) {
//			// System.out.println("layout:"+layoutMap.get(layoutFile));
//			if (layoutMap.get(layoutFile).contains("wiyun"))
//				return true;
//		}
		return false;
	}

	@Override
	public boolean isExistUrl(ApkInfo apkInfo) {
		List<String> urls = apkInfo.dexUrls;
		for (String url : urls) {
			if (url.contains("d.wiyun.com"))
				return true;
		}
		return false;
	}
}
