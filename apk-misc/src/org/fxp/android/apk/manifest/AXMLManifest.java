package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;

public class AXMLManifest extends AXMLItem {
	private static final long serialVersionUID = 1160968880866968563L;
	public String sharedUserId;
	public String sharedUserLabel;
	public int versionCode;
	public String versionName;
	public String packageName;
	public String installLocation;

	public AXMLUsesSdk usesSdk;
	public ArrayList<AXMLUsesPermission> permissions = new ArrayList<AXMLUsesPermission>();
	public AXMLApplication application;

	public AXMLManifest(String elementName) {
		super(elementName);
	}

	@Override
	public void parseAttribute(String prefix, String attributeName,
			String attributeValue) {
		if (prefix.equals("android")) {
			if (attributeName.equals("sharedUserId"))
				sharedUserId = attributeValue;
			else if (attributeName.equals("sharedUserLabel"))
				sharedUserLabel = attributeValue;
			else if (attributeName.equals("versionCode"))
				// TODO correct parser
				try {
					versionCode = Integer.valueOf(attributeValue);
				} catch (Exception e) {
					versionCode=Integer.MIN_VALUE;
					e.printStackTrace();
				}
			else if (attributeName.equals("versionName"))
				versionName = attributeValue;
			else if (attributeName.equals("installLocation"))
				installLocation = attributeValue;
			else
				parseComplete = false;
		} else {
			if (attributeName.equals("package"))
				packageName = attributeValue;
			else
				parseComplete = false;
		}
	}

	@Override
	public void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
		if (parser.getName().equals("uses-permission")) {
			permissions.add((AXMLUsesPermission) new AXMLUsesPermission(
					"uses-permission").parse(parser));
		} else if (parser.getName().equals("application")) {
			application = (AXMLApplication) new AXMLApplication("application")
					.parse(parser);
		}  else if (parser.getName().equals("uses-sdk")) {
			usesSdk = (AXMLUsesSdk) new AXMLUsesSdk("uses-sdk").parse(parser);
		} else {
			parseComplete = false;
		}
	}
}
