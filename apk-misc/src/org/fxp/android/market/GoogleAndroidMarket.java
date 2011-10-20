package org.fxp.android.market;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxp.tools.FileUtilsExt;

public class GoogleAndroidMarket {
	private static String REGEX = "http://www.google.com/url([^\"]+)";
	private static String logFile="officialSite.txt";
	
	public static void main(String[] args) throws IOException {
		Pattern p = Pattern.compile(REGEX);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(logFile));

		for (File file : FileUtilsExt.getAllFiles("X:\\apkDownload\\google\\",
				null)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"Unicode"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("访问开发人员网站")) {
					Matcher m = p.matcher(line);
					if (m.find()) {
						System.out.println( m.group(0) );
						out.write(m.group(0)+"\r\n");
					}
				}
			}

		}
	}
}
