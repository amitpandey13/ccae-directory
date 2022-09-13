package com.pdgc.general.structures.rightsource;
 
import java.io.Serializable;

/**
 * An enumeration that describes the two types of right sources. For now, we
 * only get things that affect license availability from deals and the umbrella
 * type of 'Corporate' (which is used to describe anything not produced by sales
 * planners
 * Values are taken from lookup for lookuptypeid = 9.
 *
 * @author gowtham
 * 
 * 
 */
public enum BaseRightSourceType implements Serializable{
	CORPRIGHTS, 
	DEAL, 
	SALESPLAN
}
