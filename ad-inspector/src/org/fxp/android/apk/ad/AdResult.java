package org.fxp.android.apk.ad;

import java.util.ArrayList;
import java.util.List;

public class AdResult {
	String apkPath = null;

	public AdResult(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getApkPath() {
		return apkPath;
	}

	private List<AdPattern> detectedAds = new ArrayList<AdPattern>();

	public void addDetectedAd(AdPattern pattern) {
		detectedAds.add(pattern);
	}

	public List<AdPattern> getDetectedAds() {
		return detectedAds;
	}
}
