package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLReciever extends AXMLItem {
	private static final long serialVersionUID = -3445449351111538953L;

	public String name;
	public ArrayList<AXMLIntentFilter> intentFilters = new ArrayList<AXMLIntentFilter>();

	public AXMLReciever(String elementName) {
		super(elementName);
		// TODO Auto-generated constructor stub
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
		if (parser.getName().equals("intent-filter")) {
			intentFilters.add((AXMLIntentFilter) new AXMLIntentFilter(
					"intent-filter").parse(parser));
		} else {
			parseComplete = false;
		}
	}

}
