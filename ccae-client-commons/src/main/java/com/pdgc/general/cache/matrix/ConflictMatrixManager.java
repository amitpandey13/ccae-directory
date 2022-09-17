package com.pdgc.general.cache.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.javatuples.Pair;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.ConflictTypeDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.maps.FoxConflictMatrix;

public final class ConflictMatrixManager extends LookupContainerManagerBase<Map<Pair<Long, Long>, Pair<Pair<Long, ConflictSeverity>, Pair<Long, ConflictSeverity>>>, FoxConflictMatrix> {

    public static final String CACHE_ITEM_NAME = "ConflictMatrix";
    
    private static final String queryFile = "queries/cache/matrix/conflictmatrix.properties";
    
    private static final Object initializeLock = new Object();
    private static ConflictMatrixManager instance;

    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private ConflictTypeDictionary conflictTypeDictionary;
    
    private ConflictMatrixManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        conflictTypeDictionary = ConflictTypeDictionary.getInstance();
        upstreamCaches.add(conflictTypeDictionary);
    }

    public static ConflictMatrixManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static synchronized void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ConflictMatrixManager(
                    props.getProperty("FETCH_CONFLICT_MATRIX")
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
    public Map<Pair<Long, Long>, Pair<Pair<Long, ConflictSeverity>, Pair<Long, ConflictSeverity>>> pullDataContainer() {
        Map<Pair<Long, Long>, Pair<Pair<Long, ConflictSeverity>, Pair<Long, ConflictSeverity>>> newMatrix = new HashMap<>();
        
        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Pair<Long, Long> rightTypeTuple = new Pair<Long, Long>(reader.getLong("PrimaryRightId"), reader.getLong("SecondaryRightId"));

            Pair<Long, ConflictSeverity> sameSourceConflict = new Pair<>(
            	reader.getLong("PsdConflictId"), 
            	reader.getInteger("PsdSeverityId") != null ? 
            		ConflictSeverity.byValue(reader.getInteger("PsdSeverityId")) : null);
            Pair<Long, ConflictSeverity> diffSourceConflict = new Pair<>(
            	reader.getLong("PddConflictId"), 
            	reader.getInteger("PddSeverityId") != null ?
            		ConflictSeverity.byValue(reader.getInteger("PddSeverityId")) : null);

            newMatrix.put(
                rightTypeTuple,
                new Pair<>(sameSourceConflict, diffSourceConflict));
        }
        
        return newMatrix;
    }

    @Override
    public FoxConflictMatrix buildObjectContainer(Map<Pair<Long, Long>, Pair<Pair<Long, ConflictSeverity>, Pair<Long, ConflictSeverity>>> idMatrix) {
        Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> newMatrix = new HashMap<>();
        
        for (Entry<Pair<Long, Long>, Pair<Pair<Long, ConflictSeverity>, Pair<Long, ConflictSeverity>>> entry : idMatrix.entrySet()) {
            Pair<Long, ConflictSeverity> sameSourceConflict = entry.getValue().getValue0();
            Pair<Long, ConflictSeverity> diffSourceConflict = entry.getValue().getValue1();

            newMatrix.put(entry.getKey(),
                new Pair<ConflictClass, ConflictClass>(
                    new ConflictClass(conflictTypeDictionary.get(sameSourceConflict.getValue0()), sameSourceConflict.getValue1()),
                    new ConflictClass(conflictTypeDictionary.get(diffSourceConflict.getValue0()), diffSourceConflict.getValue1())
                ));
        }

        return new FoxConflictMatrix(newMatrix);
    }
}
