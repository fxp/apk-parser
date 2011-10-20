package org.fxp.crawler.bean;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Embeddable
public class MarketBean implements Serializable {
	private static final long serialVersionUID = -4796770354595487179L;
	// For downloader
	public String marketName;
	public String marketPid;
	public String marketAppName;
	public String marketDownloadUrl;
	public String marketDescription;
	public String marketDeveloper;
	public int downloadCount = 0;
	public Date downloadTime;
	public Date publishTime;
	public String ref;

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketPid() {
		return marketPid;
	}

	public void setMarketPid(String marketPid) {
		this.marketPid = marketPid;
	}

	public String getMarketAppName() {
		return marketAppName;
	}

	public void setMarketAppName(String marketAppName) {
		this.marketAppName = marketAppName;
	}

	public String getMarketDownloadUrl() {
		return marketDownloadUrl;
	}

	public void setMarketDownloadUrl(String marketDownloadUrl) {
		this.marketDownloadUrl = marketDownloadUrl;
	}

	public String getMarketDescription() {
		return marketDescription;
	}

	public void setMarketDescription(String marketDescription) {
		this.marketDescription = marketDescription;
	}

	public String getMarketDeveloper() {
		return marketDeveloper;
	}

	public void setMarketDeveloper(String marketDeveloper) {
		this.marketDeveloper = marketDeveloper;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Date getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}
