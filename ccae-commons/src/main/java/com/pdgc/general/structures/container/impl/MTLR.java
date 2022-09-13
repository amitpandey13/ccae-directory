package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainer;
import com.pdgc.general.structures.container.IRightTypeContainer;

/**
 * A class that groups media, territory, language and right type together
 * 
 * @author Jia
 * @author Vishal Raut
 */
public class MTLR implements IMTLContainer, IRightTypeContainer,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MTL mtl;
	private RightType rightType;

	public MTLR(Media media, Territory territory, Language language, RightType rightType) {
		this(new MTL(media, territory, language), rightType);
	}

	public MTLR(MTL mtl, RightType rightType) {
		this.mtl = mtl;
		this.rightType = rightType;
	}

	public Media getMedia() {
		return mtl.getMedia();
	}

	public TerrLang getTerrLang() {
		return mtl.getTerrLang();
	}

	public Territory getTerritory() {
		return mtl.getTerritory();
	}

	public Language getLanguage() {
		return mtl.getLanguage();
	}

	@Override
	public MTL getMTL() {
		return mtl;
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
		
		return Objects.equals(mtl, ((MTLR)obj).mtl) 
			&& Objects.equals(rightType, ((MTLR)obj).rightType);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(mtl) 
			^ Objects.hashCode(rightType);
	}

	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return mtl.getFullString() + "-" + rightType;
	}

	public String getShortString() {
		return mtl.getShortString() + "-" + rightType;
	}

}
