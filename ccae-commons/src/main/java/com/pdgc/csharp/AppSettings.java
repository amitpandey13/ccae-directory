package com.pdgc.csharp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.general.lookup.Constants;

public class AppSettings {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppSettings.class);

	private static Properties appProperties;


	private AppSettings() {
		// private C'tor
	}

	static {
		appProperties = new Properties();
		final File propFile = Constants.retrieveExternalPropertyFile(Constants.CONSTANTS_PROPERTY_FILE_NAME);
        if (propFile.exists()) {
            // The property file at the same location as jar takes higher precedence
            try (final FileInputStream fileInput = new FileInputStream(propFile)) {
            	appProperties.load(fileInput);
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        } else {
            try (final InputStream resourceAsStream = Constants.class.getClassLoader().getResourceAsStream("Constants.properties")) {
            	appProperties.load(resourceAsStream);
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        }
	}

	public static String get(String key) {
		return appProperties.getProperty(key);
	}

}
