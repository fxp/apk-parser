package org.fxp.android.market.worker;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.android.market.api.ApkDAO;
import org.fxp.mode.SingletonException;

public class ApkLib {
	private static ApkLib self = null;
	private static boolean instance_flag = false;
	private ApkDAO apkDAO = null;
	private ApkFileManager apkFileManager = null;

	ApkLib() {
		if (instance_flag)
			throw new SingletonException("Only one instance allowed");
		else
			instance_flag = true;
	};

	private void init() {
		apkDAO = ApkDAO.GetInstance();
		apkFileManager = ApkFileManager.GetInstance();
		if (apkDAO == null || apkFileManager == null)
			instance_flag = false;
		instance_flag = true;
	}

	public static ApkLib GetInstance() {
		if (self == null) {
			self = new ApkLib();
			self.init();
			if (!instance_flag)
				self = null;
		}
		return self;
	}

	public int putApk(ApkBean apk) throws FileNotFoundException, IOException {
		ApkBean apkDownload=apkFileManager.testAndAddApk(apk);
		// Put it into apk file lib
		if(apkDownload!=null){
			int ret=apkDAO.insertApk(apkDownload);
			return ret;
		}
		return -1;
		
	}

	public int putSearchHistory(ApkBean apk) {
		return apkDAO.insertSearchHistory(apk);
	}

	public String[] getSearchKeyWords() {
		return apkDAO.getSearchKeywords();
	}

	public String[] getPackageNames() {
		return apkDAO.getPackageNames();
	}
	
	public void initIdLib(String marketName, String[] ids){
		apkDAO.initIdLib(marketName, ids);
	}

}
