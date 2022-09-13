package com.pdgc.tests.general.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.timecutting.ReplacementTimeEntry;
import com.pdgc.general.util.timecutting.TimeCutResult;

public class CutDateTest {
	
	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
	}
	
	@Test
	public void singleDateTest() {

		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		Term term = new Term(Constants.EPOCH, Constants.PERPETUITY);
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();
		
		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(term, null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(1, cutTerms.size());
		assertTrue(cutTerms.containsKey(term));
		assertThat(cutTerms.get(term), equalTo(1));
	}

	@Test
	public void startGapDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1998, 1, 1), DateTimeUtil.createDate(1998, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();
		
		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1998, 1, 1), DateTimeUtil.createDate(1998, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1998, 1, 1), DateTimeUtil.createDate(1998, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1998, 1, 1), DateTimeUtil.createDate(1998, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	@Test
	public void startToStartTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(2000, 1, 1));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	@Test
	public void overlappingEarlyStartTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(2000, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());

	}

	@Test
	public void startToEndTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(2001, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	@Test
	public void startNoGapDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	// Test for when the new term starts and ends earlier than the old term and
	// overlaps

	// Test for when the new term starts before the old term and ends the same
	// day as the old term

	// Test for when the old term completely falls within the new term
	@Test
	public void overArchingDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(Constants.EPOCH, Constants.PERPETUITY);

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY)));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY)).intValue());
	}

	// Test for when the old term starts the same day as the new term but ends
	// earlier
	@Test
	public void equalStartEarlyEndTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());

	}

	// Test for when the old term equals the new term
	@Test
	public void equalDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(1, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());

	}

	// Test for when the old term starts the same day as the new term but ends
	// later
	@Test
	public void equalStartLateEndTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2002, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
	}

	// Test for when the new term completely falls within the old term
	@Test
	public void encompassedDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(Constants.EPOCH, Constants.PERPETUITY), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(Constants.EPOCH, Constants.PERPETUITY)));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31)),
				new Term(Constants.EPOCH, Constants.PERPETUITY), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(term,
				new Term(Constants.EPOCH, Constants.PERPETUITY), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY),
				new Term(Constants.EPOCH, Constants.PERPETUITY), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(Constants.EPOCH, DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(term));

		assertEquals(2, cutTerms.get(term).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY)));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), Constants.PERPETUITY)).intValue());
	}

	// Test for when the old term ends the same day as the new term but starts
	// later
	@Test
	public void equalEndLateStartTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());

	}

	// Test for when the new term starts and ends later than the old term and
	// overlaps
	@Test
	public void overlappingLateStartTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2002, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());

	}

	// Test for when the new term starts the same day the old term ends
	@Test
	public void EndToLaterTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2002, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2,
				cutTerms.get(new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
	}

	// Test for when the new term starts 1 day after the old term, leaving no
	// gap
	@Test
	public void endNoGapDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();
		
		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
	}

	// Test for when the new term starts after the old term, leaving a gap
	@Test
	public void endGapDateTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
	}

	// Test for when the new term's start and end date are the same, located
	// before the old term
	@Test
	public void earlyOneDayTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 1, 1));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 1, 1)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 1, 1))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1999, 1, 1), DateTimeUtil.createDate(1999, 1, 1))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	// Test for when the new term's start and end date are the same, located the
	// same day the old term starts
	@Test
	public void startOneDayTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 1, 1))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 2), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	// Test for when the new term's start and end date are the same, located the
	// same day the old term ends
	@Test
	public void endOneDayTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(1, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 30))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2,
				cutTerms.get(new Term(DateTimeUtil.createDate(2001, 12, 31), DateTimeUtil.createDate(2001, 12, 31))).intValue());
	}

	// Test for when the new term's start and end date are the same, located
	// after the old term
	@Test
	public void lateOneDayTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 1, 1));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 1, 1)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(2, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 1, 1))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 1, 1))).intValue());
	}

	// Sanity check for when there are multiple existing right strands, and a
	// new one comes : that impacts some of them
	@Test
	public void compositeDateCutTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);

		// cuts 1st term, adds onto 2nd term, cuts 3rd term, doesn't touch 4th term
		Term term = new Term(DateTimeUtil.createDate(2000, 7, 1), DateTimeUtil.createDate(2002, 6, 30)); 
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(3, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));
		assertEquals(5, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 6, 30)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)), false)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 7, 1), DateTimeUtil.createDate(2000, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 6, 30)),
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 7, 1), DateTimeUtil.createDate(2002, 12, 31)),
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), false)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(6, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 6, 30))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 6, 30))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 7, 1), DateTimeUtil.createDate(2000, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 7, 1), DateTimeUtil.createDate(2000, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2001, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 6, 30))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 6, 30))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 7, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 7, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
	}

	// Test when the new term's start and end date don't overlap with existing
	// terms, but instead existing between two others
	@Test
	public void insideGapTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(0, removedTerms.size());
		assertEquals(1, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(term, null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
	}

	// Test when the new term spans across a gap : the existing terms
	@Test
	public void acrossGapTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2003, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(2, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(3, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)),
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(3, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
	}

	// Test that handles when the new term spans across multiple gaps : the
	// existing terms
	@Test
	public void acrossMultipleGapsTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2005, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(3, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(5, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)),
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)),
				new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(5, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));

		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));

		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))).intValue());
	}

	// Test when the new term starts before existing terms, and ends after all
	// existing terms, where there is a gap : the existing terms
	@Test
	public void acrossGapAndBeforeEndTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(2005, 12, 31));
		
		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(2, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(5, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)),
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(5, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31))).intValue());
	}

	// Test when the new term starts before all existing terms, and ends after
	// all existing terms, where there are multiple gaps : the existing terms
	@Test
	public void acrossMultipleGapsAndBeforeEndTesT() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(2010, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(3, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(7, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)),
				new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)),
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)),
				new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2010, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(7, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(1990, 1, 1), DateTimeUtil.createDate(1999, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2004, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2005, 1, 1), DateTimeUtil.createDate(2005, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2010, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2010, 12, 31))).intValue());
	}

	// Test when the new term spans a gap : the existing terms and starts/ends
	// within other gaps : the existing terms
	public void acrossGapAndStartEndInGapTest() {
		Map<Term, Integer> cutTerms = new HashMap<Term, Integer>();
		cutTerms.put(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2000, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31)), 1);
		cutTerms.put(new Term(DateTimeUtil.createDate(2009, 1, 1), DateTimeUtil.createDate(2009, 12, 31)), 1);
		Term term = new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2008, 12, 31));

		TimeCutResult<Term> dateCutResult = DateTimeUtil.cutDates(cutTerms.keySet(), term);
		ImmutableSet<Term> removedTerms = dateCutResult.getRemovedEntries();
		ImmutableSet<ReplacementTimeEntry<Term>> replacementTerms = dateCutResult.getReplacementEntries();

		assertEquals(2, removedTerms.size());
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertTrue(removedTerms.contains(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31))));
		assertEquals(5, replacementTerms.size());
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)),
				new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31)), null, true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31)),
				new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31)), true)));
		assertTrue(replacementTerms.contains(new ReplacementTimeEntry<Term>(
				new Term(DateTimeUtil.createDate(2007, 1, 1), DateTimeUtil.createDate(2008, 12, 31)), null, true)));

		List<Pair<Term, Integer>> newEntries = new ArrayList<Pair<Term, Integer>>();
		for (ReplacementTimeEntry<Term> replacementTerm : replacementTerms) {
			Term newTerm = replacementTerm.getNewEntry();
			Term oldTerm = replacementTerm.getOldEntry();
			boolean usesNewInfo = replacementTerm.getUsesNewInfo();
			int newValue;

			if (usesNewInfo) {
				if (oldTerm != null) {
					newValue = cutTerms.get(oldTerm) + 1;
				} else {
					newValue = 1;
				}
			} else {
				newValue = cutTerms.get(oldTerm);
			}
			newEntries.add(new Pair<Term, Integer>(newTerm, newValue));
		}

		for (Term removedTerm : removedTerms) {
			cutTerms.remove(removedTerm);
		}

		for (Pair<Term, Integer> newEntry : newEntries) {
			cutTerms.put(newEntry.getValue0(), newEntry.getValue1());
		}

		assertEquals(7, cutTerms.size());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2000, 1, 1), DateTimeUtil.createDate(2001, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2002, 1, 1), DateTimeUtil.createDate(2002, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2003, 1, 1), DateTimeUtil.createDate(2003, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2004, 1, 1), DateTimeUtil.createDate(2005, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31))));
		assertEquals(2, cutTerms.get(new Term(DateTimeUtil.createDate(2006, 1, 1), DateTimeUtil.createDate(2006, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2007, 1, 1), DateTimeUtil.createDate(2008, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2007, 1, 1), DateTimeUtil.createDate(2008, 12, 31))).intValue());
		assertTrue(cutTerms.containsKey(new Term(DateTimeUtil.createDate(2009, 1, 1), DateTimeUtil.createDate(2009, 12, 31))));
		assertEquals(1, cutTerms.get(new Term(DateTimeUtil.createDate(2009, 1, 1), DateTimeUtil.createDate(2009, 12, 31))).intValue());
	}
}
