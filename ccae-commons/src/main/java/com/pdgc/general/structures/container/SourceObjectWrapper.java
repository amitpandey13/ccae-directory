package com.pdgc.general.structures.container;

import java.util.Objects;

/**
 * Simple wrapper class for wrapping objects and giving them some kind of label 
 * This should be used in classes such as SetContainer or PMTLGroup, 
 * where the source objects were intentionally made as generic as possible (hence the Object declaration)
 * in order to facilitate grouping mechanisms, but where the containers may need to be
 * casted back to their original forms later...
 * 
 * It will still be on the caller to deal with properly casting the source objects back, 
 * but this wrapper should help with being able to tag and later re-identify the containers 
 * 
 * @author Linda Xu
 *
 */
public class SourceObjectWrapper {

	private String sourceType;
	private Object sourceObject;
	
	public SourceObjectWrapper(
		String sourceType,
		Object sourceObject
	) {
		this.sourceType = sourceType;
		this.sourceObject = sourceObject;
	}
	
	public String getSourceType() {
		return sourceType;
	}
	
	public Object getSourceObject() {
		return sourceObject;
	}
	
	@Override
	public String toString() {
		return sourceType + ": " + sourceObject.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(sourceType, ((SourceObjectWrapper)obj).sourceType) 
		    && Objects.equals(sourceObject, ((SourceObjectWrapper)obj).sourceObject);
	}
	
	@Override
	public int hashCode() {
		return sourceObject.hashCode();
	}
}
