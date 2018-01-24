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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mumdag.core.MappingRules;
//import org.mumdag.core.OutputXmlDoc;
//import org.mumdag.core.OutputXmlDoc.NodeAction;
//import org.mumdag.core.OutputXmlDoc.NodeStatus;
//import org.mumdag.scraper.helper.ScraperHttpClient;
import org.mumdag.model.MumdagModel;
import org.mumdag.utils.MapListUtils;
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
private String mappingRulesFilePath;
private String mappingRulesType;
//private ScraperHttpClient scraperHttpClient;
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
	//this.scraperHttpClient = ScraperHttpClient.getInstance();
	this.scraperId = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.id");
	this.scraperName = "MusicBrainz";
	this.mappingRulesFilePath = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesFilePath");
	this.mappingRulesType = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesType");
	this.mappingRules = MappingRules.getInstance();
	this.mappingRules.updateMappingRules(this.mappingRulesFilePath, this.scraperId, this.mappingRulesType);
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
//	writeMusicBrainzArtistAreaList(input);
	writeMusicBrainzArtistUrls(input);
/*	writeMusicBrainzArtistTags(input);
	writeMusicBrainzArtistRating(input);*/
	writeMusicBrainzArtistIpiIsni(input);
/*	writeMusicBrainzArtistAnnotation(input);
	writeMusicBrainzArtistDisambiguation(input);
	writeMusicBrainzArtistCredits(input);*/
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistName(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
    HashMap<String, Object> insertInfo = new HashMap<>();

	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
    insertInfo.put("unid", artistId);
    insertInfo.put("nameType", "Name");
	
    //get ArtistName from MUBZ
	HashMap<String, String> artistNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistName");
	String artistNameXpath = XmlUtils.resolveXpathString(artistNameRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistNameRule.put("xpathAbsolute", artistNameXpath);
	List<String> artistNameList = getInfoFromXml(artistInfoXml, artistNameRule);
    insertInfo.put("name", artistNameList.get(0));
	
	//get ArtistSortName from MUBZ
	HashMap<String, String> artistSortNameRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistSortName");
	String artistSortNameXpath = XmlUtils.resolveXpathString(artistSortNameRule.get("xpathAbsolute"), "_arid_::@id='"+artistId+"'");
	artistSortNameRule.put("xpathAbsolute", artistSortNameXpath);
	List<String> artistSortNameList = getInfoFromXml(artistInfoXml, artistSortNameRule);
    insertInfo.put("sortName", artistSortNameList.get(0));
	
	//put ArtistName to MMDG
    mdm.writeArtistName(insertInfo, "ArtistName", this.scraperName);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAlias(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
	HashMap<String, Object> insertInfo = new HashMap<>();

	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
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
	String artistInfoXml = (String)input[0];
    HashMap<String, Object> insertInfo = new HashMap<>();

    //get ArtistId from MUBZ
    String artistId = getMusicBrainzArtistId(input);
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
	String artistInfoXml = (String)input[0];
    HashMap<String, Object> insertInfo = new HashMap<>();

    //get ArtistId from MUBZ
    String artistId = getMusicBrainzArtistId(input);
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
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAreaList(Object[] input) throws Exception {
//GENERAL THINGS ...
	String artistInfoXml = (String)input[0];
	
	// define the behavior of the operation depending on the current node state (for artists' area)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistArea");

	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";
	
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
	HashMap<Integer, HashMap<String, String>> areaInfos = new HashMap<Integer, HashMap<String, String>>();
	areaInfos = requestMusicBrainAreaInfo(artistAreaBeginId, artistType, true);
	areaInfos.putAll(requestMusicBrainAreaInfo(artistAreaEndId, artistType, false));
	
	for (HashMap<String, String> areaInfo : areaInfos.values()) {
		//prepare information to resolve the xpaths
		HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck, "_areaidattr_", "@mbid", 
				"_areaid_", "@mbid='"+areaInfo.get("areaId").split("=")[1]+"'", "_areaseqattr_", "@seq", 
				"_areaseq_", "@seq='"+areaInfo.get("areaSeq").split("=")[1]+"'", "_areaeventattr_", "@event", 
				"_areaevent_", "@event='"+areaInfo.get("areaEvent").split("=")[1]+"'", "_areatypeattr_", "@type", 
				"_areatype_", "@type='"+areaInfo.get("areaType").split("=")[1]+"'", "_areasortnameattr_", "@sort-name", 
				"_areasortname_", "@sort-name='"+areaInfo.get("areaSortName").split("=")[1]+"'", "_areaname_", areaInfo.get("areaName"));
		
		List<String> areaInfoValues = new ArrayList<String>(areaInfo.values());
		mdm.writeInfo(areaInfoValues, "ArtistAreaList", resolveXpathInfos, copyBehavior);
	};
	
}
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistUrls(Object[] input) throws Exception {
//GENERAL THINGS ...
	String artistInfoXml = (String)input[0];
    HashMap<String, Object> insertInfo = new HashMap<>();

    //get ArtistId from MUBZ
    String artistId = getMusicBrainzArtistId(input);

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
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistTags(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];

	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	String artistIdAttr = this.mbIdAttrName+"="+artistId;
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";

	// define the behavior of the operation depending on the current node state (artists' tags)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistTags");
	
	//get ArtistTag plus according attribute 'count' from MUBZ
	HashMap<String, String> artistTagsRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistTags");
	HashMap<String, String> artistTagsCountRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistTagsCount");
	String artistTagsCountXpathUnresolved = artistTagsCountRule.get("xpathAbsolute");
	List<String> artistTagsList = getInfoFromXml(artistInfoXml, artistTagsRule);
	for(int i = 0; i < artistTagsList.size(); i++) {
		String artistTag = artistTagsList.get(i);
		
		String artistTagsCountXpath = XmlUtils.resolveXpathString(artistTagsCountXpathUnresolved, "_tag_::"+artistTag);
		artistTagsCountRule.put("xpathAbsolute", artistTagsCountXpath);
		List<String> artistTagCountList = getInfoFromXml(artistInfoXml, artistTagsCountRule);
		String artistTagCount = "";
		if(artistTagCountList.size() > 0) {
			artistTagCount = artistTagCountList.get(0);
		}
		log.info("tag: '{}' and count: {}",  artistTag, artistTagCount);
		
		//preparing artist tag infos (ArtistTag) 
		List<String> artistTagInfo = MapListUtils.createInfoList(artistIdAttr, artistTag, "count="+artistTagCount);
		
		//prepare information to resolve the xpaths
		HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck, "_scraperattr_", "@"+this.mbIdAttrName, 
				"_scraperattr_", "@"+this.mbIdAttrName, "_countattr_", "@"+"count", "_count_", "@"+"count"+"='"+artistTagCount+"'", "_tag_", artistTag);

		//put ArtistTag to MMDG
		mdm.writeInfo(artistTagInfo, "ArtistTagList", resolveXpathInfos, copyBehavior);	
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistRating(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];

	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	String artistIdAttr = this.mbIdAttrName+"="+artistId;
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";

	//preparing info of artists' rating
	String artistRatingSourceAttr = this.sourceAttrName+"="+this.sourceAttrValue;
	String artistRatingSourceAttrCheck = "@"+this.sourceAttrName+"='"+this.sourceAttrValue+"'";

	// define the behavior of the operation depending on the current node state (artists' tags)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistRating");
	
	//get ArtistRating plus according attribute 'votes-count' from MUBZ
	HashMap<String, String> artistRatingRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistRating");
	List<String> artistRatingList = getInfoFromXml(artistInfoXml, artistRatingRule);
	String artistRating = artistRatingList.get(0);
	HashMap<String, String> artistRatingVotesCountRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistRatingVotesCount");
	List<String> artistRatingVotesCountList = getInfoFromXml(artistInfoXml, artistRatingVotesCountRule);
	String artistRatingVotesCount = artistRatingVotesCountList.get(0);
	
	//preparing artist rating infos (ArtistRating) 
	List<String> artistRatingInfo = MapListUtils.createInfoList(artistIdAttr, artistRatingSourceAttr, "votes="+artistRatingVotesCount, artistRating);
	
	//prepare information to resolve the xpaths
	HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_scraperattr_", "@"+this.mbIdAttrName, "_arid_", artistIdAttrCheck,  
						"_sourceattr_", this.sourceAttrName, "_source_", artistRatingSourceAttrCheck,
						"_votescountattr_", "@"+"votes", "_votescount_", "@"+"votes"+"='"+artistRatingVotesCount+"'", "_rating_", artistRating);

	//put ArtistRating to MMDG
	mdm.writeInfo(artistRatingInfo, "ArtistRatingList", resolveXpathInfos, copyBehavior);	
}
*/
//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeMusicBrainzArtistIpiIsni(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
	
	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	
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
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistAnnotation(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
	
	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	String artistIdAttr = this.mbIdAttrName+"="+artistId;
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";

	//preparing info of artists' annotation
	String artistAnnotationSourceAttr = this.sourceAttrName+"="+this.sourceAttrValue;
	String artistAnnotationSourceAttrCheck = "@"+this.sourceAttrName+"='"+this.sourceAttrValue+"'";
	
	// define the behavior of the operation depending on the current node state (artists' annotation)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistAnnotation");
	
	//get ArtistAnnotations from MUBZ split them according splitregExp and put them to MMDG
	HashMap<String, String> artistAnnotationRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistAnnotation");
	String splitRegEx = artistAnnotationRule.get("splitRegEx");
	List<String> artistAnnotationList = getInfoFromXml(artistInfoXml, artistAnnotationRule);
	for(int i = 0; i < artistAnnotationList.size(); i++) {
		String annotationsText = artistAnnotationList.get(i); 
		String[] annotations = annotationsText.split(splitRegEx);
		for(int j = 0; j < annotations.length; j++) {
			String artistAnnotation = annotations[j];
			log.info("artistAnnotation '{}'",  artistAnnotation);
			
			//preparing artist annotation infos (ArtistAnnotation) 
			List<String> artistAnnotationInfo = MapListUtils.createInfoList(artistIdAttr, artistAnnotationSourceAttr, artistAnnotation);
			
			//prepare information to resolve the xpaths
			HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_scraperattr_", "@"+this.mbIdAttrName, "_arid_", artistIdAttrCheck,  
								"_sourceattr_", this.sourceAttrName, "_source_", artistAnnotationSourceAttrCheck, "_annotation_", artistAnnotation);
			
			//put ArtistAnnotation to MMDG
			mdm.writeInfo(artistAnnotationInfo, "ArtistAnnotationList", resolveXpathInfos, copyBehavior);	
		}
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistDisambiguation(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
	
	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	String artistIdAttr = this.mbIdAttrName+"="+artistId;
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";

	//preparing info of artists' disambiguation
	String artistDisambiguationSourceAttr = this.sourceAttrName+"="+this.sourceAttrValue;
	String artistDisambiguationSourceAttrCheck = "@"+this.sourceAttrName+"='"+this.sourceAttrValue+"'";
	
	// define the behavior of the operation depending on the current node state (artists' disambiguation)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistDisambiguation");
	
	//get ArtistDisambigaution from MUBZ and put them to MMDG
	HashMap<String, String> artistDisambiguationRule = this.mappingRules.getMappingRule(this.scraperId, "ArtistDisambiguation");
	List<String> artistDisambiguationList = getInfoFromXml(artistInfoXml, artistDisambiguationRule);
	for(int i = 0; i < artistDisambiguationList.size(); i++) {
		String artistDisambiguation = artistDisambiguationList.get(i); 
		log.info("ArtistDisambiguation '{}'",  artistDisambiguation);
			
		//preparing artist disambiguation infos (ArtistDisambiguation) 
		List<String> artistDisambiguationInfo = MapListUtils.createInfoList(artistIdAttr, artistDisambiguationSourceAttr, artistDisambiguation);
		
		//prepare information to resolve the xpaths
		HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_scraperattr_", "@"+this.mbIdAttrName, "_arid_", artistIdAttrCheck,  
							"_sourceattr_", this.sourceAttrName, "_source_", artistDisambiguationSourceAttrCheck, "_disambiguation_", artistDisambiguation);
		
		//put ArtistDisambiguation to MMDG
		mdm.writeInfo(artistDisambiguationInfo, "ArtistDisambiguationList", resolveXpathInfos, copyBehavior);	
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	nok (sortName, source, 
public void writeMusicBrainzArtistCredits(Object[] input) throws Exception {
	String artistInfoXml = (String)input[0];
	
	//get ArtistId from MUBZ
	String artistId = getMusicBrainzArtistId(input);
	HashMap<String, String> additionalInfo = new HashMap<String, String>();
	additionalInfo.put("artistId", artistId);
	additionalInfo.put("artistNameType", "Name");
	
	//String artistIdAttr = this.mbIdAttrName+"="+artistId;
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+artistId+"'";
	
	// define the behavior of the operation depending on the current node state (artists' credit list)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, "ArtistCreditIds");
	
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
	for(int i = 0; i < artistCreditIdsList.size(); i++) {
		String artistCreditId = artistCreditIdsList.get(i);
		log.info("artist credit ids {}", artistCreditId);
		additionalInfo.put("artistCreditId", artistCreditId);
		
		//prepare information to resolve the xpaths
		HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck,
							"_acid_", "@"+this.mbIdAttrName+"='"+artistCreditId+"'", "_unidattrname_", "@"+this.mbIdAttrName);
		
		//put ArtistCreditNode to MMDG
		mdm.writeInfo(this.mbIdAttrName+"="+artistCreditId, "ArtistCreditNode", resolveXpathInfos, copyBehavior);
		
		//get ArtistCredit base xpath from MMDG (used for writeUniqueIdInfos, writeNameInfos)
		HashMap<String, String> resolveBaseXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck,
																				"_acid_", "@"+this.mbIdAttrName+"='"+artistCreditId+"'");
		String artistCreditBaseXpathResolved = mdm.getResolvedBaseXpath("ArtistCreditBase", resolveBaseXpathInfos);

		//write the uniqueID node within the ArtistCreditNode
		HashMap<String, String> uniqueIdInfos = new HashMap<String, String>();
		uniqueIdInfos.put("uniqueId", artistCreditId);
		uniqueIdInfos.put("url", "-");
		uniqueIdInfos.put("placeholder", "_arid_");
		uniqueIdInfos.put("scraperId", this.scraperId);
		uniqueIdInfos.put("copyBehaviorRule", "ArtistId");
		uniqueIdInfos.put("resolvedBaseXpath", artistCreditBaseXpathResolved);
		uniqueIdInfos.put("propSection", "MusicBrainz");
		uniqueIdInfos.put("propSubSection", "Artist");
		mdm.writeUniqueIdInfos(uniqueIdInfos);
		
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
		
		//put ArtistCreditName to MMDG
		writeNameInfos(artistCreditName, artistCreditSortName, additionalInfo, "Name", "ArtistCreditName", artistCreditBaseXpathResolved);
		
		//get ArtistCreditAttributes
		String artistCreditAttributesXpath = XmlUtils.resolveXpathString(artistCreditAttributesXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditAttributesRule.put("xpathAbsolute", artistCreditAttributesXpath);
		List<String> artistCreditAttributesList = getInfoFromXml(artistInfoXml, artistCreditAttributesRule);
		artistCreditAttributesList = artistCreditAttributesList.stream().distinct().collect(Collectors.toList());
		if(artistCreditAttributesList.contains("original")) {
			artistCreditAttributesList.remove("original");
			additionalInfo.put("original", "true");
		}
		log.info("\t number of attributes {}", artistCreditAttributesList.size());
		for(int n = 0; n < artistCreditAttributesList.size(); n++) {
			String instrument = artistCreditAttributesList.get(n);
			log.info("\t\t{}", instrument);
			writeArtistRoleInfos(instrument, "instrument", additionalInfo, "RoleList", "ArtistCreditRole", artistCreditBaseXpathResolved); // alle anderen instrument attribute
		}
		
		//get ArtistCreditType
		String artistCreditsTypeXpath = XmlUtils.resolveXpathString(artistCreditsTypeXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditTypeRule.put("xpathAbsolute", artistCreditsTypeXpath);
		List<String> artistCreditTypeList = getInfoFromXml(artistInfoXml, artistCreditTypeRule);
		artistCreditTypeList = artistCreditTypeList.stream().distinct().collect(Collectors.toList());
		String membership = artistCreditTypeList.get(0);
		log.info("\ttype {}", membership);
		writeArtistRoleInfos(membership, "membership", additionalInfo, "RoleList", "ArtistCreditRole", artistCreditBaseXpathResolved); //mit original
		additionalInfo.remove("original");
		
		//get ArtistCreditPeriods
		String artistCreditsPeriodsXpath = XmlUtils.resolveXpathString(artistCreditPeriodsXpathUnresolved, "_acid_::"+artistCreditId);
		artistCreditPeriodsRule.put("xpathAbsolute", artistCreditsPeriodsXpath);
		List<String> artistCreditPeriodsList = getInfoFromXml(artistInfoXml, artistCreditPeriodsRule);
		artistCreditPeriodsList = artistCreditPeriodsList.stream().distinct().collect(Collectors.toList());
		for(int k = 0; k < artistCreditPeriodsList.size(); k++) {
			String artistCreditPeriodsStr = artistCreditPeriodsList.get(k);
			log.info("\tperiod {}", artistCreditPeriodsStr);
			
			String[] artistCreditPeriodsArr = artistCreditPeriodsStr.split(",");
			for(int l = 0; l < artistCreditPeriodsArr.length; l++) {
				String[] artistCreditPeriodArr = artistCreditPeriodsArr[l].split("=");
				if(artistCreditPeriodArr.length == 2) {
					String periodType = artistCreditPeriodArr[0];
					String period = artistCreditPeriodArr[1];
					writePeriodInfos(period, periodType, additionalInfo, "PeriodList", "ArtistCreditPeriods", artistCreditBaseXpathResolved);
				}
			}		
		}
	}
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
HashMap<Integer, HashMap<String, String>> requestMusicBrainAreaInfo(String areaid, String artistType, Boolean begin) throws Exception {
	HashMap<Integer, HashMap<String, String>> retAreaInfos = new HashMap<Integer, HashMap<String, String>>();
	
	String baseUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Base.wsUrl");
	String areaUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.url");
	String areaUrlParams = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.urlParams");
	
	int i = 1;
	int j = (begin) ? 1 : 11;
	while(areaid.length() > 0) {
		HashMap<String, String> areaInfo = new HashMap<String, String>();
		String areaXml = requestScraper(baseUrl + areaUrl + areaid + areaUrlParams);
		
		//get area name from MUBZ
		HashMap<String, String> areaNameRule = this.mappingRules.getMappingRule(this.scraperId, "AreaName");
		List<String> areaNameList = getInfoFromXml(areaXml, areaNameRule);
		if(areaNameList.size() > 0) {
			areaInfo.put("areaName", areaNameList.get(0));
		}
		
		//get area sort name from MUBZ
		HashMap<String, String> areaSortNameRule = this.mappingRules.getMappingRule(this.scraperId, "AreaSortName");
		List<String> areaSortNameList = getInfoFromXml(areaXml, areaSortNameRule);
		if(areaSortNameList.size() > 0) {
			areaInfo.put("areaSortName", "sort-name="+areaSortNameList.get(0));
		}
		//get area type from MUBZ
		HashMap<String, String> areaTypeRule = this.mappingRules.getMappingRule(this.scraperId, "AreaType");
		String areaTypeXpath = XmlUtils.resolveXpathString(areaTypeRule.get("xpathAbsolute"), "_areatype_::type");
		areaTypeRule.put("xpathAbsolute", areaTypeXpath);
		List<String> areaTypeList = getInfoFromXml(areaXml, areaTypeRule);
		if(areaTypeList.size() > 0) {
			areaInfo.put("areaType", "type="+areaTypeList.get(0));
		}
		//get area code 1 from MUBZ
		HashMap<String, String> areaCode1Rule = this.mappingRules.getMappingRule(this.scraperId, "AreaCode1");
		List<String> areaCode1List = getInfoFromXml(areaXml, areaCode1Rule);
		if(areaCode1List.size() > 0) {
			areaInfo.put("areaCode", "code="+areaCode1List.get(0));
		}
		//get area code 1 from MUBZ
		HashMap<String, String> areaCode2Rule = this.mappingRules.getMappingRule(this.scraperId, "AreaCode2");
		List<String> areaCode2List = getInfoFromXml(areaXml, areaCode2Rule);
		if(areaCode2List.size() > 0) {
			areaInfo.put("areaCode", "code="+areaCode2List.get(0));
		}
		areaInfo.put("areaId", "mbid="+areaid);
		areaInfo.put("areaSeq", "seq="+String.valueOf(i));
		if(begin && artistType.equals("Person")) {
			areaInfo.put("areaEvent", "event="+"born in");
		}
		else if(!begin && artistType.equals("Person")) {
			areaInfo.put("areaEvent", "event="+"died in");
		}
		else if(begin && !artistType.equals("Person")) {
			areaInfo.put("areaEvent", "event="+"founded in");
		}
		else if(!begin && !artistType.equals("Person")) {
			areaInfo.put("areaEvent", "event="+"dissolved in");
		}
		retAreaInfos.put(j, areaInfo);
		i++;
		j++;
		
		//get area relation backwards from MUBZ
		HashMap<String, String> areaRelationBackwardRule = this.mappingRules.getMappingRule(this.scraperId, "AreaRelationBackward");
		List<String> areaRelationBackwardList = getInfoFromXml(areaXml, areaRelationBackwardRule);
		if(areaRelationBackwardList.size() > 0) {
			areaid = areaRelationBackwardList.get(0);
		}
		else {
			areaid = "";
		}
	}
	
	return retAreaInfos;
}
*/

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
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String requestScraper(String url) throws Exception {
	HashMap<String, String> scraperResponse = new HashMap<String, String>(); 
	scraperResponse = this.scraperHttpClient.run(url);
	if(scraperResponse.get("rspCode").equals("200")) {
		return scraperResponse.get("rspBody");
	}
	else {
		log.warn("Could not get any response from MusicBrainz! url='{}', rspCode='{}'", url, scraperResponse.get("rspCode"));
		return "";
	}
}
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeNameInfos(String name, String sortName, HashMap<String, String> additionalInfo, String targetRule, String copyBehaviorRule, String resolvedBaseXpath) throws Exception {
	writeNameInfos(name, sortName, null, additionalInfo, targetRule, copyBehaviorRule, resolvedBaseXpath);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeNameInfos(String name, List<String> nameInfo, HashMap<String, String> additionalInfo, String targetRule, String copyBehaviorRule, String resolvedBaseXpath) throws Exception {
	writeNameInfos(name, "", nameInfo, additionalInfo, targetRule, copyBehaviorRule, resolvedBaseXpath);
}
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeNameInfos(String name, String sortName, List<String> nameInfo, HashMap<String, String> additionalInfo, String targetRule, String copyBehaviorRule, String resolvedBaseXpath) throws Exception {
		
	//preparing infos of the artists' name (id, source, type)
	String artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistId");
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistId")+"'";
	if(additionalInfo.containsKey("artistCreditId")) {
		artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistCreditId");
		artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistCreditId")+"'";
	}
	String artistNameSourceAttr = this.sourceAttrName+"="+this.sourceAttrValue;
	String artistNameSourceAttrCheck = "@"+this.sourceAttrName+"='"+this.sourceAttrValue+"'";
	
	String artistNameTypeAttr = "type"+"="+additionalInfo.get("artistNameType");
	String artistNameTypeAttrCheck = "@"+"type"+"="+"'"+additionalInfo.get("artistNameType")+"'";
	if(nameInfo != null) {
		for(int i = 0; i < nameInfo.size(); i++) {
			String nameInfoStr = nameInfo.get(i);
			if(nameInfoStr.contains("type=")) {
				String[] nameInfoArr = nameInfoStr.split("=");
				String typeVal = nameInfoArr[1];
				artistNameTypeAttr = nameInfoStr;
				artistNameTypeAttrCheck = "@"+"type"+"="+"'"+typeVal+"'";
			}
		}
	}
	
	// define the behavior of the operation depending on the current node state (artists' aliases)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, copyBehaviorRule);		
	
	//prepare information to resolve the xpaths
	HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck, "_scraperattr_", "@"+this.mbIdAttrName, 
			"_arname_", name, "_src_", artistNameSourceAttrCheck, "_srcattr_", "@"+this.sourceAttrName, 
			"_ntype_", artistNameTypeAttrCheck, "_ntypeattr_", "@"+"type");

	//preparing artist' name info list
	if(nameInfo == null) {
		nameInfo = new ArrayList<String>();
	}
	nameInfo = MapListUtils.createInfoList(nameInfo, artistIdAttr, name, artistNameSourceAttr, artistNameTypeAttr);
	if(sortName != null && sortName.length() > 0) {
		String artistSortNameAttr = "sort-name"+"="+sortName;
		nameInfo = MapListUtils.createInfoList(nameInfo,  artistSortNameAttr);
	}
	
	//put Name to MMDG
	mdm.writeInfo(nameInfo, targetRule, resolvedBaseXpath, resolveXpathInfos, copyBehavior);
}
*/
//-----------------------------------------------------------------------------
/*
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeArtistRoleInfos(String role, String roleType, HashMap<String, String> additionalInfo, String targetRule, String copyBehaviorRule, String resolvedBaseXpath) throws Exception {
		
	//preparing infos of the artists' name (id, source, type)
	String artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistId");
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistId")+"'";
	if(additionalInfo.containsKey("artistCreditId")) {
		artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistCreditId");
		artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistCreditId")+"'";
	}
	String roleTypeAttr = "type"+"="+roleType;
	String roleTypeAttrCheck = "@"+"type"+"='"+roleType+"'";
	String originalAttr = "";

	
	// define the behavior of the operation depending on the current node state (artists' aliases)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, copyBehaviorRule);	
	
	//preparing artists' role  
	if(role.equals("member of band") && additionalInfo.containsKey("original") && additionalInfo.get("original").equals("true")) {
		originalAttr = "original"+"="+"true";
	}
	else if(role.equals("member of band")) {
		originalAttr = "original"+"="+"false";
	}
	
	List<String> roleInfo = new ArrayList<String>(); 
	if(originalAttr.length() > 0) {
		roleInfo = MapListUtils.createInfoList(artistIdAttr, role, roleTypeAttr, originalAttr);
	}
	else {
		roleInfo = MapListUtils.createInfoList(artistIdAttr, role, roleTypeAttr);
	}

	//prepare information to resolve the xpaths
	HashMap<String, String> resolveXpathInfos = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck, "_role_", role, 
			"_roletype_", roleTypeAttrCheck, "_srcattr_", "@"+"mbid", "_roletypeattr_", "@"+"type");
	
	//put Name to MMDG
	mdm.writeInfo(roleInfo, targetRule, resolvedBaseXpath, resolveXpathInfos, copyBehavior);	
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writePeriodInfos(String period, String periodType, HashMap<String, String> additionalInfo, String targetRule, String copyBehaviorRule, String resolvedBaseXpath) throws Exception {
	
	//preparing infos of the artists' name (id, source, type)
	String artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistId");
	String artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistId")+"'";
	if(additionalInfo.containsKey("artistCreditId")) {
		artistIdAttr = this.mbIdAttrName+"="+additionalInfo.get("artistCreditId");
		artistIdAttrCheck = "@"+this.mbIdAttrName+"='"+additionalInfo.get("artistCreditId")+"'";
	}
	String artistType = "";
	if(additionalInfo.containsKey("artistType")) {
		artistType = additionalInfo.get("artistType");
	}

	// define the behavior of the operation depending on the current node state (for period begin/end)
	HashMap<NodeStatus, NodeAction> copyBehavior = this.mappingRules.getCopyBehavior(this.scraperId, copyBehaviorRule);
	
	//preparing infos for period begin/end
	String dateTypeAttr = "type"+"="+periodType;	
	String dateTypeAttrCheck = "@"+"type"+"='"+periodType+"'";
	if(periodType.equals("begin")) {
		if(artistType.equals("Person")) {
			dateTypeAttr = "type"+"="+"born";
			dateTypeAttrCheck = "@"+"type"+"='"+"born"+"'";
		}
		else if(artistType.equals("Group")) {
			dateTypeAttr = "type"+"="+"founded";	
			dateTypeAttrCheck = "@"+"type"+"='"+"founded"+"'";
		}
	}
	else if (periodType.equals("end")) {
		if(artistType.equals("Person")) {
			dateTypeAttr = "type"+"="+"died";
			dateTypeAttrCheck = "@"+"type"+"='"+"died"+"'";
		}
		else if(artistType.equals("Group")) {
			dateTypeAttr = "type"+"="+"dissolved";	
			dateTypeAttrCheck = "@"+"type"+"='"+"dissolved"+"'";
		}
	}
	
	//preparing artists' period begin infos (year, type) 
	List<String> periodInfo = MapListUtils.createInfoList(artistIdAttr, period, dateTypeAttr);
	
	//prepare information to resolve the xpaths
	HashMap<String, String> resolveXpathInfosPeriod = MapListUtils.createResolveXpathMap("_arid_", artistIdAttrCheck, "_ardate_", period, 
			"_datetype_", dateTypeAttrCheck, "_srcattr_", "@"+"mbid", "_datetypeattr_", "@"+"type");

	//put ArtistPeriod Begin/End to MMDG
	mdm.writeInfo(periodInfo, targetRule, resolvedBaseXpath, resolveXpathInfosPeriod, copyBehavior);
}
*/
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