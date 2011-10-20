package org.fxp.crawler.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.fxp.crawler.master.RemoteFileUploadImp;
import org.fxp.crawler.service.RemoteFileUploadService;

import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

public class Worker {
	public static Configuration config;

	public void init() throws ConfigurationException {
		ConfigurationFactory factory = new ConfigurationFactory(
				"controller_config.xml");
		config = factory.getConfiguration();
		config.setProperty("srv_addr", "127.0.0.1");
		config.setProperty("srv_port", "22222");
		config.setProperty("srv_name", "FileUpload");
	}

	public static void main(String[] args) throws FileNotFoundException,
			RemoteException, MalformedURLException, NotBoundException, ConfigurationException {
		Worker worker=new Worker();
		worker.init();
		
		RemoteFileUploadService fileUpload = (RemoteFileUploadService) Naming
				.lookup("rmi://" + config.getString("srv_addr","127.0.0.1") + ":"
						+ config.getString("srv_port","22222") + "/"
						+ config.getString("srv_name","FileUpload"));
		
		// Create 
		
		
		
//		InputStream fileData = new FileInputStream(new File("myapk.cn_crawler_report.xml"));
//		RemoteInputStreamServer remoteFileData = new SimpleRemoteInputStream(
//				fileData);
//		fileUpload.uploadFile("MyFile", null, remoteFileData.export());
	}
}
