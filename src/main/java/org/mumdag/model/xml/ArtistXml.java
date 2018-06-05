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

public class ArtistXml extends XmlDoc {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(ArtistXml.class);


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ArtistXml(String xmlFilePath, String templateXmlFilePath) {
    super(xmlFilePath, templateXmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ArtistXml(String xmlFilePath) {
    super(xmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ArtistXml(Document xmlDoc, Document templateXmlDoc) {
    super(xmlDoc, templateXmlDoc);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ArtistXml(Document xmlDoc) {
    super(xmlDoc);
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING ARTIST BLOCK (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public String getArtistWSURL(String aridAttrName, String srcAttrName, String srcAttrValue) throws Exception {
	String artistWSUrlXpathString = XpathUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ArtistXmlModel.xpath") +
            PropertyHandler.getInstance().getValue("ArtistXmlModel.xpath.artistUniqueId") +
            PropertyHandler.getInstance().getValue("ArtistXmlModel.xpath.sourceWSUrl"),
            "_arid_::", "_unid_::@"+aridAttrName, "_src_::@"+srcAttrName+"='"+srcAttrValue+"'");
	log.info("artistWSUrlXpathString='{}'", artistWSUrlXpathString);
	return XmlUtils.getNodeTextByXPath(getXmlDoc(), artistWSUrlXpathString);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    String origScraperId = PropertyHandler.getInstance().getValue(origScraperName + ".Scraper.id");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("placeholder", "_arid_");
    resolveInfoMap.put("propSubSection", "Artist");
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    if(writeNodeAttributes.equals("ALL_ATTRIBUTES") || writeNodeAttributes.equals("ONLY_UNID")) {
        writeUniqueId(insertInfo, resolveInfoMap, "Artist.UniqueId", copyRule+"UnId", origScraperId, scraperName, "", writeNodeAttributes);
    }
    writeUniqueIdInfos(insertInfo, resolveInfoMap, "Artist.UniqueId", copyRule, origScraperId, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeNameInfo(insertInfo, resolveInfoList, "Artist.Name", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistTypeAndGender(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeTypeInfo(insertInfo, resolveInfoList, "Artist.Type", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistPlace(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writePlaceInfo(insertInfo, resolveInfoList, "Artist.Place", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("dateType", dateType);
    resolveInfoMap.put("resolveInfo", resolveInfoList);
    writeDateInfo(insertInfo, resolveInfoMap, "Artist.Date", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistTag(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeTagInfo(insertInfo, resolveInfoList, "Artist.Tag", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistUrls(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeUrlInfo(insertInfo, resolveInfoList, "Artist.Url", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistRating(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeRatingInfo(insertInfo, resolveInfoList, "Artist.Rating", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCredit(String aristCreditId, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeArtistCreditNode(aristCreditId, resolveInfoList, "Artist.CreditNode", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCreditUniqueId(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
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
public void writeArtistCreditName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
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
public void writeArtistCreditDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
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
public void writeArtistCreditRole(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
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
public void writeArtistDisambiguation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeDisambiguationInfo(insertInfo, resolveInfoList, "Artist.Disambiguation", copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistAnnotation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_arid_::");
    writeAnnotationInfo(insertInfo, resolveInfoList, "Artist.Annotation", copyRule, scraperName);
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
