package org.fxp.android.apk.ad;

import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;

import org.fxp.android.apk.ApkBean;

public abstract class AdPattern {
	String name;

	public AdPattern(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

	public abstract boolean isExist(ApkBean apk);
	public abstract boolean isExistEntry(JarEntry jarEntry);
	public abstract boolean isExistManifest(ApkBean apk);
	public abstract boolean isExistClass(ApkBean apk);
}
