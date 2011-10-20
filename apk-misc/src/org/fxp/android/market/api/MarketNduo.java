package org.fxp.android.market.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.fxp.android.apk.ApkBean;


public class MarketNduo extends Market {

	public MarketNduo(String marketname) {
		this.market_name = marketname;
	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {

	}

	@Override
	public ApkBean[] searchAndDownload(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApkBean[] downloadApks(ApkBean[] apks) {
		// TODO Auto-generated method stub
		/*		for (final ApkBean apk : apks) {
			ApkBean apkTmp = new ApkBean();
			apkTmp.marketBean.marketPid = apk.marketBean.marketPid;
			ObjectSet<ApkBean> ret = db.query(new Predicate<ApkBean>() {
				private static final long serialVersionUID = 8720746934859387514L;

				public boolean match(ApkBean i) {
					return (apk.marketBean.marketPid == i.marketBean.marketPid && i.apkLocalPath != null);
				}
			});
			if (ret.size() == 0) {
				downloadApk(apk);
				db.store(apk);
				db.commit();
			}
		}*/
		return null;
	}

	@Override
	public ApkBean downloadApk(ApkBean apk) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			
			HttpGet httpget = new HttpGet(apk.marketBean.marketDownloadUrl);
			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				if (entity.getContentLength() <= 18) {
					apk.apkLocalPath = null;
					return apk;
				}
				saveApkFile(apk, instream);
				instream.close();
			}
			return apk;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected ApkBean[] doApkFetch(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initIdLib() {
		// TODO Auto-generated method stub

	}
}
