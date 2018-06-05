package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.mumdag.model.xml.ReleaseGroupXml;
import org.mumdag.model.xml.ReleaseXml;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
import org.mumdag.utils.XpathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//-----------------------------------------------------------------------------

public class Album {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private String albumName = "";
private Integer albumYear = 0;
private String albumFolderName;
private String albumCanonicalPath;
private Boolean isSelected;
private Integer numOfMedia = 0;
private Integer numOfTracks = 0;
private Integer numOfTracksSelected = 0;
private ReleaseGroupXml releaseGroupXml = null;
private ReleaseXml releaseXml = null;
private HashMap<String, String> releaseGroupIdMap = new HashMap<>();
private HashMap<String, String> releaseIdMap = new HashMap<>();
private HashMap<String, HashMap<String, String>> resolveBaseMap = new HashMap<>();
private TreeMap<Long, Medium> media = new TreeMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Album(String artistCanonicalPath, String albumFolderName, String albumCanonicalPath, Boolean isSelected) {
    this.albumFolderName = albumFolderName;
    this.albumCanonicalPath = albumCanonicalPath;
    this.isSelected = isSelected;

    if(this.releaseGroupXml == null) {
        try {
            String releaseGroupXmlTemplatePath = PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.templatePath");
            String artistMetadataPath = artistCanonicalPath + PropertyHandler.getInstance().getValue("Mumdag.metadataFolder") +
                    File.separator + PropertyHandler.getInstance().getValue("Mumdag.metadataFile");
            Path path = Paths.get(artistMetadataPath);
            if(Files.exists(path) && Files.isRegularFile(path)) {
                Document artistXmlDoc = XmlUtils.createXmlDoc(artistMetadataPath);
                String releaseGroupXpathStr = XpathUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.xpath"), "_rgid_::@path=\""+ albumCanonicalPath +"\"");
                Document releaseGroupXmlDoc = XmlUtils.createXmlDoc(artistXmlDoc, releaseGroupXpathStr);
                Document releaseGroupXmlTemplate = XmlUtils.createXmlDoc(releaseGroupXmlTemplatePath);
                if(releaseGroupXmlDoc != null) {
                    Node releaseList = releaseGroupXmlDoc.getElementsByTagName("ReleaseList").item(0);
                    while (releaseList.hasChildNodes()) {
                        releaseList.removeChild(releaseList.getFirstChild());
                    }
                    this.releaseGroupXml = new ReleaseGroupXml(releaseGroupXmlDoc, releaseGroupXmlTemplate);
                }
                else {
                    Document releaseGroupXmlMinimalTemplate = XmlUtils.createXmlDoc(PropertyHandler.getInstance().getValue("ReleaseGroupXmlModel.minimalTemplatePath"));
                    this.releaseGroupXml = new ReleaseGroupXml(releaseGroupXmlMinimalTemplate, releaseGroupXmlTemplate);
                }
                XmlUtils.setNodeAttributeByXPath(this.releaseGroupXml.getXmlDoc(), "/ReleaseGroup", "path", albumCanonicalPath);
            }
        } catch (Exception ex) {
            //ToDo: error handling
            //log.error
        }
    }

    if(this.releaseXml == null) {
        try {
            String releaseXmlTemplatePath = PropertyHandler.getInstance().getValue("ReleaseXmlModel.templatePath");
            String artistMetadataPath = artistCanonicalPath + PropertyHandler.getInstance().getValue("Mumdag.metadataFolder") +
                    File.separator + PropertyHandler.getInstance().getValue("Mumdag.metadataFile");
            Path path = Paths.get(artistMetadataPath);
            if(Files.exists(path) && Files.isRegularFile(path)) {
                Document artistXmlDoc = XmlUtils.createXmlDoc(artistMetadataPath);
                String releaseXpathStr = XpathUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ReleaseXmlModel.xpath"), "_rid_::@path=\""+ albumCanonicalPath +"\"");
                Document releaseXmlDoc = XmlUtils.createXmlDoc(artistXmlDoc, releaseXpathStr);
                Document releaseXmlTemplate = XmlUtils.createXmlDoc(releaseXmlTemplatePath);
                if(releaseXmlDoc != null) {
                    Node mediumList = releaseXmlDoc.getElementsByTagName("MediumList").item(0);
                    while (mediumList.hasChildNodes()) {
                        mediumList.removeChild(mediumList.getFirstChild());
                    }
                    this.releaseXml = new ReleaseXml(releaseXmlDoc, releaseXmlTemplate);
                }
                else {
                    Document releaseXmlMinimalTemplate = XmlUtils.createXmlDoc(PropertyHandler.getInstance().getValue("ReleaseXmlModel.minimalTemplatePath"));
                    this.releaseXml = new ReleaseXml(releaseXmlMinimalTemplate, releaseXmlTemplate);
                }
                XmlUtils.setNodeAttributeByXPath(this.releaseXml.getXmlDoc(), "/Release", "path", albumCanonicalPath);
            }
        } catch (Exception ex) {
            //ToDo: error handling
        }
    }
}


