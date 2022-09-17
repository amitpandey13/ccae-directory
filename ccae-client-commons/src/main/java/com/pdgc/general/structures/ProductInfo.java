package com.pdgc.general.structures;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * ProductInfo POJO
 */
@Getter
@Builder
@AllArgsConstructor
public class ProductInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private long productId;
    private Collection<String> productTypeDescription;
    private Integer seasonNumber;
    private Integer episodeNumber;
    private Integer episodePartNumber;
    private LocalDate releaseDate;
    private Integer unitCount;
    private Collection<String> genres;
    private Collection<String> themes;
    private String cast;
    private String director;
    private String synopsis;
    private String division;
    private String titleId;
    private String productionYear;
    private Integer releaseYear;
    private String originCountry;
    private String originLanguage;
    private Integer runTime;
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
    private String internationalEpisodeCount;
    private String domesticEpisodeCount;
    private String blackWhiteDesc;
    private String awardsAndNominations;
}
