package org.fxp.android.apk.malicious;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.android.apk.manifest.AXMLActivity;

public class VirusGenimi extends Virus {
	
	public VirusGenimi(){
		virusName = "Genimi";
	}

	@Override
	public boolean isExist(String apkPath) {
		ApkBean apk = ApkFileManager.unzipApk(apkPath);
		if(apk==null)
			return false;
		for (AXMLActivity activity : apk.apkManifest.application.activities) {
			if (activity.name!=null&&activity.name.contains("geinimi"))
				return true;
		}
		return false;
	}

	@Override
	public String generateReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
