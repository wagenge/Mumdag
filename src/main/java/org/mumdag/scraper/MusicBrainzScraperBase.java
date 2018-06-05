package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.MappingRules;
import org.mumdag.model.index.Album;
import org.mumdag.model.index.Artist;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.scraper.utils.ScraperHttpClient;
import org.mumdag.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//-----------------------------------------------------------------------------

public class MusicBrainzScraperBase {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
protected static final Logger log = LogManager.getLogger(MusicBrainzScraper.class);
protected MusicIndex mi = MusicIndex.getInstance();
protected String scraperId;
protected String scraperName;
private ScraperHttpClient scraperHttpClient = ScraperHttpClient.getInstance();
protected MappingRules mappingRules=  MappingRules.getInstance();
private HashMap<String, String> urlToUniqueIdsRules = new HashMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
MusicBrainzScraperBase() {
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


//=============================================================================
/*
 * 	HELPER METHODS (package-private)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
List<String> getInfoFromXml(String xmlString, String ruleName, String... replacementStrings) {
    List<String> retList = new ArrayList<>();

    HashMap<String, String> ruleMap = this.mappingRules.getMappingRule(this.scraperId, ruleName);
    if(replacementStrings.length > 0) {
        String xpathUnresolved = ruleMap.get("xpathAbsolute");
        String xpath = XpathUtils.resolveXpathString(xpathUnresolved, replacementStrings);
        ruleMap.put("xpathAbsolute", xpath);
    }
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
@SuppressWarnings("unchecked")
List<HashMap<String, Object>> getMultiInfoFromXml(String xmlString, String ruleName, String consolidationKey, String... replacementStrings) {
    List<HashMap<String, Object>> retList = new ArrayList<>();

    HashMap<String, String> ruleMap = (HashMap<String, String>)this.mappingRules.getMappingRule(this.scraperId, ruleName).clone();
    if(replacementStrings.length > 0) {
        String xpathUnresolved = ruleMap.get("xpathAbsolute");
        String xpath = XpathUtils.resolveXpathString(xpathUnresolved, replacementStrings);
        ruleMap.put("xpathAbsolute", xpath);
    }

    String ruleType = ruleMap.get("type");
    String ruleSubType = ruleMap.get("subType");
    String absPath = ruleMap.get("xpathAbsolute");

    if(ruleType.equals("xpath") && ruleSubType.equals("node-content")) {
        retList = XmlUtils.getNodeContentByXPath(xmlString, absPath);
    } else if(ruleType.equals("xpath") && ruleSubType.equals("node-content-blacklist")) {
        List<String> blacklist = new ArrayList<>();
        if(StringUtils.isNotEmpty(ruleMap.get("blacklist"))) {
            blacklist = Arrays.asList(ruleMap.get("blacklist").split(","));
        }
        retList = XmlUtils.getNodeContentByXPathBlacklist(xmlString, absPath, blacklist);
    } else if(ruleType.equals("xpath") && ruleSubType.equals("text")) {
        List<String> tmpList = XmlUtils.getNodeTextByXPath(xmlString, absPath);
        retList = ListUtils.list2ListMap(tmpList, "value");
    }
    else if(ruleType.equals("xpath") && ruleSubType.equals("attribute")) {
        List<String> tmpList = XmlUtils.getNodeAttributeTextByXPath(xmlString, absPath, "");
        retList = ListUtils.list2ListMap(tmpList, "value");
    }

    if(StringUtils.isNotEmpty(consolidationKey)) {
        retList = ListUtils.consolidateListOfMaps(retList, consolidationKey);
    }
    return retList;
}

//-----------------------------------------------------------------------------


//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:  toCheck
@SuppressWarnings("unchecked")
HashMap<Integer, HashMap<String, Object>> requestMusicBrainzAreaInfo(String areaid, String artistType, Boolean begin) throws Exception {
    HashMap<Integer, HashMap<String, Object>> retAreaInfos = new HashMap<>();

    String baseUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Base.wsUrl");
    String areaUrl = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.url");
    String areaUrlParams = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.Area.urlParams");

    int seqNr = 1;
    int idx = (begin) ? 1 : 11;
    while(areaid.length() > 0) {
        HashMap<String, Object> areaInfo = new HashMap<>();
        String resolvedUrl = XpathUtils.resolveXpathString(baseUrl + areaUrl + areaUrlParams, "_areaid_::"  +areaid);
        String areaXml = requestScraper(resolvedUrl);

        //get area infos
        List<HashMap<String, Object>> areaInfoList = getMultiInfoFromXml(areaXml,"AreaInfo", "");
        HashMap<String, Object> areaInfoRaw = new HashMap<>();
        if(areaInfoList.size() > 0) {
            areaInfoRaw = areaInfoList.get(0);
        }

        if(areaInfoRaw.containsKey("name.list") && ((List<String>)areaInfoRaw.get("name.list")).size() > 0) {
            areaInfo.put("name", ((List<String>)areaInfoRaw.get("name.list")).get(0));
        } else if (areaInfoRaw.containsKey("name")) {
            areaInfo.put("name", areaInfoRaw.get("name"));
        }

        if(areaInfoRaw.containsKey("sort-name.list") && ((List<String>)areaInfoRaw.get("sort-name.list")).size() > 0) {
            areaInfo.put("sortName", ((List<String>)areaInfoRaw.get("sort-name.list")).get(0));
        } else if (areaInfoRaw.containsKey("sort-name")) {
            areaInfo.put("sortName", areaInfoRaw.get("sort-name"));
        }

        if(areaInfoRaw.containsKey("type.list") && ((List<String>)areaInfoRaw.get("type.list")).size() > 0) {
            areaInfo.put("type", ((List<String>)areaInfoRaw.get("type.list")).get(0));
        } else if (areaInfoRaw.containsKey("type")) {
            areaInfo.put("type", areaInfoRaw.get("type"));
        }

        if(areaInfoRaw.containsKey("iso-3166-1-code.list") && ((List<String>)areaInfoRaw.get("iso-3166-1-code.list")).size() > 0) {
            areaInfo.put("code", ((List<String>)areaInfoRaw.get("iso-3166-1-code.list")).get(0));
        } else if (areaInfoRaw.containsKey("iso-3166-1-code")) {
            areaInfo.put("code", areaInfoRaw.get("iso-3166-1-code"));
        }

        if(areaInfoRaw.containsKey("iso-3166-2-code.list") && ((List<String>)areaInfoRaw.get("iso-3166-2-code.list")).size() > 0) {
            areaInfo.put("code", ((List<String>)areaInfoRaw.get("iso-3166-2-code.list")).get(0));
        } else if (areaInfoRaw.containsKey("iso-3166-2-code")) {
            areaInfo.put("code", areaInfoRaw.get("iso-3166-2-code"));
        }

        areaInfo.put("unid", areaid);
        areaInfo.put("seq", String.valueOf(seqNr));
        if (begin && artistType.equals("Person")) {
            areaInfo.put("event", "born");
        } else if (!begin && artistType.equals("Person")) {
            areaInfo.put("event", "died");
        } else if (begin && !artistType.equals("Person")) {
            areaInfo.put("event", "founded");
        } else if (!begin && !artistType.equals("Person")) {
            areaInfo.put("event", "dissolved");
        }
        retAreaInfos.put(idx, areaInfo);
        seqNr++;
        idx++;

        //get area relation backwards from MUBZ
        List<String> areaRelationBackwardList = getInfoFromXml(areaXml, "AreaRelationBackward");
        if (areaRelationBackwardList.size() > 0) {
            areaid = areaRelationBackwardList.get(0);
        } else {
            areaid = "";
        }
    }
    return retAreaInfos;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
String requestScraper(String url) throws Exception {
    HashMap<String, String> scraperResponse = this.scraperHttpClient.run(url);
    if(scraperResponse.get("rspCode").equals("200")) {
        return scraperResponse.get("rspBody");
    }
    else {
        throw new Exception("Could not get any response from MusicBrainz! url='"+url+"', rspCode='"+scraperResponse.get("rspCode")+"'");
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void readUrlToUniqueIdFilter(String filePath) throws Exception {
    BufferedReader bfr = new BufferedReader(new FileReader(new File(filePath)));

    this.urlToUniqueIdsRules.clear();
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
void writeUrlToUniqueId(HashMap<String, String> insertInfo, String mbArtistId, String nodeSection, String copyRule) throws Exception {
    String urlRegEx = "";
    String scraperName = "";
    String writeNodeAttributes = "";
    String unid;
    String url = insertInfo.get("url");
    for (Map.Entry<String, String> entry : this.urlToUniqueIdsRules.entrySet()) {
        if(url.contains(entry.getKey())) {
            String urlRuleStr = entry.getValue();
            String[] triple = urlRuleStr.split("\\|\\|");
            if(triple.length == 3) {
                urlRegEx = triple[0].trim();
                scraperName = triple[1].trim();
                writeNodeAttributes = triple[2].trim();
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
                    Artist artist = mi.getCurrentArtist();
                    artist.writeArtistUniqueId(insertInfo, copyRule, this.scraperName, scraperName, writeNodeAttributes);
                    break;
                case "ReleaseGroup":
                    Album album = mi.getCurrentAlbum();
                    album.writeReleaseGroupUniqueId(insertInfo, copyRule, this.scraperName, scraperName, writeNodeAttributes);
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

    // Create a pattern and matcher object
    Pattern pattern = Pattern.compile(urlRegex);
    Matcher matcher = pattern.matcher(url);
    if (matcher.find()) {
        log.info("Found value: " + matcher.group(1));
        unid = matcher.group(1);
    } else {
        log.warn("NO MATCH");
    }
    return unid;
}

//-----------------------------------------------------------------------------

}