package com.pdgc.general.structures.carveout.grouping;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.calculation.carveout.CarveOutGroupAction;
import com.pdgc.general.calculation.carveout.CarveOutGroupResult;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

import lombok.Builder;

/**
 * A grouping mechanism for carveOuts. We're allowing the functionality of
 * essentially giving parentheses to a group of carveOuts. <p>
 * 
 * So you can have something that looks like this: <p>
 *        Group 1 			   GROUP 2 <br>
 *   (C1 OR C2 AND C3) OR (C4 AND C5 AND C6)
 * <br>
 * In this way the group of carveOuts will unite to give a single answer on the
 * terms they affect
 * 
 * @author thomas
 * @see CarveOutContainer
 * 
 */
public class CarveOutGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	public CarveOutImpactType carveOutImpactType = CarveOutImpactType.EXCEPT_AGAINST;
	public CarveOutCombineRule carveOutCombineRule;
	public Integer carveOutGroupId;
	public Integer carveOutGroupOrder;
	private Set<CarveOut> childCarveOuts;

	@Builder
	public CarveOutGroup(
		CarveOutCombineRule carveOutCombineRule, 
		Integer carveOutGroupId,
		Integer carveOutGroupOrder,
		Iterable<? extends CarveOut> childCarveOutsIn
	) {
		this.carveOutGroupId = carveOutGroupId;
		this.carveOutGroupOrder = carveOutGroupOrder;
		
		this.carveOutCombineRule = carveOutCombineRule;
		this.childCarveOuts = ImmutableSet.copyOf(childCarveOutsIn);
	}

	public CarveOutGroupResult getCarveOutImpact(CarveOutImpactRequest request) {
		Map<Term, Map<TimePeriod, Set<CarveOut>>> carveOutTermsMap = new HashMap<>();
	
		Supplier<Set<CarveOut>> defaultValueProducer = new Supplier<Set<CarveOut>>() {
			@Override
			public Set<CarveOut> get() {
				return new HashSet<>();
			}
        };
        
        Function<Set<CarveOut>, Set<CarveOut>> valueDeepCopy = new Function<Set<CarveOut>, Set<CarveOut>>() {
			@Override
			public Set<CarveOut> apply(Set<CarveOut> t) {
				return new HashSet<>(t);
			}
        };
        
        // This will cut up your carveOuts within the group into the smallest denominator carveOut Term
        for (CarveOut carveOut : childCarveOuts) {
        	CarveOutResult carveOutResult = carveOut.getCarveOutImpact(request);
        	//Apply the actual carveout
        	for(Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : carveOutResult.impactMap.entrySet()) {
        		for (Entry<TimePeriod, RightStrandCarveOutAction> periodEntry : termEntry.getValue().entrySet()) {
        			
        			CarveOut actionCarveOut = SerializationUtils.clone(carveOut);
        			actionCarveOut.setCarveOutAction(periodEntry.getValue());
        			
        			Function<Set<CarveOut>, Set<CarveOut>> valueUpdater = new Function<Set<CarveOut>, Set<CarveOut>>() {
						@Override
						public Set<CarveOut> apply(Set<CarveOut> t) {
							t.add(actionCarveOut);
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
        }
        
        // This will figure out the action that applies to each term cut
        Map<Term, Map<TimePeriod, CarveOutGroupAction>> cleanedCarveOutTermsMap = new HashMap<>();
		for (Entry<Term, Map<TimePeriod, Set<CarveOut>>> termEntry : carveOutTermsMap.entrySet()) {
			
			Map<TimePeriod, CarveOutGroupAction> cleanedPeriodMap = new HashMap<>();
			
			for (Entry<TimePeriod, Set<CarveOut>> periodEntry : termEntry.getValue().entrySet()) {
				List<CarveOut> sortedCarveOuts = periodEntry.getValue().stream().sorted(Comparator.comparing(CarveOut::getCarveOutOrder)).collect(Collectors.toList());
				
				List<RightStrandCarveOutAction> impactOrder;
				RightStrandCarveOutAction finalCarveOutAction = RightStrandCarveOutAction.UNSET;
				Set<RightStrandCarveOutAction> actionSet = new HashSet<>();
				for(CarveOut carveOut : sortedCarveOuts) {
					actionSet.clear();
					// add your last carveOutAction
					actionSet.add(finalCarveOutAction);
					// add your next carveOutAction
					actionSet.add(carveOut.getCarveOutAction());
					
					// determine impactOrder based off of next carveOut's impact (sorted by carveOutOrder ascending)
					// The priority of actions are determined by the combine rule (AND / OR)
					impactOrder = calculateImpactOrder(carveOut.getCarveOutCombineRule());
					for (RightStrandCarveOutAction impactKey : impactOrder) {
						if(actionSet.contains(impactKey)) {
							finalCarveOutAction = impactKey;
							break;
						}
					}
				}
				cleanedPeriodMap.put(periodEntry.getKey(), new CarveOutGroupAction(
																	this.carveOutGroupId, 
																	this.carveOutCombineRule, 
																	this.carveOutGroupOrder,
																	finalCarveOutAction));
			}
			cleanedCarveOutTermsMap.put(termEntry.getKey(), cleanedPeriodMap);
		}
		
		RightStrandCarveOutAction gapAction = carveOutImpactType == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
		
		// return the cleaned impact map and gap action back
		return new CarveOutGroupResult(cleanedCarveOutTermsMap, gapAction);
	}
	
	public static List<RightStrandCarveOutAction> calculateImpactOrder(CarveOutCombineRule rule) {
		// for the action map in sorted result, clean up the result using impact order + leftOverPeriod
        List<RightStrandCarveOutAction> impactOrder;
		if (rule == CarveOutCombineRule.AND) {
			impactOrder = Arrays.asList(RightStrandCarveOutAction.BLOCKED, RightStrandCarveOutAction.APPLY_RIGHT_STRAND, RightStrandCarveOutAction.CONDITIONAL, RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND, RightStrandCarveOutAction.IGNORE_RIGHT_STRAND);
		} else { // OR
			impactOrder = Arrays.asList(RightStrandCarveOutAction.BLOCKED, RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND, RightStrandCarveOutAction.IGNORE_RIGHT_STRAND, RightStrandCarveOutAction.CONDITIONAL, RightStrandCarveOutAction.APPLY_RIGHT_STRAND);
		}
		return impactOrder;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		CarveOutGroup obj2 = (CarveOutGroup) obj;

		if (obj2 == null) {
			return false;
		}

		return carveOutImpactType.equals(obj2.carveOutImpactType) 
			&& carveOutCombineRule.equals(obj2.carveOutCombineRule) 
			&& childCarveOuts.equals(obj2.childCarveOuts);
	}

	@Override
	public int hashCode() {
		return carveOutImpactType.hashCode() 
			^ carveOutCombineRule.hashCode() 
			^ childCarveOuts.size();
	}

	public Set<CarveOut> getChildCarveOuts() {
		return Collections.unmodifiableSet(childCarveOuts);
	}
}
