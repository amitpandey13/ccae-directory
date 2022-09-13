package com.pdgc.tests.general.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmtpAppenderTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmtpAppenderTest.class);
	
	public static void main(String[] args) {
		LOGGER.info("Buffer test 1");
		LOGGER.info("Buffer test 2");
		LOGGER.info("Buffer test 3");
		LOGGER.info("Buffer test 4");
		LOGGER.error("TESTING SMTP APPENDER");
	}
}
