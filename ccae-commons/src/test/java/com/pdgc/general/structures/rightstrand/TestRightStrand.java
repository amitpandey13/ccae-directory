package com.pdgc.general.structures.rightstrand;

import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightstrand.impl.NonAggregateRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public abstract class TestRightStrand extends NonAggregateRightStrand {

	private static final long serialVersionUID = 1L;

    protected TestRightStrand(
		long rightStrandId, 
		PMTL pmtl, 
		TermPeriod termPeriod,
		RightType rightType, 
		PMTL actualPMTL, 
		Term origTerm, 
		String comment,
		boolean isCheckedIn
	) {
		super(rightStrandId, pmtl, termPeriod, rightType, actualPMTL, origTerm, comment, isCheckedIn);
	}
	
	protected TestRightStrand(NonAggregateRightStrand rs) {
		super(rs);
	}
	
	public void setRightStrandId(long rightStrandId) {
		this.rightStrandId = rightStrandId;
	}
	
	public void setPMTL(PMTL pmtl) {
		this.pmtl = pmtl;
	}
	
	public void setMTL(MTL mtl) {
		this.pmtl = new PMTL(pmtl.getProduct(), mtl);
	}
	
	public void setProduct(Product product) {
		this.pmtl = new PMTL(product, pmtl.getMTL());
	}

	public void setMedia(Media media) {
		this.pmtl = new PMTL(pmtl.getProduct(), media, pmtl.getTerritory(), pmtl.getLanguage());
	}

	public void setTerrLang(TerrLang terrLang) {
		this.pmtl = new PMTL(pmtl.getProduct(), pmtl.getMedia(), terrLang.getTerritory(), terrLang.getLanguage());
	}
	
	public void setTermPeriod(TermPeriod termPeriod) {
	    this.termPeriod = termPeriod;
	}
    
    public void setTerm(Term term) {
    	this.termPeriod = new TermPeriod(term, termPeriod.getTimePeriod());
    }
    
    public void setTimePeriod(TimePeriod timePeriod) {
    	this.termPeriod = new TermPeriod(termPeriod.getTerm(), timePeriod);
    }
    
    public void setRightType(RightType rightType) {
    	this.rightType = rightType;
    }
    
    public void setActualPMTL(PMTL actualPMTL) {
		this.actualPMTL = actualPMTL;
	}

	public void setActualMTL(MTL actualMTL) {
		actualPMTL = new PMTL(actualPMTL.getProduct(), actualMTL);
	}

	public void setActualProduct(Product actualProduct) {
		actualPMTL = new PMTL(actualProduct, actualPMTL.getMTL());
	}

	public void setActualMedia(Media actualMedia) {
		actualPMTL = new PMTL(actualPMTL.getProduct(), actualMedia, actualPMTL.getTerritory(), actualPMTL.getLanguage());
	}

	public void setActualTerrLang(TerrLang actualTerrLang) {
		actualPMTL = new PMTL(actualPMTL.getProduct(), actualPMTL.getMedia(), actualTerrLang.getTerritory(), actualTerrLang.getLanguage());
	}
    
    public void setOrigTerm(Term origTerm) {
    	this.origTerm = origTerm;
    }
    
    public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
