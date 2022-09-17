package com.pdgc.general.service;

import java.util.Properties;

public enum JobType {
	NORMAL_AVAILS, LARGE_AVAILS, CONFLICT_CHECK, CONFLICT_ROLLUP, EXCEL;
	
	private int memoryAllotment; //in MB
	private int timeAllotment; //in minutes

    /**
     *
     * @param prop
     */
    @SuppressWarnings("PMD.NPathComplexity")
	public static void instantiateAllotments(Properties prop) {
		NORMAL_AVAILS.memoryAllotment = (prop.getProperty("NORMAL_AVAILS_JOB_MB_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("NORMAL_AVAILS_JOB_MB_ALLOTMENT")) 
			: 1000;
		NORMAL_AVAILS.timeAllotment = (prop.getProperty("NORMAL_AVAILS_JOB_MINUTE_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("NORMAL_AVAILS_JOB_MINUTE_ALLOTMENT")) 
			: 60;
			
		LARGE_AVAILS.memoryAllotment = (prop.getProperty("LARGE_AVAILS_JOB_MB_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("LARGE_AVAILS_JOB_MB_ALLOTMENT")) 
			: 4000;
		LARGE_AVAILS.timeAllotment = (prop.getProperty("LARGE_AVAILS_JOB_MINUTE_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("LARGE_AVAILS_JOB_MINUTE_ALLOTMENT")) 
			: 360;
		
		CONFLICT_CHECK.memoryAllotment = (prop.getProperty("CONFLICT_CHECK_JOB_MB_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("CONFLICT_CHECK_JOB_MB_ALLOTMENT")) 
			: 1000;
		CONFLICT_CHECK.timeAllotment = (prop.getProperty("CONFLICT_CHECK_JOB_MINUTE_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("CONFLICT_CHECK_JOB_MINUTE_ALLOTMENT")) 
			: 60;
		
		CONFLICT_ROLLUP.memoryAllotment = (prop.getProperty("CONFLICT_ROLLUP_JOB_MB_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("CONFLICT_ROLLUP_JOB_MB_ALLOTMENT")) 
			: 1000;
		CONFLICT_ROLLUP.timeAllotment = (prop.getProperty("CONFLICT_ROLLUP_JOB_MINUTE_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("CONFLICT_ROLLUP_JOB_MINUTE_ALLOTMENT")) 
			: 60;
		
		EXCEL.memoryAllotment = (prop.getProperty("EXCEL_JOB_MB_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("NORMAL_AVAILS_JOB_MB_ALLOTMENT")) 
			: 1000;
		EXCEL.timeAllotment = (prop.getProperty("EXCEL_JOB_MINUTE_ALLOTMENT") != null)
			? Integer.valueOf(prop.getProperty("NORMAL_AVAILS_JOB_MINUTE_ALLOTMENT")) 
			: 60;
	}

	public int getMemoryAllotment() {
		return memoryAllotment;
	}
	
	public int getTimeAllotment() {
		return timeAllotment;
	}
}
