package com.pdgc.avails.structures.rollup.tab.result;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class RollupResultMeta<E> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private E result;
	private Collection<LeafSource> leafSources;
	private Map<RightStrand, InfoStrandParams> rightStrands;
	
	
	public RollupResultMeta(
		E result,
		Collection<LeafSource> leafSources,
		Map<RightStrand, InfoStrandParams> rightStrands
	) {
		this.result = result;
		this.leafSources = leafSources;
		this.rightStrands = rightStrands;
	}
	
	public E getResult() {
		return result;
	}
	
	public Collection<LeafSource> getLeafSources() {
		return leafSources;
	}
	
	public Map<RightStrand, InfoStrandParams> getRightStrands() {
		return rightStrands;
	}
}
