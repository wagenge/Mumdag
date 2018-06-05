package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

//-----------------------------------------------------------------------------

public final class XmlUtils {
	
//=============================================================================	
/*
* 	CLASS ATTRIBUTES (private)
*/	
private static final Logger log = LogManager.getLogger(XmlUtils.class);


//=============================================================================
/*
 * 	METHODS FOR XML (public, static)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static Document createXmlDoc(String filePath) throws Exception {
    File file = new File(filePath);
    return createXmlDoc(file);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static Document createXmlDoc(File file) throws Exception {
    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return dBuilder.parse(file);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> getNodeTextByXPath(String xmlString, String xpathString) {
	List<String> retList = new ArrayList<>();
	
	if(StringUtils.isEmpty(xmlString)) {
		log.warn("cannot resolve node text from an empty xml");
		return retList;
	}

	Document xmlDoc;
	try {
	    xmlDoc = getXmlDocFromXmlString(xmlString);
	}
	catch (Exception ex) {
		log.warn("cannot resolve node text from an corrupt xml\n Error: {}", ex.getMessage());
		return retList;
	}

	if(StringUtils.isEmpty(xpathString)) {
		log.warn("cannot resolve node text from an empty xpath");
		return retList;
	}
	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	log.trace("get value on xpath='{}'", xpathString);
	
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression expr;
	NodeList nl = null;
	try {
		expr = xpath.compile(xpathString);
		nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
	} catch (XPathExpressionException e) {
		log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
	}
	
	if(nl != null && nl.getLength() == 0) {
		log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
		return retList;
	}

	if(nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
            retList.add(nl.item(i).getNodeValue());
        }
        log.trace("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
    }
	
	return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> getNodeByXPath(String xmlString, String xpathString, String childNodeNames) {
    List<String> retList = new ArrayList<>();
    if(StringUtils.isEmpty(childNodeNames)) {
        return retList;
    }
    List<String> childNodeNameList = Arrays.asList(StringUtils.stripAll(childNodeNames.split(",")));

    if(StringUtils.isEmpty(xmlString)) {
        log.warn("cannot resolve node text from an empty xml");
        return retList;
    }

    Document xmlDoc;
    try {
        xmlDoc = getXmlDocFromXmlString(xmlString);
    }
    catch (Exception ex) {
        log.warn("cannot resolve node text from an corrupt xml\n Error: {}", ex.getMessage());
        return retList;
    }

    if(StringUtils.isEmpty(xpathString)) {
        log.warn("cannot resolve node text from an empty xpath");
        return retList;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.trace("get value on xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
    }

    if(nl != null && nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
        return retList;
    }

    if(nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
            String childStr = "";
            NodeList childNl = nl.item(i).getChildNodes();
            for (int j = 0; j < childNl.getLength(); j++) {
                Node childNode = childNl.item(j);
                String childNodeName = childNode.getNodeName();
                String childNodeValue;
                if (childNodeNameList.contains(childNodeName)) {
                    if(childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElem = (Element) childNode;
                        NodeList childChildNl = childElem.getElementsByTagName("*");
                        if(childChildNl.getLength() == 0) {
                            childNodeValue = childNode.getTextContent();
                            if (StringUtils.isNotEmpty(childStr)) {
                                childStr = childStr + "," + childNodeName + "=" + childNodeValue;
                            } else {
                                childStr = childNodeName + "=" + childNodeValue;
                            }
                        }
                        else {
                            for (int k = 0; k < childChildNl.getLength(); k++) {
                                Node childChildNode = childChildNl.item(k);
                                String childChildNodeName = childChildNode.getNodeName();
                                String childChildNodeValue;
                                if(childChildNode.getNodeType() == Node.ELEMENT_NODE) {
                                    childChildNodeValue = childChildNode.getFirstChild().getNodeValue();
                                    if(StringUtils.isNotEmpty(childChildNodeValue.trim())) {
                                        if (StringUtils.isNotEmpty(childStr)) {
                                            childStr = childStr + "," + childChildNodeName + "=" + childChildNodeValue;
                                        } else {
                                            childStr = childChildNodeName + "=" + childChildNodeValue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            retList.add(childStr);
        }
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<HashMap<String, Object>> getNodeContentByXPath(String xmlString, String xpathString) {
    //List<HashMap<String, Object>> retList = new ArrayList<>();
    //NodeList nl = getNodeListFromXpath(xmlString, xpathString);
    List<String> emptyBlacklist = new ArrayList<>();
    return getNodeContentByXPathBlacklist(xmlString, xpathString, emptyBlacklist);

/*
    if(nl != null) {
        HashMap<String, Object> nodeHashMap = new HashMap<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Element childElem = (Element) nl.item(i);
            nodeTextToHashMap(childElem.getTagName(), childElem.getTextContent(), nodeHashMap);
            attributesToHashMap(childElem.getAttributes(), nodeHashMap);

            NodeList childNl = childElem.getElementsByTagName("*");
            for (int j = 0; j < childNl.getLength(); j++) {
                Node childNode = childNl.item(j);
                String childNodeName = childNode.getNodeName();
                String nodeText = "";
                if(childNode.hasAttributes()) {
                    attributesToHashMap(childNode.getAttributes(), nodeHashMap);
                }
                if(childNode.getChildNodes().getLength() <= 1) {
                    nodeText = childNode.getTextContent();
                }
                if(StringUtils.isNotEmpty(nodeText)) {
                    nodeTextToHashMap(childNodeName, nodeText, nodeHashMap);
                }
            }
            if(!nodeHashMap.isEmpty()) {
                retList.add(nodeHashMap);
            }
        }
    }
*/
    //return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<HashMap<String, Object>> getNodeContentByXPathBlacklist(String xmlString, String xpathString, List<String> blacklist) {
    List<HashMap<String, Object>> retList = new ArrayList<>();
    NodeList nl = getNodeListFromXpath(xmlString, xpathString);
    if(nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
            HashMap<String, Object> nodeHashMap = new HashMap<>();
            iterateRecursivelyAndCollectNodeContent(nl.item(i), blacklist, true, nodeHashMap);
            if(!nodeHashMap.isEmpty()) {
                retList.add(nodeHashMap);
            }
        }
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private static void iterateRecursivelyAndCollectNodeContent(Node node, List<String> blackOrWhitelist, Boolean blackOrWhite, HashMap<String, Object> nodeHashMap) {
    Boolean blackWhiteContains = false;
    if(!blackOrWhitelist.contains(node.getNodeName()) && blackOrWhite) {
        blackWhiteContains = true;
    } else if (blackOrWhitelist.contains(node.getNodeName()) && !blackOrWhite) {
        blackWhiteContains = true;
    }

    if(blackWhiteContains) {
        nodeTextToHashMap(node.getNodeName(), node.getTextContent(), nodeHashMap);
        if(node.hasAttributes()) {
            attributesToHashMap(node.getAttributes(), nodeHashMap);
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                iterateRecursivelyAndCollectNodeContent(currentNode, blackOrWhitelist, blackOrWhite, nodeHashMap);
            }
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static Node getFirstNodeByXPath(Document xmlDoc, String xpathString) {
    NodeList nl = getNodesByXPath(xmlDoc, xpathString);
    if(nl != null && nl.getLength() > 0) {
        return nl.item(0);
    }
    else {
        return null;
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static NodeList getNodesByXPath(Document xmlDoc, String xpathString) {
    if(StringUtils.isEmpty(xpathString)) {
        log.warn("cannot resolve nodes with an empty xpath string");
        return null;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.trace("get value on xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
    }
    return nl;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static Document createXmlDoc(Document oldXmlDoc, String xpathString) {
    if(StringUtils.isEmpty(xpathString)) {
        log.warn("cannot resolve nodes with an empty xpath string");
        return null;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.trace("get value on xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(oldXmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
    }

    Node node;
    Document newXmlDoc = null;
    if(nl != null && nl.getLength() > 0) {
        node = nl.item(0);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            newXmlDoc = builder.newDocument();
            Node importedNode = newXmlDoc.importNode(node, true);
            newXmlDoc.appendChild(importedNode);
        } catch (Exception ex) {
            log.error("Could not create new document! \nError: {}", ex.getMessage());
        }
    }
    return newXmlDoc;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok, via test_getNodeAttributeTextByXPath_ok()
public static List<String> getNodeAttributeTextByXPath(String xmlString, String xpathString) {
    return XmlUtils.getNodeAttributeTextByXPath(xmlString, xpathString, null);
}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> getNodeAttributeTextByXPath(String xmlString, String xpathString, String attrNames) {
    List<String> retList = new ArrayList<>();
    List<String> attrNameList = new ArrayList<>();
    if(StringUtils.isNotEmpty(attrNames)) {
        attrNameList = Arrays.asList(StringUtils.stripAll(attrNames.split(",")));
    }

    if(StringUtils.isEmpty(xmlString)) {
        log.warn("cannot resolve node text from an empty xml");
        return retList;
    }

    Document xmlDoc;
    try {
        xmlDoc = getXmlDocFromXmlString(xmlString);
    }
    catch (Exception ex) {
        log.warn("cannot resolve node text from an corrupt xml\n Error: {}", ex.getMessage());
        return retList;
    }

    if(StringUtils.isEmpty(xpathString)) {
        log.warn("cannot resolve attribute(s) '{}' from an empty xpath", StringUtils.join(attrNameList, ", "));
        return retList;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.trace("get attribute(s) '{}' on xpath='{}'", StringUtils.join(attrNameList, ", "), xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
    }

    if(nl != null && nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
        return retList;
    }

    if(nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
            Node node =  nl.item(i);
            NamedNodeMap attrMap = node.getAttributes();
            if(attrNameList.size() > 0) {
                for(String attrName : attrNameList) {
                    if(attrMap != null) {
                        Node attrNode = attrMap.getNamedItem(attrName);
                        if (attrNode != null) {
                            retList.add(attrNode.getNodeName() + "=" + attrNode.getNodeValue());
                        }
                    }
                    else {
                        retList.add(node.getNodeName() + "=" + node.getNodeValue());
                    }
                }
            }
            else {
                if(attrMap != null) {
                    for (int k = 0; k < attrMap.getLength(); k++) {
                        Node attrNode = attrMap.item(k);
                        retList.add(attrNode.getNodeName() + "=" + attrNode.getNodeValue());
                    }
                }
                else {
                    retList.add(node.getNodeName() + "=" + node.getNodeValue());
                }
            }
        }
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void writeOutputDocToFile(Document doc, String filePath, String fileName) {
    File outputPath = new File(filePath);

    if(!outputPath.exists()) {
        outputPath.mkdirs();
    }

    doc.getDocumentElement().normalize();
    Transformer transformer= null;
    try {
        transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    } catch (TransformerConfigurationException e) {
        e.printStackTrace();
    }
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File(filePath + "\\" + fileName));
    try {
        if(transformer != null) {
            transformer.transform(source, result);
        }
    } catch (TransformerException e) {
        e.printStackTrace();
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeTextByXPath(Document xmlDoc, String xpathString, String nodeText) throws Exception {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("value '{}' set on an empty xpath", nodeText);
        return;
    }

    log.trace("value '{}' set on xpath='{}'", nodeText, xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile(xpathString);
    NodeList nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

    if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. node text '{}' applied only on the first node", xpathString, nl.getLength(), nodeText);
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes. node text '{}' cannot be applied!", xpathString, nl.getLength(), nodeText);
        return;
    }
    nl.item(0).setTextContent(nodeText);
}


//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static String getNodeTextByXPath(Document xmlDoc, String xpathString) {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("node text resolved from an empty xpath");
        return "";
    }

    log.trace("get value on xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        e.printStackTrace();
    }

    if(nl == null){
        log.warn("XPath '{}' resulted in null nodes", xpathString);
        return null;
    }
    else if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. return node text from the first node", xpathString, nl.getLength());
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
        return null;
    }
    //return nl.item(0).getNodeValue();
    return nl.item(0).getTextContent();
}

//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributesByXPath(Document xmlDoc, String xpathString, List<HashMap<String, String>> attributes) throws Exception {
    for (HashMap<String, String> attribute : attributes) {
        setNodeAttributeByXPath(xmlDoc, xpathString, attribute.get("attrName"), attribute.get("attrValue"));
    }
}
*/

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributesByXPath(Document xmlDoc, String xpathString, List<String> attributes) throws Exception {
    setNodeAttributesByXPath(xmlDoc, xpathString, attributes, false);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributesByXPath(Document xmlDoc, String xpathString, List<String> attributes, Boolean replace) throws Exception {
    if(replace) {
        removeNodeAttributeByXPath(xmlDoc, xpathString);
    }
    for (String attribute : attributes) {
        String[] attrPair = attribute.split("=");
        if(attrPair.length == 2) {
            String attrName = attrPair[0];
            String attrValue = attrPair[1];
            setNodeAttributeByXPath(xmlDoc, xpathString, attrName, attrValue);
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributesByXPath(Document xmlDoc, String xpathString, String[] attributes) throws Exception {
    setNodeAttributesByXPath(xmlDoc, xpathString, attributes, false);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributesByXPath(Document xmlDoc, String xpathString, String[] attributes, Boolean replace) throws Exception {
    if(replace) {
        removeNodeAttributeByXPath(xmlDoc, xpathString);
    }
    for (int i = 0; i < attributes.length; i++){
        String attribute = attributes[i];
        String[] attrPair = attribute.split("=");
        if(attrPair.length == 2) {
            String attrName = attrPair[0];
            String attrValue = attrPair[1];
            setNodeAttributeByXPath(xmlDoc, xpathString, attrName, attrValue);
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void setNodeAttributeByXPath(Document xmlDoc, String xpathString, String attrName, String attrValue) throws Exception {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("attribute {}={} set on empty xpath", attrName, attrValue);
        return;
    }
    log.trace("attribute {}={} set on xpath='{}'", attrName, attrValue, xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile(xpathString);
    NodeList nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

    if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. attribute {}={} applied only on the first node", xpathString, nl.getLength(), attrName, attrValue);
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes. attribute {}={} cannot be applied!", xpathString, nl.getLength(), attrName, attrValue);
        return;
    }
    Element el = (Element)nl.item(0);
    el.setAttribute(attrName, attrValue);
}

//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//FUNC-NAME:		nok .. better setNodeAttributeByXPath(..)
public static void setNodeAttributeTextByXPath(Document xmlDoc, String xpathString, String attrName, String attrValue) throws Exception {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("attribute {}={} set on empty xpath", attrName, attrValue);
        return;
    }
    log.trace("attribute {}={} set on xpath='{}'", attrName, attrValue, xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile(xpathString);
    NodeList nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

    if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. attribute {}={} applied only on the first node", xpathString, nl.getLength(), attrName, attrValue);
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes. attribute {}={} cannot be applied!", xpathString, nl.getLength(), attrName, attrValue);
        return;
    }
    Element el = (Element)nl.item(0);
    el.setAttribute(attrName, attrValue);
}
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void removeNodeAttributeByXPath(Document xmlDoc, String xpathString) throws Exception {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("remove attributes from an empty xpath");
        return;
    }
    log.trace("remove attributes from xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = xpath.compile(xpathString);
    NodeList nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

    if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. attributes removed only from the first node", xpathString, nl.getLength());
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes. attribute removement cannot be applied!", xpathString, nl.getLength());
        return;
    }

    //remove all attributes
    Node node = nl.item(0);
    while (node.getAttributes().getLength() > 0) {
        Node att = node.getAttributes().item(0);
        node.getAttributes().removeNamedItem(att.getNodeName());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static String getNodeAttributeTextByXPath(Document xmlDoc, String xpathString, String attrName) {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    if(xpathString.length() == 0) {
        log.warn("attribute name {} resolved from an empty xpath", attrName);
        return "";
    }

    log.trace("get attribute '{}' on xpath='{}'", attrName, xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = null;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        e.printStackTrace();
    }

    if(nl.getLength() > 1) {
        log.warn("XPath '{}' resulted in {} nodes. attribute {} applied only on the first node", xpathString, nl.getLength(), attrName);
    }
    else if(nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
        return null;
    }
    Element el = (Element)nl.item(0);
    return el.getAttribute(attrName);
}

//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static Integer getNumberOfNodes(Document doc, String xpathString) {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.info("getNumberOfNodes: xpath={}", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        e.printStackTrace();
        return -1;
    }
    log.warn("found #{} nodes", nl.getLength());
    return nl.getLength();
}
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static Boolean checkIfNodeHasGivenContent(Document doc, String xpathString) {
    log.trace("xpath={}", xpathString);
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr = null;
    Boolean result = false;
    try {
        expr = xpath.compile(xpathString);
        result = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);
    } catch (XPathExpressionException e) {
        e.printStackTrace();
        return false;
    }
    log.trace("xpath={} ... {}", xpathString, result);
    return result;
}
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> getXpathsForMatchingNodes(Document doc, String xpathString) {
    List<String> retList = new ArrayList<>();

    if(StringUtils.isEmpty(xpathString)) {
        return retList;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr;
    NodeList nl;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.error("could not retrieve nodeset for given xpath '{}' \nError: {}", xpathString, e.getMessage());
        return retList;
    }
    if(nl != null) {
        for(int i = 0; i < nl.getLength(); i++) {
            String matchingXpathStr = xpathString+'['+String.valueOf(i+1)+']';
            XPath matchingXpath = XPathFactory.newInstance().newXPath();
            XPathExpression matchingXpathExpr;
            NodeList matchingXpathNl = null;
            try {
                matchingXpathExpr = matchingXpath.compile(matchingXpathStr);
                matchingXpathNl = (NodeList)matchingXpathExpr.evaluate(doc, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                log.error("could not retrieve nodeset for matching xpath '{}' \nError: {}", matchingXpathStr, e.getMessage());
            }
            if(matchingXpathNl != null && matchingXpathNl.getLength() == 1) {
                retList.add(matchingXpathStr);
            } else if(matchingXpathNl != null && matchingXpathNl.getLength() == 0) {
                log.error("could not retrieve nodeset for matching xpath '{}'", matchingXpathStr);
            } else if(matchingXpathNl != null && matchingXpathNl.getLength() > 1) {
                log.error("retrieved {} nodesets for matching xpath '{}'",  matchingXpathNl.getLength(), matchingXpathStr);
            } else  {
                log.error("could not retrieve nodeset for matching xpath '{}'", matchingXpathStr);
            }
        }
    }
    log.trace("xpath={} ... retrieved {} occurences", xpathString, retList.size());
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> getXpathsForMatchingEmptyNodes(Document doc, String xpathString) {
    List<String> retList = new ArrayList<>();

    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr;
    NodeList nl;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.error("could not retrieve nodeset for given xpath '{}' \nError: {}", xpathString, e.getMessage());
        return retList;
    }
    if(nl != null) {
        for(int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(isNodeEmpty(node)) {
                String matchingXpathStr = xpathString+'['+String.valueOf(i+1)+']';
                XPath matchingXpath = XPathFactory.newInstance().newXPath();
                XPathExpression matchingXpathExpr;
                NodeList matchingXpathNl = null;
                try {
                    matchingXpathExpr = matchingXpath.compile(matchingXpathStr);
                    matchingXpathNl = (NodeList)matchingXpathExpr.evaluate(doc, XPathConstants.NODESET);
                } catch (XPathExpressionException e) {
                    log.error("could not retrieve nodeset for matching xpath '{}' \nError: {}", matchingXpathStr, e.getMessage());
                }
                if(matchingXpathNl != null && matchingXpathNl.getLength() == 1) {
                    retList.add(matchingXpathStr);
                } else if(matchingXpathNl != null && matchingXpathNl.getLength() == 0) {
                    log.error("could not retrieve nodeset for matching xpath '{}'", matchingXpathStr);
                } else if(matchingXpathNl != null && matchingXpathNl.getLength() > 1) {
                    log.error("retrieved {} nodesets for matching xpath '{}'",  matchingXpathNl.getLength(), matchingXpathStr);
                } else  {
                    log.error("could not retrieve nodeset for matching xpath '{}'", matchingXpathStr);
                }
            }
        }
    }
    log.trace("xpath={} ... retrieved {} occurences", xpathString, retList.size());
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static Boolean checkIfXpathExists(Document doc, String xpathString) {
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr;
    NodeList nl;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.error("could not retrieve nodeset for given xpath '{}' \nError: {}", xpathString, e.getMessage());
        return false;
    }
    return nl.getLength() != 0;
}

//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static Integer checkIfNodeAndSubNodesAreEmpty(Document doc, String xpathString) {
    log.trace("xpath={}", xpathString);
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    String nodeText;
    try {
        expr = xpath.compile(xpathString);
        nodeText = (String) expr.evaluate(doc, XPathConstants.STRING);
    } catch (XPathExpressionException e) {
        e.printStackTrace();
        return -1;
    }
    return nodeText.trim().length();
}
*/
//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok, implicite via test_getNodeTextByXPath_ok, test_getNodeByXPath_ok and test_getNodeByXPath_ok
private static Document getXmlDocFromXmlString(String xmlString) throws Exception {
    final InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return dBuilder.parse(is);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok, implicite via test_getNodeTextByXPath_ok, test_getNodeByXPath_ok and test_getNodeByXPath_ok
private static Boolean isNodeEmpty(Node node) {
    if(node.hasAttributes()) {
        return false;
    }
    if(node.getNodeValue() != null && node.getNodeValue().trim().length() > 0) {
        return false;
    }
    if(node.hasChildNodes()) {
        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if(!isNodeEmpty(childNode)) {
                return false;
            }
        }
        return true;
    } else {
      return true;
    }
}


//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok, implicite via test_getNodeTextByXPath_ok, test_getNodeByXPath_ok and test_getNodeByXPath_ok
private static void attributesToHashMap(NamedNodeMap attributes, HashMap<String, Object> nodeHashMap) {
    for(int i = 0; i < attributes.getLength(); i++) {
        Node attribute = attributes.item(i);
        String attrName = attribute.getNodeName();
        String attrValue = attribute.getNodeValue();
        nodeTextToHashMap(attrName, attrValue, nodeHashMap);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok, implicite via test_getNodeTextByXPath_ok, test_getNodeByXPath_ok and test_getNodeByXPath_ok
private static void nodeTextToHashMap(String key, String value, HashMap<String, Object> nodeHashMap) {
    if(nodeHashMap.containsKey(key)) {
        if(nodeHashMap.containsKey(key+".list")) {
            List<String> tmpList = (List<String>) nodeHashMap.get(key+".list");
            tmpList.add(value);
            nodeHashMap.put(key+".list",tmpList);
        } else {
            List<String> newTmpList = new ArrayList<>();
            newTmpList.add((String)nodeHashMap.get(key));
            newTmpList.add(value);
            nodeHashMap.put(key+".list",newTmpList);
        }
    }
    nodeHashMap.put(key, value);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private static NodeList getNodeListFromXpath(String xmlString, String xpathString) {
    if(StringUtils.isEmpty(xmlString)) {
        log.warn("cannot resolve node text from an empty xml");
        return null;
    }

    Document xmlDoc;
    try {
        xmlDoc = getXmlDocFromXmlString(xmlString);
    }
    catch (Exception ex) {
        log.warn("cannot resolve node text from an corrupt xml\n Error: {}", ex.getMessage());
        return null;
    }

    if(StringUtils.isEmpty(xpathString)) {
        log.warn("cannot resolve node text from an empty xpath");
        return null;
    }
    if(xpathString.endsWith("/")) {
        xpathString = xpathString.substring(0, xpathString.length()-1);
    }
    log.trace("get value on xpath='{}'", xpathString);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    XPathExpression expr;
    NodeList nl = null;
    try {
        expr = xpath.compile(xpathString);
        nl = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
        log.warn("XPath '{}' is not correct\n Error: {}", xpathString, e.getMessage());
    }

    if(nl != null && nl.getLength() == 0) {
        log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
        return null;
    }
    return nl;
}

//-----------------------------------------------------------------------------

}