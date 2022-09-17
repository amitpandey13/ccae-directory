package com.pdgc.ccae.dao.intermediateobjects.mapper;

import com.pdgc.ccae.dao.intermediateobjects.ProductReleaseDateEntry;
import com.pdgc.db.structures.DataTable.DataRow;

/**
 * Mapper class to map DataRow from db query results
 * to ProductReleaseDateEntry object
 *
 * @author Jessica Shin
 */
public final class ProductReleaseDateEntryMapper {

    private ProductReleaseDateEntryMapper() {}

    /**
     * Map to ProductReleaseDateEntry from masterData.productReleaseDate
     * @param reader - row fetched from database
     * @return ProductReleaseDateEntry POJO
     */
    public static ProductReleaseDateEntry mapDataRowToProductReleaseDateEntry(DataRow reader) {
        return new ProductReleaseDateEntry(
            reader.getDate("releaseDate"),
            reader.getString("releaseDateStatus"),
            reader.getString("releaseDateType"),
            reader.getDate("feed_date"),
            reader.getString("feed_date_status"),
            reader.getDate("manual_date"),
            reader.getString("temp_perm"),
            reader.getDate("projected_date"),
            reader.getLong("businessUnitId"),
            reader.getString("releaseDateTag")
        );
    }

}
