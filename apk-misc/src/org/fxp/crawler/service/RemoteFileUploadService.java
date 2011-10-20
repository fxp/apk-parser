package org.fxp.crawler.service;

import java.rmi.*;
import java.io.IOException;
import java.io.InputStream;

import org.fxp.android.apk.ApkBean;
import org.fxp.tools.FileUtilsExt;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

// A simple remote file upload service
public interface RemoteFileUploadService extends Remote {
	
	public void uploadFile(String fileName,ApkBean apk, RemoteInputStream remoteFileData)throws RemoteException;
	
}