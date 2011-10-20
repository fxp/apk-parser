package org.fxp.android.apk.processor;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;
import org.fxp.tools.FileUtilsExt;

public class MarketDb {
	String dbName = "market.db";
	String basePath;
	String marketName;
	boolean isDebug;

	EntityManagerFactory emf;
	EntityManager em;

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public void init() {
		emf = Persistence.createEntityManagerFactory(basePath
				+ "marketdb/apk.odb");
		em = emf.createEntityManager();
	}

	public MarketDb(String marketName, String basePath, boolean isDebug) {
		super();
		this.marketName = marketName;
		this.isDebug = isDebug;
		if (!basePath.endsWith("\\"))
			this.basePath = basePath + "\\";
		else
			this.basePath = basePath;
	}

	protected int putApk(ApkBean apk) {
		int ret=0;
		if (em.find(ApkBean.class, apk.apkFileChecksum) == null) {
			if (isDebug)
				System.out.println("Persisting apk " + apk.apkLocalPath);
			
			apk.marketBean.marketName = marketName;
			em.persist(apk);
			ret=1;
			for (CertBean cert : apk.getCerts()) {
				if (em.find(CertBean.class, cert.certificateHash) == null) {
					if (isDebug)
						System.out.println("Persisting cert "
								+ cert.certificateHash);
					
					em.persist(cert);
				}
			}
		}
		return ret;
	}

	public int rebuild() {
		if (isDebug)
			System.out.println("Searching apks from " + basePath);

		// Get all apks
		List<ApkBean> apks = ApkFileManager.getAllApk(basePath);

		if (isDebug) {
			for (ApkBean apk : apks)
				System.out.println(apk.apkLocalPath);
			System.out.println("Starting persisting total " + apks.size());
		}

		int count = 0;
		em.getTransaction().begin();
		for (ApkBean apk : apks) {
			count+=putApk(apk);
		}
		em.getTransaction().commit();
		
		for (ApkBean apk : apks) {
			apk=null;
		}		
		apks=null;

		return count;
	}

	public void close() {
		if (isDebug)
			System.out.println("Closing EntityManager");
		em=null;
		emf=null;
		// em.close();
		// emf.close();
	}

	public List<ApkBean> loadAllApk() {
		em.getTransaction().begin();
		TypedQuery<ApkBean> query = em.createQuery("SELECT p FROM ApkBean p",
				ApkBean.class);
		em.getTransaction().commit();
		return query.getResultList();
	}

	public String[] loadAllPackage() {
		em.getTransaction().begin();
		TypedQuery<ApkBean> query = em.createQuery("SELECT p FROM ApkBean p",
				ApkBean.class);
		em.getTransaction().commit();
		HashSet <String> packageTable=new HashSet <String>();
		for(ApkBean apk:query.getResultList()){
			packageTable.add(apk.getPackageName());
		}
		return packageTable.toArray(new String[packageTable.size()]);
	}

	public List<ApkBean> getApkByPackage(ApkBean apk) {
		TypedQuery<ApkBean> query = em.createQuery(
				"SELECT p FROM ApkBean p WHERE p.packageName = :packName",
				ApkBean.class);
		query.setParameter("packName", apk.packageName);
		return query.getResultList();
	}

	public int putApks(List<ApkBean> apks) {
		int count = 0;

		em.getTransaction().begin();
		for (ApkBean apk : apks) {
			count+=putApk(apk);
		}
		em.getTransaction().commit();

		return count;
	}

}
