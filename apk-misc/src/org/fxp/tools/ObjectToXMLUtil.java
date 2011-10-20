package org.fxp.tools;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ObjectToXMLUtil {
	public static void objectXmlEncoder(Object obj, String fileName)
			throws FileNotFoundException, IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			String path = fileName.substring(0, fileName.lastIndexOf('.'));
			File pFile = new File(path);
			pFile.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		XMLEncoder encoder = new XMLEncoder(fos);
		encoder.writeObject(obj);
		encoder.flush();
		encoder.close();
		fos.close();
	}

	public static Object objectXmlDecoder(String objSource)
			throws FileNotFoundException, IOException {
		File fin = new File(objSource);
		FileInputStream fis = new FileInputStream(fin);
		XMLDecoder decoder = new XMLDecoder(fis);
		Object obj = null;
		obj = decoder.readObject();
		fis.close();
		decoder.close();
		return obj;
	}
}