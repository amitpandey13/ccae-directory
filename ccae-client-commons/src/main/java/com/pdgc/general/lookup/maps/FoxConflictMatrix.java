package com.pdgc.general.lookup.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.classificationEnums.RightTypeType;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.CustomerGroup;
import com.pdgc.general.structures.rightstrand.impl.FoxDealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

/**
 * FoxConflictMatrix
 */
public class FoxConflictMatrix extends ConflictMatrix {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConflictMatrix.class);

    /**
     * Key: 1st Long = Primary Right Type, 2nd Long = Conflicting Right Type
     * Value:
     * 1st ConflictClass = same-source /same customer group / etc. conflicts
     * 2nd ConflictClass = difference-source/ different customer group / etc. conflicts
     */
    private Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> conflictMatrix;

    public FoxConflictMatrix(Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> conflictMatrix) {
        this.conflictMatrix = conflictMatrix;
    }

    /**
     * Get the conflict type and severity between two right strands
     *
     * @param primaryRightStrand     - right type of the owner
     * @param conflictingRightStrand
     * @return
     */
    @Override
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
    public ConflictClass getConflictType(RightStrand primaryRightStrand, RightStrand conflictingRightStrand) {

        if (primaryRightStrand.getRightType().allowsEpisodeLimit()) {
            LOGGER.debug("FoxConflictMatrix epLimit for primary {} {} and conflicting {} {} strandIds",
                primaryRightStrand.getRightStrandId(), primaryRightStrand.getRightType().getEpisodeLimit(),
                conflictingRightStrand.getRightStrandId(), conflictingRightStrand.getRightType().getEpisodeLimit());
            if (primaryRightStrand.getRightType().getRightTypeType() == RightTypeType.HOLDBACK) {
                if (conflictingRightStrand.getRightType().allowsEpisodeLimit() && conflictingRightStrand.getRightType().getEpisodeLimit() < primaryRightStrand.getRightType().getEpisodeLimit()) {
                    LOGGER.debug("FoxConflictMatrix first condition for primary {} and conflicting {} strandIds", primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
                    return null;
                }
            } else {
                if (conflictingRightStrand.getRightType().allowsEpisodeLimit() && primaryRightStrand.getRightType().getEpisodeLimit() < conflictingRightStrand.getRightType().getEpisodeLimit()) {
                    LOGGER.debug("FoxConflictMatrix 2nd condition for primary {} and conflicting {} strandIds", primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
                    return null;
                }
            }
            LOGGER.debug("FoxConflictMatrix not matched to a condition for primary {} and conflicting {} strandIds", primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
        }

        LOGGER.debug("FoxConflictMatrix gg for primary {} and conflicting {} strandIds", primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());

        //Tuple for the Conflict Matrix key: primary and conflicting right type Ids.
        if (primaryRightStrand.getRightType() == null || conflictingRightStrand.getRightType() == null) {
            LOGGER.warn("Primary strand {} or conflicting strand {} right types do not exist.", primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
            return null;
        }
        Pair<Long, Long> rightsTuple = new Pair<Long, Long>(primaryRightStrand.getRightType().getRightTypeId(), conflictingRightStrand.getRightType().getRightTypeId());
        if (!conflictMatrix.containsKey(rightsTuple)) {
            LOGGER.debug("FoxConflictMatrix for p {} and/or c {} no key for tuple.",
                primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
            return null;
        }


        Customer primaryCustomer = null;
        Customer conflictingCustomer = null;
        Long primaryStatusId = null;
        Long conflictingStatusId = null;
        String primaryContractType = null;
        String conflictingContractType = null;


        if (primaryRightStrand instanceof FoxDealRightStrand) {
            primaryCustomer = new Customer(((FoxDealRightStrand) primaryRightStrand).getCustomer());
            primaryStatusId = ((FoxDealRightStrand) primaryRightStrand).getStatusId();
            primaryContractType = ((FoxDealRightStrand) primaryRightStrand).getContractType();
        }

        if (conflictingRightStrand instanceof FoxDealRightStrand) {
            conflictingCustomer = new Customer(((FoxDealRightStrand) conflictingRightStrand).getCustomer());
            conflictingStatusId = ((FoxDealRightStrand) conflictingRightStrand).getStatusId();
            conflictingContractType = ((FoxDealRightStrand) conflictingRightStrand).getContractType();
        }

        // If either deal strand is Initial status, no conflict returned
        if (primaryStatusId != null && conflictingStatusId != null
            && (primaryStatusId.equals(Constants.INITIAL_STATUS) || conflictingStatusId.equals(Constants.INITIAL_STATUS)
            || primaryStatusId.equals(Constants.RIGHT_STRAND_STATUS_CANCELLED) || conflictingStatusId.equals(Constants.RIGHT_STRAND_STATUS_CANCELLED))) {
            LOGGER.debug("FoxConflictMatrix for p {} and/or c {} have initial or cancelled status.",
                primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
            return ConflictConstants.NO_CONFLICT;
        }


        // If the conflicting strand is Presented status, no conflict returned  (Primary presented still returns normal conflicts)
        if (conflictingStatusId != null && conflictingStatusId.equals(Constants.PRESENTED_STATUS)) {
            LOGGER.debug("FoxConflictMatrix for p {} and/or c {} have presented status.",
                primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
            return ConflictConstants.NO_CONFLICT;
        }

        // If the primary and conflicting deals are the same, then always use the same deal analysis (Value0)
        if (primaryRightStrand.getRightSource().getSourceId() == conflictingRightStrand.getRightSource().getSourceId()
            && primaryRightStrand.getRightSource().getSourceType().equals(conflictingRightStrand.getRightSource().getSourceType())
        ) {
            LOGGER.debug("FoxConflictMatrix for p {} and/or c {} are same deal.",
                primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
            return conflictMatrix.get(rightsTuple).getValue0();
        }

        //Ensure Customer of Default Type runs a "different-source" conflict
        if (primaryCustomer != null && conflictingCustomer != null && (Objects.equals(primaryCustomer.getCustomerId(), Constants.CUSTOMER_DEFAULT_ID)
            || Objects.equals(conflictingCustomer.getCustomerId(), Constants.CUSTOMER_DEFAULT_ID))) {
            return conflictMatrix.get(rightsTuple).getValue1();
        }

        ArrayList<CustomerGroup> primaryCustomerGroups;
        ArrayList<CustomerGroup> conflictingCustomerGroups;
        //Continue by evaluating customer and customerGroups
        if (primaryCustomer != null && conflictingCustomer != null
            && primaryRightStrand.getRightSource().getSourceType().equals(conflictingRightStrand.getRightSource().getSourceType())) {

            //if the customers are equal, treat conflict as same deal.
            if (Objects.equals(primaryCustomer, conflictingCustomer)) {
                if (primaryContractType.equals(conflictingContractType) 
                    || (!primaryContractType.equals(conflictingContractType)
                        && !primaryContractType.equals(Constants.RESERVATION_DISPLAY_SOURCE)
                        && !conflictingContractType.equals(Constants.RESERVATION_DISPLAY_SOURCE)
                        )) {
                    return conflictMatrix.get(rightsTuple).getValue0();
                } else {
                    return conflictMatrix.get(rightsTuple).getValue1();
                }
            }

            primaryCustomerGroups = (ArrayList<CustomerGroup>) primaryCustomer.getCustomerGroups();
            conflictingCustomerGroups = (ArrayList<CustomerGroup>) conflictingCustomer.getCustomerGroups();

            if (primaryCustomerGroups != null && conflictingCustomerGroups != null) {
                if (compareCustomerGroupsAndContractTypes(primaryCustomerGroups, conflictingCustomerGroups, primaryContractType, conflictingContractType)) {
                    LOGGER.debug("FoxConflictMatrix for p {} and/or c {} are in same customer group.",
                        primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
                    return conflictMatrix.get(rightsTuple).getValue0();
                } else {
                    LOGGER.debug("no customer groups in common: " + primaryCustomerGroups + "::" + conflictingCustomerGroups);
                    return conflictMatrix.get(rightsTuple).getValue1();
                }
            }
        }

        //else, return normal "different-source" conflicts.
        LOGGER.debug("FoxConflictMatrix for p {} and/or c {} are diff deal.",
            primaryRightStrand.getRightStrandId(), conflictingRightStrand.getRightStrandId());
        return conflictMatrix.get(rightsTuple).getValue1();
    }
    
    /*
     * If both sets are not disjoint then the same customer group conflict logic applies if:
     * 
     * 1) Both rightStrand contract types are in the specified list of contract types to consider
     *      AND they are the same contract type
     * OR
     *      
     * 2) One rightStrand or the other is NOT in the specified list of contract types
     * 
     * Otherwise, it is a nomral "different-source" conflict       
     */
    public boolean compareCustomerGroupsAndContractTypes(
        List<CustomerGroup> primaryCustomerGroups, 
        List<CustomerGroup> conflictingCustomerGroups, 
        String primaryContractType, 
        String conflictingContractType) {
        // if both sets are empty they are still disjoint
        
        return !Collections.disjoint(primaryCustomerGroups, conflictingCustomerGroups) 
                && 
                (
                    primaryContractType.equals(conflictingContractType) 
                    || (!primaryContractType.equals(conflictingContractType)
                        && !primaryContractType.equals(Constants.RESERVATION_DISPLAY_SOURCE)
                        && !conflictingContractType.equals(Constants.RESERVATION_DISPLAY_SOURCE)
                        )
                );
    }
}
