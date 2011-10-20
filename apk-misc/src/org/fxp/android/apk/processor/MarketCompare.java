package org.fxp.android.apk.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

public class MarketCompare extends MarketDb {

	public MarketCompare(String marketName, String basePath, boolean isDebug) {
		super(marketName, basePath, isDebug);
	}

	private static String baseDir;
	private static String MARKET_RANK_FILE = "marketrank.report.csv";
	private static String PACKAGE_REPORT_FILE = "package.report.csv";

	public static void main(String[] args) throws IOException {
		MarketCompare.setBase("X:\\apkDownload");
//		MarketCompare.setBase(args[0]);

		List<String> marketNames = new ArrayList<String>();
/*		marketNames.add("misc");
				marketNames.add("3g");
		marketNames.add("coolapk");
		marketNames.add("d");
				marketNames.add("eoemarket");
		marketNames.add("fengbao");
				marketNames.add("hiapk");
		marketNames.add("imiyoo");
*/		marketNames.add("aimi8");
/*				marketNames.add("goapk");
		marketNames.add("nduoa");
		marketNames.add("yingyonghui");
		marketNames.add("mumayi");
				marketNames.add("gfan");
		marketNames.add("alcatelclub.cn");
		marketNames.add("anzhi.us");
		marketNames.add("apkcn.com");
		marketNames.add("androiddownloadz");
		marketNames.add("chomikuj");
		marketNames.add("freewarelovers");
		marketNames.add("myapk.cn");

*/		reloadAll(marketNames);
		 loadAll(marketNames);
		 //mergeMarketDb(marketNames);
	}

	public static void setBase(String base) {
		if (base.endsWith("\\"))
			baseDir = base;
		else
			baseDir = base + "\\";
	}

	public static void loadAll(List<String> marketNames) {
		for (String marketName : marketNames)
			loadMarket(marketName, true);
	}

	public static MarketDb loadMarket(String marketName, boolean isDebug) {
		MarketDb marketDb = new MarketDb(marketName, baseDir + marketName,
				isDebug);
		marketDb.init();
		List<ApkBean> apks = marketDb.loadAllApk();
		String[] packages = marketDb.loadAllPackage();
		if (packages != null)
			System.out.print(marketName + "," + packages.length);
		/*
		 * for (ApkBean apk : apks) { List<ApkBean> rets =
		 * marketDb.getApkByPackage(apk); //
		 * System.out.println(apk.packageName+" count "+rets.size());
		 * System.out.println(apk.packageName + "," + apk.apkFileChecksum); }
		 */
		System.out.println("," + apks.size());

		return marketDb;
	}

	public static void reloadAll(List<String> marketNames) {
		for (String marketName : marketNames)
			rebuildMarket(marketName, true);
	}

	public static void rebuildMarket(String marketName, boolean isDebug) {
		MarketDb market = new MarketDb(marketName, baseDir + marketName,
				isDebug);
		market.init();
		int count = market.rebuild();
		market.close();

		System.out.println("New apks from " + marketName + " total " + count);
	}

