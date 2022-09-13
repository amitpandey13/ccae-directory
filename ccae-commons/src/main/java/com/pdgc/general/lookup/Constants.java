package com.pdgc.general.lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;

// TODO Vishal Raut, May 15, 2017: Change it to be client independent - should be handled in ETL
public class Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
    

    public static final String ENV_PROPERTY_FILE_DIRECTORY_PATH = "CCAE_PROPERTY_FILE_DIR_PATH";
    public static final String CONSTANTS_PROPERTY_FILE_NAME = "Constants.properties";

    public static Level ROOT_LOG_LEVEL;

    // NFS credentials
    public static String NFS_USER;
    public static String NFS_HOST;

    // rsync args
    public static String ASSEMBLER_DIR;
    public static String XLSX_LOCAL_DIR;
    public static String REMOTE_DIR; 

    // Build xlsx locally using shell commands to copy files from nfs to local
    public static Boolean LOCAL_XLSX_CREATION;      // Toggle if you want to build locally by copying files from the nfs to local
    public static Boolean LOCAL_XLSX_CREATION_TEST; // Used only if you're running a local integration test without an nfs

    // Cache Update/Client Maintenance
    // To refresh or not?
    public static boolean REFRESH_DATABASE;
    public static boolean REFRESH_CONSTANTS;

    public static LocalDate EPOCH;
    public static LocalDate PERPETUITY;
    public static LocalDate TBA_DATE;

    public static Term TERM_EPOCH_TO_PERPETUITY;
    public static Long TEST_SLEEP_TIME;

    // Right Source Type
    public static int SOURCE_TYPE_ID_DEAL = 27;
    public static int SOURCE_TYPE_ID_DISTRIBUTION = 28;
    public static int SOURCE_TYPE_ID_RESTRICTION = 45;
    public static int SOURCE_TYPE_ID_CARVEOUT = 46;
    public static int SOURCE_TYPE_ID_PLAYOFF = 47;
    public static int SOURCE_TYPE_ID_SALES_PLAN = 48;
    public static int SOURCE_TYPE_ID_PRODUCT_RESTRICTION = 49;
    public static Collection<Integer> SOURCE_TYPES_CORPORATE;
    public static Collection<Integer> SOURCE_TYPES_RESTRICTION;

    // Right Type
    public static long RIGHT_TYPE_ID_NONEXCLUSIVE_LICENSE = 1;
    public static long RIGHT_TYPE_ID_HOLDBACK = 2;
    public static Long RIGHT_TYPE_ID_SALES_PLAN_WINDOW = 15L;
    public static long RIGHT_TYPE_ID_SALES_PLAN_BLOCK = 16;
    public static long RIGHT_TYPE_ID_SALES_PLAN_AS_DIST_RIGHTS = 19; // Acts as distribution right when paired with NRE restriction
    public static long RIGHT_TYPE_ID_PRELIMINARY_RIGHTS = 10037;
    public static long RIGHT_TYPE_ID_RESTRICTION_TBA_START = 12001;
    public static long RIGHT_TYPE_ID_RESTRICTION_TBA_END = 12002;
    public static long RIGHT_TYPE_ID_RESTRICTION_NRE = 12003;
    public static long RIGHT_TYPE_ID_CATCHUP_ROLLING = 1001;
    public static long RIGHT_TYPE_ID_CATCHUP_BLOCK_ROLLING = 1101;
    public static long RIGHT_TYPE_ID_PROMOTIONAL_GRANT = 7;
    public static long RIGHT_TYPE_ID_PROMOTIONAL_BLACKOUT = 8;
    public static Collection<Long> RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK = new HashSet<>(); //TODO: consolidate whether or not to base distribution ignores using this or the corp type...
    public static Collection<String> CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON = new HashSet<>();
    public static Collection<Long> RIGHT_TYPES_WITH_LICENSE = new HashSet<>();
    
    public static long RIGHT_TYPE_ID_MUSIC_DISTRIBUTION = 20;
    public static long RIGHT_TYPE_ID_DISNEY_DISTRIBUTION = 21;

    public static int SALESWINDOW_UPDATE_BATCH_SIZE;
    public static final int SALES_PLAN_WINDOW_DISTRIBUTION_OWNER_ID = 1;
    public static final int SALES_PLAN_WINDOW_PRODUCT_HIERARCHY_ID = 102;
    
    public static final long EXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID = -1;
    public static final long NONEXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID = -2;
    public static final long IGNORED_CORP_AVAIL_RIGHT_TYPE_ID = -3;
    public static final long MUSIC_AVAILS_RIGHT_TYPE_ID = -4;

    public static String SALES_PLAN_CURRENT_LIFECYCLE_ID = "First-Run";

    public static Territory WORLD;
    public static Language ALL_LANGUAGES;
    public static Media ALL_MEDIA;
    
    // Media
    public static long MEDIA_ID_THEATRICAL = 9;

    public static int AGG_NAME_PRODUCT_INCLUDING_THRESHOLD;
    public static int AGG_NAME_PRODUCT_EXCLUDING_THRESHOLD;
    public static int AGG_NAME_MEDIA_INCLUDING_THRESHOLD;
    public static int AGG_NAME_MEDIA_EXCLUDING_THRESHOLD;
    public static int AGG_NAME_TERRITORY_INCLUDING_THRESHOLD;
    public static int AGG_NAME_TERRITORY_EXCLUDING_THRESHOLD;
    
    public static long INITIAL_STATUS;
    public static long PRESENTED_STATUS;
    public static long RESERVED_STATUS;
    public static long FINAL_STATUS;
    public static long RIGHT_STRAND_STATUS_CANCELLED;
    public static long INACTIVE_STATUS;
    public static long  ESTIMATED_STATUS;
    public static long  TENTATIVE_STATUS;
    public static long  FIRM_STATUS;
    public static Collection<Long> NO_IMPACT_AVAILS_DEAL_STATUSES;  // Deals with these statuses do not impact or appear in avails results

    public static int MAX_CUSTOMERS_CARVEOUT_TYPE;
    public static int ALLOWED_NUM_CUSTOMERS_CARVEOUT_TYPE;

    public static int LOOKUP_TYPE_CUSTOMER_TYPE;
    public static int LOOKUP_TYPE_CONFLICT_SEV;
    public static int LOOKUP_TYPE_CUSTOMER_LIMIT_TYPE;
    public static int LOOKUP_TYPE_OVERRIDE_TYPE_GENERAL;
    public static int LOOKUP_TYPE_OVERRIDE_TYPE_RIGHTS;
    public static int LOOKUP_TYPE_OVERRIDE_TYPE_LICENSE;
    public static Collection<Integer> LOOKUP_TYPE_OVERRIDE_TYPES;

    // Boolean db values
    public static final String TRUE_INT = "1";
    public static final String FALSE_INT = "0";
    public static final String TRUE_WORD = "true";
    public static final String FALSE_WORD = "false";

    // Season Type
    public static final long SEASON_TYPE_BROADCAST = 0;
    public static final long SEASON_TYPE_SALES = 1;

    // Product Genre
    public static final long GENRE_TYPE_GENRE = 1100;
    public static final long GENRE_TYPE_THEME = 1101;

    public static int maxDeleteAttempts;
    
    // Avails Workbook - Include entries whose Start Date is after the requested Latest Start Date
    public static boolean INCLUDE_AVAILS_AFTER_LATEST_START_DATE;

    // run status
    public static final int RUN_STATUS_INITIALIZED = 58;
    public static final int RUN_STATUS_IN_PROGRESS = 59;
    public static final int RUN_STATUS_WORKBOOK_GENERATING = 62;
    public static final int RUN_STATUS_FINISHED = 63;
    public static final int RUN_STATUS_ERROR = -1;
    public static final int RUN_STATUS_CANCELED = -2;
    public static final int RUN_STATUS_TIME_OUT = -51;

    // Job Statuses
    public static final int JOB_STATUS_IDLE = 0;
    public static final int JOB_STATUS_INITIALIZED = 58; // When the app first sees a request
    public static final int AVAILS_JOB_INITIALIZING = 591;
    public static final int AVAILS_JOB_STATUS_READY_TO_RUN = 592;// job is parsed sucessfully on node
                                                                    // side, ready to be
                                                                    // run
    public static final int AVAILS_JOB_STATUS_CALCULATED_AND_ROLLEDUP = 593; // job has been
                                                                                // calculated and result
                                                                                // is
                                                                                // generated on node side
    public static final int AVAILS_JOB_STATUS_RECEIVING_XML_RESULTS = 594; // job has finished
                                                                            // processing on the node
                                                                            // side and has been sent.
                                                                            // (not received
                                                                            // yet)
    public static final int AVAILS_JOB_STATUS_ALL_XML_RESULTS_COLLECTED = 595; // #job has received
                                                                                // all xml results
    public static final int JOB_STATUS_FINISHED = 63; // calculation and results are finished and
                                                        // calculated and has
                                                        // been received by the app side
    public static final int JOB_STATUS_ERROR = -1; // any general error that prevents completion of
                                                    // the job.
    public static final int JOB_STATUS_TIME_OUT = -2;// job timeout
    public static final int JOB_STATUS_FATAL_ERROR = -4; // job couldn't gracefully clean itself up
                                                            // due to inability to
                                                            // send to proper kafka topics
                                                            // This is after the second try failed and it
                                                            // then had to
                                                            // run lower level methods to handle it
                                                            // itself
                                                            // This is an extremely fatal error and
                                                            // potentially caused
                                                            // many other requests to fail in a domino -
                                                            // effect
                                                            // We cleaned this up and all jobs blocked on
                                                            // this to
                                                            // prevent blockages. TODO: time out manager
                                                            // will handle
                                                            // the recursive cleanup in the future
    public static final int JOB_STATUS_SHUT_DOWN = 4; // final state when thread ends.
    public static final int JOB_STATUS_IN_PROGRESS = 59;
    public static final int JOB_STATUS_BEFORE_RUN = 60;
    public static final int JOB_STATUS_AFTER_RUN = 61;
    public static final int JOB_STATUS_PROCESSED_SUCCEED = 62;
    public static final int JOB_STATUS_PROCESSED_FAILED = -3;
    public static final List<Integer> JOB_STATUS_ERRORS = Arrays.asList(JOB_STATUS_PROCESSED_FAILED,
            JOB_STATUS_FATAL_ERROR, JOB_STATUS_ERROR, JOB_STATUS_TIME_OUT, RUN_STATUS_TIME_OUT, RUN_STATUS_ERROR);

    // Wait Times
    public static final int WAIT_SECONDS_CC_OR_ROLLUP_BLOCKING_COMMIT = 30;
    public static final int COMMIT_BLOCKED_SLEEP_TIME_BETWEEN_CHECKS_IN_SECONDS = 5;
    public static Double CC_WAIT_FOR_ROLLUP_ADD_TO_LOCKING_QUEUE_MAX_TIME_IN_SECONDS = 20d;
    public static int CC_WAIT_FOR_ROLLUP_ADD_TO_LOCKING_QUEUE_RETRY_INTERVAL_IN_MS = 500;
    public static int CC_WAIT_FOR_ROLLUP_MAX_RETRIES;

    // Timer Constants
    public static long TIME_OUT_EXPIRATION_MS;
    public static long TIME_OUT_KAFKA_BUFFER_MS;
    public static int AVAILS_REMOTE_JOB_EXPIRATION_SEC;
    public static int AVAILS_REMOTE_JOB_BUFFER;
    public static int MONITOR_TIME_DELAY;
    public static int CC_REQUEST_MONITOR;

    // File Manipulation
    public static String PATH_TO_BASH;
    public static String PATH_TO_COMPLETED_PRODUCTS;
    public static String PATH_TO_PREVIOUS_NET_RESULTS;
    public static Boolean AVAILS_USE_SHELL_FILE_COUNT;
    public static Long LARGE_JOB_CLEANUP_SLEEP_TIME;
    public static Long LARGE_JOB_CLEANUP_DELETE_ATTEMPTS;
    public static Integer LARGE_AVAIL_FILE_FETCH_RETRY_COUNT;
    public static Long LARGE_AVAIL_FILE_FETCH_RETRY_SLEEP_TIME;

    // mapper of Jackson
    public static ObjectMapper mapper;
    
    
    // Default Customer ID for Deals
    public static Long CUSTOMER_DEFAULT_ID;

    // Commit Request Configuration
    public static final int COMMIT_REQUEST_OVERRIDE_ALL = 1; // RI and RO conflicts without overrides
    public static final int COMMIT_REQUEST_OVERRIDE_RIGHTSIN_ONLY = 2; // RI conflicts without
                                                                        // overrides
    public static final int COMMIT_REQUEST_OVERRIDE_DEAL_ONLY = 3; // RO conflicts without overrides
    public static final int COMMIT_REQUEST_OVERRIDE_NONE = 4; // All conflicts regardless of overrides
    public static final int COMMIT_REQUEST_STATUS_RESOLVED = 5; // Only allow commit on conflicts that
                                                                // have Resolved status (3)
    public static final int COMMIT_REQUEST_STATUS_NEEDS_ACK_RESOLVED = 6; // Allow commit on conflicts
                                                                            // that have either Needs
                                                                            // Acknowledgement (2) or
                                                                            // Resolved status

    // Conflict Types
    public static int NO_RIGHTS_IN_CONFLICT;
    public static int DIFFERENT_DEAL_CONFLICT;

    // Num Threads
    public static int CONFLICT_CALCULATION_NODE_THREAD_COUNT;
    public static int CONFLICT_ROLLUP_NODE_THREAD_COUNT;
    public static int RIGHTS_IN_CHANGE_NODE_THREAD_COUNT;
    public static int CARVEOUT_CHECK_NODE_THREAD_COUNT;

    // Recovering unfinished requests
    public static boolean RECOVER_REQUESTS;

    // Schema names
    public static String CHECKED_IN;
    public static String CHECKED_OUT;

    // PERP or TBA
    public static final String PERPETUITY_STR = "P";
    public static final String TBA_STR = "TBA";

    // Time out manager can now be turned on and off using the constants properties
    // file
    public static boolean LOCKING_QUEUE_MONITOR;
    // This unit must be set like this:
    // 1 millisecond
    // 1 second
    // Etc... must adhere to the PostgreSQL interval pattern
    public static String TIME_OUT_UNIT;

    // CONFLICT CHECK read replica retry pattern - no back-off
    // Time to wait for a request's license to enter the read-only replica
    public static long VERIFY_SYNCED_READ_REPLICA_WAIT_TIME;
    // Num of times to wait for a request's license to enter the read-only replica
    public static long VERIFY_SYNCED_READ_REPLICA_NUM_RETRIES;

    // AVAILS read replica retry pattern
    // If query returns null, delay before next attempt (to account for read-only replica db latency)
    // using millisecond multiplier for exponential back-off algorithm (2^attempts * backoffMillis)
    public static int AVAILS_RR_DB_RETRY_BACKOFF_MILLIS;
    // Max number of times to attempt query from read replica database
    public static int AVAILS_RR_DB_RETRY_MAX_ATTEMPTS;

    // Constants needed for RightsInChangeProcessor
    public static final long DEFAULT_USER_ID = 0;
    public static final long DEFAULT_RUN_ID = 0;

    // Constants for waiting
    public static int WAIT_LOCKING_QUEUE_TO_FINISH_TILL_NUM_REQUESTS;
    public static long WAIT_LOCKING_QUEUE_TO_FINISH_SLEEP_TIME;
    public static long DEDUP_PROCESSOR_SLEEP_TIME;

    public static long REPEAT_RECOVERABLE_CC_ERROR;

    // Constant for waiting before listing connectors in resync-deals tool
    public static int WAIT_BEFORE_LISTING_CONNECTORS_WHILE_RESYNC;

    public static int CHECK_ALL_CCREQUESTS_HAVE_FINISHED_AFTER_RESYNC_SLEEP_TIME_MS;
    public static int MAX_WAIT_FOR_ALL_CCREQUESTS_TO_FINISH_AFTER_RESYNC_MS;
    
    // NUM Threads for processors to use
    public static int LOCKING_QUEUE_NUM_THREADS;
    public static int CC_RESULT_NUM_THREADS;
    public static int CC_CONFLICT_RESPONSE_NUM_THREADS;
    public static int ROLLUP_RESULT_NUM_THREADS;
    public static int NODE_STATUS_NUM_THREADS;
    public static int MESSAGE_OFFSET_STATUS_NUM_THREADS;

    public static int DISPLAY_REQUEST_NUM_THREADS;
    public static int COMMIT_REQUEST_NUM_THREADS;
    public static int CHECK_OUT_REQUEST_NUM_THREADS;
    public static int CANCEL_CHECKOUT_REQUEST_NUM_THREADS;
    public static int DELETE_REQUEST_NUM_THREADS;
    public static int RESYNC_REQUEST_NUM_THREADS;
    
    public static  Collection<Integer> CUSTOMER_COUNT_CARVEOUT_TYPES = new HashSet<>();


    public static String RIGHTS_IN_BUSINESS_UNITS;
    public static Collection<String> DEFAULT_FILTER_BUSINESS_UNIT_IDS;

    // Bulk controller
    public static boolean BLOCK_BULK_LOAD;

    // Client Request skip cache
    public static boolean BYPASSCACHE;
    public static Set<Long> BUSINESS_UNITS_TO_CC;
    public static Long MAX_CONLFLICT_CHECK_CACHE_SIZE;
    public static Long CONFLICT_CHECK_CACHE_EXPIRE_AFTER_WRITE_SECONDS;

    // Replay only
    public static String REPLAY_REQUEST_TYPES;

    // Kafka Config
    // Cache timer
    public static long CACHE_UPDATE_SLEEP_INTERVAL_MS; // How long to wait before re-updating cache
    public static long CACHE_POLL_TIME_MILLISECONDS;
    public static Integer CACHE_MAX_POLL_RECORDS; 
    
    public static String BULK_LOAD_REQUEST_MAX_POLL_RECORDS;
    public static long BULK_LOAD_POLL_TIME_MILLISECONDS;
    public static long CLIENT_REQUEST_EMPTY_POLL_ATTEMPTS;
    public static long BULK_LOAD_DEDUP_POLL_TIME_MILLISECONDS;

    public static String CLIENT_REQUEST_MAX_POLL_RECORDS;
    public static long CLIENT_POLL_TIME_MILLISECONDS;
    public static long CC_POLL_TIME_MILLISECONDS;
    public static long ROLLUP_POLL_TIME_MILLISECONDS;
    
    // First Run/Rerun 
    public static long FRRR_REQUEST_SLEEP_INTERVAL_SECONDS; // How long to wait before polling from request topic 
    public static long FRRR_REQUEST_POLL_TIME_MILLISECONDS; 
    public static String FRRR_REQUEST_MAX_POLL_RECORDS;
    public static long FRRR_REQUEST_CACHE_EVICTION_TIME_SECONDS;    // How long after last access to cache to evict  
    public static long FRRR_REQUEST_CACHE_EVICTION_MAX_SIZE;    // Max number of entries allowed in cache before eviction 
    public static long FRRR_REQUEST_CACHE_STATS_LOG_HIT_COUNT;  // How many hit counts until we print stats 
    public static long FRRR_REQUEST_CACHE_STATS_LOG_MISS_COUNT;     // How many miss counts until we print stats 

    
    public static long FRRR_PRODUCT_SLEEP_INTERVAL_SECONDS; // How long to wait before re-updating productfirstrun table
    public static long FRRR_PRODUCT_POLL_TIME_MILLISECONDS; 
    public static String FRRR_PRODUCT_MAX_POLL_RECORDS;

    public static long CARVEOUT_POLL_TIME_MILLISECONDS;
