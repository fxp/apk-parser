package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLMetaData extends AXMLItem {
	private static final long serialVersionUID = -6769587426943496996L;

	public String name;
	public String resource;
	public String value;
	
	public ArrayList<AXMLIntentFilter> intentFilters = new ArrayList<AXMLIntentFilter>();

	public AXMLMetaData(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("name"))
				name = attributeValue;
			else if (attributeName.equals("resource"))
				resource = attributeValue;
			else if (attributeName.equals("value"))
				value = attributeValue;
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
