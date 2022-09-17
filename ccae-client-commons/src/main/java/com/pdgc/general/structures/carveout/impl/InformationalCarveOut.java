package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;

import lombok.Builder;

/**
 * This describes carveouts that are unknown to our system.
 * CarveOuts of this type always resolve rightStrands that overlap it
 * as CONDITIONAL when it's a conflict.
 * <p>
 *
 * @author thomas
 */
public class InformationalCarveOut extends FoxCarveOut {

    private static final long serialVersionUID = 1L;

    private int carveOutTypeId;

    @Builder
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public InformationalCarveOut(
        Long carveOutId,
        FoxCarveOutType carveOutType,
        Term origTerm,
        Collection<TimePeriodPart> timePeriodParts,
        String carveOutComment,
        int carveOutTypeId,
        CarveOutImpactType carveOutImpactType,
        CarveOutCombineRule carveOutCombineRule,
        Integer carveOutOrder,
        Integer carveOutGroupId,
        CarveOutCombineRule carveOutGroupCombineRule,
        Integer carveOutGroupOrder
    ) {
        super(carveOutId, carveOutType, origTerm, timePeriodParts, carveOutComment, carveOutImpactType, carveOutCombineRule, carveOutOrder, carveOutGroupId, carveOutGroupCombineRule, carveOutGroupOrder);

        this.carveOutTypeId = carveOutTypeId;

        StringBuilder carveOutStringBuilder = new StringBuilder();
		carveOutStringBuilder.append("General");
        if (super.getCarveOutTerm() != null) {
            carveOutStringBuilder.append(" from ").append(super.getCarveOutTerm());
        }
        super.setCarveOutString(carveOutStringBuilder.toString());
    }

    @Override
    @SuppressWarnings("PMD")
    public CarveOutResult getCarveOutImpact(CarveOutImpactRequest request) {
        RightStrandCarveOutAction gapAction = super.getCarveOutImpactType() == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
        Term relevantTerm = request.term != null ? Term.getIntersectionTerm(request.term, super.getCarveOutTerm()) : super.getCarveOutTerm();
        TimePeriod relevantPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, super.getCarveOutTimePeriod()) : super.getCarveOutTimePeriod();

        if (relevantTerm == null || relevantPeriod.isEmpty()) {
            return new CarveOutResult(new HashMap<>(), gapAction);
        }

        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> carveOutImpact = new HashMap<>();
        carveOutImpact.put(relevantTerm, new HashMap<>());
        carveOutImpact.get(relevantTerm).put(relevantPeriod, RightStrandCarveOutAction.CONDITIONAL);

        return new CarveOutResult(carveOutImpact, gapAction);
    }

    public int getCarveOutTypeId() {
        return carveOutTypeId;
    }
}
