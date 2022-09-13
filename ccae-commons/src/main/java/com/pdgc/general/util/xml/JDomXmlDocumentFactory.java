package com.pdgc.general.util.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * Implementation of XmlDocumentFactory which uses JDom Library
 * 
 * @author Vishal Raut
 * This is still needed for excel snippets but json should be used for any other data.
 */
public class JDomXmlDocumentFactory implements XmlDocumentFactory {

	@Override
	public XmlDocument createDocument(XmlNode root) {
		return new JDomXmlDocument(new Document(((JDomXmlNode) root).getElement()));
	}
	
	@Override
	public XmlDocument createDocumentFromXmlText(String xml) {
		try {
			/**
			 * SAXBuilder is not a threadsafe object!!!! Since each node will run on a single process without parallel processing,
			 * it should be ok to put this in a static context for this class and only initialize once.
			 * However, i think conflictDataFetch and availsDataFetch may do some parallel processing which would cause an issue.
			 * So for the time being, this heavyweight initialization of the SAXBuilder needs to to be done each time.
			 */
			SAXBuilder builder = new SAXBuilder();
			Document jdomDoc = builder.build(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			return new JDomXmlDocument(jdomDoc);
		} catch (IOException | JDOMException e) {
			throw new IllegalArgumentException("Error occurred while creating xml document from passed text", e);
		}
	}
	
	@Override
	public XmlNode createNode(String name) {
		return createNode(name, null);
	}

	@Override
	public XmlNode createNode(String name, String namespace) {
		return createNode(name, namespace, null);
	}
	
	@Override
	public XmlNode createNode(String name, String namespace, String text) {
		JDomXmlNode xmlNode = new JDomXmlNode(new Element(name, namespace));
		xmlNode.setText(text);
		return xmlNode;
	}

	@Override
	public XmlAttribute createAttribute(String name, String value) {
		return new JDomXmlAttribute(new Attribute(name, value));
	}

	private static class JDomXmlDocument implements XmlDocument {

		private Document document;
		private Map<String, List<XmlNode>> elementsCache = new HashMap<>();

		public JDomXmlDocument(Document document) {
			this.document = document;
		}
		
		@Override
		public XmlNode getRootElement() {
			return new JDomXmlNode(new WeakReference<>(document.getRootElement()));
		}

		@Override
		public List<XmlNode> getElementsByTagName(String name) {
			List<XmlNode> nodes;
			if (elementsCache.containsKey(name)) {
				nodes = elementsCache.get(name);
			} else {
				Element root = document.getRootElement();
				List<Element> children = root.getChildren(name, root.getNamespace());
				if (children != null && !children.isEmpty()) {
					nodes = new ArrayList<>(children.size());
					children.forEach(c -> nodes.add(new JDomXmlNode(c)));
				} else {
					nodes = Collections.emptyList();
				}
				elementsCache.put(name, nodes);
			}
			return nodes;
		}

		@Override
		public String outputXmlAsString() {
			XMLOutputter xmlOutput = new XMLOutputter();
			return xmlOutput.outputString(document);
		}

	}

	private static class JDomXmlNode implements XmlNode {

		private Element element;
		private WeakReference<Element> elementWeakRef;
		private Map<String, Attribute> attributesCache = new HashMap<>();

		public JDomXmlNode(Element element) {
			this.element = element;
		}

		public JDomXmlNode(WeakReference<Element> elementWeakRef) {
			this.elementWeakRef = elementWeakRef;
		}

		private Element getElement() {
			return (element == null) ? elementWeakRef.get() : element;
		}

		@Override
		public String getInnerXml() {
			return getElement().getText();
		}

		@Override
		public boolean hasAttributes() {
			return getElement().hasAttributes();
		}

		@Override
		public XmlAttribute getAttribute(String name) {
			if (!hasAttributes()) {
				return null;
			}

			if (attributesCache.isEmpty()) {
				buildAttributesCache();
			}
			Attribute attribute = attributesCache.get(name);
			if (attribute != null) {
				return new JDomXmlAttribute(attribute);
			} else {
				return null;
			}
		}
		

		@Override
		public void setText(String text) {
			getElement().setText(text);
		}

		@Override
		public void addChild(XmlNode node) {
			// remove the element from existing parent, if any
			Element element = ((JDomXmlNode) node).getElement();
			if (element.getParent() != null) {
				element.getParent().removeContent(element);
			}
			getElement().addContent(((JDomXmlNode) node).getElement());
		}
		
		@Override
		public boolean removeChild(String name) {
			return getElement().removeChild(name, getElement().getNamespace());
		}

		@Override
		public void setAttribute(String name, String value) {
			getElement().setAttribute(name, value);
		}

		@Override
		public XmlNode getChild(String name) {
			Element child = getElement().getChild(name, getElement().getNamespace());
			if (child != null) {
				return new JDomXmlNode(new WeakReference<>(child));
			} else {
				return null;
			}
		}

		@Override
		public List<XmlNode> getChildren(String name) {
			List<Element> children = getElement().getChildren(name, getElement().getNamespace());
			List<XmlNode> childNodes = new ArrayList<>(children.size());
			children.forEach(c -> childNodes.add(new JDomXmlNode(new WeakReference<>(c))));
			return childNodes;
		}
		
		@Override
		public String getNamespace() {
			return getElement().getNamespace().getURI();
		}
		
		@Override
		public void setNamespace(String namespace) {
			getElement().setNamespace(Namespace.getNamespace(namespace));
		}

		private void buildAttributesCache() {
			for (Attribute attribute : getElement().getAttributes()) {
				attributesCache.put(attribute.getName(), attribute);
			}
		}
	}

	private static class JDomXmlAttribute implements XmlAttribute {

		private Attribute attribute;

		public JDomXmlAttribute(Attribute attribute) {
			this.attribute = attribute;
		}

		@Override
		public String getValue() {
			return attribute != null ? attribute.getValue() : null;
		}
	}

}
