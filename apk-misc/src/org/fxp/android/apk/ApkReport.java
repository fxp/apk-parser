package org.fxp.android.apk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import org.fxp.tools.FileUtilsExt;

// Detail report of a apk file
public class ApkReport {
	Date creationTime;
	Date lastModifyTime = new Date(0);
	static Element rootElement;
	static String reportName=null;

	ArrayList<ZipEntry> zipEntries = new ArrayList<ZipEntry>();

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String AXMLFILE_EXT = "AndroidManifest.xml";

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException {
		// Open zip file
		File[] files = FileUtilsExt.getAllFiles(new File(args[0]), null);
		reportName=args[1];
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		rootElement = document.createElement("APKS");
		document.appendChild(rootElement);
		for (File file : files) {
			ZipFile zipFile = new ZipFile(file.getAbsoluteFile());
			// Process it
			ApkReport apkReport = new ApkReport(zipFile);
			apkReport.generateReport(zipFile, document);
		}
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new BufferedWriter(
				new FileWriter("apk_report.xml")));
		transformer.transform(source, result);
	}

	public ApkReport(ZipFile zipFile) {
		super();
		unZipApk(zipFile);
	}

	private ApkBean unZipApk(ZipFile zipFile) {
		Enumeration enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			try {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				zipEntries.add(zipEntry);
				if (zipEntry.getTime() > lastModifyTime.getTime())
					lastModifyTime = new Date(zipEntry.getTime());
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return null;
	}

	public void generateReport(ZipFile zipFile, Document document) {
		if (zipFile == null)
			return;
		try {
			CreatXMLFile(zipFile, document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void CreatXMLFile(ZipFile zipFile, Document document)
			throws Exception {
		ApkBean apk = ApkFileManager.unzipApk(zipFile.getName());

		Element apkEm = document.createElement("APK");
		apkEm.setAttribute("PATH", zipFile.getName());
		if (apk == null)
			apkEm.setAttribute("STATUS", "UNKNOWN");
		else
			apkEm.setAttribute("STATUS", "HEALTHY");
		apkEm.setAttribute("PACKAGE_NAME", apk.packageName);
		apkEm.setAttribute("VERSION_CODE", String.valueOf(apk.versionCode));
		apkEm.setAttribute("VERSION_NAME", apk.versionName);
		apkEm.setAttribute("MAIN_NAME", apk.mainName);
		apkEm.setAttribute("APP_NAME", apk.marketBean.marketAppName);
		rootElement.appendChild(apkEm);

		for (ZipEntry entry : zipEntries) {
			Element em = document.createElement("ENTRY");
			apkEm.appendChild(em);

			em.setAttribute("NAME", entry.getName());
			em.setAttribute("LASTMOTIFY", sdf.format(new Date(entry.getTime())));
			em.setAttribute("SIZE", String.valueOf(entry.getSize()));
			em.setAttribute("METHOD", String.valueOf(entry.getMethod()));
			em.setAttribute("COMMENT", String.valueOf(entry.getComment()));
		}
		// new ReadApkXML(document);
	}
}
