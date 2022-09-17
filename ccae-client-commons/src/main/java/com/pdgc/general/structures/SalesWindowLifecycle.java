package com.pdgc.general.structures;

import java.io.Serializable;

/**
 * Entity for masterData.sales_window_lifecycle Lifecycle is a property of sales
 * window
 */

public enum SalesWindowLifecycle implements Serializable {

	FIRST_RUN("First-Run", "Current", 0), 
	FIRST_CYCLE_LIBRARY("1st Cycle Library", "1st Cycle Library", 1),
	LIBRARY("Library", "Library", 2);

	private String id;
	private String name;
	private int index;

	SalesWindowLifecycle(String id, String name, int index) {
		this.id = id;
		this.name = name;
		this.index = index;
	}

	public static SalesWindowLifecycle byId(String id) {
		for (SalesWindowLifecycle foxSalesWindowLifeCycle : SalesWindowLifecycle.values()) {
			if (foxSalesWindowLifeCycle.getId().equals(id)) {
				return foxSalesWindowLifeCycle;
			}
		}
		return null;
	}
	
	public static SalesWindowLifecycle byName(String name) {
		for (SalesWindowLifecycle foxSalesWindowLifeCycle : SalesWindowLifecycle.values()) {
			if (foxSalesWindowLifeCycle.getName().equals(name)) {
				return foxSalesWindowLifeCycle;
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
}
