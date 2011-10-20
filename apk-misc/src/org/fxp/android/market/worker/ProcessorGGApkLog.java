package org.fxp.android.market.worker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxp.tools.EncodingToolkit;
import org.fxp.tools.FileUtilsExt;

class OnlyExt implements FilenameFilter {
	String ext;

	public OnlyExt(String ext) {
		this.ext = "." + ext;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(ext);
	}
}

public class ProcessorGGApkLog {

	public static String RESPONSE_ITEM_HEAD = "<SEARCH,";
	public static String RESPONSE_ITEM_REGEXP = "(http://[^:]+?\\.(apk|zip|rar))";
	public static BufferedWriter log = null;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: GGApkProcessor res_file");
			return;
		}
		log = new BufferedWriter(new FileWriter(args[0] + ".result"));

		processResponse(new File(args[0]));

	}

	private static Set processResponse(File file) throws IOException {
		// Read response file
		BufferedReader resFile = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "GB2312"));

		// If meet a <SEARCH,*> pattern
		// process a regular expression process
		String strResItem = null;
		String strTmp = null;
		Set<URL> downloadUrls = new HashSet<URL>();
		do {
			strTmp = resFile.readLine();
			if (strTmp != null && strTmp.startsWith(RESPONSE_ITEM_HEAD)) {
				if (strResItem != null) {
					// Process a response
					for (URL url : getUrlFromResponseItem(strResItem)) {
						downloadUrls.add(url);
						log.write(url.toString());
						log.newLine();
						log.flush();
					}
					strResItem = null;
				}
			}
			strResItem += strTmp + "\r\n";
		} while (strTmp != null);
		System.out.println("Found " + downloadUrls.size() + " urls");
		log.close();
		return downloadUrls;
	}

	private static ArrayList<URL> getUrlFromResponseItem(String strResItem) {
		ArrayList<URL> downloadUrls = new ArrayList<URL>();
		Pattern p = Pattern.compile(RESPONSE_ITEM_REGEXP);
		Matcher m = p.matcher(strResItem);
		int count = 0;
		while (m.find()) {
			count++;
			try {
				downloadUrls.add(new URL(m.group(1)));
				if(m.group(1).contains("02.01.07")){
					int i=0;
				}
			} catch (MalformedURLException e) {
				System.out.println("Err phrase url: " + m.group(1));
			}
		}
		System.out.println("Found " + count + " results");
		return downloadUrls;

	}

}
