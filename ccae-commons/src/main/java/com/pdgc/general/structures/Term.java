package com.pdgc.general.structures;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.util.DateTimeUtil;


/**
 * Structure that stores a start and end date. These are truly dates, so
 * hours/seconds/minutes are ignored when comparing the start/end DateTimes of
 * the two Terms.
 * 
 * @author Vishal Raut
 */
public class Term implements Comparable<Term>, Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Term.class);

	private static final long serialVersionUID = 1L;

	private LocalDate startDate;
	private LocalDate endDate;
	
	protected Term() {}
	
	public Term(Term term) {
		this.startDate = term.startDate;
		this.endDate = term.endDate; 
	}
	
	public Term(LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
		if (startDate.isAfter(endDate)) {
			endDate = startDate;
			LOGGER.warn("Term EndDate was before StartDate. EndDate changed to equal StartDate");
			//throw new IllegalArgumentException("Start date cannot be after end date");
		}

		this.startDate = startDate;
		this.endDate = endDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}
	
	@JsonIgnore
	public boolean isMaxEndDate() {
		return Constants.PERPETUITY.equals(endDate);
	}
	
	/**
	 * Compares two terms and finds the intersection
	 * 
	 * @param t1
	 * @param t2
	 * @return null if the terms don't intersect, otherwise returns a term that
	 *         is the max start date and min end date of the two terms
	 */
	public static Term getIntersectionTerm(Term t1, Term t2) {
		if (t1 == null || t2 == null) {
		    return null;
		}
		
		LocalDate maxStartDate = t1.startDate;
        LocalDate minEndDate = t1.endDate;
		
		if (t1.startDate.isBefore(t2.startDate)) {
			maxStartDate = t2.startDate;
		}

		if (t1.endDate.isAfter(t2.endDate)) {
			minEndDate = t2.endDate;
		}
		if (maxStartDate.isAfter(minEndDate)) {
	
			return null;
		}

		return new Term(maxStartDate, minEndDate);
	}

	public static boolean hasIntersection(Term t1, Term t2) {
		if (t1 == null || t2 == null) {
            return false;
        }

        LocalDate maxStartDate = t1.startDate;
        LocalDate minEndDate = t1.endDate;
        
        if (t1.startDate.isBefore(t2.startDate)) {
            maxStartDate = t2.startDate;
        }

        if (t1.endDate.isAfter(t2.endDate)) {
            minEndDate = t2.endDate;
        }

        return !maxStartDate.isAfter(minEndDate);
	}
	
	/**
	 * Creates a term that consists of the earliest start date and the latest end date.
	 * Nulls are ignored. If all terms are null, the answer is null
	 * @param terms
	 * @return
	 */
	public static Term getUnion(Iterable<Term> terms) {
	    Set<LocalDate> startDates = new HashSet<>();
	    Set<LocalDate> endDates = new HashSet<>();
	    for (Term term : terms) {
	        if (term != null) {
	            startDates.add(term.getStartDate());
	            endDates.add(term.getEndDate());
	        }
	    }
	    
	    //Terms cannot have null dates, so we don't have to check the contents
	    if (startDates.isEmpty()) {
	        return null;
	    }
	    
	    return new Term(
            DateTimeUtil.getMinDate(startDates),
            DateTimeUtil.getMaxDate(endDates)
	    );
	}
	
	/**
	 * @see Term#getUnion(Iterable)
	 * @param terms
	 * @return
	 */
	public static Term getUnion(Term... terms) {
	    return getUnion(Arrays.asList(terms));
	}
	
	/**
	 * Is this term fully encompassed by the other term.
	 * @param t2
	 * @return true if it is. False otherwise.
	 */
	public boolean isCoveredBy(Term t2) {
		if (t2 == null) {
			return false;
		}
		
		if (startDate.isBefore(t2.getStartDate())) {
		    return false;
		}
		if (endDate.isAfter(t2.getEndDate())) {
		    return false;
		}
			
		return true;
	}
	
	/**
	 * Returns whether or not the term includes the given date
	 * @param date
	 * @return
	 */
	public boolean includes(LocalDate date) {
	    return !date.isBefore(startDate) 
	        && !date.isAfter(endDate);
	}
	
	/**
	 * Subtract {@code subtrahend} from {@code minuend}
	 * 
	 * The result is anywhere from 0 to 2 terms, which will contain the time
	 * that contains the subtrahend term but not the minuend term
	 * 
	 * @param minuend
	 *            The term being subtracted from
	 * @param subtrahend
	 *            The subtracting term
	 * @return The remainder (up to 2) terms that encompass the subtrahend without the minuend
	 */
	public static Set<Term> subtractTerms(Term minuend, Term subtrahend) {
		if (subtrahend == null || subtrahend.getEndDate().isBefore(minuend.getStartDate()) || subtrahend.getStartDate().isAfter(minuend.getEndDate())) {
			return Collections.singleton(minuend);
		}
		
		if (minuend.isCoveredBy(subtrahend)) {
			return new HashSet<>();
		}
		
		Set<Term> remainderTerms = new HashSet<>();
		
		if (minuend.getStartDate().isBefore(subtrahend.getStartDate())) {
			remainderTerms.add(new Term(minuend.getStartDate(), subtrahend.getStartDate().plusDays(-1L)));
		}
		
		if (minuend.getEndDate().isAfter(subtrahend.getEndDate())) {
			remainderTerms.add(new Term(subtrahend.getEndDate().plusDays(1L), minuend.getEndDate()));
		}
		
		return remainderTerms;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(startDate, ((Term)obj).startDate) 
			&& Objects.equals(endDate, ((Term)obj).endDate);
	}

	@Override
	public int hashCode() {
		return startDate.hashCode() 
			^ endDate.hashCode();
	}

	@Override
	public String toString() {
		return DateTimeUtil.formatDate(startDate) + " - " + DateTimeUtil.formatDate(endDate);
	}

	@Override
	public int compareTo(Term t) {
		int startDateCompare = startDate.compareTo(t.startDate);
		
		if (startDateCompare == 0) {
			startDateCompare = endDate.compareTo(t.endDate);
		}
		
		return startDateCompare;
	}
}

