package com.pdgc.general.util.xml;

import java.util.List;

/**
 * Wrapper for actual implementation of the xml document. So that we can easily
 * replace the xml document processing library.
 * 
 * @author Vishal Raut
 *
 */
public interface XmlDocument {
	
	XmlNode getRootElement();
	
	List<XmlNode> getElementsByTagName(String name);
	
	String outputXmlAsString();

}
