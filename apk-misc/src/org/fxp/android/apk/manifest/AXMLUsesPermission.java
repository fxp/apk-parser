package org.fxp.android.apk.manifest;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLUsesPermission extends AXMLItem{
	private static final long serialVersionUID = -4441090752091794964L;
	
	public String name;

	public AXMLUsesPermission(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("name"))
				name=attributeValue;
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