	public static void mergeMarketDb(List<String> marketNames) {
		List<MarketDb> marketDbs = new ArrayList<MarketDb>();
		for (String marketName : marketNames) {
			MarketDb market = new MarketDb(marketName, baseDir + marketName,
					true);
			market.init();
			marketDbs.add(market);
		}
		try {
			mergeMarketDb(marketDbs.toArray(new MarketDb[marketDbs.size()]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void mergeMarketDb(MarketDb[] markets) throws IOException {
		Hashtable<String, PackageReportEntry> packageReports = new Hashtable<String, PackageReportEntry>();

		for (MarketDb market : markets) {
			List<ApkBean> apks = market.loadAllApk();
			for (ApkBean apk : apks) {
				if (apk.getPackageName() == null)
					continue;

				PackageReportEntry packageReport = packageReports.get(apk
						.getPackageName());
				if (packageReport == null) {
					packageReports.put(apk.getPackageName(),
							new PackageReportEntry(apk));
					// System.out.println("New package name "
					// + apk.getPackageName());
				} else {
					packageReport.getApks().add(apk);
					// System.out.println("Same package name "
					// + apk.getPackageName());
				}
			}
			market.close();
		}
		generateMarketRank(packageReports, markets);
		generateMergeReport(packageReports, markets);
	}

	public static void generateMarketRank(
			Hashtable<String, PackageReportEntry> packageReports,
			MarketDb[] markets) throws IOException {
		FileWriter fstream = new FileWriter(baseDir + MARKET_RANK_FILE);
		BufferedWriter out = new BufferedWriter(fstream);

		int maxScore = markets.length;
		Hashtable<String, Integer> scores = new Hashtable<String, Integer>();
		for (MarketDb market : markets)
			scores.put(market.getMarketName(), 0);

		String str;
		Set<String> set = packageReports.keySet();
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();

			// Calculate scores
			boolean isCollision = false;
			String fileHash = null;
			Collections.sort(packageReports.get(str).getApks());

			int versionCodeTmp = packageReports.get(str).getApks().get(0)
					.getVersionCode();
			int curScore = maxScore;
			for (ApkBean apk : packageReports.get(str).getApks()) {
				if (apk.getVersionCode() < versionCodeTmp)
					curScore--;
				scores.put(
						apk.getMarketBean().getMarketName(),
						Integer.valueOf(scores.get(apk.getMarketBean()
								.getMarketName())) + curScore);
			}
		}
		out.write("\r\n");
		for (MarketDb market : markets)
			out.write(market.getMarketName() + ","
					+ scores.get(market.getMarketName()) + "\r\n");
		out.close();
	}

	public static void generateMergeReport(
			Hashtable<String, PackageReportEntry> packageReports,
			MarketDb[] markets) throws IOException {
		FileWriter fstream = new FileWriter(baseDir + PACKAGE_REPORT_FILE);
		BufferedWriter out = new BufferedWriter(fstream);

		int maxScore = markets.length;
		Hashtable<String, Boolean> isExist = new Hashtable<String, Boolean>();
		for (MarketDb market : markets)
			isExist.put(market.getMarketName(), false);

		String str;
		Set<String> set = packageReports.keySet();
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();
			out.write(str);

			String fileHash = null;
			boolean isCollision = false;

			// Write detail
			for (MarketDb market : markets) {
				ApkBean latestApk = null;
				for (ApkBean apk : packageReports.get(str).getApks()) {
					if (apk.getMarketBean().getMarketName()
							.equals(market.getMarketName())) {
						if (latestApk == null){
							latestApk = apk;
						}else if (latestApk.getVersionCode() < apk
								.getVersionCode())
							latestApk = apk;
					}
				}
				if (latestApk != null)
					out.write("," + latestApk.getVersionCode() + ","
							+ latestApk.marketBean.getMarketName() + ","
							+ latestApk.getApkLocalPath() + ","
							+ latestApk.getApkFileChecksum());
				else
					out.write(",,,,");
			}

			for (ApkBean apk : packageReports.get(str).getApks()) {
				if (fileHash == null)
					fileHash = apk.getApkFileChecksum();
				else if (!fileHash.equals(apk.getApkFileChecksum()))
					isCollision = true;
			}
			out.write("," + isCollision);
		}
		out.close();
	}
	
	public static List<ApkBean> loadOfficialApks(String officialApkDir){
		List<File> files=FileUtilsExt.getAllFiles(officialApkDir,null);
		List<ApkBean> officialApks=new ArrayList<ApkBean>();
		for(File file:files){
			ApkBean apk=ApkFileManager.unzipApk(file.getAbsolutePath());
			if(apk==null)
				continue;
			officialApks.add(apk);
		}
		return officialApks;
	}

	public static List<ApkBean> getFakeApks(List<ApkBean> officialApks,
			List<String> marketNames) {
		MarketDb[] markets=new MarketDb[marketNames.size()];
		for (int i=0;i<marketNames.size();i++)
			markets[i]=loadMarket(marketNames.get(i), true);
		
		for (MarketDb market : markets) {
			List<ApkBean> fakeApks = getFakeApks(officialApks, market);
			for (ApkBean fakeApk : fakeApks);
		}
		return null;
	}

	private static List<ApkBean> getFakeApks(List<ApkBean> officialApks,
			MarketDb market) {
		List<ApkBean> fakeApks = new ArrayList<ApkBean>();

		for (ApkBean officialApk : officialApks) {
			List<ApkBean> marketApks = market.getApkByPackage(officialApk);
			for (ApkBean marketApk : marketApks) {
				if (!officialApk.hasSameCerts(marketApk)) {
					fakeApks.add(marketApk);
					System.out.println(market.getMarketName() + ","
							+ officialApk.getPackageName() + ","
							+ officialApk.getApkLocalPath()+","
							+ marketApk.getApkLocalPath());

					break;
				}
			}
		}
		return fakeApks;
	}
}

class PackageReportEntry {
	String packageName;
	List<ApkBean> apks = new ArrayList<ApkBean>();

	public PackageReportEntry(ApkBean apk) {
		super();
		this.packageName = apk.getPackageName();
		apks.add(apk);
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<ApkBean> getApks() {
		return apks;
	}

	public void setApks(List<ApkBean> apks) {
		this.apks = apks;
	}
}
