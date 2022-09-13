package com.pdgc.general.structures.container.impl;


import java.time.LocalDate;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainerFull;
import com.pdgc.general.structures.container.IProductContainer;
import com.pdgc.general.structures.container.ITermContainer;

/**
 * A class that groups product, media, territory, language and term together
 * 
 * @author Vishal Raut
 */
public class PMTLT implements IProductContainer, IMTLContainerFull, ITermContainer {

	PMTL pmtl;
	Term term; 

	public PMTLT(Product product, Media media, Territory territory, Language language, LocalDate startDate, LocalDate endDate) {
		pmtl = new PMTL(product, media, territory, language);
		term = new Term(startDate, endDate);
	}

	public PMTLT(PMTL pmtl, Term term) {
		this.pmtl = pmtl;
		this.term = term;
	}

	@Override
	public Product getProduct() {
		return pmtl.getProduct();
	}

	@Override
	public Media getMedia() {
		return pmtl.getMedia();
	}

	@Override
	public TerrLang getTerrLang() {
		return pmtl.getTerrLang();
	}

	@Override
	public Territory getTerritory() {
		return pmtl.getTerritory();
	}

	@Override
	public Language getLanguage() {
		return pmtl.getLanguage();
	}

	@Override
	public MTL getMTL() {
		return pmtl.getMTL();
	}

	public PMTL getPMTL() {
		return pmtl;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(pmtl, ((PMTLT)obj).getPMTL()) 
			&& Objects.equals(term, ((PMTLT)obj).getTerm());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pmtl) 
			^ Objects.hashCode(term);
	}

	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return pmtl.getFullString() + "(" + term.toString() + ")";
	}

	public String getShortString() {
		return pmtl.getShortString() + "(" + term.toString() + ")";
	}

}
