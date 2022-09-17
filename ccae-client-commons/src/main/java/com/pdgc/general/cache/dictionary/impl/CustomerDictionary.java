package com.pdgc.general.cache.dictionary.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.pdgc.ccae.dao.intermediateobjects.CustomerEntry;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.AbstractDependantDictionaryBase;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.cache.dictionary.impl.*;
import com.pdgc.ccae.dao.intermediateobjects.*;
public class CustomerDictionary extends AbstractDependantDictionaryBase<KeyWithBusinessUnit<Long>, Customer, CustomerEntry> {

    public static final String CACHE_ITEM_NAME = "CustomerDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/customer.properties";
    
    private static final Object initializeLock = new Object();
    private static CustomerDictionary instance;

    //Dictionaries this one's object depends on
    private CustomerTypeDictionary customerTypeDictionary;
    private CustomerGenreDictionary customerGenreDictionary;
    private CustomerGroupDictionary customerGroupDictionary;
    
    private CustomerDictionary(
        String fetchAllQuery, 
        String lookupQuery, 
        String customerTypeFetchAllQuery, 
        String customerTypeLookupQuery,
        String customerGenreFetchAllQuery, 
        String customerGenreLookupQuery,
        String customerGroupFetchAllQuery,
        String customerGroupLookupQuery
    ) {
        entryDictionary = new CustomerEntryDictionary(
            fetchAllQuery,
            lookupQuery,
            customerTypeFetchAllQuery,
            customerTypeLookupQuery,
            customerGenreFetchAllQuery,
            customerGenreLookupQuery,
            customerGroupFetchAllQuery,
            customerGroupLookupQuery
        );
        
        customerTypeDictionary = CustomerTypeDictionary.getInstance();
        upstreamCaches.add(customerTypeDictionary);
        
        customerGenreDictionary = CustomerGenreDictionary.getInstance();
        upstreamCaches.add(customerGenreDictionary);
        
        customerGroupDictionary = CustomerGroupDictionary.getInstance();
        upstreamCaches.add(customerGroupDictionary);
    }

