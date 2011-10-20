package org.fxp.crawler.master;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.configuration.ConfigurationException;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkManager;
import org.fxp.crawler.service.RemoteFileUploadService;
import org.fxp.tools.FileUtilsExt;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

public class RemoteFileUploadImp extends UnicastRemoteObject implements
		RemoteFileUploadService {

	private static final long serialVersionUID = -4822688794814729980L;
	public ApkManager apkManager;

	protected RemoteFileUploadImp() throws ConfigurationException, IOException {
		super();
		apkManager=new ApkManager(); 
		apkManager.init();
	}

	@Override
	public void uploadFile(String fileName, ApkBean apk,RemoteInputStream remoteFileData)
			throws RemoteException {
		try {
			InputStream fileData = RemoteInputStreamClient.wrap(remoteFileData);
			apkManager.saveApk(apk, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
