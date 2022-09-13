package com.pdgc.general.util;

import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

public abstract class StreamsUtil {

	/**
	 * Helper function that converts a readonly iterable parameter to a set 
	 * so that methods such as contains() are available
	 * The returned set might just be a casted version of the iterable, 
	 * so this method should be kept private, as all the util methods are effectively readonly 
	 * @param c
	 */
	private static <T> Set<T> convertToSet(Iterable<T> c) {
		Set<T> itemSet;
		if (c instanceof Set<?>) {
			itemSet = (Set<T>) c;
		}
		else {
			itemSet = Sets.newHashSet(c);
		}
		
		return itemSet;
	}
		
	public static <E> boolean notEmpty(Stream<E> stream) {
		return stream.iterator().hasNext();
	}

	public static <E> Stream<E> intersect(Stream<E> stream, Iterable<E> retain) {
		return stream.filter(convertToSet(retain)::contains);
	}

	/**
	 * This method may be taking a really long time to run.  Not sure why.  It may just be on the first run.  
	 * I replaced it in places with myCollection.removeAll(exceptCollection). AT20170625.
	 * @param collection
	 * @param except
	 * @return
	 */
	public static <E> Stream<E> except(Stream<E> stream, Iterable<E> except) {
		Set<E> exceptSet = convertToSet(except);
		return stream.filter(e -> !exceptSet.contains(e));
	}
}
