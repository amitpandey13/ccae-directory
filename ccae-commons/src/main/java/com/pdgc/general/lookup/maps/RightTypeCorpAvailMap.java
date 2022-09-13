package com.pdgc.general.lookup.maps;

import java.util.Map;

import com.pdgc.general.structures.RightType;

public class RightTypeCorpAvailMap {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger( RightTypeCorpAvailMap.class);
	
	/**
	 * Key: non-Corporate availability right type
	 * Value: The Corporate availability required for the key
	 */
	private Map<Long, RightType> rightTypeCorpAvailMap;
	
	public RightTypeCorpAvailMap(Map<Long, RightType> rightTypeCorpAvailMap) {
		this.rightTypeCorpAvailMap = rightTypeCorpAvailMap;
	}
	
	/**
	 * Gets the type of corporate availability needed for the requested right type to be available
	 * 
	 * @param requestedType
	 * @return
	 */
	public RightType getRequiredCorpAvailRightType(RightType requestedType) {
		if (!rightTypeCorpAvailMap.containsKey(requestedType.getRightTypeId())) {
			throw new IllegalArgumentException("Unrecognized right type: " + requestedType.getRightTypeId().toString());
		}

		return rightTypeCorpAvailMap.get(requestedType.getRightTypeId());
	}
}
