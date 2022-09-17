package com.pdgc.general.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SQLConstants on client-commons
 */
public class SQLConstants {

	public static Properties loadQueriesFromFile(String fileName) {
		Properties props = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query file " + fileName, e);
		}
	    return props;
	}
}
