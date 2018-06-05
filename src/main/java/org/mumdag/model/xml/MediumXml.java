package org.mumdag.model.xml;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.index.Medium;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public class MediumXml extends XmlDoc {


//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(MediumXml.class);

//private Document xmlDoc = null;
//private Document templateXmlDoc = null;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(String xmlFilePath, String templateXmlFilePath) {
    try {
        this.xmlDoc = XmlUtils.createXmlDoc(xmlFilePath);
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
    } catch (Exception ex) {
        log.error("Could not create Medium Data Model! \nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(String xmlFilePath) {
    try {
        String templateXmlFilePath = PropertyHandler.getInstance().getValue("MediumXmlModel.templatePath");
        this.xmlDoc = XmlUtils.createXmlDoc(xmlFilePath);
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
    } catch (Exception ex) {
        log.error("Could not create Medium Data Model! \nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(Document xmlDoc, Document templateXmlDoc) {
    this.xmlDoc = xmlDoc;
    this.templateXmlDoc = templateXmlDoc;
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(Document xmlDoc) {
    this.xmlDoc = xmlDoc;
    try {
        String templateXmlFilePath = PropertyHandler.getInstance().getValue("MediumXmlModel.templatePath");
        this.templateXmlDoc = XmlUtils.createXmlDoc(templateXmlFilePath);
    } catch (Exception ex) {
        log.error("Could not create template for Medium Data Model! \nError: {}", ex.getMessage());
    }
}
*/

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(String xmlFilePath, String templateXmlFilePath) {
    super(xmlFilePath, templateXmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(String xmlFilePath) {
    super(xmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(Document xmlDoc, Document templateXmlDoc) {
    super(xmlDoc, templateXmlDoc);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public MediumXml(Document xmlDoc) {
    super(xmlDoc);
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING MEDIUM BLOCK (public)
 */

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public void writeMediumDiscNo(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    String origScraperId = PropertyHandler.getInstance().getValue(origScraperName + ".Scraper.id");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_mid_::");

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    if(writeNodeAttributes.equals("ALL_ATTRIBUTES") || writeNodeAttributes.equals("ONLY_UNID")) {
        insertInfo.put("unid", insertInfo.get("discNo"));
        insertInfo.remove("discNo");
        writeUniqueId(insertInfo, resolveInfoMap, "Medium.UnId", copyRule, origScraperId, scraperName, "discNo", writeNodeAttributes);
    }
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
