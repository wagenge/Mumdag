package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import org.mumdag.model.xml.TrackXml;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;

import org.mumdag.utils.XpathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

//-----------------------------------------------------------------------------

public class Track {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private String trackFileName;
private Integer trackPos;
private String trackNum;
private String trackCanonicalPath;
private Boolean isSelected;
private TrackXml trackXml;
private HashMap<String, String> trackIdMap = new HashMap<>();
private HashMap<String, HashMap<String, String>> resolveBaseMap = new HashMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Track(String artistCanonicalPath, String trackFileName, Integer trackPos, String trackNum, String trackCanonicalPath, Boolean isSelected) {
    this.trackFileName = trackFileName;
    this.trackPos = trackPos;
    this.trackNum = trackNum;
    this.trackCanonicalPath = trackCanonicalPath;
    this.isSelected = isSelected;

    if(trackXml == null) {
        try {
            String trackXmlTemplatePath = PropertyHandler.getInstance().getValue("TrackXmlModel.templatePath");
            String artistMetadataPath = artistCanonicalPath + PropertyHandler.getInstance().getValue("Mumdag.metadataFolder") +
                    File.separator + PropertyHandler.getInstance().getValue("Mumdag.metadataFile");
            Path path = Paths.get(artistMetadataPath);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                Document artistXmlDoc = XmlUtils.createXmlDoc(artistMetadataPath);
                String trackXpathStr = XpathUtils.resolveXpathString(PropertyHandler.getInstance().getValue("TrackXmlModel.xpath"),
                        "_trid_::@path=\"" + trackCanonicalPath + "\"");
                Document trackXmlDoc = XmlUtils.createXmlDoc(artistXmlDoc, trackXpathStr);
                Document trackXmlTemplate = XmlUtils.createXmlDoc(trackXmlTemplatePath);
                if (trackXmlDoc != null) {
                    this.trackXml = new TrackXml(trackXmlDoc, trackXmlTemplate);
                } else {
                    Document trackXmlMinimalTemplate = XmlUtils.createXmlDoc(PropertyHandler.getInstance().getValue("TrackXmlModel.minimalTemplatePath"));
                    //this.mediumXml = new MediumXml(mediumXmlMinimalTemplate, mediumXmlTemplate);
                    this.trackXml = new TrackXml(trackXmlMinimalTemplate, trackXmlTemplate);
                }
                Path artist = Paths.get(artistCanonicalPath);
                Path track = Paths.get(trackCanonicalPath);
                String trackRelativePath = "./"+artist.relativize(track).toString().replaceAll("\\\\", "/");
                this.trackXml.writeTrackFileInfo(trackCanonicalPath, trackRelativePath, trackFileName,
                        trackFileName.substring(trackFileName.lastIndexOf(".")+1), FileUtils.sizeOf(new File(trackCanonicalPath)));
                this.trackXml.writeTrackPosition(this.trackPos, this.trackNum);
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
public HashMap<String, Object> getTrackInfoFlat() {
    HashMap<String, Object> retMap = new HashMap<>();
    retMap.put("type", "track");
    retMap.put("trackFileName", trackFileName);
    retMap.put("trackPos", trackPos);
    retMap.put("trackCanonicalPath", trackCanonicalPath);
    retMap.put("isSelected", isSelected);
    retMap.put("trackIdMap", trackIdMap);
    retMap.put("resolveBaseMap", resolveBaseMap);
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key, HashMap<String, HashMap<String, String>> upperMap) {
    HashMap<String, String> innerMap = new HashMap<>();
    innerMap.putAll(upperMap.get(key));
    if(trackIdMap.containsKey(key) && upperMap.containsKey(key)) {
        innerMap.put("track", trackIdMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
    }
    else {
        this.resolveBaseMap.put(key, innerMap);
    }
}
//-----------------------------------------------------------------------------

public void getFullXmlDocument(Document retXmlDoc) {
    Node trackRoot = this.trackXml.getXmlDoc().getDocumentElement();
    Node copiedTrackRoot = retXmlDoc.importNode(trackRoot, true);
    Node trackNode = XmlUtils.getFirstNodeByXPath(retXmlDoc, "//ReleaseGroup[last()]/ReleaseList/Release[last()]/MediumList/Medium[last()]/TrackList");
    if (trackNode != null) {
        trackNode.appendChild(copiedTrackRoot);
    }
}


//=============================================================================
/*
 * METHODS TO WRITE TRACK INFO INTO THE MODEL (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    trackXml.writeTrackUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeNodeAttributes);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackTag(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    trackXml.writeTrackTag(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackAudioInfo(HashMap<String, String> insertInfo, String source) throws Exception {
    trackXml.writeTrackAudioInfo(insertInfo, source);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackFileInfo(String trackCanonicalPath, String trackRelativePath, String trackFileName, String trackFileExtension, Long trackFileSize) throws Exception {
    trackXml.writeTrackFileInfo(trackCanonicalPath, trackRelativePath, trackFileName, trackFileExtension, trackFileSize);
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
public String getTrackName() {
    return trackFileName;
}

//-----------------------------------------------------------------------------

public void setTrackName(String trackName) {
    this.trackFileName = trackName;
}

//-----------------------------------------------------------------------------

public Integer getTrackPos() {
    return trackPos;
}

//-----------------------------------------------------------------------------

public void setTrackPos(Integer trackPos) {
    this.trackPos = trackPos;
}

//-----------------------------------------------------------------------------

public String getTrackCanonicalPath() {
    return trackCanonicalPath;
}

//-----------------------------------------------------------------------------

public void setTrackCanonicalPath(String trackCanonicalPath) {
    this.trackCanonicalPath = trackCanonicalPath;
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

public HashMap<String, String> getTrackIdMap() {
    return trackIdMap;
}

//-----------------------------------------------------------------------------

public void setTrackIdMap(HashMap<String, String> trackIdMap) {
    this.trackIdMap = trackIdMap;
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

public TrackXml getTrackXml() {
    return trackXml;
}

//-----------------------------------------------------------------------------

}