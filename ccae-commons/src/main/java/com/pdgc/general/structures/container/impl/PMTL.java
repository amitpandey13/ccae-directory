package com.pdgc.general.structures.container.impl;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.IMTLContainerFull;
import com.pdgc.general.structures.container.IProductContainer;

/**
 * A class that groups product, media, territory, and language together
 * 
 * We have to be careful when we instantiate new objects here
 * we do not want to simple use the existing MTL because an outside
 * agent will have a reference to it, and if it changes.. then we change as well
 * very bad if we do.
 * 
 * So in the constructor.. we do NOT want to re-use the passed information.
 * 
 * @author Vishal Raut
 */
public class PMTL implements IProductContainer, IMTLContainerFull ,Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Product product;
	private MTL mtl;

	protected PMTL() {}
	
	public PMTL(Product product, Media media, Territory territory, Language language) {
		this.product = product;
		mtl = new MTL(media, territory, language);
	}

	public PMTL(Product product, MTL newmtl) {
		this.product = product;
		this.mtl = new MTL(newmtl.getMedia(),newmtl.getTerrLang());
	}
	
	public PMTL(PMTL pmtl) {
		this.product = pmtl.getProduct();
		this.mtl = new MTL(pmtl.getMedia(), pmtl.getTerritory(), pmtl.getLanguage());
	}

	@Override
	public Product getProduct() {
		return product;
	}
	
	@Override
	public MTL getMTL() {
		return mtl;
	}

	@Override
	public Media getMedia() {
		return mtl.getMedia();
	}

	@Override
	public TerrLang getTerrLang() {
		return mtl.getTerrLang();
	}

	@Override
	public Territory getTerritory() {
		return mtl.getTerritory();
	}

	@Override
	public Language getLanguage() {
		return mtl.getLanguage();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(product, ((PMTL)obj).product) 
			&& Objects.equals(mtl, ((PMTL)obj).mtl);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(product) 
			^ Objects.hashCode(mtl);
	}

	@Override
	public String toString() {
		return getFullString();
	}

	public String getFullString() {
		return product.getTitle() + "/" + mtl.getFullString();
	}

	public String getShortString() {
		return product.getProductId() + "/" + mtl.getShortString();
	}
}
