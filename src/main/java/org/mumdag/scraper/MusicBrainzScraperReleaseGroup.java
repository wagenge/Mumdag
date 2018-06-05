package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.mumdag.model.index.Album;
import org.mumdag.utils.ListUtils;
import org.mumdag.utils.PropertyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

class MusicBrainzScraperReleaseGroup extends MusicBrainzScraperBase {

//=============================================================================
/*
 * 	SCRAPER METHODS FOR RELEASE GROUP (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
String requestReleaseGroupInfo() {
    //get current artist
    Album album = mi.getCurrentAlbum();
    String wsUrl = "";
    String releaseGroupInfo = "";
    try {
        wsUrl = album.getReleaseGroupWSURL("mbid", this.scraperName);
    } catch (Exception ex) {
        log.error("Could not find url for the web service, requesting the release gruop info from MusicBrainz");
    }
    try {
        releaseGroupInfo = requestScraper(wsUrl);
    } catch (Exception ex) {
        log.error(ex.getMessage());
    }
    return releaseGroupInfo;
}

//-----------------------------------------------------------------------------


//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupTitle(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    HashMap<String, Object> insertInfo = new HashMap<>();
    //get ReleaseGroupName from MUBZ
    try {
        List<HashMap<String, Object>> releaseGroupTitleList = getMultiInfoFromXml(releaseGroupInfoXml, "ReleaseGroupTitle", "");
        insertInfo.put("name", releaseGroupTitleList.get(0).get("value"));
        insertInfo.put("nameType", "Title");
    } catch (Exception ex) {
        log.error("Error while resolving release group title from musicbrainz response!\nError:", ex.getMessage());
        return;
    }

    //put ReleaseGroupTitle to MMDG
    try {
        album.writeReleaseGroupName(insertInfo, "ReleaseGroupName", this.scraperName);
    } catch (Exception ex) {
        log.error("Error while executing 'writeReleaseGroupName'!\nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupAlias(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("inputXml are empty");
        return;
    }

    //get ReleaseGroupAliasName plus according Attributes from MUBZ
    List<HashMap<String, Object>> releaseGroupAliasList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralAlias", "", "_section_::release-group");
    for(HashMap<String, Object> releaseGroupAlias : releaseGroupAliasList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("name", releaseGroupAlias.get("alias"));

        //put nameType and additional attributes to insertInfo
        insertInfo.put("nameType", "Alias");
        List<String> releaseGroupAliasInfo = ListUtils.map2ListWithBlacklist(releaseGroupAlias, "alias");
        insertInfo.put("additionalInfo", releaseGroupAliasInfo);
        log.info("alias name '{}' and attributes: {}",  releaseGroupAlias.get("alias"), String.join(", ", releaseGroupAliasInfo));

        //put ReleaseGroupAlias to MMDG
        try {
            album.writeReleaseGroupAlias(insertInfo, "ReleaseGroupAlias", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupAlias'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupType(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupId = album.getReleaseGroupIdMap().get("mbid");
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupId) || StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("releaseGroupId or inputXml are empty");
        return;
    }

    List<HashMap<String, Object>> releaseGroupPrimaryTypeList = getMultiInfoFromXml(releaseGroupInfoXml, "ReleaseGroupPrimaryType", "");
    String releaseGroupPrimaryType = (String)releaseGroupPrimaryTypeList.get(0).get("value");
    HashMap<String, Object> insertInfo = new HashMap<>();
    insertInfo.put("type", releaseGroupPrimaryType);
    insertInfo.put("typeType", "PrimaryType");

    try {
        album.writeReleaseGroupType(insertInfo, "ReleaseGroupType", this.scraperName);
    } catch (Exception ex) {
        log.error("Error while executing 'writeReleaseGroupType'!\nError: {}", ex.getMessage());
    }

    List<HashMap<String, Object>> releaseGroupSecondaryTypeList = getMultiInfoFromXml(releaseGroupInfoXml, "ReleaseGroupSecondaryType", "");
    for(HashMap<String, Object> releaseGroupSecondaryType: releaseGroupSecondaryTypeList) {
        insertInfo = new HashMap<>();
        insertInfo.put("type", releaseGroupSecondaryType.get("value"));
        insertInfo.put("typeType", "SecondaryType");

        try {
            album.writeReleaseGroupType(insertInfo, "ReleaseGroupType", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupType'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupTypeFromRels(Object[] input) {
//GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    //String releaseGroupId = album.getReleaseGroupIdMap().get("mbid");
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("inputXml is empty");
        return;
    }

//EXTRACT AND WRITE RELEASEGROUP RELATIONS
    String whiteListStr = "";
    try {
        whiteListStr = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.ReleaseGroup.rgRelationWhitelist");
    } catch (Exception ex) {
        log.warn("Could not find property 'MusicBrainz.Scraper.ReleaseGroup.rgRelationWhitelist', ignoring whitelist and taking all release-group relations!");
    }
    List<String> relationsWhiteList = new ArrayList<>(Arrays.asList(whiteListStr.split(",")));
    //get ReleaseGroupRelations from MUBZ
    for(String whiteListEntry : relationsWhiteList) {
        List<HashMap<String, Object>> releaseGroupRelationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralRelations", "",
                "_section_::release-group",
                "_targettype_::@target-type='release_group'",
                "_whitelist_::@type='" + whiteListEntry + "'");
        for (HashMap<String, Object> releaseGroupRelation : releaseGroupRelationList) {
            HashMap<String, Object> insertInfo = new HashMap<>();
            insertInfo.put("type", releaseGroupRelation.get("title"));
            insertInfo.put("unid", releaseGroupRelation.get("id"));
            insertInfo.put("typeType", releaseGroupRelation.get("type"));
            List<String> additionalInfo = new ArrayList<>();
            additionalInfo.add("firstReleaseDate=" + releaseGroupRelation.get("first-release-date"));
            insertInfo.put("additionalInfo", additionalInfo);

            try {
                album.writeReleaseGroupType(insertInfo, "ReleaseGroupType", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupTypeFromRels (ReleaseGroupRels)'!\nError: {}", ex.getMessage());
            }
        }
    }

//EXTRACT AND WRITE SERIES RELATIONS
    whiteListStr = "";
    try {
        whiteListStr = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.ReleaseGroup.seriesRelationWhitelist");
    } catch (Exception ex) {
        log.warn("Could not find property 'MusicBrainz.Scraper.ReleaseGroup.seriesRelationWhitelist', ignoring whitelist and taking all release-group relatiopns!");
    }
    relationsWhiteList = new ArrayList<>(Arrays.asList(whiteListStr.split(",")));
    //get ReleaseGroupRelations from MUBZ
    for(String whiteListEntry : relationsWhiteList) {
        List<HashMap<String, Object>> releaseGroupRelationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralRelations", "",
                "_section_::release-group",
                "_targettype_::@target-type='series'",
                "_whitelist_::@type='" + whiteListEntry + "'");
        for (HashMap<String, Object> releaseGroupRelation : releaseGroupRelationList) {
            HashMap<String, Object> insertInfo = new HashMap<>();
            insertInfo.put("type", releaseGroupRelation.get("name"));
            insertInfo.put("unid", releaseGroupRelation.get("id"));
            insertInfo.put("typeType", releaseGroupRelation.get("type"));
            try {
                album.writeReleaseGroupType(insertInfo, "ReleaseGroupType", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupRelsSeries'!\nError: {}", ex.getMessage());
            }
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupDate(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("inputXml are empty");
        return;
    }

//EXTRACT AND WRITE FIRST RELEASE DATE
    List<HashMap<String, Object>> firstReleaseDateList = getMultiInfoFromXml(releaseGroupInfoXml, "FirstReleaseDate", "");
    if(firstReleaseDateList.size() > 0) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("date", firstReleaseDateList.get(0).get("value"));
        insertInfo.put("type", "firstReleaseDate");
        try {
            album.writeReleaseGroupDate(insertInfo, "ReleaseGroupDate", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupDate'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupTags(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("input xml is empty");
        return;
    }

//EXTRACT AND WRITE RELEASEGROUP TAGS
    List<HashMap<String, Object>> releaseGroupTagList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralTags", "", "_section_::release-group");
    for(HashMap<String, Object> releaseGroupTag : releaseGroupTagList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("tag", releaseGroupTag.get("name"));
        List<String> additionalInfoList = new ArrayList<>();
        additionalInfoList.add("count="+releaseGroupTag.get("count"));
        insertInfo.put("additionalInfo", additionalInfoList);
        try {
            album.writeReleaseGroupTag(insertInfo, "ReleaseGroupTags", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupTags'!\nError: {}", ex.getMessage());
        }
    }
}


//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupUrls(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupId = album.getReleaseGroupIdMap().get("mbid");
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupId) || StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("releaseGroupId or inputXml are empty");
        return;
    }

    try {
        //read UniqueId-Url-Filter
        String ruleFileName = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.ReleaseGroup.urlToUniqueIdFilterFile");
        readUrlToUniqueIdFilter(ruleFileName);
    } catch (Exception ex) {
        log.error("Error while executing 'writeReleaseGroupUrls'!\nError: {}", ex.getMessage());
        return;
    }

    //get ReleaseGroupRelations from MUBZ
    List<HashMap<String, Object>> releaseGroupRelationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralRelations", "",
            "_section_::release-group",
            "_targettype_::@target-type='url'",
            "_whitelist_::");
    for (HashMap<String, Object> releaseGroupRelation : releaseGroupRelationList) {
        HashMap<String, String> insertInfo = new HashMap<>();
        insertInfo.put("url", ((String)releaseGroupRelation.get("target")).replaceAll("=", "%3D"));
        insertInfo.put("type", (String)releaseGroupRelation.get("type"));

        try {
            //write the current url
            album.writeReleaseGroupUrls(insertInfo, "ReleaseGroupUrl", this.scraperName);
            //extract the unique id from the current url (if the pattern matches) and write it as unique id
            writeUrlToUniqueId(insertInfo, releaseGroupId, "ReleaseGroup", "ReleaseGroupId");
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupUrls'!\nError: {}", ex.getMessage());
            return;
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        ok
void writeReleaseGroupRating(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("input xml is empty");
        return;
    }

//EXTRACT AND WRITE RELEASEGROUP RATING
    List<HashMap<String, Object>> releaseGroupRatingList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralRating", "", "_section_::release-group");
    for(HashMap<String, Object> releaseGroupRating : releaseGroupRatingList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("rating", releaseGroupRating.get("rating"));
        List<String> additionalInfoList = new ArrayList<>();
        additionalInfoList.add("votes="+releaseGroupRating.get("votes-count"));
        insertInfo.put("additionalInfo", additionalInfoList);
        try {
            album.writeReleaseGroupRating(insertInfo, "ReleaseGroupRating", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupRating'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULE:        toChedck
void writeReleaseGroupArtistCredits(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupId = album.getReleaseGroupIdMap().get("mbid");
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupId) || StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("releaseGroupId or inputXml are empty");
        return;
    }

    String whiteListStr = "";
    try {
        whiteListStr = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.ReleaseGroup.artistRelationWhitelist");
    } catch (Exception ex) {
        log.warn("Could not find property 'MusicBrainz.Scraper.ReleaseGroup.artistRelationWhitelist', ignoring whitelist and taking all artist relatiopns!");
    }
    List<String> relationsWhiteList = new ArrayList<>(Arrays.asList(whiteListStr.split(",")));
    for(String whiteListEntry : relationsWhiteList) {
        List<HashMap<String, Object>> releaseGroupRelationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralRelations", "",
                "_section_::release-group",
                "_targettype_::@target-type='artist'",
                "_whitelist_::@type='" + whiteListEntry + "'");
        for (HashMap<String, Object> releaseGroupCredit : releaseGroupRelationList) {
            //get ReleaseGroupCreditId
            String releaseGroupCreditId = (String) releaseGroupCredit.get("id");
            log.info("relaeseGroup credit id {}", releaseGroupCreditId);

            try {
                album.writeReleaseGroupCredit(releaseGroupCreditId, "ReleaseGroupCreditIds", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistCreditNode)!\nError: {}", ex.getMessage());
            }

            HashMap<String, String> insertInfoUniqueId = new HashMap<>();
            insertInfoUniqueId.put("unid", releaseGroupCreditId);
            try {
                album.writeReleaseGroupCreditUniqueId(insertInfoUniqueId, "ReleaseGroupCreditIds", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistUniqueId)!\nError: {}", ex.getMessage());
            }

            //get ReleaseGroupCreditName and ReleaseGroupCreditSortName
            String releaseGroupCreditName = (String) releaseGroupCredit.get("name");
            log.info("\tname {}", releaseGroupCreditName);
            String releaseGroupCreditSortName = (String) releaseGroupCredit.get("sort-name");
            log.info("\tsort-name {}", releaseGroupCreditSortName);

            HashMap<String, Object> insertInfoName = new HashMap<>();
            insertInfoName.put("unid", releaseGroupCreditId);
            insertInfoName.put("nameType", "Name");
            insertInfoName.put("name", releaseGroupCreditName);
            insertInfoName.put("sortName", releaseGroupCreditSortName);

            try {
                //put ArtistName to MMDG
                album.writeReleaseGroupCreditName(insertInfoName, "ReleaseGroupCreditName", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistName)!\nError: {}", ex.getMessage());
            }

            //get ArtistCreditType
            HashMap<String, Object> insertInfoRole = new HashMap<>();
            String releaseGroupCreditType = (String) releaseGroupCredit.get("type");
            log.info("\ttype {}", releaseGroupCreditType);
            insertInfoRole.put("unid", releaseGroupCreditId);
            insertInfoRole.put("role", releaseGroupCreditType);
            insertInfoRole.put("roleType", "role");
            if(releaseGroupCredit.containsKey("attribute") && releaseGroupCredit.get("attribute").equals("additional")) {
                List<String> additionalInfoList = new ArrayList<>();
                additionalInfoList.add("additional=true");
                insertInfoRole.put("additionalInfo", additionalInfoList);
            }
            try {
                album.writeReleaseGroupCreditRole(insertInfoRole, "ReleaseGroupCreditRole", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistRole)!\nError: {}", ex.getMessage());
            }

            //get ArtistCreditPeriods
            HashMap<String, Object> insertInfoPeriod = new HashMap<>();
            insertInfoPeriod.put("unid", releaseGroupCreditId);
            String releaseGroupCreditPeriodBegin = (String) releaseGroupCredit.get("begin");
            if (StringUtils.isNotEmpty(releaseGroupCreditPeriodBegin)) {
                log.info("\tperiod begin {}", releaseGroupCreditPeriodBegin);
                insertInfoPeriod.put("date", releaseGroupCreditPeriodBegin);
                try {
                    album.writeReleaseGroupCreditDate(insertInfoPeriod, "begin", "ReleaseGroupCreditPeriods", this.scraperName);
                } catch (Exception ex) {
                    log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistPeriod->Begin)!\nError: {}", ex.getMessage());
                }
            }
            String releaseGroupCreditPeriodEnd = (String) releaseGroupCredit.get("end");
            if (StringUtils.isNotEmpty(releaseGroupCreditPeriodBegin)) {
                log.info("\tperiod end {}", releaseGroupCreditPeriodEnd);
                insertInfoPeriod.put("date", releaseGroupCreditPeriodEnd);
                try {
                    album.writeReleaseGroupCreditDate(insertInfoPeriod, "end", "ReleaseGroupCreditPeriods", this.scraperName);
                } catch (Exception ex) {
                    log.error("Error while executing 'writeReleaseGroupRelsArtists' (ArtistPeriod->End)!\nError: {}", ex.getMessage());
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeReleaseGroupDisambiguation(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("input xml is empty");
        return;
    }

    //get ReleaseGroupDisambigaution from MUBZ and put them to MMDG
    List<HashMap<String, Object>> releaseGroupDisambiguationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralDisambiguation", "", "_section_::release-group");
    for(HashMap<String, Object> releaseGroupDisambiguation : releaseGroupDisambiguationList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        log.info("ReleaseGroupDisambiguation '{}'",  releaseGroupDisambiguation.get("value"));
        insertInfo.put("disambiguation", releaseGroupDisambiguation.get("value"));
        try {
            album.writeReleaseGroupDisambiguation(insertInfo, "ReleaseGroupDisambiguation", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeReleaseGroupDisambiguation'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeReleaseGroupAnnotation(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current album/release group
    Album album = mi.getCurrentAlbum();
    String releaseGroupInfoXml = (String)input[0];
    if(StringUtils.isEmpty(releaseGroupInfoXml)) {
        log.error("input xml is empty");
        return;
    }

    //get ArtistAnnotations from MUBZ split them according splitRegExp and put them to MMDG
    List<HashMap<String, Object>> releaseGroupAnnotationList = getMultiInfoFromXml(releaseGroupInfoXml, "GeneralAnnotation", "", "_section_::release-group");
    String splitRegEx = this.mappingRules.getMappingRule(this.scraperId, "GeneralAnnotation").get("splitRegEx");
    for(HashMap<String, Object> annotationsText : releaseGroupAnnotationList) {
        for(String artistAnnotation: ((String)annotationsText.get("value")).split(splitRegEx)) {
            HashMap<String, Object> insertInfo = new HashMap<>();
            log.info("releaseGroupAnnotation '{}'",  artistAnnotation);
            insertInfo.put("annotation", artistAnnotation);
            try {
                album.writeReleaseGroupAnnotation(insertInfo, "ReleaseGroupAnnotation", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeReleaseGroupAnnotation'!\nError: {}", ex.getMessage());
            }
        }
    }
}

//-----------------------------------------------------------------------------

}