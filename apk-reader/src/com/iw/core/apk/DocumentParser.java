package com.iw.core.apk;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentParser {

	private static List<String> FindInDocument(Document doc, String keyName,
			String attribName) {
		List<String> ret = new ArrayList<String>();
		NodeList usesPermissions = doc.getElementsByTagName(keyName);

		if (usesPermissions != null) {
			for (int s = 0; s < usesPermissions.getLength(); s++) {
				Node permissionNode = usesPermissions.item(s);
				if (permissionNode.getNodeType() == Node.ELEMENT_NODE) {
					Node node = permissionNode.getAttributes().getNamedItem(
							attribName);
					if (node != null)
						ret.add(node.getNodeValue());
				}
			}
		}
		return ret;
	}

	public static String FindStringInDocument(Document doc, String parent,
			String attr) {
		String ret = null;
		List<String> results = FindInDocument(doc, parent, attr);
		if (results.size() > 0)
			ret = results.get(0);
		return ret;
	}

	public static Boolean FindBooleanInDocument(Document doc, String parent,
			String attr) {
		Boolean ret = null;
		List<Boolean> results = FindBooleansInDocument(doc, parent, attr);
		if (results.size() > 0)
			ret = results.get(0);
		return ret;
	}

	public static Integer FindIntegerInDocument(Document doc, String parent,
			String attr) {
		Integer ret = null;
		List<Integer> results = FindIntegersInDocument(doc, parent, attr);
		if (results.size() > 0)
			ret = results.get(0);
		return ret;
	}

	public static List<String> FindStringsInDocument(Document doc,
			String parent, String attr) {
		return FindInDocument(doc, parent, attr);
	}

	public static List<Boolean> FindBooleansInDocument(Document doc,
			String parent, String attr) {
		List<String> results = FindInDocument(doc, parent, attr);
		List<Boolean> ret = new ArrayList<Boolean>();
		for (String result : results) {
			try {
				ret.add(Boolean.valueOf(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static List<Integer> FindIntegersInDocument(Document doc,
			String parent, String attr) {
		List<String> results = FindInDocument(doc, parent, attr);
		List<Integer> ret = new ArrayList<Integer>();
		for (String result : results) {
			try {
				ret.add(Integer.valueOf(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
