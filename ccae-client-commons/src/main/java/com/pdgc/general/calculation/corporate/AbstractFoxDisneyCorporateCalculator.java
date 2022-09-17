package com.pdgc.general.calculation.corporate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.CollectionsUtil;

/**
 * Parent class for holding logic shared by the fox and disney calculators,
 * as they are ultimately using the same outputs
 * @author Linda Xu
 *
 */
public abstract class AbstractFoxDisneyCorporateCalculator extends CorporateCalculator {

    /**
     * Returns true if there are no corporate strands
     * @return
     */
    public boolean hasNoRights(
        CorporateCalculatorParams params
    ) {
        return CollectionsUtil.isNullOrEmpty(params.rightStrands);
    }
    
    @Override
    public FoxAvailsCorporateCalculatorResult calculateForAvails(
        CorporateCalculatorParams params
    ) {
        //Don't bother with actual work if there are no right strands
        if (hasNoRights(params)) {
            return getEmptyAvailsResult(params);
        }
        return calculateForNonEmptyAvails(params);
    }
    
    public abstract FoxAvailsCorporateCalculatorResult calculateForNonEmptyAvails(CorporateCalculatorParams params);
    
    @Override
    public FoxConflictCorporateCalculatorResult calculateForConflictCheck(CorporateCalculatorParams params) {
        //Don't bother with actual work if there are no right strands
        if (hasNoRights(params)) {
            return getEmptyConflictResult(params);
        }
        return calculateForNonEmptyConflictCheck(params);
    }
    
    public abstract FoxConflictCorporateCalculatorResult calculateForNonEmptyConflictCheck(CorporateCalculatorParams params);
    
    public FoxAvailsCorporateCalculatorResult getEmptyAvailsResult(
        CorporateCalculatorParams params
    ) {
        Map<RightType, Map<TermPeriod, AvailsCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                .put(
                    new TermPeriod(calcRequest.requestedTerm, calcRequest.requestedTimePeriod), 
                    new AvailsCorpResult(
                        new AvailabilityResult(
                            getDefaultAvailability(calcRequest.requestedRightType), 
                            Arrays.asList(FoxWindowReasonDetail.NO_CORP_RIGHTS)
                        ),
                        new HashMap<>()
                    )
                );
        }
        return new FoxAvailsCorporateCalculatorResult(
            resultMap,
            new HashMap<>(),
            new HashSet<>(),
            false,
            false
        );
    }

    public FoxConflictCorporateCalculatorResult getEmptyConflictResult(
        CorporateCalculatorParams params
    ) {
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                .put(
                    new TermPeriod(calcRequest.requestedTerm, calcRequest.requestedTimePeriod), 
                    new ConflictCorpResult(
                        new AvailabilityResult(
                            getDefaultAvailability(calcRequest.requestedRightType), 
                            Arrays.asList(FoxWindowReasonDetail.NO_CORP_RIGHTS)
                        ),
                        new HashSet<>()
                    )
                );
        }
        return new FoxConflictCorporateCalculatorResult(resultMap);
    }
    
    public Availability getDefaultAvailability(
        RightType requestedCorpType
    ) {
        // For right types do not require distribution rights in order to be Available. 
        // Default to YES (restrictions and other exclusivities will still impact availability). 
        if (!needsDistributionRights(requestedCorpType)) {
            return Availability.YES;
        }
        return Availability.UNSET;
    }
    
    public static boolean needsDistributionRights(RightType requestedCorpType) {
        // For right types that require exclusive corp avail (e.g. exclusivity, blocks, etc), 
        // we do not require distribution rights in order to be Available. 
        return (Long.compare(requestedCorpType.getRightTypeId(), Constants.EXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID) != 0 
                && Long.compare(requestedCorpType.getRightTypeId(), Constants.IGNORED_CORP_AVAIL_RIGHT_TYPE_ID) != 0);
    }
    
    public FoxWindowReasonDetail convertRecordDetailToWindowDetail(
        FoxRecordReasonDetail recordDetail
    ) {
        FoxWindowReasonDetail windowDetail = null;
        switch (recordDetail) {
            case RESTRICTS_AVAILABILITY:
                windowDetail = FoxWindowReasonDetail.RESTRICTED_RIGHTS;
                break;
            case PRELIMINARY_DISTRIBUTION:
                windowDetail = FoxWindowReasonDetail.PRELIMINARY_DISTRIBUTION;
                break;
            case PRIOR_TO_SALES_WINDOW:
                windowDetail = FoxWindowReasonDetail.PRIOR_TO_SALES_WINDOW;
                break;
            case EXCEPTION_DEFAULT:
                windowDetail = FoxWindowReasonDetail.EXCEPTION_DEFAULT;
                break;
            default:
                break;
        }
        return windowDetail;
    }
    
    /**
     * Output structure for storing the results of the term-period analysis
     * @author Linda Xu
     */
    protected class TermPeriodAnalysisResults {
        Map<TermPeriod, AvailabilityResult> availabilityMap;
        Map<TermPeriod, Map<RightStrand, AvailabilityResult>> rightStrandImpactMap; //only read by avails, but good for informational purposes
    }
}
