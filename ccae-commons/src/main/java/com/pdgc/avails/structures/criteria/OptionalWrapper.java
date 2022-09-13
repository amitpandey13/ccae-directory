package com.pdgc.avails.structures.criteria;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 * @author Vishal Raut
 *
 * @param <E> the type of criteria this one wraps
 */
public class OptionalWrapper<E> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private E element;
	private boolean optional;

	public OptionalWrapper(E element) {
		this(element, false);
	}
	
	public OptionalWrapper(E element, boolean optional) {
		this.element = element;
		this.optional = optional;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
	    
	    return Objects.equals(element, ((OptionalWrapper<?>)obj).element)
	        && optional == ((OptionalWrapper<?>)obj).optional
	    ;
	}
	
	@Override
	public int hashCode() {
	    return Objects.hashCode(element)
	        ^ Boolean.hashCode(optional);
	}

	public E getElement() {
		return element;
	}

	public boolean isOptional() {
		return optional;
	}

}