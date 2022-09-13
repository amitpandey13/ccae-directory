package com.pdgc.general.lookup;

public class Lookup {
	
	Long id; 
	Long lookupTypeId; 
	String name; 
	String code; 

	public Lookup(Long id, Long lookupTypeId, String name, String code) {
		this.id = id; 
		this.lookupTypeId = lookupTypeId; 
		this.name = name; 
		this.code = code; 
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLookupTypeId() {
		return lookupTypeId;
	}

	public void setLookupTypeId(Long lookupTypeId) {
		this.lookupTypeId = lookupTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return id == ((Lookup) obj).id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return code;
	}
	
}
