package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.customer.CustomerGenre;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;

import lombok.Builder;

/**
 * This works much like a CustomerListCarveOut.
 * Anything that falls within the genres of the carveOut will be considered valid.
 * All customers will have a list of customerGenres it's apart of.
 * <p>
 *
 * @author thomas
 * @see CustomerCarveOut
 */
public class CustomerGenreCarveOut extends FoxCarveOut {

    private static final long serialVersionUID = 1L;

    private Set<CustomerGenre> carveOutGenres;

    @Builder
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CustomerGenreCarveOut(
        Long carveOutId,
        FoxCarveOutType carveOutType,
        Term origTerm,
        Collection<TimePeriodPart> timePeriodParts,
        String carveOutComment,
        Collection<CustomerGenre> carveOutGenres,
        CarveOutImpactType carveOutImpactType,
        CarveOutCombineRule carveOutCombineRule,
        Integer carveOutOrder,
        Integer carveOutGroupId,
        CarveOutCombineRule carveOutGroupCombineRule,
        Integer carveOutGroupOrder
    ) {
        super(carveOutId, carveOutType, origTerm, timePeriodParts, carveOutComment, carveOutImpactType, carveOutCombineRule, carveOutOrder, carveOutGroupId, carveOutGroupCombineRule, carveOutGroupOrder);

        this.carveOutGenres = ImmutableSet.copyOf(carveOutGenres);

        StringBuilder carveOutStringBuilder = new StringBuilder();
		carveOutStringBuilder.append(carveOutImpactType.toString());
        carveOutStringBuilder.append(" Genres (").append(carveOutGenres.stream().map(CustomerGenre::getGenreName).collect(Collectors.joining(", "))).append(')');
        if (super.getCarveOutTerm() != null) {
            carveOutStringBuilder.append(" from ").append(super.getCarveOutTerm());
        }
        super.setCarveOutString(carveOutStringBuilder.toString());
    }
    
    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public CarveOutResult getCarveOutImpact(CarveOutImpactRequest request) {
        RightStrandCarveOutAction gapAction = super.getCarveOutImpactType() == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
        Term relevantTerm = request.term != null ? Term.getIntersectionTerm(request.term, super.getCarveOutTerm()) : super.getCarveOutTerm();
        TimePeriod relevantPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, super.getCarveOutTimePeriod()) : super.getCarveOutTimePeriod();

        if (relevantTerm == null || relevantPeriod.isEmpty()) {
            return new CarveOutResult(new HashMap<>(), gapAction);
        }

        RightStrandCarveOutAction rsAction;
        if (request.customer == null) {
            rsAction = RightStrandCarveOutAction.CONDITIONAL;
        } else {
            if (CustomerGenreCarveOutHelper.matchesGenre(request.customer, carveOutGenres)) {
                rsAction = (super.getCarveOutImpactType() == CarveOutImpactType.EXCEPT_AGAINST) ? RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
            } else {
                rsAction = (super.getCarveOutImpactType() == CarveOutImpactType.EXCEPT_AGAINST) ? RightStrandCarveOutAction.APPLY_RIGHT_STRAND : RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;
            }
        }

        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> carveOutImpact = new HashMap<>();
        carveOutImpact.put(relevantTerm, new HashMap<>());
        carveOutImpact.get(relevantTerm).put(relevantPeriod, rsAction);

        return new CarveOutResult(carveOutImpact, gapAction);
    }

    public Set<CustomerGenre> getCarveOutGenres() {
        return carveOutGenres;
    }
}
