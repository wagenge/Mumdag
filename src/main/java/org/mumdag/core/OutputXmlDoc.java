package org.mumdag.core;

//-----------------------------------------------------------------------------

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.MapListUtils;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
//import org.mumdag.utils.XpathString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//-----------------------------------------------------------------------------

public class OutputXmlDoc {

//=============================================================================	
/*
* 	CLASS ATTRIBUTES (private)
*/	
private static final Logger log = LogManager.getLogger(OutputXmlDoc.class);

private Document outputXmlDoc = null;
private Document templateXmlDoc = null;
private String templateFilePath = "";
private String scraperId;
private String mappingRulesFilePath;
private String mappingRulesType;
private MappingRules mappingRules;

public enum NodeStatus 	{ 	A_EMPTY_NODE_EXISTS,
							NO_EMPTY_NODE_EXISTS,
							NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS,
							NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS,
							NODE_WITH_SAME_CONTENT_EXISTS,
							SAME_NODE_EXISTS,
							UNKOWN
						}
public enum NodeAction 	{ 	UPDATE,
							REPLACE, 
							ADD,
							DO_NOTHING
						}


//=============================================================================	
/*
* 	CONSTRUCTOR METHODS (public)
*/		
	
// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			ok
public OutputXmlDoc(String templateFilePath) throws Exception {
	Path path = Paths.get(templateFilePath);
	if(Files.exists(path) && Files.isRegularFile(path)) {
		this.templateFilePath = templateFilePath;
	}
	else {
		if(!Files.exists(path))
			throw new FileNotFoundException("Template file '" + templateFilePath + "' not found!");
		else if(!Files.isRegularFile(path)) 
			throw new FileNotFoundException("Template file '" + templateFilePath + "' is not a regular file!");
	}
	
	this.mappingRules = MappingRules.getInstance();
	this.scraperId = PropertyHandler.getInstance().getValue("Mumdag.scraperId");
	this.mappingRulesFilePath = PropertyHandler.getInstance().getValue("Mumdag.mappingRulesFilePath");
	this.mappingRulesType = PropertyHandler.getInstance().getValue("Mumdag.mappingRulesType");
	this.mappingRules.updateMappingRules(this.mappingRulesFilePath, this.scraperId, this.mappingRulesType);
}
	

//=============================================================================	
/*
* 	METHODS FOR WRITING/READING ARTIST BLOCK (public)
*/	
/*
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
//PARAMTRISATION:	nok
public NodeStatus writeArtistUniqueId(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	String artistUniqueIdXpathString = "";
	String artistUniqueIdXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.artistUniqueId");
	String artistXpathString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.artist"), "_arid_::");
	String sourceAttrName = elementValues.get("sourceAttrName");
	String sourceAttrValue = elementValues.get("sourceAttrValue");
	String aridAttrName = elementValues.get("aridAttrName");
	String aridValue = elementValues.get("aridValue");
	String artistUrlString = elementValues.get("artistUrlString");
	String artistWSUrlString = elementValues.get("artistWSUrlString");

	
	// check if necessary values are set
	if(aridValue == null || aridValue.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist id attribute value is missing (variable name 'aridValue')");
		return nodeState;		
	}
	if(aridAttrName == null || aridAttrName.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist id attribute name is missing (variable name 'aridAttrName')");
		return nodeState;		
	}	
	if(sourceAttrValue == null || sourceAttrValue.length() == 0) {
		log.warn("Could not write Artist UniqueId due to source attribute value is missing (variable name 'sourceAttrValue')");
		return nodeState;		
	}
	if(sourceAttrName == null || sourceAttrName.length() == 0) {
		log.warn("Could not write Artist UniqueId due to source attribute name is missing (variable name 'sourceAttrName')");
		return nodeState;		
	}	
	if(artistUrlString == null || artistUrlString.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist url is missing (variable name 'artistUrlString')");
		return nodeState;		
	}	
	if(artistWSUrlString == null || artistWSUrlString.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist webservice url is missing (variable name 'artistWSUrlString')");
		return nodeState;		
	}	
	if(artistUniqueIdXpathBaseString.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist unique id xpath string is empty (property name 'OutputXmlDoc.xpath.artistUniqueId')");
		return nodeState;		
	}
	if(artistXpathString.length() == 0) {
		log.warn("Could not write Artist UniqueId due to artist xpath string is empty (property name 'OutputXmlDoc.xpath.artist')");
		return nodeState;		
	}
	
	// prepare the xpath strings for checking the node status
	String aridNodeEmptyXpathCheckString = XpathString.resolve(artistUniqueIdXpathBaseString, 
			"_arid_::", "_unid_::not(@*)");
	String aridNodeSameAttrValueXpathCheckString = XpathString.resolve(artistUniqueIdXpathBaseString, 
				"_arid_::@"+aridAttrName+"='"+aridValue+"'", "_unid_::@"+aridAttrName+"='"+aridValue+"'");
	String aridNodeSameAttrNameXpathCheckString = XpathString.resolve(artistUniqueIdXpathBaseString,
			"_arid_::@"+aridAttrName, "_unid_::@"+aridAttrName);

	if(getNumberOfNodes(aridNodeSameAttrValueXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		artistUniqueIdXpathString = XpathString.resolve(artistUniqueIdXpathBaseString, "_arid_::", "_unid_::last()");
		log.trace("Artist Unique Id already exists {}={}", aridAttrName, aridValue); 
	}
	else if(getNumberOfNodes(aridNodeSameAttrNameXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		artistUniqueIdXpathString = XpathString.resolve(artistUniqueIdXpathBaseString, "_arid_::", "_unid_::last()");
		log.trace("Artist Unique Id already exists for {}", aridAttrName); 
	}
	else if(getNumberOfNodes(aridNodeEmptyXpathCheckString, true) == 1) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		artistUniqueIdXpathString = XpathString.resolve(artistUniqueIdXpathBaseString, "_arid_::", "_unid_::last()");
		log.trace("Artist Unique Id does not exists for {}, an empty unique id node exists", aridAttrName);
	}
	else if(getNumberOfNodes(aridNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		artistUniqueIdXpathString = XpathString.resolve(artistUniqueIdXpathBaseString, "_arid_::", "_unid_::last()");
		log.trace("Artist Unique Id does not exists for {}, and no empty unique id node exists", aridAttrName);
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node /Artist/UniqueIdList/UniqueId[last()]
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(artistUniqueIdXpathString+"/Value", aridValue));
		nodesText.add(new SimpleEntry<String,String>(artistUniqueIdXpathString+"/SourceUrl", artistUrlString));		
		nodesText.add(new SimpleEntry<String,String>(artistUniqueIdXpathString+"/SourceWSUrl", artistWSUrlString));
		
		//prepare information (nodes attributes) for updating the node /Artist/UniqueIdList/UniqueId[last()]
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(artistXpathString, aridAttrName + "=" + aridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(artistUniqueIdXpathString, aridAttrName + "=" + aridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(artistUniqueIdXpathString, sourceAttrName + "=" + sourceAttrValue));
		
		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		else if(whatToDo == NodeAction.ADD) { 
			addNodes(artistUniqueIdXpathString, artistUniqueIdXpathString+"/..", nodesText, nodesAttributes, true);
		}
	}
	return nodeState;
}	
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getArtistWSURL(String aridAttrName) throws Exception {
	String artistWSUrlXpathString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.artistUniqueId"), "_arid_::", "_unid_::@"+aridAttrName)
									+ "/" +	PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.realtive.sourceWSUrl");
	log.info("artistWSUrlXpathString='{}'", artistWSUrlXpathString);
	return getNodeTextByXPath(artistWSUrlXpathString);
}
*/

//=============================================================================	
/*
* 	METHODS FOR WRITING/READING RELEASE GROUP BLOCK (public)
*/	
/*
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
//PARAMTRISATION:	nok
public NodeStatus writeReleaseGroupUniqueId(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	String releaseGroupUniqueIdXpathString = "";
	String releaseGroupUniqueIdXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.releaseGroupUniqueId");
	String releaseGroupXpathString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.releaseGroup"), "_arid_::", "_rgid_::last()");	
	String sourceAttrName = elementValues.get("sourceAttrName");
	String sourceAttrValue = elementValues.get("sourceAttrValue");
	String aridValue = elementValues.get("aridValue");
	String rgidAttrName = elementValues.get("rgidAttrName");
	String rgidValue = elementValues.get("rgidValue");
	String releaseGroupUrlString = elementValues.get("releaseGroupUrlString");
	String releaseGroupWSUrlString = elementValues.get("releaseGroupWSUrlString");

	
	// check if necessary values are set
	if(aridValue == null || aridValue.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to artist id attribute value is missing (variable name 'aridValue')");
		return nodeState;		
	}
	if(sourceAttrValue == null || sourceAttrValue.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to source attribute value is missing (variable name 'sourceAttrValue')");
		return nodeState;		
	}
	if(sourceAttrName == null || sourceAttrName.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to source attribute name is missing (variable name 'sourceAttrName')");
		return nodeState;		
	}
	if(rgidValue == null || rgidValue.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group id attribute value is missing (variable name 'rgidValue')");
		return nodeState;		
	}
	if(rgidAttrName == null || rgidAttrName.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group id attribute name is missing (variable name 'rgidAttrName')");
		return nodeState;		
	}	
	if(releaseGroupUrlString == null || releaseGroupUrlString.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group url is missing (variable name 'releaseGroupUrlString')");
		return nodeState;		
	}	
	if(releaseGroupWSUrlString == null || releaseGroupWSUrlString.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group webservice url is missing (variable name 'releaseGroupWSUrlString')");
		return nodeState;		
	}	
	if(releaseGroupUniqueIdXpathBaseString.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group unique id xpath string is empty (property name 'OutputXmlDoc.xpath.releaseGroupUniqueId')");
		return nodeState;		
	}
	if(releaseGroupXpathString.length() == 0) {
		log.warn("Could not write ReleaseGroup UniqueId due to release group xpath string is empty (property name 'OutputXmlDoc.xpath.releaseGroup')");
		return nodeState;		
	}
	
	log.trace("add release group {} for artist {} ...", rgidValue, aridValue);
	
	// prepare the xpath strings for checking the node status
	String rgidNodeEmptyXpathCheckString = resolveXpath(releaseGroupUniqueIdXpathBaseString, 
			"_arid_::", "_rgid_::not(@*)", "_unid_::not(@*)");	
	String rgidNodeSameAttrValueXpathCheckString = resolveXpath(releaseGroupUniqueIdXpathBaseString, 
				"_arid_::@"+rgidAttrName+"='"+aridValue+"'", "_rgid_::@"+rgidAttrName+"='"+rgidValue+"'", "_unid_::@"+rgidAttrName+"='"+rgidValue+"'");
	String rgidNodeSameAttrNameXpathCheckString = resolveXpath(releaseGroupUniqueIdXpathBaseString,
			"_arid_::@"+rgidAttrName, "_rgid_::@"+rgidAttrName, "_unid_::@"+rgidAttrName);

	if(getNumberOfNodes(rgidNodeSameAttrValueXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseGroupUniqueIdXpathString = resolveXpath(releaseGroupUniqueIdXpathBaseString, "_arid_::", "_rgid_::last()", "_unid_::last()");		
		log.trace("ReleaseGroup Unique Id already exists {}={}", rgidAttrName, aridValue); 
	}
	else if(getNumberOfNodes(rgidNodeSameAttrNameXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseGroupUniqueIdXpathString = resolveXpath(releaseGroupUniqueIdXpathBaseString, "_arid_::", "_rgid_::last()", "_unid_::last()");
		log.trace("ReleaseGroup Unique Id already exists for {}", rgidAttrName); 
	}
	else if(getNumberOfNodes(rgidNodeEmptyXpathCheckString, true) == 1) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseGroupUniqueIdXpathString = resolveXpath(releaseGroupUniqueIdXpathBaseString, "_arid_::", "_rgid_::last()", "_unid_::last()");
		log.trace("ReleaseGroup Unique Id does not exists for {}, an empty unique id node exists", rgidAttrName);
	}
	else if(getNumberOfNodes(rgidNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseGroupUniqueIdXpathString = resolveXpath(releaseGroupUniqueIdXpathBaseString, "_arid_::", "_rgid_::last()", "_unid_::last()");
		log.trace("ReleaseGroup Unique Id does not exists for {}, and no empty unique id node exists", rgidAttrName);
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node /Artist/Discography/ReleaseGroupList/ReleaseGroup/UniqueIdList/UniqueId
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(releaseGroupUniqueIdXpathString+"/Value", rgidValue));
		nodesText.add(new SimpleEntry<String,String>(releaseGroupUniqueIdXpathString+"/SourceUrl", releaseGroupUrlString));		
		nodesText.add(new SimpleEntry<String,String>(releaseGroupUniqueIdXpathString+"/SourceWSUrl", releaseGroupWSUrlString));
		
		//prepare information (nodes attributes) for updating the node /Artist/Discography/ReleaseGroupList/ReleaseGroup/UniqueIdList/UniqueId
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(releaseGroupXpathString, rgidAttrName + "=" + rgidValue));
		nodesAttributes.add(new SimpleEntry<String,String>(releaseGroupUniqueIdXpathString, rgidAttrName + "=" + rgidValue));
		nodesAttributes.add(new SimpleEntry<String,String>(releaseGroupUniqueIdXpathString, sourceAttrName + "=" + sourceAttrValue));
		
		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		else if(whatToDo == NodeAction.ADD) { 
			addNodes(releaseGroupXpathString, releaseGroupXpathString+"/..", nodesText, nodesAttributes, true);
		}
	}
	return nodeState;
}		
*/
//-----------------------------------------------------------------------------


//=============================================================================	
/*
* 	METHODS FOR WRITING/READING RELEASE BLOCK (public)
*/	
/*
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
//PARAMTRISATION:	nok
public NodeStatus writeReleaseUniqueId(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	String releaseUniqueIdXpathString = "";
	String releaseUniqueIdXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.releaseUniqueId");
	String releaseXpathSourceString =  XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.release"), "_arid_::", "_rgid_::last()", "_rid_::last()");
	String sourceAttrName = elementValues.get("sourceAttrName");
	String sourceAttrValue = elementValues.get("sourceAttrValue");
	String aridValue = elementValues.get("aridValue");
	String rgidValue = elementValues.get("rgidValue");
	String ridValue = elementValues.get("ridValue");
	String ridAttrName = elementValues.get("ridAttrName");
	String releaseUrlString = elementValues.get("releaseUrlString");
	String releaseWSUrlString = elementValues.get("releaseWSUrlString");
	String releaseXpathTargetString =  XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.release"), "_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::last()");	

	log.trace("add release {} for release group {} for artist {} ...", ridValue, rgidValue, aridValue);
	
	// check if necessary values are set
	if(aridValue == null || aridValue.length() == 0) {
		log.warn("Could not write Release UniqueId due to artist id attribute value is missing (variable name 'aridValue')");
		return nodeState;		
	}
	if(rgidValue == null || rgidValue.length() == 0) {
		log.warn("Could not write Release UniqueId due to release group id attribute value is missing (variable name 'rgidValue')");
		return nodeState;		
	}	
	if(sourceAttrValue == null || sourceAttrValue.length() == 0) {
		log.warn("Could not write Release UniqueId due to source attribute value is missing (variable name 'sourceAttrValue')");
		return nodeState;		
	}
	if(sourceAttrName == null || sourceAttrName.length() == 0) {
		log.warn("Could not write Release UniqueId due to source attribute name is missing (variable name 'sourceAttrName')");
		return nodeState;		
	}
	if(ridValue == null || ridValue.length() == 0) {
		log.warn("Could not write Release UniqueId due to release id attribute value is missing (variable name 'ridValue')");
		return nodeState;		
	}	
	if(ridAttrName == null || ridAttrName.length() == 0) {
		log.warn("Could not write Release UniqueId due to release id attribute name is missing (variable name 'ridAttrName')");
		return nodeState;		
	}	
	if(releaseUrlString == null || releaseUrlString.length() == 0) {
		log.warn("Could not write Release UniqueId due to release url is missing (variable name 'releaseUrlString')");
		return nodeState;		
	}	
	if(releaseWSUrlString == null || releaseWSUrlString.length() == 0) {
		log.warn("Could not write Release UniqueId due to release webservice url is missing (variable name 'releaseWSUrlString')");
		return nodeState;		
	}	
	if(releaseUniqueIdXpathBaseString.length() == 0) {
		log.warn("Could not write Release UniqueId due to release unique id xpath string is empty (property name 'OutputXmlDoc.xpath.releaseUniqueId')");
		return nodeState;		
	}
	if(releaseXpathSourceString.length() == 0 && releaseXpathTargetString.length() == 0) {
		log.warn("Could not write Release UniqueId due to release xpath string is empty (property name 'OutputXmlDoc.xpath.release')");
		return nodeState;		
	}
		
	// prepare the xpath strings for checking the node status
	String ridNodeEmptyXpathCheckString = XpathString.resolve(releaseUniqueIdXpathBaseString, 
			"_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::not(@*)", "_unid_::not(@*)");
	String ridNodeSameAttrValueXpathCheckString = XpathString.resolve(releaseUniqueIdXpathBaseString, 
				"_arid_::@"+ridAttrName+"='"+aridValue+"'", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", 
				"_rid_::@"+ridAttrName+"='"+ridValue+"'", "_unid_::@"+ridAttrName+"='"+ridValue+"'");
	String ridNodeSameAttrNameXpathCheckString = XpathString.resolve(releaseUniqueIdXpathBaseString,
			"_arid_::@"+ridAttrName, "_rgid_::@"+ridAttrName, "_rid_::@"+ridAttrName, "_unid_::@"+ridAttrName);
	
	if(getNumberOfNodes(ridNodeSameAttrValueXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseUniqueIdXpathString = XpathString.resolve(releaseUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::last()", "_unid_::last()");		
		log.trace("Release Unique Id already exists {}={}", ridAttrName, aridValue); 
	}
	else if(getNumberOfNodes(ridNodeSameAttrNameXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseUniqueIdXpathString = XpathString.resolve(releaseUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::last()", "_unid_::last()");
		log.trace("Release Unique Id already exists for {}", ridAttrName); 
	}
	else if(getNumberOfNodes(ridNodeEmptyXpathCheckString, true) == 1) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseUniqueIdXpathString = XpathString.resolve(releaseUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::last()", "_unid_::last()");
		log.trace("Release Unique Id does not exists for {}, an empty unique id node exists", ridAttrName);
	}
	else if(getNumberOfNodes(ridNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		releaseUniqueIdXpathString = XpathString.resolve(releaseUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+ridAttrName+"='"+rgidValue+"'", "_rid_::last()", "_unid_::last()");
		log.trace("Release Unique Id does not exists for {}, and no empty unique id node exists", ridAttrName);
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/UniqueIdList/UniqueId
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(releaseUniqueIdXpathString+"/Value", ridValue));
		nodesText.add(new SimpleEntry<String,String>(releaseUniqueIdXpathString+"/SourceUrl", releaseUrlString));	
		nodesText.add(new SimpleEntry<String,String>(releaseUniqueIdXpathString+"/SourceWSUrl", releaseWSUrlString));
		
		//prepare information (nodes attributes) for updating the node /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/UniqueIdList/UniqueId
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(releaseXpathTargetString, ridAttrName + "=" + ridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(releaseUniqueIdXpathString, ridAttrName + "=" + ridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(releaseUniqueIdXpathString, sourceAttrName + "=" + sourceAttrValue));
		
		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		else if(whatToDo == NodeAction.ADD) { 
			addNodes(releaseXpathSourceString, releaseXpathTargetString+"/..", nodesText, nodesAttributes, true);
		}
	}
	return nodeState;
}	
*/
//-----------------------------------------------------------------------------


//=============================================================================	
/*
* 	METHODS FOR WRITING/READING MEDIUM BLOCK (public)
*/	
/*
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public void writeMediumDiscNo(HashMap<String,String> elementValues) throws Exception {
	List<Entry<String,String>> nodesAttributes = new ArrayList<>();

	int discNo = Integer.parseInt(elementValues.get("discNo"));
	String aridValue = elementValues.get("aridValue");
	String rgidValue = elementValues.get("rgidValue");
	String ridValue = elementValues.get("ridValue");
	String mbidAttrName = elementValues.get("mbidAttrName");
	String mediumDiscNoAttrName = elementValues.get("mediumDiscNoAttrName");
	String mediumXpathSourceString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::last()", "_rid_::last()", "_mid_::last()");
	String mediumXpathTargetString = "";	
	String mediumXpathCheckString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::@"+mbidAttrName+"='"+rgidValue+"'", "_rid_::@"+mbidAttrName+"='"+ridValue+"'","_mid_::");

	log.trace("add/update medium node #{} for release {} for release group {} for artist {} ...", discNo, ridValue, rgidValue, aridValue);

	// check if necessary values are set
	if(aridValue == null || aridValue.length() == 0) {
		log.warn("Could not write Medium DiscNo due to artist id attribute value is missing (variable name 'aridValue')");
		return;		
	}
	if(rgidValue == null || rgidValue.length() == 0) {
		log.warn("Could not write Medium DiscNo due to release group id attribute value is missing (variable name 'rgidValue')");
		return;		
	}	
	if(ridValue == null || ridValue.length() == 0) {
		log.warn("Could not write Medium DiscNod due to release id attribute value is missing (variable name 'ridValue')");
		return;		
	}	
	if(mbidAttrName == null || mbidAttrName.length() == 0) {
		log.warn("Could not write Medium DiscNo due to MBID attribute name is missing (variable name 'mbidAttrName')");
		return;		
	}
	if(mediumDiscNoAttrName == null || mediumDiscNoAttrName.length() == 0) {
		log.warn("Could not write Medium DiscNo due to MediumDiscNo  attribute name is missing (variable name 'mediumDiscNoAttrName')");
		return;		
	}
	if(discNo < 1) {
		log.warn("Could not write Medium DiscNo due to discNo is smaller than 1 (variable name 'discNo')");
		return;		
	}
	if(mediumXpathSourceString.length() == 0 && mediumXpathTargetString.length() == 0) {
		log.warn("Could not write Medium DiscNo due to medium xpath string is empty (property name 'OutputXmlDoc.xpath.medium')");
		return;		
	}
	
	int numOfMediumNodes = getNumberOfNodes(mediumXpathCheckString, true);
	if(numOfMediumNodes == 0) {
		log.warn("Could not write Medium DiscNo due to no medium nodes existing at for '{}'", mediumXpathCheckString);
	}
	else if(numOfMediumNodes == discNo-1) {
		mediumXpathTargetString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::@"+mbidAttrName+"='"+rgidValue+"'", "_rid_::@"+mbidAttrName+"='"+ridValue+"'","_mid_::last()");	
		log.warn("#2: numOfMediumNodes='{}', mediumXpathCheckString='{}', mediumXpathTargetString='{}'", numOfMediumNodes, mediumXpathCheckString, mediumXpathTargetString);
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium
		nodesAttributes.add(new SimpleEntry<String,String>(mediumXpathTargetString, mediumDiscNoAttrName + "=" + discNo));
		addNodes(mediumXpathSourceString, mediumXpathTargetString+"/..", null, nodesAttributes, true);		
	}
	else if(numOfMediumNodes >= discNo) {
		mediumXpathTargetString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::@"+mbidAttrName+"='"+rgidValue+"'", "_rid_::@"+mbidAttrName+"='"+ridValue+"'","_mid_::"+discNo);
		log.warn("#1: numOfMediumNodes='{}', mediumXpathCheckString='{}', mediumXpathTargetString='{}'", numOfMediumNodes, mediumXpathCheckString, mediumXpathTargetString);
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium
		nodesAttributes.add(new SimpleEntry<String,String>(mediumXpathTargetString, mediumDiscNoAttrName + "=" + discNo));
		updateNodes(null, nodesAttributes, false);
	}
	else {
		log.warn("Could not write Medium DiscNo due to less medium nodes existing. expected minimal {}, only {} are existing", discNo-1, numOfMediumNodes);
	}
	return;
}
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public void writeAllMediumDiscNos(HashMap<String,String> elementValues) throws Exception {
	int discTotal = Integer.parseInt(elementValues.get("discTotal"));
	String mbidAttrName = elementValues.get("mbidAttrName");
	String mediumDiscNoAttrName = elementValues.get("mediumDiscNoAttrName");
	String aridValue = elementValues.get("aridValue");
	String rgidValue = elementValues.get("rgidValue");
	String ridValue = elementValues.get("ridValue");
	String mediumXpathSourceString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::last()", "_rid_::last()", "_mid_::last()");
	String mediumXpathTargetString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::@"+mbidAttrName+"='"+rgidValue+"'", "_rid_::@"+mbidAttrName+"='"+ridValue+"'","_mid_::last()");	

	
	log.trace("add #{}  medium nodes for release {} for release group {} for artist {} ...", discTotal, ridValue, rgidValue, aridValue);
	
	// check if necessary values are set
	if(aridValue == null || aridValue.length() == 0) {
		log.warn("Could not write all Medium DiscNo due to artist id attribute value is missing (variable name 'aridValue')");
		return;		
	}
	if(rgidValue == null || rgidValue.length() == 0) {
		log.warn("Could not write all Medium DiscNo due to release group id attribute value is missing (variable name 'rgidValue')");
		return;		
	}	
	if(ridValue == null || ridValue.length() == 0) {
		log.warn("Could not write all Medium DiscNod due to release id attribute value is missing (variable name 'ridValue')");
		return;		
	}	
	if(mbidAttrName == null || mbidAttrName.length() == 0) {
		log.warn("Could not write all Medium DiscNo due to MBID attribute name is missing (variable name 'mbidAttrName')");
		return;		
	}
	if(mediumDiscNoAttrName == null || mediumDiscNoAttrName.length() == 0) {
		log.warn("Could not write all Medium DiscNo due to MediumDiscNo  attribute name is missing (variable name 'mediumDiscNoAttrName')");
		return;		
	}
	if(discTotal < 1) {
		log.warn("Could not write all Medium DiscNo due to discNo is smaller than 1 (variable name 'discTotal')");
		return;		
	}
	if(mediumXpathSourceString.length() == 0 && mediumXpathTargetString.length() == 0) {
		log.warn("Could not write Medium DiscNo due to medium xpath string is empty (property name 'OutputXmlDoc.xpath.medium')");
		return;		
	}
		
	for(int discNo = 1; discNo <= discTotal; discNo++) {
		String mediumXpathCheckString = XpathString.resolve(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.medium"), "_arid_::", "_rgid_::@"+mbidAttrName+"='"+rgidValue+"'", "_rid_::@"+mbidAttrName+"='"+ridValue+"'","_mid_::"+discNo);	
	
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(mediumXpathCheckString, mediumDiscNoAttrName + "=" + discNo));

		if(getNumberOfNodes(mediumXpathCheckString, true) == 1) {
			log.info("UPDATE");
			updateNodes(null, nodesAttributes, false);
		}
		else {
			log.info("ADD");
			addNodes(mediumXpathSourceString, mediumXpathTargetString+"/..", null, nodesAttributes, true);
		}
	}
	return;
}
*/
//-----------------------------------------------------------------------------

//=============================================================================	
/*
* 	METHODS FOR WRITING/READING TRACK BLOCK (public)
*/	
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMTRISATION:	nok
public NodeStatus writeTrackUniqueId(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	String trackUniqueIdXpathString = "";
	String sourceAttrName = elementValues.get("sourceAttrName");
	String sourceAttrValue = elementValues.get("sourceAttrValue");
	String aridValue = elementValues.get("aridValue");
	String rgidValue = elementValues.get("rgidValue");
	String ridValue = elementValues.get("ridValue");
	String tridValue = elementValues.get("tridValue");
	String tridAttrName = elementValues.get("tridAttrName");
	String discNo = elementValues.get("discNo");
	String discTotal = elementValues.get("discTotal");
	String trackNo = elementValues.get("trackNo");
	String trackTotal = elementValues.get("trackTotal");
	String trackPosAttrName = elementValues.get("trackPosAttrName");
	String trackUrlString = elementValues.get("trackUrlString");
	String trackWSUrlString = elementValues.get("trackWSUrlString");
	String trackUniqueIdXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.trackUniqueId");
	
	log.trace("add track {} for release group {} for release {} for artist {} ...", tridValue, ridValue, rgidValue, aridValue);
	
	// check if necessary values are set
	if(tridValue.length() == 0 || tridAttrName.length() == 0 ||
		sourceAttrValue.length() == 0 || sourceAttrName.length() == 0 ||
		aridValue.length() == 0 || rgidValue.length() == 0 || ridValue.length() == 0 ||
		discNo.length() == 0 || discTotal.length() == 0 ||
		trackNo.length() == 0 || trackTotal.length() == 0 || trackPosAttrName.length() == 0 ||
		trackUrlString.length() == 0 || trackWSUrlString.length() == 0 ||
		trackUniqueIdXpathBaseString.length() == 0) {
		log.warn("Could not write Track UniqueId due to some missing elementValues");
		return nodeState;
	}
	
	// prepare the xpath strings for checking the node status
	String tridNodeEmptyXpathCheckString = resolveXpath(trackUniqueIdXpathBaseString, 
			"_arid_::", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", "_rid_::@"+tridAttrName+"='"+ridValue+"'", 
			"_mid_::@discNo='"+discNo+"'", "_trid_::not(@*)", "_unid_::not(@*)");
	String tridNodeSameAttrValueXpathCheckString = resolveXpath(trackUniqueIdXpathBaseString, 
				"_arid_::@"+tridAttrName+"='"+aridValue+"'", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", 
				"_rid_::@"+tridAttrName+"='"+ridValue+"'", "_mid_::@discNo='"+discNo+"'", 
				"_trid_::@"+tridAttrName+"='"+tridValue+"'", "_unid_::@"+tridAttrName+"='"+tridValue+"'");
	String tridNodeSameAttrNameXpathCheckString = resolveXpath(trackUniqueIdXpathBaseString,
			"_arid_::@"+tridAttrName+"='"+aridValue+"'", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", 
			"_rid_::@"+tridAttrName+"='"+ridValue+"'", "_mid_::@discNo='"+discNo+"'", 
			"_trid_::@"+tridAttrName, "_unid_::@"+tridAttrName);

	if(getNumberOfNodes(tridNodeSameAttrValueXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.trace("Track Unique Id already exists {}={}", tridAttrName, tridValue); 
	}
	else if(getNumberOfNodes(tridNodeSameAttrNameXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackUniqueIdXpathString = resolveXpath(trackUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", 
				"_rid_::@"+tridAttrName+"='"+ridValue+"'", "_mid_::@discNo='"+discNo+"'", "_trid_::last()", "_unid_::last()");
		log.trace("Track Unique Id already exists for {}", trackUniqueIdXpathString); 
	}
	else if(getNumberOfNodes(tridNodeEmptyXpathCheckString, true) == 1) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackUniqueIdXpathString = resolveXpath(trackUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'",
				"_rid_::@"+tridAttrName+"='"+ridValue+"'", "_mid_::last()", "_trid_::last()", "_unid_::last()");
		log.trace("Track Unique Id does not exists for {}, an empty unique id node exists", tridAttrName);
	}
	else if(getNumberOfNodes(tridNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackUniqueIdXpathString = resolveXpath(trackUniqueIdXpathBaseString, "_arid_::", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", 
				"_rid_::@"+tridAttrName+"='"+ridValue+"'", "_mid_::last()", "_trid_::last()", "_unid_::last()");
		log.trace("Track Unique Id does not exists for {}, and no empty unique id node exists", tridAttrName);
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node 
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/UniqueIdList/UniqueId
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(trackUniqueIdXpathString+"/Value", ridValue));
		nodesText.add(new SimpleEntry<String,String>(trackUniqueIdXpathString+"/SourceUrl", trackUrlString));		
		nodesText.add(new SimpleEntry<String,String>(trackUniqueIdXpathString+"/SourceWSUrl", trackWSUrlString));
		nodesText.add(new SimpleEntry<String,String>(trackUniqueIdXpathString+"/../../Position", trackNo));
		
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/UniqueIdList/UniqueId
		String trackXpathSourceString = resolveXpath(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.track"), 
				"_arid_::", "_rgid_::last()", "_rid_::last()", "_mid_::last()", "_trid_::last()");
		String trackXpathTargetString = resolveXpath(PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.track"), 
				"_arid_::", "_rgid_::@"+tridAttrName+"='"+rgidValue+"'", "_rid_::@"+tridAttrName+"='"+ridValue+"'","_mid_::@discNo='"+discNo+"'", "_trid_::last()");
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(trackXpathTargetString, tridAttrName + "=" + tridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(trackXpathTargetString, trackPosAttrName + "=" + trackNo));
		nodesAttributes.add(new SimpleEntry<String,String>(trackUniqueIdXpathString, tridAttrName + "=" + tridValue));
		nodesAttributes.add(new SimpleEntry<String,String>(trackUniqueIdXpathString, sourceAttrName + "=" + sourceAttrValue));
		
		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		else if(whatToDo == NodeAction.ADD) { 
			addNodes(trackXpathSourceString, trackXpathTargetString+"/..", nodesText, nodesAttributes, true);
		}
	}
	return nodeState;
}	
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public NodeStatus writeTrackTag(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;

	String tagName = elementValues.get("tagName");
	String tagValue = elementValues.get("tagValue");
	String trackFieldName = elementValues.get("trackFieldName");
	String trackFieldAttrName = elementValues.get("trackFieldAttrName");
	String mbTrackId = elementValues.get("mbTrackId");
	String mbIdAttrName = elementValues.get("mbIdAttrName");
	String trackTagAttrName = elementValues.get("trackTagAttrName");
	String sourceAttrName = elementValues.get("sourceAttrName");
	String sourceAttrValue = elementValues.get("sourceAttrValue");
	String trackTagXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.trackTag");
	String trackTagXpathString = "";
	
	log.trace("add tag '{}={}' for track {} ...", tagName, tagValue, mbTrackId);
	
	// check if necessary values are set
	if(tagName.length() == 0 || tagValue.length() == 0 ||
		mbTrackId.length() == 0 || mbIdAttrName.length() == 0 ||
		trackTagAttrName.length() == 0 ||
		trackFieldName.length() == 0 || trackFieldAttrName.length() == 0 ||
		sourceAttrValue.length() == 0 || sourceAttrName.length() == 0 || 
		trackTagXpathBaseString.length() == 0) {
		log.warn("Could not write Track Tag due to some missing elementValues");
		return nodeState;
	}
	
	// prepare the xpath strings for checking the node status
	String trackTagNodeEmptyXpathCheckString = resolveXpath(trackTagXpathBaseString, 
			"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::not(@*)");
	String trackTagNodeSameAttrValueXpathCheckString = resolveXpath(trackTagXpathBaseString, 
			"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::@"+trackTagAttrName+"='"+tagName+"'");
	String trackTagNodeSameAttrNameXpathCheckString = resolveXpath(trackTagXpathBaseString,
			"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::@"+trackTagAttrName);
	//String trackTagNodeSameContentXpathCheckString = resolveXpath(trackTagXpathBaseString,
	//		"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", 
	//		"_tagAttr_::@"+trackTagAttrName+" and text()='"+tagValue+"'");
	
	if(getNumberOfNodes(trackTagNodeSameAttrValueXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackTagXpathString = resolveXpath(trackTagXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::last()");
		log.trace("Track Tag '{}={}' already exists for track {}", trackTagAttrName, tagName, mbTrackId); 
	}
	else if(getNumberOfNodes(trackTagNodeSameAttrNameXpathCheckString, true) == 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackTagXpathString = resolveXpath(trackTagXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::last()");
		log.trace("Track Tag '{}={}' already exists for track {}", trackTagAttrName, tagName, mbTrackId); 
	}
	else if(getNumberOfNodes(trackTagNodeEmptyXpathCheckString, true) == 1) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackTagXpathString = resolveXpath(trackTagXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::last()");;
		log.trace("Track Tag '{}={}' does not exists for track {}, an empty tag node exists", trackTagAttrName, tagName, mbTrackId);
	}
	else if(getNumberOfNodes(trackTagNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackTagXpathString = resolveXpath(trackTagXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::last()");
		log.trace("Track Tag '{}={}' does not exists for track {}, and no tag node exists", trackTagAttrName, tagName, mbTrackId);
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node 
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/TagList/Tag
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(trackTagXpathString, tagValue));
		
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/TagList/Tag
		String trackTagXpathSourceString = resolveXpath(trackTagXpathBaseString, 
				"_arid_::", "_rgid_::last()", "_rid_::last()", "_mid_::last()", "_trid_::last()", "_tagAttr_::last()");
		String trackTagXpathTargetString = resolveXpath(trackTagXpathBaseString, 
				"_arid_::", "_rgid_::", "_rid_::","_mid_::", "_trid_::@"+mbIdAttrName+"='"+mbTrackId+"'", "_tagAttr_::last()");
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(trackTagXpathTargetString, sourceAttrName + "=" + sourceAttrValue));
		nodesAttributes.add(new SimpleEntry<String,String>(trackTagXpathTargetString, trackTagAttrName + "=" + tagName));
		nodesAttributes.add(new SimpleEntry<String,String>(trackTagXpathTargetString, trackFieldAttrName + "=" + trackFieldName));

		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		else if(whatToDo == NodeAction.ADD) { 
			addNodes(trackTagXpathSourceString, trackTagXpathTargetString+"/..", nodesText, nodesAttributes, true);
		}
	}		
	return nodeState;	
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMTRISATION:	nok
public NodeStatus writeTrackFileInfo(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	
	String tridValue = elementValues.get("tridValue");
	String tridAttrName = elementValues.get("tridAttrName");
	String trackFileName = elementValues.get("trackFileName");
	String trackFileExtension = elementValues.get("trackFileExtension");
	String trackFileSize = elementValues.get("trackFileSize");
	String trackCanonicalPath = elementValues.get("trackCanonicalPath");
	String trackFileInfoXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.trackFileInfo");
	String trackFileInfoXpathString = "";
	
	log.trace("add fileInfos (name={}, ext={}, size={}, path={}) for track {} ...", trackFileName, trackFileExtension, trackFileSize, trackCanonicalPath, tridValue);
	
	// check if necessary values are set
	if(tridValue.length() == 0 || tridAttrName.length() == 0 ||
		trackFileName.length() == 0 || trackFileExtension.length() == 0 ||
		trackFileSize.length() == 0 || trackCanonicalPath.length() == 0 ||
		trackFileInfoXpathBaseString.length() == 0) {
		log.warn("Could not write Track FileInfo due to some missing elementValues");
		return nodeState;
	}
	
	// prepare the xpath strings for checking the node status
	String trackFileInfoNodeEmptyXpathCheckString = resolveXpath(trackFileInfoXpathBaseString, 
			"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'") + "/text()";

	if(checkIfNodeAndSubNodesAreEmpty(trackFileInfoNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackFileInfoXpathString = resolveXpath(trackFileInfoXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'");
		log.trace("Track FileInfo is empty for track {}", tridValue); 
	}
	else if(checkIfNodeAndSubNodesAreEmpty(trackFileInfoNodeEmptyXpathCheckString, true) > 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackFileInfoXpathString = resolveXpath(trackFileInfoXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'");
		log.trace("Track FileInfo is already filled for track {}", tridValue); 		
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}

	if(whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node 
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/FileInfo
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(trackFileInfoXpathString+"/Extension", trackFileExtension));
		nodesText.add(new SimpleEntry<String,String>(trackFileInfoXpathString+"/Name", trackFileName));		
		nodesText.add(new SimpleEntry<String,String>(trackFileInfoXpathString+"/Size", trackFileSize));
		nodesText.add(new SimpleEntry<String,String>(trackFileInfoXpathString+"/Path", trackCanonicalPath));
		
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/FileInfo
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		
		// writing the values
		updateNodes(nodesText, nodesAttributes, false);
	}		
	return nodeState;	
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMTRISATION:	nok
public NodeStatus writeTrackAudioInfo(HashMap<String,String> elementValues, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	
	String tridValue = elementValues.get("tridValue");
	String tridAttrName = elementValues.get("tridAttrName");
	String BitRate = elementValues.get("BitRate");
	String IsVariableBitRate = elementValues.get("IsVariableBitRate");
	String BitsPerSample = elementValues.get("BitsPerSample");
	String Channels = elementValues.get("Channels");
	String Emphasis = elementValues.get("Emphasis");
	String Encoder = elementValues.get("Encoder");
	String IsLossless = elementValues.get("IsLossless");
	String EncodingType = elementValues.get("EncodingType");
	String MpegVersion = elementValues.get("MpegVersion");
	String MpegLayer = elementValues.get("MpegLayer");
	String TrackLengthSeconds = elementValues.get("TrackLength");
	Long TrackLengthMilliseconds = Math.round(Double.valueOf(elementValues.get("PreciseTrackLength")) * 1000);
	String TrackLengthMinutesSeconds = LocalTime.MIN.plusSeconds(Integer.valueOf(TrackLengthSeconds)).toString();
	String SampleRate = elementValues.get("SampleRate");
	String IsCopyrighted = elementValues.get("IsCopyrighted");
	String IsOriginal = elementValues.get("IsOriginal");
	String trackAudioInfoXpathBaseString = PropertyHandler.getInstance().getValue("OutputXmlDoc.xpath.trackAudioInfo");
	String trackAudioInfoXpathString = "";
	
	log.trace("add AudioInfos (u.a. BitRate={}, MpegVersion={}, MpegLayer={}, TrackLengthMinuteSeconds={}) for track {} ...", BitRate, MpegVersion, MpegLayer, TrackLengthMinutesSeconds, tridValue);
	
	// check if necessary values are set
	if(tridValue.length() == 0 || tridAttrName.length() == 0 ||
		trackAudioInfoXpathBaseString.length() == 0) {
		log.warn("Could not write Track AudioInfo due to some missing elementValues");
		return nodeState;
	}
	
	// prepare the xpath strings for checking the node status
	String trackAudioInfoNodeEmptyXpathCheckString = resolveXpath(trackAudioInfoXpathBaseString, 
			"_arid_::", "_rgid_::", "_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'") + "/text()";

	if(checkIfNodeAndSubNodesAreEmpty(trackAudioInfoNodeEmptyXpathCheckString, true) == 0) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackAudioInfoXpathString = resolveXpath(trackAudioInfoXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'");
		log.trace("Track AudioInfo is empty for track {}", tridValue); 
	}
	else if(checkIfNodeAndSubNodesAreEmpty(trackAudioInfoNodeEmptyXpathCheckString, true) > 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		trackAudioInfoXpathString = resolveXpath(trackAudioInfoXpathBaseString, "_arid_::", "_rgid_::", 
				"_rid_::", "_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'");
		log.trace("Track AudioInfo is already filled for track {}", tridValue); 		
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}

	if(whatToDo == NodeAction.UPDATE) {
		// prepare information (nodes text) for updating the node 
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/AudioInfo
		List<Entry<String,String>> nodesText = new ArrayList<>();
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/BitRate", BitRate));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/BitsPerSample", BitsPerSample));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Channels", Channels));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Emphasis", Emphasis));	
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Encoder", Encoder));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/EncodingType", EncodingType));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/MpegVersion", MpegVersion));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/MpegLayer", MpegLayer));	
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Length/Milliseconds", String.valueOf(TrackLengthMilliseconds)));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Length/Seconds", TrackLengthSeconds));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/Length/MinutesSeconds", TrackLengthMinutesSeconds));
		nodesText.add(new SimpleEntry<String,String>(trackAudioInfoXpathString+"/SampleRate", SampleRate));
		
