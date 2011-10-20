package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLIntentFilter extends AXMLItem{
	private static final long serialVersionUID = -1851441843443359143L;
	public ArrayList<AXMLAction> actions = new ArrayList<AXMLAction>();
	public ArrayList<AXMLCategory> categories = new ArrayList<AXMLCategory>();
	
	public AXMLIntentFilter(String elementName) {
		super(elementName);
	}

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		this.parseComplete=false;
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
		if (parser.getName().equals("action")) {
			actions.add((AXMLAction) new AXMLAction("action").parse(parser));
		}else if (parser.getName().equals("category")) {
			categories.add((AXMLCategory) new AXMLCategory("category").parse(parser));
		} else {
			parseComplete = false;
		}
	}

}
