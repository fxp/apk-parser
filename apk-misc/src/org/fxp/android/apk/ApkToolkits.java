package org.fxp.android.apk;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.fxp.tools.ObjectToXMLUtil;

public class ApkToolkits {
	
	public static void saveApkToFile(ApkBean apk, String fileName) throws FileNotFoundException, IOException{
		ObjectToXMLUtil.objectXmlEncoder(apk, fileName);
	}

	public static ApkBean loadApkFromFile(String fileName) throws FileNotFoundException, IOException{
		return (ApkBean)ObjectToXMLUtil.objectXmlDecoder(fileName);
	}
}
