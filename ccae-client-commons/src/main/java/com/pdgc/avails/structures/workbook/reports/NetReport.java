package com.pdgc.avails.structures.workbook.reports;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Chen Su <chen.su@pdgc.com>
 * @version September 21, 2018
 * @since September 21, 2018
 *
 * Net report object to translate to JSON
 *
 * These property names are parsed directly to column headers
 * e.g. windowStart becomes the header name "Window Start"
 */
@Getter
@Setter
public class NetReport implements ReportModel {
    private String title;                       //Title
    private String titleId;                     //Title Id
    private String media;                       //Media
    private String terrLang;                    //Territory Language
    private String productType;                 //Product Type

    private String availsStatus;                //Availability Status
    private String reasonNotAvailable;
    private String licenseAvailsExhiExcl;       //License Availability Exhibition | Exclusivity
    private String windowStart;                 //Availability Start
    private String windowEnd;                   //Availability End
    private String distributionStart;
    private String rightsExpiration;
    private String hasAllOptionalCriteria;     //Has All Optional Criteria

    private String salesWindow;                 // Sales Window
    private String salesWindowStatus;           // Sales Window Status
    private String lifecycle;                   // Sales Window Lifecycle
    private String salesWindowCustomer;         // Sales Window Customer
    private String missingOptionalCriteria;     //Missing Optional Criteria
    private String missingMandatoryCriteria;

    private String windowLength;                //Window Length
    private Boolean contMinLength;              //Contiguous Windows Passed Minimum Length

    private String hasAllEps;                  //Has All Episodes
    private Integer availsEpsMandatory;         //Available Episodes - Mandatory Criteria
    private Integer availsEpsAll;               //Available Episodes - All Criteria
    private Integer numOfEps;                   //# of Episodes
    private String salesEpisodeCount;           // TVD Sales Episode Count
    private String broadcastEpisodeCount;       // Broadcast Episode Count
    private String terrWithAvails;              //Territory With Availability
    private String mediaWithAvails;

    private String existingLicensees;           //Existing Licenses
    private String exhibitions;                 //Existing Exhibition
    private String exclusivities;               //Existing Exclusivity
    private String carveOuts;                   //Carve-Outs
    private String carveOutComments;            //Carve-Out Comments

    private String contentOwner;                // Content Owner Restrictions
    private String generalInfoCode;             // General Info Restrictions
    private String licensingInfoCode;           // Licensing Info Restrictions
    private String motInfoCode;                 // MOT Info Restrictions
    private String licenseComment;              // License Comments

    private Integer relYear;                     //Release Year
    private String prodYear;                    //Production Year
    private String genre;                       //Genre

    private String countryOfOrigin;             //Country of Origin
    private String originalLanguage;            //Original Language
    private Integer runTime;                     //Run Time
    private String firstRunRR;                  //First Run/RR
    
    private LocalDate firstRunDate;                //First Run Date

    private String lastLicensedCustomer;
    private String allLicensedCustomers;
    private String currentCustomerPreviouslyLicensed;

    private Long localBoxOffice;
    private String localBoxOfficeCurrency;
    private Long localBoxOfficeUsd;
    private Long localAdmissions;
    private Long localScreens;

    private LocalDate localHeRelease;
    
    private LocalDate localInitialAir;
    
    private LocalDate localTheatricalRelease;

    private LocalDate releaseDate;
    private Long internationalAdmissions;
    private Long usAdmissions;
    private Long worldwideAdmissions;
    private Long internationalBoxOffice;
    private Long usBoxOffice;
    private Long worldwideBoxOffice;
    private String initialAirNetwork;
    private String mpaaRating;
   
    private LocalDate globalFAD;
    
    private LocalDate usHeRelease;
    
    private LocalDate usInitialAir;
    
    private LocalDate usInitialRelease;
    
    private LocalDate usTheatricalRelease;
    private Long internationalScreens;
    private Long usScreens;		
    private Long worldwideScreens;
    private String durationMonths;
    private String durationDays;
    private String statusFlag;
    private String hasPrePromoPeriod;
    private String hasPostPromoPeriod;
    private Integer availabilityNumber;
    private String colorORblackandwhite;
    private Boolean contiguousWindowPassedMinLength;
    private String director;
    private String division;
    private String talent;
    private String theme;
    private String synopsis;
    private String localTitleRating;
}
