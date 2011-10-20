package org.fxp.android.apk.tester;

import java.io.File;
import java.util.Scanner;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;
import org.fxp.tools.FileUtilsExt;

public class ApkManCmd {

	public static void insertAllCertificate(){
//		ApkManDao dao = ApkManDao.GetInstance();
		File[] files=FileUtilsExt.getAllFiles((new File("X:\\apkDownload\\eoemarket")), null);
		for(File file:files){
			ApkBean apk=ApkFileManager.unzipApk(file.getAbsolutePath());
			if(apk==null)
				continue;
			System.out.println(apk.toString());
//			dao.insertCert(apk);
			
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		insertAllCertificate();
		
/*		ApkManDao dao = ApkManDao.GetInstance();
		ApkBean apk = ApkFileManager.unzipApk(args[0]);
		// Put apk into database
		dao.fillApk(apk);
		System.out.println(apk.marketBean.marketDescription);
		System.out.println(apk.marketBean.marketDeveloper);
		System.out.println(apk.marketBean.marketAppName);
		Scanner sc = new Scanner(System.in);
		
		System.out.println("OfficialSite");
		String officialSite=sc.nextLine();
		System.out.println("Note");
		String note=sc.nextLine();
		System.out.println("DeveloperName");
		String dev=sc.nextLine();

		for(CertBean cert:apk.certs){
			cert.officialSite = officialSite;
			cert.note = note;
			cert.devName = dev;
		}

		dao.updateCert(apk);
*/	}
}
