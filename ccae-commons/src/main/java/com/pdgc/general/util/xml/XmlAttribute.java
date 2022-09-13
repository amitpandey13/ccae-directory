package com.pdgc.general.util.xml;

/**
 * Wrapper for actual implementation of the xml attribute. So that we can easily
 * replace the xml document processing library.
 * 
 * @author Vishal Raut
 *
 */
public interface XmlAttribute {
	
	String getValue();

}
