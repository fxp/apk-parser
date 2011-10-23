package com.iw.core.apk;

public class DecompiledParser {

	public static void addDecompiled(String docName, String content,
			ApkInfo info) {
		info.addDecompiledClasses(docName, content);
	}
}
