package com.pdgc.general.structures.container.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.container.impl.PMTL;

public class PMTLEquivalenceGroupedMedia extends Equivalence<PMTL>{

	@Override
	protected boolean doEquivalent(PMTL left, PMTL right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }
        
        return Objects.equals(left.getProduct(), right.getProduct())
        	&& Objects.equals(left.getTerritory(), right.getTerritory())
        	&& Objects.equals(left.getLanguage(), right.getLanguage())
        ;
	}

	@Override
	protected int doHash(PMTL obj) {
		return Objects.hashCode(obj.getProduct())
			^ Objects.hashCode(obj.getTerritory())
			^ Objects.hash(obj.getLanguage())
		;
	}	
}