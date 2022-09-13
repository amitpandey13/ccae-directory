package com.pdgc.tests.general.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

public class CollectionsUtilTest {

	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void isNullOrEmptyTest() {

		List<Integer> testList = new ArrayList<Integer>();
		testList.add(1);
		assertFalse(CollectionsUtil.isNullOrEmpty(testList));

		testList.clear();
		assertTrue(CollectionsUtil.isNullOrEmpty(testList));
		
		assertTrue(CollectionsUtil.isNullOrEmpty(new ArrayList<Integer>()));

		Map<String, Integer> testMap = new HashMap<>();
		testMap.put("key", 1);
		assertFalse(CollectionsUtil.isNullOrEmpty(testMap));

		testMap.clear();
		assertTrue(CollectionsUtil.isNullOrEmpty(testMap));
		
		assertTrue(CollectionsUtil.isNullOrEmpty(new HashMap<String, Integer>()));
		
		
		testList = null;
		assertTrue(CollectionsUtil.isNullOrEmpty(testList));
		testMap = null;
		assertTrue(CollectionsUtil.isNullOrEmpty(testMap));
		
	}

	@Test
	public void whereTest() {
		List<Term> terms = new ArrayList<>();
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		Term term2014 = new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31));
		terms.add(term2014);

		Collection<Term> termsAfter1Feb2013 = CollectionsUtil.where(terms, t -> t.getStartDate().isAfter(DateTimeUtil.createDate(2013, 2, 1)));
		assertThat(termsAfter1Feb2013, hasSize(equalTo(1)));
		assertThat(termsAfter1Feb2013, hasItem(term2014));
	}

	@Test // Collection<E> collection, Collection<E> retain
	public void intersectTest() {
		List<Term> terms = new ArrayList<>();
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		List<Term> terms2 = new ArrayList<>();
		terms2.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));

		Set<Term> resultTerms = new HashSet<>();
		resultTerms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		resultTerms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		resultTerms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));

		assertTrue(resultTerms.containsAll(CollectionsUtil.intersect(terms, terms2)));

		terms.clear();
		terms2.clear();
		resultTerms.clear();
		assertTrue(CollectionsUtil.intersect(terms, terms2).isEmpty());

	}

	@Test
	public void exceptTest() {
		List<Term> terms = new ArrayList<>();
		terms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		List<Term> terms2 = new ArrayList<>();
		terms2.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		Set<Term> resultTerms = new HashSet<>();
		resultTerms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		assertEquals(resultTerms, CollectionsUtil.except(terms, terms2));

		// Both Terms are null
		terms.clear();
		terms2.clear();
		assertTrue(CollectionsUtil.intersect(terms, terms2).isEmpty());

		// first Term is null
		terms2.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms2.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		assertTrue(CollectionsUtil.intersect(terms, terms2).isEmpty());

		// second Term is null
		terms2.clear();
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		assertTrue(CollectionsUtil.intersect(terms, terms2).isEmpty());
	}

	@Test
	public void distinctTest() {
		List<Term> terms = new ArrayList<>();
		terms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		Set<Term> resultTerms = new HashSet<>();
		resultTerms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		resultTerms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		resultTerms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		resultTerms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		assertEquals(resultTerms, new HashSet<Term>(terms));

		// When terms is empty
		terms.clear();
		assertTrue(terms.isEmpty());
	}

	@Test
	public void findFirstTest() {
		List<Term> terms = new ArrayList<>();
		Term firstTerm = new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31));
		terms.add(firstTerm);
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));
		assertEquals(firstTerm, CollectionsUtil.findFirst(terms));

		// terms is Empty
		terms.clear();
		assertNull(CollectionsUtil.findFirst(terms));
		assertNull(CollectionsUtil.findFirst(null));
	}

	@Test
	public void findLastTest() {
		List<Term> terms = new ArrayList<>();
		terms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		terms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		Term lastTerm = new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31));
		terms.add(lastTerm);
		assertEquals(lastTerm, CollectionsUtil.findLast(terms));

		// terms is Empty
		terms.clear();
		assertNull(CollectionsUtil.findLast(terms));
		assertNull(CollectionsUtil.findLast(null));
	}
	
	@Test
	public void isProperSupersetOfTest() {
		List<Term> actualterms = new ArrayList<>();
		actualterms.add(new Term(DateTimeUtil.createDate(2011, 1, 1), DateTimeUtil.createDate(2011, 12, 31)));
		actualterms.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		actualterms.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		actualterms.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		List<Term> termsTrue = new ArrayList<>();
		termsTrue.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		termsTrue.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		termsTrue.add(new Term(DateTimeUtil.createDate(2014, 1, 1), DateTimeUtil.createDate(2014, 12, 31)));

		assertTrue(CollectionsUtil.isProperSupersetOf(actualterms, termsTrue));

		// TermsFalse is not a subset of actualTerms
		List<Term> termsFalse = new ArrayList<>();
		termsFalse.add(new Term(DateTimeUtil.createDate(2012, 1, 1), DateTimeUtil.createDate(2012, 12, 31)));
		termsFalse.add(new Term(DateTimeUtil.createDate(2013, 1, 1), DateTimeUtil.createDate(2013, 12, 31)));
		termsFalse.add(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
		assertFalse(CollectionsUtil.isProperSupersetOf(actualterms, termsFalse));
	}

	@Test
	public void properSubsetOfBothNullThrowsException() {
		thrown.expect(IllegalArgumentException.class);
		CollectionsUtil.isProperSupersetOf(null, null);
	}

	@Test
	public void properSubsetOfSourceNullThrowsException() {
		thrown.expect(IllegalArgumentException.class);
		CollectionsUtil.isProperSupersetOf(null, Collections.<Object>emptyList());
	}

	@Test
	public void properSubsetOfOtherNullThrowsException() {
		thrown.expect(IllegalArgumentException.class);
		CollectionsUtil.isProperSupersetOf(Collections.<Object>emptyList(), null);
	}

	@Test
	public void properSubsetOfBothEmpty() {
		assertFalse(CollectionsUtil.isProperSupersetOf(Collections.<Object>emptyList(), Collections.<Object>emptyList()));
	}
}
