package com.pdgc.general.util.xml;

public class XmlDocumentFactoryRegistry {

	private static XmlDocumentFactory sXmlDocumentFactory;

	public static XmlDocumentFactory getXmlDocumentFactory() {
		if (sXmlDocumentFactory == null) {
			sXmlDocumentFactory = new JDomXmlDocumentFactory();
		}
		return sXmlDocumentFactory;
	}
}
