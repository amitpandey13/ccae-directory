package com.pdgc.general.structures.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.commons.exceptions.CCAEInvalidConfigurationException;

import static com.pdgc.general.lookup.Constants.retrieveExternalPropertyFile;

/**
 * Client-specified configurations that determine how the engine runs. 
 * 
 * @author CLARA HONG
 *
 */
public class ClientConfiguration {

	private static final String CLIENT_PROPERTY_FILE_NAME = "client.properties";

	public static int COMMIT_OVERRIDE;
	
	public static int TIME_OUT_EXPIRATION_SECONDS;
	public static int TIME_OUT_EXPIRATION_SECONDS_DISPLAY; 
	public static int TIME_OUT_EXPIRATION_SECONDS_COMMIT; 
	public static int TIME_OUT_EXPIRATION_SECONDS_ROLLUP; 
	public static int TIME_OUT_SECONDS_KAFKA_BUFFER; 
	public static boolean DRY_RUN_NO_COMMIT;
	
	public static int TIMER_SLEEP_SECONDS; 
	public static int TIMER_LOCKLIST_WATCHDOG = 60;   // Default to 60 Seconds
	
	public static int FAILED_REQUEST_MAX; 
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfiguration.class);
	
	static {
		
		try {
			loadConfigurations();
		} catch (CCAEInvalidConfigurationException e) {
			LOGGER.error("Missing or bad properties file entry in client.properties file. Exiting Immediately {} ",e.getMessage(), e);
			throw new RuntimeException("Missing or bad properties file or bad entry in client.properties. Exiting Immediately" + e.getMessage(),e);
		}
	}

	//
	// This is only called by the static block to get the properties file loaded only once and before anything attempts to use it
	//
	private static void loadConfigurations() throws CCAEInvalidConfigurationException {

		File configFile = retrieveExternalPropertyFile(CLIENT_PROPERTY_FILE_NAME);
		FileInputStream configFileInput;
		try {
			configFileInput = new FileInputStream(configFile);
			Properties configProp = new Properties();
			configProp.load(configFileInput);
			configFileInput.close();
			
			COMMIT_OVERRIDE = Integer.valueOf(configProp.getProperty("COMMIT_REQUEST_OVERRIDE_CONFIGURATION"));
			
			TIME_OUT_EXPIRATION_SECONDS = Integer.valueOf(configProp.getProperty("TIME_OUT_EXPIRATION_SECONDS"));
			TIME_OUT_EXPIRATION_SECONDS_DISPLAY = Integer.valueOf(configProp.getProperty("TIME_OUT_EXPIRATION_SECONDS_DISPLAY"));
			TIME_OUT_EXPIRATION_SECONDS_COMMIT = Integer.valueOf(configProp.getProperty("TIME_OUT_EXPIRATION_SECONDS_COMMIT"));
			TIME_OUT_EXPIRATION_SECONDS_ROLLUP = Integer.valueOf(configProp.getProperty("TIME_OUT_EXPIRATION_SECONDS_ROLLUP"));
			TIME_OUT_SECONDS_KAFKA_BUFFER = Integer.valueOf(configProp.getProperty("TIME_OUT_SECONDS_KAFKA_BUFFER"));
			TIMER_SLEEP_SECONDS = Integer.valueOf(configProp.getProperty("TIMER_SLEEP_SECONDS"));
			FAILED_REQUEST_MAX = Integer.valueOf(configProp.getProperty("FAILED_REQUEST_MAX"));
			
			if (configProp.getProperty("DRY_RUN_NO_COMMIT") == null) {
				LOGGER.warn("DRY_RUN_NO_COMMIT is defined in client.properties, defaulting to FALSE");
				DRY_RUN_NO_COMMIT = false;
			} else {
				DRY_RUN_NO_COMMIT = Boolean.valueOf(configProp.getProperty("DRY_RUN_NO_COMMIT"));
			}
			
			if (configProp.getProperty("TIMER_LOCKLIST_WATCHDOG") == null) {
				LOGGER.warn("TIMER_LOCKLIST_WATCHDOG is defined in client.properties, defaulting to 60 seconds");
				TIMER_LOCKLIST_WATCHDOG = 60;
			} else {
				TIMER_LOCKLIST_WATCHDOG = Integer.valueOf(configProp.getProperty("TIMER_LOCKLIST_WATCHDOG"));
			}
			
			LOGGER.info("COMMIT_OVERRIDE................................:" + COMMIT_OVERRIDE);
			LOGGER.info("TIME_OUT_EXPIRATION_SECONDS....................:" + TIME_OUT_EXPIRATION_SECONDS);
			LOGGER.info("TIME_OUT_EXPIRATION_SECONDS_DISPLAY............:" + TIME_OUT_EXPIRATION_SECONDS_DISPLAY);
			LOGGER.info("TIME_OUT_EXPIRATION_SECONDS_COMMIT.............:" + TIME_OUT_EXPIRATION_SECONDS_COMMIT);
			LOGGER.info("TIME_OUT_EXPIRATION_SECONDS_ROLLUP.............:" + TIME_OUT_EXPIRATION_SECONDS_ROLLUP);
			LOGGER.info("TIME_OUT_SECONDS_KAFKA_BUFFER..................:" + TIME_OUT_SECONDS_KAFKA_BUFFER);
			LOGGER.info("TIMER_SLEEP_SECONDS............................:" + TIMER_SLEEP_SECONDS);
			LOGGER.info("FAILED_REQUEST_MAX.............................:" + FAILED_REQUEST_MAX);
			LOGGER.info("DRY_RUN_NO_COMMIT..............................:" + DRY_RUN_NO_COMMIT);
			LOGGER.info("TIMER_LOCKLIST_WATCHDOG........................:" + TIMER_LOCKLIST_WATCHDOG);
			
		} catch (FileNotFoundException e) {
			LOGGER.error("Cannot find the client.properties file!");
			throw new CCAEInvalidConfigurationException("Cannot find the client.properties file!",e);
		} catch (IOException e) {
			LOGGER.error("Cannot read  the client.properties file due to an I/O Error");
			throw new CCAEInvalidConfigurationException("Cannot read  the client.properties file due to an I/O Error",e);
		} catch (Exception e) {
			LOGGER.error("Missing or bad properties file entry in client.properties file");
			throw new CCAEInvalidConfigurationException("Missing or bad properties file entry in client.properties file",e);
		}
	}

}
