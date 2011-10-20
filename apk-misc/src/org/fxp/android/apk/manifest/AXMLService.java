package org.fxp.android.apk.manifest;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLService extends AXMLItem {
	private static final long serialVersionUID = 7831498328670555273L;
	public String name;
	public boolean exported;

	public AXMLService(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("name"))
				name = attributeValue;
			else if (attributeName.equals("exported"))
				exported = Boolean.valueOf(attributeValue);
			else
				parseComplete = false;
		} else
			parseComplete = false;
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
	}

}