//  #The maximum delay between invocations of poll() when using consumer group management. This places an upper bound on the amount of time 
//  #that the consumer can be idle before fetching more records. If poll() is not called before expiration of this timeout, then the consumer is 
//  #considered failed and the group will rebalance in order to reassign the partitions to another member.
    public static String ROLLUP_MAX_POLL_INTERVAL_MS;
    
//  #The expected time between heartbeats to the consumer coordinator when using Kafka's group management facilities. 
//  #Heartbeats are used to ensure that the consumer's session stays active and to facilitate rebalancing when new consumers join or leave the group. 
//  #The value must be set lower than session.timeout.ms, but typically should be set no higher than 1/3 of that value. 
//  #It can be adjusted even lower to control the expected time for normal rebalances
    public static String ROLLUP_HEARTBEAT_INTERVAL_MS;
    
//  #The timeout used to detect consumer failures when using Kafka's group management facility. The consumer sends periodic heartbeats to 
//  #indicate its liveness to the broker. If no heartbeats are received by the broker before the expiration of this session timeout, 
//  #then the broker will remove this consumer from the group and initiate a rebalance. Note that the value must be in the allowable range as 
//  #configured in the broker configuration by group.min.session.timeout.ms and group.max.session.timeout.ms.
    public static String ROLLUP_SESSION_TIMEOUT_MS;
    
