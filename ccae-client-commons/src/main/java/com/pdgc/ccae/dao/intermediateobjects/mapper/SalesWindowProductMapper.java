package com.pdgc.ccae.dao.intermediateobjects.mapper;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;

/**
 * Mapper class to map DataRow from db query results
 * for saleswindowproduct table to FoxSalesWindowProduct class
 * @author Linda Xu
 *
 */
public final class SalesWindowProductMapper {

    private SalesWindowProductMapper() {
        
    }
    
    /**
     * Maps window information to the FoxSalesWindowProduct. Must include the saleswindowId column, 
     * else return is null
     * @param reader row fetched from database
     * @return
     */
    public static FoxSalesWindowProduct mapDataRowToSalesWindowProduct(DataRow reader) {
        FoxSalesWindowProduct windowProduct = null;
        if (reader.columnExists("salesWindowId")) {
            windowProduct = FoxSalesWindowProduct.builder()
                .salesWindowId(reader.getLong("salesWindowId"))
                .startDate(reader.getDate("salesWindowStartDate"))
                .endDate(reader.getDate("salesWindowEndDate"))
                .outsideDate(reader.getDate("salesWindowOutsideDate"))
                .build();
        }
        return windowProduct;
    }
}
