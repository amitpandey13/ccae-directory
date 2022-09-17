package com.pdgc.ccae.dao.intermediateobjects.mapper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.postgresql.jdbc.PgArray;

import com.google.common.collect.Sets;
import com.pdgc.ccae.dao.intermediateobjects.AvailsRightsEntry;
import com.pdgc.conflictcheck.convenience.RollupRequestHelper;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.hierarchysource.HierarchyProvider;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.util.FoxPMTLUtil;
import com.pdgc.general.util.IntegerConversionUtil;

/**
 * Mapper class to map DataRow from db query results
 * to AvailsRightsEntry object
 *
 * @author Jessica Shin
 */
public final class AvailsRightsEntryMapper {

    private AvailsRightsEntryMapper() {}

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
	public static AvailsRightsEntry mapDataRowToAvailsRightsEntry(DataRow reader) throws SQLException {
        Long productHierarchyId = reader.getLong("productHierarchyId");
        
        Set<Long> productIds;
        ProductLevel productLevel;
        if (reader.columnExists("productArray")) {
            productIds = IntegerConversionUtil.convertToLongSet(reader.getArrayOfType("productArray", Integer.class));
            productLevel = FoxPMTLUtil.getProductFromId(productIds.iterator().next(), productHierarchyId).getProductLevel();
        } else {
            Long productId = reader.getLong("productId");
            productIds = Sets.newHashSet(productId);
            productLevel = FoxPMTLUtil.getProductFromId(productId, productHierarchyId).getProductLevel();
        }
        
        Set<Long> territoryIds = IntegerConversionUtil.convertToLongSet((Integer[])((PgArray) reader.getColumn("territoryArray")).getArray());
		Set<Long> languageIds = IntegerConversionUtil.convertToLongSet((Integer[])((PgArray) reader.getColumn("languageArray")).getArray());
		Set<Long> mediaIds = IntegerConversionUtil.convertToLongSet((Integer[])((PgArray) reader.getColumn("mediaArray")).getArray());

		Set<String> comments = reader.getString("comments") != null
			? new HashSet<String>(Arrays.asList(reader.getString("comments")))
			: new HashSet<String>();

		// license rights
		Set<Long> rightIdSet = null;
		if (reader.columnExists("rightStrandIds")) {
			rightIdSet = Sets.newHashSet((Long[])((PgArray) reader.getColumn("rightStrandIds")).getArray());
		}

		AggregateMedia rightsGroupMedia = new AggregateMedia(FoxPMTLUtil.getMediasFromIds(
			Arrays.asList((Integer[])((PgArray) reader.getColumn("rights_group_medias")).getArray())));

		AggregateTerritory rightsGroupTerritory = new AggregateTerritory(FoxPMTLUtil.getTerritoriesFromIds(
			Arrays.asList((Integer[])((PgArray) reader.getColumn("rights_group_territories")).getArray())));

		AggregateLanguage rightsGroupLanguage = new AggregateLanguage(FoxPMTLUtil.getLanguagesFromIds(
			Arrays.asList((Integer[])((PgArray) reader.getColumn("rights_group_languages")).getArray())));
		
		Set<Product> elevatedProducts = RollupRequestHelper.getElevatedProducts(
	        productIds,
	        productHierarchyId
	    );
		Product elevatedProduct = new AggregateProduct(elevatedProducts);
		
		Set<Long> leafProductSet = HierarchyProvider.getHierarchies().getProductIdHierarchy(productHierarchyId)
            .convertToLeaves(productIds);

		return AvailsRightsEntry.builder()
			.rightId(reader.getLong("rightId"))
			.rightTypeId(reader.getLong("rightTypeId"))
			.episodeLimit(reader.getInteger("episodeLimit"))
			.productHierarchyId(reader.getLong("productHierarchyId"))
			.productIds(productIds)
			.territoryIds(territoryIds)
			.languageIds(languageIds)
			.mediaIds(mediaIds)
			.startDate(reader.getDate("maskedStartDate"))
			.endDate(reader.getDate("maskedEndDate"))
			.sourceTypeId(reader.getInteger("sourceTypeId"))
			.sourceId(reader.getLong("sourceId"))
			.sourceDetailId(reader.getString("sourceDetailId"))
			.businessUnitId(reader.getLong("businessUnitId"))
			.comments(comments)
			.statusId(reader.getLong("statusId"))
			.customerId(reader.getLong("customerId"))
			.calculationOrder(reader.getInteger("calculationOrder"))
			.distributionRightsOwnerId(reader.getLong("distributionRightsOwner"))
			.dealProduct(reader.columnExists("dealProductId") ? DealProductMapper.mapDataRowToDealProduct(reader) : null)
			.displaySourceType(reader.columnExists("displaySourceType") ? reader.getString("displaySourceType") : null)
			.reservationTypeId(reader.columnExists("reservation_type_id") ? reader.getString("reservation_type_id") : null)
			.lifecycleId(reader.columnExists("lifecycle_id") ? reader.getString("lifecycle_id") : null)
			.windowProduct(SalesWindowProductMapper.mapDataRowToSalesWindowProduct(reader))
			.currencyId(reader.columnExists("currency_code") ? reader.getString("currency_code") : null)
			.rightIdSet(rightIdSet)
			.hasCarveOut(reader.columnExists("hasCarveOut") ? reader.getBoolean("hasCarveOut") : null)
			.rightsGroupMedia(rightsGroupMedia)
			.rightsGroupTerritory(rightsGroupTerritory)
			.rightsGroupLanguage(rightsGroupLanguage)
			.elevatedProduct(elevatedProduct)
			.leafProductSet(leafProductSet)
			.productLevel(productLevel)
			.build();
	}

}