    public static CustomerDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new CustomerDictionary(
                    props.getProperty("FETCH_ALL_QUERY_CUSTOMER"),
                    props.getProperty("LOOK_UP_QUERY_CUSTOMER"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_TYPE"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_TYPE_LOOKUP"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_GENRE"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_GENRE_LOOKUP"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_GROUP"),
                    props.getProperty("ONE_TO_MANY_CUSTOMER_GROUP_LOOKUP")
                );
                MasterDataCacheManager.getInstance().addCacheItem(instance);
            }
        }
    }
    
    @Override
    public String getCacheItemName() {
        return null;
    }
    
    @Override
    protected Customer mapEntryToObject(CustomerEntry entry) {
        return new CustomerBuilder(entry.getCustomerId().getId(), entry.getCustomerId().getBusinessUnitId())
                .customerName(entry.getCustomerName())
                .customerTypes(CollectionsUtil.select(entry.getCustomerTypes(), customerTypeDictionary::get))
                .customerGenres(CollectionsUtil.select(entry.getCustomerGenres(), customerGenreDictionary::get))
                .customerGroups(CollectionsUtil.select(entry.getCustomerGroups(), customerGroupDictionary::get))
                .build();
    }
    
    @Override
    public boolean isPopulated() {
    	return entryDictionary.isPopulated();
    }

    private static class CustomerEntryDictionary extends DictionaryContainerBase<KeyWithBusinessUnit<Long>, CustomerEntry> {

        private static final String CUSTOMER_TYPE = "customerType";
        private static final String CUSTOMER_GENRE = "customerGenre";
        private static final String CUSTOMER_GROUP = "customerGroup";
        
        private CustomerEntryDictionary(
            String fetchAllQuery, 
            String lookupQuery, 
            String customerTypeFetchAllQuery, 
            String customerTypeLookupQuery,
            String customerGenreFetchAllQuery, 
            String customerGenreLookupQuery,
            String customerGroupFetchAllQuery,
            String customerGroupLookupQuery
        ) {
            super(fetchAllQuery, lookupQuery, createOneToManyQueries(
                customerTypeFetchAllQuery, 
                customerTypeLookupQuery,
                customerGenreFetchAllQuery, 
                customerGenreLookupQuery,
                customerGroupFetchAllQuery,
                customerGroupLookupQuery
            ));
        }
        
        private static Collection<OneToManyQuery<KeyWithBusinessUnit<Long>, ?>> createOneToManyQueries(
        		String customerTypeFetchAllQuery, 
                String customerTypeLookupQuery,
                String customerGenreFetchAllQuery, 
                String customerGenreLookupQuery,
                String customerGroupFetchAllQuery,
                String customerGroupLookupQuery
        ) {
            List<OneToManyQuery<KeyWithBusinessUnit<Long>, ?>> queries = new ArrayList<>();
            queries.add(new CustomerTypeQuery(customerTypeFetchAllQuery, customerTypeLookupQuery));
            queries.add(new CustomerGenreQuery(customerGenreFetchAllQuery, customerGenreLookupQuery));
            queries.add(new CustomerGroupQuery(customerGroupFetchAllQuery, customerGroupLookupQuery));
            return queries;
        }
        
        @Override
        public String getCacheItemName() {
            return CACHE_ITEM_NAME;
        }

        @Override
        protected Map<String, Object> getLookupQueryParameters(KeyWithBusinessUnit<Long> key) {
            Map<String, Object> params = new HashMap<>();
            params.put("customerId", key.getId());
            params.put("businessUnitId", key.getBusinessUnitId());
            
            return params;
        }

        @Override
        protected KeyWithBusinessUnit<Long> mapToKey(DataRow reader) {
            return new KeyWithBusinessUnit<>(reader.getLong("id"), reader.getLong("businessUnitId"));
        }

        @Override
        protected CustomerEntry mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
            Set<Long> customerTypeIds = new HashSet<>();
            Set<Long> customerGenreIds = new HashSet<>();
            Set<Long> customerGroupIds = new HashSet<>();
            
            if (oneToManyResults != null) {
                for (Entry<String, Collection<Object>> oneToManyResult : oneToManyResults.entrySet()) {
                    switch (oneToManyResult.getKey()) {
                        case CUSTOMER_TYPE:
                            for (Object obj : oneToManyResult.getValue()) {
                                customerTypeIds.add((Long)obj);
                            }
                            break;
                        case CUSTOMER_GENRE:
                            for (Object obj : oneToManyResult.getValue()) {
                                customerGenreIds.add((Long)obj);
                            }
                            break;
                        case CUSTOMER_GROUP:
                            for (Object obj : oneToManyResult.getValue()) {
                                customerGroupIds.add((Long)obj);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            
            return CustomerEntry.builder()
                .customerId(new KeyWithBusinessUnit<>(reader.getLong("id"), reader.getLong("businessUnitId")))
                .customerName(reader.getString("name"))
                .customerTypes(Collections.unmodifiableSet(customerTypeIds))
                .customerGenres(Collections.unmodifiableSet(customerGenreIds))
                .customerGroups(Collections.unmodifiableSet(customerGroupIds))
                .build();
        }
        
        private static class CustomerTypeQuery extends OneToManyQuery<KeyWithBusinessUnit<Long>, Long> {
            
            CustomerTypeQuery(String fetchAllQuery, String lookupQuery) {
                super(CUSTOMER_TYPE, fetchAllQuery, lookupQuery);
            }

            @Override
            protected Map<String, Object> getLookupQueryParameters(KeyWithBusinessUnit<Long> key) {
                Map<String, Object> params = new HashMap<>();
                params.put("customerId", key.getId());
                params.put("businessUnitId", key.getBusinessUnitId());
                return params;
            }

            @Override
            protected KeyWithBusinessUnit<Long> mapToKey(DataRow reader) {
                return new KeyWithBusinessUnit<>(reader.getLong("customerId"), reader.getLong("businessUnitId"));
            }

            @Override
            protected Long mapToValue(DataRow reader) {
                return reader.getLong("customerTypeId");
            }
        }

        private static class CustomerGenreQuery extends OneToManyQuery<KeyWithBusinessUnit<Long>, Long> {
            
            CustomerGenreQuery(String fetchAllQuery, String lookupQuery) {
                super(CUSTOMER_GENRE, fetchAllQuery, lookupQuery);
            }

            @Override
            protected Map<String, Object> getLookupQueryParameters(KeyWithBusinessUnit<Long> key) {
                Map<String, Object> params = new HashMap<>();
                params.put("customerId", key.getId());
                params.put("businessUnitId", key.getBusinessUnitId());
                return params;
            }

            @Override
            protected KeyWithBusinessUnit<Long> mapToKey(DataRow reader) {
                return new KeyWithBusinessUnit<>(reader.getLong("customerId"), reader.getLong("businessUnitId"));
            }

            @Override
            protected Long mapToValue(DataRow reader) {
                return reader.getLong("customerGenreId");
            }
        }
        
        private static class CustomerGroupQuery extends OneToManyQuery<KeyWithBusinessUnit<Long>, Long> {
            
            CustomerGroupQuery(String fetchAllQuery, String lookupQuery) {
                super(CUSTOMER_GROUP, fetchAllQuery, lookupQuery);
            }

            @Override
            protected Map<String, Object> getLookupQueryParameters(KeyWithBusinessUnit<Long> key) {
                Map<String, Object> params = new HashMap<>();
                params.put("customerId", key.getId());
                params.put("businessUnitId", key.getBusinessUnitId());
                return params;
            }

            @Override
            protected KeyWithBusinessUnit<Long> mapToKey(DataRow reader) {
                return new KeyWithBusinessUnit<>(reader.getLong("customerId"), reader.getLong("businessUnitId"));
            }

            @Override
            protected Long mapToValue(DataRow reader) {
                return reader.getLong("customerGroupId");
            }
        }
    }
}
