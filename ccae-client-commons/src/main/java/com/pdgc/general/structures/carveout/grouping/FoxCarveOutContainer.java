package com.pdgc.general.structures.carveout.grouping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.calculation.carveout.CarveOutGroupAction;
import com.pdgc.general.calculation.carveout.CarveOutGroupResult;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.impl.CustomerCarveOut;
import com.pdgc.general.structures.carveout.impl.CustomerCountCarveOut;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

import lombok.Builder;

/**
 * Fox specific structure for CarveOutContainers
 *
 * @author thomas
 * @see CarveOutContainer
 */
public class FoxCarveOutContainer extends CarveOutContainer {

    private static final long serialVersionUID = 1L;

    private Set<CarveOutGroup> carveOutGroups;
    private Set<CarveOut> childCarveOuts;

    @Builder
    public FoxCarveOutContainer(
        Term parentTerm,
        TimePeriod parentTimePeriod,
        Iterable<CarveOutGroup> carveOutGroups
    ) {
        super(parentTerm, parentTimePeriod);

        List<CarveOutGroup> compressedGroups = new ArrayList<>();
        for (CarveOutGroup carveOutGroup : carveOutGroups) {
            compressedGroups.add(new CarveOutGroup(
                carveOutGroup.carveOutCombineRule,
                carveOutGroup.carveOutGroupId,
                carveOutGroup.carveOutGroupOrder,
                combineWithCustomerCountCarveOuts(carveOutGroup.getChildCarveOuts())
            ));
        }
        this.carveOutGroups = ImmutableSet.copyOf(compressedGroups);

        childCarveOuts = new HashSet<>();
        for (CarveOutGroup carveOutGroup : compressedGroups) {
            childCarveOuts.addAll(carveOutGroup.getChildCarveOuts());
        }
        childCarveOuts = Collections.unmodifiableSet(childCarveOuts);
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> getCarveOutImpact(
        CarveOutImpactRequest request
    ) {
        Term limitingTerm = request.term != null ? Term.getIntersectionTerm(request.term, parentTerm) : parentTerm;
        if (limitingTerm == null) {
        	return new HashMap<Term, Map<TimePeriod, RightStrandCarveOutAction>>();
        }
        TimePeriod limitingPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, parentTimePeriod) : parentTimePeriod;

        MergedCarveOutResult mergedResults = mergeCarveOutResults(carveOutGroups.stream().map(co -> co.getCarveOutImpact(request)).collect(Collectors.toSet()));
        RightStrandCarveOutAction overallGapAction = mergedResults.gapAction;

        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> cleanedCarveOutTermsMap = new HashMap<>();

        for (Entry<Term, Map<TimePeriod, Set<CarveOutGroupAction>>> termEntry : mergedResults.carveOutActionMap.entrySet()) {

            Term relevantTerm = Term.getIntersectionTerm(limitingTerm, termEntry.getKey());
            if (relevantTerm == null) {
                continue;
            }

            Map<TimePeriod, RightStrandCarveOutAction> cleanedPeriodMap = new HashMap<>();

            for (Entry<TimePeriod, Set<CarveOutGroupAction>> periodEntry : termEntry.getValue().entrySet()) {

                TimePeriod relevantPeriod = TimePeriod.intersectPeriods(request.timePeriod, periodEntry.getKey());
                if (relevantPeriod.isEmpty()) {
                    continue;
                }

                List<RightStrandCarveOutAction> impactOrder;
                RightStrandCarveOutAction finalCarveOutAction = RightStrandCarveOutAction.UNSET;
                Set<RightStrandCarveOutAction> actionSet = new HashSet<>();

                List<CarveOutGroupAction> sortedCarveOutGroups = periodEntry.getValue().stream().sorted(Comparator.comparing(CarveOutGroupAction::getCarveOutGroupOrder)).collect(Collectors.toList());
                for (CarveOutGroupAction action : sortedCarveOutGroups) {
                    actionSet.clear();
                    // add your last carveOutAction
                    actionSet.add(finalCarveOutAction);
                    // add your next carveOutAction
                    actionSet.add(action.getAction());
                    // determine impactOrder based off of next carveOut's impact (sorted by carveOutOrder ascending)
                    impactOrder = CarveOutGroup.calculateImpactOrder(action.getCarveOutCombineRule());
                    for (RightStrandCarveOutAction impactKey : impactOrder) {
                        if (actionSet.contains(impactKey)) {
                            finalCarveOutAction = impactKey;
                            break;
                        }
                    }
                }

                if (finalCarveOutAction == RightStrandCarveOutAction.UNSET) {
                    finalCarveOutAction = overallGapAction;
                }

                cleanedPeriodMap.put(periodEntry.getKey(), finalCarveOutAction);
            }

            cleanedCarveOutTermsMap.put(termEntry.getKey(), cleanedPeriodMap);
        }

        //Find and fill the gaps of cleanedCarveOutTermsMap with overallGapAction
        {
            for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : cleanedCarveOutTermsMap.entrySet()) {
                TimePeriod gapPeriod = limitingPeriod;
                for (TimePeriod period : termEntry.getValue().keySet()) {
                    gapPeriod = TimePeriod.subtractPeriods(gapPeriod, period);
                }

                if (!gapPeriod.isEmpty()) {
                    termEntry.getValue().put(gapPeriod, overallGapAction);
                }
            }

            Collection<Term> gapTerms = DateTimeUtil.findGapTerms(cleanedCarveOutTermsMap.keySet(), limitingTerm);
            for (Term gapTerm : gapTerms) {
                Map<TimePeriod, RightStrandCarveOutAction> gapPeriodMap = new HashMap<>();
                gapPeriodMap.put(limitingPeriod, overallGapAction);
                cleanedCarveOutTermsMap.put(gapTerm, gapPeriodMap);
            }
        }

