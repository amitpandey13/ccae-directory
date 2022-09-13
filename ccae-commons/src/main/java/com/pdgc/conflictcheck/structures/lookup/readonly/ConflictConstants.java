package com.pdgc.conflictcheck.structures.lookup.readonly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.ConflictType;
import com.pdgc.general.lookup.Constants;

public class ConflictConstants {
	
    private static boolean initialized = false;
    
	public static ConflictClass NO_CORP_CONFLICT; //Conflict for when there are no corporate strands found
	public static ConflictClass NO_CONFLICT; //Dummy conflict for filling the matrix that actually will not produce any conflict
	public static ConflictClass UNAVAILABLE_CORP_RIGHTS_CONFLICT; //Conflict for when the corporate availablity = NO
	public static ConflictClass CONDITIONAL_CORP_RIGHTS_CONFLICT; //Conflict for when the corporate availability = CONDITIONAL
	public static ConflictClass PLAYOFF_PERIOD_CONFLICT; //Conflict to represent a playoff period - unused for now

	private static final Logger LOGGER = LoggerFactory.getLogger(ConflictConstants.class);

	public static void instantiateConstants() throws IOException {

		Properties prop = new Properties();

        File file = Constants.retrieveExternalPropertyFile("Constants.properties");
		if (file.exists()) {
			// The property file at the same location as jar takes higher precedence
			try (final FileInputStream fileInputStream = new FileInputStream(file)) {
				prop.load(fileInputStream);
			} catch (IOException ex) {
				LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
			}
		} else {
			try (final InputStream resourceInputStream = Constants.class.getClassLoader().getResourceAsStream("Constants.properties")) {
			    if (resourceInputStream != null) {
			        prop.load(resourceInputStream);   
			    }
			} catch (IOException ex) {
				LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
			}
		}

		
		NO_CORP_CONFLICT = new ConflictClass(
			new ConflictType(Long.valueOf(prop.getProperty("NO_CORP_CONFLICT_TYPE_ID", "15")), prop.getProperty("NO_CORP_CONFLICT_NAME", "No Corporate Strands")),
			ConflictSeverity.byValue(Integer.valueOf(prop.getProperty("NO_CORP_CONFLICT_SEVERITY", "42")))
		);
		
		NO_CONFLICT = new ConflictClass(
			new ConflictType(Long.valueOf(prop.getProperty("NO_CONFLICT_TYPE_ID", "1")), prop.getProperty("NO_CONFLICT_NAME", "No Conflict")),
			ConflictSeverity.byValue(Integer.valueOf(prop.getProperty("NO_CONFLICT_SEVERITY", "44")))
		);
		
		UNAVAILABLE_CORP_RIGHTS_CONFLICT = new ConflictClass(
			new ConflictType(Long.valueOf(prop.getProperty("UNAVAILABLE_CORP_RIGHTS_CONFLICT_TYPE_ID", "15")), prop.getProperty("UNAVAILABLE_CORP_RIGHTS_CONFLICT_NAME", "No Rights In")),
			ConflictSeverity.byValue(Integer.valueOf(prop.getProperty("UNAVAILABLE_CORP_RIGHTS_CONFLICT_SEVERITY", "42")))
		);
		
		CONDITIONAL_CORP_RIGHTS_CONFLICT = new ConflictClass(
			new ConflictType(Long.valueOf(prop.getProperty("CONDITIONAL_CORP_RIGHTS_CONFLICT_TYPE_ID", "15")), prop.getProperty("CONDITIONAL_CORP_RIGHTS_CONFLICT_NAME", "No Rights In")),
			ConflictSeverity.byValue(Integer.valueOf(prop.getProperty("CONDITIONAL_CORP_RIGHTS_CONFLICT_SEVERITY", "42")))
		);
		
		PLAYOFF_PERIOD_CONFLICT = new ConflictClass(
			new ConflictType(Long.valueOf(prop.getProperty("PLAYOFF_PERIOD_CONFLICT_TYPE_ID", "17")), prop.getProperty("PLAYOFF_PERIOD_CONFLICT_NAME", "No Rights In - Playoff Period")),
			ConflictSeverity.byValue(Integer.valueOf(prop.getProperty("PLAYOFF_PERIOD_CONFLICT_SEVERITY", "42")))
		);
		
		initialized = true;
	}
	
	/**
	 * Unlike normal Constants, these are not necessarily needed by every part of the app (see: avails), 
	 * so don't bother attempting to maintain these if they're not needed
	 * @return
	 */
	public static boolean isInitialized() {
	    return initialized;
	}
}
