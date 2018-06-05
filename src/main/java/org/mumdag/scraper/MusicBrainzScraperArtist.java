package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.mumdag.model.index.Artist;
import org.mumdag.utils.ListUtils;
import org.mumdag.utils.PropertyHandler;

import java.util.*;

//-----------------------------------------------------------------------------

class MusicBrainzScraperArtist extends MusicBrainzScraperBase {

//=============================================================================
/*
 * 	SCRAPER METHODS FOR ARTIST (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
String requestArtistInfo() {
    //get current artist
    Artist artist = mi.getCurrentArtist();
    String wsUrl = "";
    String artistInfo = "";
    try {
        wsUrl = artist.getArtistWSURL("mbid", this.scraperName);
    } catch (Exception ex) {
        log.error("Could not find url for the web service, requesting the artist info from MusicBrainz");
    }
    try {
        artistInfo = requestScraper(wsUrl);
    } catch (Exception ex) {
        log.error(ex.getMessage());
    }
    return artistInfo;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistName(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    HashMap<String, Object> insertInfo = new HashMap<>();
    //get ArtistName from MUBZ
    try {
        List<HashMap<String, Object>> artistNameList = getMultiInfoFromXml(artistInfoXml, "ArtistName", "");
        insertInfo.put("name", artistNameList.get(0).get("value"));
        insertInfo.put("nameType", "Name");
    } catch (Exception ex) {
        log.error("Error while resolving artist name from musicbrainz response!\nError:", ex.getMessage());
        return;
    }

    //get ArtistSortName from MUBZ
    List<HashMap<String, Object>> artistSortNameList = getMultiInfoFromXml(artistInfoXml, "ArtistSortName", "");
    insertInfo.put("sortName", artistSortNameList.get(0).get("value"));

    //put ArtistName to MMDG
    try {
        artist.writeArtistName(insertInfo, "ArtistName", this.scraperName);
    } catch (Exception ex) {
        log.error("Error while executing 'writeArtistName'!\nError: {}", ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistAlias(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    //get ArtistAliasName plus according Attributes from MUBZ
    List<HashMap<String, Object>> artistAliasList = getMultiInfoFromXml(artistInfoXml, "GeneralAlias", "", "_section_::artist");
    for(HashMap<String, Object> artistAlias : artistAliasList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("name", artistAlias.get("alias"));

        //put nameType and additional attributes to insertInfo
        insertInfo.put("nameType", "Alias");
        List<String> artistAliasInfo = ListUtils.map2ListWithBlacklist(artistAlias, "alias");
        insertInfo.put("additionalInfo", artistAliasInfo);
        log.info("alias name '{}' and attributes: {}",  artistAlias.get("alias"), String.join(", ", artistAliasInfo));

        //put ArtistAlias to MMDG
        try {
            artist.writeArtistAlias(insertInfo, "ArtistAliasName", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistAlias'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistTypeAndGender(Object[] input) {
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    //get ArtistType from MUBZ
    List<HashMap<String, Object>> artistTypeList = getMultiInfoFromXml(artistInfoXml, "ArtistType", "");
    if(artistTypeList.size() > 0) {
        String artistType = (String)artistTypeList.get(0).get("value");
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("type", artistType);
        insertInfo.put("typeType", "ArtistType");

        try {
            artist.writeArtistTypeAndGender(insertInfo, "ArtistType", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistTypeAndGender'!\nError: {}", ex.getMessage());
        }

        //get ArtistGender from MUBZ
        if (artistType.equals("Person")) {
            List<HashMap<String, Object>> artistGenderList = getMultiInfoFromXml(artistInfoXml, "ArtistGender", "");
            HashMap<String, Object> insertGenderInfo = new HashMap<>();
            insertGenderInfo.put("type", artistGenderList.get(0).get("value"));
            insertGenderInfo.put("typeType", "Gender");

            try {
                artist.writeArtistTypeAndGender(insertGenderInfo, "ArtistType", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistTypeAndGender'!\nError: {}", ex.getMessage());
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
void writeArtistPlaceList(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    //get ArtistType from MUBZ
    List<HashMap<String, Object>> artistTypeList = getMultiInfoFromXml(artistInfoXml, "ArtistType", "");
    String artistType = (String)artistTypeList.get(0).get("value");

//EXTRACT AREA BEGIN
    //get are begin id from MUBZ (artistAreaBeginId rule)
    List<HashMap<String, Object>> artistAreaBeginIdList = getMultiInfoFromXml(artistInfoXml, "ArtistAreaBeginId", "");
    String artistAreaBeginId = "";
    if(artistAreaBeginIdList.size() > 0) {
        artistAreaBeginId = (String)artistAreaBeginIdList.get(0).get("value");
    }
    log.info("ArtistAreaBeginId ='{}'", artistAreaBeginId);

//EXTRACT AREA END
    //get area end id from MUBZ (artistAreaEndId rule)
    List<HashMap<String, Object>> artistAreaEndIdList = getMultiInfoFromXml(artistInfoXml, "ArtistAreaEndId", "");
    String artistAreaEndId = "";
    if(artistAreaEndIdList.size() > 0) {
        artistAreaEndId = (String)artistAreaEndIdList.get(0).get("value");
    }
    log.info("ArtistAreaEndId ='{}'", artistAreaEndId);

//RESOLVE AREA CHAINS BEGIN/END
    HashMap<Integer, HashMap<String, Object>> areaInfos = new HashMap<>();
    try {
        areaInfos = requestMusicBrainzAreaInfo(artistAreaBeginId, artistType, true);
        areaInfos.putAll(requestMusicBrainzAreaInfo(artistAreaEndId, artistType, false));
    } catch (Exception ex) {
        log.error("Error while executing 'writeArtistPlace'!\nError: {}", ex.getMessage());
    }

    for (HashMap<String, Object> insertInfo : areaInfos.values()) {
        try {
            artist.writeArtisPlace(insertInfo, "ArtistPlace", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistPlace'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistDate(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    //get ArtistType from MUBZ
    List<HashMap<String, Object>> artistTypeList = getMultiInfoFromXml(artistInfoXml, "ArtistType", "");
    HashMap<String, Object> insertInfo = new HashMap<>();
    insertInfo.put("type", artistTypeList.get(0).get("value"));

//EXTRACT AND WRITE DATE BEGIN/END
    List<HashMap<String, Object>> artistLifeSpanList = getMultiInfoFromXml(artistInfoXml, "ArtistLifeSpan", "");
    if(artistLifeSpanList.size() > 0) {
        HashMap<String, Object> artistLifeSpan = artistLifeSpanList.get(0);
        if(artistLifeSpan.containsKey("begin")) {
            insertInfo.put("date", artistLifeSpan.get("begin"));
            try {
                artist.writeArtistDate(insertInfo, "begin", "ArtistDate", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistDate (begin)'!\nError: {}", ex.getMessage());
            }
        }
        if(artistLifeSpan.containsKey("end")) {
            insertInfo.put("date", artistLifeSpan.get("end"));
            try {
                artist.writeArtistDate(insertInfo, "end", "ArtistDate", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistDate (end)'!\nError: {}", ex.getMessage());
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
void writeArtistTags(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty");
        return;
    }

//EXTRACT AND WRITE ARTIST TAGS
    //get Tags from MUBZ
    List<HashMap<String, Object>> tagsList = getMultiInfoFromXml(artistInfoXml, "GeneralTags", "", "_section_::artist");
    for(HashMap<String, Object> tag : tagsList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("tag", tag.get("name"));
        List<String> additionalInfoList = new ArrayList<>();
        additionalInfoList.add("count="+tag.get("count"));
        insertInfo.put("additionalInfo", additionalInfoList);
        try {
            artist.writeArtistTag(insertInfo, "ArtistTags", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistTags'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistUrls(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistId = artist.getArtistIdMap().get("mbid");
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistId) || StringUtils.isEmpty(artistInfoXml)) {
        log.error("artistId or inputXml are empty");
        return;
    }

    try {
        //read UniqueId-Url-Filter
        String ruleFileName = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Artist.urlToUniqueIdFilterFile");
        readUrlToUniqueIdFilter(ruleFileName);
    } catch (Exception ex) {
        log.error("Error while executing 'writeArtistUrls'!\nError: {}", ex.getMessage());
        return;
    }

    List<HashMap<String, Object>> artistUrlList = getMultiInfoFromXml(artistInfoXml, "GeneralRelations", "",
          "_section_::artist",
                            "_targettype_::@target-type='url'",
                            "_whitelist_::");
    for(HashMap<String, Object> artistUrlInfo : artistUrlList) {
        HashMap<String, String> insertInfo = new HashMap<>();
        String artistUrl = (String)artistUrlInfo.get("target");
        String artistUrlType = (String)artistUrlInfo.get("type");
        log.info("url: '{}' and type: {}",  artistUrl, artistUrlType);

        //preparing artist url infos
        insertInfo.put("unid", artistId);
        insertInfo.put("url", artistUrl.replaceAll("=", "%3D"));
        insertInfo.put("type", artistUrlType);

        try {
            //write the current url
            artist.writeArtistUrls(insertInfo, "ArtistUrl", this.scraperName);
            //extract the unique id from the current url (if the pattern matches) and write it as unique id
            writeUrlToUniqueId(insertInfo, artistId, "Artist", "ArtistId");
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistUrls'!\nError: {}", ex.getMessage());
            return;
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistRating(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty");
        return;
    }

//EXTRACT AND WRITE ARTIST RATING
    //get rating from MUBZ
    List<HashMap<String, Object>> artistRatingList = getMultiInfoFromXml(artistInfoXml, "GeneralRating", "", "_section_::artist");
    for(HashMap<String, Object> artistRating : artistRatingList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        insertInfo.put("rating", artistRating.get("rating"));
        List<String> additionalInfoList = new ArrayList<>();
        additionalInfoList.add("votes="+artistRating.get("votes-count"));
        insertInfo.put("additionalInfo", additionalInfoList);
        try {
            artist.writeArtistRating(insertInfo, "ArtistRating", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistRating'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistIpiIsni(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty!");
        return;
    }

    //get ArtistIPIs from MUBZ and put them to MMDG using method 'writeArtistUniqueId'
    List<HashMap<String, Object>> artistIpiList = getMultiInfoFromXml(artistInfoXml, "ArtistIPI", "");
    for(HashMap<String, Object> artistIpi : artistIpiList) {
        HashMap<String, String> insertInfo = new HashMap<>();
        insertInfo.put("unid", (String)artistIpi.get("value"));
        try {
            artist.writeArtistUniqueId(insertInfo, "ArtistId", this.scraperName, "IPI", "NO_ATTRIBUTES");
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistIpiIsni'!\nError: {}", ex.getMessage());
        }
    }

    //get ArtistISNIs from MUBZ and put them to MMDG using method 'writeArtistUniqueId'
    List<HashMap<String, Object>> artistIsniList = getMultiInfoFromXml(artistInfoXml, "ArtistISNI", "");
    for(HashMap<String, Object> artistIsni : artistIsniList) {
        HashMap<String, String> insertInfo = new HashMap<>();
        insertInfo.put("unid", (String)artistIsni.get("value"));
        try {
            artist.writeArtistUniqueId(insertInfo, "ArtistId", this.scraperName, "ISNI", "NO_ATTRIBUTES");
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistIpiIsni'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
@SuppressWarnings("unchecked")
void writeArtistCredits(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist and the path of its first track
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];

    String whiteListStr = "";
    try {
        whiteListStr = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Artist.artistRelationWhitelist");
    } catch (Exception ex) {
        log.warn("Could not find property 'MusicBrainz.Scraper.ReleaseGroup.rgRelationWhitelist', ignoring whitelist and taking all release-group relations!");
    }
    List<String> artistrelationWhiteList = new ArrayList<>(Arrays.asList(whiteListStr.split(",")));
    for(String whiteListEntry : artistrelationWhiteList) {
        List<HashMap<String, Object>> artistCreditList = getMultiInfoFromXml(artistInfoXml, "GeneralRelations", "id",
                "_section_::artist",
                "_targettype_::@target-type='artist'",
                "_whitelist_::@type='" + whiteListEntry + "'");

        for(HashMap<String, Object> artistCredit : artistCreditList) {
            //put ArtistCreditNode with artistCreditId and UniqueId to MMDG
            String artistCreditId = (String)artistCredit.get("id");
            log.info("artist credit id {}", artistCreditId);
            try {
                artist.writeArtistCredit(artistCreditId, "ArtistCreditIds", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistCredits' (ArtistCreditNode)!\nError: {}", ex.getMessage());
            }
            HashMap<String, String> insertInfoUniqueId = new HashMap<>();
            insertInfoUniqueId.put("unid", artistCreditId);
            try {
                artist.writeArtistCreditUniqueId(insertInfoUniqueId, "ArtistCreditIds", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistCredits' (ArtistCrediUniqueId)!\nError: {}", ex.getMessage());
            }

            //put ArtistCreditName and ArtistCreditSortName to MMDG
            String artistCreditName = (String)artistCredit.get("name");
            log.info("\tname {}", artistCreditName);
            String artistCreditSortName = (String)artistCredit.get("sort-name");
            log.info("\tsort-name {}", artistCreditSortName);
            HashMap<String, Object> insertInfoName = new HashMap<>();
            insertInfoName.put("unid", artistCreditId);
            insertInfoName.put("nameType", "Name");
            insertInfoName.put("name", artistCreditName);
            insertInfoName.put("sortName", artistCreditSortName);
            try {
                artist.writeArtistCreditName(insertInfoName, "ArtistCreditName", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistCredits' (ArtistCreditName)!\nError: {}", ex.getMessage());
            }

            //get ArtistCreditAttributes (e.g. instruments)
            HashMap<String, Object> insertInfoRoleMember = new HashMap<>();
            insertInfoRoleMember.put("unid", artistCreditId);
            List<String> artistCreditAttributesList = new ArrayList<>();
            if(artistCredit.containsKey("attribute.list")) {
                artistCreditAttributesList = (List<String>) artistCredit.get("attribute.list");
            } else if (artistCredit.containsKey("attribute")) {
                artistCreditAttributesList.add((String)artistCredit.get("attribute"));
            }
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
                try {
                    artist.writeArtistCreditRole(insertInfoRoleInstrument, "ArtistCreditRole", this.scraperName);
                } catch (Exception ex) {
                    log.error("Error while executing 'writeArtistCredits' (ArtistCreditRole->Instrument)!\nError: {}", ex.getMessage());
                }
            }

            //get ArtistCreditType
            String membership = (String)artistCredit.get("type");
            log.info("\ttype {}", membership);
            insertInfoRoleMember.put("role", membership);
            insertInfoRoleMember.put("roleType", "membership");
            try {
                artist.writeArtistCreditRole(insertInfoRoleMember, "ArtistCreditRole", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistCredits' (ArtistCreditRole->Membership)!\nError: {}", ex.getMessage());
            }

            //get ArtistCreditPeriodsBegin
            List<String> artistCreditPeriodsBeginList = new ArrayList<>();
            if(artistCredit.containsKey("begin.list")) {
                artistCreditPeriodsBeginList = (List<String>) artistCredit.get("begin.list");
            } else if (artistCredit.containsKey("begin")) {
                artistCreditPeriodsBeginList.add((String)artistCredit.get("begin"));
            }
            ListUtils.concatenateStringToListEntries(artistCreditPeriodsBeginList, ",begin");
            //get ArtistCreditPeriodsEnd
            List<String> artistCreditPeriodsEndList = new ArrayList<>();
            if(artistCredit.containsKey("end.list")) {
                artistCreditPeriodsEndList = (List<String>) artistCredit.get("end.list");
            } else if (artistCredit.containsKey("end")) {
                artistCreditPeriodsEndList.add((String)artistCredit.get("end"));
            }
            ListUtils.concatenateStringToListEntries(artistCreditPeriodsEndList, ",end");
            List<String> artistCreditPeriodsList = new ArrayList<>(artistCreditPeriodsBeginList);
            artistCreditPeriodsList.addAll(artistCreditPeriodsEndList);
            Collections.sort(artistCreditPeriodsList);

            HashMap<String, Object> insertInfoPeriod = new HashMap<>();
            insertInfoPeriod.put("unid", artistCreditId);
            for(String artistCreditPeriodsStr : artistCreditPeriodsList) {
                log.info("\tperiod {}", artistCreditPeriodsStr);
                String[] artistCreditPeriodsArr = artistCreditPeriodsStr.split(",");
                String artistCreditPeriodsDate = artistCreditPeriodsArr[0];
                String artistCreditPeriodsType = artistCreditPeriodsArr[1];
                insertInfoPeriod.put("date", artistCreditPeriodsDate);
                try {
                    artist.writeArtistCreditDate(insertInfoPeriod, artistCreditPeriodsType, "ArtistCreditPeriods", this.scraperName);
                } catch (Exception ex) {
                    log.error("Error while executing 'writeArtistCredits' (ArtistCreditPeriod)!\nError: {}", ex.getMessage());
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
void writeArtistDisambiguation(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty");
        return;
    }

    //get ArtistDisambigaution from MUBZ and put them to MMDG
    List<HashMap<String, Object>> artistDisambiguationList = getMultiInfoFromXml(artistInfoXml, "GeneralDisambiguation", "", "_section_::artist");
    for(HashMap<String, Object> artistDisambiguation : artistDisambiguationList) {
        HashMap<String, Object> insertInfo = new HashMap<>();
        log.info("ArtistDisambiguation '{}'",  artistDisambiguation.get("value"));
        insertInfo.put("disambiguation", artistDisambiguation.get("value"));
        try {
            artist.writeArtistDisambiguation(insertInfo, "ArtistDisambiguation", this.scraperName);
        } catch (Exception ex) {
            log.error("Error while executing 'writeArtistDisambiguation'!\nError: {}", ex.getMessage());
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:	toCheck
//COPY RULES:       ok
void writeArtistAnnotation(Object[] input) {
// GENERAL THINGS ...
    if(input == null || input.length < 1 || input[0] == null) {
        log.error("input arguments not correct. expecting input[0] = inputXml");
        return;
    }
    //get current artist
    Artist artist = mi.getCurrentArtist();
    String artistInfoXml = (String)input[0];
    if(StringUtils.isEmpty(artistInfoXml)) {
        log.error("input xml is empty");
        return;
    }

    //get ArtistAnnotations from MUBZ split them according splitRegExp and put them to MMDG
    List<HashMap<String, Object>> artistAnnotationList = getMultiInfoFromXml(artistInfoXml, "GeneralAnnotation", "", "_section_::artist");
    String splitRegEx = this.mappingRules.getMappingRule(this.scraperId, "GeneralAnnotation").get("splitRegEx");
    for(HashMap<String, Object> annotationsText : artistAnnotationList) {
        for(String artistAnnotation: ((String)annotationsText.get("value")).split(splitRegEx)) {
            HashMap<String, Object> insertInfo = new HashMap<>();
            log.info("artistAnnotation '{}'",  artistAnnotation);
            insertInfo.put("annotation", artistAnnotation);
            try {
                artist.writeArtistAnnotation(insertInfo, "ArtistAnnotation", this.scraperName);
            } catch (Exception ex) {
                log.error("Error while executing 'writeArtistAnnotation'!\nError: {}", ex.getMessage());
            }
        }
    }
}

//-----------------------------------------------------------------------------

}