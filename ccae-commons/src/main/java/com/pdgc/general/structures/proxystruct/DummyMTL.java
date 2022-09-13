package com.pdgc.general.structures.proxystruct;

import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.TerrLang;

public class DummyMTL extends MTL {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String customName;
	
	public DummyMTL() {
		super();
		//Set some dummies to prevent null pointer errors later...
		media = new DummyMedia();
		terrLang = new DummyTerrLang();
		customName = "dummyMTL";
	}
	
	public DummyMTL(
		Media media,
		TerrLang terrLang
	) {
		super(media, terrLang);
	}
	
	public String getCustomName() {
        if (customName != null)
        {
            return customName;
        }

        return getFullString();
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
