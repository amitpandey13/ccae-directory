package com.pdgc.general.structures.carveout;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;

import lombok.Builder;

@Builder
public class CustomerCountLicense implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Customer customer;
    private RightSource rightSource;
    private Term term;
    private TimePeriod timePeriod;
    private PMTL pmtl;
    
    public CustomerCountLicense(
        Customer customer,
        RightSource rightSource,
        Term term,
        TimePeriod timePeriod,
        PMTL pmtl
    ) {
        this.customer = customer;
        this.rightSource = rightSource;
        this.term = term;
        if (timePeriod == null) {
        	this.timePeriod = TimePeriod.FULL_WEEK;
        }
        else {
        	this.timePeriod = timePeriod;
        }
        this.pmtl = pmtl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
        	return true;
        }
        
        if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

        return Objects.equals(customer, ((CustomerCountLicense)obj).customer)
            && Objects.equals(rightSource, ((CustomerCountLicense)obj).rightSource)
            && Objects.equals(term, ((CustomerCountLicense)obj).term)
            && Objects.equals(timePeriod, ((CustomerCountLicense)obj).timePeriod)
            && Objects.equals(pmtl, ((CustomerCountLicense)obj).pmtl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customer)
            ^ Objects.hashCode(rightSource)
            ^ Objects.hashCode(term)
            ^ Objects.hashCode(timePeriod)
            ^ Objects.hashCode(pmtl);
    }
    
	public Customer getCustomer() {
		return customer;
	}
	
    public RightSource getRightSource() {
    	return rightSource;
    }
    
    public Term getTerm() {
    	return term;
    }
    
    public TimePeriod getTimePeriod()  {
    	return timePeriod;
    }
    
    public PMTL getPMTL() {
    	return pmtl;
    }
}
