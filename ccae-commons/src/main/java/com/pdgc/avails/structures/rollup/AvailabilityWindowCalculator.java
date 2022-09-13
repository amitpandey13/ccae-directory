package com.pdgc.avails.structures.rollup;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityResultStruct;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityWindow;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;

/**
 * Used to create AvailabilityWindow objects based on the rules of what a window is.
 * For now, a window extends across any term that has any period of non-NO net availability.
 * 
 * In the future, that may change to deal with time periods, 
 * or to only combine certain availabilities together instead of all non-NO availabilities, for example
 * @author Linda Xu
 *
 */
public class AvailabilityWindowCalculator {

    public AvailabilityWindowCalculator() {
        
    }

    public <E> Set<AvailabilityWindow> createAvailabilityWindows(
        Map<Term, Map<TimePeriod, E>> resultMap,
        Function<E, AvailabilityResultStruct> availabilityResultMapper
    ) {
        Map<Availability, Map<Term, Map<TimePeriod, AvailabilityResultStruct>>> sortedAvailabilities = new HashMap<>();
        for (Entry<Term, Map<TimePeriod, E>> termEntry : resultMap.entrySet()) {
            for (Entry<TimePeriod, E> periodEntry : termEntry.getValue().entrySet()) {
                AvailabilityResultStruct availabilityResult = availabilityResultMapper.apply(periodEntry.getValue());
                sortedAvailabilities.computeIfAbsent(availabilityResult.getNetAvailability(), k -> new HashMap<>())
                    .computeIfAbsent(termEntry.getKey(), k -> new HashMap<>())
                    .put(periodEntry.getKey(), availabilityResult);
            }
        }
        
        SortedMap<Term, Map<TimePeriod, AvailabilityResultStruct>> orderedAvailableTerms = new TreeMap<>();
        for (Entry<Availability, Map<Term, Map<TimePeriod, AvailabilityResultStruct>>> availabilityEntry : sortedAvailabilities.entrySet()) {
            if (availabilityEntry.getKey() != Availability.NO) {
                for (Entry<Term, Map<TimePeriod, AvailabilityResultStruct>> termEntry : availabilityEntry.getValue().entrySet()) {
                    for (Entry<TimePeriod, AvailabilityResultStruct> periodEntry : termEntry.getValue().entrySet()) {
                        orderedAvailableTerms.computeIfAbsent(termEntry.getKey(), k -> new HashMap<>())
                            .put(periodEntry.getKey(), periodEntry.getValue());
                    }
                }
            }
        }
        
        Set<AvailabilityWindow> windows = new HashSet<>();
        
        LocalDate contiguousStartDate = null; //as we're evaluating the ordered windows, keep track of the earliest start date among a series of contiguous windows
        LocalDate contiguousEndDate = null; //as we're evaluating the ordered windows, keep track of the latest end date among a series of contiguous windows               
        AvailabilityWindow currentWindow = null;
        for (Entry<Term, Map<TimePeriod, AvailabilityResultStruct>> termEntry : orderedAvailableTerms.entrySet()) {
            // a date gap between the previous entry and current entry was found, 
            // so updated minWindowLength for the current list of contiguous windows 
            // and start a new list of contiguous windows 
            if (contiguousEndDate != null && !termEntry.getKey().getStartDate().minusDays(1).isEqual(contiguousEndDate)) {
                currentWindow.windowTerm = new Term(contiguousStartDate, contiguousEndDate);
                windows.add(currentWindow);
                
                //Reset all information about contiguous available windows
                contiguousStartDate = null;
                contiguousEndDate = null;
                currentWindow = null;
            }

            //Indicator that the window needs to be initialized
            if (currentWindow == null) {
                currentWindow = new AvailabilityWindow();
                currentWindow.availability = Availability.UNSET;
                currentWindow.windowPeriod = TimePeriod.EMPTY_WEEK;
                contiguousStartDate = termEntry.getKey().getStartDate();
            }
            
            //Update the window info
            contiguousEndDate = termEntry.getKey().getEndDate();
            for (Entry<TimePeriod, AvailabilityResultStruct> periodEntry : termEntry.getValue().entrySet()) {
                currentWindow.windowPeriod = TimePeriod.unionPeriods(currentWindow.windowPeriod, periodEntry.getKey());
                currentWindow.availability = AvailabilityHelper.combineAvailability(
                    currentWindow.availability, 
                    periodEntry.getValue().getNetAvailability()
                );
            }
        }
        if (currentWindow != null) {
            currentWindow.windowTerm = new Term(contiguousStartDate, contiguousEndDate);
            windows.add(currentWindow);
        }
        
        return windows;
    }
}
