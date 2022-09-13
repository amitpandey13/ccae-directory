package com.pdgc.conflictcheck.service.comparer;

import java.util.Collections;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.container.impl.PMTLSetNoId;

public class PMTLSetEquivalenceGroupedMedia extends Equivalence<PMTLSetNoId> {

	@Override
	protected boolean doEquivalent(PMTLSetNoId left, PMTLSetNoId right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false; 
        }
        if (left != null && right == null) {
            return false;
        } 
        
        return !Collections.disjoint(left.getProductSet(), right.getProductSet())
        	&& !Collections.disjoint(left.getTerritorySet(), right.getTerritorySet())
        	&& !Collections.disjoint(left.getLanguageSet(), right.getLanguageSet())
        ;
	}

	@Override
	protected int doHash(PMTLSetNoId arg0) {
		return 1;
	}	
}

