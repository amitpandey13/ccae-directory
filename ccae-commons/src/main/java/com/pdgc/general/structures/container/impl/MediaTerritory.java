package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Territory;

/**
 * A struct that groups media, territory together
 * 
 * @author Steve Wang
 */
public class MediaTerritory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Media media;
	protected Territory territory;
	
	protected MediaTerritory() {}
	

	public MediaTerritory(Media media, Territory territory) {
		this.media = media;
		this.territory = territory;
	}

	public Media getMedia() {
		return media;
	}

	public Territory getTerritory() {
		return territory;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(territory, ((MediaTerritory)obj).territory) 
			&& Objects.equals(media, ((MediaTerritory)obj).media);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(media)
			^ Objects.hashCode(territory);
	}
	
	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return media.getMediaShortName() + "/" + territory.getTerritoryShortName();
	}

}
