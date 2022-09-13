package com.pdgc.avails.structures.criteria;

import java.util.Set;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.customer.Customer;

/**
 * Used to be its own class (formerly AvailsCriteria, then AvailsQuery) 
 * that had a full list of licensing and export criteria. 
 * 
 * As most of the rollup and export logic has shifted to be client-specific,
 * most fields became redundant and were just being transferred from the client-specific 
 * criteria, so this class has been reduced to an interface with only the parts needed 
 * for the shared AvailsCalculation and AvailsResult logic. 
 * 
 * @author Linda Xu
 *
 */
public interface AvailsQuery {

    public Customer getCustomer();
    public Term getEvaluatedPrimaryTerm();
    public boolean allowPartialAvailability();
    
    public Set<CriteriaSource> getCriteriaSources();
    public Set<Integer> getRequestedTerritoryIds();
}