//=============================================================================
/*
 * METHODS TO BUILD AND MANAGE INDEX (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Long addEntry(Long keyBase, HashMap<String, Object> indexInfo) {
    Long insertKeyMedium = addMedium(keyBase, (String)indexInfo.get("artistCanonicalPath"), (String)indexInfo.get("mediumFolderName"),
                                            (Integer)indexInfo.get("mediumNumber"), (String)indexInfo.get("mediumCanonicalPath"), true);
    return this.media.get(insertKeyMedium).addEntry(insertKeyMedium, indexInfo);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void addReleaseGroupId(String key, String value) {
    getReleaseGroupIdMap().put(key, value);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void addReleaseId(String key, String value) {
    getReleaseIdMap().put(key, value);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public TreeMap<Long, HashMap<String, Object>> getFlatIndex(Long albumKey) {
    TreeMap<Long, HashMap<String, Object>> retMap = new TreeMap<>();
    retMap.put(albumKey, getAlbumInfoFlat());
    for (Map.Entry<Long, Medium> entry : media.entrySet()) {
        Long mediumKey = entry.getKey();
        Medium medium = entry.getValue();
        retMap.putAll(medium.getFlatIndex(mediumKey));
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracks() {
    Integer numOfTracks = 0;
    for (Map.Entry<Long, Medium> entry : media.entrySet()) {
        Medium medium = entry.getValue();
        numOfTracks = numOfTracks +(medium.calcNumOfTracks());
    }
    this.numOfTracks = numOfTracks;
    return numOfTracks;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracksSelected() {
    Integer numOfTracksSelected = 0;
    for (Map.Entry<Long, Medium> entry : media.entrySet()) {
        Medium medium = entry.getValue();
        numOfTracksSelected = numOfTracksSelected +(medium.calcNumOfTracksSelected());
    }
    this.numOfTracksSelected = numOfTracksSelected;
    return numOfTracksSelected;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key, HashMap<String, HashMap<String, String>> upperMap) {
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.putAll(upperMap.get(key));
    if(releaseGroupIdMap.containsKey(key) && upperMap.containsKey(key)) {
        innerMap.put("releaseGroup", releaseGroupIdMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
    }
    if(releaseIdMap.containsKey(key) && upperMap.containsKey(key)) {
        innerMap.put("release", releaseIdMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
    }
    if(releaseGroupIdMap.containsKey(key) && releaseIdMap.containsKey(key) && upperMap.containsKey(key)) {
        HashMap<String, HashMap<String, String>> lowerMap = new HashMap<>(this.resolveBaseMap);
        for (Map.Entry<Long, Medium> entry : media.entrySet()) {
            Medium medium = entry.getValue();
            medium.calcResolvBaseMaps(key, lowerMap);
        }
    }
    else {
        this.resolveBaseMap.put(key, innerMap);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Object findEntry(Long key) {
    if(media.containsKey(key)) {
        return media.get(key);
    }
    else {
        for(Medium medium: media.values()) {
            if(medium.findEntry(key) != null) {
                return medium.findEntry(key);
            }
        }
    }
    return null;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Track getFirstTrack() {
    if(media.values().stream().findFirst().isPresent()) {
        Medium firstArtist = media.values().stream().findFirst().get();
        return firstArtist.getFirstTrack();
    }
    else {
        return null;
    }
}

//-----------------------------------------------------------------------------

public void getFullXmlDocument(Document retXmlDoc) {
    Node releaseGroupRoot = this.releaseGroupXml.getXmlDoc().getDocumentElement();
    Node copiedReleaseGroupRoot = retXmlDoc.importNode(releaseGroupRoot, true);
    retXmlDoc.getElementsByTagName("ReleaseGroupList").item(0).appendChild(copiedReleaseGroupRoot);

    Node releaseRoot = this.releaseXml.getXmlDoc().getDocumentElement();
    Node copiedReleaseRoot = retXmlDoc.importNode(releaseRoot, true);
    Node releaseNode = XmlUtils.getFirstNodeByXPath(retXmlDoc, "//ReleaseGroup[last()]/ReleaseList");
    if(releaseNode != null) {
        releaseNode.appendChild(copiedReleaseRoot);
    }

    for (Map.Entry<Long, Medium> entry : media.entrySet()) {
        Medium medium = entry.getValue();
        medium.getFullXmlDocument(retXmlDoc);
    }
}


//=============================================================================
/*
 * METHODS TO GET RELEASEGROUP INFO FROM THE MODEL (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getReleaseGroupWSURL(String unidAttrName, String scraperName) throws Exception {
    String srcAttriName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    return this.releaseGroupXml.getReleaseGroupWSURL(unidAttrName, srcAttriName, scraperName);
}


//=============================================================================
/*
 * METHODS TO WRITE ALBUM INFO INTO THE MODEL (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    releaseGroupXml.writeReleaseGroupUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeNodeAttributes);
}


//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupAlias(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupType(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("type")) {
        throw new Exception("Wrong parameters! attribute 'type' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupType(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupDate(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("type")) {
        throw new Exception("Wrong parameters! attribute 'type' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupDate(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupTag(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("tag")) {
        throw new Exception("Wrong parameters! attribute 'tag' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupTag(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    releaseXml.writeReleaseUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeNodeAttributes);
}


//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupUrls(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("url")) {
        throw new Exception("Wrong parameters! attribute 'url' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupUrls(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupRating(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("rating")) {
        throw new Exception("Wrong parameters! attribute 'rating' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupRating(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCredit(String releaseGroupCreditId, String copyRule, String scraperName) throws Exception {
    if(StringUtils.isEmpty(releaseGroupCreditId)) {
        throw new Exception("Wrong parameters! attribute 'releaseGroupCreditId' should be not null!");
    }
    this.releaseGroupXml.writeReleaseGroupCredit(releaseGroupCreditId, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditUniqueId(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupCreditUniqueId(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupCreditName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("date")) {
        throw new Exception("Wrong parameters! attribute 'date' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupCreditDate(insertInfo, dateType, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupCreditRole(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("role")) {
        throw new Exception("Wrong parameters! attribute 'role' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("roleType")) {
        throw new Exception("Wrong parameters! attribute 'roleType' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupCreditRole(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeReleaseGroupDisambiguation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("disambiguation")) {
        throw new Exception("Wrong parameters! attribute 'disambiguation' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writeReleaseGroupDisambiguation(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeReleaseGroupAnnotation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.releaseGroupIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("annotation")) {
        throw new Exception("Wrong parameters! attribute 'annotation' is missing in the insertInfo map!");
    }
    this.releaseGroupXml.writReleaseGroupAnnotation(insertInfo, copyRule, scraperName);
}



//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:  nok (keybaseValue)
private Long addMedium(Long keyBase, String artistFolder, String mediumFolderName, Integer mediumNumber, String mediumCanonicalPath, Boolean isSelected) {
    Long insertKey = keyBase + (mediumNumber*100);
    if(!this.media.containsKey(insertKey)) {
        Medium medium = new Medium(artistFolder, mediumFolderName, mediumNumber, mediumCanonicalPath, isSelected);
        medium.getMediumIdMap().put("path", mediumCanonicalPath);
        this.numOfMedia++;
        this.media.put(insertKey, medium);
    }
    return insertKey;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private HashMap<String, Object> getAlbumInfoFlat() {
    HashMap<String, Object> retMap = new HashMap<>();
    retMap.put("type", "album");
    retMap.put("albumName", albumName);
    retMap.put("albumYear", albumYear);
    retMap.put("albumFolderName", albumFolderName);
    retMap.put("albumCanonicalPath", albumCanonicalPath);
    retMap.put("isSelected", isSelected);
    retMap.put("numOfMedia", numOfMedia);
    retMap.put("numOfTracks", numOfTracks);
    retMap.put("numOfTracksSelected", numOfTracksSelected);
    retMap.put("releaseGroupIdMap", releaseGroupIdMap);
    retMap.put("releaseIdMap", releaseIdMap);
    retMap.put("resolveBaseMap", resolveBaseMap);
    return retMap;
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
public String getAlbumName() {
    return albumName;
}

//-----------------------------------------------------------------------------

public void setAlbumName(String albumName) {
    this.albumName = albumName;
}

//-----------------------------------------------------------------------------

public Integer getAlbumYear() {
    return albumYear;
}

//-----------------------------------------------------------------------------

public void setAlbumYear(Integer albumYear) {
    this.albumYear = albumYear;
}

//-----------------------------------------------------------------------------

public String getAlbumFolderName() {
    return albumFolderName;
}

//-----------------------------------------------------------------------------

public void setAlbumFolderName(String albumFolderName) {
    this.albumFolderName = albumFolderName;
}

//-----------------------------------------------------------------------------

public String getAlbumCanonicalPath() {
    return albumCanonicalPath;
}

//-----------------------------------------------------------------------------

public void setAlbumCanonicalPath(String albumCanonicalPath) {
    this.albumCanonicalPath = albumCanonicalPath;
}

//-----------------------------------------------------------------------------

public Boolean getSelected() {
    return isSelected;
}

//-----------------------------------------------------------------------------

public void setSelected(Boolean selected) {
    isSelected = selected;
}

//-----------------------------------------------------------------------------

public Integer getNumOfMedia() {
    return numOfMedia;
}

//-----------------------------------------------------------------------------

public void setNumOfMedia(Integer numOfMedia) {
    this.numOfMedia = numOfMedia;
}

//-----------------------------------------------------------------------------

public Integer getNumOfTracks() {
    return numOfTracks;
}

//-----------------------------------------------------------------------------

public void setNumOfTracks(Integer numOfTracks) {
    this.numOfTracks = numOfTracks;
}

//-----------------------------------------------------------------------------

public Integer getNumOfTracksSelected() {
    return numOfTracksSelected;
}

//-----------------------------------------------------------------------------

public void setNumOfTracksSelected(Integer numOfTracksSelected) {
    this.numOfTracksSelected = numOfTracksSelected;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getReleaseGroupIdMap() {
    return releaseGroupIdMap;
}

//-----------------------------------------------------------------------------

public void setReleasegroupIdMap(HashMap<String, String> releaseGroupIdMap) {
    this.releaseGroupIdMap = releaseGroupIdMap;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getReleaseIdMap() {
    return releaseIdMap;
}

//-----------------------------------------------------------------------------

public void setReleaseIdMap(HashMap<String, String> releaseIdMap) {
    this.releaseIdMap = releaseIdMap;
}

//-----------------------------------------------------------------------------

public HashMap<String, HashMap<String, String>> getResolveBaseMap() {
    return resolveBaseMap;
}

//-----------------------------------------------------------------------------

public void setResolveBaseMap(HashMap<String, HashMap<String, String>> resolveBaseMap) {
    this.resolveBaseMap = resolveBaseMap;
}

//-----------------------------------------------------------------------------

public TreeMap<Long, Medium> getMedia() {
    return media;
}

//-----------------------------------------------------------------------------

public void setMedia(TreeMap<Long, Medium> media) {
    this.media = media;
}

//-----------------------------------------------------------------------------

public ReleaseGroupXml getReleaseGroupXml() {
    return releaseGroupXml;
}

//-----------------------------------------------------------------------------

public void setReleaseGroupXml(ReleaseGroupXml releaseGroupXml) {
    this.releaseGroupXml = releaseGroupXml;
}

//-----------------------------------------------------------------------------

public ReleaseXml getReleaseXml() {
    return releaseXml;
}

//-----------------------------------------------------------------------------

public void setReleaseXml(ReleaseXml releaseXml) {
    this.releaseXml = releaseXml;
}

//-----------------------------------------------------------------------------

}
