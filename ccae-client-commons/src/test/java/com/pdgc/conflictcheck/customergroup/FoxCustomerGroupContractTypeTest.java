package com.pdgc.conflictcheck.customergroup;



import java.util.ArrayList;



import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.FoxConflictMatrix;
import com.pdgc.general.structures.customer.CustomerGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests when testing conflicts between customer groups
 * then contract type is considered correctly
 * 
 * @author MATTEO TANAKA
 *
 */
public class FoxCustomerGroupContractTypeTest {
    
    private static ArrayList<CustomerGroup> nonDisjointPrimaryCustomerGroup = new ArrayList<>();
    private static ArrayList<CustomerGroup> nonDisjointConflictingCustomerGroup = new ArrayList<>();
    private static ArrayList<CustomerGroup> disjointPrimaryCustomerGroup = new ArrayList<>();
    private static ArrayList<CustomerGroup> disjointConflictingCustomerGroup = new ArrayList<>();
    
    private static FoxConflictMatrix foxConflictMatrix;

    @BeforeAll
    private static void setup() {
        Constants.instantiateConstants();
        
        nonDisjointPrimaryCustomerGroup.add(new CustomerGroup(1L, "group 1"));
        nonDisjointConflictingCustomerGroup.add(new CustomerGroup(1L, "group 1"));
        disjointPrimaryCustomerGroup.add(new CustomerGroup(1L, "group 1"));
        disjointConflictingCustomerGroup.add(new CustomerGroup(2L, "group 2"));
        
        foxConflictMatrix = new FoxConflictMatrix(null);
    }
    
    @Test
    void givenNonDisjointCustomerGroups_whenContractTypesAreSame_expectCustomerGroupConflict() {
        assertTrue(foxConflictMatrix.compareCustomerGroupsAndContractTypes(nonDisjointPrimaryCustomerGroup, 
                nonDisjointConflictingCustomerGroup, 
                "Contract", 
                "Contract"));
    }
    
    @Test
    void givenNonDisjointCustomerGroups_whenContractTypesAreRelevantAndDifferent_expectDifferentSourceConflict() {
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(nonDisjointPrimaryCustomerGroup, 
                nonDisjointConflictingCustomerGroup, 
                "Contract", 
                "Reservation"));
    }
    
    @Test
    void givenNonDisjointCustomerGroups_whenContractTypesAreIrrelevant_expectCustomerGroupConflict() {
        assertTrue(foxConflictMatrix.compareCustomerGroupsAndContractTypes(nonDisjointPrimaryCustomerGroup, 
                nonDisjointConflictingCustomerGroup, 
                "Baseline", 
                "DRC"));
        
        assertTrue(foxConflictMatrix.compareCustomerGroupsAndContractTypes(nonDisjointPrimaryCustomerGroup, 
                nonDisjointConflictingCustomerGroup, 
                "Baseline", 
                "Contract"));
        
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(nonDisjointPrimaryCustomerGroup, 
                nonDisjointConflictingCustomerGroup, 
                "Baseline", 
                "Reservation"));
    }
    
    @Test
    void givenDisjointCustomerGroups_whenContractTypesAreRelevant_expectDifferentSourceConflicts() {
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(disjointPrimaryCustomerGroup, 
                disjointConflictingCustomerGroup, 
                "Contract", 
                "Contract"));
        
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(disjointPrimaryCustomerGroup, 
                disjointConflictingCustomerGroup, 
                "Reservation", 
                "Contract"));
    }
    
    @Test
    void givenDisjointCustomerGroups_whenContractTypesAreIrrelevant_expectDifferentSourceConflicts() {
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(disjointPrimaryCustomerGroup, 
                disjointConflictingCustomerGroup, 
                "Baseline", 
                "DRC"));
        
        assertFalse(foxConflictMatrix.compareCustomerGroupsAndContractTypes(disjointPrimaryCustomerGroup, 
                disjointConflictingCustomerGroup, 
                "Baseline", 
                "Contract"));
    }

}
