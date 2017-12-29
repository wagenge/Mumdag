package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//-----------------------------------------------------------------------------

public final class XmlUtils {
	
//=============================================================================	
/*
* 	CLASS ATTRIBUTES (private)
*/	
private static final Logger log = LogManager.getLogger(XmlUtils.class);

//=============================================================================	
/*
* 	METHODS (public, static)
*/

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static String resolveXpathString(String xpath, String... replacementStrings) {
	if(StringUtils.isEmpty(xpath)) {
		return xpath;
	}
	for (String replacementString : replacementStrings) {
		if(StringUtils.isNotEmpty(replacementString)) {
			String[] parts = replacementString.split("::");
			if (parts.length == 2) {
				String replKey = parts[0];
				String replVal = parts[1];
				xpath = xpath.replaceAll(replKey, replVal);
			} else if (parts.length == 1) {
				String replKey = parts[0];
				int idxOfReplKey = xpath.indexOf(replKey);
				if (idxOfReplKey != -1) {
					xpath = xpath.substring(0, idxOfReplKey - 1) + xpath.substring(idxOfReplKey + replKey.length() + 1, xpath.length());
				}
			}
		}
	}
	return xpath;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static String resolveXpathString(String xpath, HashMap<String, String> resolveXpathInfos) {
	String patternStr = "(\\[|'| )(_[a-zA-Z]{3,}_)(\\]|'| )";
	return resolveXpathString(xpath, resolveXpathInfos, patternStr);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				ok (implicite with test_resolveXpathString_map_ok()
private static String resolveXpathString(String xpath, HashMap<String, String> resolveXpathInfos, String patternStr) {
    if(StringUtils.isEmpty(xpath)) {
        return xpath;
    }
	Pattern pattern = Pattern.compile(patternStr);
	Matcher matcher = pattern.matcher(xpath);
	while (matcher.find()) {
		String replaceStr = matcher.group(2);
		String strPrefix = matcher.group(1);
		String strPostfix = matcher.group(3);
		//search in the resolveXpathInfo map with the key (replaceStr), but without the leading and closing '_' 
		String newReplacementStr = replaceStr.substring(1, replaceStr.length()-1);
		String replacementStr = resolveXpathInfos.get(newReplacementStr);
		
		//if nothing found in the resolveXpathInfo map, try it with the key including the leading and closing '_'
		if(replacementStr == null) {
			replacementStr = resolveXpathInfos.get(replaceStr);
		}

		//if a replacement string is found and it is not an empty string
		if(StringUtils.isNotEmpty(replacementStr)) {
			//case: [id='1234'] ==> [@id='1234']
			if(replacementStr.contains("=") && !(replacementStr.charAt(0) == '@')) {
				replacementStr = "@" + replacementStr;
			}
			//case: [id] ==> [@id]
			else if(!replacementStr.contains("=") && !(replacementStr.charAt(0) == '@') && replacementStr.matches("[a-zA-Z]+") &&
					strPrefix.equals("[") && strPostfix.equals("]")) {
				replacementStr = "@" + replacementStr;
			}
			xpath = xpath.replaceAll(replaceStr, replacementStr);
		}
		//if a replacement string is found (_arid_, _arname_) but it is an empty string ("")
		// case 1: abc[_arid]/def ==> abc/def
		// case 2: abc/text() = '_arname_' ==> abc/contains(., '')
		else if(replacementStr != null && replacementStr.length() == 0) {
			//case 1
			if(matcher.group(1).equals("[") && matcher.group(3).equals("]")) {
				int idxOfReplStr = xpath.indexOf(replaceStr);
				xpath = xpath.substring(0, idxOfReplStr-1) + xpath.substring(idxOfReplStr+replaceStr.length()+1, xpath.length());
			}
			//case 2
			else if(matcher.group(1).equals("'") && matcher.group(3).equals("'")) {
				int idxOfReplStr = xpath.indexOf(replaceStr);
				int idxOfPrevSlash = xpath.lastIndexOf('/', idxOfReplStr)+1;
				int idxOfReplStrEnd = idxOfReplStr + replaceStr.length()+1;
				StringBuilder bld = new StringBuilder(xpath);
				bld.replace(idxOfPrevSlash, idxOfReplStrEnd, "contains(., '')");
				xpath = bld.toString();
			}
		}
		//if no replacement string is found
		else {
			log.warn("cannot find entry for '{}' in the resolveXpathInfos HashMap!", replaceStr);
		}
	}
	log.info("xpath='{}'", xpath);
	
	return xpath;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static String removePredicatesFromXpath(String xpath) {
	return xpath.replaceAll("(\\[.*?\\])", "");
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> getNodeTextByXPath(String xmlString, String xpathString) throws Exception {
	List<String> retList = new ArrayList<>();
	
	if(xmlString.length() == 0) {
		log.warn("cannot resolve node text from an empty xml");
		return retList;
	}
	final InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document xmlDoc = dBuilder.parse(is);

	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	if(xpathString.length() == 0) {
		log.warn("cannot resolve node text from an empty xpath");
		return retList;
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
	
	if(nl != null && nl.getLength() == 0) {
		log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
		return retList;
	}

	if(nl != null) {
        for (int i = 0; i < nl.getLength(); i++) {
            retList.add(nl.item(i).getNodeValue());
        }
    }
	
	return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> getNodeByXPath(String xmlString, String xpathString, String childNodeNames) throws Exception {
	List<String> childNodeNameList = Arrays.asList(childNodeNames.split(","));
	List<String> retList = new ArrayList<>();
	
	if(xmlString.length() == 0) {
		log.warn("cannot resolve node text from an empty xml");
		return retList;
	}
	final InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document xmlDoc = dBuilder.parse(is);

	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	if(xpathString.length() == 0) {
		log.warn("cannot resolve node text from an empty xpath");
		return retList;
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
                    childNodeValue = childNode.getTextContent();
                    if (StringUtils.isNotEmpty(childStr)) {
                        childStr = childStr + "," + childNodeName + "=" + childNodeValue;
                    } else {
                        childStr = childNodeName + "=" + childNodeValue;
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
public static List<String> getNodeAttributeTextByXPath(String xmlString, String xpathString, String attrName) throws Exception {
	List<String> retList = new ArrayList<>();
	if(xmlString.length() == 0) {
		log.warn("cannot resolve node text from an empty xml");
		return retList;
	}
	final InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document xmlDoc = dBuilder.parse(is);
	
	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	
	if(xpathString.length() == 0) {
		log.warn("attribute name {} resolved from an empty xpath", attrName);
		return retList;
	}
	
	log.trace("get attribute '{}' on xpath='{}'", attrName, xpathString);
	
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
	
	if(nl != null && nl.getLength() == 0) {
		log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
		return retList;
	}

	if(nl != null) {
		for (int i = 0; i < nl.getLength(); i++) {
			Attr attr = (Attr) nl.item(i);
			String name = attr.getName();
			String value = attr.getValue();
			retList.add(name + "=" + value);
		}
	}
	
	return retList;
}	

//-----------------------------------------------------------------------------

}