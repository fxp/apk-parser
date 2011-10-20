package org.fxp.android.apk.manifest;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLAction extends AXMLItem {
	private static final long serialVersionUID = -5243571394563520236L;
	public String name;

	public AXMLAction(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("name"))
				name = attributeValue;
			else
				parseComplete = false;
		} else {
			parseComplete = false;
		}
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
	}

}
