package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import java.util.HashMap;

//-----------------------------------------------------------------------------

public class Track {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private String trackFileName;
private Integer trackPos;
private String trackCanonicalPath;
private Boolean isSelected;
private HashMap<String, String> trackIdMap = new HashMap<>();
private HashMap<String, HashMap<String, String>> resolveBaseMap = new HashMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Track(String trackFileName, Integer trackPos, String trackCanonicalPath, Boolean isSelected) {
    this.trackFileName = trackFileName;
    this.trackPos = trackPos;
    this.trackCanonicalPath = trackCanonicalPath;
    this.isSelected = isSelected;
}


//=============================================================================
/*
 * 	PUBLIC METHODS (public)
 */
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
    if(trackIdMap.containsKey(key) && upperMap.containsKey(key)) {
        HashMap<String, String> innerMap = new HashMap<>();
        innerMap.put("track", trackIdMap.get(key));
        innerMap.putAll(upperMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
    }
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

}