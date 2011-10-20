/*
 * Copyright 2008 Android4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fxp.tools.axml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.manifest.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

public class AXMLPrinter {
	public static String AXML_SCHEMAS = "http://schemas.android.com/apk/res/android";
	public static int MAX_XML_DEPTH = 20;
	public static String apkManifest = "";
	public static String[][] elementStack = new String[MAX_XML_DEPTH][2];
	private static ArrayList<String> permission = new ArrayList<String>();

	public static void apkSpy(InputStream stream, ApkBean apk) {
		AXmlResourceParser parser = new AXmlResourceParser();
		parser.open(stream);
		while (true) {
			try {
				int type = parser.next();
				if (type == XmlPullParser.END_DOCUMENT) {
					break;
				}
				switch (type) {
				case XmlPullParser.START_TAG: {
					if (parser.getName().equals("manifest"))
						processManifest(parser, apk);
				}
				case XmlPullParser.END_TAG: {
					// System.out.printf("</%s%s>",
					// getNamespacePrefix(parser.getPrefix()),
					// parser.getName());
					break;
				}
				case XmlPullParser.TEXT: {
					// System.out.printf("%s", parser.getText());
					break;
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

	}
	StringBuffer axmlSb=new StringBuffer();

	private static void processManifest(AXmlResourceParser parser, ApkBean apk)
			throws XmlPullParserException, IOException {
		apk.apkManifest = (AXMLManifest) new AXMLManifest("manifest")
				.parse(parser);
		for (AXMLActivity activity : apk.apkManifest.application.activities) {
			for (AXMLIntentFilter intentFilter : activity.intentFilters) {
				for (AXMLAction action : intentFilter.actions) {
					if (action.name != null
							&& action.name.equals("android.intent.action.MAIN")) {
						for (AXMLCategory category : intentFilter.categories) {
							if (category.name
									.equals("android.intent.category.LAUNCHER"))
								if (apk.mainName == null) {
									apk.mainName = activity.name;
									if (!activity.name.contains(".")
											&& !activity.name.startsWith(".")) {
										apk.mainName = "." + apk.mainName;
									}
								} else
									System.err
											.println("Duplicated MAIN exists "
													+ apk.mainName + "\t"
													+ activity.name);
						}
					}
				}
			}
		}
		apk.versionCode = apk.apkManifest.versionCode;
		apk.versionName = apk.apkManifest.versionName;
		apk.packageName = apk.apkManifest.packageName;

		for (int i = 0; i < apk.apkManifest.permissions.size(); i++) {
			apk.apkPermission.add(apk.apkManifest.permissions.get(i).name);
		}

		/*
		 * int curDepth = 0;
		 * 
		 * apk.apkManifest += "<" + parser.getName() + "\r\n";
		 * elementStack[parser.getDepth()][0] = parser.getName();
		 * 
		 * curDepth = parser.getDepth(); for (int i =
		 * parser.getNamespaceCount(parser.getDepth() - 1); i != parser
		 * .getNamespaceCount(parser.getDepth()); ++i) { apk.apkManifest +=
		 * parser.getNamespaceUri(i); }
		 * 
		 * for (int i = 0; i != parser.getAttributeCount(); ++i) { String prefix
		 * = parser.getAttributePrefix(i); String attributeName =
		 * parser.getAttributeName(i); String attributeValue =
		 * getAttributeValue(parser, i); apk.apkManifest += prefix + "\t" +
		 * attributeName + "\t" + attributeValue + "\r\n"; if (curDepth == 1) {
		 * if (prefix.equals("android:") && attributeName.equals("versionCode"))
		 * { if (attributeValue.startsWith("0x")) { apk.apkVersionCode =
		 * String.valueOf(Integer.valueOf( attributeValue.substring(2),
		 * 16).intValue()); } else apk.apkVersionCode = attributeValue; } if
		 * (prefix.equals("android:") && attributeName.equals("versionName"))
		 * apk.apkVersionName = attributeValue; if (prefix.equals("") &&
		 * attributeName.equals("package")) apk.apkPackageName = attributeValue;
		 * 
		 * } else if (curDepth == 3) { if (prefix.equals("android:") &&
		 * attributeName.equals("name")) elementStack[parser.getDepth()][1] =
		 * attributeValue; } else if (curDepth == 5) { if
		 * (prefix.equals("android:") && attributeName.equals("name") &&
		 * attributeValue.equals("android.intent.action.MAIN")) { if
		 * (apk.apkAppName == null) { apk.apkMainName = elementStack[3][1]; if
		 * (elementStack[3][1].startsWith(".")) apk.apkAppName =
		 * elementStack[3][1].substring(1, elementStack[3][1].length()); } } }
		 * // System.out.println("Prefix: "+getNamespacePrefix(parser //
		 * .getAttributePrefix(i))); //
		 * System.out.println("AttributeName: "+parser.getAttributeName(i)); //
		 * System.out.println("Value: "+getAttributeValue(parser, // i)); //
		 * System
		 * .out.println(curDepth+"\t"+prefix+"\t"+attributeName+"\t"+attributeValue
		 * ); }
		 */
	}

}