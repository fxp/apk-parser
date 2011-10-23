package com.iw.core.apk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iw.core.common.FileUtil;

public class ResourceParser extends DocumentParser {

	public static void addResource(String docName, String content, ApkInfo info) {
		info.addRawResource(docName, content);
	}
	

}
