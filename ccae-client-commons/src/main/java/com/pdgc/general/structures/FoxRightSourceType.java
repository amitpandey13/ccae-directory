package com.pdgc.general.structures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.RightSourceType;

public class FoxRightSourceType extends RightSourceType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Map<Integer, FoxRightSourceType> rightSourceTypeMap = new HashMap<>();
	
	public static final FoxRightSourceType RIGHTSIN = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_DISTRIBUTION, BaseRightSourceType.CORPRIGHTS, "Distribution Rights");
	public static final FoxRightSourceType RESTRICTION = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_RESTRICTION, BaseRightSourceType.CORPRIGHTS, "Restriction");
	public static final FoxRightSourceType PRODUCT_LEVEL_RESTRICTION = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_PRODUCT_RESTRICTION, BaseRightSourceType.CORPRIGHTS, "Product Level Restriction");
	public static final FoxRightSourceType PLAYOFF = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_PLAYOFF, BaseRightSourceType.CORPRIGHTS, "Playoff Rights");
	public static final FoxRightSourceType DEAL = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_DEAL, BaseRightSourceType.DEAL, "Deal");
	public static final FoxRightSourceType SALESPLAN = new FoxRightSourceType(Constants.SOURCE_TYPE_ID_SALES_PLAN, BaseRightSourceType.SALESPLAN, "Sales Plan");
	
	private String description;

	/**
	 * Constructor only for jackson.
	 */
	protected FoxRightSourceType() {
		super();
	}
	
	private FoxRightSourceType(int id, BaseRightSourceType parentSourceType, String description) {
		super(id, parentSourceType);
		this.description = description;
		
		rightSourceTypeMap.put(id, this);
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	public static FoxRightSourceType byValue(int value) {
		return rightSourceTypeMap.get(value);
	}
	
	public static Collection<FoxRightSourceType> getRightSourceTypes() {
		return rightSourceTypeMap.values();
	}
}
