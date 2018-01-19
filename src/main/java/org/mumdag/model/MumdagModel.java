package org.mumdag.model;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.MappingRules;
import org.mumdag.utils.PropertyHandler;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public class MumdagModel {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(org.mumdag.model.MumdagModel.class);

private OutputXmlDoc mmdgModelXml;
private MappingRules mappingRules;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			ok
public MumdagModel() throws Exception {
    this.mmdgModelXml = new OutputXmlDoc(PropertyHandler.getInstance().getValue("OutputXmlDoc.templatePath"));
    this.mappingRules = MappingRules.getInstance();
}

//=============================================================================
/*
 * 	METHODS FOR WRITING ARTIST INFO (public)
 */
public void writeArtistName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
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
    String targetRule = "Artist.Name";

    //if the name type is set in the additionalInfo list, take it from these list
    List<String> additionalInfo = new ArrayList<>();
    if(insertInfo.containsKey("additionalInfo")) {
        additionalInfo = (List<String>)insertInfo.get("additionalInfo");
        for(String nameInfoStr: additionalInfo) {
            if(nameInfoStr.contains("type=")) {
                String[] nameTypeInfos = nameInfoStr.split("=");
                ntypeAttrValue = nameTypeInfos[1];
            }
        }
    }

    // define the behavior of the operation depending on the state of the information to be inserted
    HashMap<OutputXmlDoc.NodeStatus, OutputXmlDoc.NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(scraperId, copyRule);

    //prepare information to resolve the xpaths
    HashMap<String, String> resolveMap = mmdgModelXml.createResolveMap("_idAttr_::"+idAttrName+"="+idAttrValue,
                                                                        "_srcAttr_::"+srcAttrName+"="+srcAttrValue,
                                                                        "_ntypeAttr_::"+ntypeAttrName+"="+ntypeAttrValue,
                                                                        "_arname_::"+arNameValue);

    //resolve the base for Artist)
    HashMap<String, String> resolveBaseMap = mmdgModelXml.createResolveMap("_arid_::"+idAttrName+"="+idAttrValue);
    String resolvedBase = mmdgModelXml.getResolvedBase("Artist", resolveBaseMap);

    //add idAttr, srcAttr and ntypeAttr to additionalInfo list
    additionalInfo.add(resolveMap.get("_idAttr_"));
    additionalInfo.add(resolveMap.get("_srcAttr_"));
    additionalInfo.add(resolveMap.get("_ntypeAttr_"));

    //create resolveInfo map, including tragetRule, copyBehaivor, resolveMap and resolvedBase
    HashMap<String, Object> resolveInfo = new HashMap<>();
    resolveInfo.put("targetRule", targetRule);
    resolveInfo.put("copyBehavior", copyBehavior);
    resolveInfo.put("resolveMap", resolveMap);
    resolveInfo.put("resolvedBase", resolvedBase);

    mmdgModelXml.writeNameInfos(arNameValue, (String)insertInfo.get("sortName"), additionalInfo, resolveInfo);
}

//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public OutputXmlDoc getMmdgModelXml() {
    return mmdgModelXml;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public MappingRules getMappingRules() {
    return mappingRules;
}

//-----------------------------------------------------------------------------

}
