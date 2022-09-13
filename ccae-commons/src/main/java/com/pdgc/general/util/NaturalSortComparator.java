package com.pdgc.general.util;

import java.util.Comparator;

/**
 * Orders strings according to the "natural" sort, which considers any numbers in a string
 * Ex: Given "String_1", "String_2", "String_10", and "String_100", 
 * 	the default string sort will order it as "String_1, "String_10", "String_100", "String_2",
 *  while using this comparer will return the order as "String_1", "String_2", "String_10", and "String_100"
 * 
 * This comparator is declard on Object rather than String simply for ease-of-use, as all Objects have toString() methods.
 * If the desire is to use something other than the object's default toString() method, then the caller will have to deal 
 * with casting things over to strings
 * 
 * @author Linda Xu
 *
 */
public class NaturalSortComparator implements Comparator<Object> {

	@Override
	public int compare(Object left, Object right) {
		//returns -1, 0, 1
        if (left == right) {
            return 0;
        }

        String[] leftArray = left.toString().split("(?=(?!^)\\d)(?<!\\d)|(?!\\d)(?<=\\d)");
        String[] rightArray = right.toString().split("(?=(?!^)\\d)(?<!\\d)|(?!\\d)(?<=\\d)");

        int maxLength = Math.min(leftArray.length, rightArray.length);
        for (int i = 0; i < maxLength; i++) {
            int compareResult = partCompare(leftArray[i], rightArray[i]);

            if (compareResult != 0) {
                return compareResult;
            }
        }

        if (leftArray.length < rightArray.length) {
            return -1;
        }
        else if (leftArray.length > rightArray.length) {
            return 1;
        }

        return 0;
	}
	
	private int partCompare(String left, String right) {
        boolean leftIsNum = false;
        int leftInt = 0;
        try {
        	leftInt = Integer.parseInt(left);
        	leftIsNum = true;
        }
		catch (NumberFormatException e) {
			leftIsNum = false;
		}
		
		boolean rightIsNum = false;
		int rightInt = 0;
		try {
        	rightInt = Integer.parseInt(right);
        	rightIsNum = true;
        }
		catch (NumberFormatException e) {
			rightIsNum = false;
		}
		
		
		if (leftIsNum && !rightIsNum) {
            return -1;
        }
        else if (!leftIsNum && rightIsNum) {
            return 1;
        }
        else if (leftIsNum && rightIsNum) {
            if (leftInt < rightInt) {
                return -1;
            }
            else if (rightInt < leftInt) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            return left.compareTo(right);
        }
    }

}
