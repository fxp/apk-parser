package org.fxp.android.apk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.fxp.android.apk.malicious.ScanEngine;
import org.fxp.android.market.api.ApkDAO;
import org.fxp.tools.FileUtilsExt;
import org.fxp.tools.OnlyExt;

public class ApkEverything {
	public static String DB_DEFAULT_NAME = "apkDb.db";
	public String dbFilename = null;
	public String libPath = null;

	// For mysql
	ApkDAO dao;

	public ApkEverything() {
	}

	public void init() {
		// For db4o
		/*
		 * FilenameFilter ff = new OnlyExt("db"); File libFile = new File(".");
		 * File[] dbFiles = libFile.listFiles(ff); if (dbFiles.length == 0) {
		 * System
		 * .out.println("Cannot find database. Create a new one "+DB_DEFAULT_NAME
		 * ); dbFilename=DB_DEFAULT_NAME; }else dbFilename =
		 * dbFiles[0].getAbsolutePath();
		 * 
		 * db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
		 * dbFilename); if (db == null) {
		 * System.out.println("Open database failed"); System.exit(0); }
		 */
		// For mysql
		EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("objectdb://fxp-workstation/apkfile.odb;user=admin;password=admin");
        EntityManager em = emf.createEntityManager();
        
//		dao = ApkDAO.GetInstance();

	}

	public void shutdown() {
		// For db4o
//		db.commit();
//		db.close();
	}

	public ApkBean[] reloadDB(String libPath) {
		System.out.println("Refreshing database");
		this.libPath = libPath;

		init();
		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		OnlyExt onlyExt = new OnlyExt("apk");
		File[] files = FileUtilsExt
				.getAllFiles(new File(this.libPath), onlyExt);
		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
				System.out.println("FailedRead " + file.getAbsolutePath());
				continue;
			}
			/*
			 * ObjectSet<ApkBean> result = db.queryByExample(apk); if
			 * (result.size() == 0 && apk != null) { db.store(apk); db.commit();
			 * apks.add(apk); System.out.println("Storing " + file.getName());
			 * }else System.out.println("Exist "+file.getName());
			 */
			dao.insertCert(apk);
		}
		shutdown();
		return apks.toArray(new ApkBean[apks.size()]);
	}
