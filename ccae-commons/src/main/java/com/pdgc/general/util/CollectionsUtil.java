package com.pdgc.general.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CollectionsUtil {
	
	@SafeVarargs
	public static <T> List<T> toList(T... args) {
		return new ArrayList<T>(Arrays.asList(args));
	}

	@SafeVarargs
	public static <T> Set<T> toSet(T... args) {
		return new HashSet<T>(Arrays.asList(args));
	}
	
	/**
	 * Checks whether a collection is null or empty
	 * 
	 * @param c
	 *            The collection
	 * @param <T>
	 *            The generic collection's type
	 * @return <code>true</code> if the collection is null or empty, <code>false</code> otherwise
	 */
	public static <T> boolean isNullOrEmpty(Iterable<T> c) {
		return c == null || !c.iterator().hasNext();
	}

	public static <K,V> boolean isNullOrEmpty(Map<K,V> c) {
		return c == null || c.isEmpty();
	}
	
	public static <E, T, A, R> R select(Iterable<E> collection, Function<? super E, ? extends T> mapper, Collector<? super T,A,R> collector) {
        return StreamSupport.stream(collection.spliterator(), false).map(mapper).collect(collector);
    }
	
	public static <E, T> Collection<T> select(Iterable<E> collection, Function<? super E, ? extends T> mapper) {
		return select(collection, mapper, Collectors.toList());
	}
	
	public static <E, T, A, R> R selectMany(Iterable<E> collection, Function<? super E, ? extends Stream<? extends T>> mapper, Collector<? super T,A,R> collector) {
        return StreamSupport.stream(collection.spliterator(), false).flatMap(mapper).collect(collector);
    }
	
	public static <E, T> Collection<T> selectMany(Iterable<E> collection, Function<? super E, ? extends Stream<? extends T>> mapper) {
		return selectMany(collection, mapper, Collectors.toList());
	}
	
	public static <E> boolean any(Iterable<E> collection, Predicate<? super E> filterPredicate) {
		return StreamSupport.stream(collection.spliterator(), false).anyMatch(filterPredicate);
	}
	
	public static <E, A, R> R where(Iterable<E> collection, Predicate<? super E> filterPredicate, Collector<? super E,A,R> collector) {
	    return StreamSupport.stream(collection.spliterator(), false).filter(filterPredicate).collect(collector);
	}
	
	public static <E> Collection<E> where(Iterable<E> collection, Predicate<? super E> filterPredicate) {
		return where(collection, filterPredicate, Collectors.toList());
	}
	
	/**
	 * Similar to the where() method, except that the expected output is supposed to consist of one entry.
	 * Returns null if there are no matches
	 * 
	 * @param collection
	 * @param filterPredicate
	 * @return
	 */
	public static <E> E firstMatch(Iterable<E> collection, Predicate<? super E> filterPredicate) {
		return StreamSupport.stream(collection.spliterator(), false).filter(filterPredicate).findFirst().orElse(null);
	}
	
	public static <E> List<E> orderBy(Iterable<E> collection, Comparator<? super E> comparator) {
		return StreamSupport.stream(collection.spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
	
	public static <E> Set<E> intersect(Iterable<E> collection, Iterable<E> retain) {
		return isNullOrEmpty(collection) ? new HashSet<E>() : 
			StreamsUtil.intersect(StreamSupport.stream(collection.spliterator(),  false), retain).collect(Collectors.toSet());
	}
	
	public static <E> Set<E> except(Iterable<E> collection, Iterable<E> except) {
		return isNullOrEmpty(collection) ? new HashSet<E>() : 
			StreamsUtil.except(StreamSupport.stream(collection.spliterator(),  false), except).collect(Collectors.toSet());
	}
	
	public static <E> E findFirst(Iterable<E> collection) {
		return isNullOrEmpty(collection) ? null : 
			collection.iterator().next();
	}

	public static <E> E findLast(Iterable<E> collection) {
		return isNullOrEmpty(collection) ? null : 
			StreamSupport.stream(collection.spliterator(), false).reduce((e1, e2) -> e2).get();
	}

	public static <E> boolean isProperSupersetOf(Collection<E> source, Collection<E> other) {
		if (source == null || other == null) {
			throw new IllegalArgumentException("both source and other should not be null");
		}

		if (source.size() <= other.size()) {
			return false;
		}

		return source.containsAll(other);
	}
	
	/**
	 * Converts the iterable source to a hashMap. 
	 * If multiple elements map to the same key, then the last element to be iterated through will take precedence
	 * @param source
	 * @param keyMapper
	 * @param valueMapper
	 * @return
	 */
	public static <E, K, V> Map<K, V> toMap(
		Iterable<E> source, 
		Function<? super E, ? extends K> keyMapper, 
		Function<? super E, ? extends V> valueMapper
	) {
		HashMap<K, V> newMap = new HashMap<>();
		
		for (E element : source) {
			newMap.put(keyMapper.apply(element), valueMapper.apply(element));
		}
		
		return newMap;
	}
	
	/**
	 * Creates a list of specific type from the Object[]
	 * 
	 * @param arr
	 * @param clazz the type of list expected
	 * @param       <T> the type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> convertObjectArrayToListOfType(Object[] arr, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			list.add((T) arr[i]);
		}
		return list;
	}
}
