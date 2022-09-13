package com.pdgc.general.util.strings;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * String manipulator class. 
 * 
 * @author CLARA HONG
 *
 */
public class StringHelperUtil {

	/**
	 * Formats collections into a SQL-friendly string. 
	 * 
	 *  e.g. [1, 2, 3] -> 1,2,3
	 *  e.g. ["a", "b", "c"] -> 'a','b','c'
	 *  
	 * @param elements
	 * @return
	 */
	public static String joinCollection(Collection<?> elements) {
		if (elements.iterator().next() instanceof String) {
			return elements.stream().map(s -> "'" + s + "'").collect(Collectors.joining(",")); 
		} else {
			return StringUtils.join(elements, ","); 
		}
	}
}