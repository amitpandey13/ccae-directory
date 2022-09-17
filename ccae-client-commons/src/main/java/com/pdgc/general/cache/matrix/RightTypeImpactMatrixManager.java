package com.pdgc.general.cache.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.javatuples.Pair;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;

public final class RightTypeImpactMatrixManager extends LookupContainerManagerBase<Map<Pair<Long, Long>, Availability>, RightTypeImpactMatrix> {

    public static final String CACHE_ITEM_NAME = "RightTypeImpactMatrix";
    
    private static final String queryFile = "queries/cache/matrix/righttypeimpact.properties";
    
    private static final Object initializeLock = new Object();
    private static RightTypeImpactMatrixManager instance;

    private String fetchQuery;
    
    private RightTypeImpactMatrixManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
    }

    public static RightTypeImpactMatrixManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new RightTypeImpactMatrixManager(
                    props.getProperty("FETCH_RIGHT_TYPE_IMPACT_MATRIX")
                );
                MasterDataCacheManager.getInstance().addCacheItem(instance);
            }
        }
    }
    
    @Override
    public String getCacheItemName() {
        return CACHE_ITEM_NAME;
    }

    @Override
    public Map<Pair<Long, Long>, Availability> pullDataContainer() {
        Map<Pair<Long, Long>, Availability> newMatrix = new HashMap<>();

        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Availability availability = Availability.byValue(reader.getInteger("availabilityId"));
            newMatrix.put(new Pair<Long, Long>(reader.getLong("requestedRightId"), reader.getLong("existingRightId")),
                availability);
        }

        return newMatrix;
    }
    
    @Override
    public RightTypeImpactMatrix buildObjectContainer(Map<Pair<Long, Long>, Availability> idMatrix) {
        return new RightTypeImpactMatrix(idMatrix);
    }
}
