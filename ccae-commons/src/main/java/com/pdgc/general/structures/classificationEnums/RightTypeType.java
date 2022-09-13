package com.pdgc.general.structures.classificationEnums;

import java.io.Serializable;

/**
 * TODO: think about deprecating this or at least shifting over the declarations 
 * to the client - currently appears to be used in dealing with episode limits rules concerning holdbacks
 * and carveouts...both of which may have malleable rules depending on client anyway
 * @author Linda Xu
 *
 */
public enum RightTypeType implements Serializable{
	 HOLDBACK(0), 
	 EXHIBITION(1), 
	 EXCLUSIVE_EXHIBITION(2),
	 CORP_AVAIL(3),
	 OTHER(4);
	 
	 private int value;
	 RightTypeType(int value) {
		this.value = value;
	 }
	 public int getValue() {
		return value;
	 }
	 
	 public static RightTypeType getRightTypeType(Integer value) {
		 if(value != null) { 
			 for(RightTypeType rightTypeType : RightTypeType.values()) {
				 if(rightTypeType.getValue() == value) {
					 return rightTypeType;
				 }
			 }
		 }
		 return null;
	 }
	 
	 
	 
}
