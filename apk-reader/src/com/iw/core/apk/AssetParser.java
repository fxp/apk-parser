package com.iw.core.apk;

public class AssetParser extends DocumentParser {

	public static void addAsset(String docName, String content, ApkInfo info) {
		info.addAssets(docName, content);
	}

}
