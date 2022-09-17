package com.pdgc.ccae.dao.intermediateobjects;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.postgresql.jdbc.PgArray;

import com.pdgc.general.structures.timeperiod.TimePeriodPart;

import lombok.Getter;

/**
 * Class to hold CarveOutLicense information.
 */
@Getter
public class CarveOutLicenseData {

    private Long customerId;

    private Long sourceId;
    private String sourceDetailId;
    private Long businessUnitId;
    private Long rightsGroupId;
    private String displaySourceType;
    private String displaySourceId;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<TimePeriodPart> carveoutTimePeriodList;
    public String carveOutTimePeriodXml;

    private Long productId;
    private Set<Integer> mediaSet;
    private Set<Integer> territorySet;
    private Set<Integer> languageSet;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CarveOutLicenseData(
        Long customerId,
        Long sourceId,
        String sourceDetailId,
        Long businessUnitId,
        Long rightsGroupId,
        String displaySourceType,
        LocalDate cstartdate,
        LocalDate cenddate,
        Long productId,
        Object mediaArray,
        Object territoryArray,
        Object languageArray,
        String displaySourceId
    ) throws SQLException {
        this.customerId = customerId;
        this.sourceId = sourceId;
        this.sourceDetailId = sourceDetailId;
        this.businessUnitId = businessUnitId;
        this.rightsGroupId = rightsGroupId;
        this.displaySourceType = displaySourceType;
        this.startDate = cstartdate;
        this.endDate = cenddate;
        Integer[] intSet;
        this.productId = productId;
        intSet = (Integer[]) ((PgArray) mediaArray).getArray();
        this.mediaSet = new HashSet<Integer>(Arrays.asList(intSet));
        intSet = (Integer[]) ((PgArray) territoryArray).getArray();
        this.territorySet = new HashSet<Integer>(Arrays.asList(intSet));
        intSet = (Integer[]) ((PgArray) languageArray).getArray();
        this.languageSet = new HashSet<Integer>(Arrays.asList(intSet));
        this.displaySourceId = displaySourceId;
    }

}
