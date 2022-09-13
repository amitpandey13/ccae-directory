package com.pdgc.general.util.tuple;

import java.util.Collection;
import java.util.function.Function;

import org.javatuples.Pair;

public class TupleUtil {
    
    /**
     * This will allow you to define the method that will stringify the pair you're passing in. Also allows definition of
     * separator between pairs
     * 
     * @param pairCollection
     * @param separator - how you will separate each stringified pair
     * @param fun - function/method that will stringify your pair
     * @return
     */
    public static <E> String pairCollectionToString(Collection<Pair<E, E>> pairCollection, String separator, Function<Pair<E, E>, String> fun) {
        if (pairCollection.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Pair<E, E> pair : pairCollection) {
            sb.append(fun.apply(pair));
            sb.append(separator);
        }
        
        return sb.substring(0, sb.length() - separator.length()).toString();
    }
    
    
    /**
     * General toString method that will return the pair in a string of format of:<br>
     * <br>
     * "(value1,value2)"
     * 
     * @param pair
     * @return
     */
    public static <E> String pairToString(Pair<E, E> pair) {
        return new StringBuilder().append("(").append(pair.getValue0().toString()).append(",").append(pair.getValue1().toString()).append(")").toString();
    }
}
