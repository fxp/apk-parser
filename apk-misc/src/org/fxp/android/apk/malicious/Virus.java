package org.fxp.android.apk.malicious;

public abstract class Virus {
	public String virusName=null;
	
	public abstract boolean isExist(String apkPath);
	public abstract String generateReport();
}
