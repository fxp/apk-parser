package com.iw.core.apk;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class ApkInfo {

	private String path;
	private String fileName;

	private String label;
	private String versionName;
	private String versionCode;
	private String minSdkVersion;
	private String packageName;

	// All permissions with
	private Map<String, String> permissions = new Hashtable<String, String>();
	private List<Certificate> certs = new ArrayList<Certificate>();
	private ApkIcon icon;
	private ApkScreenSupport screenSupport = new ApkScreenSupport();
	private Map<String, List<String>> resStrings = new Hashtable<String, List<String>>();

	private Map<String, JarEntry> entries = new Hashtable<String, JarEntry>();
	private String rawAndroidManifest;
	private Map<String, String> layoutStrings = new Hashtable<String, String>();
	private Map<String, String> decompiledClasses = new Hashtable<String, String>();

	public static class ApkPermission {
		Hashtable<String, String> permissions = new Hashtable<String, String>();

		public ApkPermission(String type, List<String> values) {
			for (String value : values) {
				permissions.put(type, value);
			}
		}
	}

	public List<Certificate> getCerts() {
		return certs;
	}

	public void addCert(Certificate cert) {
		if (cert == null)
			return;
		if (certs == null)
			certs = new ArrayList<Certificate>();
		certs.add(cert);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getMinSdkVersion() {
		return minSdkVersion;
	}

	public void setMinSdkVersion(String minSdkVersion) {
		this.minSdkVersion = minSdkVersion;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Map<String, String> getPermissions() {
		return permissions;
	}

	public void addPermission(String type, String permission) {
		this.permissions.put(type, permission);
	}

	public ApkIcon getIcon() {
		return icon;
	}

	public void setIcon(ApkIcon icon) {
		this.icon = icon;
	}

	public ApkScreenSupport getScreenSupport() {
		return screenSupport;
	}

	public void setScreenSupport(ApkScreenSupport screenSupport) {
		this.screenSupport = screenSupport;
	}

	public Map<String, List<String>> getResStrings() {
		return resStrings;
	}

	public void setResStrings(Map<String, List<String>> resStrings) {
		this.resStrings = resStrings;
	}

	public Map<String, JarEntry> getEntries() {
		return entries;
	}

	public void addEntry(String name, JarEntry entry) {
		getEntries().put(name, entry);
	}

	public String getRawAndroidManifest() {
		return rawAndroidManifest;
	}

	public void setRawAndroidManifest(String rawAndroidManifest) {
		this.rawAndroidManifest = rawAndroidManifest;
	}

	public Map<String, String> getLayoutStrings() {
		return layoutStrings;
	}

	public void setLayoutStrings(Map<String, String> layoutStrings) {
		this.layoutStrings = layoutStrings;
	}

	public Map<String, String> getDecompiledClasses() {
		return decompiledClasses;
	}

	public void setDecompiledClasses(Map<String, String> decompiledClasses) {
		this.decompiledClasses = decompiledClasses;
	}

	/*
	 * Format <supports-screens android:resizeable=["true"| "false"]
	 * android:smallScreens=["true" | "false"] android:normalScreens=["true" |
	 * "false"] android:largeScreens=["true" | "false"]
	 * android:xlargeScreens=["true" | "false"] android:anyDensity=["true" |
	 * "false"] android:requiresSmallestWidthDp="integer"
	 * android:compatibleWidthLimitDp="integer"
	 * android:largestWidthLimitDp="integer"/>
	 */
	public class ApkScreenSupport {
		Boolean resizeable = null;
		Boolean smallScreens = null;
		Boolean normalScreens = null;
		Boolean largeScreens = null;
		Boolean xlargeScreens = null;
		Boolean anyDensity = null;
		int requiresSmallestWidthDp = -1;
		int compatibleWidthLimitDp = -1;
		int largestWidthLimitDp = -1;
	}

	public class ApkIcon {
		List<String> jarPath;
	}

}
