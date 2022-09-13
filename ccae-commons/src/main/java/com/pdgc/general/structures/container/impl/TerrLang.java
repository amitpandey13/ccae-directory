package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.ITerrLangContainerFull;

/**
 * A class that groups territory and language together
 * 
 * @author Vishal Raut
 */
public class TerrLang implements ITerrLangContainerFull, Serializable {
	
    private static final long serialVersionUID = 1L;

    protected Territory territory;
	protected Language language;
	
	protected TerrLang() {}
	
	public TerrLang(Territory territory, Language language) {
		super();
		this.territory = territory;
		this.language = language;
	}
	
	@Override
	public Territory getTerritory() {
		return territory;
	}
	
	@Override
	public Language getLanguage() {
		return language;
	}
	

	protected void setTerritory(Territory territory) {
		this.territory = territory;
	}

	protected void setLanguage(Language language) {
		this.language = language;
	}

	public String getFullString() {
		return territory.getTerritoryName() + "/" + language.getLanguageName();
	}
	
	public String getShortString() {
		return territory.getTerritoryShortName() + "/" + language.getLanguageShortName();
	}

	@Override
	public TerrLang getTerrLang() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(territory, ((TerrLang)obj).territory) 
			&& Objects.equals(language, ((TerrLang)obj).language);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(territory) 
			^ Objects.hashCode(language);
	}

	@Override
	public String toString() {
		return getFullString();
	}
}
