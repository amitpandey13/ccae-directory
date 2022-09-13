package com.pdgc.avails.structures.rollup.tab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pdgc.avails.service.AvailsRollup;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.avails.structures.rollup.FullAvailsResult;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityResultStruct;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;

/**
 * Tests for accurate analysis of contiguous windows 
 * 
 * @author Clara Hong
 *
 */
class TabRollupResultsHelper_ContinguousWindowTest {

    private Predicate<FullAvailsResult> availabilityTest = new Predicate<FullAvailsResult>() {
        @Override
        public boolean test(FullAvailsResult availsResult) {
            return availsResult.primaryNetResult.getNetAvailability() != Availability.NO;
        }
    };
    
    @Test
	void givenYYYY_whenEvaluateContiguousTerms_thenContiguousNumbers1111() {
		// Arrange
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		List<Term> orderedTerms = Arrays.asList(term1, term2, term3, term4); 
		for (Term term : orderedTerms) {
			groupTermMap.put(term, createAvailsResults(Availability.YES, Availability.YES));
		}
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
	        groupTermMap,
	        Function.identity(), 
	        availabilityTest, 
	        null,
	        LocalDate.MIN,
	        LocalDate.MAX,
	        LocalDate.MAX
		); 
		
		// Assert
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
	}

	@Test
	void givenNNNN_whenEvaluateContiguousTerms_thenContiguousNumbers1234() {
		// Arrange
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		List<Term> orderedTerms = Arrays.asList(term1, term2, term3, term4); 
		for (Term term : orderedTerms) {
			groupTermMap.put(term, createAvailsResults(Availability.NO, Availability.NO));
		}
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(3, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(4, r.contiguousWindowNumber));
	}

	@Test
	void givenYNNN_whenEvaluateContiguousTerms_thenContiguousNumbers1234() {
		// Arrange
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		groupTermMap.put(term1, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term2, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term3, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term4, createAvailsResults(Availability.NO, Availability.NO));
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert 
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(3, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(4, r.contiguousWindowNumber));
	}

	@Test
	void givenYYNN_whenEvaluateContiguousTerms_thenContiguousNumbers1123() {
		// Arrange 
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		groupTermMap.put(term1, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term2, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term3, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term4, createAvailsResults(Availability.NO, Availability.NO));
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert 
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(3, r.contiguousWindowNumber));

	}
	
	@Test
	void givenNYYY_whenEvaluateContiguousTerms_thenContiguousWindowNumbers1222() {
		// Arrange 
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		groupTermMap.put(term1, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term2, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term3, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term4, createAvailsResults(Availability.YES, Availability.YES));
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert 
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
	}
	
	@Test
	void givenNYYN_whenEvaluateContiguousTerms_thenContiguousWindowNumbers1223() {
		// Arrange 
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		groupTermMap.put(term1, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term2, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term3, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term4, createAvailsResults(Availability.NO, Availability.NO));
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert 
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(3, r.contiguousWindowNumber));
	}
	
	@Test
	void givenYNNY_whenEvaluateContiguousTerms_thenContiguousWindowNumbers1234() {
		// Arrange 
		SortedMap<Term, Collection<FullAvailsResult>> groupTermMap = new TreeMap<>();
		
		LocalDate date2010 = LocalDate.of(2010, 1, 1);
		LocalDate date2011 = LocalDate.of(2011, 1, 1);
		LocalDate date2012 = LocalDate.of(2012, 1, 1);
		Term term1 = new Term(Constants.EPOCH, date2010.minusDays(1)); 
		Term term2 = new Term(date2010, date2011.minusDays(1));
		Term term3 = new Term(date2011, date2012.minusDays(1)); 
		Term term4 = new Term(date2012, Constants.PERPETUITY); 
		
		groupTermMap.put(term1, createAvailsResults(Availability.YES, Availability.YES));
		groupTermMap.put(term2, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term3, createAvailsResults(Availability.NO, Availability.NO));
		groupTermMap.put(term4, createAvailsResults(Availability.YES, Availability.YES));
		
		// Act 
		TabRollupResultsHelper.evaluateContiguousTerms(
            groupTermMap,
            Function.identity(), 
            availabilityTest, 
            null,
            LocalDate.MIN,
            LocalDate.MAX,
            LocalDate.MAX
        ); 
		
		// Assert 
		groupTermMap.get(term1).forEach(r -> assertEquals(1, r.contiguousWindowNumber));
		groupTermMap.get(term2).forEach(r -> assertEquals(2, r.contiguousWindowNumber));
		groupTermMap.get(term3).forEach(r -> assertEquals(3, r.contiguousWindowNumber));
		groupTermMap.get(term4).forEach(r -> assertEquals(4, r.contiguousWindowNumber));
	}
	
	@BeforeAll
	private static void setup() {
		Constants.instantiateConstants();
	}
	
	private static Collection<FullAvailsResult> createAvailsResults(Availability corpAvail, Availability nonCorpAvail) {
		Collection<FullAvailsResult> availsResults = new HashSet<>(); 
		AvailabilityResultStruct availsStruct = new AvailabilityResultStruct(
			new AvailabilityResult(corpAvail, new ArrayList<>()),
			new AvailabilityResult(nonCorpAvail, new ArrayList<>())
		);
		
		FullAvailsResult availsResult = new FullAvailsResult(); 
		availsResult.primaryNetResult = AvailsRollup.reviseForNetCalc(availsStruct);
		availsResults.add(availsResult); 
		
		return availsResults;
	}
}
