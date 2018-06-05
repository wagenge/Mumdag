package org.mumdag.model;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.mumdag.model.index.Artist;
import org.mumdag.model.index.MusicIndex;
//import org.mumdag.utils.PropertyHandler;

//import java.util.HashMap;

//-----------------------------------------------------------------------------

public class MumdagModel {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(org.mumdag.model.MumdagModel.class);

private static MumdagModel instance = null;

//private OutputXmlDoc mmdgModel;
private MusicIndex mi = MusicIndex.getInstance();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
private MumdagModel() {
    //this.mmdgModel = new OutputXmlDoc(PropertyHandler.getInstance().getValue("OutputXmlDoc.artistMinimalPath"));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized MumdagModel getInstance() {
    if (instance == null) {
        instance = new MumdagModel();
    }
    return instance;
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
//private MumdagModel(String filePath) throws Exception {
//    //this.mmdgModel = new OutputXmlDoc(filePath);
//}

//=============================================================================
/*
 * 	METHODS FOR WRITING ARTIST INFO (public)
 */
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistUniqueId(HashMap<String, String> insertInfo, String origArtistId, String copyRule, String origScraperName, String scraperName, Boolean writeUnId) throws Exception {
    if(writeUnId) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        mi.setArtistId(idAttrName, (String)insertInfo.get("unid"));
    }
    Artist artist = (Artist)mi.getCurrentEntry();
    artist.getArtistXml().writeArtistUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeUnId);
    //mmdgModel.writeArtistUniqueId(insertInfo, origArtistId, copyRule, origScraperName, scraperName, writeUnId);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeArtistName(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistAlias(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    writeArtistName(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistTypeAndGender(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    mmdgModel.writeArtistTypeAndGender(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistArea(HashMap<String, Object> insertInfo, String origArtistId, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistArea(insertInfo, origArtistId, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistPeriod(HashMap<String, Object> insertInfo, String periodType, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistPeriod(insertInfo, periodType, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCredit(String artistCreditId, String origAristId, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistCredit(artistCreditId, origAristId, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistCreditUniqueId(HashMap<String, Object> insertInfo, String origArtistId, String copyRule, String scraperName, Boolean writeUnId) throws Exception {
    //mmdgModel.writeArtistCreditUniqueId(insertInfo, origArtistId, copyRule, scraperName, writeUnId);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistCreditName(HashMap<String, Object> insertInfo, String origArtistId, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistCreditName(insertInfo, origArtistId, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistCreditPeriod(HashMap<String, Object> insertInfo, String periodType, String origArtistId, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistCreditPeriod(insertInfo, periodType, origArtistId, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistCreditRole(HashMap<String, Object> insertInfo, String origArtistId, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistCreditRole(insertInfo, origArtistId, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistUrls(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeUrls(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistTag(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistTag(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistRating(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistRating(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistDisambiguation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistDisambiguation(insertInfo, copyRule, scraperName);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistAnnotation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    //mmdgModel.writeArtistAnnotation(insertInfo, copyRule, scraperName);
}
*/

//=============================================================================
/*
 * 	METHODS FOR WRITING RELEASE GROUP INFO (public)
 */
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeReleaseGroupUniqueId(HashMap<String, Object> insertInfo, String copyRule, String origScraperName, String scraperName, Boolean writeUnId) throws Exception {
    if(writeUnId) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        mi.setReleaseGroupId(idAttrName, (String)insertInfo.get("unid"));
    }
    //mmdgModel.writeReleaseGroupUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeUnId);
}
*/
//-----------------------------------------------------------------------------


//=============================================================================
/*
 * 	METHODS FOR WRITING RELEASE INFO (public)
 */
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeReleaseUniqueId(HashMap<String, Object> insertInfo, String copyRule, String origScraperName, String scraperName, Boolean writeUnId) throws Exception {
    if(writeUnId) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        mi.setReleaseId(idAttrName, (String)insertInfo.get("unid"));
    }
    //mmdgModel.writeReleaseUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeUnId);
}
*/
//-----------------------------------------------------------------------------


//=============================================================================
/*
 * 	METHODS FOR WRITING RELEASE INFO (public)
 */
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeMediumDiscNo(HashMap<String, Object> insertInfo, String copyRule, String origScraperName, String scraperName, Boolean writeUnId) throws Exception {
    if(writeUnId) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        mi.setMediumId(idAttrName, "discNo", (String)insertInfo.get("discNo"));
    }
    //mmdgModel.writeMediumDiscNo(insertInfo, copyRule, origScraperName, scraperName, writeUnId);
}
*/
//-----------------------------------------------------------------------------


//=============================================================================
/*
 * 	GENERAL MODEL METHODS (public)
 */
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeOutputDocToFile(String filePath, String fileName) {
    //mmdgModel.writeOutputDocToFile(filePath, fileName);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//public String getOutputDocAsString() {
//    return mmdgModel.getOutputDocAsString();
//}
*/


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//public OutputXmlDoc getMmdgModel() {
//    return mmdgModel;
//}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//public void setMmdgModel(OutputXmlDoc mmdgModel) {
//    this.mmdgModel = mmdgModel;
//}

//-----------------------------------------------------------------------------

}
