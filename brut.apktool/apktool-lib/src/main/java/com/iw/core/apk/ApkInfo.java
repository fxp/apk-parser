package com.iw.core.apk;

import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

public class ApkInfo implements Serializable {

	private static final long serialVersionUID = -7652985665552224926L;

	private String path;

	private String label;
	private String versionName;
	private String versionCode;
	private String minSdkVersion;
	private String packageName;

	// All permissions
	private Set<String> permissions = new HashSet<String>();
	// All certificates
	private List<Certificate> certs = new ArrayList<Certificate>();

	// All icon paths
	private ApkIcon icon = new ApkIcon();
	private ApkScreenSupport screenSupport = new ApkScreenSupport();

	// File hash
	String fileHash;
	// Zip entires
	private Map<String, JarEntryInfo> entries = new Hashtable<String, JarEntryInfo>();
	// Raw content of AndroidManifest.xml
	private String rawAndroidManifest;
	// Raw content of resources
	private Map<ApkResourceKey, byte[]> rawResources = new Hashtable<ApkResourceKey, byte[]>();
	// Decompiled source like smali or just java source code
	private Map<String, String> decompiledClasses = new Hashtable<String, String>();
	// Raw content of asset
	private Map<String, byte[]> assets = new Hashtable<String, byte[]>();

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Map<ApkResourceKey, byte[]> getRawResources() {
		return rawResources;
	}

	public void addRawResource(ApkResourceKey key, byte[] rawResource) {
		rawResources.put(key, rawResource);
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

	public Set<String> getPermissions() {
		return permissions;
	}

	public void addPermission(String permission) {
		this.permissions.add(permission);
	}

	public ApkIcon getIcon() {
		return icon;
	}

	public void setIcon(Set<String> jarPaths) {
		icon.jarPath = jarPaths;
	}

	public void addIcons(List<String> jarPaths) {
		for (String jarPath : jarPaths) {
			icon.jarPath.add(jarPath);
		}
	}

	public ApkScreenSupport getScreenSupport() {
		return screenSupport;
	}

	public void setScreenSupport(ApkScreenSupport screenSupport) {
		this.screenSupport = screenSupport;
	}

	public Map<String, JarEntryInfo> getEntries() {
		return entries;
	}

	public void addEntry(String name, JarEntry entry) {
		getEntries().put(name, new JarEntryInfo(entry));
	}

	public String getRawAndroidManifest() {
		return rawAndroidManifest;
	}

	public void setRawAndroidManifest(String rawAndroidManifest) {
		this.rawAndroidManifest = rawAndroidManifest;
	}

	public Map<String, String> getDecompiledClasses() {
		return decompiledClasses;
	}

	public void addDecompiledClasses(String decompiledName, String rawDecompiled) {
		decompiledClasses.put(decompiledName, rawDecompiled);
	}

	public Map<String, byte[]> getAssets() {
		return assets;
	}

	public void addAssets(String assetName, byte[] asset) {
		this.assets.put(assetName, asset);
	}

	public static class ApkResourceKey implements Serializable {
		String type;
		String name;

		public ApkResourceKey(String type, String name) {
			super();
			this.type = type;
			this.name = name;
		}
	}

	public static class ApkScreenSupport implements Serializable {
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

	public static class ApkIcon implements Serializable {
		Set<String> jarPath = new HashSet<String>();
	}

	public static class JarEntryInfo implements Serializable {
		String name;
		long lastModifyTime;
		long crc;

		public JarEntryInfo(JarEntry entry) {
			this.name = entry.getName();
			this.lastModifyTime = entry.getTime();
			this.crc = entry.getCrc();
		}

		public String getName() {
			return name;
		}

		public Long getLastModifyTime() {
			return lastModifyTime;
		}

		public long getCrc() {
			return crc;
		}

	}
}
