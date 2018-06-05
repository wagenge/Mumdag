package org.mumdag.model.xml;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public class ReleaseXml extends XmlDoc {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(ReleaseXml.class);


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseXml(String xmlFilePath, String templateXmlFilePath) {
    super(xmlFilePath, templateXmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseXml(String xmlFilePath) {
    super(xmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseXml(Document xmlDoc, Document templateXmlDoc) {
    super(xmlDoc, templateXmlDoc);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public ReleaseXml(Document xmlDoc) {
    super(xmlDoc);
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING RELEASE BLOCK (public)
 */

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    String origScraperId = PropertyHandler.getInstance().getValue(origScraperName + ".Scraper.id");
    String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_rid_::");

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("placeholder", "_rid_");
    resolveInfoMap.put("propSubSection", "Release");
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    if(writeNodeAttributes.equals("ALL_ATTRIBUTES") || writeNodeAttributes.equals("ONLY_UNID")) {
        writeUniqueId(insertInfo, resolveInfoMap, "Release.UniqueId", copyRule+"UnId", origScraperId, scraperName, "", writeNodeAttributes);
        resolveInfoList.remove("_rid_::");
        String releaseId = insertInfo.get("unid");
        resolveInfoList.add("_rid_::"+idAttrName+"="+releaseId);
        resolveInfoMap.replace("resolveInfo", resolveInfoList);
    }
    writeUniqueIdInfos(insertInfo, resolveInfoMap, "Release.UniqueId", copyRule, origScraperId, scraperName);
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
