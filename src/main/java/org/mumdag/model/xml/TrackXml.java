package org.mumdag.model.xml;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
import org.w3c.dom.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//-----------------------------------------------------------------------------

public class TrackXml extends XmlDoc {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(TrackXml.class);


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public TrackXml(String xmlFilePath, String templateXmlFilePath) {
    super(xmlFilePath, templateXmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public TrackXml(String xmlFilePath) {
    super(xmlFilePath);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public TrackXml(Document xmlDoc, Document templateXmlDoc) {
    super(xmlDoc, templateXmlDoc);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public TrackXml(Document xmlDoc) {
    super(xmlDoc);
}


//=============================================================================
/*
 * 	METHODS FOR WRITING/READING TRACK BLOCK (public)
 */


// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    String origScraperId = PropertyHandler.getInstance().getValue(origScraperName + ".Scraper.id");
    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_trid_::");

    HashMap<String, Object> resolveInfoMap = new HashMap<>();
    resolveInfoMap.put("placeholder", "_trid_");
    resolveInfoMap.put("propSubSection", "Track");
    resolveInfoMap.put("resolveInfo", resolveInfoList);

    if(writeNodeAttributes.equals("ALL_ATTRIBUTES") || writeNodeAttributes.equals("ONLY_UNID")) {
        writeUniqueId(insertInfo, resolveInfoMap, "Track.UniqueId", copyRule+"UnId", origScraperId, scraperName, "", writeNodeAttributes);
    }
    writeUniqueIdInfos(insertInfo, resolveInfoMap, "Track.UniqueId", copyRule, origScraperId, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackFileInfo(String trackCanonicalPath, String trackRelativePath, String trackFileName, String trackFileExtension, Long trackFileSize) throws Exception {
    if(StringUtils.isNotEmpty(trackCanonicalPath)) {
        XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track", "path", trackCanonicalPath);
        if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/FileInfo/AbsolutePath")) {
            copyNodeFromTemplateToOutputXmlDoc("/Track/FileInfo/AbsolutePath", "/Track/FileInfo", true);
        }
        XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/AbsolutePath", trackCanonicalPath);
    }
    if(StringUtils.isNotEmpty(trackRelativePath)) {
        if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/FileInfo/RelativePath")) {
            copyNodeFromTemplateToOutputXmlDoc("/Track/FileInfo/RelativePath", "/Track/FileInfo", true);
        }
        XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/RelativePath", trackRelativePath);
    }
    if(StringUtils.isNotEmpty(trackFileName)) {
        if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/FileInfo/Name")) {
            copyNodeFromTemplateToOutputXmlDoc("/Track/FileInfo/Name", "/Track/FileInfo", true);
        }
        XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/Name", trackFileName);
    }
    if(StringUtils.isNotEmpty(trackFileExtension)) {
        if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/FileInfo/Extension")) {
            copyNodeFromTemplateToOutputXmlDoc("/Track/FileInfo/Extension", "/Track/FileInfo", true);
        }
        XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/Extension", trackFileExtension);
    }
    if(trackFileSize != null && trackFileSize > 0) {
        if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/FileInfo/Size")) {
            copyNodeFromTemplateToOutputXmlDoc("/Track/FileInfo/Size", "/Track/FileInfo", true);
        }
        XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/Size", String.valueOf(trackFileSize));
    }
}

//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackPath(String trackCanonicalPath) throws Exception {
    XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track", "path", trackCanonicalPath);
    XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/Path", trackCanonicalPath);
}
*/
//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackPosition(Integer trackPos, String trackNum) throws Exception {
    XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track", "trackPos", String.valueOf(trackPos));
    XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track", "trackNo", trackNum);
    XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/Position", String.valueOf(trackPos));
    XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/Position", "number", trackNum);
}

