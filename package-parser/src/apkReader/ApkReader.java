package apkReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.res.AXMLPrinter;

public class ApkReader {
	private static final Logger log = Logger.getLogger("APKReader");

	static final int BUFFER = 2048;

	private static final int VER_ID = 0;
	private static final int ICN_ID = 1;
	private static final int LABEL_ID = 2;
	String[] VER_ICN = new String[3];

	static String TMP_PREFIX = "apktemp_";

	// Some possible tags and attributes
	String[] TAGS = { "manifest", "application", "activity" };
	String[] ATTRS = { "android:", "a:", "activity:", "_:" };

	JarFile apkJar = null;
	Hashtable<String, JarEntry> entryList = new Hashtable<String, JarEntry>();

	List<String> tmpFiles = new ArrayList<String>();

	public String fuzzFindInDocument(Document doc, String tag, String attr) {
		for (String t : TAGS) {
			NodeList nodelist = doc.getElementsByTagName(t);
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node element = (Node) nodelist.item(i);
				if (element.getNodeType() == Document.ELEMENT_NODE) {
					NamedNodeMap map = element.getAttributes();
					for (int j = 0; j < map.getLength(); j++) {
						Node element2 = map.item(j);
						if (element2.getNodeName().endsWith(attr)) {
							return element2.getNodeValue();
						}
					}
				}
			}
		}
		return null;
	}

	private Document initDoc(String fileName) {
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

	private void extractPermissions(ApkInfo info, Document doc) {
		ExtractPermission(info, doc, "uses-permission", "android:name");
		ExtractPermission(info, doc, "permission-group", "android:name");
		ExtractPermission(info, doc, "service", "android:permission");
		ExtractPermission(info, doc, "provider", "android:permission");
		ExtractPermission(info, doc, "activity", "android:permission");
	}

	private boolean readBoolean(Document doc, String tag, String attribute) {
		String str = FindInDocument(doc, tag, attribute);
		boolean ret = false;
		try {
			ret = Boolean.valueOf(str);
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	private void extractSupportScreens(ApkInfo info, Document doc) {
		info.supportSmallScreens = readBoolean(doc, "supports-screens",
				"android:smallScreens");
		info.supportNormalScreens = readBoolean(doc, "supports-screens",
				"android:normalScreens");
		info.supportLargeScreens = readBoolean(doc, "supports-screens",
				"android:largeScreens");

		if (info.supportSmallScreens || info.supportNormalScreens
				|| info.supportLargeScreens)
			info.supportAnyDensity = false;
	}

	public ApkInfo extractInfo(String fileName, ApkInfo info) throws Exception {
		VER_ICN[VER_ID] = "";
		VER_ICN[ICN_ID] = "";
		VER_ICN[LABEL_ID] = "";
		try {
			Document doc = initDoc(fileName);
			if (doc == null)
				throw new Exception("Document initialize failed");

			// Fill up the permission field
			extractPermissions(info, doc);

			// Fill up some basic fields
			info.minSdkVersion = FindInDocument(doc, "uses-sdk",
					"android:minSdkVersion");
			info.targetSdkVersion = FindInDocument(doc, "uses-sdk",
					"android:targetSdkVersion");
			info.versionCode = FindInDocument(doc, "manifest",
					"android:versionCode");
			info.versionName = FindInDocument(doc, "manifest",
					"android:versionName");
			info.packageName = FindInDocument(doc, "manifest", "package");
			info.label = FindInDocument(doc, "application", "android:label");
			if (info.label.startsWith("@"))
				VER_ICN[LABEL_ID] = info.label;

			// Fill up the support screen field
			extractSupportScreens(info, doc);

			if (info.versionCode == null)
				info.versionCode = fuzzFindInDocument(doc, "manifest",
						"versionCode");

			if (info.versionName == null)
				info.versionName = fuzzFindInDocument(doc, "manifest",
						"versionName");
			else if (info.versionName.startsWith("@"))
				VER_ICN[VER_ID] = info.versionName;

			String id = FindInDocument(doc, "application", "android:icon");
			if (null == id) {
				id = fuzzFindInDocument(doc, "manifest", "icon");
			}

			if (null == id) {
				System.err.println("icon resId Not Found!");
				return info;
			}

			// Find real strings
			if (!info.hasIcon && id != null) {
				if (id.startsWith("@android:"))
					VER_ICN[ICN_ID] = "@"
							+ (id.substring("@android:".length()));
				else
					VER_ICN[ICN_ID] = id;

				ArrayList<String> resId = new ArrayList<String>();

				for (int i = 0; i < VER_ICN.length; i++) {
					if (VER_ICN[i].startsWith("@"))
						resId.add(VER_ICN[i]);
				}

				ResourceFinder finder = new ResourceFinder();

				ByteBuffer bb = ByteBuffer.wrap(info.manifestFileBytes);
				bb.order(ByteOrder.LITTLE_ENDIAN);

				info.resStrings = finder.processResourceTable(bb, resId);

				if (!VER_ICN[VER_ID].equals("")) {
					ArrayList<String> versions = info.resStrings
							.get(VER_ICN[VER_ID].toUpperCase());
					if (versions != null) {
						if (versions.size() > 0)
							info.versionName = versions.get(0);
					} else {
						throw new Exception(
								"VersionName Cant Find in resource with id "
										+ VER_ICN[VER_ID]);
					}
				}

				ArrayList<String> iconPaths = info.resStrings
						.get(VER_ICN[ICN_ID].toUpperCase());
				if (iconPaths != null && iconPaths.size() > 0) {
					info.iconFileNameToGet = new ArrayList<String>();
					for (String iconFileName : iconPaths) {
						if (iconFileName != null
								&& entryList.containsKey(iconFileName)) {
							info.iconFileNameToGet.add(iconFileName);
							info.hasIcon = true;
							break;
						}
					}
				} else {
					throw new Exception("Icon Cant Find in resource with id "
							+ VER_ICN[ICN_ID]);
				}

				if (!VER_ICN[LABEL_ID].equals("")) {
					List<String> labels = info.resStrings.get(VER_ICN[LABEL_ID]);
					if (labels.size() > 0) {
						info.label = labels.get(0);
					}
				}
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error in APKReader", e);
			throw e;
		}
		return info;
	}

	private void ExtractPermission(ApkInfo info, Document doc, String keyName,
			String attribName) {
		NodeList usesPermissions = doc.getElementsByTagName(keyName);
		if (usesPermissions != null) {
			for (int s = 0; s < usesPermissions.getLength(); s++) {
				Node permissionNode = usesPermissions.item(s);
				if (permissionNode.getNodeType() == Node.ELEMENT_NODE) {
					Node node = permissionNode.getAttributes().getNamedItem(
							attribName);
					if (node != null)
						info.Permissions.add(node.getNodeValue());
				}
			}
		}
	}

	private String FindInDocument(Document doc, String keyName,
			String attribName) {
		NodeList usesPermissions = doc.getElementsByTagName(keyName);

		if (usesPermissions != null) {
			for (int s = 0; s < usesPermissions.getLength(); s++) {
				Node permissionNode = usesPermissions.item(s);
				if (permissionNode.getNodeType() == Node.ELEMENT_NODE) {
					Node node = permissionNode.getAttributes().getNamedItem(
							attribName);
					if (node != null)
						return node.getNodeValue();
				}
			}
		}
		return null;
	}

	private String normalizeXml(String rawXMl) {
		String xml = "";
		/* Dealing with ugly content */
		for (String line : rawXMl.split("\n")) {
			/* Deal with invalid character & */
			line = line.replace("&", "&amp;");
			/* Deal with android:versionName="1.0.3.7-969a */
			line = line.replace((char) 0, ' ');
			/* Deal with versionName="0.1.8 "Archer"" */
			int charCount = line.replaceAll("[^\"]", "").length();

			if (charCount > 2 && !line.contains("xml version")
					&& line.endsWith("\"")) {
				Pattern p = Pattern.compile("(.+[\\w:=]+)\\\"(.+)\\\"");
				Matcher m = p.matcher(line);
				if (m.find()) {
					line = m.group(1) + '"' + m.group(2).replace('"', '\'')
							+ '"';
				}
			}
			xml += line + "\n";
		}
		return xml;
	}

	static Pattern p = Pattern.compile(".+\\.xml");

	private int extractFiles(String apkPath, ApkInfo info) {
		int errorCode = ApkInfo.BAD_READ_INFO;
		try {
			info.fileHash = Hash.getFileHash(apkPath);

			info.manifestFileName = null;
			String packedXMLFile = getTempFile("apktemp_", ".xml");
			String unzippedXMLFile = getTempFile("apktemp_", ".xml");
			String rawXMl;
			String xml = "";

			extractFile("AndroidManifest.xml", packedXMLFile);

			// extract html
			rawXMl = AXMLPrinter.getString(packedXMLFile);
			info.rawAndroidManifest = rawXMl;
			FileWriter xmlFile = new FileWriter(new File(unzippedXMLFile), true);

			xml = normalizeXml(rawXMl);

			xmlFile.write(xml);
			xmlFile.flush();
			xmlFile.close();
			// log.log(Level.INFO, "Success extract AndroidManifest.xml,"
			// + apkPath + "," + unzippedXMLFile);
			info.manifestFileName = unzippedXMLFile;

			if (info.manifestFileName == null)
				return ApkInfo.NULL_MANIFEST;

			info.manifestFileBytes = extractBytes("resources.arsc");
			extractInfo(unzippedXMLFile, info);
			// Extract icon file
			info.iconFileName = new ArrayList<String>();
			info.iconHash = new ArrayList<String>();
			for (String iconName : info.iconFileNameToGet)
				info.iconFileName.add(getTempFile(
						"apktemp_" + iconName.hashCode() + "_", ".png"));
			extractFile(info.iconFileNameToGet, info.iconFileName);

			for (String icon : info.iconFileName)
				info.iconHash.add(Hash.getFileHash(icon));

			// Extract xml
			info.layoutStrings = extractXmls(p);

			errorCode = info.isValid();
		} catch (Exception e) {
			e.printStackTrace();
			errorCode = ApkInfo.BAD_READ_INFO;
		}

		return errorCode;
	}

	private byte[] extractBytes(String nameInPackage)
			throws FileNotFoundException, IOException {
		int count = 0;
		byte data[] = new byte[BUFFER];

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JarEntry entry = (JarEntry) apkJar.getEntry(nameInPackage);
		InputStream is = apkJar.getInputStream(entry);
		while ((count = is.read(data, 0, BUFFER)) != -1) {
			output.write(data, 0, count);
		}
		return output.toByteArray();
	}

	private int extractFile(List<String> nameInPackage, List<String> destFile)
			throws FileNotFoundException, IOException {
		for (int i = 0; i < nameInPackage.size(); i++) {
			JarEntry entry = (JarEntry) apkJar.getEntry(nameInPackage.get(i));
			expandEntry(apkJar.getInputStream(entry), destFile.get(i));
		}
		return nameInPackage.size();
	}

	private Map<String, String> extractXmls(Pattern p) {
		if (p == null)
			return null;
		Hashtable<String, String> ret = new Hashtable<String, String>();
		Enumeration<JarEntry> entries = apkJar.entries();
		try {
			List<String> filesToExtract = new ArrayList<String>();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (p.matcher(entry.getName()).matches()
						&& !entry.getName().equals("AndroidManifest.xml")) {
					filesToExtract.add(entry.getName());
				}
			}
			for (String fileToExtract : filesToExtract) {
				String tmpFile = getTempFile(TMP_PREFIX, "."
						+ p.pattern().hashCode());
				extractFile(fileToExtract, tmpFile);
				// if()
				String content;
				try {
					// DocumentBuilder docBuilder;
					// Document doc = null;
					// DocumentBuilderFactory docBuilderFactory =
					// DocumentBuilderFactory
					// .newInstance();
					// docBuilder = docBuilderFactory.newDocumentBuilder();
					// doc = docBuilder.parse(new File(tmpFile));
					// doc.getDocumentElement().normalize();
					// content = doc.getTextContent();
					//
					// if(content==null){
					FileReader fr = new FileReader(tmpFile);
					BufferedReader reader = new BufferedReader(fr);
					content = "";
					String line;
					content = reader.readLine();
					if (!content.startsWith("<"))
						throw new Exception();
					while ((line = reader.readLine()) != null)
						content += line + "\r\n";
					fr.close();
					reader.close();
					// }
				} catch (Exception e) {
					content = AXMLPrinter.getString(tmpFile);
				}

				if (content == null)
					System.err.println("Error Reading:" + fileToExtract);
				else
					ret.put(fileToExtract, content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private int extractFile(String nameInPackage, String destFile)
			throws FileNotFoundException, IOException {
		JarEntry entry = (JarEntry) apkJar.getEntry(nameInPackage);
		return expandEntry(apkJar.getInputStream(entry), destFile);
	}

	protected int expandEntry(InputStream input, String name)
			throws IOException {
		int count = 0;
		File file = new File(name);
		BufferedOutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(file));
			byte buffer[] = new byte[2048];
			while (true) {
				int n = input.read(buffer);
				if (n <= 0)
					break;
				output.write(buffer, 0, n);
				count += n;
			}
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			input.close();
		}
		return count;
	}

	private String getTempFile(String prefix, String postfix)
			throws IOException, IllegalArgumentException, SecurityException {
		File tmpFile = File.createTempFile(prefix, postfix);
		tmpFiles.add(tmpFile.getAbsolutePath());
		tmpFile.delete();
		return tmpFile.getPath();
	}

	public int verifyJar(String apkPath) {
		int ret = ApkInfo.FINE;
		JarFile apkJar = null;
		try {
			apkJar = new JarFile(apkPath, true);
			if (apkJar.getJarEntry("AndroidManifest.xml") == null)
				ret = ApkInfo.NULL_MANIFEST;
			else if (apkJar.getJarEntry("resources.arsc") == null)
				ret = ApkInfo.NULL_RESOURCES;
			else if (apkJar.getJarEntry("classes.dex") == null)
				ret = ApkInfo.NULL_DEX;
			else if (apkJar.getJarEntry("META-INF/MANIFEST.MF") == null)
				ret = ApkInfo.NULL_METAINFO;
		} catch (Exception e) {
			ret = ApkInfo.BAD_JAR;
			// e.printStackTrace();
		} finally {
			if (apkJar != null) {
				try {
					apkJar.close();
				} catch (IOException e) {
					log.log(Level.WARNING, e.getCause().getMessage());
					e.printStackTrace();
				}
				apkJar = null;
			}
		}
		return ret;
	}

	private int parseJar(JarFile jar) throws IOException {
		int ret = ApkInfo.FINE;

		int entryCount = 0;
		byte[] buf = new byte[2048];
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			entryList.put(entry.getName(), entry);
			entryCount++;
			InputStream is = null;
			try {
				is = jar.getInputStream(entry);
				while ((is.read(buf)) != -1)
					;
			} catch (SecurityException se) {
				ret = ApkInfo.BAD_JAR;
				break;
			} finally {
				if (is != null)
					is.close();
			}
		}
		log.log(Level.FINE, "Entry count:" + entryCount);
		return ret;
	}

	public int read(String apkPath, ApkInfo info) {
		// Verify a valid jar file
		int errCode = verifyJar(apkPath);
		if (!(errCode == ApkInfo.FINE))
			return errCode;

		try {
			apkJar = new JarFile(apkPath);
			if ((errCode = parseJar(apkJar)) != ApkInfo.FINE)
				return errCode;
			info.entryList = entryList;
			// Extract all file needed
			if (info == null)
				info = new ApkInfo();
			errCode = extractFiles(apkPath, info);
			apkJar.close();
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			cleanup();
		}
		// System.out.println(info);
		return errCode;
	}

	private void cleanup() {
		for (String tmpFile : tmpFiles) {
			if (!(new File(tmpFile)).delete()) {
				(new File(tmpFile)).deleteOnExit();
				// log.info("Delete failed:" + tmpFile);
			}
		}
	}
}
