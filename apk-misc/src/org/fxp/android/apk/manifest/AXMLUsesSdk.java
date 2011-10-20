package org.fxp.android.apk.manifest;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLUsesSdk extends AXMLItem {
	private static final long serialVersionUID = 8779183542789934167L;

	int minSdkVersion;

	public AXMLUsesSdk(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("minSdkVersion"))
				minSdkVersion = Integer.valueOf(attributeValue);
			else
				parseComplete = false;
		} else {
			parseComplete = false;
		}
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub

	}

}