//  The maximum number of records returned in a single call to poll().
    public static String ROLLUP_MAX_POLL_RECORDS;
    
    public static String CC_MAX_POLL_INTERVAL_MS;
    public static String CC_HEARTBEAT_INTERVAL_MS;
    public static String CC_SESSION_TIMEOUT_MS;
    public static String CC_MAX_POLL_RECORDS;

    public static Long CC_NODE_TIMEOUT_MS;
    
    public static String RESPONSE_MAX_POLL_INTERVAL_MS;
    public static String RESPONSE_HEARTBEAT_INTERVAL_MS;
    public static String RESPONSE_SESSION_TIMEOUT_MS;
    public static String RESPONSE_MAX_POLL_RECORDS;
    
    public static String UNLOCK_MAX_POLL_INTERVAL_MS;
    public static String UNLOCK_HEARTBEAT_INTERVAL_MS;
    public static String UNLOCK_SESSION_TIMEOUT_MS;
    public static String UNLOCK_MAX_POLL_RECORDS;

    public static String CARVEOUT_MAX_POLL_INTERVAL_MS;
    public static String CARVEOUT_HEARTBEAT_INTERVAL_MS;
    public static String CARVEOUT_SESSION_TIMEOUT_MS;
    public static String CARVEOUT_MAX_POLL_RECORDS;
    public static boolean CARVEOUT_CHECK_FOR_CUSTOMER_COUNT_IS_ACTIVE;
    
    public static int JOB_PRODUCT_LEAF_THRESHOLD;
    public static int REMOTE_JOB_FEATURE_BATCH_SIZE_LARGEST;
    public static int REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_LARGEST;
    public static int REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_SMALLEST;
    public static int REMOTE_JOB_FEATURE_BATCH_SIZE_SMALLEST;
    
    public static int REMOTE_JOB_MTL_COUNT_LOWEST;
    public static int REMOTE_JOB_MTL_COUNT_MIDDLE;
    public static int REMOTE_JOB_MTL_COUNT_HIGHEST;
    
    public static long JOB_MANAGER_WAIT_INTERVAL_MS;
    public static long LARGE_AVAILS_CHECK_FILE_WAIT_INTERVAL_MS;
        
    //Business unit filters
    public static String DRO_FILTER;
    public static String BUSINESS_UNITS_TO_MERGE_RS; //Comma delimited list of business units to rollup across sourcedetails.
    
    public static String CONTRACT_DISPLAY_SOURCE;
    public static String RESERVATION_DISPLAY_SOURCE;
    public static String BASELINE_WORKFLOW_DISPLAY_SOURCE;

    // Exclude deals and titles that match for rightsInChanges. This is only temporary until we interpret rightStrands better
    public static Set<String> RIGHTS_IN_CHANGE_EXCLUDE_DEALS;
    public static Set<String> RIGHTS_IN_CHANGE_EXCLUDE_TITLES;
    
    public static Set<String> CLIENT_EXCLUDE_DEALS;
    public static Set<String> CLIENT_EXCLUDE_TITLES;
    
    public static Integer EXCEL_CELL_CHAR_MAX;
    
    // Constants to resync multiple deals
    public static String RESYNC_DEAL_MODE;
    public static Integer RESYNC_DEAL_MULTIPLE_COUNT;
    
    public static String METRIC_TYPE_BOX_OFFICE = "BOX";
    public static String METRIC_TYPE_ADMISSIONS = "ADM";
    public static String METRIC_TYPE_SCREENS = "SCR";
    public static String METRIC_TYPE_RATING = "RATING";
    
    // Customer ID to represent lack of customer
    public static Long NULL_CUSTOMER_ID;
    
    public static Collection<Long> SALESPLAN_SET_MEDIAS;
    
    public static boolean CALC_ALL_LICENSED_CUSTOMERS;

    // Choose to calculate Sales Plans as distribution rights 
    public static boolean CALC_SALES_PLANS_AS_DISTR_RIGHTS;

    public static String SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_MEDIA_TERRITORY = "1";
    public static String SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_TERRITORY = "2";
    public static String SALES_WINDOW_SELECT_FROM_LIST = "3";
    
    public static int ALL_INFORMATION_CODE_EXIST = 1;
    public static int ANY_INFORMATION_CODE_EXIST = 2;
    public static int NON_OF_INFORMATION_CODE_EXIST = 3;

    public static Integer CONFLICT_RESPONSE_CLIENT_RETRY_NUM;
    public static Long CONFLICT_RESPONSE_CLIENT_RETRY_BACKOFF_MS;
    public static String CONFLICT_RESPONSE_AUTH_TOKEN_REQUEST_URL;
    public static String CONFLICT_RESPONSE_AUTH_GRANT_TYPE;
    public static String CONFLICT_RESPONSE_AUTH_CLIENT_ID;
    public static String CONFLICT_RESPONSE_AUTH_CLIENT_SECRET;
    public static String CONFLICT_RESPONSE_AUTH_USERNAME;
    public static String CONFLICT_RESPONSE_AUTH_PASSWORD;
    
    // Hierarchy Ids
    public static long PRODUCTION_HIERARCHY_ID;
    public static long SALES_HIERARCHY_ID;
    public static long BROADCAST_HIERARCHY_ID;
    public static long HE_SALES_HIERARCHY_ID;

    // Avails UI Errored Products count of titles to display by name 
    public static int AVAILS_UI_NUM_ERROR_PRODUCTS_MAX;
    
    public static Set<Long> BUSINESS_UNITS_LOCAL_RELEASE_DATE;  // default 400 (Prophet)
    public static Set<Long> MEDIAS_LOCAL_HE_DVD_RELEASE_DATE;   // default 3,10
    public static Set<Long> MEDIAS_LOCAL_TV_RELEASE_DATE;       // default 2,7,8
    public static Set<Long> MEDIAS_LOCAL_THEA_RELEASE_DATE;     // default theatrical (9)

    // Conflict Rollup Processor - Last Process Time Cache max size 
    public static Long CC_ROLLUP_LAST_PROCESS_TIME_CACHE_MAX_SIZE;

    public static Set<Integer> RESTRICTION_CODES_FOR_LEGEND; // 141,142,143,144
    public static final int RESTRICTION_CODES_AVAILABILITY_ID_FATAL = 4;
    public static final int RESTRICTION_CODES_AVAILABILITY_ID_CONDITIONAL = 3;
    public static final int RESTRICTION_CODES_REQUESTED_RIGHT_ID = -2;

    // User Permission ExternalTypeId 
    public static long PERMISSION_VIEW_LICENSE_FEES_TYPE_ID;
    public static long PERMISSION_CUSTOMER_TYPE_ID;
    
    // User Role roletypeid - Admin 
    public static long ROLE_ADMIN_TYPE_ID;
    
    public static final String DISTR_RIGHTS_NOT_CHECKED_STR = "Dist Rights Not Checked";
    public static final String NO_DISTR_RIGHTS = "No distribution rights";
    
    public static final Set<String> DATE_STRINGS = new ImmutableSet.Builder<String>().add(DISTR_RIGHTS_NOT_CHECKED_STR, NO_DISTR_RIGHTS, PERPETUITY_STR, TBA_STR).build();
    
    public static final Long TUSCANY_BUSINESS_UNIT_ID = 810L;
    
    public static final Long US_INCLUDING_PALAU_TERRITORY_ID = 509L;
    public static final String US_INCLUDING_PALAU_ORIGIN_COUNTRY_NAME = "United States";
    public static final Long US_EXCLUDING_PALAU_TERRITORY_ID = 27183L;
    public static final String US_EXCLUDING_PALAU_ORIGIN_COUNTRY_NAME = "United States excluding Palau";
    
    // limit is set to 1040000 we provide an 8000 row buffer in case we want to print things that's not snippet related
    // This limit is the .xlsx limitation
    public static Integer EXCEL_ROW_LIMIT; // the real limit is: 1048576... put a buffer for any extra styling we decided
    
    //Controls whether or not we want expired conflicts to show up in the db. 
    //All CC-related functions should agree whether or not produce these or delete them
    public static boolean NEEDS_EXPIRED_CONFLICTS = true;
    
    /**
     * Load the property file object from the local file system
     * @param fileName
     * @return
     */
    public static File retrieveExternalPropertyFile(String fileName) {
        Objects.requireNonNull(fileName, "fileName can't be null");
        String fileDirPath = System.getenv(ENV_PROPERTY_FILE_DIRECTORY_PATH);
        String filePath;
        if (StringUtils.isBlank(fileDirPath)) {
            filePath = "." + File.separator + fileName;
        } else {
            filePath = fileDirPath + File.separator + fileName;
        }
        LOGGER.debug("Load {} property file from {}", fileName, filePath);
        return new File(filePath);
    }

    /**
     * This method instantiates Constants and any shared variables (ObjectMapper)
     * @throws Exception
     *
     * @throws IOException
     */ 
    public static void instantiateConstants() {

        Properties prop = new Properties();

        File file = retrieveExternalPropertyFile(CONSTANTS_PROPERTY_FILE_NAME);
        if (file.exists()) {
            // The property file at the same location as jar takes higher precedence
            try (final FileInputStream fileInput = new FileInputStream(file)) {
                prop.load(fileInput);
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        } else {
            LOGGER.debug("Constants file is not found at local file system. Load it from class path");
            try (final InputStream resourceAsStream = Constants.class.getClassLoader()
                                                                     .getResourceAsStream(CONSTANTS_PROPERTY_FILE_NAME)) {
                if (resourceAsStream != null) {
                    prop.load(resourceAsStream);
                }
            } catch (IOException ex) {
                LOGGER.error("Fail to load constant property file! {}", ex.getMessage());
            }
        }

        // mapper instantiation
        mapper = new ObjectMapper().registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
        mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        mapper.disable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);

        // Cache Update - to refresh or not to refresh?
        REFRESH_DATABASE = Boolean.valueOf(prop.getProperty("REFRESH_DATABASE", "true"));
        REFRESH_CONSTANTS = Boolean.valueOf(prop.getProperty("REFRESH_CONSTANTS", "true"));
        CARVEOUT_CHECK_FOR_CUSTOMER_COUNT_IS_ACTIVE = Boolean
                .valueOf(prop.getProperty("CARVEOUT_CHECK_FOR_CUSTOMER_COUNT_IS_ACTIVE", "true"));

        // EPOCH - 1900/1/1
        String[] epochStr = prop.getProperty("EPOCH", "1900/1/1").split("/");
        EPOCH = LocalDate.of(Integer.valueOf(epochStr[0]), Integer.valueOf(epochStr[1]), Integer.valueOf(epochStr[2]));

        // PERPETUITY - 9999/12/31
        String[] perpStr = prop.getProperty("PERPETUITY", "9999/12/31").split("/");
        PERPETUITY = LocalDate.of(Integer.valueOf(perpStr[0]), Integer.valueOf(perpStr[1]),
                Integer.valueOf(perpStr[2]));

        // TBA DATE - 9999/9/9
        String[] tbaStr = prop.getProperty("TBA_DATE", "9999/9/9").split("/");
        TBA_DATE = LocalDate.of(Integer.valueOf(tbaStr[0]), Integer.valueOf(tbaStr[1]), Integer.valueOf(tbaStr[2]));

        TERM_EPOCH_TO_PERPETUITY = new Term(EPOCH, PERPETUITY);
        
        // Right Source Type
        SOURCE_TYPE_ID_DEAL = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_DEAL", "27"));
        SOURCE_TYPE_ID_DISTRIBUTION = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_DISTRIBUTION", "28"));
        SOURCE_TYPE_ID_RESTRICTION = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_RESTRICTION", "45"));
        SOURCE_TYPE_ID_CARVEOUT = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_CARVEOUT", "46"));
        SOURCE_TYPE_ID_PLAYOFF = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_PLAYOFF", "47"));
        SOURCE_TYPE_ID_SALES_PLAN = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_SALES_PLAN", "48"));
        SOURCE_TYPE_ID_PRODUCT_RESTRICTION = Integer.valueOf(prop.getProperty("SOURCE_TYPE_ID_PRODUCT_RESTRICTION", "49"));
        SOURCE_TYPES_CORPORATE = Stream.of(SOURCE_TYPE_ID_DISTRIBUTION, SOURCE_TYPE_ID_RESTRICTION, SOURCE_TYPE_ID_PRODUCT_RESTRICTION).collect(Collectors.toSet());
        SOURCE_TYPES_RESTRICTION = Stream.of(SOURCE_TYPE_ID_RESTRICTION, SOURCE_TYPE_ID_PRODUCT_RESTRICTION).collect(Collectors.toSet());

        // Right Type
        RIGHT_TYPE_ID_NONEXCLUSIVE_LICENSE = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_NON_EXCLUSIVE_LICENSE", "1"));
        RIGHT_TYPE_ID_HOLDBACK = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_HOLDBACK", "2"));
        RIGHT_TYPE_ID_SALES_PLAN_WINDOW = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_SALES_PLAN_WINDOW", "15"));
        RIGHT_TYPE_ID_SALES_PLAN_BLOCK = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_SALES_PLAN_BLOCK", "16"));
        RIGHT_TYPE_ID_SALES_PLAN_AS_DIST_RIGHTS = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_SALES_PLAN_AS_DIST_RIGHTS", "19"));
        RIGHT_TYPE_ID_PRELIMINARY_RIGHTS = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_PRELIMINARY_RIGHTS", "10037"));
        RIGHT_TYPE_ID_RESTRICTION_TBA_START = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_RESTRICTION_TBA_START", "12001"));
        RIGHT_TYPE_ID_RESTRICTION_TBA_END = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_RESTRICTION_TBA_END", "12002"));
        RIGHT_TYPE_ID_RESTRICTION_NRE = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_RESTRICTION_NRE", "12003"));
        RIGHT_TYPE_ID_CATCHUP_ROLLING = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_CATCHUP_ROLLING", "1001"));
        RIGHT_TYPE_ID_CATCHUP_BLOCK_ROLLING = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_CATCHUP_BLOCK_ROLLING", "1101"));
        RIGHT_TYPE_ID_PROMOTIONAL_GRANT = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_PROMOTIONAL_GRANT", "7"));
        RIGHT_TYPE_ID_PROMOTIONAL_BLACKOUT = Long.valueOf(prop.getProperty("RIGHT_TYPE_ID_PROMOTIONAL_GRANT", "8"));
        
        SALESWINDOW_UPDATE_BATCH_SIZE = Integer.valueOf(prop.getProperty("SALESWINDOW_UPDATE_BATCH_SIZE", "5"));

        if (prop.getProperty("RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK") != null) {
            RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK = Arrays.asList(
                prop.getProperty("RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK").split("\\s*,\\s*"))
                .stream().map(s -> new Long(s)).collect(Collectors.toSet());
        }
        if (prop.getProperty("CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON") != null) {
            CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON = Arrays.asList(
                prop.getProperty("CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON").split("\\s*,\\s*"))    
                .stream().map(s -> new String(s)).collect(Collectors.toSet());
        }
        else {
            CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON.add("Contract");
            CONTRACT_TYPES_FOR_CUSTOMER_GROUP_COMPARISON.add("Reservation");
        }
        if (prop.getProperty("RIGHT_TYPES_WITH_LICENSE") != null) {
            RIGHT_TYPES_WITH_LICENSE = Arrays.asList(prop.getProperty("RIGHT_TYPES_WITH_LICENSE").split(",")).stream().map(s -> new Long(s.trim())).collect(Collectors.toSet());
        } else {
            RIGHT_TYPES_WITH_LICENSE = Sets.newHashSet(RIGHT_TYPE_ID_NONEXCLUSIVE_LICENSE);
        }

        SALES_PLAN_CURRENT_LIFECYCLE_ID = prop.getProperty("SALES_PLAN_CURRENT_LIFECYCLE_ID", "First-Run");

        // Customer ID
        CUSTOMER_DEFAULT_ID = Long.valueOf(prop.getProperty("CUSTOMER_DEFAULT_ID", "0"));
    
        // Deal status IDs
        INITIAL_STATUS = Long.valueOf(prop.getProperty("INITIAL_STATUS", "313"));
        PRESENTED_STATUS = Long.valueOf(prop.getProperty("PRESENTED_STATUS", "314"));
        RESERVED_STATUS = Long.valueOf(prop.getProperty("RESERVED_STATUS", "315"));
        FINAL_STATUS = Long.valueOf(prop.getProperty("FINAL_STATUS", "316"));
        RIGHT_STRAND_STATUS_CANCELLED = Long.valueOf(prop.getProperty("RIGHT_STRAND_STATUS_CANCELLED", "317"));
        INACTIVE_STATUS = Long.valueOf(prop.getProperty("INACTIVE_STATUS", "318"));
        ESTIMATED_STATUS = Long.valueOf(prop.getProperty("ESTIMATED_STATUS", "319"));
        TENTATIVE_STATUS = Long.valueOf(prop.getProperty("TENTATIVE_STATUS", "320"));
        FIRM_STATUS = Long.valueOf(prop.getProperty("FIRM_STATUS", "321"));
        if (prop.getProperty("NO_IMPACT_AVAILS_DEAL_STATUSES") != null) {
            NO_IMPACT_AVAILS_DEAL_STATUSES = new HashSet<Long>(); 
            for (String status : prop.getProperty("NO_IMPACT_AVAILS_DEAL_STATUSES").split(",")) {
                NO_IMPACT_AVAILS_DEAL_STATUSES.add(Long.valueOf(status.trim())); 
            }
        } else {
            NO_IMPACT_AVAILS_DEAL_STATUSES = Stream.of(INITIAL_STATUS, PRESENTED_STATUS, RIGHT_STRAND_STATUS_CANCELLED).collect(Collectors.toSet());
        }

        // World Territory
        WORLD = new Territory(Long.valueOf(prop.getProperty("WORLD_NUM", "537")), prop.getProperty("WORLD_NAME", "Worldwide (WW) Territory"),
                TerritoryLevel.OTHER);

        // All Languages
        ALL_LANGUAGES = new Language(Long.valueOf(prop.getProperty("LANGUAGE_NUM", "2")), prop.getProperty("LANGUAGE_NAME", "All"));

        // All Media
        ALL_MEDIA = new Media(Long.valueOf(prop.getProperty("MEDIA_NUM", "16")), prop.getProperty("MEDIA_NAME", "All Media"));

        // Theatrical Media
        MEDIA_ID_THEATRICAL = Long.valueOf(prop.getProperty("MEDIA_ID_THEATRICAL", "9"));
        
        // Aggregate Namer thresholds
        AGG_NAME_PRODUCT_INCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_PRODUCT_INCLUDING_THRESHOLD", "2"));
        AGG_NAME_PRODUCT_EXCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_PRODUCT_EXCLUDING_THRESHOLD", "5"));
        AGG_NAME_MEDIA_INCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_MEDIA_INCLUDING_THRESHOLD", "10"));
        AGG_NAME_MEDIA_EXCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_MEDIA_EXCLUDING_THRESHOLD", "10"));
        AGG_NAME_TERRITORY_INCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_TERRITORY_INCLUDING_THRESHOLD", "10"));
        AGG_NAME_TERRITORY_EXCLUDING_THRESHOLD = Integer.valueOf(prop.getProperty("AGG_NAME_TERRITORY_EXCLUDING_THRESHOLD", "10"));

        MAX_CUSTOMERS_CARVEOUT_TYPE = Integer.valueOf(prop.getProperty("MAX_CUSTOMERS_CARVEOUT_TYPE", "4"));
        ALLOWED_NUM_CUSTOMERS_CARVEOUT_TYPE = Integer.valueOf(prop.getProperty("ALLOWED_NUM_CUSTOMERS_CARVEOUT_TYPE", "5"));

        LOOKUP_TYPE_CUSTOMER_TYPE = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_CUSTOMER_TYPE", "11"));
        LOOKUP_TYPE_CONFLICT_SEV = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_CONFLICT_SEV", "14"));
        LOOKUP_TYPE_CUSTOMER_LIMIT_TYPE = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_CUSTOMER_LIMIT_TYPE", "17"));
        LOOKUP_TYPE_OVERRIDE_TYPE_GENERAL = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_OVERRIDE_TYPE_GENERAL", "21"));
        LOOKUP_TYPE_OVERRIDE_TYPE_RIGHTS = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_OVERRIDE_TYPE_RIGHTS", "29"));
        LOOKUP_TYPE_OVERRIDE_TYPE_LICENSE = Integer.valueOf(prop.getProperty("LOOKUP_TYPE_OVERRIDE_TYPE_LICENSE", "30"));
        LOOKUP_TYPE_OVERRIDE_TYPES = Arrays.asList(LOOKUP_TYPE_OVERRIDE_TYPE_GENERAL, LOOKUP_TYPE_OVERRIDE_TYPE_RIGHTS, LOOKUP_TYPE_OVERRIDE_TYPE_LICENSE);

        TIME_OUT_EXPIRATION_MS = Integer.valueOf(prop.getProperty("TIME_OUT_EXPIRATION_MS", "300000"));
        TIME_OUT_KAFKA_BUFFER_MS = Integer.valueOf(prop.getProperty("TIME_OUT_KAFKA_BUFFER_MS", "10000"));
        AVAILS_REMOTE_JOB_EXPIRATION_SEC = Integer.valueOf(prop.getProperty("AVAILS_REMOTE_JOB_EXPIRATION_SEC", "1000"));
        AVAILS_REMOTE_JOB_BUFFER = Integer.valueOf(prop.getProperty("AVAILS_REMOTE_JOB_BUFFER", "1000"));
        MONITOR_TIME_DELAY = Integer.valueOf(prop.getProperty("MONITOR_TIME_DELAY", "1000"));
        CC_REQUEST_MONITOR = Integer.valueOf(prop.getProperty("CC_REQUEST_MONITOR", "1"));

        CC_WAIT_FOR_ROLLUP_MAX_RETRIES = Integer.valueOf(prop.getProperty("CC_WAIT_FOR_ROLLUP_MAX_RETRIES", "120"));
                
        CC_WAIT_FOR_ROLLUP_ADD_TO_LOCKING_QUEUE_RETRY_INTERVAL_IN_MS = Integer.valueOf(prop.getProperty("CC_WAIT_FOR_ROLLUP_ADD_TO_LOCKING_QUEUE_RETRY_INTERVAL_IN_MS", "500"));

        NO_RIGHTS_IN_CONFLICT = Integer.valueOf(prop.getProperty("NO_RIGHTS_IN_CONFLICT", "15"));
        DIFFERENT_DEAL_CONFLICT = Integer.valueOf(prop.getProperty("DIFFERENT_DEAL_CONFLICT", "9"));
        CONFLICT_CALCULATION_NODE_THREAD_COUNT = Integer.valueOf(prop.getProperty("CONFLICT_CALCULATION_NODE_THREAD_COUNT", "10"));
        CONFLICT_ROLLUP_NODE_THREAD_COUNT = Integer.valueOf(prop.getProperty("CONFLICT_ROLLUP_NODE_THREAD_COUNT", "10"));
        RIGHTS_IN_CHANGE_NODE_THREAD_COUNT = Integer.valueOf(prop.getProperty("RIGHTS_IN_CHANGE_NODE_THREAD_COUNT", "2"));
        CARVEOUT_CHECK_NODE_THREAD_COUNT = Integer.valueOf(prop.getProperty("CARVEOUT_CHECK_NODE_THREAD_COUNT", "2"));
        
        RECOVER_REQUESTS = Boolean.valueOf(prop.getProperty("RECOVER_REQUESTS", "true"));
        LOCKING_QUEUE_MONITOR = Boolean.valueOf(prop.getProperty("LOCKING_QUEUE_MONITOR", "true"));
        TIME_OUT_UNIT = prop.getProperty("TIME_OUT_UNIT", "1 millisecond");

        VERIFY_SYNCED_READ_REPLICA_WAIT_TIME = Long.valueOf(prop.getProperty("VERIFY_SYNCED_READ_REPLICA_WAIT_TIME", "3000")); // ms
        VERIFY_SYNCED_READ_REPLICA_NUM_RETRIES = Long.valueOf(prop.getProperty("VERIFY_SYNCED_READ_REPLICA_NUM_RETRIES", "2"));

        // exponential back-off algorithm is using (2^attempts * backoffMillis) to calculate retry delay
        AVAILS_RR_DB_RETRY_BACKOFF_MILLIS = Integer.valueOf(prop.getProperty("AVAILS_RR_DB_RETRY_BACKOFF_MILLIS", "200"));
        AVAILS_RR_DB_RETRY_MAX_ATTEMPTS = Integer.valueOf(prop.getProperty("AVAILS_RR_DB_RETRY_MAX_ATTEMPTS", "4"));

        CHECKED_IN = prop.getProperty("CHECKED_IN", "checkedIn");
        CHECKED_OUT = prop.getProperty("CHECKED_OUT", "checkedOut");

        WAIT_LOCKING_QUEUE_TO_FINISH_SLEEP_TIME = Long.valueOf(prop.getProperty("WAIT_LOCKING_QUEUE_TO_FINISH_SLEEP_TIME", "3000")); // ms
        WAIT_LOCKING_QUEUE_TO_FINISH_TILL_NUM_REQUESTS = Integer.valueOf(prop.getProperty("WAIT_LOCKING_QUEUE_TO_FINISH_TILL_NUM_REQUESTS", "100"));

        REPEAT_RECOVERABLE_CC_ERROR = Long.valueOf(prop.getProperty("REPEAT_RECOVERABLE_CC_ERROR", "2"));
        WAIT_BEFORE_LISTING_CONNECTORS_WHILE_RESYNC = Integer.valueOf(prop.getProperty("WAIT_BEFORE_LISTING_CONNECTORS_WHILE_RESYNC", "3000")); // ms

        LOCKING_QUEUE_NUM_THREADS = Integer.valueOf(prop.getProperty("LOCKING_QUEUE_NUM_THREADS", "3"));
        CC_RESULT_NUM_THREADS = Integer.valueOf(prop.getProperty("CC_RESULT_NUM_THREADS", "3"));
        CC_CONFLICT_RESPONSE_NUM_THREADS = Integer.valueOf(prop.getProperty("CC_CONFLICT_RESPONSE_NUM_THREADS", "3"));
        ROLLUP_RESULT_NUM_THREADS = Integer.valueOf(prop.getProperty("ROLLUP_RESULT_NUM_THREADS", "3"));
        NODE_STATUS_NUM_THREADS = Integer.valueOf(prop.getProperty("NODE_STATUS_NUM_THREADS", "5"));
        MESSAGE_OFFSET_STATUS_NUM_THREADS = Integer.valueOf(prop.getProperty("MESSAGE_OFFSET_STATUS_NUM_THREADS", "5"));
        DISPLAY_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("DISPLAY_REQUEST_NUM_THREADS", "1"));
        COMMIT_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("COMMIT_REQUEST_NUM_THREADS", "1"));
        CHECK_OUT_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("CHECK_OUT_REQUEST_NUM_THREADS", "1"));
        CANCEL_CHECKOUT_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("CANCEL_CHECKOUT_REQUEST_NUM_THREADS", "1"));
        DELETE_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("DELETE_REQUEST_NUM_THREADS", "10"));
        RESYNC_REQUEST_NUM_THREADS = Integer.valueOf(prop.getProperty("RESYNC_REQUEST_NUM_THREADS", "1"));

                
        RESYNC_DEAL_MODE = prop.getProperty("RESYNC_DEAL_MODE","SINGLE");
        RESYNC_DEAL_MULTIPLE_COUNT = Integer.valueOf(prop.getProperty("RESYNC_DEAL_MULTIPLE_COUNT","10"));
        CHECK_ALL_CCREQUESTS_HAVE_FINISHED_AFTER_RESYNC_SLEEP_TIME_MS = Integer.valueOf(prop.getProperty("CHECK_ALL_CCREQUESTS_HAVE_FINISHED_AFTER_RESYNC_SLEEP_TIME_MS", "1000"));
        MAX_WAIT_FOR_ALL_CCREQUESTS_TO_FINISH_AFTER_RESYNC_MS = Integer.valueOf(prop.getProperty("MAX_WAIT_FOR_ALL_CCREQUESTS_TO_FINISH_AFTER_RESYNC_MS", "600000")); 
        
        // Kafka Constants
        // Caching
        CACHE_UPDATE_SLEEP_INTERVAL_MS = Long.valueOf(prop.getProperty("CACHE_UPDATE_SLEEP_INTERVAL_MS", "300000"));
        CACHE_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("CACHE_POLL_TIME_MILLISECONDS", "500"));
        CACHE_MAX_POLL_RECORDS = Integer.valueOf(prop.getProperty("CACHE_MAX_POLL_RECORDS", "100"));

        // Config
        CLIENT_REQUEST_MAX_POLL_RECORDS = prop.getProperty("CLIENT_REQEUST_MAX_POLL_RECORDS", "1000");
        CLIENT_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("CLIENT_POLL_TIME_MILLISECONDS", "100"));
        CC_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("CC_POLL_TIME_MILLISECONDS", "100"));
        ROLLUP_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("ROLLUP_POLL_TIME_MILLISECONDS", "100"));
        
        BULK_LOAD_REQUEST_MAX_POLL_RECORDS = prop.getProperty("BULK_LOAD_REQUEST_MAX_POLL_RECORDS", "100");
        BULK_LOAD_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("BULK_LOAD_POLL_TIME_MILLISECONDS", "100"));
        CLIENT_REQUEST_EMPTY_POLL_ATTEMPTS = Integer.valueOf(prop.getProperty("CLIENT_REQUEST_EMPTY_POLL_ATTEMPTS", "3"));
                
        // First Run/Rerun 
        FRRR_REQUEST_SLEEP_INTERVAL_SECONDS = Integer.valueOf(prop.getProperty("FRRR_REQUEST_SLEEP_INTERVAL_SECONDS", "1")); 
        
        FRRR_REQUEST_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("FRRR_REQUEST_POLL_TIME_MILLISECONDS", "300"));
        FRRR_REQUEST_MAX_POLL_RECORDS = prop.getProperty("FRRR_REQUEST_MAX_POLL_RECORDS", "300");
        FRRR_REQUEST_CACHE_EVICTION_TIME_SECONDS = Integer.valueOf(prop.getProperty("FRRR_REQUEST_CACHE_EVICTION_TIME_SECONDS", "600"));
        FRRR_REQUEST_CACHE_EVICTION_MAX_SIZE = Integer.valueOf(prop.getProperty("FRRR_REQUEST_CACHE_EVICTION_MAX_SIZE", "10000"));
        FRRR_REQUEST_CACHE_STATS_LOG_HIT_COUNT = Integer.valueOf(prop.getProperty("FRRR_REQUEST_CACHE_STATS_LOG_HIT_COUNT", "10000"));
        FRRR_REQUEST_CACHE_STATS_LOG_MISS_COUNT = Integer.valueOf(prop.getProperty("FRRR_REQUEST_CACHE_STATS_LOG_MISS_COUNT", "5000"));

        
        FRRR_PRODUCT_SLEEP_INTERVAL_SECONDS = Integer.valueOf(prop.getProperty("FRRR_PRODUCT_SLEEP_INTERVAL_SECONDS", "3600"));
        FRRR_PRODUCT_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("FRRR_PRODUCT_POLL_TIME_MILLISECONDS", "1000"));
        FRRR_PRODUCT_MAX_POLL_RECORDS = prop.getProperty("FRRR_PRODUCT_MAX_POLL_RECORDS", "20000");

        CARVEOUT_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("CARVEOUT_POLL_TIME_MILLISECONDS", "100"));

        BULK_LOAD_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("BULK_LOAD_POLL_TIME_MILLISECONDS", "10000"));
        BULK_LOAD_DEDUP_POLL_TIME_MILLISECONDS = Integer.valueOf(prop.getProperty("BULK_LOAD_DEDUP_POLL_TIME_MILLISECONDS", "100"));
        CLIENT_REQUEST_EMPTY_POLL_ATTEMPTS = Integer.valueOf(prop.getProperty("CLIENT_REQUEST_EMPTY_POLL_ATTEMPTS", "3"));
                
        CC_MAX_POLL_INTERVAL_MS = prop.getProperty("CC_MAX_POLL_INTERVAL_MS", "300000");
        CC_HEARTBEAT_INTERVAL_MS = prop.getProperty("CC_HEARTBEAT_INTERVAL_MS", "3000");
        CC_SESSION_TIMEOUT_MS = prop.getProperty("CC_SESSION_TIMEOUT_MS", "10000");
        CC_MAX_POLL_RECORDS = prop.getProperty("CC_MAX_POLL_RECORDS", "500");

        RESPONSE_MAX_POLL_INTERVAL_MS = prop.getProperty("RESPONSE_MAX_POLL_INTERVAL_MS", "300000");
        RESPONSE_HEARTBEAT_INTERVAL_MS = prop.getProperty("RESPONSE_HEARTBEAT_INTERVAL_MS", "3000");
        RESPONSE_SESSION_TIMEOUT_MS = prop.getProperty("RESPONSE_SESSION_TIMEOUT_MS", "10000");
        RESPONSE_MAX_POLL_RECORDS = prop.getProperty("RESPONSE_MAX_POLL_RECORDS", "500");

        UNLOCK_MAX_POLL_INTERVAL_MS = prop.getProperty("UNLOCK_MAX_POLL_INTERVAL_MS", "100000");
        UNLOCK_HEARTBEAT_INTERVAL_MS = prop.getProperty("UNLOCK_HEARTBEAT_INTERVAL_MS", "3000");
        UNLOCK_SESSION_TIMEOUT_MS = prop.getProperty("UNLOCK_SESSION_TIMEOUT_MS", "60000");
        UNLOCK_MAX_POLL_RECORDS = prop.getProperty("UNLOCK_MAX_POLL_RECORDS", "500");

        CARVEOUT_MAX_POLL_INTERVAL_MS = prop.getProperty("CARVEOUT_MAX_POLL_INTERVAL_MS", "300000");
        CARVEOUT_HEARTBEAT_INTERVAL_MS = prop.getProperty("CARVEOUT_HEARTBEAT_INTERVAL_MS", "3000");
        CARVEOUT_SESSION_TIMEOUT_MS = prop.getProperty("CARVEOUT_SESSION_TIMEOUT_MS", "60000");
        CARVEOUT_MAX_POLL_RECORDS = prop.getProperty("CARVEOUT_MAX_POLL_RECORDS", "500");

        CC_NODE_TIMEOUT_MS = (long) (Long.valueOf(CC_SESSION_TIMEOUT_MS) * 0.80);

        ROLLUP_MAX_POLL_INTERVAL_MS = prop.getProperty("ROLLUP_MAX_POLL_INTERVAL_MS", "300000");
        ROLLUP_HEARTBEAT_INTERVAL_MS = prop.getProperty("ROLLUP_HEARTBEAT_INTERVAL_MS", "3000");
        ROLLUP_SESSION_TIMEOUT_MS = prop.getProperty("ROLLUP_SESSION_TIMEOUT_MS", "10000");
        ROLLUP_MAX_POLL_RECORDS = prop.getProperty("ROLLUP_MAX_POLL_RECORDS", "500");

        String LOG_LEVEL = prop.getProperty("LOG_LEVEL", "warn");
        if (LOG_LEVEL.equals("info")) ROOT_LOG_LEVEL = Level.INFO;
        else if (LOG_LEVEL.equals("debug")) ROOT_LOG_LEVEL = Level.DEBUG;
        else if (LOG_LEVEL.equals("trace")) ROOT_LOG_LEVEL = Level.TRACE;
        else if (LOG_LEVEL.equals("error")) ROOT_LOG_LEVEL = Level.ERROR;
        else ROOT_LOG_LEVEL = Level.WARN;
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(ROOT_LOG_LEVEL);
        LoggerConfig lqpConfig = config.getLoggerConfig("com.pdgc.ccae.locking.LockingQueueMonitor");
        lqpConfig.setLevel(ROOT_LOG_LEVEL);
        ctx.updateLoggers(); // This causes all Loggers to refetch information from their LoggerConfig.

        TEST_SLEEP_TIME = new Long(prop.getProperty("TEST_SLEEP_TIME", "200"));
        DEDUP_PROCESSOR_SLEEP_TIME = new Long(prop.getProperty("DEDUP_PROCESSOR_SLEEP_TIME", "10000"));

        BLOCK_BULK_LOAD = Boolean.valueOf(prop.getProperty("BLOCK_BULK_LOAD", "false"));
        RIGHTS_IN_BUSINESS_UNITS = prop.getProperty("RIGHTS_IN_BUSINESS_UNITS", "810");
        DEFAULT_FILTER_BUSINESS_UNIT_IDS = Collections.unmodifiableSet(Stream.of(prop.getProperty("DEFAULT_FILTER_BUSINESS_UNIT_IDS", "810,170")).collect(Collectors.toSet()));

        BYPASSCACHE = Boolean.valueOf(prop.getProperty("BYPASSCACHE", "false"));
        BUSINESS_UNITS_TO_CC = Collections.unmodifiableSet(Arrays.asList(prop.getProperty("BUSINESS_UNITS_TO_CC", "810").replace(" ", "").split(",")).stream().map(b -> Long.valueOf(b)).collect(Collectors.toSet()));
        MAX_CONLFLICT_CHECK_CACHE_SIZE = Long.valueOf(prop.getProperty("MAX_CONLFLICT_CHECK_CACHE_SIZE", "100000"));
        
        JOB_PRODUCT_LEAF_THRESHOLD = Integer.valueOf(prop.getProperty("JOB_PRODUCT_LEAF_THRESHOLD", "25"));
        
        //Decides how many features can be in a single avail job
        //Maximum number of features to be in a batch for an avail rmeote job
        REMOTE_JOB_FEATURE_BATCH_SIZE_LARGEST = Integer.valueOf(prop.getProperty("REMOTE_JOB_FEATURE_BATCH_SIZE_LARGEST", "10"));
        REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_LARGEST = Integer.valueOf(prop.getProperty("REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_LARGEST", "7"));
        REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_SMALLEST = Integer.valueOf(prop.getProperty("REMOTE_JOB_FEATURE_BATCH_SIZE_SECOND_SMALLEST", "3"));
        //Minimum number of features to be in a batch for an avail rmeote job
        REMOTE_JOB_FEATURE_BATCH_SIZE_SMALLEST = Integer.valueOf(prop.getProperty("REMOTE_JOB_FEATURE_BATCH_SIZE_SMALLEST", "1"));

        //Thresholds to decide how many features are in a batch, based on count of MTL
        REMOTE_JOB_MTL_COUNT_LOWEST = Integer.valueOf(prop.getProperty("REMOTE_JOB_MTL_COUNT_LOWEST", "100")); 
        REMOTE_JOB_MTL_COUNT_MIDDLE =  Integer.valueOf(prop.getProperty("REMOTE_JOB_MTL_COUNT_MIDDLE", "1000"));
        REMOTE_JOB_MTL_COUNT_HIGHEST =  Integer.valueOf(prop.getProperty("REMOTE_JOB_MTL_COUNT_HIGHEST", "10000"));
        
        
        JOB_MANAGER_WAIT_INTERVAL_MS = Long.valueOf(prop.getProperty("JOB_MANAGER_WAIT_INTERVAL_MS", "1000"));
        LARGE_AVAILS_CHECK_FILE_WAIT_INTERVAL_MS = Long.valueOf(prop.getProperty("LARGE_AVAILS_CHECK_FILE_WAIT_INTERVAL_MS", "10000"));
        
        DRO_FILTER = prop.getProperty("DRO_FILTER", "1,3");
        BUSINESS_UNITS_TO_MERGE_RS = prop.getProperty("BUSINESS_UNITS_TO_MERGE_RS", "170");
        maxDeleteAttempts = Integer.valueOf(prop.getProperty("MAX_DELETE_ATTEMPTS", "5"));
        
        CONTRACT_DISPLAY_SOURCE = prop.getProperty("CONTRACT_DISPLAY_SOURCE", "Contract");
        RESERVATION_DISPLAY_SOURCE = prop.getProperty("RESERVATION_DISPLAY_SOURCE", "Reservation");
        BASELINE_WORKFLOW_DISPLAY_SOURCE = prop.getProperty("BASELINE_WORKFLOW_DISPLAY_SOURCE", "Baseline");
        
        INCLUDE_AVAILS_AFTER_LATEST_START_DATE = Boolean.valueOf(prop.getProperty("INCLUDE_AVAILS_AFTER_LATEST_START_DATE", "false"));
        
        PATH_TO_BASH = prop.getProperty("PATH_TO_BASH", "/bin/bash");
        PATH_TO_COMPLETED_PRODUCTS = prop.getProperty("PATH_TO_COMPLETED_PRODUCTS", "/mnt/share/avails/CompletedProducts");
        PATH_TO_PREVIOUS_NET_RESULTS = prop.getProperty("PATH_TO_PREVIOUS_NET_RESULTS", "/mnt/share/avails/NetResults");
        AVAILS_USE_SHELL_FILE_COUNT=new Boolean(prop.getProperty("AVAILS_USE_SHELL_FILE_COUNT", "false"));
        LARGE_JOB_CLEANUP_SLEEP_TIME = Long.valueOf(prop.getProperty("LARGE_JOB_CLEANUP_SLEEP_TIME", "1000"));
        LARGE_JOB_CLEANUP_DELETE_ATTEMPTS = Long.valueOf(prop.getProperty("LARGE_JOB_CLEANUP_DELETE_ATTEMPTS", "2"));
        LARGE_AVAIL_FILE_FETCH_RETRY_COUNT = Integer.valueOf(prop.getProperty("LARGE_AVAIL_FILE_FETCH_RETRY_COUNT", "10"));
        LARGE_AVAIL_FILE_FETCH_RETRY_SLEEP_TIME = Long.valueOf(prop.getProperty("LARGE_AVAIL_FILE_FETCH_RETRY_SLEEP_TIME", "100"));
        
        // Excel configuration
        EXCEL_CELL_CHAR_MAX = Integer.valueOf(prop.getProperty("EXCEL_CELL_CHAR_MAX", "6000"));

        CONFLICT_CHECK_CACHE_EXPIRE_AFTER_WRITE_SECONDS = Long.valueOf(prop.getProperty("CONFLICT_CHECK_CACHE_EXPIRE_AFTER_WRITE_SECONDS", "600"));

        REPLAY_REQUEST_TYPES = prop.getProperty("REPLAY_REQUEST_TYPES", "70, 73, 75");
        
        
        String RIGHTS_IN_CHANGE_EXCLUDE_DEALS_TEMP = prop.getProperty("RIGHTS_IN_CHANGE_EXCLUDE_DEALS", "").replaceAll("\\s+","");
        String RIGHTS_IN_CHANGE_EXCLUDE_TITLES_TEMP = prop.getProperty("RIGHTS_IN_CHANGE_EXCLUDE_TITLES", "").replaceAll("\\s+","");
        
        String CLIENT_EXCLUDE_DEALS_TEMP = prop.getProperty("CLIENT_IN_CHANGE_EXCLUDE_DEALS", "").replaceAll("\\s+","");
        String CLIENT_EXCLUDE_TITLES_TEMP = prop.getProperty("CLIENT_IN_CHANGE_EXCLUDE_TITLES", "").replaceAll("\\s+","");
        
        RIGHTS_IN_CHANGE_EXCLUDE_DEALS = new HashSet<>();
        RIGHTS_IN_CHANGE_EXCLUDE_TITLES = new HashSet<>();
        
        CLIENT_EXCLUDE_DEALS = new HashSet<>();
        CLIENT_EXCLUDE_TITLES = new HashSet<>();
        
        if (StringUtils.isNotBlank(RIGHTS_IN_CHANGE_EXCLUDE_DEALS_TEMP)) {
            RIGHTS_IN_CHANGE_EXCLUDE_DEALS = new HashSet<>(Arrays.asList(RIGHTS_IN_CHANGE_EXCLUDE_DEALS_TEMP.split(",")));
        }

        if (StringUtils.isNotBlank(RIGHTS_IN_CHANGE_EXCLUDE_TITLES_TEMP)) {
            RIGHTS_IN_CHANGE_EXCLUDE_TITLES = new HashSet<>(Arrays.asList(RIGHTS_IN_CHANGE_EXCLUDE_TITLES_TEMP.split(",")));
        }

        if (StringUtils.isNotBlank(CLIENT_EXCLUDE_DEALS_TEMP)) {
            CLIENT_EXCLUDE_DEALS = new HashSet<>(Arrays.asList(CLIENT_EXCLUDE_DEALS_TEMP.split(",")));
        }

        if (StringUtils.isNotBlank(CLIENT_EXCLUDE_TITLES_TEMP)) {
            CLIENT_EXCLUDE_TITLES = new HashSet<>(Arrays.asList(CLIENT_EXCLUDE_TITLES_TEMP.split(",")));
        }

        CUSTOMER_COUNT_CARVEOUT_TYPES.add(4);

        METRIC_TYPE_BOX_OFFICE = prop.getProperty("METRIC_TYPE_BOX_OFFICE", "BOX");
        METRIC_TYPE_ADMISSIONS = prop.getProperty("METRIC_TYPE_ADMISSIONS", "ADM");
        METRIC_TYPE_SCREENS = prop.getProperty("METRIC_TYPE_SCREENS", "SCR");

        NULL_CUSTOMER_ID = Long.valueOf(prop.getProperty("NULL_CUSTOMER_ID","0"));

        SALESPLAN_SET_MEDIAS = new HashSet<>();
        String SALESPLAN_SET_MEDIAS_STR = prop.getProperty("SALESPLAN_SET_MEDIAS", "16");
        for (String mediaId : SALESPLAN_SET_MEDIAS_STR.replaceAll("\\s+","").split(",")) {
            try {
                SALESPLAN_SET_MEDIAS.add(Long.valueOf(mediaId));
            } catch (NumberFormatException e) {
                LOGGER.warn("SALESPLAN_SET_MEDIAS has mediaId values that aren't numbers: {}", SALESPLAN_SET_MEDIAS_STR);
            }
        }

        CALC_ALL_LICENSED_CUSTOMERS = Boolean.valueOf(prop.getProperty("CALC_ALL_LICENSED_CUSTOMERS", "true"));

        CALC_SALES_PLANS_AS_DISTR_RIGHTS = Boolean.valueOf(prop.getProperty("CALC_SALES_PLANS_AS_DISTR_RIGHTS", "true"));
         
        SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_MEDIA_TERRITORY = prop.getProperty("SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_MEDIA_TERRITORY", "1");
        SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_TERRITORY = prop.getProperty("SALES_WINDOW_AUTOSELECT_BY_EXHIBITION_TERRITORY", "2");
        SALES_WINDOW_SELECT_FROM_LIST = prop.getProperty("SALES_WINDOW_SELECT_FROM_LIST", "3");

        NFS_USER = prop.getProperty("NFS_USER");
        NFS_HOST = prop.getProperty("NFS_HOST");

        ASSEMBLER_DIR = prop.getProperty("ASSEMBLER_DIR", "/tmp/Sessions/");
        XLSX_LOCAL_DIR = prop.getProperty("XLSX_LOCAL_DIR", "/tmp/Saved/");
        REMOTE_DIR = prop.getProperty("REMOTE_DIR");

        LOCAL_XLSX_CREATION = Boolean.valueOf(prop.getProperty("LOCAL_XLSX_CREATION", "false"));
        LOCAL_XLSX_CREATION_TEST = Boolean.valueOf(prop.getProperty("LOCAL_XLSX_CREATION_TEST", "true"));

        CONFLICT_RESPONSE_CLIENT_RETRY_NUM = Integer.parseInt(prop.getProperty("CONFLICT_RESPONSE_CLIENT_RETRY_NUM", "3"));
        CONFLICT_RESPONSE_CLIENT_RETRY_BACKOFF_MS = Long.parseLong(prop.getProperty("CONFLICT_RESPONSE_CLIENT_RETRY_BACKOFF_MS", "5000"));
        CONFLICT_RESPONSE_AUTH_TOKEN_REQUEST_URL = prop.getProperty("CONFLICT_RESPONSE_AUTH_TOKEN_REQUEST_URL");
        CONFLICT_RESPONSE_AUTH_GRANT_TYPE = prop.getProperty("CONFLICT_RESPONSE_AUTH_GRANT_TYPE");
        CONFLICT_RESPONSE_AUTH_CLIENT_ID = prop.getProperty("CONFLICT_RESPONSE_AUTH_CLIENT_ID");
        CONFLICT_RESPONSE_AUTH_CLIENT_SECRET = prop.getProperty("CONFLICT_RESPONSE_AUTH_CLIENT_SECRET");
        CONFLICT_RESPONSE_AUTH_USERNAME = prop.getProperty("CONFLICT_RESPONSE_AUTH_USERNAME");
        CONFLICT_RESPONSE_AUTH_PASSWORD = prop.getProperty("CONFLICT_RESPONSE_AUTH_PASSWORD");

        BUSINESS_UNITS_LOCAL_RELEASE_DATE  = new HashSet<>();
        String localReleaseDateBusinessUnits = prop.getProperty("BUSINESS_UNITS_LOCAL_RELEASE_DATE", "400");
        for (String businessUnitId : localReleaseDateBusinessUnits.replaceAll("\\s+","").split(",")) {
            try {
                BUSINESS_UNITS_LOCAL_RELEASE_DATE.add(Long.valueOf(businessUnitId));
            } catch (NumberFormatException e) {
                LOGGER.warn("BUSINESS_UNITS_LOCAL_RELEASE_DATE has businessUnitId values that aren't numbers: {}", localReleaseDateBusinessUnits);
            }
        }
        MEDIAS_LOCAL_HE_DVD_RELEASE_DATE = new HashSet<>();
        String localHeDvdReleaseDateMedias = prop.getProperty("MEDIAS_LOCAL_HE_DVD_RELEASE_DATE", "3,10");
        for (String mediaId : localHeDvdReleaseDateMedias.replaceAll("\\s+","").split(",")) {
            try {
                MEDIAS_LOCAL_HE_DVD_RELEASE_DATE.add(Long.valueOf(mediaId));
            } catch (NumberFormatException e) {
                LOGGER.warn("MEDIAS_LOCAL_HE_DVD_RELEASE_DATE has mediaId values that aren't numbers: {}", localHeDvdReleaseDateMedias);
            }
        }
        MEDIAS_LOCAL_TV_RELEASE_DATE = new HashSet<>();
        String localTvReleaseDateMedias = prop.getProperty("MEDIAS_LOCAL_TV_RELEASE_DATE", "2,7,8");
        for (String mediaId : localTvReleaseDateMedias.replaceAll("\\s+","").split(",")) {
            try {
                MEDIAS_LOCAL_TV_RELEASE_DATE.add(Long.valueOf(mediaId));
            } catch (NumberFormatException e) {
                LOGGER.warn("MEDIAS_LOCAL_TV_RELEASE_DATE has mediaId values that aren't numbers: {}", localTvReleaseDateMedias);
            }
        }
        MEDIAS_LOCAL_THEA_RELEASE_DATE = new HashSet<>();
        String localTheatricalReleaseDateMedias = prop.getProperty("MEDIAS_LOCAL_THEA_RELEASE_DATE");
        if (localTheatricalReleaseDateMedias == null) {
            MEDIAS_LOCAL_THEA_RELEASE_DATE.add(MEDIA_ID_THEATRICAL);
        } else {
            for (String mediaId : localTheatricalReleaseDateMedias.replaceAll("\\s+", "").split(",")) {
                try {
                    MEDIAS_LOCAL_THEA_RELEASE_DATE.add(Long.valueOf(mediaId));
                } catch (NumberFormatException e) {
                    LOGGER.warn("MEDIAS_LOCAL_THEA_RELEASE_DATE has mediaId values that aren't numbers: {}", localTheatricalReleaseDateMedias);
                }
            }
        }

        CC_ROLLUP_LAST_PROCESS_TIME_CACHE_MAX_SIZE = Long.parseLong(prop.getProperty("CC_ROLLUP_LAST_PROCESS_TIME_CACHE_MAX_SIZE", "10000"));

        RESTRICTION_CODES_FOR_LEGEND = new HashSet<>();
        String restrictionCodesForLegend = prop.getProperty("RESTRICTION_CODES_FOR_LEGEND", "141,142,143,144");
        for (String restrictionCode : restrictionCodesForLegend.replaceAll("\\s+","").split(",")) {
            try {
                RESTRICTION_CODES_FOR_LEGEND.add(Integer.valueOf(restrictionCode));
            } catch (NumberFormatException e) {
                LOGGER.warn("RESTRICTION_CODES_FOR_LEGEND has restrictionCode values that aren't numbers: {}", restrictionCodesForLegend);
            }
        }
        
        PERMISSION_VIEW_LICENSE_FEES_TYPE_ID = Long.valueOf(prop.getProperty("PERMISSION_VIEW_LICENSE_FEES_TYPE_ID", "199"));
        PERMISSION_CUSTOMER_TYPE_ID = Long.valueOf(prop.getProperty("PERMISSION_CUSTOMER_TYPE_ID", "200"));
        ROLE_ADMIN_TYPE_ID = Long.valueOf(prop.getProperty("ROLE_ADMIN_TYPE_ID", "232"));
        
        PRODUCTION_HIERARCHY_ID = Long.valueOf(prop.getProperty("PRODUCTION_HIERARCHY_ID", "101"));
        SALES_HIERARCHY_ID = Long.valueOf(prop.getProperty("SALES_HIERARCHY_ID", "102"));
        BROADCAST_HIERARCHY_ID = Long.valueOf(prop.getProperty("BROADCAST_HIERARCHY_ID", "103"));
        HE_SALES_HIERARCHY_ID = Long.valueOf(prop.getProperty("HE_SALES_HIERARCHY_ID", "104"));
        
        AVAILS_UI_NUM_ERROR_PRODUCTS_MAX = Integer.valueOf(prop.getProperty("AVAILS_UI_NUM_ERROR_PRODUCTS_MAX", "4"));
        
        EXCEL_ROW_LIMIT = Integer.valueOf(prop.getProperty("EXCEL_ROW_LIMIT", "1040000"));
        
        NEEDS_EXPIRED_CONFLICTS = Boolean.valueOf(prop.getProperty("NEEDS_EXPIRED_CONFLICTS", "true"));
    }
}
