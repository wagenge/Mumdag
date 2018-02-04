package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mumdag.core.MappingRules;
import org.mumdag.scraper.utils.ScraperHttpClient;
import org.mumdag.model.MumdagModel;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;

//-----------------------------------------------------------------------------

public class MusicBrainzScraper {

//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */	
private static final Logger log = LogManager.getLogger(MusicBrainzScraper.class);
	
private MumdagModel mdm;
private String artistName;
private String artistPath;
private String scraperId;
private String scraperName;
private ScraperHttpClient scraperHttpClient;
private MappingRules mappingRules;
private HashMap<String, String> urlToUniqueIdsRules;
	

//=============================================================================	
/*
* 	CONSTRUCTOR METHODS (public)
*/
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public MusicBrainzScraper(String artistName, String artistPath, MumdagModel mdm) throws Exception {
	//load MusicBrainz specific properties
	PropertyHandler.getInstance().addPropertiesFromFile("./src/main/resources/configFiles/MusicBrainz.properties", "mubz");
	
	this.artistName = artistName;
	this.artistPath = artistPath;
	this.mdm = mdm;
	this.scraperHttpClient = ScraperHttpClient.getInstance();
	this.scraperId = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.id");
	this.scraperName = "MusicBrainz";
    String mappingRulesFilePath = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesFilePath");
    String mappingRulesType = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesType");
	this.mappingRules = MappingRules.getInstance();
	this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
	this.urlToUniqueIdsRules = new HashMap<>();
}


//=============================================================================	
/*
* 	SCRAPER METHODS (public)	
*/
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String requestMusicBrainzArtistInfo() throws Exception {
	return requestScraper(mdm.getArtistWSURL(this.mbIdAttrName));
}
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getMusicBrainzArtistId(Object[] input) {
	String artistInfoXml = (String)input[0];
	
	HashMap<String, String> artistIdRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistId");
	String artistIdXpath = XmlUtils.resolveXpathString(artistIdRule.get("xpathAbsolute"), "_arid_::id");
	artistIdRule.put("xpathAbsolute", artistIdXpath);
	List<String> artistIdList = getInfoFromXml(artistInfoXml, artistIdRule);
	return artistIdList.get(0);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok 
public void writeMusicBrainzArtistInfo(Object[] input) throws Exception {
	writeMusicBrainzArtistName(input);
	writeMusicBrainzArtistAlias(input);
	writeMusicBrainzArtistTypeAndGender(input);
	writeMusicBrainzArtistPeriod(input);
	writeMusicBrainzArtistAreaList(input);
	writeMusicBrainzArtistUrls(input);
	writeMusicBrainzArtistTags(input);
	writeMusicBrainzArtistRating(input);
	writeMusicBrainzArtistIpiIsni(input);
	writeMusicBrainzArtistAnnotation(input);
	writeMusicBrainzArtistDisambiguation(input);
	writeMusicBrainzArtistCredits(input);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistName(Object[] input) {
    if(input == null || input.length < 2 || input[0] == null || input[1] == null) {
        log.error("input arguments not correct. expecting input[0] = artistId and input[1]=inputXml");
        return;
    }

    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
	String artistInfoXml = (String)input[1];

	if(StringUtils.isEmpty(artistId) || StringUtils.isEmpty(artistInfoXml)) {
        log.error("artistId or inputXml are empty");
        return;
    }

	//put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);
    insertInfo.put("nameType", "Name");
	
    //get ArtistName from MUBZ
    try {
        HashMap<String, String> artistNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistName");
        String artistNameXpath = XmlUtils.resolveXpathString(artistNameRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
        artistNameRule.put("xpathAbsolute", artistNameXpath);
        List<String> artistNameList = getInfoFromXml(artistInfoXml, artistNameRule);
        insertInfo.put("name", artistNameList.get(0));
    }
    catch (Exception ex) {
        log.error("Error while resolving artist name from musicbrainz response!\nError:", ex.getMessage());
        return;
    }

	//get ArtistSortName from MUBZ
	HashMap<String, String> artistSortNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistSortName");
	String artistSortNameXpath = XmlUtils.resolveXpathString(artistSortNameRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistSortNameRule.put("xpathAbsolute", artistSortNameXpath);
	List<String> artistSortNameList = getInfoFromXml(artistInfoXml, artistSortNameRule);
    insertInfo.put("sortName", artistSortNameList.get(0));
	
	//put ArtistName to MMDG
    try {
        mdm.writeArtistName(insertInfo, "ArtistName", this.scraperName);
    }
    catch (Exception ex) {
        log.error("Error while executing 'writeArtistName'!\nError:", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAlias(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
	insertInfo.put("unid", artistId);
	insertInfo.put("nameType", "Alias");

	//get ArtistAliasName plus according Attributes from MUBZ
	HashMap<String, String> artistAliasNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAliasName");
	HashMap<String, String> artistAliasAttrRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAliasAttributes");
	String artistAliasNameXpath = XmlUtils.resolveXpathString(artistAliasNameRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistAliasNameRule.put("xpathAbsolute", artistAliasNameXpath);
	String xpathAbsoluteUnresolved = artistAliasAttrRule.get("xpathAbsolute");
	List<String> artistAliasNameList = getInfoFromXml(artistInfoXml, artistAliasNameRule);
	for(String artistAliasName : artistAliasNameList) {
		insertInfo.put("name", artistAliasName);
		String artistAliasAttrXpath = XmlUtils.resolveXpathString(xpathAbsoluteUnresolved, "_arid_::@id='"+artistId+"'", "_alname_::"+artistAliasName);
		artistAliasAttrRule.put("xpathAbsolute", artistAliasAttrXpath);
		List<String> artistAliasAttributes = getInfoFromXml(artistInfoXml, artistAliasAttrRule);
		log.info("alias name '{}' and attributes: {}",  artistAliasName, String.join(", ", artistAliasAttributes));
		
		//preparing artist'alias attributes 
		List<String> artistAliasInfo = new ArrayList<>(artistAliasAttributes);
        insertInfo.put("additionalInfo", artistAliasInfo);

		//put ArtistName to MMDG
		mdm.writeArtistAlias(insertInfo, "ArtistAliasName", this.scraperName);
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistTypeAndGender(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);
	
	//get ArtistType from MUBZ
	HashMap<String, String> artistTypeRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistType");
	String artistTypeXpath = XmlUtils.resolveXpathString(artistTypeRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'", "_artype_::type");
	artistTypeRule.put("xpathAbsolute", artistTypeXpath);
	List<String> artistTypeList = getInfoFromXml(artistInfoXml, artistTypeRule);
	String artistType = artistTypeList.get(0);
    insertInfo.put("type", artistType);

	if(artistType.equals("Person")) {
		//get ArtistGender from MUBZ
		HashMap<String, String> artistGenderRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistGender");
		String artistGenderXpath = XmlUtils.resolveXpathString(artistGenderRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
		artistGenderRule.put("xpathAbsolute", artistGenderXpath);
		List<String> artistGenderList = getInfoFromXml(artistInfoXml, artistGenderRule);

		//preparing artist type infos (ArtistType)
        insertInfo.put("gender", artistGenderList.get(0));
	}

	//put ArtistType to MMDG
	mdm.writeArtistTypeAndGender(insertInfo, "ArtistType", this.scraperName);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistPeriod(Object[] input) throws Exception {
// GENERAL THINGS ...
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);

	//get ArtistType from MUBZ
	HashMap<String, String> artistTypeRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistType");
	String artistTypeXpath = XmlUtils.resolveXpathString(artistTypeRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'", "_artype_::type");
	artistTypeRule.put("xpathAbsolute", artistTypeXpath);
	List<String> artistTypeList = getInfoFromXml(artistInfoXml, artistTypeRule);
    insertInfo.put("type", artistTypeList.get(0));

//EXTRACT AND WRITE PERIOD BEGIN
	//get PeriodBegin from MUBZ
	HashMap<String, String> artistLifeSpanBeginRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistLifeSpanBegin");
	String artistLifeSpanBeginXpath = XmlUtils.resolveXpathString(artistLifeSpanBeginRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistLifeSpanBeginRule.put("xpathAbsolute", artistLifeSpanBeginXpath);
	List<String> artistLifeSpanBeginList = getInfoFromXml(artistInfoXml, artistLifeSpanBeginRule);
	if(artistLifeSpanBeginList.size() > 0) {
        insertInfo.put("period", artistLifeSpanBeginList.get(0));
        mdm.writeArtistPeriod(insertInfo, "begin","ArtistPeriod", this.scraperName);
	}
	
//EXTRACT AND WRITE PERIOD END	
	//get PeriodEnd from MUBZ
	HashMap<String, String> artistLifeSpanEndRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistLifeSpanEnd");
	String artistLifeSpanEndXpath = XmlUtils.resolveXpathString(artistLifeSpanEndRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistLifeSpanEndRule.put("xpathAbsolute", artistLifeSpanEndXpath);
	List<String> artistLifeSpanEndList = getInfoFromXml(artistInfoXml, artistLifeSpanEndRule);
	if(artistLifeSpanEndList.size() > 0) {
        insertInfo.put("period", artistLifeSpanEndList.get(0));
        mdm.writeArtistPeriod(insertInfo, "end", "ArtistPeriod", this.scraperName);
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAreaList(Object[] input) throws Exception {
//GENERAL THINGS ...
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

	//get ArtistType from MUBZ
	HashMap<String, String> artistTypeRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistType");
	String artistTypeXpath = XmlUtils.resolveXpathString(artistTypeRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'", "_artype_::type");
	artistTypeRule.put("xpathAbsolute", artistTypeXpath);
	List<String> artistTypeList = getInfoFromXml(artistInfoXml, artistTypeRule);
	String artistType = artistTypeList.get(0);
	
//EXTRACT AREA BEGIN
	//get area begin id from MUBZartistLifeSpanEndRule
	HashMap<String, String> artistAreaBeginIdRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAreaBeginId");
	String artistAreaBeginIdXpath = XmlUtils.resolveXpathString(artistAreaBeginIdRule.get("xpathAbsolute"), "_areaid_::id");
	artistAreaBeginIdRule.put("xpathAbsolute", artistAreaBeginIdXpath);
	List<String> artistAreaBeginIdList = getInfoFromXml(artistInfoXml, artistAreaBeginIdRule);
	String artistAreaBeginId = "";
	if(artistAreaBeginIdList.size() > 0) {
		artistAreaBeginId = artistAreaBeginIdList.get(0);
	}
	log.info("ArtistAreaBeginId ='{}'", artistAreaBeginId);
	
//EXTRACT AREA END
	//get area end id from MUBZartistLifeSpanEndRule
	HashMap<String, String> artistAreaEndIdRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAreaEndId");
	String artistAreaEndIdXpath = XmlUtils.resolveXpathString(artistAreaEndIdRule.get("xpathAbsolute"), "_areaid_::id");
	artistAreaEndIdRule.put("xpathAbsolute", artistAreaEndIdXpath);
	List<String> artistAreaEndIdList = getInfoFromXml(artistInfoXml, artistAreaEndIdRule);
	String artistAreaEndId = "";
	if(artistAreaEndIdList.size() > 0) {
		artistAreaEndId = artistAreaEndIdList.get(0);
	}
	log.info("ArtistAreaEndId ='{}'", artistAreaEndId);

//RESOLVE AREA CHAINS BEGIN/END	
	HashMap<Integer, HashMap<String, Object>> areaInfos;
	areaInfos = requestMusicBrainAreaInfo(artistAreaBeginId, artistType, true);
	areaInfos.putAll(requestMusicBrainAreaInfo(artistAreaEndId, artistType, false));
	
	for (HashMap<String, Object> insertInfo : areaInfos.values()) {
		mdm.writeArtistArea(insertInfo, artistId,"ArtistArea", this.scraperName);
	}
	
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistUrls(Object[] input) throws Exception {
//GENERAL THINGS ...
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

	//read UniqueId-Url-Filter
	String ruleFileName = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Artist.urlToUniqueIdFilterFile");
	readUrlToUniqueIdFilter(ruleFileName);

//EXTRACT URLs INCLUDING URL TYPE AND WRITE TO OXD
    //get ArtistUrl plus according Url type from MUBZ
    HashMap<String, String> artistUrlRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistUrl");
    HashMap<String, String> artistUrlTypeRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistUrlType");
    String artistUrlTypeXpathUnresolved = artistUrlTypeRule.get("xpathAbsolute");
    List<String> artistUrlList = getInfoFromXml(artistInfoXml, artistUrlRule);
    for(String artistUrl : artistUrlList) {
        String artistUrlTypeXpath = XmlUtils.resolveXpathString(artistUrlTypeXpathUnresolved, "_url_::"+artistUrl);
        artistUrlTypeRule.put("xpathAbsolute", artistUrlTypeXpath);
        List<String> artistUrlTypeList = getInfoFromXml(artistInfoXml, artistUrlTypeRule);
        String artistUrlType = "";
        if(artistUrlTypeList.size() > 0) {
            artistUrlType = artistUrlTypeList.get(0);
        }
        log.info("url: '{}' and type: {}",  artistUrl, artistUrlType);

        //preparing artist url infos
        insertInfo.put("unid", artistId);
        insertInfo.put("url", artistUrl.replaceAll("=", "%3D"));
        insertInfo.put("type", artistUrlType);

        //write the current url
        mdm.writeArtistUrls(insertInfo, "ArtistUrl", this.scraperName);

        //extract the unique id from the current url (if the pattern matches) and write it as unique id
        writeUrlToUniqueId(insertInfo, artistId, "Artist", "ArtistId");
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistTags(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);
	
	//get ArtistTag plus according attribute 'count' from MUBZ
	HashMap<String, String> artistTagsRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistTags");
	HashMap<String, String> artistTagsCountRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistTagsCount");
	String artistTagsCountXpathUnresolved = artistTagsCountRule.get("xpathAbsolute");
	List<String> artistTagsList = getInfoFromXml(artistInfoXml, artistTagsRule);
	for(String artistTag : artistTagsList) {
        insertInfo.put("tag", artistTag);
		
		String artistTagsCountXpath = XmlUtils.resolveXpathString(artistTagsCountXpathUnresolved, "_tag_::"+artistTag);
		artistTagsCountRule.put("xpathAbsolute", artistTagsCountXpath);
		List<String> artistTagCountList = getInfoFromXml(artistInfoXml, artistTagsCountRule);
		String artistTagCount = "";
		if(artistTagCountList.size() > 0) {
			artistTagCount = artistTagCountList.get(0);
			List<String> additionalInfoList = new ArrayList<>();
			additionalInfoList.add("count="+artistTagCount);
            insertInfo.put("additionalInfo", additionalInfoList);
		}
		log.info("tag: '{}' and count: {}",  artistTag, artistTagCount);

        mdm.writeArtistTag(insertInfo, "ArtistTags", this.scraperName);
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistRating(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);

	//get ArtistRating plus according attribute 'votes-count' from MUBZ
	HashMap<String, String> artistRatingRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistRating");
	List<String> artistRatingList = getInfoFromXml(artistInfoXml, artistRatingRule);
    if(artistRatingList.size() > 0) {
        insertInfo.put("rating", artistRatingList.get(0));

        HashMap<String, String> artistRatingVotesCountRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistRatingVotesCount");
        List<String> artistRatingVotesCountList = getInfoFromXml(artistInfoXml, artistRatingVotesCountRule);
        if(artistRatingVotesCountList.size() > 0) {
            List<String> additionalInfoList = new ArrayList<>();
            additionalInfoList.add("votes="+artistRatingVotesCountList.get(0));
            insertInfo.put("additionalInfo", additionalInfoList);
        }

        mdm.writeArtistRating(insertInfo, "ArtistRating", this.scraperName);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeMusicBrainzArtistIpiIsni(Object[] input) throws Exception {
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

	//get ArtistIPIs from MUBZ and put them to MMDG using method 'writeArtistUniqueId'
	HashMap<String, String> artistIpiRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistIPI");
	List<String> artistIpiList = getInfoFromXml(artistInfoXml, artistIpiRule);
	for(String artistIpi : artistIpiList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("unid", artistIpi);
        insertInfo.put("url", "-");
        mdm.writeArtistUniqueId(insertInfo, artistId, "ArtistId", this.scraperName, "IPI");
	}
	
	//get ArtistISNIs from MUBZ and put them to MMDG using method 'writeArtistUniqueId'
	HashMap<String, String> artistIsniRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistISNI");
	List<String> artistIsniList = getInfoFromXml(artistInfoXml, artistIsniRule);
	for(String artistIsni : artistIsniList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("unid", artistIsni);
        insertInfo.put("url", "-");
        mdm.writeArtistUniqueId(insertInfo, artistId, "ArtistId", this.scraperName, "ISNI");
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAnnotation(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);

	//get ArtistAnnotations from MUBZ split them according splitregExp and put them to MMDG
	HashMap<String, String> artistAnnotationRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAnnotation");
	String splitRegEx = artistAnnotationRule.get("splitRegEx");
	List<String> artistAnnotationList = getInfoFromXml(artistInfoXml, artistAnnotationRule);
	for(String annotationsText : artistAnnotationList) {
		for(String artistAnnotation: annotationsText.split(splitRegEx)) {
			log.info("artistAnnotation '{}'",  artistAnnotation);
            insertInfo.put("annotation", artistAnnotation);
            mdm.writeArtistAnnotation(insertInfo, "ArtistAnnotation", this.scraperName);
		}
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistDisambiguation(Object[] input) throws Exception {
    HashMap<String, Object> insertInfo = new HashMap<>();
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];

    //put artistId and nameType to insertInfo
    insertInfo.put("unid", artistId);

	//get ArtistDisambigaution from MUBZ and put them to MMDG
	HashMap<String, String> artistDisambiguationRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistDisambiguation");
	List<String> artistDisambiguationList = getInfoFromXml(artistInfoXml, artistDisambiguationRule);
	for(String artistDisambiguation : artistDisambiguationList) {
		log.info("ArtistDisambiguation '{}'",  artistDisambiguation);
        insertInfo.put("disambiguation", artistDisambiguation);
        mdm.writeArtistDisambiguation(insertInfo, "ArtistDisambiguation", this.scraperName);
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistCredits(Object[] input) throws Exception {
    String artistId = (String)input[0];
    String artistInfoXml = (String)input[1];
	
	//get ArtistCredits from MUBZ and put them to MMDG
	HashMap<String, String> artistCreditIdsRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditIds");
	HashMap<String, String> artistCreditNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditName");
	String artistCreditsNameXpathUnresolved = artistCreditNameRule.get("xpathAbsolute");
	HashMap<String, String> artistCreditSortNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditSortName");
	String artistCreditsSortNameXpathUnresolved = artistCreditSortNameRule.get("xpathAbsolute");
	HashMap<String, String> artistCreditTypeRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditType");
	String artistCreditsTypeXpathUnresolved = artistCreditTypeRule.get("xpathAbsolute");
	HashMap<String, String> artistCreditAttributesRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditAttributes");
	String artistCreditAttributesXpathUnresolved = artistCreditAttributesRule.get("xpathAbsolute");
	HashMap<String, String> artistCreditPeriodsRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistCreditPeriods");
	String artistCreditPeriodsXpathUnresolved = artistCreditPeriodsRule.get("xpathAbsolute");

	List<String> artistCreditIdsList = getInfoFromXml(artistInfoXml, artistCreditIdsRule);
	artistCreditIdsList = artistCreditIdsList.stream().distinct().collect(Collectors.toList());
	for(String artistCreditId : artistCreditIdsList) {
		log.info("artist credit ids {}", artistCreditId);

		//put ArtistCreditNode to MMDG
		mdm.writeArtistCredit(artistCreditId, artistId, "ArtistCreditIds", this.scraperName);

        HashMap<String, Object> insertInfoUniqueId = new HashMap<>();
        insertInfoUniqueId.put("unid", artistCreditId);
        insertInfoUniqueId.put("url", "-");
        mdm.writeArtistCreditUniqueId(insertInfoUniqueId, artistId, "ArtistCreditIds", this.scraperName);

		//get ArtistCreditName
		String artistCreditsNameXpath = XmlUtils.resolveXpathString(artistCreditsNameXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditNameRule.put("xpathAbsolute", artistCreditsNameXpath);
		List<String> artistCreditNameList = getInfoFromXml(artistInfoXml, artistCreditNameRule);
		artistCreditNameList = artistCreditNameList.stream().distinct().collect(Collectors.toList());
		String artistCreditName = artistCreditNameList.get(0);
		log.info("\tname {}", artistCreditName);
		
		//get ArtistCreditSortName
		String artistCreditsSortNameXpath = XmlUtils.resolveXpathString(artistCreditsSortNameXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditSortNameRule.put("xpathAbsolute", artistCreditsSortNameXpath);
		List<String> artistCreditSortNameList = getInfoFromXml(artistInfoXml, artistCreditSortNameRule);
		artistCreditSortNameList = artistCreditSortNameList.stream().distinct().collect(Collectors.toList());
		String artistCreditSortName = artistCreditSortNameList.get(0);
		log.info("\tsort-name {}", artistCreditSortName);
		
        HashMap<String, Object> insertInfoName = new HashMap<>();
        insertInfoName.put("unid", artistCreditId);
        insertInfoName.put("nameType", "Name");
        insertInfoName.put("name", artistCreditName);
        insertInfoName.put("sortName", artistCreditSortName);

        //put ArtistName to MMDG
        mdm.writeArtistCreditName(insertInfoName, artistId, "ArtistCreditName", this.scraperName);

		//get ArtistCreditAttributes (e.g. instruments)
		HashMap<String, Object> insertInfoRoleMember = new HashMap<>();
		insertInfoRoleMember.put("unid", artistCreditId);
		String artistCreditAttributesXpath = XmlUtils.resolveXpathString(artistCreditAttributesXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditAttributesRule.put("xpathAbsolute", artistCreditAttributesXpath);
		List<String> artistCreditAttributesList = getInfoFromXml(artistInfoXml, artistCreditAttributesRule);
		artistCreditAttributesList = artistCreditAttributesList.stream().distinct().collect(Collectors.toList());
		if(artistCreditAttributesList.contains("original")) {
			artistCreditAttributesList.remove("original");
			insertInfoRoleMember.put("original", "true");
		}
		log.info("\t number of attributes {}", artistCreditAttributesList.size());
		for(String instrument  : artistCreditAttributesList) {
			log.info("\t\t{}", instrument);
			HashMap<String, Object> insertInfoRoleInstrument = new HashMap<>();
			insertInfoRoleInstrument.put("unid", artistCreditId);
			insertInfoRoleInstrument.put("role", instrument);
            insertInfoRoleInstrument.put("roleType", "instrument");
			mdm.writeArtistCreditRole(insertInfoRoleInstrument, artistId, "ArtistCreditRole", this.scraperName);
		}
		
		//get ArtistCreditType
		String artistCreditsTypeXpath = XmlUtils.resolveXpathString(artistCreditsTypeXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditTypeRule.put("xpathAbsolute", artistCreditsTypeXpath);
		List<String> artistCreditTypeList = getInfoFromXml(artistInfoXml, artistCreditTypeRule);
		artistCreditTypeList = artistCreditTypeList.stream().distinct().collect(Collectors.toList());
		String membership = artistCreditTypeList.get(0);
		log.info("\ttype {}", membership);
		insertInfoRoleMember.put("role", membership);
        insertInfoRoleMember.put("roleType", "membership");
        mdm.writeArtistCreditRole(insertInfoRoleMember, artistId, "ArtistCreditRole", this.scraperName);

		//get ArtistCreditPeriods
		String artistCreditsPeriodsXpath = XmlUtils.resolveXpathString(artistCreditPeriodsXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditPeriodsRule.put("xpathAbsolute", artistCreditsPeriodsXpath);
		List<String> artistCreditPeriodsList = getInfoFromXml(artistInfoXml, artistCreditPeriodsRule);
		artistCreditPeriodsList = artistCreditPeriodsList.stream().distinct().collect(Collectors.toList());

		HashMap<String, Object> insertInfoPeriod = new HashMap<>();
        insertInfoPeriod.put("unid", artistCreditId);
		for(String artistCreditPeriodsStr : artistCreditPeriodsList) {
			log.info("\tperiod {}", artistCreditPeriodsStr);
			
			String[] artistCreditPeriodsArr = artistCreditPeriodsStr.split(",");
			for(String artistCreditPeriods : artistCreditPeriodsArr) {
				String[] artistCreditPeriodArr = artistCreditPeriods.split("=");
				if(artistCreditPeriodArr.length == 2) {
					String periodType = artistCreditPeriodArr[0];
					String period = artistCreditPeriodArr[1];
                    insertInfoPeriod.put("period", period);
                    mdm.writeArtistCreditPeriod(insertInfoPeriod, periodType, artistId, "ArtistCreditPeriods", this.scraperName);
				}
			}		
		}
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private HashMap<Integer, HashMap<String, Object>> requestMusicBrainAreaInfo(String areaid, String artistType, Boolean begin) throws Exception {
	HashMap<Integer, HashMap<String, Object>> retAreaInfos = new HashMap<>();
	
	String baseUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Base.wsUrl");
	String areaUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.url");
	String areaUrlParams = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.urlParams");
	
	int i = 1;
	int j = (begin) ? 1 : 11;
	while(areaid.length() > 0) {
        HashMap<String, Object> areaInfo = new HashMap<>();
        String areaXml = requestScraper(baseUrl + areaUrl + areaid + areaUrlParams);

        //get area name from MUBZ
        HashMap<String, String> areaNameRule = this.mappingRules.getMappingRule(this.scraperId, "AreaName");
        List<String> areaNameList = getInfoFromXml(areaXml, areaNameRule);
        if (areaNameList.size() > 0) {
            areaInfo.put("name", areaNameList.get(0));
        }

        //get area sort name from MUBZ
        HashMap<String, String> areaSortNameRule = this.mappingRules.getMappingRule(this.scraperId, "AreaSortName");
        List<String> areaSortNameList = getInfoFromXml(areaXml, areaSortNameRule);
        if (areaSortNameList.size() > 0) {
            areaInfo.put("sortName", areaSortNameList.get(0));
        }
        //get area type from MUBZ
        HashMap<String, String> areaTypeRule = this.mappingRules.getMappingRule(this.scraperId, "AreaType");
        String areaTypeXpath = XmlUtils.resolveXpathString(areaTypeRule.get("xpathAbsolute"), "_areatype_::type");
        areaTypeRule.put("xpathAbsolute", areaTypeXpath);
        List<String> areaTypeList = getInfoFromXml(areaXml, areaTypeRule);
        if (areaTypeList.size() > 0) {
            areaInfo.put("type", areaTypeList.get(0));
        }
        //get area code 1 from MUBZ
        HashMap<String, String> areaCode1Rule = this.mappingRules.getMappingRule(this.scraperId, "AreaCode1");
        List<String> areaCode1List = getInfoFromXml(areaXml, areaCode1Rule);
        if (areaCode1List.size() > 0) {
            areaInfo.put("code", areaCode1List.get(0));
        }
        //get area code 1 from MUBZ
        HashMap<String, String> areaCode2Rule = this.mappingRules.getMappingRule(this.scraperId, "AreaCode2");
        List<String> areaCode2List = getInfoFromXml(areaXml, areaCode2Rule);
        if (areaCode2List.size() > 0) {
            areaInfo.put("code", areaCode2List.get(0));
        }
        areaInfo.put("unid", areaid);
        areaInfo.put("seq", String.valueOf(i));
        if (begin && artistType.equals("Person")) {
            areaInfo.put("event", "born in");
        } else if (!begin && artistType.equals("Person")) {
            areaInfo.put("event", "died in");
        } else if (begin && !artistType.equals("Person")) {
            areaInfo.put("event", "founded in");
        } else if (!begin && !artistType.equals("Person")) {
            areaInfo.put("event", "dissolved in");
        }
        retAreaInfos.put(j, areaInfo);
        i++;
        j++;

        //get area relation backwards from MUBZ
        HashMap<String, String> areaRelationBackwardRule = this.mappingRules.getMappingRule(this.scraperId, "AreaRelationBackward");
        List<String> areaRelationBackwardList = getInfoFromXml(areaXml, areaRelationBackwardRule);
        if (areaRelationBackwardList.size() > 0) {
            areaid = areaRelationBackwardList.get(0);
        } else {
            areaid = "";
        }
    }
	return retAreaInfos;
}


//=============================================================================	
/*
* 	HELPER METHODS (private)
*/
private List<String> getInfoFromXml(String xmlString, HashMap<String, String> ruleMap) {
	List<String> retList = new ArrayList<>();

	String ruleType = ruleMap.get("type");
	String ruleSubType = ruleMap.get("subType");
	String absPath = ruleMap.get("xpathAbsolute");
	String childNodeNames = ruleMap.get("childNodeNames");

	if(ruleType.equals("xpath") && ruleSubType.equals("text")) {
		retList = XmlUtils.getNodeTextByXPath(xmlString, absPath);
	}
	else if(ruleType.equals("xpath") && ruleSubType.equals("attribute")) {
		retList = XmlUtils.getNodeAttributeTextByXPath(xmlString, absPath, "");
	}
	else if(ruleType.equals("xpath") && ruleSubType.equals("node")) {
		retList = XmlUtils.getNodeByXPath(xmlString, absPath, childNodeNames);
	}
	
	return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private String requestScraper(String url) throws Exception {
	HashMap<String, String> scraperResponse = this.scraperHttpClient.run(url);
	if(scraperResponse.get("rspCode").equals("200")) {
		return scraperResponse.get("rspBody");
	}
	else {
		log.warn("Could not get any response from MusicBrainz! url='{}', rspCode='{}'", url, scraperResponse.get("rspCode"));
		return "";
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void readUrlToUniqueIdFilter(String filePath) throws Exception {
	BufferedReader  bfr = new BufferedReader(new FileReader(new File(filePath)));

    String line;
    while ((line = bfr.readLine()) != null) {
        if (!line.startsWith("#") && !line.isEmpty()) {
        	//split key and value ('=')
        	String[] pair = line.trim().split("=", 2);
        	String key = pair[0].trim();
        	String value = pair[1].trim();
        	this.urlToUniqueIdsRules.put(key, value);
       }
    }
    bfr.close();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void writeUrlToUniqueId(HashMap<String, Object> insertInfo, String mbArtistId, String nodeSection, String copyRule) throws Exception {
	String urlRegEx = "";
	String scraperName = "";
	String unid;
	String url = (String)insertInfo.get("url");
	for (Map.Entry<String, String> entry : this.urlToUniqueIdsRules.entrySet()) {
		if(url.contains(entry.getKey())) {
			String[] pair = entry.getValue().split("¦¦¦");
			if(pair.length == 2) {
				urlRegEx = pair[0].trim();
                scraperName = pair[1].trim();
			}
			if(urlRegEx.equals("mbid")) {
				unid = mbArtistId;
			}
			else {
				unid = getUniqueIdFromUrl(url, urlRegEx);
			}

			insertInfo.put("unid", unid);
			switch (nodeSection) {
                case "Artist":
                    mdm.writeArtistUniqueId(insertInfo, mbArtistId, copyRule, this.scraperName, scraperName);
                    break;
                default:
                    log.error("could not find node section '{}'", nodeSection);
            }
		}
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private String getUniqueIdFromUrl(String url, String urlRegex) {
	String unid = "";

	// Create a Pattern object and Matcher object
    Pattern patterrn = Pattern.compile(urlRegex);
    Matcher matcher = patterrn.matcher(url);
    if (matcher.find()) {
    	log.info("Found value: " + matcher.group(1));
		unid = matcher.group(1);
    } else {
    	log.warn("NO MATCH");
    }
	return unid;
}


//=============================================================================	
/*
* 	GETTER/SETTER METHODS	
*/
/*
public OutputXmlDoc getOxd() {	return oxd;	}

//-----------------------------------------------------------------------------

public void setOxd(OutputXmlDoc oxd) {	this.oxd = oxd;	}

//-----------------------------------------------------------------------------
*/
}