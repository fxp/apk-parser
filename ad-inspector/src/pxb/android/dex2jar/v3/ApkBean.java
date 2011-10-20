package pxb.android.dex2jar.v3;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

public class ApkBean implements Serializable, Comparable<ApkBean> {
	private static final long serialVersionUID = -8617841844584910667L;

	// Info inside apk file
	public String packageName;
	public int versionCode;
	public String versionName;
	public String mainName;
	public List<JarEntry> jarEntries = new ArrayList<JarEntry>();
	public List<String> classNames=new ArrayList<String>();

	public Date apkCreateTime;
	public String apkLocalPath;
	public String apkFileChecksum;

	public List<String> apkPermission = new ArrayList<String>();

	// For search behavior by downloader
	public String searchKeyword;
	public String searchResult;
	public int searchResultNum;

	// Other
	public String misc;

	public int compareTo(ApkBean compareObject) {
		if (this.getVersionCode() < compareObject.getVersionCode())
			return -1;
		else if (this.getVersionCode() == compareObject.getVersionCode())
			return 0;
		else
			return 1;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getMainName() {
		return mainName;
	}

	public void setMainName(String mainName) {
		this.mainName = mainName;
	}

	public Date getApkCreateTime() {
		return apkCreateTime;
	}

	public void setApkCreateTime(Date apkCreateTime) {
		this.apkCreateTime = apkCreateTime;
	}

	public String getApkLocalPath() {
		return apkLocalPath;
	}

	public void setApkLocalPath(String apkLocalPath) {
		this.apkLocalPath = apkLocalPath;
	}

	public String getApkFileChecksum() {
		return apkFileChecksum;
	}

	public void setApkFileChecksum(String apkFileChecksum) {
		this.apkFileChecksum = apkFileChecksum;
	}

	public List<String> getApkPermission() {
		return apkPermission;
	}

	public void setApkPermission(List<String> apkPermission) {
		this.apkPermission = apkPermission;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public String getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(String searchResult) {
		this.searchResult = searchResult;
	}

	public int getSearchResultNum() {
		return searchResultNum;
	}

	public void setSearchResultNum(int searchResultNum) {
		this.searchResultNum = searchResultNum;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

}
