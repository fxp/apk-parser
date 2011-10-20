package org.fxp.android.apk;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.fxp.android.apk.manifest.AXMLManifest;
import org.fxp.crawler.bean.CertBean;
import org.fxp.crawler.bean.MarketBean;

@Entity
public class ApkBean implements Serializable, Comparable<ApkBean> {
	private static final long serialVersionUID = -8617841844584910667L;

	// Info inside apk file
	public String packageName;
	public int versionCode;
	public String versionName;
	public String mainName;
	@Transient
	public List<JarEntry> jarEntries = new ArrayList<JarEntry>();
	public List<String> classNames=new ArrayList<String>();

	public Date apkCreateTime;
	public String apkLocalPath;
	@Id
	public String apkFileChecksum;

	@Transient
	public AXMLManifest apkManifest;
	public List<String> apkPermission = new ArrayList<String>();

	// Specified in market page
	@OneToMany(targetEntity = CertBean.class)
	public List<CertBean> certs = new ArrayList<CertBean>();
	@Embedded
	public MarketBean marketBean = new MarketBean();

	// For search behavior by downloader
	public String searchKeyword;
	public String searchResult;
	public int searchResultNum;

	// Other
	public String misc;

	public boolean hasSameCerts(ApkBean comparedApk) {
		if (certs.size() != comparedApk.getCerts().size())
			return false;

		for (int i = 0; i < certs.size(); i++) {
			if (!certs.get(i).certificateHash.equals(comparedApk.getCerts()
					.get(i).certificateHash))
				return false;
		}
		return true;
	}

	public int compareTo(ApkBean compareObject) {
		if (this.getVersionCode() < compareObject.getVersionCode())
			return -1;
		else if (this.getVersionCode() == compareObject.getVersionCode())
			return 0;
		else
			return 1;
	}

	public String certInfo() {
		String certInfo = "";
		for (CertBean cert : certs) {
			if (cert.certificate instanceof X509Certificate) {
				certInfo += "Hash:" + cert.certificateHash + "\r\n";
				certInfo += cert.certificate.toString();
			} else {
				certInfo += "Unknow certificate ";
			}
		}
		return certInfo;
	}

	public String toString() {
		return "Package name: " + packageName + "\r\nVersion code: "
				+ versionCode + "\r\nVersion name: " + versionName
				+ "\r\nApp name: " + marketBean.marketAppName + "\r\nMain: "
				+ mainName + "\r\nLocation: " + apkLocalPath
				+ "\r\nCertificate:\r\n" + certInfo();
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

	public AXMLManifest getApkManifest() {
		return apkManifest;
	}

	public void setApkManifest(AXMLManifest apkManifest) {
		this.apkManifest = apkManifest;
	}

	public List<String> getApkPermission() {
		return apkPermission;
	}

	public void setApkPermission(List<String> apkPermission) {
		this.apkPermission = apkPermission;
	}

	public MarketBean getMarketBean() {
		return marketBean;
	}

	public void setMarketBean(MarketBean marketBean) {
		this.marketBean = marketBean;
	}

	public List<CertBean> getCerts() {
		return certs;
	}

	public void setCerts(List<CertBean> certs) {
		this.certs = certs;
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
