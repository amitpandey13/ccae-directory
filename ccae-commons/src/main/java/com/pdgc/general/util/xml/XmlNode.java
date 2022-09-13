package com.pdgc.general.util.xml;

import java.util.List;

/**
 * Wrapper for actual implementation of the xml node. So that we can easily
 * replace the xml document processing library.
 * 
 * @author Vishal Raut
 *
 */
public interface XmlNode {

	String getInnerXml();
	
	boolean hasAttributes();
	
	XmlAttribute getAttribute(String name);
	
	void setText(String text);
	
	void setAttribute(String name, String value);
	
	void addChild(XmlNode node);
	
	XmlNode getChild(String name);
	
	List<XmlNode> getChildren(String name);
	
	boolean removeChild(String name);
	
	String getNamespace();
	
	void setNamespace(String namespace);
	
}
