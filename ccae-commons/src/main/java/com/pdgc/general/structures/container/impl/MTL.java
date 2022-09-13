package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainer;

/**
 * A struct that groups media, territory, and language together
 * 
 * @author Vishal Raut
 */
public class MTL implements IMTLContainer, Serializable {
	
    private static final long serialVersionUID = 1L;
	
	protected Media media;
	protected TerrLang terrLang;
	
	protected MTL() {}
	
	public MTL(Media media, Territory territory, Language language) {
		this(media, new TerrLang(territory, language));
	}

	public MTL(Media media, TerrLang terrLang) {
		this.media = media;
		this.terrLang = terrLang;
	}

	public Media getMedia() {
		return media;
	}

	public TerrLang getTerrLang() {
		return terrLang;
	}

	public Territory getTerritory() {
		return terrLang.getTerritory();
	}
	
	public Language getLanguage() {
		return terrLang.getLanguage();
	}

	@Override
	public MTL getMTL() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(terrLang, ((MTL)obj).terrLang) 
			&& Objects.equals(media, ((MTL)obj).media);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(media)
			^ Objects.hashCode(terrLang);
	}
	
	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return media.getMediaShortName() + "/" + terrLang.getFullString();
	}

	public String getShortString() {
		return media.getMediaShortName() + "/" + terrLang.getShortString();
	}

}
