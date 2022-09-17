package com.pdgc.ccae.dao.intermediateobjects;

import java.time.LocalDate;
import java.util.Collection;

import com.pdgc.general.cache.dictionary.impl.GenreKey;
import com.pdgc.general.cache.dictionary.impl.KeyWithBusinessUnit;

import lombok.Builder;
import lombok.Value;

/**
 * Placeholder class that holds basically all product information from the db
 * without going through any translations of the ids
 */
@Value
@Builder
public class ProductInfoEntry {

	private long productId;
	private String title;
	private String productTypeCode;
	private int productLevel;
	private Integer seasonNumber;
	private Integer episodeNumber;
	private Integer episodePartNumber;
	private LocalDate releaseDate;
	private Integer numberOfUnits;
	private Collection<GenreKey> genreIds;
	private Collection<GenreKey> themeIds;
	private Collection<KeyWithBusinessUnit<String>> productTypeIds;
	private String castString;
	private String director;
	private String titleId;
	private String synopsis;
	private String divisionCode;
	private String productionYear;
	private Integer releaseYear;
	private Long originCountryId;
	private Long originLanguageId;
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
	private String blackwhitedesc;
	private String awardsAndNominations;
	private String productType;
     
	
	
    @Override
	public String toString() {
		return title;
	}
}
