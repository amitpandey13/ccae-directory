package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMediaContainer;
import com.pdgc.general.structures.container.IProductContainer;
import com.pdgc.general.structures.container.ITerritoryContainer;

/**
 * A class that groups product, media and territory together
 * 
 * @author Vishal Raut
 */
public class PMT implements IProductContainer, IMediaContainer, ITerritoryContainer, Serializable {
    private static final long serialVersionUID = 1L;
	
    Product product;
	Media media;
	Territory territory;

	public PMT(Product product, Media media, Territory territory) {
		this.product = product;
		this.media = media;
		this.territory = territory;
	}

	@Override
	public Product getProduct() {
		return product;
	}

	@Override
	public Media getMedia() {
		return media;
	}

	@Override
	public Territory getTerritory() {
		return territory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(product, ((PMT)obj).product) 
			&& Objects.equals(media, ((PMT)obj).media) 
			&& Objects.equals(territory, ((PMT)obj).territory);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(product) 
			^ Objects.hashCode(media) 
			^ Objects.hashCode(territory);
	}

	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return product.toString() + "/" + media.getMediaName() + "/" + territory.getTerritoryName();
	}

	public String getShortString() {
		return product.toString() + "/" + media.getMediaShortName() + "/" + territory.getTerritoryShortName();
	}

}