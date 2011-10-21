package com.iw.core.apk;

import java.io.File;

public class ParserException extends Exception {
	private static final long serialVersionUID = -3583328409623565161L;

	public ParserException(String apkPath, String msg, Exception e) {
		System.err.print(apkPath+",");
		System.err.print(msg+",");
		System.err.println(e.toString());
	}

	public ParserException(File apkFile, String msg, Exception e) {
		System.err.print(apkFile.getAbsolutePath()+",");
		System.err.print(msg+",");
		System.err.println(e.toString());
	}
}
