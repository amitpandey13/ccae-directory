package com.pdgc.general.structures.proxystruct;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.TerrLang;

/**
 * This TerrLang exposes the naming methods for a normal TerrLang structures,
 * as the source Territory and Language structures are probably some form of
 * Dummy structure (ie. one not from the database)
 *  
 * @author Linda Xu
 *
 */
public class DummyTerrLang extends TerrLang {

	private static final long serialVersionUID = 1L;
	protected String customName;
	
	public DummyTerrLang() {
		super();
		//Set some dummies to prevent null pointer errors later...
		territory = new DummyTerritory();
		language = new DummyLanguage();
		customName = "dummyTerrLang";
	}
	
	public DummyTerrLang(String customName) {
		this();
		this.customName = customName;
	}
	
	public DummyTerrLang(
		Territory territory,
		Language language
	) {
		super(territory, language);
	}
	
	public String getCustomName() {
	    if (customName == null) {
            customName = super.getFullString();
        }

        return customName;
    }

    public void setCustomName(String customName) {
        if (customName == null) {
        	customName = "";
        }
        this.customName = customName;
    }

	@Override 
	public String getFullString() {
		return getCustomName();
	}
	
	@Override
	public String getShortString() {
		return getCustomName();
	}
	
	@Override
	public String toString() {
		return getCustomName();
	}
}
