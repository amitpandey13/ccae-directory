package com.pdgc.general.lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConstants.class);
	private static final String KAFKA_PROPERTY_FILE_NAME = "kafka.properties";


	public static String KAFKA_SERVER_ADDRESS;
	public static String APPLICATION_ID;
	public static String CLIENT_REQUEST;
	public static String DEAD_LETTER_REQUEST;
	public static String BULK_LOAD_REQUEST;

	//Avails related
	public static String AVAILS_CALCULATION;
	public static String AVAILS_REMOTEJOBREQUEST;
	public static String EXCEL_JOB;
	public static String AVAILS_NOTIFICATION;
	public static String LARGE_AVAILS_CALCULATION;
	public static String SOLD_UNSOLD_RESULT;
	
	// Avails group name
	public static String AVAILS_JOBSTATUSPROCESSOR_GROUPNAME;
	public static String AVAILS_REMOTEJOBPRODUCERPROCESSOR_GROUPNAME;
	public static String AVAILS_FRRRPRODUCT_GROUPNAME;
	public static String AVAILS_FRRRREQUEST_GROUPNAME;
	public static String AVAILS_REMOTEJOBPROCESSOR_GROUPNAME;
	public static String AVAILS_EXCELJOBPROCESSOR_GROUPNAME;
	public static String AVAILS_LARGEAVAILSJOBPROCESSOR_GROUPNAME;
	
	
	//ConflictCheck related
	public static String CC_REQUEST;
	public static String CC_RESULT;
	public static String CC_ROLLUP_REQUEST;
	public static String CC_ROLLUP_RESULT;
	public static String CC_STATUS;
	
	//ConflictCheck group name
	public static String CC_CLIENTREQUESTPROCESSOR_GROUPNAME;
	public static String CC_BULKLOADREQUEST_GROUPNAME;
	public static String CC_DEDUPPROCESSOR_GROUPNAME;
	public static String CC_LOCKINGQUEUEPROCESSOR_GROUPNAME;
	public static String CC_CONFLICTCHECKNODEMANAGER_GROUPNAME;
	public static String CC_RIGHTSINCHANGEPROCESSOR_GROUPNAME;
	public static String CC_CANCELCHECKOUTREQUESTPROCESSOR_GROUPNAME;
	public static String CC_CHECKOUTREQUESTPROCESSOR_GROUPNAME;
	public static String CC_COMMITREQUESTPROCESSOR_GROUPNAME;
	public static String CC_CONFLICTCHECKREQUESTPROCESSOR_GROUPNAME;
	public static String CC_CONFLICTCHECKRESULTPROCESSOR_GROUPNAME;
	public static String CC_MESSAGEOFFSETPROCESSOR_GROUPNAME;
	public static String CC_NODESTATUSPROCESSOR_GROUPNAME;
	public static String CC_RESYNCPROCESSOR_GROUPNAME;
	public static String CC_ROLLUPRESULTPROCESSOR_GROUPNAME;
	public static String CC_ROLLUPREQUESTPROCESSOR_GROUPNAME;
	public static String CC_CANCELCHECKOUTPROCESSOR_GROUPNAME;
	public static String CC_CARVEOUTCONFLICTPROCESSOR_GROUPNAME;
	public static String CC_CONFLICTCHECKRESULTSPROCESSOR_GROUPNAME;
	public static String CC_DISPLAYREQUESTPROCESSOR_GROUPNAME;
	public static String CC_CONFLICTRESPONSEPROCESSOR_GROUPNAME;
	public static String PRODUCT_HIERARCHY_CHANGE_GROUPNAME;

	

	// Cache topics 
	public static String CACHE_UPDATE_TOPIC;
	
	// Offset logging
	public static String OFFSET_STATUS;
	
	//Display Related
	public static String DISPLAY_REQUEST;
	
	//Check out request
	public static String CHECK_OUT_REQUEST;

	//Check out request
	public static String CANCEL_CHECK_OUT_REQUEST;

	//Commit
	public static String COMMIT_REQUEST;
	
	//RightsIn Change
	public static String RIGHTS_IN_CHANGE_REQUEST;
	
	//Locking
	public static String LOCKING_REMOVAL;
	
	// Resync
	public static String RESYNC_REQUEST;
	
	// BULK LOAD dedupe
	public static String BULK_LOAD_REQUEST_DEDUPED;
	
	// admin sink
	public static String ADMIN_SINK;
	
	// carveout
	public static String CARVEOUT_CHECK;
	
	// conflicts response
	public static String CC_CONFLICT_RESPONSE;

	// product hierarchy change
	public static String PRODUCT_HIERARCHY_CHANGE_TOPIC_NAME;
	
	// Producer Properties 
	public static String KAFKA_STRING_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
	public static String KAFKA_BYTE_ARRAY_SERIALIZER = "org.apache.kafka.common.serialization.ByteArraySerializer";
	
	public static String PRODUCER_ACKS;
	public static String PRODUCER_RETRIES;
	public static String PRODUCER_BATCH_SIZE;
	public static String PRODUCER_LINGER_MS;
	public static String PRODUCER_BUFFER_MEMORY;
	public static String PRODUCER_MAX_REQUEST_SIZE;
	
	// Consumer Properties
	public static String AVAILS_MAX_POLL_INTERVAL_MS;
	public static String AVAILS_HEARTBEAT_INTERVAL_MS;
	public static String AVAILS_SESSION_TIMEOUT_MS;
	public static String AVAILS_MAX_POLL_RECORDS;
	public static Long AVAILS_POLL_TIME_MS;
	
	// Consumer Properties
	public static String KAFKA_STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
	public static String KAFKA_BYTE_ARRAY_DESERIALIZER = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
	
	// Avails App processing
	public static int AVAILS_REMOTE_JOB_PRODUCER_NUM_THREADS;
	public static int AVAILS_JOB_STATUS_NUM_THREADS;
	public static int AVAILS_CALCULATION_NUM_THREADS;
	public static int AVAILS_LARGE_CALCULATION_NUM_THREADS;
	public static int AVAILS_EXCEL_NUM_THREADS;
	public static int AVAILS_FRRR_REQUEST_NUM_THREADS;
	public static int AVAILS_FRRR_PRODUCT_NUM_THREADS;
	
	// Bulk_Load
	public static Integer BULK_LOAD_MAX_POLL_RECORDS;
	public static Integer BULK_LOAD_DEDUPE_MAX_POLL_RECORDS;
	
	public static String AVAILS_FRRR_REQUEST;
  	public static String AVAILS_FRRR_PRODUCT;
	
	
	public static void instantiateConstants() throws IOException {

		File file = Constants.retrieveExternalPropertyFile(KAFKA_PROPERTY_FILE_NAME);
		if (!file.exists()) {
			LOGGER.error("Can't find the {} at {}", KAFKA_PROPERTY_FILE_NAME, file.getAbsolutePath());
			throw new RuntimeException("Fails to load " + KAFKA_PROPERTY_FILE_NAME);
		}

		FileInputStream fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInput);
		fileInput.close();
		//require a prefix for the topic names.
		String prefix = prop.getProperty("kafkaTopicPrefix", "no_prefix_set");
		prefix = prefix + "-";
		
		KAFKA_SERVER_ADDRESS = prop.getProperty("kafkaServerAddress");
		APPLICATION_ID = prefix + prop.getProperty("conflictApplicationID");
		CLIENT_REQUEST = prefix + prop.getProperty("clientRequestTopic");
		DEAD_LETTER_REQUEST = prefix + prop.getProperty("deadLetterClientRequestTopic");
		BULK_LOAD_REQUEST = prefix + prop.getProperty("bulkLoadRequestTopic");
    	BULK_LOAD_REQUEST_DEDUPED =
        prefix + prop.getProperty("bulkLoadRequestDedupedTopic", "eng-bulk-load-deduped");

		
		//Locking
		LOCKING_REMOVAL = prefix + prop.getProperty("ccPopQueue");
				
		//AvailsCalculation Related
		AVAILS_CALCULATION = prefix + prop.getProperty("availsCalculationTopic");
		AVAILS_REMOTEJOBREQUEST = prefix + prop.getProperty("availsRemoteJobRequestTopic");
		EXCEL_JOB = prefix + prop.getProperty("availsResultTopic");
		AVAILS_NOTIFICATION = prefix + prop.getProperty("notificationTopic");
		LARGE_AVAILS_CALCULATION = prefix + prop.getProperty("largeAvailsCalculationTopic");
		AVAILS_FRRR_REQUEST = prefix + prop.getProperty("availsFRRRRequestTopic");
		AVAILS_FRRR_PRODUCT = prefix + prop.getProperty("availsFRRRProductTopic");
		SOLD_UNSOLD_RESULT = prefix + prop.getProperty("soldUnsoldResultTopic");
		AVAILS_JOBSTATUSPROCESSOR_GROUPNAME = prefix + prop.getProperty("availsJobStatusProcessorGroupName");
		AVAILS_REMOTEJOBPRODUCERPROCESSOR_GROUPNAME = prefix + prop.getProperty("availsRemoteJobProducerProcessorGroupName");
		AVAILS_FRRRPRODUCT_GROUPNAME = prefix + prop.getProperty("availsFRRRProductGroupName");
		AVAILS_FRRRREQUEST_GROUPNAME = prefix + prop.getProperty("availsFRRRRequestGroupName");
		AVAILS_REMOTEJOBPROCESSOR_GROUPNAME = prefix + prop.getProperty("availsRemoteJobProcessorGroupName");
		AVAILS_EXCELJOBPROCESSOR_GROUPNAME = prefix + prop.getProperty("availsExcelJobProcessorGroupName");
		AVAILS_LARGEAVAILSJOBPROCESSOR_GROUPNAME = prefix + prop.getProperty("availsLargeAvailsJobProcessorGroupName");
		
		//ConflictCalculation Related
		CC_REQUEST = prefix + prop.getProperty("ccRequestTopic");
		CC_RESULT = prefix + prop.getProperty("ccResultTopic");
		CC_ROLLUP_REQUEST = prefix + prop.getProperty("ccRollupRequestTopic");
		CC_ROLLUP_RESULT = prefix + prop.getProperty("ccRollupResultTopic");
		CC_STATUS = prefix + prop.getProperty("ccStatusTopic");
		CC_CONFLICT_RESPONSE = prefix + prop.getProperty("ccConflictsResponseTopic");
		CC_CLIENTREQUESTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccClientRequestProcessorGroupName");
		CC_BULKLOADREQUEST_GROUPNAME =  prefix + prop.getProperty("ccBulkLoadRequestGroupName");
		CC_DEDUPPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccDedupProcessorGroupName");
		CC_LOCKINGQUEUEPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccLockingQueueProcessorGroupName");
		CC_CONFLICTCHECKNODEMANAGER_GROUPNAME =  prefix + prop.getProperty("ccConflictCheckNodeManagerGroupName");
		CC_RIGHTSINCHANGEPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccRightsInChangeProcessorGroupName");
		CC_CANCELCHECKOUTREQUESTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccCancelCheckoutRequestProcessorGroupName");
		CC_CHECKOUTREQUESTPROCESSOR_GROUPNAME = prefix + prop.getProperty("ccCheckoutRequestProcessorGroupName");
		CC_COMMITREQUESTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccCommitRequestProcessorGroupName");
		CC_CONFLICTCHECKREQUESTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccConflictCheckRequestProcessorGroupName");
		CC_CONFLICTCHECKRESULTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccConflictCheckResultsProcessorGroupName");
		CC_MESSAGEOFFSETPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccMessageOffsetProcessorGroupName");
		CC_NODESTATUSPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccNodeStatusProcessorGroupName");
		CC_RESYNCPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccResyncProcessorGroupName");
		CC_ROLLUPRESULTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccRollupResultProcessorGroupName");
		CC_ROLLUPREQUESTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccRollupRequestProcessorGroupName");
		CC_CANCELCHECKOUTPROCESSOR_GROUPNAME =  prefix + prop.getProperty("ccCancelCheckoutProcessorGroupName");
		CC_CARVEOUTCONFLICTPROCESSOR_GROUPNAME = prefix + prop.getProperty("ccCarveoutConflictProcessorGroupName");	
		CC_CONFLICTCHECKRESULTSPROCESSOR_GROUPNAME = prefix + prop.getProperty("ccConflictCheckResultsProcessorGroupName");	
		CC_DISPLAYREQUESTPROCESSOR_GROUPNAME = prefix + prop.getProperty("ccDisplayRequestProcessorGroupName");
		CC_CONFLICTRESPONSEPROCESSOR_GROUPNAME = prefix + prop.getProperty("ccConflictResponseProcessorGroupName");

		PRODUCT_HIERARCHY_CHANGE_GROUPNAME = prefix + prop.getProperty("productHierarchyChangeGroupName");
		
		
		// Cache Update Topics 
		CACHE_UPDATE_TOPIC = prefix + prop.getProperty("clientMaintenanceInternalTopic");
		
		// Offset logging
		OFFSET_STATUS = prefix + prop.getProperty("offsetStatusTopic");
		
		//Display Related
		DISPLAY_REQUEST = prefix + prop.getProperty("displayRequestTopic");
		
		//Check out request
		CHECK_OUT_REQUEST = prefix + prop.getProperty("checkOutRequestTopic");
		
		//Check out request
		CANCEL_CHECK_OUT_REQUEST = prefix + prop.getProperty("cancelCheckOutRequestTopic");

		//Commit
		COMMIT_REQUEST = prefix + prop.getProperty("commitRequestTopic");
		
		//RightsInChange Request
		RIGHTS_IN_CHANGE_REQUEST = prefix + prop.getProperty("ccRightsInChangeTopic");
		
		// Resync request
		RESYNC_REQUEST = prefix + prop.getProperty("resyncRequestTopic");
		
		// Bulk
	    BULK_LOAD_REQUEST_DEDUPED = prefix + prop.getProperty("bulkLoadRequestDedupedTopic", "eng-bulk-load-deduped");
	    
	    // Check Carveouts
	    CARVEOUT_CHECK = prefix + prop.getProperty("carveoutCheckTopic");

	    // Product Hierarchy Change
		PRODUCT_HIERARCHY_CHANGE_TOPIC_NAME = prefix + prop.getProperty("productHierarchyChangeTopic");
	    
		// Producer Properties
		PRODUCER_ACKS  = prop.getProperty("PRODUCER_ACKS", "all");
		PRODUCER_RETRIES = prop.getProperty("PRODUCER_RETRIES", "500");
		PRODUCER_BATCH_SIZE = prop.getProperty("PRODUCER_BATCH_SIZE", "16384");
		PRODUCER_LINGER_MS = prop.getProperty("PRODUCER_LINGER_MS", "0");
		PRODUCER_BUFFER_MEMORY = prop.getProperty("PRODUCER_BUFFER_MEMORY", "33554432");
		PRODUCER_MAX_REQUEST_SIZE = prop.getProperty("PRODUCER_MAX_REQUEST_SIZE", "10000000");
		
		// Consumer Properties
		AVAILS_MAX_POLL_INTERVAL_MS = prop.getProperty("AVAILS_MAX_POLL_INTERVAL_MS", "8000000");
		AVAILS_HEARTBEAT_INTERVAL_MS = prop.getProperty("AVAILS_HEARTBEAT_INTERVAL_MS", "3000");
		AVAILS_SESSION_TIMEOUT_MS = prop.getProperty("AVAILS_SESSION_TIMEOUT_MS", "10000");
		AVAILS_MAX_POLL_RECORDS = prop.getProperty("AVAILS_MAX_POLL_RECORDS", "500");
		AVAILS_POLL_TIME_MS = Long.valueOf(prop.getProperty("AVAILS_POLL_TIME_MS", "100"));
		
		// Avails App processing
		AVAILS_REMOTE_JOB_PRODUCER_NUM_THREADS = Integer.valueOf(prop.getProperty("REMOTE_JOB_PRODUCER_THREADS", "5"));
		AVAILS_JOB_STATUS_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_JOB_STATUS_NUM_THREADS", "5"));
		AVAILS_CALCULATION_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_CALCULATION_NUM_THREADS", "5"));
		AVAILS_LARGE_CALCULATION_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_LARGE_CALCULATION_NUM_THREADS", "5"));
		AVAILS_EXCEL_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_EXCEL_NUM_THREADS", "5"));
		AVAILS_FRRR_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_FRRR_REQUEST_NUM_THREADS", "1"));
		AVAILS_FRRR_PRODUCT_NUM_THREADS = Integer.valueOf(prop.getProperty("AVAILS_FRRR_PRODUCT_NUM_THREADS", "1"));
		
		// Bulk load
	    BULK_LOAD_MAX_POLL_RECORDS = new Integer(prop.getProperty("BULK_LOAD_MAX_POLL_RECORDS", "10000"));
	    BULK_LOAD_DEDUPE_MAX_POLL_RECORDS = new Integer(prop.getProperty("BULK_LOAD_DEDUPE_MAX_POLL_RECORDS", "100"));
	    
	    
		boolean ADMIN_TO_USER = Boolean.valueOf(prop.getProperty("ADMIN_TO_USER", "false"));
	    ADMIN_SINK = ADMIN_TO_USER ? CLIENT_REQUEST : BULK_LOAD_REQUEST;
	    
	    AVAILS_FRRR_REQUEST = prefix + prop.getProperty("availsFRRRRequestTopic");
		AVAILS_FRRR_PRODUCT = prefix + prop.getProperty("availsFRRRProductTopic");
	    
	    
		LOGGER.info(
				" LOADED KAFKA TOPICS ARE:"
				+"\n\t\t\t\tbroker....................................{}"
				+"\n\t\t\t\tclientRequestTopic........................{}" 
				+"\n\t\t\t\tconflictCalculationRequestTopic...........{}"
				+"\n\t\t\t\tdisplayResultTopic........................{}"
				+"\n\t\t\t\tcommitRequestTopic........................{}"
				+"\n\t\t\t\tcalculatingJobResponseTopic...............{}"
				+"\n\t\t\t\tconflictCalculationStatusTopic............{}"
				+"\n\t\t\t\tccRequestTopic............................{}"
				+"\n\t\t\t\trollupTopic...............................{}"
				+"\n\t\t\t\trollupRequestTopic........................{}"
				+"\n\t\t\t\trollupResultTopic.........................{}"
				+"\n\t\t\t\tccClientResponseTopic.....................{}"
				+"\n\t\t\t\tccConflictResponseTopic...................{}"
				+"\n\t\t\t\tccRightsInChangeTopic.....................{}"
				+"\n\t\t\t\tccCarveOutChangeTopic.....................{}"
				+"\n\t\t\t\tlockingQueueTopic.........................{}"
				+"\n\t\t\t\tcheckOutRequestTopic......................{}"
				+"\n\t\t\t\tcancelCheckOutRequestTopic................{}"
				+"\n\t\t\t\toffsetStatusTopic.........................{}"
				+"\n\t\t\t\tdeadLetter_client_request.................{}"
				+"\n\t\t\t\tavailsRemoteJobRequestTopic...............{}"
				+"\n\t\t\t\tavailsCalculationTopic....................{}"
				+"\n\t\t\t\tavailsResultTopic.........................{}"
				+"\n\t\t\t\tnotificationTopic ........................{}"
				+"\n\t\t\t\tclientMaintenanceTopic ...................{}"
				+"\n\t\t\t\tresyncTopic ..............................{}"
				+"\n\t\t\t\tavailsRequestTopic .......................{}"
				+"\n\t\t\t\tavailsCalculationTopic ...................{}"
				+"\n\t\t\t\tavailsNotificationTopic ..................{}"
				+"\n\t\t\t\tavailsLargeAvailsTopic ...................{}"
				+"\n\t\t\t\tavailsExcelTopic .........................{}"
				+"\n\t\t\t\tavailsFRRRRequestTopic....................{}"
				+"\n\t\t\t\tavailsFRRRProductTopic....................{}"
				,
				KAFKA_SERVER_ADDRESS,
				CLIENT_REQUEST,
				DISPLAY_REQUEST, 
				COMMIT_REQUEST, 
				CC_RESULT,
				CC_STATUS,
				CC_REQUEST,
				CC_ROLLUP_REQUEST,
				CC_ROLLUP_RESULT,
				CC_CONFLICT_RESPONSE,
				RIGHTS_IN_CHANGE_REQUEST,
				CARVEOUT_CHECK,
				LOCKING_REMOVAL,
				CHECK_OUT_REQUEST,
				CANCEL_CHECK_OUT_REQUEST,
				OFFSET_STATUS,
				DEAD_LETTER_REQUEST,
	            AVAILS_REMOTEJOBREQUEST,
				AVAILS_CALCULATION,
				EXCEL_JOB,
				AVAILS_NOTIFICATION,
				CACHE_UPDATE_TOPIC, 
				RESYNC_REQUEST, 
				AVAILS_REMOTEJOBREQUEST, 
				AVAILS_CALCULATION, 
				AVAILS_NOTIFICATION,
				LARGE_AVAILS_CALCULATION, 
				EXCEL_JOB,
				AVAILS_FRRR_REQUEST,
				AVAILS_FRRR_PRODUCT);

		LOGGER.info(
				" LOADED KAFKA GROUP NAMES ARE:"
				+"\n\t\t\t\tclientRequestProcessorGroupName...........{}" 
				+"\n\t\t\t\tbulkLoadRequestGroupName..................{}" 
				+"\n\t\t\t\tdedupProcessorGroupName...................{}"
				+"\n\t\t\t\tlockingQueueProcessorGroupName............{}"
				+"\n\t\t\t\tconflictCalculationProcessorGroupName.....{}"
				+"\n\t\t\t\tconflictRollupProcessorGroupName..........{}"
				+"\n\t\t\t\tconflictCheckNodeManagerGroupName.........{}"
				+"\n\t\t\t\trightsInChangeProcessorGroupName..........{}"
				+"\n\t\t\t\tcancelCheckoutRequestProcessorGroupName...{}"
				+"\n\t\t\t\tcheckoutRequestProcessorGroupName.........{}"
				+"\n\t\t\t\tcommitRequestProcessorGroupName...........{}"
				+"\n\t\t\t\tconflictCheckRequestProcessorGroupName....{}"
				+"\n\t\t\t\tconflictCheckResultProcessorGroupName.....{}"
				+"\n\t\t\t\tmessageOffsetProcessorGroupName...........{}"
				+"\n\t\t\t\tnodeStatusProcessorGroupName..............{}"
				+"\n\t\t\t\tresyncProcessorGroupName..................{}"
				+"\n\t\t\t\trollupResultProcessorGroupName............{}"
				+"\n\t\t\t\trollupRequestProcessorGroupName...........{}"
				+"\n\t\t\t\tcancelCheckoutProcessorGroupName..........{}"
				+"\n\t\t\t\tcarveoutConflictProcessorGroupName........{}"
				+"\n\t\t\t\tconflictCheckResultProcessorGroupName.....{}"
				+"\n\t\t\t\tdisplayRequestProcessorGroupName..........{}"
				+"\n\t\t\t\tclientResponseProcessorGroupName..........{}"
				+"\n\t\t\t\tconflictResponseProcessorGroupName........{}"
				+"\n\t\t\t\tavailsJobStatusProcessorGroupName.........{}"
				+"\n\t\t\t\tavailsRemoteJobProducerProcessorGroupName.{}"
				+"\n\t\t\t\tavailsFRRRProductGroupName................{}"
				+"\n\t\t\t\tavailsFRRRRequestGroupName................{}"
				+"\n\t\t\t\tavailsRemoteJobProcessorGroupName.........{}"
				+"\n\t\t\t\tavailsExcelJobProcessorGroupName..........{}"
				+"\n\t\t\t\tavailsLargeAvailsJobProcessorGroupName....{}"

				,
				CC_CLIENTREQUESTPROCESSOR_GROUPNAME,
				CC_BULKLOADREQUEST_GROUPNAME,
				CC_DEDUPPROCESSOR_GROUPNAME,
				CC_LOCKINGQUEUEPROCESSOR_GROUPNAME,
				CC_CONFLICTCHECKNODEMANAGER_GROUPNAME,
				CC_RIGHTSINCHANGEPROCESSOR_GROUPNAME,
				CC_CANCELCHECKOUTREQUESTPROCESSOR_GROUPNAME,
				CC_CHECKOUTREQUESTPROCESSOR_GROUPNAME,
				CC_COMMITREQUESTPROCESSOR_GROUPNAME,
				CC_CONFLICTCHECKREQUESTPROCESSOR_GROUPNAME,
				CC_CONFLICTCHECKRESULTPROCESSOR_GROUPNAME,
				CC_MESSAGEOFFSETPROCESSOR_GROUPNAME,
				CC_NODESTATUSPROCESSOR_GROUPNAME,
				CC_RESYNCPROCESSOR_GROUPNAME,
				CC_ROLLUPRESULTPROCESSOR_GROUPNAME,
				CC_ROLLUPREQUESTPROCESSOR_GROUPNAME,
				CC_CANCELCHECKOUTPROCESSOR_GROUPNAME,
				CC_CARVEOUTCONFLICTPROCESSOR_GROUPNAME,
				CC_CONFLICTCHECKRESULTSPROCESSOR_GROUPNAME,
				CC_DISPLAYREQUESTPROCESSOR_GROUPNAME,
				CC_CONFLICTRESPONSEPROCESSOR_GROUPNAME,
				AVAILS_JOBSTATUSPROCESSOR_GROUPNAME,
				AVAILS_REMOTEJOBPRODUCERPROCESSOR_GROUPNAME,
				AVAILS_FRRRPRODUCT_GROUPNAME,
				AVAILS_FRRRREQUEST_GROUPNAME,
				AVAILS_REMOTEJOBPROCESSOR_GROUPNAME,
				AVAILS_EXCELJOBPROCESSOR_GROUPNAME,
				AVAILS_LARGEAVAILSJOBPROCESSOR_GROUPNAME
				);
	}
}
