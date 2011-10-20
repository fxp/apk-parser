package org.fxp.crawler.bean;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public abstract class MarketPageBean {
	public String marketName;
	protected static URL seed;
	protected static String[] baseUrls;
	protected static String linkUrl;
	protected static Pattern customFilter;
	
	protected static Document document;
	protected static Element rootElement;

	abstract public void processPage(Page page);

	public void init(String marketName) throws ParserConfigurationException,
			TransformerFactoryConfigurationError, IOException,
			TransformerException {
		this.marketName = marketName;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		document = documentBuilder.newDocument();
		rootElement = document.createElement("APKS");
		document.appendChild(rootElement);
	}
	
	public void onFinish() throws Exception, TransformerFactoryConfigurationError{
		// Save xml file
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new BufferedWriter(
				new FileWriter(this.marketName + "_crawler_report.xml")));
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
	}

}
