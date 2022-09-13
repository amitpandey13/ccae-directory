package com.pdgc.general.util.xml;

import java.io.IOException;

/**
 * Abstract Factory pattern to create XML Documents so that it is easy to
 * replace it with any library implementation.
 * 
 * @author Vishal Raut
 *
 */
public interface XmlDocumentFactory {
	
	XmlDocument createDocument(XmlNode root);
	
	XmlDocument createDocumentFromXmlText(String xml) throws IOException;

	XmlNode createNode(String name);
	
	XmlNode createNode(String name, String namespace);
	
	XmlNode createNode(String name, String namespace, String text);

	XmlAttribute createAttribute(String name, String value);

}
