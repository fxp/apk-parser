package org.fxp.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Configuration {
	private static PropertiesConfiguration cfg;

	public static void main(String[] args) throws IOException, ConfigurationException {
		File logFile = new File("gfan.cfg");
		if (!logFile.isFile())
			logFile.createNewFile();

		cfg = new PropertiesConfiguration(logFile);
		cfg.setAutoSave(true);

		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < 300; i++) {
			map.put(i + "key", i);
		}
		cfg.setProperty("lalala", map);
		HashMap test = (HashMap) cfg.getProperty("lalala");
		Integer j = (Integer) test.get("2key");
	}
}
