package org.mumdag.model.xml;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
import org.mumdag.utils.XpathUtils;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public class ReleaseGroupXml extends XmlDoc{

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(ReleaseGroupXml.class);


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseGroupXml(String xmlFilePath, String templateXmlFilePath) {
    super(xmlFilePath, templateXmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseGroupXml(String xmlFilePath) {
    super(xmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseGroupXml(Document xmlDoc, Document templateXmlDoc) {
    super(xmlDoc, templateXmlDoc);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseGroupXml(Document xmlDoc) {
    super(xmlDoc);
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING RELEASEGROUP BLOCK (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public String getReleaseGroupWSURL(String rgidAttrName, String srcAttrName, String srcAttrValue) throws Exception {
    String releaseGroupWSUrlXpathString = XpathUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.xpath") +
                    PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.xpath.releaseGroupUniqueId") +
                    PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.xpath.sourceWSUrl"),
            "_rgid_::", "_unid_::@"+rgidAttrName, "_src_::@"+srcAttrName+"='"+srcAttrValue+"'");
    log.info("releaseGroupWSUrlXpathString='{}'", releaseGroupWSUrlXpathString);
    return XmlUtils.getNodeTextByXPath(getXmlDoc(), releaseGroupWSUrlXpathString);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    String origScraperId = PropertyHandler.getInstance().getValue(origScraperName + ".Scraper.id");
    //String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("placeholder", "_rgid_");
    resolveInfoMap.put("propSubSection", "ReleaseGroup");
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    if(writeNodeAttributes.equals("ALL_ATTRIBUTES") || writeNodeAttributes.equals("ONLY_UNID")) {
        writeUniqueId(insertInfo, resolveInfoMap, "ReleaseGroup.UniqueId", copyRule+"UnId", origScraperId, scraperName, "", writeNodeAttributes);
    }
    writeUniqueIdInfos(insertInfo, resolveInfoMap, "ReleaseGroup.UniqueId", copyRule, origScraperId, scraperName);
}


//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeNameInfo(insertInfo, resolveInfoList, "ReleaseGroup.Name", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupType(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeTypeInfo(insertInfo, resolveInfoList, "ReleaseGroup.Type", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupDate(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("dateType", insertInfo.get("type"));
    resolveInfoMap.put("resolveInfo", resolveInfoList);
    writeDateInfo(insertInfo, resolveInfoMap, "ReleaseGroup.Date", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupTag(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeTagInfo(insertInfo, resolveInfoList, "ReleaseGroup.Tag", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupUrls(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeUrlInfo(insertInfo, resolveInfoList, "ReleaseGroup.Url", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupRating(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeRatingInfo(insertInfo, resolveInfoList, "ReleaseGroup.Rating", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCredit(String releaseGroupCreditId, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeArtistCreditNode(releaseGroupCreditId, resolveInfoList, "ReleaseGroup.CreditNode", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditUniqueId(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    String scraperId = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = insertInfo.get("unid");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_acid_::"+idAttrName+"="+idAttrValue);

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("placeholder", "_arid_");
    resolveInfoMap.put("propSubSection", "Artist");
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    writeUniqueIdInfos(insertInfo, resolveInfoMap, "Credit.UniqueId", copyRule, scraperId, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_acid_::"+idAttrName+"="+idAttrValue);
    writeNameInfo(insertInfo, resolveInfoList, "Credit.Name", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_acid_::"+idAttrName+"="+idAttrValue);

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("dateType", dateType);
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    writeDateInfo(insertInfo, resolveInfoMap, "Credit.Date", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditRole(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    String idAttrValue = (String)insertInfo.get("unid");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_acid_::"+idAttrName+"="+idAttrValue);
    writeRoleInfo(insertInfo, resolveInfoList, "Credit.Role", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupDisambiguation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeDisambiguationInfo(insertInfo, resolveInfoList, "ReleaseGroup.Disambiguation", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writReleaseGroupAnnotation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rgid_::");
    writeAnnotationInfo(insertInfo, resolveInfoList, "ReleaseGroup.Annotation", copyRule, scraperName);
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
public Document getXmlDoc() {
    return super.getXmlDoc();
}

//-----------------------------------------------------------------------------

}