        return cleanedCarveOutTermsMap;
    }

    @Override
    public Set<CarveOut> getAllCarveOuts() {
        return childCarveOuts;
    }

    public Set<CarveOutGroup> getCarveOutGroups() {
        return carveOutGroups;
    }

    /**
     * Combine customerLIst and customerCountCarveOut
     *
     * @param carveOuts
     * @return
     */
    private Collection<CarveOut> combineWithCustomerCountCarveOuts(Collection<CarveOut> carveOuts) {
        List<CarveOut> result = new ArrayList<>();

        List<CarveOut> compressingGroup = new ArrayList<>();
        List<CustomerCountCarveOut> customerCountCarveOuts = new ArrayList<>();
        List<CarveOut> sortedCarveOuts = carveOuts.stream().sorted(Comparator.comparing(CarveOut::getCarveOutOrder)).collect(Collectors.toList());
        for (CarveOut carveOut : sortedCarveOuts) {
            // If we hit this then we have a group to analyze now whether we can compress it
            if (carveOut.getCarveOutCombineRule().equals(CarveOutCombineRule.AND)) {
                Collection<CarveOut> filteredGroup = new ArrayList<>();
                if (!CollectionsUtil.isNullOrEmpty(customerCountCarveOuts)) {
                    filteredGroup = compressCarveOuts(customerCountCarveOuts, compressingGroup);
                } else {
                    filteredGroup.addAll(compressingGroup);
                }
                result.addAll(filteredGroup);
                compressingGroup.clear();
                customerCountCarveOuts.clear();
            }

            if (carveOut instanceof CustomerCountCarveOut) {
                customerCountCarveOuts.add((CustomerCountCarveOut) carveOut);
            }
            compressingGroup.add(carveOut);
        }

        // Last iteration to catch if we didn't hit an AND at the very end. Otherwise we'll miss carveOUts
        if (!CollectionsUtil.isNullOrEmpty(compressingGroup)) {
            Collection<CarveOut> filteredGroup = new ArrayList<>();
            if (!CollectionsUtil.isNullOrEmpty(customerCountCarveOuts)) {
                filteredGroup = compressCarveOuts(customerCountCarveOuts, compressingGroup);
            } else {
                filteredGroup.addAll(compressingGroup);
            }
            result.addAll(filteredGroup);
            compressingGroup.clear();
            customerCountCarveOuts.clear();
        }
        return result;
    }

    /**
     * Designed to scan over a list of carveouts for CustomerList CarveOuts to add to customerCountCarveOuts
     *
     * @param masterCarveOut
     * @param carveOuts
     * @return
     */
    private Collection<CarveOut> compressCarveOuts(List<CustomerCountCarveOut> customerCountCarveOuts, List<CarveOut> carveOuts) {
        Set<CarveOut> compressedCarveOuts = new HashSet<>(carveOuts);

        compressedCarveOuts.removeAll(customerCountCarveOuts);
        for (CustomerCountCarveOut customerCountCarveOut : customerCountCarveOuts) {
            List<CarveOut> fittingCarveOuts = carveOuts.stream().filter(co -> co instanceof CustomerCarveOut && co.getOrigTerm().equals(customerCountCarveOut.getOrigTerm())).collect(Collectors.toList());
            customerCountCarveOut.getInternalBrandedCustomers().addAll(
                fittingCarveOuts.stream().flatMap(co -> ((CustomerCarveOut) co).getCarveOutCustomers().stream()).collect(Collectors.toList()));
            compressedCarveOuts.removeAll(fittingCarveOuts);
            compressedCarveOuts.add(customerCountCarveOut);
        }
        return compressedCarveOuts;
    }

    /**
     * Groups the carveOutResults by the term/period actions
     *
     * @param carveOutGroupResults
     * @return
     */
    private MergedCarveOutResult mergeCarveOutResults(
        Collection<CarveOutGroupResult> carveOutGroupResults
    ) {
        Map<Term, Map<TimePeriod, Set<CarveOutGroupAction>>> carveOutTermsMap = new HashMap<>();

        RightStrandCarveOutAction overallGapAction = RightStrandCarveOutAction.IGNORE_RIGHT_STRAND;

        Supplier<Set<CarveOutGroupAction>> defaultValueProducer = new Supplier<Set<CarveOutGroupAction>>() {
            @Override
            public Set<CarveOutGroupAction> get() {
                return new HashSet<>();
            }
        };

        Function<Set<CarveOutGroupAction>, Set<CarveOutGroupAction>> valueDeepCopy = new Function<Set<CarveOutGroupAction>, Set<CarveOutGroupAction>>() {
            @Override
            public Set<CarveOutGroupAction> apply(Set<CarveOutGroupAction> t) {
                return new HashSet<>(t);
            }
        };

        for (CarveOutGroupResult carveOutGroupResult : carveOutGroupResults) {
            //Apply the actual carveout
            for (Entry<Term, Map<TimePeriod, CarveOutGroupAction>> termEntry : carveOutGroupResult.impactMap.entrySet()) {
                for (Entry<TimePeriod, CarveOutGroupAction> periodEntry : termEntry.getValue().entrySet()) {

                    Function<Set<CarveOutGroupAction>, Set<CarveOutGroupAction>> valueUpdater = new Function<Set<CarveOutGroupAction>, Set<CarveOutGroupAction>>() {
                        @Override
                        public Set<CarveOutGroupAction> apply(Set<CarveOutGroupAction> t) {
                            t.add(periodEntry.getValue());
                            return t;
                        }
                    };

                    DateTimeUtil.updateTermPeriodValueMap(
                        carveOutTermsMap,
                        termEntry.getKey(),
                        periodEntry.getKey(),
                        defaultValueProducer,
                        valueDeepCopy,
                        valueUpdater
                    );
                }
            }

            //Update the gapAction for the right strand
            if (carveOutGroupResult.gapAction.getValue() > overallGapAction.getValue()) {
                overallGapAction = carveOutGroupResult.gapAction;
            }
        }

        return new MergedCarveOutResult(carveOutTermsMap, overallGapAction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        return Objects.equals(parentTerm, ((FoxCarveOutContainer) obj).parentTerm)
            && Objects.equals(parentTimePeriod, ((FoxCarveOutContainer) obj).parentTimePeriod)
            && Objects.equals(carveOutGroups, ((FoxCarveOutContainer) obj).carveOutGroups)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentTerm)
            ^ Objects.hashCode(parentTimePeriod)
            ^ Objects.hashCode(carveOutGroups)
            ;
    }

    private class MergedCarveOutResult {
        Map<Term, Map<TimePeriod, Set<CarveOutGroupAction>>> carveOutActionMap;
        RightStrandCarveOutAction gapAction;

        public MergedCarveOutResult(
            Map<Term, Map<TimePeriod, Set<CarveOutGroupAction>>> carveOutActionMap,
            RightStrandCarveOutAction gapAction
        ) {
            this.carveOutActionMap = carveOutActionMap;
            this.gapAction = gapAction;
        }
    }
}
