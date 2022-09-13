package com.pdgc.conflictcheck.structures.component;

import java.time.LocalDate;

import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public interface IConflictKeyContainer {
    
    ConflictKey getConflictKey();
    
    public default ConflictClass getConflictClass() {
		return getConflictKey().getConflictClass();
	}
	
	public default ConflictType getConflictType() {
		return getConflictKey().getConflictType();
	}

	public default ConflictSeverity getConflictSeverity() {
		return getConflictKey().getConflictSeverity();
	}

	public default ConflictSourceGroupKey getPrimaryConflictSourceGroupKey() {
		return getConflictKey().getPrimaryConflictSourceGroupKey();
	}

	public default RightSource getPrimaryRightSource() {
		return getConflictKey().getPrimaryRightSource();
	}

	public default RightType getPrimaryRightType() {
		return getConflictKey().getPrimaryRightType();
	}

	public default ConflictSourceGroupKey getConflictingConflictSourceGroupKey() {
		return getConflictKey().getConflictingConflictSourceGroupKey();
	}

	public default RightSource getConflictingRightSource() {
		return getConflictKey().getConflictingRightSource();
	}

	public default RightType getConflictingRightType() {
		return getConflictKey().getConflictingRightType();
	}

	public default PMTL getPMTL() {
		return getConflictKey().getPMTL();
	}

	public default MTL getMTL() {
		return getPMTL().getMTL();
	}

	public default TerrLang getTerrLang() {
		return getPMTL().getTerrLang();
	}

	public default Product getProduct() {
        return getPMTL().getProduct();
    }

    public default Media getMedia() {
        return getPMTL().getMedia();
    }

    public default Territory getTerritory() {
        return getPMTL().getTerritory();
    }

    public default Language getLanguage() {
        return getPMTL().getLanguage();
    }

    public default Term getTerm() {
		return getConflictKey().getTerm();
	}

	public default LocalDate getStartDate() {
		return getTerm().getStartDate();
	}

	public default LocalDate getEndDate() {
		return getTerm().getEndDate();
	}

	public default TimePeriod getTimePeriod() {
		return getConflictKey().getTimePeriod();
	}
}

