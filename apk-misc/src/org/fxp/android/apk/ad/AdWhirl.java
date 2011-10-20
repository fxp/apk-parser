package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;

public class AdWhirl extends AdPattern {

	public AdWhirl() {
		super("AdWhirl");
	}

	@Override
	public boolean isExist(ApkBean apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistEntry(JarEntry jarEntry) {
		if(jarEntry.getName().contains("ad_whirl"))
			return true;
		
		return false;
	}

	@Override
	public boolean isExistManifest(ApkBean apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistClass(ApkBean apk) {
		// TODO Auto-generated method stub
		return false;
	}

}
