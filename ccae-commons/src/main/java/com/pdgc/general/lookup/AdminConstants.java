package com.pdgc.general.lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AdminConstants {
	/**
	 * This method instantiates Constants and any shared variables (ObjectMapper)
	 * 
	 * @throws IOException
	 */
	
	public static boolean FULL_LOAD;
	public static void instantiateConstants() throws IOException {
		File file = new File("./admin.properties");
		FileInputStream fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInput);
		fileInput.close();
		
		FULL_LOAD = Boolean.valueOf(prop.getProperty("FULL_LOAD", "false"));
	}
}