//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackFileName(String trackName) throws Exception {
    XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/FileInfo/Name", trackName);
}
*/
//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackAudioInfo(HashMap<String, String> insertInfo, String source) throws Exception {
    for (Map.Entry<String, String> entry : insertInfo.entrySet()) {
        String audioInfoKey = entry.getKey();
        String audioInfoValue = entry.getValue();
        switch (audioInfoKey) {
            case "TrackLength":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/LengthList")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/LengthList", "/Track/AudioInfo", true);
                }
                //add or update the length tag for track length in seconds (type='ss')
                List<String> xpathListLengthSeconds = XmlUtils.getXpathsForMatchingNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length[@type='ss' and @source='"+source+"' and text() = '"+audioInfoValue+"']");
                List<String> xpathListLengthSecondsEmpty = XmlUtils.getXpathsForMatchingEmptyNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length");
                String xpathLengthSeconds;
                if(xpathListLengthSeconds.size() == 0) {
                    if(xpathListLengthSecondsEmpty.size() == 0) {
                        copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/LengthList/Length", "/Track/AudioInfo/LengthList", true);
                        xpathLengthSeconds = "/Track/AudioInfo/LengthList/Length[last()]";
                    } else {
                        xpathLengthSeconds = xpathListLengthSecondsEmpty.get(0);
                    }
                } else {
                    xpathLengthSeconds = xpathListLengthSeconds.get(0);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthSeconds, "source", source);
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthSeconds, "type", "ss");
                XmlUtils.setNodeTextByXPath(this.getXmlDoc(), xpathLengthSeconds, audioInfoValue);

                //add or update the length tag for track length in hours:minutes:seconds (type='hhmmss')
                String trackLengthHoursMinutesSeconds = LocalTime.MIN.plusSeconds(Integer.valueOf(audioInfoValue)).toString();
                List<String> xpathListLengthHoursMinutesSeconds = XmlUtils.getXpathsForMatchingNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length[@type='hhmmss' and @source='"+source+"' and text() = '"+trackLengthHoursMinutesSeconds+"']");
                List<String> xpathListLengthHoursMinutesSecondsEmpty = XmlUtils.getXpathsForMatchingEmptyNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length");
                String xpathLengthHoursMinutesSeconds;
                if(xpathListLengthHoursMinutesSeconds.size() == 0) {
                    if(xpathListLengthHoursMinutesSecondsEmpty.size() == 0) {
                        copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/LengthList/Length", "/Track/AudioInfo/LengthList", true);
                        xpathLengthHoursMinutesSeconds = "/Track/AudioInfo/LengthList/Length[last()]";
                    } else {
                        xpathLengthHoursMinutesSeconds = xpathListLengthHoursMinutesSecondsEmpty.get(0);
                    }
                } else {
                    xpathLengthHoursMinutesSeconds = xpathListLengthHoursMinutesSeconds.get(0);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthHoursMinutesSeconds, "source", source);
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthHoursMinutesSeconds, "type", "hhmmss");
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthHoursMinutesSeconds, "pattern", "hh:mm:ss");
                XmlUtils.setNodeTextByXPath(this.getXmlDoc(), xpathLengthHoursMinutesSeconds, trackLengthHoursMinutesSeconds);
                break;
            case "PreciseTrackLength":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/LengthList")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/LengthList", "/Track/AudioInfo", true);
                }
                //add or update the length tag for track length in milliseconds (type='ms')
                Long trackLengthMilliseconds = Math.round(Double.valueOf(audioInfoValue) * 1000);
                List<String> xpathListLengthMilliseconds = XmlUtils.getXpathsForMatchingNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length[@type='ms' and @source='"+source+"' and text() = '"+String.valueOf(trackLengthMilliseconds)+"']");
                List<String> xpathListLengthHoursMillisecondsEmpty = XmlUtils.getXpathsForMatchingEmptyNodes(this.getXmlDoc(),
                        "/Track/AudioInfo/LengthList/Length");
                String xpathLengthMilliseconds;
                if(xpathListLengthMilliseconds.size() == 0) {
                    if(xpathListLengthHoursMillisecondsEmpty.size() == 0) {
                        copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/LengthList/Length", "/Track/AudioInfo/LengthList", true);
                        xpathLengthMilliseconds = "/Track/AudioInfo/LengthList/Length[last()]";
                    } else {
                        xpathLengthMilliseconds = xpathListLengthHoursMillisecondsEmpty.get(0);
                    }
                } else {
                    xpathLengthMilliseconds = xpathListLengthMilliseconds.get(0);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthMilliseconds, "source", source);
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), xpathLengthMilliseconds, "type", "ms");
                XmlUtils.setNodeTextByXPath(this.getXmlDoc(), xpathLengthMilliseconds, String.valueOf(trackLengthMilliseconds));
                break;
            case "IsVariableBitRate":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/BitRate")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/BitRate", "/Track/AudioInfo", true);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/AudioInfo/BitRate", "variable", audioInfoValue);
                break;
            case "IsLossless":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/BitRate")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/BitRate", "/Track/AudioInfo", true);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/AudioInfo/BitRate", "lossless", audioInfoValue);
                break;
            case "IsCopyrighted":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/IntellectualProperty")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/IntellectualProperty", "/Track/AudioInfo", true);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/AudioInfo/IntellectualProperty", "copyright", audioInfoValue);
                break;
            case "IsOriginal":
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/IntellectualProperty")) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/IntellectualProperty", "/Track/AudioInfo", true);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/AudioInfo/IntellectualProperty", "original", audioInfoValue);
                break;
            default:
                if(!XmlUtils.checkIfXpathExists(this.getXmlDoc(), "/Track/AudioInfo/" + audioInfoKey)) {
                    copyNodeFromTemplateToOutputXmlDoc("/Track/AudioInfo/" + audioInfoKey, "/Track/AudioInfo", true);
                }
                XmlUtils.setNodeAttributeByXPath(this.getXmlDoc(), "/Track/AudioInfo/" + audioInfoKey, "source", source);
                XmlUtils.setNodeTextByXPath(this.getXmlDoc(), "/Track/AudioInfo/" + audioInfoKey, audioInfoValue);
        }
    }
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeTrackTag(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    String tagName = insertInfo.get("tag").split("=")[0];
    String tagValue = insertInfo.get("tag").split("=")[1];

    HashMap<String, Object> updatedInsertInfo = new HashMap<>();
    String unid = "tagName"+"="+tagName;
    updatedInsertInfo.put("unid", unid);
    String source = "source"+"="+"ID3Tag";
    updatedInsertInfo.put("source", source);
    updatedInsertInfo.put("tag", tagValue);

    List<String> additionalInfo = new ArrayList<>();
    for(Map.Entry<String, String> entry : insertInfo.entrySet()) {
        String entryKey = entry.getKey();
        String entryValue = entry.getValue();
        if(!entryKey.equals("tag")) {
            additionalInfo.add(entryKey+"="+entryValue);
        }
    }
    updatedInsertInfo.put("additionalInfo", additionalInfo);

    List<String> resolveInfoList = new ArrayList<>();
    resolveInfoList.add("_trid_::");

    writeTagInfo(updatedInsertInfo, resolveInfoList, "Track.Tag", copyRule, scraperName);
}

//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void replaceTrackAttributes(String resolvedXpath, List<String> attributeList) throws Exception {
    XmlUtils.setNodeAttributesByXPath(this.getXmlDoc(), resolvedXpath, attributeList, true);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void addTrackAttributes(String resolvedXpath, List<String> attributeList) throws Exception {
    XmlUtils.setNodeAttributesByXPath(this.getXmlDoc(), resolvedXpath, attributeList, false);
}
*/
//-----------------------------------------------------------------------------
/*
// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void addTrackAttributes(String resolvedXpath, String... attributeList) throws Exception {
    XmlUtils.setNodeAttributesByXPath(this.getXmlDoc(), resolvedXpath, attributeList, false);
}
*/

//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
public Document getXmlDoc() {
    return super.getXmlDoc();
}

//-----------------------------------------------------------------------------

}