/*
	public void cleanDB() {
		init();
		System.out.println("WARRNING. it will clean up all data in database");
		ApkBean apk = new ApkBean();
		ObjectSet<ApkBean> result = db.queryByExample(apk);
		for (Object o : result) {
			db.delete(o);
		}
		shutdown();
	}

	private boolean multiContains(String keyword, String dstString) {
		String[] keywords = keyword.split("\\s+");
		boolean ret = true;
		for (String key : keywords) {
			ret = ret && dstString.toLowerCase().contains(key.toLowerCase());
		}
		return ret;
	}

	public ApkBean[] searchByName(final String keyword) {
		System.out.println("Search by app name: " + keyword);

		init();
		List<ApkBean> apkRet = db.query(new Predicate<ApkBean>() {
			private static final long serialVersionUID = -7407859620045751219L;

			@Override
			public boolean match(ApkBean apk) {
				return multiContains(keyword, apk.marketBean.marketAppName);
			}
		});
		ApkBean[] apks = apkRet.toArray(new ApkBean[apkRet.size()]);
		shutdown();
		return apks;
	}

	public ApkBean[] searchByPackage(final String keyword) {
		System.out.println("Search by package name: " + keyword);

		init();
		List<ApkBean> apkRet = db.query(new Predicate<ApkBean>() {
			private static final long serialVersionUID = -3474466779692799981L;

			@Override
			public boolean match(ApkBean apk) {
				return multiContains(keyword, apk.packageName);
			}
		});
		ApkBean[] apks = apkRet.toArray(new ApkBean[apkRet.size()]);
		shutdown();
		return apks;
	}

	public ApkBean[] searchByCertIssuer(final String keyword) {
		System.out.println("Search by certification keyword: " + keyword);

		init();
		List<ApkBean> apkRet = db.query(new Predicate<ApkBean>() {
			private static final long serialVersionUID = 7654998843074605522L;

			@Override
			public boolean match(ApkBean apk) {
				// if (apk.apkCert instanceof X509Certificate) {
				// return multiContains(keyword,((X509Certificate)
				// apk.apkCert).getIssuerX500Principal().toString());
				// }
				return false;
			}
		});
		ApkBean[] apks = apkRet.toArray(new ApkBean[apkRet.size()]);
		shutdown();
		return apks;
	}

	public ApkBean[] checkApks(String dstPath) {
		this.libPath = dstPath;

		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		File[] files;
		if (new File(dstPath).isFile()) {
			files = new File[1];
			files[0]=new File(dstPath);
			System.out.println("Checking apk");
		} else {
			OnlyExt onlyExt = new OnlyExt("apk");
			files = FileUtilsExt.getAllFiles(new File(this.libPath),
					onlyExt);
			System.out.println("Checking apks");
		}
		for (File file : files) {
			ApkBean apk = ApkFileManager.unzipApk(file.getAbsolutePath());
			if (apk == null) {
				System.out.println("FailedRead " + file.getAbsolutePath());
				continue;
			}
			apks.add(apk);
		}
		return apks.toArray(new ApkBean[apks.size()]);
	}
	
	public void scanApk(String dstPath){
		this.libPath = dstPath;
		ScanEngine engine=new ScanEngine();
		engine.init();

		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		File[] files;
		if (new File(dstPath).isFile()) {
			files = new File[1];
			files[0]=new File(dstPath);
			System.out.println("Checking apk");
		} else {
			OnlyExt onlyExt = new OnlyExt("apk");
			files = FileUtilsExt.getAllFiles(new File(this.libPath),
					onlyExt);
			System.out.println("Checking apks");
		}
		for (File file : files) {
			System.out.println("Scanning "+file.getAbsolutePath());
			engine.scan(file.getAbsolutePath());
		}
		return ;
	}*/
	
	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		ApkEverything apkSearch = new ApkEverything();
		if (args.length == 1) {
			if (args[0].equals("-c"))
				apkSearch.cleanDB();
		} else if (args.length == 2) {
			ApkBean[] apks = null;
			if (args[0].equals("-build"))
				apks = apkSearch.reloadDB(args[1]);
			else if (args[0].equals("-info"))
				apks = apkSearch.checkApks(args[1]);
			else if (args[0].equals("-scan"))
				apkSearch.scanApk(args[1]);
			else if (args[0].equals("-name"))
				apks = apkSearch.searchByName(args[1]);
			else if (args[0].equals("-package"))
				apks = apkSearch.searchByPackage(args[1]);
			else if (args[0].equals("-cert"))
				apks = apkSearch.searchByCertIssuer(args[1]);
			else {
				printUsage();
				return;
			}

			if (apks != null){
				for (ApkBean apk : apks) {
					System.out.println(apk.toString());
				}
				System.out.println("Total result " + apks.length);
			}
		} else {
			printUsage();
		}
	}
*/
	public static void printUsage() {
		System.out.println("Usage: apk_search");
		System.out.println("Clean up database:\t-c");
		System.out.println("Refresh(add new apks):\t-r [apk directory path]");
		System.out.println("Search by name:\t-n [keyword]");
		System.out.println("Search by package name:\t-p [keyword]");
		System.out.println("Search by certIssuer:\t-cert [keyword]");
		System.out
				.println("[keyword] can be multiple keywords string, but must included with \"\"");
		System.out.println("e.g. Build up database: apk_search -r d:\\apk");
		System.out
				.println("e.g. Search by name: apk_search -n \"android yingyonghui\"");
	}
}
