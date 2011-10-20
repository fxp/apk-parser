package org.fxp.android.market.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.fxp.crawler.worker.toolkits.CrawlStat;
import org.fxp.crawler.worker.toolkits.SimpleWebCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class MarketMyapk {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 4)
			return;
		String dbbase="data/crawl/"+args[0];
		URL base=new URL(args[1]);
		URL baseUrl=new URL(args[2]);
		int workerNum=Integer.valueOf(args[3]);
		
		CrawlController controller = new CrawlController(dbbase);
		controller.addSeed(base.toString());
		SimpleWebCrawler.baseUrls = baseUrl.toString().split(" ");
//		SimpleWebCrawler.myfilter=Pattern.compile("http://.+/web/default/apk/id/.+");
		
		controller.start(SimpleWebCrawler.class, workerNum);

		ArrayList<Object> crawlersLocalData = controller.getCrawlersLocalData();
		long totalLinks = 0;
		long totalTextSize = 0;
		int totalProcessedPages = 0;
		for (Object localData : crawlersLocalData) {
			CrawlStat stat = (CrawlStat) localData;
			totalLinks += stat.getTotalLinks();
			totalTextSize += stat.getTotalTextSize();
			totalProcessedPages += stat.getTotalProcessedPages();
		}
		System.out.println("Aggregated Statistics:");
		System.out.println("   Processed Pages: " + totalProcessedPages);
		System.out.println("   Total Links found: " + totalLinks);
		System.out.println("   Total Text Size: " + totalTextSize);
	}

}
