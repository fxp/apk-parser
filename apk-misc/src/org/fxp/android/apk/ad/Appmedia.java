package org.fxp.android.apk.ad;

import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;

public class Appmedia extends AdPattern {

	public Appmedia() {
		super("Appmedia");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isExist(ApkBean apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistEntry(JarEntry jarEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistManifest(ApkBean apk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistClass(ApkBean apk) {
		String dstString="com.bw.appmedia.AdView".replace('.', '/');
		for(String className:apk.classNames){
			if(className.equals(dstString))
				return true;
		}
		return false;
	}

}
