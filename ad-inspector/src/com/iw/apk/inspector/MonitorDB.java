package com.iw.apk.inspector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apkReader.ApkInfo;

import com.db4o.*;
import com.db4o.query.*;

public class MonitorDB {

	static Logger log = LoggerFactory.getLogger(MonitorDB.class);

	static int MAX_RETRIEVE_COUNT = 100;
	String dbName;

	ObjectContainer db;

	public MonitorDB(String appId) {
		this.dbName = appId;
		open();
	}

	public void open() {
		dbName =  dbName + ".db";
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbName);
	}

	public ApkInfo[] getLatestAppList(int max) {
		if (max < -1)
			return null;

		ObjectSet<ApkInfo> result = db.query(
				new Predicate<ApkInfo>() {
					int resultCount = 0;

					@Override
					public boolean match(ApkInfo arg0) {
						return true;
					}
				});
		
		int resultCount;
		if(max==-1)
			resultCount=result.size();
		else
			resultCount = Math.min(max, result.size());
		ApkInfo[] results = new ApkInfo[resultCount];
		for (int i = 0; i < resultCount; i++)
			results[i] = result.get(i);

		printlList(result);
		return results;
	}

	public void printlList(ObjectSet result) {
//		System.out.println("result count=" + result.size());
//		for (Object o : result) {
//			System.out.println(((ExceptionUpdate) o));
//		}
	}

	public List<ApkInfo> insertMonitorAppItems(
			List<ApkInfo> items, int maxRollback) {
		return _insertMonitorAppItems(items, maxRollback);
	}

	public List<ApkInfo> insertMonitorAppItems(
			List<ApkInfo> items) {
		return _insertMonitorAppItems(items, MAX_RETRIEVE_COUNT);
	}

	private List<ApkInfo> _insertMonitorAppItems(
			List<ApkInfo> items, int maxRollback) {
		// If exist, do nothing
		List<ApkInfo> ret = new ArrayList<ApkInfo>();
		// ExceptionUpdate[] latestItems = getLatestAppList(maxRollback);
		// if (latestItems == null)
		// return ret;

		for (ApkInfo item : items) {
			if (insertMonitorAppItem(item))
				ret.add(item);
		}

		return ret;
	}

	public synchronized boolean insertMonitorAppItem(ApkInfo item) {
		db.store(item);
		log.info("inserteditem:" + item);
		return true;
	}

	public void close() {
		db.commit();
		db.close();
	}
}
