package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.manifest.AXMLActivity;
import org.fxp.android.apk.manifest.AXMLManifest;

public class Admob extends AdPattern{
	
	public Admob() {
		super("Admob");
	}

	@Override
	public boolean isExist(ApkBean apk) {
		// check entries
		
		
		// check manifest
		
		
		// check decompile
		
		
		return false;
	}

	@Override
	public boolean isExistEntry(JarEntry jarEntry) {
		if(jarEntry.getName().contains("admob_advert.xml"))
			return true;

		return false;
	}

	@Override
	public boolean isExistManifest(ApkBean apk) {
		AXMLManifest manifest=apk.getApkManifest();
		for(AXMLActivity a:manifest.application.activities){
			if(a.name.equals("com.google.ads.AdActivity"))
				return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistClass(ApkBean apk) {
		String dstString="com.google.ads.AdActivity".replace('.', '/');
		for(String className:apk.classNames){
			if(className.equals(dstString))
				return true;
		}
		return false;
	}

}
