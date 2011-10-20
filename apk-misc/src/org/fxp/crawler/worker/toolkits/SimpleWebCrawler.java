package org.fxp.crawler.worker.toolkits;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.fxp.crawler.bean.MarketPageBean;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class SimpleWebCrawler extends WebCrawler {
	Pattern fileFilters = Pattern.compile(".*(\\.(bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|apk|rar|gz))$");

	CrawlStat myCrawlStat;
	public static MarketPageBean marketPager;
	public static String[] baseUrls;

	public SimpleWebCrawler() {
		myCrawlStat = new CrawlStat();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();

		if (fileFilters.matcher(href).matches()) {
//			System.out.println("<FILE " + url.getURL() + ">");
			return false;
		}

		for (String filterUrl : baseUrls) {
			if (href.startsWith(filterUrl)) {
				return true;
			}
		}
		return false;
	}

	public void visit(Page page) {
		myCrawlStat.incProcessedPages();
		ArrayList<WebURL> links = page.getURLs();
		myCrawlStat.incTotalLinks(links.size());
		try {
			myCrawlStat
					.incTotalTextSize(page.getText().getBytes("UTF-8").length);
			
			marketPager.processPage(page);
			
			page.getWebURL();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// We dump this crawler statistics after processing every 50 pages
		if (myCrawlStat.getTotalProcessedPages() % 10 == 0) {
		 dumpMyData();
		 }
	}

	// This function is called by controller to get the local data of this
	// crawler when job is finished
	public Object getMyLocalData() {
		return myCrawlStat;
	}

	// This function is called by controller before finishing the job.
	// You can put whatever stuff you need here.
	public void onBeforeExit() {
		dumpMyData();
	}

	public void dumpMyData() {
		try {
			marketPager.onFinish();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// This is just an example. Therefore I print on screen. You may
		// probably want to write in a text file.
		// System.out.println("Crawler " + myId + "> Processed Pages: " +
		// myCrawlStat.getTotalProcessedPages());
		// System.out.println("Crawler " + myId + "> Total Links Found: " +
		// myCrawlStat.getTotalLinks());
		// System.out.println("Crawler " + myId + "> Total Text Size: " +
		// myCrawlStat.getTotalTextSize());
	}
}
