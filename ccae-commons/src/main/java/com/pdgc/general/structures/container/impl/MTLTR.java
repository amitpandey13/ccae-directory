package com.pdgc.general.structures.container.impl;


import java.time.LocalDate;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainerFull;
import com.pdgc.general.structures.container.IRightTypeContainer;
import com.pdgc.general.structures.container.ITermContainer;

/**
 * A class that groups media, territory, language, right type and term together
 * 
 * @author Jia
 * @author Vishal Raut
 */
public class MTLTR implements IMTLContainerFull, ITermContainer, IRightTypeContainer {

	private MTLR mtlr;
	private Term term;

	public MTLTR(Media media, Territory territory, Language language, LocalDate startDate, LocalDate endDate, RightType rightType) {
		mtlr = new MTLR(media, territory, language, rightType);
		term = new Term(startDate, endDate);
	}

	public MTLTR(MTLR mtlr, Term term) {
		this.mtlr = mtlr;
		this.term = term;
	}

	@Override
	public Media getMedia() {
		return mtlr.getMedia();
	}

	@Override
	public TerrLang getTerrLang() {
		return mtlr.getTerrLang();
	}

	@Override
	public Territory getTerritory() {
		return mtlr.getTerritory();
	}

	@Override
	public Language getLanguage() {
		return mtlr.getLanguage();
	}

	@Override
	public MTL getMTL() {
		return mtlr.getMTL();
	}

	@Override
	public RightType getRightType() {
		return mtlr.getRightType();
	}

	public MTLR getMTLR() {
		return mtlr;
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
	public Term getTerm() {
		return term;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(mtlr, ((MTLTR)obj).mtlr) 
			&& Objects.equals(term, ((MTLTR)obj).term);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(mtlr) 
			^ Objects.hashCode(term);
	}

	@Override
	public String toString() {
		return getFullString();
	}
	
	public String getFullString() {
		return mtlr.getFullString() + "(" + term + ")";
	}
	
	public String getShortString() {
		return mtlr.getShortString() + "(" + term + ")";
	}

}
