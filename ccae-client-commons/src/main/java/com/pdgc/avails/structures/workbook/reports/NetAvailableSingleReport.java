package com.pdgc.avails.structures.workbook.reports;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Chen Su <chen.su@pdgc.com>
 * @version September 21, 2018
 * @since September 21, 2018
 *
 * NetAvailableSingle report object to translate to JSON
 */
@Getter
@Setter
public class NetAvailableSingleReport implements ReportModel {
    private String title;                       //Title
    private Integer titleId;                    //Title Id
    private String media;                       //Media
    private String terrLang;                    //Territory Language
    private String productType;                 //Product Type
    private String availsStatus;                //Availability Status
    private String licenseAvails;               //License Availability Exhibition | Exclusivity
    private String availsStart;                 //Availability Start
    private String availsEnd;                   //Availability End
    private String licensedTimePeriod;          //Licensed Time Period
    private boolean hasAllOptionalCriteria;     //Has All Optional Criteria
    private String salesWindowPlan;             //Sales Window Plan
    private String missingOptionalCriteria;     //Missing Optional Criteria
    private Integer windowLength;               //Window Length
    private boolean contMinLength;              //Contiguous Windows Passed Minimum Length
    private boolean availsWindowBeforeLSD;      //Availability Window Starts Before Latest Start Date
    private boolean hasAllEps;                  //Has All Episodes
    private Integer availsEpsMandatory;         //Available Episodes - Mandatory Criteria
    private Integer availsEpsAll;               //Available Episodes - All Criteria
    private Integer episodes;                   //# of Episodes
    private String terrWithAvails;              //Territory With Availability
    private String existingLic;                 //Existing Licenses
    private String existingTL;                  //Existing TitleList
    private String carveOut;                    //Carve-Out
    private String carveOutComments;            //Carve-Out Comments
    private String restrictions;                //Restrictions
    private Integer relYear;                    //Release Year
    private Integer prodYear;                   //Production Year
    private Integer units;                      //# of Units
    private String genre;                       //Genre
    private String originalCountry;             //Country of Origin
    private String originalLanguage;            //Original Language
    private String runTime;                     //Run Time
    private String frrr;                        //FF/RR
    private String firstRunDate;                //First Run Date
}
