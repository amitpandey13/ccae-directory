package com.pdgc.ccae.dao.intermediateobjects.mapper;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.structures.rightsource.DealProduct;

/**
 * Mapper class to map DataRow from db query results
 * to DealProduct object
 *
 * @author Jessica Shin
 */
public final class DealProductMapper {

    private DealProductMapper() {}

    /**
     * Map to DealProduct including category, sub-category, and rights variance
     * @param reader - row fetched from database
     * @return DealProduct POJO
     */
    @SuppressWarnings("PMD.NPathComplexity") 
    public static DealProduct mapDataRowToDealProduct(DataRow reader) {
        DealProduct dealProduct = null;
        Long dealProductId = reader.getLong("dealProductId");
        if (dealProductId != null) {
            dealProduct = DealProduct.builder()
                .dealProductId(dealProductId)
                .businessUnitId(reader.getLong("businessUnitId"))
                .categoryDescription(reader.getString("categoryDescription"))
                .subCategoryDescription(reader.getString("subCategoryDescription"))
                .seriesCommitmentType(reader.columnExists("commitment_type_code") ? reader.getString("commitment_type_code") : null)
                .seriesCommitmentYear(reader.columnExists("commitment_year") ? reader.getString("commitment_year") : null)
                .seriesCommitmentAnnualIncrease(reader.columnExists("commitment_annual_percent") ? reader.getString("commitment_annual_percent") : null)
                .seriesCommitmentComments(reader.columnExists("commitment_comments") ? reader.getString("commitment_comments") : null)
                .rightsTemplate(reader.getString("rightsTemplate"))
                .tentative(reader.getBoolean("tentative"))
                .build();
        }
        return dealProduct;
    }

}
