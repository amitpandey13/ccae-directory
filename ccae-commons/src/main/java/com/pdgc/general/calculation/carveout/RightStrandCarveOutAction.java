package com.pdgc.general.calculation.carveout;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 1 - Don't apply the holdback that had the carveout (rightStrand conflict is okay)
 * 2 - 
 * 3 - 
 * 4 - Apply the holdback (rightStrand conflict will apply)
 * 5 -
 * @author thomas
 *
 */
public enum RightStrandCarveOutAction {
    UNSET(0),
	IGNORE_RIGHT_STRAND(1),
    TRANSFERRABLE_IGNORE_RIGHT_STRAND(2),
    CONDITIONAL(3),
    APPLY_RIGHT_STRAND(4),        
    BLOCKED(5);
    
    private int value;
	RightStrandCarveOutAction(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
	public static RightStrandCarveOutAction[] getRightStrandCarveOutActionsInDescendingOrder() {
		  RightStrandCarveOutAction[] rightStrandCarveOutActionArray = RightStrandCarveOutAction.values();
          Comparator<RightStrandCarveOutAction> rightStrandCarveOutActionComparator = new Comparator<RightStrandCarveOutAction>() {
          	@Override
          	public int compare(RightStrandCarveOutAction left, RightStrandCarveOutAction right) {
          		return Integer.compare(left.value, right.value);
          	}
          };
          Arrays.sort(rightStrandCarveOutActionArray, rightStrandCarveOutActionComparator);
          return rightStrandCarveOutActionArray;
	}
    
}

