package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.time.LocalDate;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.ITermContainer;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TermPeriod implements ITermContainer, Serializable {
	 
    private static final long serialVersionUID = 1L;
    
    private Term term;
    private TimePeriod timePeriod;

    public TermPeriod(Term term, TimePeriod timePeriod) {
        this.term = term;
        this.timePeriod = timePeriod;
    }
     
    public static TermPeriod getIntersection(TermPeriod tp1, TermPeriod tp2) {
        Term overlappingTerm = Term.getIntersectionTerm(tp1.getTerm(), tp2.getTerm());
        if (overlappingTerm == null) {
            return null;
        }

        TimePeriod overlappingPeriod = TimePeriod.intersectPeriods(tp1.getTimePeriod(), tp2.getTimePeriod());
        if (overlappingPeriod.isEmpty()) {
            return null;
        }
         
        return new TermPeriod(
            overlappingTerm,
            overlappingPeriod
        );
    }
     
    public static boolean hasIntersection(TermPeriod tp1, TermPeriod tp2) {
        if (tp1 == null || tp2 == null) {
            return false;
        }
        
        return Term.hasIntersection(tp1.getTerm(), tp2.getTerm())
            && TimePeriod.hasIntersection(tp1.getTimePeriod(), tp2.getTimePeriod())
        ;
    }
     
    @Override
    public boolean equals(Object obj) {
        TermPeriod obj2 = (TermPeriod) obj;

        if (obj2 == null) {
            return false;
        }

        return term.equals(obj2.term) && timePeriod.equals(obj2.timePeriod);
    }

    @Override
    public int hashCode() {
        return term.hashCode() ^ timePeriod.hashCode();
    }

	@Override
	public Term getTerm() {
		return term;
	}

	@Override
	public LocalDate getStartDate() {
		return term.getStartDate();
	}

	@Override
	public LocalDate getEndDate() {
		return term.getEndDate();
	}
	
	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	@Override
	public String toString() {
		return "TermPeriod [term=" + term + ", timePeriod=" + timePeriod + "]";
	}
}
