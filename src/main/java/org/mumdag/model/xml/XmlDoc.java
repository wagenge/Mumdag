package org.mumdag.model.xml;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.MappingRules;
import org.mumdag.utils.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.util.*;

//-----------------------------------------------------------------------------

public class XmlDoc {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(XmlDoc.class);

private Document xmlDoc = null;
private Document templateXmlDoc = null;
private MappingRules mappingRules;
private String scraperId;

public enum NodeStatus {    A_EMPTY_NODE_EXISTS,
                            NO_EMPTY_NODE_EXISTS,
                            NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS,
                            NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS,
                            NODE_WITH_SAME_CONTENT_EXISTS,
                            SAME_NODE_EXISTS,
                            UNKOWN
}
public enum NodeAction {    UPDATE,
                            REPLACE,
                            ADD,
                            DO_NOTHING
}


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
XmlDoc(String xmlFilePath, String templateXmlFilePath) {
    try {
        this.xmlDoc = XmlUtils.createXmlDoc(xmlFilePath);
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
        this.scraperId = PropertyHandler.getInstance().getValue("Mumdag.scraperId");
        setupMappingRule();
    } catch (Exception ex) {
        log.error("Could not create xml doc! \nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
XmlDoc(String xmlFilePath) {
    try {
        String templateXmlFilePath = PropertyHandler.getInstance().getValue("ArtistXmlModel.templatePath");
        this.xmlDoc = XmlUtils.createXmlDoc(xmlFilePath);
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
        this.scraperId = PropertyHandler.getInstance().getValue("Mumdag.scraperId");
        setupMappingRule();
    } catch (Exception ex) {
        log.error("Could not create xml doc! \nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
XmlDoc(Document xmlDoc, Document templateXmlDoc) {
    this.xmlDoc = xmlDoc;
    this.templateXmlDoc = templateXmlDoc;
    try {
        this.scraperId = PropertyHandler.getInstance().getValue("Mumdag.scraperId");
        setupMappingRule();
    } catch (Exception ex) {
        this.scraperId = "mmdg";
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
XmlDoc(Document xmlDoc) {
    this.xmlDoc = xmlDoc;
    try {
        String templateXmlFilePath = PropertyHandler.getInstance().getValue("ArtistXmlModel.templatePath");
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
        this.scraperId = PropertyHandler.getInstance().getValue("Mumdag.scraperId");
        setupMappingRule();
    } catch (Exception ex) {
        log.error("Could not create template for xml doc! \nError: {}", ex.getMessage());
    }
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING GENERAL BLOCKS (protected)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeUniqueIdInfos(HashMap<String, String> insertInfos, HashMap<String, Object> resolveInfos, String targetRule, String copyRule, String origScraperId, String scraperName) throws Exception {
    String uniqueId = insertInfos.get("unid");
    String url = insertInfos.get("url");
    if(StringUtils.isEmpty(url)) {
        url = "-";
    }
    String propSubSection = (String)resolveInfos.get("propSubSection");
    String placeholder = (String)resolveInfos.get("placeholder");
    List<String> resolveInfoList = (List<String>)resolveInfos.get("resolveInfo");
    Boolean foundProp = false;

    //retrieve Information for Scraper.Section (e.g., Discogs.Scraper.[Artist].xyz)
    // 	infos are 	.id, .srcAttrName, .sourceAttrValue, .Base.url,
    //				.[Artist|ReleaseGroup|Release|Track].url, .[Artist|ReleaseGroup|Release|Track].urlParams
    String idAttrName;
    String srcAttrName = "";
    String srcAttrValue = "";
    String wsUrl = "";

    try {
        idAttrName = PropertyHandler.getInstance().getValue(scraperName+".Scraper."+propSubSection+".idAttrName");
    } catch (Exception ex) {
        idAttrName = "";
    }

    try {
        if(StringUtils.isEmpty(idAttrName)) {
            idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        }
        srcAttrName = PropertyHandler.getInstance().getValue(scraperName+".Scraper.srcAttrName");
        srcAttrValue = PropertyHandler.getInstance().getValue(scraperName+".Scraper.srcAttrValue");
        if(!PropertyHandler.getInstance().containsKey(scraperName+".Scraper.Base.wsUrl")) {
            wsUrl= "-";
        }
        else if(PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.wsUrl").equals("-")) {
            wsUrl = PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.wsUrl");
        }
        else {
            if(PropertyHandler.getInstance().containsKey(scraperName+".Scraper."+propSubSection+".wsUrl")) {
                wsUrl = (PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.wsUrl") +
                        PropertyHandler.getInstance().getValue(scraperName+".Scraper."+propSubSection+".wsUrl")).replaceAll(placeholder, uniqueId);
            }
            else {
                wsUrl = (PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.wsUrl") +
                        PropertyHandler.getInstance().getValue(scraperName+".Scraper."+propSubSection+".url")).replaceAll(placeholder, uniqueId);
            }
        }
        if(PropertyHandler.getInstance().containsKey(scraperName+".Scraper."+propSubSection+".url") ||
                (PropertyHandler.getInstance().containsKey(scraperName+".Scraper.Base.url") && !PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.url").equals("-"))) {
            url = (PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.url") +
                    PropertyHandler.getInstance().getValue(scraperName+".Scraper."+propSubSection+".url")).replaceAll(placeholder, uniqueId);
        }

        log.info("idAttrName={}, srcAttrName={}, sourceAttrValue={}, wsUrl={}, url={}", idAttrName, srcAttrName, srcAttrValue, wsUrl, url);
        foundProp = true;
    } catch (Exception e) {
        log.warn("Could not write {} uniqueId for {}, {}", scraperName, propSubSection, e.getMessage());
    }

    if(foundProp) {
        // define the behavior of the operation depending on the current node state (for artists' uniqueId and sub-tags)
        HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(origScraperId, copyRule);

        //prepare information to resolve the xpaths
        HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+uniqueId,
                "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
                "_unidValue_::"+uniqueId,
                "_srcUrl_::"+url.replaceAll("=", "%3D"),
                "_srcWsUrl_::"+wsUrl.replaceAll("=", "%3D"));

        //resolve the base xpath
        // resolveInfo is converted into a String[]
        HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
        String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

        //preparing infos (UniqueIdNode)
        List<String> uniqueIdNode = ListUtils.array2List(idAttrName+"='"+uniqueId+"'", srcAttrName+"='"+srcAttrValue+"'");
        //put ArtistUniqueIdNode to MMDG
        writeInfo(uniqueIdNode, "UniqueIdNode", resolvedBase, resolveMap, copyBehavior);

        //preparing infos (UniqueIdValue)
        List<String> uniqueIdValue = ListUtils.array2List(uniqueId);
        //put UniqueIdValue to MMDG
        writeInfo(uniqueIdValue, "UniqueIdValue", resolvedBase, resolveMap, copyBehavior);

        //preparing infos (UniqueIdSource)
        List<String> uniqueIdSource = ListUtils.array2List(url.replaceAll("=", "%3D"));
        //put UniqueIdSource to MMDG
        writeInfo(uniqueIdSource, "UniqueIdSourceUrl", resolvedBase, resolveMap, copyBehavior);

        //preparing infos (UniqueIdWSSource)
        List<String> uniqueIdWSSource = ListUtils.array2List(wsUrl.replaceAll("=", "%3D"));
        //put UniqueIdWSSource to MMDG
        writeInfo(uniqueIdWSSource, "UniqueIdSourceWSUrl", resolvedBase, resolveMap, copyBehavior);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeUniqueId(HashMap<String, String> insertInfos, HashMap<String, Object> resolveInfos, String targetRule, String copyRule, String origScraperId, String scraperName, String idAttrName, String writeNodeAttributes) throws Exception {
    String uniqueId = insertInfos.get("unid");
    String propSubSection = (String)resolveInfos.get("propSubSection");
    List<String> resolveInfoList = (List<String>)resolveInfos.get("resolveInfo");
    Boolean foundProp = false;

    //retrieve Information for Scraper.Section (e.g., Discogs.Scraper.[Artist].xyz)
    // 	infos are 	.id, .srcAttrName, .sourceAttrValue, .Base.url,
    //				.[Artist|ReleaseGroup|Release|Track].url, .[Artist|ReleaseGroup|Release|Track].urlParams
    if(StringUtils.isEmpty(idAttrName)) {
        try {
            idAttrName = PropertyHandler.getInstance().getValue(scraperName+".Scraper."+propSubSection+".idAttrName");
            log.info("idAttrName={}", idAttrName);
            foundProp = true;
        } catch (Exception e) {
            idAttrName = "";
        }

        if(StringUtils.isEmpty(idAttrName)) {
            try {
                idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
                log.info("idAttrName={}", idAttrName);
                foundProp = true;
            } catch (Exception e) {
                idAttrName = "";
                log.warn("Could not write {} uniqueId for {}, {}", scraperName, propSubSection, e.getMessage());
            }
        }
    }
    else {
        foundProp = true;
    }

    if(foundProp) {
        // define the behavior of the operation depending on the current node state (for artists' uniqueId and sub-tags)
        HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(origScraperId, copyRule);

        //prepare information to resolve the xpaths
        HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::" + idAttrName + "=" + uniqueId);

        //resolve the base xpath
        // resolveInfo is converted into a String[]
        HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
        String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

        //preparing infos (UnId)
        List<String> unId = ListUtils.array2List(idAttrName + "='" + uniqueId + "'");
        if(writeNodeAttributes.equals("ALL_ATTRIBUTES")) {
            HashMap<String, String> insertUnIds = new HashMap<>(insertInfos);
            insertUnIds.remove("unid");
            unId = ListUtils.addMapToInfoList(unId, insertUnIds, "=", "'");
        }
        //put UnId to MMDG
        writeInfo(unId, "UnId", resolvedBase, resolveMap, copyBehavior);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeNameInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String ntypeAttrName = "type";
    String ntypeAttrValue = "unkown";
    if(insertInfo.containsKey("nameType")) {
        ntypeAttrValue = (String) insertInfo.get("nameType");
    }
    String arNameValue = (String)insertInfo.get("name");
    String arSortNameValue = "";
    if(insertInfo.containsKey("sortName")) {
        arSortNameValue = (String) insertInfo.get("sortName");
    }

    //extract the additionInfo list
    // if the name type is set in the additionalInfo list, take it from these list
    ArrayList<String> insertInfoList = new ArrayList<>();
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
        for (String nameInfoStr : insertInfoList) {
            if (nameInfoStr.contains("type=")) {
                String[] nameTypeInfos = nameInfoStr.split("=");
                ntypeAttrValue = nameTypeInfos[1];
            }
        }
    }

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
            "_ntypeAttr_::"+ntypeAttrName+"="+ntypeAttrValue,
            "_arname_::"+arNameValue);

    //resolve the base xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //add idAttr, srcAttr and ntypeAttr to insertInfoList list
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(resolveMap.get("_ntypeAttr_"));

    //add name and sort-name (attribute) to the insertInfoList
    insertInfoList.add(arNameValue);
    if(arSortNameValue != null && arSortNameValue.length() > 0) {
        String artistSortNameAttr = "sort-name"+"="+arSortNameValue;
        insertInfoList.add(artistSortNameAttr);
    }

    //put Name to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeTypeInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String typeAttrName = "type";
    String typeAttrValue = "Unkown";
    if(insertInfo.containsKey("typeType")) {
        typeAttrValue = (String) insertInfo.get("typeType");
    }
    String typeValue = (String)insertInfo.get("type");

    //extract the additionInfo list
    // if the name type is set in the additionalInfo list, take it from these list
    ArrayList<String> insertInfoList = new ArrayList<>();
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
            "_typeAttr_::"+typeAttrName+"="+typeAttrValue,
            "_type_::"+typeValue);

    //resolve the base xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //add idAttr, srcAttr and ntypeAttr to insertInfoList list
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(resolveMap.get("_typeAttr_"));
    insertInfoList.add(typeValue);

    //put Name to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARATERIZATION:   nok, born/founded/died/dissolved
@SuppressWarnings("unchecked")
void writePlaceInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String placeIdAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String placeIdAttrValue = (String)insertInfo.get("unid");
    String placeTypeAttrName = "type";
    String placeTypeAttrValue = (String)insertInfo.get("type");
    String placeEventAttrName = "event";
    String placeEventAttrValue = (String)insertInfo.get("event");
    String placeValue = (String)insertInfo.get("name");

    // define the behavior of the operation depending on the current node state (for period begin/end)
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap((resolveInfoList.toArray(new String[0])));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_placeIdAttr_::"+placeIdAttrName+"="+placeIdAttrValue,
            "_placeEventAttr_::"+placeEventAttrName+"="+placeEventAttrValue,
            "_placeTypeAttr_::"+placeTypeAttrName+"="+placeTypeAttrValue,
            "_place_::"+placeValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_placeIdAttr_"));
    insertInfoList.add(resolveMap.get("_placeEventAttr_"));
    insertInfoList.add(resolveMap.get("_placeTypeAttr_"));
    insertInfoList.add(srcAttrName+"="+srcAttrValue);
    insertInfoList.add(placeValue);
    insertInfoList.add("seq="+insertInfo.get("seq"));
    if(insertInfo.containsKey("code")) {
        insertInfoList.add("code=" + insertInfo.get("code"));
    }
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>)insertInfo.get("additionalInfo"));
    }

    //put ArtistPeriod Begin/End to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARATERIZATION:   nok, born/founded/died/dissolved
@SuppressWarnings("unchecked")
void writeDateInfo(HashMap<String, Object> insertInfo, HashMap<String, Object> resolveInfoMap, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String arTypeValue = (String)insertInfo.get("type");
    String dateValue = (String)insertInfo.get("date");
    String dateTypeAttrName = "type";

    String dateType = (String)resolveInfoMap.get("dateType");
    List<String> resolveInfoList = (List<String>)resolveInfoMap.get("resolveInfo");

    // define the behavior of the operation depending on the current node state (for period begin/end)
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //preparing infos for period begin/end
    String dateTypeAttrValue = dateType;
    if (dateType.equals("begin")) {
        if(StringUtils.isNotEmpty(arTypeValue) && arTypeValue.equals("Person")) {
            dateTypeAttrValue = "born";
        } else if(StringUtils.isNotEmpty(arTypeValue) && arTypeValue.equals("Group")) {
            dateTypeAttrValue = "founded";
        }
    } else if (dateType.equals("end")) {
        if(StringUtils.isNotEmpty(arTypeValue) && arTypeValue.equals("Person")) {
            dateTypeAttrValue = "died";
        } else if(StringUtils.isNotEmpty(arTypeValue) && arTypeValue.equals("Group")) {
            dateTypeAttrValue = "dissolved";
        }
    }

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_dateTypeAttr_::"+dateTypeAttrName+"="+dateTypeAttrValue,
            "_date_::"+dateValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_dateTypeAttr_"));
    insertInfoList.add(dateValue);
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    //put ArtistPeriod Begin/End to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeTagInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName;
    String idAttrValue;
    String srcAttrName;
    String srcAttrValue;
    //try to extract attribute infos from insertInfo Map
    //  if it does not work, try the classical way via properties
    try {
        idAttrName = ((String) insertInfo.get("unid")).split("=")[0];
        idAttrValue = ((String) insertInfo.get("unid")).split("=")[1];
        srcAttrName = ((String) insertInfo.get("source")).split("=")[0];
        srcAttrValue = ((String) insertInfo.get("source")).split("=")[1];
    } catch (Exception ex) {
        idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        idAttrValue = (String)insertInfo.get("unid");
        srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
        srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    }
    String tagValue = (String)insertInfo.get("tag");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for Artist
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue, "_tag_::"+tagValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(tagValue);
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    //put Tag to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeUrlInfo(HashMap<String, String> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = insertInfo.get("unid");
    String urlTypeAttrName = "type";
    String urlTypeValue = "unkown";
    if(insertInfo.containsKey("type")) {
        urlTypeValue = insertInfo.get("type");
    }
    String urlValue = insertInfo.get("url");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for Artist
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_urlTypeAttr_::"+urlTypeAttrName+"="+urlTypeValue,
            "_url_::"+urlValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_urlTypeAttr_"));
    insertInfoList.add(urlValue);

    //put ArtistUrl to MMDG
    writeInfo(insertInfoList, "Artist.Url", resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeRatingInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String ratingValue = (String)insertInfo.get("rating");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for Artist
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
            "_rating_::"+ratingValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(ratingValue);
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    //put ArtistUrl to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeArtistCreditNode(String artistCreditId, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_acidAttr_::"+idAttrName+"="+artistCreditId);

    //resolve the base xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //put ArtistCreditNode to MMDG
    writeInfo(idAttrName+"="+artistCreditId, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeRoleInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String role = (String)insertInfo.get("role");
    String roleTypeAttrName = "type";
    String roleTypeAttrValue = (String)insertInfo.get("roleType");
    String originalAttr = "";
    if(role.equals("member of band") && insertInfo.containsKey("original") && insertInfo.get("original").equals("true")) {
        originalAttr = "original"+"="+"true";
    }
    else if(role.equals("member of band")) {
        originalAttr = "original"+"="+"false";
    }

    // define the behavior of the operation depending on the current node state (for period begin/end)
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for xpath
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_roleTypeAttr_::"+roleTypeAttrName+"="+roleTypeAttrValue,
            "_role_::"+role);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_roleTypeAttr_"));
    insertInfoList.add(role);
    if(StringUtils.isNotEmpty(originalAttr)) {
        insertInfoList.add(originalAttr);
    }
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    //put Name to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeDisambiguationInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String disambiguationValue = (String)insertInfo.get("disambiguation");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for Artist
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
            "disambiguation::"+disambiguationValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(disambiguationValue);
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    //put ArtistUrl to MMDG
    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
void writeAnnotationInfo(HashMap<String, Object> insertInfo, List<String> resolveInfoList, String targetRule, String copyRule, String scraperName) throws Exception {
    //extract information about (scraper)id, source, name and name type
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    String srcAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    String srcAttrValue = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrValue");
    String annotationValue = (String)insertInfo.get("annotation");

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<XmlDoc.NodeStatus, XmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //resolve the base for Artist
    // resolveInfo is converted into a String[]
    HashMap<String, String> resolveBaseMap = XpathUtils.createResolveMap(resolveInfoList.toArray(new String[0]));
    String resolvedBase = getResolvedBase(this.scraperId, targetRule.substring(0, targetRule.indexOf(".")), resolveBaseMap);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = XpathUtils.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
            "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
            "_annotation_::"+annotationValue);

    //prepeare the info to be inserted
    ArrayList<String> insertInfoList = new ArrayList<>();
    insertInfoList.add(resolveMap.get("_idAttr_"));
    insertInfoList.add(resolveMap.get("_srcAttr_"));
    insertInfoList.add(annotationValue);
    if(insertInfo.containsKey("additionalInfo")) {
        insertInfoList.addAll((List<String>) insertInfo.get("additionalInfo"));
    }

    writeInfo(insertInfoList, targetRule, resolvedBase, resolveMap, copyBehavior);
}


//=============================================================================
/*
 * 	BASIC METHODS FOR WRITING/READING BLOCKS (private)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private NodeStatus writeInfo(String valueToAdd, String ruleName, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
    List<String> valuesToAdd = new ArrayList<>();
    valuesToAdd.add(valueToAdd);
    return writeInfo(valuesToAdd, ruleName, "", resolveXpathInfos, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private NodeStatus writeInfo(List<String> valuesToAdd, String ruleName, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
    return writeInfo(valuesToAdd, ruleName, "", resolveXpathInfos, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private NodeStatus writeInfo(String valueToAdd, String ruleName, String resolvedBaseXpath, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
    List<String> valuesToAdd = new ArrayList<>();
    valuesToAdd.add(valueToAdd);
    return writeInfo(valuesToAdd, ruleName, resolvedBaseXpath, resolveXpathInfos, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private NodeStatus writeInfo(List<String> valuesToAdd, String ruleName, String resolvedBaseXpath, HashMap<String, String> resolveXpathInfos, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
    //get the maping rule for the given ruleName
    // in case the ruleName has two part (separated by a '.') use the second part
    HashMap<String, String> mmdgMappingRule;
    if(ruleName.contains(".")) {
        mmdgMappingRule = this.mappingRules.getMappingRule(this.scraperId, ruleName.split("\\.")[1]);
    } else {
        mmdgMappingRule = this.mappingRules.getMappingRule(this.scraperId, ruleName);
    }

    String xpathRelative = "";
    if(mmdgMappingRule.containsKey("xpathRelative")) {
        xpathRelative = mmdgMappingRule.get("xpathRelative");
    }

    String xpathTemplate = "";
    if(mmdgMappingRule.containsKey("xpathTemplate")) {
        xpathTemplate = XpathUtils.removePredicatesFromXpath(resolvedBaseXpath+mmdgMappingRule.get("xpathTemplate"));
    }

    String xpathCheckNodeEmpty = "";
    if(mmdgMappingRule.containsKey("xpathCheckNodeEmpty")) {
        xpathCheckNodeEmpty = resolvedBaseXpath + XpathUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckNodeEmpty"), resolveXpathInfos);
    }

    String xpathCheckSameAttrName = "";
    if(mmdgMappingRule.containsKey("xpathCheckSameAttrName")) {
        xpathCheckSameAttrName = resolvedBaseXpath + XpathUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameAttrName"), resolveXpathInfos);
    }

    String xpathCheckSameAttrValue = "";
    if(mmdgMappingRule.containsKey("xpathCheckSameAttrValue")) {
        xpathCheckSameAttrValue = resolvedBaseXpath + XpathUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameAttrValue"), resolveXpathInfos);
    }

    String xpathCheckSameNodeText = "";
    if(mmdgMappingRule.containsKey("xpathCheckSameNodeText")) {
        xpathCheckSameNodeText = resolvedBaseXpath + XpathUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameNodeText"), resolveXpathInfos);
    }

    String xpathCheckSameNode = "";
    if(mmdgMappingRule.containsKey("xpathCheckSameNode")) {
        xpathCheckSameNode = resolvedBaseXpath + XpathUtils.resolveXpathString(mmdgMappingRule.get("xpathCheckSameNode"), resolveXpathInfos);
    }

    HashMap<String, String> checkXpaths = new HashMap<>();
    checkXpaths.put("xpathCheckNodeEmpty", xpathCheckNodeEmpty);
    checkXpaths.put("xpathCheckSameAttrName", xpathCheckSameAttrName);
    checkXpaths.put("xpathCheckSameAttrValue", xpathCheckSameAttrValue);
    checkXpaths.put("xpathCheckSameNodeText", xpathCheckSameNodeText);
    checkXpaths.put("xpathCheckSameNode", xpathCheckSameNode);
    checkXpaths.put("xpathTargetBase", resolvedBaseXpath);
    checkXpaths.put("xpathTargetRelative", xpathRelative);
    checkXpaths.put("xpathSource", xpathTemplate);

    return mapInfo(valuesToAdd, checkXpaths, copyBehavior);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok (OK-Tests ok, NOK-Tests are missing)
private NodeStatus mapInfo(List<String> valuesToAdd, HashMap<String, String> checkXpaths, HashMap<NodeStatus, NodeAction> copyBehavior) throws Exception {
    NodeStatus nodeState = NodeStatus.UNKOWN;
    NodeAction whatToDo = NodeAction.DO_NOTHING;

    for(NodeStatus ns : NodeStatus.values()) {
        NodeAction checkedAction = copyBehavior.get(ns);
        if(checkedAction == null && ns != NodeStatus.UNKOWN) {
            log.error("No action defined for NodeState '{}",  ns.toString());
            return nodeState;
        }
    }

    String targetBaseXpath = checkXpaths.get("xpathTargetBase");
    String targetRelativeXpath = checkXpaths.get("xpathTargetRelative");
    String targetXpath = targetBaseXpath + targetRelativeXpath;
    String sourceXpath = checkXpaths.get("xpathSource");
    String checkXpathNodeEmpty = checkXpaths.get("xpathCheckNodeEmpty");
    String checkXpathSameAttrName = checkXpaths.get("xpathCheckSameAttrName");
    String checkXpathSameAttrValue = checkXpaths.get("xpathCheckSameAttrValue");
    String checkXpathSameNodeText = checkXpaths.get("xpathCheckSameNodeText");
    String checkXpathCheckSameNode = checkXpaths.get("xpathCheckSameNode");
    List<String> sameNodeList =  XmlUtils.getXpathsForMatchingNodes(this.xmlDoc, checkXpathCheckSameNode);
    List<String> sameNodeTextList =  XmlUtils.getXpathsForMatchingNodes(this.xmlDoc, checkXpathSameNodeText);
    List<String> sameAttrValueList =  XmlUtils.getXpathsForMatchingNodes(this.xmlDoc, checkXpathSameAttrValue);
    List<String> sameAttrNameList =  XmlUtils.getXpathsForMatchingNodes(this.xmlDoc, checkXpathSameAttrName);
    List<String> emptyNodeList =  XmlUtils.getXpathsForMatchingEmptyNodes(this.xmlDoc, checkXpathNodeEmpty);
    if(sameNodeList.size() >= 1) {
        nodeState = NodeStatus.SAME_NODE_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        if(whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE ) {
            targetXpath = sameNodeList.get(0);
        }
        log.info("SAME_NODE_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathCheckSameNode, whatToDo.toString(), targetXpath);
    } else if(sameNodeTextList.size() >= 1) {
        nodeState = NodeStatus.NODE_WITH_SAME_CONTENT_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        if(whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE ) {
            targetXpath = sameNodeTextList.get(0);
        }
        log.info("NODE_WITH_SAME_CONTENT_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathSameNodeText, whatToDo.toString(), targetXpath);
    } else if(sameAttrValueList.size() >= 1) {
        nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        if(whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE ) {
            targetXpath = sameAttrValueList.get(0);
        }
        log.info("NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathSameAttrValue, whatToDo.toString(), targetXpath);
    } else if(sameAttrNameList.size() == 1) {
        nodeState = NodeStatus.NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        if(whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE ) {
            targetXpath = sameAttrNameList.get(0);
        }
        log.info("NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathSameAttrName, whatToDo.toString(), targetXpath);
    } else if(emptyNodeList.size() >= 1) {
        nodeState = NodeStatus.A_EMPTY_NODE_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        if(whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE ) {
            targetXpath = emptyNodeList.get(0);
        }
        log.info("A_EMPTY_NODE_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathNodeEmpty, whatToDo.toString(), targetXpath);
    } else if(emptyNodeList.size() == 0) {
        nodeState = NodeStatus.NO_EMPTY_NODE_EXISTS;
        whatToDo = copyBehavior.get(nodeState);
        log.info("NO_EMPTY_NODE_EXISTS for xpath '{}' ...\n\twhatToDo='{}' ...\n\ttargetXPath='{}'", checkXpathNodeEmpty, whatToDo.toString(), targetXpath);
    } else {
        nodeState = NodeStatus.UNKOWN;
        log.warn("Strange structure in the node");
    }

    if(whatToDo == NodeAction.ADD || whatToDo == NodeAction.UPDATE || whatToDo == NodeAction.REPLACE) {
        List<Map.Entry<String,String>> nodesAttributes = new ArrayList<>();
        List<Map.Entry<String,String>> nodesText = new ArrayList<>();
        //prepare all values which should be added at the targetXpath.
        // It should be one nodeText and numerous attributes
        for (String valToAdd : valuesToAdd) {
            // prepare information (nodes attributes) for updating the node
            if(valToAdd.contains("=")) {
                nodesAttributes.add(new AbstractMap.SimpleEntry<>(targetXpath, valToAdd.replaceAll("'", "").replaceAll("\"", "")));
            }
            else {
                // prepare information (nodes text) for updating the node
                nodesText.add(new AbstractMap.SimpleEntry<>(targetXpath, valToAdd.replaceAll("%3D", "=").replaceAll("_equalsign_", "%3D")));
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
private void updateNodes(List<Map.Entry<String,String>> nodesTexts, List<Map.Entry<String,String>> nodesAttributes, Boolean replace) throws Exception{

    if(nodesTexts != null) {
        for (Map.Entry<String, String> entrNodesText : nodesTexts) {
            XmlUtils.setNodeTextByXPath(this.xmlDoc, entrNodesText.getKey(), entrNodesText.getValue());
        }
    }

    if(nodesAttributes != null) {
        //first delete all attributes (if replace)
        if(replace) {
            for (Map.Entry<String, String> entrNodesAttributes : nodesAttributes) {
                XmlUtils.removeNodeAttributeByXPath(this.xmlDoc, entrNodesAttributes.getKey());
            }
        }
        //afterwards set all attributes
        for (Map.Entry<String, String> entrNodesAttributes : nodesAttributes) {
            String[] parts = entrNodesAttributes.getValue().split("=");
            if(parts.length == 2) {
                XmlUtils.setNodeAttributeByXPath(this.xmlDoc, entrNodesAttributes.getKey(), parts[0], parts[1]);
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
private void addNodes(String xpathSourceString, String xpathTargetString, List<Map.Entry<String,String>> nodesTexts, List<Map.Entry<String,String>> nodesAttributes, Boolean append) throws Exception {
    // copy node from template to output xml
    copyNodeFromTemplateToOutputXmlDoc(xpathSourceString, xpathTargetString, append);

    //update values
    updateNodes(nodesTexts, nodesAttributes, false);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void copyNodeFromTemplateToOutputXmlDoc(String sourceXpathString, String targetXpathString, boolean append) {
    log.trace(" from {} to {}", sourceXpathString, targetXpathString);
    if(sourceXpathString.endsWith("/")) {
        sourceXpathString = sourceXpathString.substring(0, sourceXpathString.length()-1);
    }
    if(targetXpathString.endsWith("/")) {
        targetXpathString = targetXpathString.substring(0, targetXpathString.length()-1);
    }

    if(StringUtils.isNotEmpty(targetXpathString) && StringUtils.isNotEmpty(sourceXpathString)) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression sourceExpr;
        XPathExpression targetExpr;
        NodeList sourceNl;
        NodeList targetNl;
        try {
            sourceExpr = xpath.compile(sourceXpathString);
            targetExpr = xpath.compile(targetXpathString);
            sourceNl = (NodeList) sourceExpr.evaluate(this.templateXmlDoc, XPathConstants.NODESET);
            targetNl = (NodeList) targetExpr.evaluate(this.xmlDoc, XPathConstants.NODESET);
            if (sourceNl.getLength() == 1 && targetNl.getLength() == 1) {
                Node sourceNode = sourceNl.item(0);
                Node targetNode = targetNl.item(0);

                Node copiedNode = this.xmlDoc.importNode(sourceNode, true);
                //if append=true, then append the copied node to the list
                if (append) {
                    targetNode.appendChild(copiedNode);
                }
                //if append is false, then put the copied node at the fist position of the list
                else {
                    targetNode.insertBefore(copiedNode, targetNode.getFirstChild());
                }
            } else {
                log.warn("Source XPath resulted in NodeList={}, Target XPath resulted in NodeList={} ... both NodeLists should have a length of 1", sourceNl.getLength(), targetNl.getLength());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}


//=============================================================================
/*
 * 	HELPER METHODS (private)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private String getResolvedBase(String scraperId, String ruleName, HashMap<String, String> resolveXpathInfos) {
    HashMap<String, String> artistBaseRule = this.mappingRules.getMappingRule(scraperId, ruleName);
    if(artistBaseRule != null) {
        String baseXpathUnresolved = artistBaseRule.get("xpathAbsolute");
        return XpathUtils.resolveXpathString(baseXpathUnresolved, resolveXpathInfos);
    }
    else {
        return "";
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void setupMappingRule() throws Exception {
    this.mappingRules = MappingRules.getInstance();
    String mappingRulesFilePath = PropertyHandler.getInstance().getValue("Mumdag.mappingRulesFilePath");
    String mappingRulesType = PropertyHandler.getInstance().getValue("Mumdag.mappingRulesType");
    this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
public Document getXmlDoc() {
    return xmlDoc;
}

//-----------------------------------------------------------------------------

public Document getTemplateXmlDoc() {
    return templateXmlDoc;
}

//-----------------------------------------------------------------------------

}
