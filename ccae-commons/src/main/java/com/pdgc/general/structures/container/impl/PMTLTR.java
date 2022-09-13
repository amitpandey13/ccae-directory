package com.pdgc.general.structures.container.impl;


import java.time.LocalDate;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainerFull;
import com.pdgc.general.structures.container.IProductContainer;
import com.pdgc.general.structures.container.IRightTypeContainer;
import com.pdgc.general.structures.container.ITermContainer;

/**
 * A class that groups product, media, territory, language and term together
 * 
 * @author Vishal Raut
 */
public class PMTLTR implements IProductContainer, IMTLContainerFull, ITermContainer, IRightTypeContainer {

	private PMTLT pmtlt;
	private RightType rightType;

	public PMTLTR(Product product, Media media, Territory territory, Language language, LocalDate startDate, LocalDate endDate, RightType rightType) {
		this.pmtlt = new PMTLT(product, media, territory, language, startDate, endDate);
		this.rightType = rightType;
	}

	public PMTLTR(PMTLT pmtlt, RightType rightType) {
		this.pmtlt = pmtlt;
		this.rightType = rightType;
	}

	@Override
	public Product getProduct() {
		return pmtlt.getProduct();
	}
 
	@Override
	public Media getMedia() {
		return pmtlt.getMedia();
	}

	@Override
	public TerrLang getTerrLang() {
		return pmtlt.getTerrLang();
	}

	@Override
	public Territory getTerritory() {
		return pmtlt.getTerritory();
	}

	@Override
	public Language getLanguage() {
		return pmtlt.getLanguage();
	}

	@Override
	public MTL getMTL() {
		return pmtlt.getMTL();
	}

	@Override
	public Term getTerm() {
		return pmtlt.getTerm();
	}

	@Override
	public LocalDate getStartDate() {
		return pmtlt.getStartDate();
	}

	@Override
	public LocalDate getEndDate() {
		return pmtlt.getEndDate();
	}

	public PMTLT getPMTLT() {
		return pmtlt;
	}

	@Override
	public RightType getRightType() {
		return rightType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(pmtlt, ((PMTLTR)obj).getPMTLT()) 
			&& Objects.equals(rightType, ((PMTLTR)obj).getRightType());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pmtlt) 
			^ Objects.hashCode(rightType);
	}

	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return pmtlt.getPMTL().getFullString() + "-" + rightType.toString() + "(" + pmtlt.getTerm() + ")";
	}

	public String getShortString() {
		return pmtlt.getPMTL().getShortString() + "-" + rightType.toString() + "(" + pmtlt.getTerm() + ")";
	}
}
