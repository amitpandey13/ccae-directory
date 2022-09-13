package com.pdgc.general.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used for storing utility methods for working with Integers and Longs,
 * which we have a tendency to work with these the most
 * (Long b/c ids tend to be stored as that, Integer b/c of the PMTL IdSets) 
 * 
 * These methods are mostly used to deal with conversions to enforce consistency
 * so that the comparison methods don't break thanks to cases like these:
 *      new Long(1L).equals(new Long(1L))   -> true
 *      new Long(1L).equals(new Integer(1)) -> false
 *      new Long(1L).equals(1L)             -> true (the primitive 1L was auto-boxed to a Long)
 *      new Long(1L).equals(1)              -> false (the primitive 1 was auto-boxed to a Integer)
 * 
 * @author Linda Xu
 *
 */
public class IntegerConversionUtil {

    @SuppressWarnings("unchecked")
    public static Set<Integer> convertToIntSet(Set<? extends Number> ids) {
        if (ids.stream().anyMatch(Integer.class::isInstance)) {
            return (Set<Integer>)ids;
        }
        return CollectionsUtil.select(ids, i -> i.intValue(), Collectors.toSet());
    }
    
    @SuppressWarnings("unchecked")
    public static Set<Long> convertToLongSet(Set<? extends Number> ids) {
        if (ids.stream().anyMatch(Long.class::isInstance)) {
            return (Set<Long>)ids;
        }
        return CollectionsUtil.select(ids, i -> i.longValue(), Collectors.toSet());
    }
    
    public static <E extends Number> Set<Integer> convertToIntSet(E[] ids) {
        Set<Integer> longSet = new HashSet<>(ids.length);
        for (int i=0; i<ids.length; i++) {
            longSet.add(ids[i].intValue());
        }
        return longSet;
    }
    
    public static <E extends Number> Set<Long> convertToLongSet(E[] ids) {
        Set<Long> longSet = new HashSet<>(ids.length);
        for (int i=0; i<ids.length; i++) {
            longSet.add(ids[i].longValue());
        }
        return longSet;
    }
    
    /**
     * We are inconsistent about whether or not we store ids as an Object or primitive,
     * as well as whether we feel like using longs vs ints, which causes issues between using 
     * == vs .equals() and boxing/unboxing breaking comparisons between Long and Integer
     * 
     * Have this around to force consistency so that comparisons don't break or cause compile errors
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean longEquals(Number v1, Number v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v2 == null) {
            return false; //we already know v1 is null
        }
        
        return v1.longValue() == v2.longValue();
    }
    
    /**
     * We are inconsistent about whether or not we store ids as an Object or primitive,
     * as well as whether we feel like using longs vs ints, which causes issues between using 
     * == vs .equals() and boxing/unboxing breaking comparisons between Long and Integer
     * 
     * Have this around to force consistency so that comparisons don't break or cause compile errors
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean intEquals(Number v1, Number v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v2 == null) {
            return false; //we already know v1 is null
        }
        
        return v1.intValue() == v2.intValue();
    }
    
    /**
     * Wrapped contains() method for making sure we don't get screwed 
     * trying to run a contains() on say, a Collection<Long> against a Integer
     * @param collection
     * @param val
     * @return
     */
    public static boolean intContains(Collection<? extends Number> collection, Number val) {
        return collection.stream().anyMatch(v -> intEquals(v, val));
    }
    
    /**
     * Wrapped contains() method for making sure we don't get screwed 
     * trying to run a contains() on say, a Collection<Long> against a Integer
     * @param collection
     * @param val
     * @return
     */
    public static boolean longContains(Collection<? extends Number> collection, Number val) {
        return collection.stream().anyMatch(v -> longEquals(v, val));
    }
}
