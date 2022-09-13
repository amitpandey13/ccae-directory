package com.pdgc.general.structures.proxystruct;

import java.util.Objects;

import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;

/**
 * Used for creating 'fake' medias that may use program-generated ids,
 * which may end up clashing with ids used by real medias from the db...
 * so this overrides the equals() method such that it only returns true 
 * using a reference equals 
 *  
 * @author Linda Xu
 *
 */
public class DummyTerritory extends Territory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DummyTerritory() {
		super();
		this.territoryLevel = TerritoryLevel.PSEUDOTERRITORY;
		
		this.territoryName = "dummyTerrritory";
		this.territoryShortName = "dummyTerritoryShortName";
	}
	
	public DummyTerritory(String customName) {
		this();
		this.setCustomName(customName);
	}
	
	public DummyTerritory(Territory territory) {
		super(territory);
	}
	
	/**
	 * Sets both the long and short names to the specified string
	 * The individual set methods are exposed if the user wants different long and short names
	 * @param customName
	 */
	public void setCustomName(String customName) {
		setTerritoryName(customName);
		setTerritoryShortName(customName);
	}
	
	@Override
	public void setTerritoryShortName(String territoryShortName) {
		super.setTerritoryShortName(territoryShortName);
	}
	
	@Override
	public void setTerritoryName(String territoryName) {
		super.setTerritoryName(territoryName);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(territoryId);
	}
}
