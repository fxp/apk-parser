package org.fxp.android.apk.manifest;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Embeddable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

@Embeddable
public abstract class AXMLItem implements Serializable{
	private static final long serialVersionUID = -8729633477783041968L;
	private String elementName;
	boolean parseComplete = true;

	public AXMLItem(String elementName) {
		this.elementName = elementName;
	}

	public AXMLItem parse(AXmlResourceParser parser)
			throws XmlPullParserException, IOException {
		if (elementName == null)
			return null;

		for (int i = 0; i != parser.getAttributeCount(); ++i) {
			parseAttribute(parser.getAttributePrefix(i),
					parser.getAttributeName(i), getAttributeValue(parser, i));
		}

		while (true) {
			int type = parser.next();
			switch (type) {
			case XmlPullParser.START_TAG: {
				parseChildren(parser);
			}
			case XmlPullParser.END_TAG: {
				if (parser.getName().equals(elementName)) {
					return this;
				}
			}
			}
		}
	}

	protected abstract void parseAttribute(String prefix, String attributeName,
			String attributeValue);

	public abstract void parseChildren(AXmlResourceParser parser)
			throws XmlPullParserException, IOException;

	protected static String getAttributeValue(AXmlResourceParser parser,
			int index) {
		int type = parser.getAttributeValueType(index);
		int data = parser.getAttributeValueData(index);
		if (type == TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type == TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type == TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X", data);
		}
		if (type == TypedValue.TYPE_INT_BOOLEAN) {
			return data != 0 ? "true" : "false";
		}
		if (type == TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))
					+ DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type == TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))
					+ FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type >= TypedValue.TYPE_FIRST_COLOR_INT
				&& type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X", data);
		}
		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>", data, type);
	}

	private static String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	// ///////////////////////////////// ILLEGAL STUFF, DONT LOOK :)

	public static float complexToFloat(int complex) {
		return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
	}

	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F,
			1.192093E-007F, 4.656613E-010F };
	private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt",
			"in", "mm", "", "" };
	private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "",
			"", "" };
}
