package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLApplication extends AXMLItem {
	private static final long serialVersionUID = 1279972485837337670L;
	public String name;

	public AXMLApplication(String elementName) {
		super(elementName);
	}

	public ArrayList<AXMLActivity> activities = new ArrayList<AXMLActivity>();
	public ArrayList<AXMLService> services = new ArrayList<AXMLService>();
	public ArrayList<AXMLReciever> receivers = new ArrayList<AXMLReciever>();
	public ArrayList<AXMLProvider> providers = new ArrayList<AXMLProvider>();
	public ArrayList<AXMLUsesLibrary> usesLibraries = new ArrayList<AXMLUsesLibrary>();
	public ArrayList<AXMLMetaData> metaDatas = new ArrayList<AXMLMetaData>();

	@Override
	protected void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("name"))
				name = attributeValue;
			else
				parseComplete = false;
		} else
			parseComplete = false;
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
		// TODO Auto-generated method stub
		if (parser.getName().equals("activity")) {
			activities.add((AXMLActivity) new AXMLActivity("activity")
					.parse(parser));
		} else if (parser.getName().equals("service")) {
			services.add((AXMLService) new AXMLService("service").parse(parser));
		} else if (parser.getName().equals("receiver")) {
			receivers.add((AXMLReciever) new AXMLReciever("receiver")
					.parse(parser));
		} else if (parser.getName().equals("meta-data")) {
			metaDatas.add((AXMLMetaData) new AXMLMetaData("meta-data")
					.parse(parser));
		} else {
			parseComplete = false;
		}
	}

}
