package com.pdgc.avails.structures.workbook.reports;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Chen Su <chen.su@pdgc.com>
 * @version September 21, 2018
 * @since September 21, 2018
 *
 * Sold Unsold report object to translate to JSON
 */
@Getter
@Setter
public class SoldUnsoldReport implements ReportModel {
    private String title;
    private String titleId;
    private String media;
    private String territory;
    private String haveRights;     //corps
    private String availsToSell;   //no-corps
    private String isSold;
    private String customer;
    private String customerId;
    private String noRightsEntered;
}
