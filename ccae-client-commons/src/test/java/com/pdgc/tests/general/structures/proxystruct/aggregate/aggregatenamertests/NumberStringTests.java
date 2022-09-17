package com.pdgc.tests.general.structures.proxystruct.aggregate.aggregatenamertests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.pdgc.general.util.AggregateNamer;

public class NumberStringTests {

	@Test
	public void singleNumberTest() {
		Collection<Integer> numbers = Arrays.asList(1);
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("1", numberString);
	}
	
	@Test
	public void emptyTest() {
		Collection<Integer> numbers = new ArrayList<>();
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("", numberString);
	}
	
	@Test
	public void nullTest() {
		Collection<Integer> numbers = Arrays.asList(null, 1);
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("1", numberString);
	}
	
	@Test
	public void consecutiveNumbersTest() {
		Collection<Integer> numbers = Arrays.asList(3,2,1);
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("1-3", numberString);
	}
	
	@Test
	public void nonConsecutiveNumbersTest() {
		Collection<Integer> numbers = Arrays.asList(7,5,3,1);
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("1,3,5,7", numberString);
	}
	
	@Test
	public void mixedConsecutiveAndNonConsecutiveNumbersTest() {
		Collection<Integer> numbers = Arrays.asList(3,2,1,7,10,11,12);
		String numberString = AggregateNamer.createNumbersString(numbers, "-", ",");
		
		assertEquals("1-3,7,10-12", numberString);
	}
}
