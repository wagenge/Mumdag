package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mumdag.core.MappingRules;
import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class MusicBrainzScraper {

//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static MusicBrainzScraper instance = null;
private static final Logger log = LogManager.getLogger(MusicBrainzScraper.class);
private String scraperId;
private String scraperName;
private MappingRules mappingRules =  MappingRules.getInstance();
private MusicBrainzScraperReleaseGroup mubzReleaseGroup = new MusicBrainzScraperReleaseGroup();
private MusicBrainzScraperArtist mubzArtist = new MusicBrainzScraperArtist();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (private, with public getInstance methods)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private MusicBrainzScraper() {
	this.scraperName = "MusicBrainz";

	//load MusicBrainz specific properties
	String mubzPropFile = "./src/main/resources/configFiles/MusicBrainz.properties";
	try {
		PropertyHandler.getInstance().addPropertiesFromFile(mubzPropFile, "mubz");
		this.scraperId = PropertyHandler.getInstance().getValue(this.scraperName+".Scraper.id");
		String mappingRulesFilePath = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesFilePath");
		String mappingRulesType = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesType");
		this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
	} catch (Exception ex) {
		log.error("could not load property file {}\nError: {}", mubzPropFile, ex.getMessage());
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private MusicBrainzScraper(String mubzPropFile) {
	this.scraperName = "MusicBrainz";

	//load MusicBrainz specific properties
	try {
		PropertyHandler.getInstance().addPropertiesFromFile(mubzPropFile, "mubz");
		this.scraperId = PropertyHandler.getInstance().getValue(this.scraperName+".Scraper.id");
		String mappingRulesFilePath = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesFilePath");
		String mappingRulesType = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesType");
		this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
	} catch (Exception ex) {
		log.error("could not load property file {}\nError: {}", mubzPropFile, ex.getMessage());
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized MusicBrainzScraper getInstance() {
	if (instance == null) {
		instance = new MusicBrainzScraper();
	}
	return instance;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized MusicBrainzScraper getInstance(String mubzPropFile) {
	if (instance == null) {
		instance = new MusicBrainzScraper(mubzPropFile);
	}
	return instance;
}


//=============================================================================	
/*
* 	SCRAPER METHODS FOR ARTIST (public)
*/
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
String requestArtistInfo() {
    return mubzArtist.requestArtistInfo();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok 
void writeArtistInfo(Object[] input) {
	writeArtistName(input);
	writeArtistAlias(input);
	writeArtistTypeAndGender(input);
    writeArtistPlace(input);
    writeArtistDate(input);
    writeArtistTags(input);
    writeArtistUrls(input);
	writeArtistRating(input);
	writeArtistIpiIsni(input);
    writeArtistCredits(input);
    writeArtistDisambiguation(input);
	writeArtistAnnotation(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistName(Object[] input) {
    mubzArtist.writeArtistName(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistAlias(Object[] input) {
    mubzArtist.writeArtistAlias(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistTypeAndGender(Object[] input) {
    mubzArtist.writeArtistTypeAndGender(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistPlace(Object[] input) {
    mubzArtist.writeArtistPlaceList(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistDate(Object[] input) {
    mubzArtist.writeArtistDate(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistTags(Object[] input) {
    mubzArtist.writeArtistTags(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistUrls(Object[] input) {
    mubzArtist.writeArtistUrls(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistRating(Object[] input) {
    mubzArtist.writeArtistRating(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistIpiIsni(Object[] input) {
    mubzArtist.writeArtistIpiIsni(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistCredits(Object[] input) {
    mubzArtist.writeArtistCredits(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistDisambiguation(Object[] input) {
    mubzArtist.writeArtistDisambiguation(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeArtistAnnotation(Object[] input) {
    mubzArtist.writeArtistAnnotation(input);
}


//=============================================================================
/*
 * 	SCRAPER METHODS FOR RELEASE GROUP (package-private)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupInfo(Object[] input) {
    writeReleaseGroupTitle(input);
    writeReleaseGroupAlias(input);
    writeReleaseGroupType(input);
    writeReleaseGroupTypeFromRels(input);
    writeReleaseGroupDate(input);
    writeReleaseGroupTags(input);
    writeReleaseGroupUrls(input);
    writeReleaseGroupRating(input);
    writeReleaseGroupArtistCredits(input);
    writeReleaseGroupDisambiguation(input);
    writeReleaseGroupAnnotation(input);
}

//-----------------------------------------------------------------------------



//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
String requestReleaseGroupInfo() {
	return mubzReleaseGroup.requestReleaseGroupInfo();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupTitle(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupTitle(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupAlias(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupAlias(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupType(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupType(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupTypeFromRels(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupTypeFromRels(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupDate(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupDate(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupTags(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupTags(input);
}


//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupUrls(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupUrls(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupRating(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupRating(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupArtistCredits(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupArtistCredits(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupDisambiguation(Object[] input) {
    mubzReleaseGroup.writeReleaseGroupDisambiguation(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeReleaseGroupAnnotation(Object[] input) {
	mubzReleaseGroup.writeReleaseGroupAnnotation(input);
}

//-----------------------------------------------------------------------------

}