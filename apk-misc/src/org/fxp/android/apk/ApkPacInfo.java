package org.fxp.android.apk;

import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.fxp.crawler.bean.CertBean;

public class ApkPacInfo {
	/**
	 * @param args
	 * @throws CertificateException
	 * @throws IOException
	 */

	static final String MANIFEST_XML = "AndroidManifest.xml";

	public static void main(String[] args) throws InvalidKeyException,
			CertificateException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException,
			InvalidKeySpecException {

		if (args.length == 1) {
			System.out.println("ARG[0]:" + args[0]);
			ApkBean apkTmp = ApkFileManager.unzipApk(args[0]);
			// System.out.print(apkTmp.apkCert[].toString());
			int certCount = 0;

			if(apkTmp ==null)
				return;
			for (CertBean cert : apkTmp.certs) {
				System.out.println("<Certificate " + certCount++ + ">");
				// System.out.println(cert.toString());
				byte[] pkByte = cert.certificate.getPublicKey().getEncoded();

				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pkByte);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

				try {
					((X509Certificate) cert.certificate).verify(pubKey);
					// Import it into datastore
					// ImportKey.doImport("test.keystore", apkTmp.apkCert);

					System.out.println("Verify success");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Verify failed");
				}
			}
			System.out.println(apkTmp);

		} else if (args.length == 2) {
			System.out.println("ARG[0]:" + args[0]);
			System.out.println("ARG[1]:" + args[1]);
			ApkBean apkTmp1 = ApkFileManager.unzipApk(args[0]);
			ApkBean apkTmp2 = ApkFileManager.unzipApk(args[1]);
			if (apkTmp1.certs.size() == apkTmp2.certs.size()) {
				for (int i = 0; i < apkTmp1.certs.size(); i++) {
					if (!apkTmp1.certs.get(i).equals(apkTmp2.certs.get(i))) {
						System.out.println("Different certificate");
						return;
					}
				}
				System.out.println("Same certificate");
			}
		} else
			System.out.println("Usage: ApkCert file");
	}

/*	public static CodeSigner[] getJarSigner(String apkFileName)
			throws IOException {

		JarFile jf = new JarFile(apkFileName, true);
		Enumeration<JarEntry> entries = jf.entries();
		Vector<JarEntry> entriesVec = new Vector<JarEntry>();

		byte[] buffer = new byte[8192];
		while (entries.hasMoreElements()) {
			JarEntry je = entries.nextElement();
			entriesVec.addElement(je);
			InputStream is = null;
			try {
				is = jf.getInputStream(je);
				while ((is.read(buffer, 0, buffer.length)) != -1) {
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		Enumeration<JarEntry> e = entriesVec.elements();
		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();
			CodeSigner[] signers = je.getCodeSigners();
			if (signers != null) {
				return signers;
			}
		}
		jf.close();
		return null;
	}
*/
/*	
	public static List<Certificate> getJarCerts(String apkFileName)
			throws IOException {

		JarFile jf = new JarFile(apkFileName, true);
		Enumeration<JarEntry> entries = jf.entries();
		Vector<JarEntry> entriesVec = new Vector<JarEntry>();
		List<Certificate> certs = new ArrayList<Certificate>();

		try {
			byte[] buffer = new byte[8192];
			while (entries.hasMoreElements()) {
				JarEntry je = entries.nextElement();
				entriesVec.addElement(je);
				InputStream is = null;

				is = jf.getInputStream(je);
				while ((is.read(buffer, 0, buffer.length)) != -1) {
				}

				if (is != null) {
					is.close();
				}
			}
			Enumeration<JarEntry> e = entriesVec.elements();
			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (jf.getManifest() != null
						&& jf.getManifest().getEntries()
								.containsKey(je.getName())) {
					for (Certificate cert : je.getCertificates()) {
						certs.add(cert);
					}
					return certs;
				}
			}
			jf.close();
		} catch (Exception e) {
			System.err.println(apkFileName);
			e.printStackTrace();
		}
		return certs;
	}
*/
}
