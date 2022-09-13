package com.pdgc.conflictcheck.structures;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.ConflictType;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.impl.TestConflictSourceGroupKey;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestConflictKey extends ConflictKey {
	
	private static final long serialVersionUID = 1L;

	public TestConflictKey(
		ConflictClass conflictClass,
        TestConflictSourceGroupKey primaryConflictSourceGroupKey,
        TestConflictSourceGroupKey conflictingConflictSourceGroupKey,
        PMTL pmtl,
        Term term,
        TimePeriod timePeriod
	) {
		super(
			conflictClass, 
			primaryConflictSourceGroupKey, 
			conflictingConflictSourceGroupKey, 
			pmtl, 
			null,
			term, 
			timePeriod
		);
	}
	
	TestConflictKey(ConflictKey keyContainer) {
		super(keyContainer);
	}
	
	public void setConflictClass(ConflictClass conflictClass) {
		this.conflictClass = conflictClass;
	}
	
	public void setConflictType(ConflictType conflictType) {
		this.conflictClass = new ConflictClass(conflictType, conflictClass.getConflictSeverity());
	}
	
	public void setConflictSeverity(ConflictSeverity conflictSeverity) {
		this.conflictClass = new ConflictClass(conflictClass.getConflictType(), conflictSeverity);
	}
	
	public void setPrimaryConflictSourceGroupKey (ConflictSourceGroupKey primaryConflictSourceGroupKey) {
		this.primaryConflictSourceGroupKey = primaryConflictSourceGroupKey;
	}
	
	public void setConflictingConflictSourceGroupKey (ConflictSourceGroupKey conflictingConflictSourceGroupKey) {
		this.conflictingConflictSourceGroupKey = conflictingConflictSourceGroupKey;
	}
	
	public void setPMTL (PMTL pmtl) {
		this.pmtl = pmtl;
	}
	
	public void setMTL (MTL mtl) {
		this.pmtl = new PMTL(pmtl.getProduct(), mtl);
	}
	
	public void setProduct (Product product) {
		this.pmtl = new PMTL(product, pmtl.getMTL());
	}
	
	public void setMedia (Media media) {
		this.pmtl = new PMTL(pmtl.getProduct(), media, pmtl.getTerritory(), pmtl.getLanguage());
	}
	
	public void setTerritory (Territory territory) {
		this.pmtl = new PMTL(pmtl.getProduct(), pmtl.getMedia(), territory, pmtl.getLanguage());
	}
	
	public void setLanguage (Language language) {
		this.pmtl = new PMTL(pmtl.getProduct(), pmtl.getMedia(), pmtl.getTerritory(), language);
	}
	
	public void setTerrLang (TerrLang terrLang) {
		this.pmtl = new PMTL(pmtl.getProduct(), pmtl.getMedia(), terrLang.getTerritory(), terrLang.getLanguage());
	}
	
	public void setTerm (Term term) {
		this.term = term;
	}
	
	public void setTimePeriod (TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}
}
