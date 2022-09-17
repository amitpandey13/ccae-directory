package com.pdgc.general.structures.rightsource;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors; 

public class FoxBusinessUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String description;
	private String code;

	public FoxBusinessUnit(Long id, String description, String code) {
		this.id = id;
		this.description = description;
		this.code = code;
	}
	
	public FoxBusinessUnit(){
        // This constructor is intentionally empty. Nothing special is needed here.
    }

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getCode() {
		return code;
	}

	public static List<String> getCodes(List<FoxBusinessUnit> businessUnits) {
	    return businessUnits.stream()
                  .map(FoxBusinessUnit::getCode)
                      .collect(Collectors.toList());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return id == ((FoxBusinessUnit) obj).id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public String toString() {
		return description;
	}
}