		// prepare information (nodes attributes) for updating the node
		//  /Artist/Discography/ReleaseGroupList/ReleaseGroup/ReleaseList/Release/MediumList/Medium/TrackList/Track/AudioInfo
		String trackAudioInfoXpathTargetString = resolveXpath(trackAudioInfoXpathBaseString, 
				"_arid_::", "_rgid_::", "_rid_::","_mid_::", "_trid_::@"+tridAttrName+"='"+tridValue+"'");
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		nodesAttributes.add(new SimpleEntry<String,String>(trackAudioInfoXpathTargetString + "/BitRate", "variable" + "=" + IsVariableBitRate));
		nodesAttributes.add(new SimpleEntry<String,String>(trackAudioInfoXpathTargetString + "/BitRate", "lossless" + "=" + IsLossless));
		nodesAttributes.add(new SimpleEntry<String,String>(trackAudioInfoXpathTargetString + "/IntellectualProperty", "copyright" + "=" + IsCopyrighted));
		nodesAttributes.add(new SimpleEntry<String,String>(trackAudioInfoXpathTargetString + "/IntellectualProperty", "original" + "=" + IsOriginal));
		
		// writing the values
		updateNodes(nodesText, nodesAttributes, false);
	}		
	return nodeState;	
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeUniqueIdInfos(HashMap<String, String> uniqueIdInfos) throws Exception {
	String uniqueId = uniqueIdInfos.get("uniqueId");
	String url = uniqueIdInfos.get("url");
	String scraperId = uniqueIdInfos.get("scraperId");
	String copyBehaviorRule = uniqueIdInfos.get("copyBehaviorRule");
	String resolvedBaseXpath = uniqueIdInfos.get("resolvedBaseXpath");
	String propSection = uniqueIdInfos.get("propSection");
	String propSubSection = uniqueIdInfos.get("propSubSection");
	String placeholder = uniqueIdInfos.get("placeholder");
	
	Boolean foundProp = false;
	
	//retrieve Information for Scraper.Section (e.g., Discogs.Scraper.[Artist].xyz)
	// 	infos are 	.id, .sourceAttrName, .sourceAttrValue, .Base.url,
	//				.[Artist|ReleaseGroup|Release|Track].url, .[Artist|ReleaseGroup|Release|Track].urlParams
	String idAttrName = "";
	String sourceAttrName = "";
	String sourceAttrValue = "";
	String wsUrl = "";
	try {
		idAttrName = PropertyHandler.getInstance().getValue(propSection+".Scraper.idAttrName");
		sourceAttrName = PropertyHandler.getInstance().getValue(propSection+".Scraper.sourceAttrName");
		sourceAttrValue = PropertyHandler.getInstance().getValue(propSection+".Scraper.sourceAttrValue");
		if(!PropertyHandler.getInstance().containsKey(propSection+".Scraper.Base.wsUrl")) {
			wsUrl= "-";
		}
		else if(PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.wsUrl").equals("-")) {
			wsUrl = PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.wsUrl");
		}
		else {
			if(PropertyHandler.getInstance().containsKey(propSection+".Scraper."+propSubSection+".wsUrl")) {
				wsUrl = (PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.wsUrl") + 
						PropertyHandler.getInstance().getValue(propSection+".Scraper."+propSubSection+".wsUrl")).replaceAll(placeholder, uniqueId);				
			}
			else {
				wsUrl = (PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.wsUrl") + 
						PropertyHandler.getInstance().getValue(propSection+".Scraper."+propSubSection+".url")).replaceAll(placeholder, uniqueId);
			}
		}
		if(PropertyHandler.getInstance().containsKey(propSection+".Scraper."+propSubSection+".url") || 
				(PropertyHandler.getInstance().containsKey(propSection+".Scraper.Base.url") && !PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.url").equals("-"))) {
			url = (PropertyHandler.getInstance().getValue(propSection+".Scraper.Base.url") + 
				PropertyHandler.getInstance().getValue(propSection+".Scraper."+propSubSection+".url")).replaceAll(placeholder, uniqueId);
		}
		
		log.info("idAttrName={}, sourceAttrName={}, sourceAttrValue={}, wsUrl={}, url={}", idAttrName, sourceAttrName, sourceAttrValue, wsUrl, url);
		foundProp = true;
	} catch (Exception e) {
		log.warn("Could not write {} uniqueId for {}, {}", propSection, propSubSection, e.getMessage());
	}
	
	if(foundProp) {
		// define the behavior of the operation depending on the current node state (for artists' uniqueId and sub-tags)
		HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyBehaviorRule);
	
		//prepare information to resolve the xpaths
		HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", "@"+idAttrName+"='"+uniqueId+"'", "_aridattrname_", "@"+idAttrName,
				"_unid_", "@"+idAttrName+"='"+uniqueId+"'", "_unidattrname_", "@"+idAttrName, "_unidvalue_", uniqueId, 
				"_source_", "@"+sourceAttrName+"='"+sourceAttrValue+"'", "_sourceattrname_", "@"+sourceAttrName, 
				"_sourceurl_", url, "_sourcewsurl_", wsUrl);	
		
		//preparing artist type infos (ArtistUniqueIdNode) 
		List<String> artistUniqueId = MapListUtils.createInfoList(idAttrName+"='"+uniqueId+"'", sourceAttrName+"='"+sourceAttrValue+"'");
		//put ArtistUniqueIdNode to MMDG
		writeInfo(artistUniqueId, "UniqueIdNode", resolvedBaseXpath, resolveXpathInfos, copyBehavior);
		
		//preparing artist type infos (ArtistUniqueIdValue) 
		List<String> artistUniqueIdValue = MapListUtils.createInfoList(uniqueId);
		//put ArtistUniqueIdNode to MMDG
		writeInfo(artistUniqueIdValue, "UniqueIdValue", resolvedBaseXpath, resolveXpathInfos, copyBehavior);
		
		//preparing artist type infos (ArtistUniqueIdSource) 
		List<String> artistUniqueIdSource = MapListUtils.createInfoList(url.replaceAll("=", "%3D"));
		//put ArtistUniqueIdNode to MMDG
		writeInfo(artistUniqueIdSource, "UniqueIdSourceUrl", resolvedBaseXpath, resolveXpathInfos, copyBehavior);
		
		//preparing artist type infos (ArtistUniqueIdWSSource) 
		List<String> artistUniqueIdWSSource = MapListUtils.createInfoList(wsUrl.replaceAll("=", "%3D"));
		//put ArtistUniqueIdNode to MMDG
		writeInfo(artistUniqueIdWSSource, "UniqueIdSourceWSUrl", resolvedBaseXpath, resolveXpathInfos, copyBehavior);
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public NodeStatus writeInfo(String valueToAdd, String ruleName,  HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	List<String> valuesToAdd = new ArrayList<>();
	valuesToAdd.add(valueToAdd);
	return writeInfo(valuesToAdd, ruleName, "", resolveXpathInfos, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public NodeStatus writeInfo(List<String> valuesToAdd, String ruleName, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	return writeInfo(valuesToAdd, ruleName, "", resolveXpathInfos, copyBehavior); 
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public NodeStatus writeInfo(String valueToAdd, String ruleName,  String resolvedBaseXpath, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	List<String> valuesToAdd = new ArrayList<>();
	valuesToAdd.add(valueToAdd);
	return writeInfo(valuesToAdd, ruleName, resolvedBaseXpath, resolveXpathInfos, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public NodeStatus writeInfo(List<String> valuesToAdd, String ruleName, String resolvedBaseXpath, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	HashMap<String, String> mmdgMappingRule = this.mappingRules.getMappingRule(this.scraperId, ruleName);
	String xpathTarget = "";
	if(mmdgMappingRule.containsKey("xpathTarget")) {
		xpathTarget = mmdgMappingRule.get("xpathTarget");
	}
	else if(mmdgMappingRule.containsKey("xpathAbsolute")) {
		xpathTarget = XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathAbsolute"), resolveXpathInfos);
	}
	else if(mmdgMappingRule.containsKey("xpathRelative")) {
		xpathTarget = resolvedBaseXpath + XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathRelative"), resolveXpathInfos);
	}
	
	String xpathTemplate = "";
	if(mmdgMappingRule.containsKey("xpathTemplate")) {
		xpathTemplate = XmlUtils.removePredicatesFromXpath(resolvedBaseXpath+mmdgMappingRule.get("xpathTemplate"));
	}
	
	String xpathCheckNodeEmpty = "";
	if(mmdgMappingRule.containsKey("xpathCheckNodeEmpty")) {	
		xpathCheckNodeEmpty = resolvedBaseXpath + XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckNodeEmpty"), resolveXpathInfos);
	}
	
	String xpathCheckSameAttrName = ""; 
	if(mmdgMappingRule.containsKey("xpathCheckSameAttrName")) {	
		xpathCheckSameAttrName = resolvedBaseXpath + XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameAttrName"), resolveXpathInfos);
	}
	
	String xpathCheckSameAttrValue = "";
	if(mmdgMappingRule.containsKey("xpathCheckSameAttrValue")) {	
			xpathCheckSameAttrValue = resolvedBaseXpath + XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameAttrValue"), resolveXpathInfos);
	}
	
	String xpathCheckSameNodeText = "";
	if(mmdgMappingRule.containsKey("xpathCheckSameNodeText")) {		
			xpathCheckSameNodeText = resolvedBaseXpath + XmlUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameNodeText"), resolveXpathInfos);
	}
	
	HashMap<String, String> checkXpaths = new HashMap<String, String>();
	checkXpaths.put("xpathCheckNodeEmpty", xpathCheckNodeEmpty);
	checkXpaths.put("xpathCheckSameAttrName", xpathCheckSameAttrName);
	checkXpaths.put("xpathCheckSameAttrValue", xpathCheckSameAttrValue);
	checkXpaths.put("xpathCheckSameNodeText", xpathCheckSameNodeText);

	return mapInfo(valuesToAdd, xpathTarget, xpathTemplate, checkXpaths, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok (OK-Tests ok, NOK-Tests are missing)
public NodeStatus mapInfo(List<String> valuesToAdd, String targetXpath, String sourceXpath, HashMap<String, String> checkXpaths, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
	NodeStatus nodeState = NodeStatus.UNKOWN;
	NodeAction whatToDo = NodeAction.DO_NOTHING;
	
	for(NodeStatus ns : NodeStatus.values()) {
		NodeAction checkedAction = copyBehavior.get(ns);
		if(checkedAction == null && ns != NodeStatus.UNKOWN) {
			log.error("No action defined for NodeState '{}",  ns.toString());
			return nodeState;
		}
	}
	
	
	String checkXpathNodeEmpty = checkXpaths.get("xpathCheckNodeEmpty");
	String checkXpathSameAttrName = checkXpaths.get("xpathCheckSameAttrName");
	String checkXpathSameAttrValue = checkXpaths.get("xpathCheckSameAttrValue");
	String checkXpathSameNodeText = checkXpaths.get("xpathCheckSameNodeText");
	
	if(checkXpathSameAttrValue.length() > 0 && checkXpathSameNodeText.length() > 0 && getNumberOfNodes(checkXpathSameAttrValue, true) == 1 && checkIfNodeHasGivenContent(checkXpathSameNodeText, true) == true) {
		nodeState = NodeStatus.SAME_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("SAME_NODE_EXISTS for xpaths #1'{}' and #2'{}' ... whatToDo='{}'", checkXpathSameAttrValue, checkXpathSameNodeText, whatToDo.toString());		
	}
	else if(checkXpathSameNodeText.length() > 0 && checkIfNodeHasGivenContent(checkXpathSameNodeText, true) == true) {
		nodeState = NodeStatus.NODE_WITH_SAME_CONTENT_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("NODE_WITH_SAME_CONTENT_EXISTS for xpath '{}' ... whatToDo='{}'", checkXpathSameNodeText, whatToDo.toString());
	}
	else if(checkXpathSameAttrValue.length() > 0 && getNumberOfNodes(checkXpathSameAttrValue, true) >= 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS for xpath '{}' ... whatToDo='{}'", checkXpathSameAttrValue, whatToDo.toString());
	}
	else if(checkXpathSameAttrName.length() > 0 && getNumberOfNodes(checkXpathSameAttrName, true) >= 1) {
		nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS for xpath '{}' ... whatToDo='{}'", checkXpathSameAttrName, whatToDo.toString());
	}
	else if(targetXpath.length() > 0 && getNumberOfNodes(targetXpath, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("NO_EMPTY_NODE_EXISTS for xpath '{}' ... whatToDo='{}'", targetXpath, whatToDo.toString());
	}
	else if(targetXpath.length() > 0 && getNumberOfNodes(checkXpathNodeEmpty, true) == 0) {
		nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("NO_EMPTY_NODE_EXISTS (2) for xpath '{}' ... whatToDo='{}'", checkXpathNodeEmpty, whatToDo.toString());
	}
	else if(checkXpathNodeEmpty.length() > 0 && checkIfNodeAndSubNodesAreEmpty(checkXpathNodeEmpty, true) == 0) {
		nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
		whatToDo = copyBehavior.get(nodeState);
		log.info("A_EMPTY_NODE_EXISTS for xpath '{}' ... whatToDo='{}'", checkXpathNodeEmpty, whatToDo.toString());
	}
	else {
		nodeState = NodeStatus.UNKOWN;
		log.warn("Strange structure in the node");
	}
	
	if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE) {
		List<Entry<String,String>> nodesAttributes = new ArrayList<>();
		List<Entry<String,String>> nodesText = new ArrayList<>();
		//prepare all values which should be added at the targetXpath.
		// It should be one nodeText and numerous attributes  
		for (String valToAdd : valuesToAdd) {
			// prepare information (nodes attributes) for updating the node
			if(valToAdd.contains("=")) {
				nodesAttributes.add(new SimpleEntry<String,String>(targetXpath, valToAdd.replaceAll("'", "").replaceAll("\"", "")));		
			}
			else {
				// prepare information (nodes text) for updating the node 
				nodesText.add(new SimpleEntry<String,String>(targetXpath, valToAdd.replaceAll("%3D", "=").replaceAll("_equalsign_", "%3D")));
			}
		}

		// writing the values
		if(whatToDo == NodeAction.UPDATE) {
			updateNodes(nodesText, nodesAttributes, false);
		}
		if(whatToDo == NodeAction.REPLACE) {
			updateNodes(nodesText, nodesAttributes, true);
		}
		else if(whatToDo == NodeAction.ADD) {
			targetXpath = targetXpath.substring(0, targetXpath.lastIndexOf("/"));
			log.info("targetXpath='{}'", targetXpath);
			addNodes(sourceXpath, targetXpath, nodesText, nodesAttributes, true);
		}
	}	
	return nodeState;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getResolvedBaseXpath(String ruleName, HashMap<String, String> resolveXpathInfos) throws Exception {
	HashMap<String, String> artistBaseRule = this.mappingRules.getMappingRule(this.scraperId, ruleName);
	String baseXpathUnresolved = artistBaseRule.get("xpathAbsolute");
	
	return XmlUtils.resolveXpathString(baseXpathUnresolved, resolveXpathInfos);
}

	
//=============================================================================	
/*
* 	HELPER METHODS (private)
*/	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void updateNodes(List<Entry<String,String>> nodesTexts, List<Entry<String,String>> nodesAttributes, Boolean replace) throws Exception{
	
	if(nodesTexts != null) {
		for (Entry<String, String> entrNodesText : nodesTexts) {
			setNodeTextByXPath(entrNodesText.getKey(), entrNodesText.getValue());
		}
	}
	
	if(nodesAttributes != null) {
		//first delete all attributes (if replace)
		if(replace) {
			for (Entry<String, String> entrNodesAttributes : nodesAttributes) {			
				removeNodeAttributeByXPath(entrNodesAttributes.getKey());
			}
		}
		//afterwards set all attributes
		for (Entry<String, String> entrNodesAttributes : nodesAttributes) {			
			String[] parts = entrNodesAttributes.getValue().split("=");
			if(parts.length == 2) {
				setNodeAttributeTextByXPath(entrNodesAttributes.getKey(), parts[0], parts[1]);
			}
			else {
				log.warn("could not set attribute {} to node {}", entrNodesAttributes.getValue(), entrNodesAttributes.getKey());
			}
		}
	}
}
	
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void addNodes(String xpathSourceString, String xpathTargetString, List<Entry<String,String>> nodesTexts, List<Entry<String,String>> nodesAttributes, Boolean append) throws Exception {
	// copy node from template to output xml
	copyNodeFromTemplateToOutputXmlDoc(xpathSourceString, xpathTargetString, append);
	
	//update values
	updateNodes(nodesTexts, nodesAttributes, false);
}
	
//-----------------------------------------------------------------------------	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private Integer getNumberOfNodes(String xpathString, Boolean useOutputXmlDoc) {
	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	
	log.info("getNumberOfNodes: xpath={}", xpathString);
	
	Document currentXmlDoc = null;
	if(useOutputXmlDoc) {
		currentXmlDoc = outputXmlDoc;
	}
	else {
		currentXmlDoc = templateXmlDoc;
	}
	
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression expr;
	NodeList nl;
	try {
		expr = xpath.compile(xpathString);
		nl = (NodeList) expr.evaluate(currentXmlDoc, XPathConstants.NODESET);
	} catch (XPathExpressionException e) {
		e.printStackTrace();
		return -1;
	}
	log.warn("found #{} nodes", nl.getLength());
	return nl.getLength();
}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private Integer checkIfNodeAndSubNodesAreEmpty(String xpathString, Boolean useOutputXmlDoc) {
	log.trace("checkIfNodeAndSubNodesAreEmpty: xpath={}", xpathString); 	
	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	
	Document currentXmlDoc = null;
	if(useOutputXmlDoc) {
		currentXmlDoc = outputXmlDoc;
	}
	else {
		currentXmlDoc = templateXmlDoc;
	}
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression expr = null;
	String nodeText = "";
	try {
		expr = xpath.compile(xpathString);
		nodeText = (String) expr.evaluate(currentXmlDoc, XPathConstants.STRING);
	} catch (XPathExpressionException e) {
		e.printStackTrace();
		return -1;
	}
	return nodeText.trim().length();
}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private Boolean checkIfNodeHasGivenContent(String xpathString, Boolean useOutputXmlDoc) {
	log.trace("checkIfNodeHasGivenContent: xpath={}", xpathString); 	
	if(xpathString.endsWith("/")) {
		xpathString = xpathString.substring(0, xpathString.length()-1);
	}
	
	Document currentXmlDoc = null;
	if(useOutputXmlDoc) {
		currentXmlDoc = outputXmlDoc;
	}
	else {
		currentXmlDoc = templateXmlDoc;
	}
	
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression expr = null;
	Boolean result = false;
	try {
		expr = xpath.compile(xpathString);
		result = (Boolean) expr.evaluate(currentXmlDoc, XPathConstants.BOOLEAN);
	} catch (XPathExpressionException e) {
		e.printStackTrace();
		return false;
	}
	log.trace("checkIfNodeHasGivenContent: xpath={} ... {}", xpathString, result); 	
	return result;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void copyNodeFromTemplateToOutputXmlDoc(String sourceXpathString, String targetXpathString, boolean append) {
	log.trace("copyNodeFromTemplateToOutputXmlDoc(): ... from {} to {}", sourceXpathString, targetXpathString);
	if(sourceXpathString.endsWith("/")) {
		sourceXpathString = sourceXpathString.substring(0, sourceXpathString.length()-1);
	}
	if(targetXpathString.endsWith("/")) {
		targetXpathString = targetXpathString.substring(0, targetXpathString.length()-1);
	}
	
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression sourceExpr;
	XPathExpression targetExpr;
	NodeList sourceNl;
	NodeList targetNl;
	try {
		sourceExpr = xpath.compile(sourceXpathString);
		targetExpr = xpath.compile(targetXpathString);
		sourceNl = (NodeList) sourceExpr.evaluate(templateXmlDoc, XPathConstants.NODESET);
		targetNl = (NodeList) targetExpr.evaluate(outputXmlDoc, XPathConstants.NODESET);
		if(sourceNl.getLength() == 1 && targetNl.getLength() == 1) {
			Node sourceNode = sourceNl.item(0);
			Node targetNode = targetNl.item(0);
			
			Node copiedNode = outputXmlDoc.importNode(sourceNode, true);
			//if append=true, then append the copied node to the list 
			if(append) {
				targetNode.appendChild(copiedNode);
			}
			//if append is false, then put the copied node at the fist position of the list
			else {
				targetNode.insertBefore(copiedNode, targetNode.getFirstChild());
			}
		}
		else {
			log.warn("Source XPath resulted in NodeList={}, Target XPath resulted in NodeList={} ... both NodeLists should have a length of 1", sourceNl.getLength(), targetNl.getLength());
		}
	} catch (XPathExpressionException e) {
		e.printStackTrace();
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private String resolveXpath(String xpath, String... replacementStrings) {
  for (String replacementString : replacementStrings) {
  	String[] parts = replacementString.split("::");
  	if(parts.length == 2) {
  		String replKey = parts[0];
  		String replVal = parts[1];
  		xpath = xpath.replaceAll(replKey, replVal);
  	}
  	else if(parts.length == 1) {
  		String replKey = parts[0];
  		int idxOfReplKey = xpath.indexOf(replKey);
  		if(idxOfReplKey == -1) {
  			return xpath;
  		}
  		else {
  			xpath = xpath.substring(0, idxOfReplKey-1) + xpath.substring(idxOfReplKey+replKey.length()+1, xpath.length());
  		}
  	}
  }
	return xpath;
}
	
	
//=============================================================================	
/*
* 	GETTER/SETTER/CREATOR/WRITER METHODS (public)
*/	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public void createOutputXmlDoc() throws Exception {
	File fXmlTemplateFile = new File(templateFilePath);
	
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	this.setTemplateXmlDoc(dBuilder.parse(fXmlTemplateFile));
	this.setOutputXmlDoc(dBuilder.parse(fXmlTemplateFile));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public void overwriteTemplateXmlDoc(String templateFilePath) throws Exception {
	File fXmlTemplateFile = new File(templateFilePath);
	
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	this.setTemplateXmlDoc(dBuilder.parse(fXmlTemplateFile));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeOutputDocToFile(String filePath, String fileName) {
	 File outputPath = new File(filePath);
	 
	 if(!outputPath.exists()) {
		 outputPath.mkdirs();
	 }
	
	outputXmlDoc.getDocumentElement().normalize();
	Transformer transformer = null;
	try {
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	} catch (TransformerConfigurationException e) {
		e.printStackTrace();
	}
	DOMSource source = new DOMSource(outputXmlDoc);
	StreamResult result = new StreamResult(new File(filePath + "\\" + fileName));
	try {
		transformer.transform(source, result);
	} catch (TransformerException e) {
		e.printStackTrace();
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public String getOutputDocAsString() throws Exception {
	
	outputXmlDoc.getDocumentElement().normalize();
	Transformer transformer = null;
	transformer = TransformerFactory.newInstance().newTransformer();
	DOMSource source = new DOMSource(outputXmlDoc);
	StringWriter writer = new StringWriter();
	transformer.transform(source, new StreamResult(writer));
	
	return writer.toString();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public String getTemplateDocAsString() throws Exception {
	
	templateXmlDoc.getDocumentElement().normalize();
	Transformer transformer = null;
	transformer = TransformerFactory.newInstance().newTransformer();
	DOMSource source = new DOMSource(templateXmlDoc);
	StringWriter writer = new StringWriter();
	transformer.transform(source, new StreamResult(writer));
	
	return writer.toString();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setNodeTextByXPath(String xpathString, String nodeText) throws Exception {
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
	NodeList nl = (NodeList) expr.evaluate(outputXmlDoc, XPathConstants.NODESET);

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
public String getNodeTextByXPath(String xpathString) {
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
		nl = (NodeList) expr.evaluate(outputXmlDoc, XPathConstants.NODESET);
	} catch (XPathExpressionException e) {
		e.printStackTrace();
	}
	
	if(nl != null && nl.getLength() > 1) {
		log.warn("XPath '{}' resulted in {} nodes. return node text from the first node", xpathString, nl.getLength());
	}
	else if(nl != null && nl.getLength() == 0) {
		log.warn("XPath '{}' resulted in {} nodes", xpathString, nl.getLength());
		return null;
	}
	else {
		log.warn("XPath '{}' resulted in null nodes", xpathString);
		return null;
	}
	
	return nl.item(0).getNodeValue(); 
}

//-----------------------------------------------------------------------------	
	
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//FUNC-NAME:		nok .. better setNodeAttributeByXPath(..)
public void setNodeAttributeTextByXPath(String xpathString, String attrName, String attrValue) throws Exception {
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
	NodeList nl = (NodeList) expr.evaluate(outputXmlDoc, XPathConstants.NODESET);

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

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void removeNodeAttributeByXPath(String xpathString) throws Exception {
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
	NodeList nl = (NodeList) expr.evaluate(outputXmlDoc, XPathConstants.NODESET);

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
public String getNodeAttributeTextByXPath(String xpathString, String attrName) {
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
		nl = (NodeList) expr.evaluate(outputXmlDoc, XPathConstants.NODESET);
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

public String getTemplateFilePath() {	return templateFilePath;	}

//-----------------------------------------------------------------------------

public void setTemplateFilePath(String templateFilePath) {	this.templateFilePath = templateFilePath;	}

//-----------------------------------------------------------------------------

public Document getOutputXmlDoc() { return outputXmlDoc; }

//-----------------------------------------------------------------------------

public void setOutputXmlDoc(Document outputXmlDoc) { this.outputXmlDoc = outputXmlDoc; }

//-----------------------------------------------------------------------------

public Document getTemplateXmlDoc() { return templateXmlDoc; }

//-----------------------------------------------------------------------------

public void setTemplateXmlDoc(Document templateXmlDoc) { this.templateXmlDoc = templateXmlDoc; }

//-----------------------------------------------------------------------------

}