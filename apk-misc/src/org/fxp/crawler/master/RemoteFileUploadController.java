package org.fxp.crawler.master;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.fxp.android.apk.ApkManager;

public class RemoteFileUploadController {
	public static Configuration config;

	public RemoteFileUploadController() {
		super();
	}

	public void init() throws ConfigurationException, NumberFormatException,
			IOException {
		XMLConfiguration config = new XMLConfiguration();
		File file = new File("controller_config.xml");
		if(!file.isFile()){
			// Create new configuration file
			file.createNewFile();
			config.setFile(file);
			config.setProperty("srv_addr", "127.0.0.1");
			config.setProperty("srv_port", "22222");
			config.setProperty("srv_name", "FileUpload");
			config.save();			
		}
		config.setFile(file);
		config.load();

		LocateRegistry.createRegistry(Integer.valueOf(config
				.getString("srv_port")));
		RemoteFileUploadImp uploadSrv = new RemoteFileUploadImp();
		Naming.rebind(
				"rmi://" + config.getString("srv_addr") + ":"
						+ config.getString("srv_port") + "/"
						+ config.getString("srv_name"), uploadSrv);
		
		System.out.println("File upload server is ready.");
	}

	/**
	 * @param args
	 * @throws ConfigurationException
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws ConfigurationException, NumberFormatException, IOException {
		RemoteFileUploadController controller = new RemoteFileUploadController();
		controller.init();
	}

}
