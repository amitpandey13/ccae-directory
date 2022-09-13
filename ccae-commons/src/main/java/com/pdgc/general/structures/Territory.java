package com.pdgc.general.structures;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.pdgc.general.structures.classificationEnums.TerritoryLevel;

/**
 * A class to describe a territory
 * 
 * @author Vishal Raut
 */
public class Territory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Long territoryId;
	protected String territoryName;
	protected String territoryShortName;
	protected TerritoryLevel territoryLevel;
	
	protected Territory() {}
	
	public Territory(Long territoryId, String territoryName, TerritoryLevel territoryLevel) {
		this(territoryId, territoryName, null, territoryLevel);
	}

	public Territory(Long territoryId, String territoryName, String territoryShortName, TerritoryLevel territoryLevel) {
		this.territoryId = territoryId;
		setTerritoryName(territoryName);
		setTerritoryShortName(territoryShortName);
		this.territoryLevel = territoryLevel;
	}

	public Territory(Territory t) {
		territoryId = t.territoryId;
		territoryName = t.territoryName;
		territoryShortName = t.territoryShortName;
		territoryLevel = t.territoryLevel;
	}
	
	protected void setTerritoryName(String territoryName) {
		if (StringUtils.isBlank(territoryName)) {
			territoryName = "";
		}
		this.territoryName = territoryName;
	}
	
	protected void setTerritoryShortName(String territoryShortName) {
		if (StringUtils.isBlank(territoryShortName)) {
			territoryShortName = territoryName;
		}
		this.territoryShortName = territoryShortName;
	}

	public Long getTerritoryId() {
		return territoryId;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public String getTerritoryShortName() {
		return territoryShortName;
	}
	
	public TerritoryLevel getTerritoryLevel() {
		return territoryLevel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return territoryId.equals(((Territory) obj).territoryId);
	}

	@Override
	public int hashCode() {
		return territoryId.hashCode();
	}

	@Override
	public String toString() {
		return territoryId + " = " + territoryName;
	}
}
