package org.fxp.android.apk.tester;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;
import org.fxp.crawler.bean.MarketBean;
import org.fxp.tools.FileUtilsExt;

public class ApkEditorDAO {
	static EntityManager em;
	static EntityManagerFactory emf;

	public static void init() {
		emf = Persistence
				.createEntityManagerFactory("objectdb://10.18.135.100/apk.odb;user=admin;password=admin");
		em = emf.createEntityManager();
	}

	public static void putCert(CertBean cert) {
		em.getTransaction().begin();
		em.persist(cert);
		em.getTransaction().commit();
	}

	public static CertBean getCert(CertBean cert) {
		return null;
	}

	public static void putApk(ApkBean apk) {
		em.getTransaction().begin();
		// em.persist(new testEn());
		ApkBean apkTmp = new ApkBean();
		apkTmp.apkFileChecksum = apk.apkFileChecksum;
		if (em.find(ApkBean.class, apk.apkFileChecksum) == null) {
			em.persist(apk);
		}

		CertBean certTmp = new CertBean();
		for (CertBean cert : apk.certs) {
			certTmp.certificateHash = cert.certificateHash;
			CertBean certFound = em.find(CertBean.class,
					certTmp.certificateHash);
			if (certFound == null) {
				em.persist(cert);
			} else {
				certFound.setNote(cert.note);
				certFound.setDevName(cert.devName);
				certFound.setOfficialSite(cert.officialSite);
				certFound.setVerifyStatus(cert.verifyStatus);
			}
		}
		em.getTransaction().commit();
	}

	public static ApkBean getApk(ApkBean apk) {
		em.getTransaction().begin();

		ApkBean apkTmp = new ApkBean();
		apkTmp.apkFileChecksum = apk.apkFileChecksum;
		ApkBean ret = em.find(ApkBean.class, apk.apkFileChecksum);
		if (ret == null)
			em.persist(apk);

		CertBean certTmp = new CertBean();
		for (CertBean cert : apk.certs) {
			certTmp.certificateHash = cert.certificateHash;
			CertBean certFound = em.find(CertBean.class,
					certTmp.certificateHash);
			if (certFound != null) {
				cert.note = certFound.note;
				cert.devName = certFound.devName;
				cert.officialSite = certFound.officialSite;
				cert.verifyStatus = certFound.verifyStatus;
			}
		}

/*		else if (apk.getCerts().size() != ret.getCerts().size())
			ret = null;
		else {
			for (int i = 0; i < ret.getCerts().size(); i++) {
				apk.getCerts().get(i).setNote(ret.getCerts().get(i).getNote());
				apk.getCerts().get(i)
						.setDevName(ret.getCerts().get(i).getDevName());
				apk.getCerts()
						.get(i)
						.setOfficialSite(
								ret.getCerts().get(i).getOfficialSite());
			}
		}*/
		em.getTransaction().commit();
		return ret;
	}

	public static void close() {
		em.close();
		emf.close();
	}

	public static void main(String[] args) {
		ApkEditorDAO.init();
		File[] files = FileUtilsExt.getAllFiles((new File(
				"E:\\apk_yingyonghui\\marketapps\\apps.c")), null);
		for (File file : files) {
			try {
				ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
				if (apk == null)
					continue;

				ApkManDao dao = ApkManDao.GetInstance();
				// Put apk into database
				dao.fillApk(apk);

				ApkEditorDAO.putApk(apk);
				System.out.println("Put apk " + apk.apkLocalPath);
				/*
				 * s for (CertBean cert : apk.certs) {
				 * ApkEditorDAO.putCert(cert); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ApkEditorDAO.close();
	}
}
