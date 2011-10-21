package com.iw.core.apk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import brut.apktool.Main;

import com.iw.core.common.ApkUtil;
import com.iw.core.common.FileUtil;

public class ApkParser {
	private static final Log log = LogFactory.getLog(ApkParser.class);

	public static String HASH_ALGO = "SHA-512";

	private static final String MANIFEST = "AndroidManifest.xml";
	private static final String RESOURCE_BASEDIR = "res";
	private static final String RESOURCE_VALUEDIR = "values";
	private static final String RESOURCE = "public.xml";

	// To be delete after parsing
	List<String> tmpFiles = new ArrayList<String>();

	private ApkParser() {
	}

	public ApkParser(File apkPath, File decompiledDir) throws ParserException {
		if (!apkPath.exists())
			throw new ParserException(apkPath, "Apk file not exist", null);
		if (decompiledDir.exists())
			throw new ParserException(apkPath,
					"Destination folder has existed", null);
	}

	public static ApkInfo readApk(String apkPath) {
		ApkInfo ret = null;
		String tmpDir = null;
		ApkInfo info = null;
		try {
			tmpDir = FileUtil.getTempFile("apktooltmp_", "");
			if (tmpDir == null)
				throw new ParserException(apkPath, "Cannot get a tmp dir", null);

			if (!ApkUtil.validateApk(apkPath))
				throw new ParserException(apkPath, "Not a valide apk file",
						null);

			info = new ApkInfo();
			info.setPath(apkPath);

			// read basic info
			// Zip entries, certificate
			if (!readBasic(info, tmpDir))
				return null;

			// unpack apk file
			try {
				Main.decodeApk(apkPath, tmpDir);
			} catch (Exception e) {
				throw new ParserException(apkPath,
						"Cannot decompile apk with apktool", e);
			}

			// read resource directory
			if (!readResouse(info, tmpDir))
				return null;

			// read AndroidManifest.xml
			if (!readManifest(info, tmpDir))
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		} finally {
			cleanup(info, tmpDir);
		}
		return ret;
	}

	private static void cleanup(ApkInfo info, String tmpDir) {
		// TODO delete all file under this directory
	}

	private static boolean readBasic(ApkInfo info, String tmpDir)
			throws ParserException {
		boolean ret = false;

		byte[] buf = new byte[2048];
		JarFile jf = null;
		try {
			jf = new JarFile(info.getPath());
			if (jf.getManifest() == null)
				throw new Exception();
			Enumeration<JarEntry> entries = jf.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				info.addEntry(entry.getName(), entry);
				InputStream is = null;
				try {
					is = jf.getInputStream(entry);
					while ((is.read(buf)) != -1)
						;
				} catch (SecurityException se) {
					ret = false;
					break;
				} finally {
					if (is != null)
						is.close();
				}
			}
			// Get all certificates
			Set<String> entryKeys = info.getEntries().keySet();
			for (String entryKey : entryKeys) {
				JarEntry je = info.getEntries().get(entryKey);
				if (jf.getManifest().getEntries().containsKey(entryKey)) {
					Certificate[] cs = je.getCertificates();
					if (cs != null && cs.length > 0) {
						for (Certificate c : cs)
							info.addCert(c);
						break;
					}
				}
			}
			ret = true;
		} catch (Exception e) {
			ret = false;
		} finally {
			if (jf != null)
				try {
					jf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info("Entry count:" + info.getEntries().size());
		return ret;
	}

	private static boolean readResouse(ApkInfo info, String tmpDir) {
		boolean ret = false;
		try {
			String resourcePath = (new File(tmpDir + File.separator
					+ RESOURCE_BASEDIR + File.separator + RESOURCE_VALUEDIR,
					RESOURCE)).getAbsolutePath();

			Document doc = initDoc(resourcePath); 
			ResourceParser.setResource(doc, info);
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	private static Document initDoc(String fileName) {
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(fileName));
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			doc = null;
			e.printStackTrace();
		} catch (SAXException e) {
			doc = null;
			e.printStackTrace();
		} catch (IOException e) {
			doc = null;
			e.printStackTrace();
		}
		return doc;
	}

	private static boolean readManifest(ApkInfo info, String tmpDir) {
		boolean ret = false;
		try {
			String manifestPath = (new File(tmpDir, MANIFEST))
					.getAbsolutePath();
			Document doc = initDoc(manifestPath);
			ret = ManifestParser.parserManifest(doc, info);
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		// unpack apk file
		ApkInfo info = ApkParser
				.readApk("/home/fxp/Downloads/MobeeBook_soho_11.00.30.apk");
		System.out.println(info);

	}

}
