package com.pdgc.general.structures.rightsource;

import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.RightSourceType;

public class TestRightSourceType extends RightSourceType {

	private static final long serialVersionUID = 1L;

	static int baseTypeIdCounter = 1;
	
	public static final TestRightSourceType RIGHTSIN = new TestRightSourceType(BaseRightSourceType.CORPRIGHTS, "Distribution Rights");
	public static final TestRightSourceType RESTRICTION = new TestRightSourceType(BaseRightSourceType.CORPRIGHTS, "Restriction");
	public static final TestRightSourceType PLAYOFF = new TestRightSourceType(BaseRightSourceType.CORPRIGHTS, "Playoff Rights");
	public static final TestRightSourceType DEAL = new TestRightSourceType(BaseRightSourceType.DEAL, "Deal");
	public static final TestRightSourceType SALESPLAN = new TestRightSourceType(BaseRightSourceType.SALESPLAN, "Sales Plan");
	
	private String description;

	private TestRightSourceType() {
		super();
	}
	
	private TestRightSourceType(BaseRightSourceType parentSourceType, String description) {
		super(baseTypeIdCounter++, parentSourceType);
		this.description = description;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
}
