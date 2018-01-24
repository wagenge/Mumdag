package org.mumdag.model;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;

import java.util.HashMap;

//-----------------------------------------------------------------------------

public class MumdagModel {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(org.mumdag.model.MumdagModel.class);

private OutputXmlDoc mmdgModel;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			ok
public MumdagModel() throws Exception {
    this.mmdgModel = new OutputXmlDoc(PropertyHandler.getInstance().getValue("OutputXmlDoc.templatePath"));
    this.mmdgModel.createOutputXmlDoc();
}

//=============================================================================
/*
 * 	METHODS FOR WRITING ARTIST INFO (public)
 */

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistUniqueId(HashMap<String, Object> insertInfo, String origArtistId, String copyRule, String origScraperName, String scraperName) throws Exception {
    mmdgModel.writeArtistUniqueId(insertInfo, origArtistId, copyRule, origScraperName, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeArtistName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistAlias(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    writeArtistName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistTypeAndGender(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeArtistTypeAndGender(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistPeriod(HashMap<String, Object> insertInfo, String periodType, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeArtistPeriod(insertInfo, periodType, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistUrls(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeUrls(insertInfo, copyRule, scraperName);
}


//=============================================================================
/*
 * 	GENERAL MODEL METHODS (public)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeOutputDocToFile(String filePath, String fileName) {
    mmdgModel.writeOutputDocToFile(filePath, fileName);
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public OutputXmlDoc getMmdgModel() {
    return mmdgModel;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setMmdgModel(OutputXmlDoc mmdgModel) {
    this.mmdgModel = mmdgModel;
}

//-----------------------------------------------------------------------------

}
