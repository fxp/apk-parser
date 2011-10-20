package org.fxp.android.apk.tag;

import org.fxp.android.apk.processor.InspectorAd;

public class Tagger {
	public static void main(String[] args) {
		InspectorAd.loadAdPatterns();
		InspectorAd.inspectApks(args[0]);
	}
}
