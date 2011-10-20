package org.fxp.crawler.worker.toolkits;

public class CrawlStat {
	private int totalProcessedPages;
	private long totalLinks;
	private long totalTextSize;
	private String pageText;
	
	public int getTotalProcessedPages() {
		return totalProcessedPages;
	}

	public void setTotalProcessedPages(int totalProcessedPages) {
		this.totalProcessedPages = totalProcessedPages;
	}
	
	public void incProcessedPages() {
		this.totalProcessedPages++;
	}

	public long getTotalLinks() {
		return totalLinks;
	}

	public void setTotalLinks(long totalLinks) {
		this.totalLinks = totalLinks;
	}

	public long getTotalTextSize() {
		return totalTextSize;
	}

	public void setTotalTextSize(long totalTextSize) {
		this.totalTextSize = totalTextSize;
	}
	
	public void incTotalLinks(int count) {
		this.totalLinks += count;
	}
	
	public void incTotalTextSize(int count) {
		this.totalTextSize += count;
	}

	public void setPageText(String pageText) {
		this.pageText = pageText;
	}

	public String getPageText() {
		return pageText;
	}

}