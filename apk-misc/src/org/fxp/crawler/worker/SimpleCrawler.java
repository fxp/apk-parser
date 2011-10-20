package org.fxp.crawler.worker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.fxp.crawler.worker.toolkits.CrawlStat;
import org.fxp.crawler.worker.toolkits.SimpleWebCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class SimpleCrawler {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 5)
			return;
		String dbbase="data/crawl/"+args[0];
		URL base=new URL(args[1]);
		URL baseUrl=new URL(args[2]);
		String customFilter=args[3];
		int workerNum=Integer.valueOf(args[4]);
		
		CrawlController controller = new CrawlController(dbbase);
		controller.addSeed(base.toString());
//		SimpleWebCrawler.baseUrls = baseUrl.toString().split(" ");
//		SimpleWebCrawler.myfilter=Pattern.compile(customFilter);
		
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
