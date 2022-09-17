package com.pdgc.general.calculation.corporate;

/**
 * Different scenarios for the windows that may populate the avails' 'Reason No Available' column
 * @author Linda Xu
 *
 */
public enum FoxWindowReasonDetail {
    NONE,
    NO_CORP_RIGHTS,
    RESTRICTED_RIGHTS,
    NO_VALID_DISTRIBUTION,
    PRE_DISTRIBUTION,
    POST_DISTRIBUTION,
    PRIOR_TO_SALES_WINDOW_START,
    PRIOR_TO_SALES_WINDOW_OUTSIDE,
    PRIOR_TO_SALES_WINDOW,
    PRELIMINARY_DISTRIBUTION,
    NO_MUSIC_RIGHTS,
    EXCEPTION_DEFAULT
}
