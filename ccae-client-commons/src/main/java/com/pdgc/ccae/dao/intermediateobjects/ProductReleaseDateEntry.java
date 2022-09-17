package com.pdgc.ccae.dao.intermediateobjects;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Product Release Date entry populated from database row
 */
@AllArgsConstructor
@Getter
@Builder
public class ProductReleaseDateEntry implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate releaseDate;
    private String releaseDateStatus;
    private String releaseDateType;
    private LocalDate feedDate;
    private String feedDateStatus;
    private LocalDate manualDate;
    private String tempOrPerm;
    private LocalDate projectedDate;
    private Long businessUnitId;
    private String releaseDateTag;

}
