package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//-----------------------------------------------------------------------------

public class Medium {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */

private String mediumFolderName;
private String mediumCanonicalPath;
private Integer mediumNumber;
private Integer numOfTracks = 0;
private Integer numOfTracksSelected = 0;
private Boolean isSelected;
private HashMap<String, String> mediumIdMap = new HashMap<>();
private HashMap<String, HashMap<String, String>> resolveBaseMap = new HashMap<>();
private TreeMap<Long, Track> tracks = new TreeMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Medium(String mediumFolderName, Integer mediumNumber, String mediumCanonicalPath, Boolean isSelected) {
    this.mediumFolderName = mediumFolderName;
    this.mediumCanonicalPath = mediumCanonicalPath;
    this.mediumNumber = mediumNumber;
    this.isSelected = isSelected;
}


//=============================================================================
/*
 * 	PUBLIC METHODS (public)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Long addEntry(Long keyBase, HashMap<String, Object> indexInfo) {
    return addTrack(keyBase, (String)indexInfo.get("trackFileName"), (Integer)indexInfo.get("trackPos"),
            (String)indexInfo.get("trackCanonicalPath"), true);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public TreeMap<Long, HashMap<String, Object>> getFlatIndex(Long mediumKey) {
    TreeMap<Long, HashMap<String, Object>> retMap = new TreeMap<>();
    retMap.put(mediumKey, getMediumInfoFlat());
    for (Map.Entry<Long, Track> entry : tracks.entrySet()) {
        Long trackKey = entry.getKey();
        Track track = entry.getValue();
        if(track.getSelected()) {
            retMap.put(trackKey, track.getTrackInfoFlat());
        }
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracks() {
    this.numOfTracks = tracks.size();
    return this.numOfTracks;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracksSelected() {
    Integer numOfTracksSelected = 0;
    for (Map.Entry<Long, Track> entry : tracks.entrySet()) {
        Track track = entry.getValue();
        if(track.getSelected()) {
            numOfTracksSelected++;
        }
    }
    this.numOfTracksSelected = numOfTracksSelected;
    return numOfTracksSelected;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key, HashMap<String, HashMap<String, String>> upperMap) {
    if((mediumIdMap.containsKey(key) || mediumIdMap.containsKey("discNo")) && upperMap.containsKey(key)) {
        HashMap<String, String> innerMap = new HashMap<>();
        if(mediumIdMap.containsKey(key)) {
            innerMap.put("medium", mediumIdMap.get(key));
        }
        else {
            innerMap.put("medium", mediumIdMap.get("discNo"));
        }
        innerMap.putAll(upperMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
        HashMap<String, HashMap<String, String>> lowerMap = new HashMap<>(this.resolveBaseMap);
        for (Map.Entry<Long, Track> entry : tracks.entrySet()) {
            Track track= entry.getValue();
            track.calcResolvBaseMaps(key, lowerMap);
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Object findEntry(Long key) {
    if(tracks.containsKey(key)) {
        return tracks.get(key);
    }
    return null;
}


//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private Long addTrack(Long keyBase, String trackFileName, Integer trackPos, String trackCanonicalPath, Boolean isSelected) {
    Long insertKey = keyBase + trackPos;
    if(!this.tracks.containsKey(insertKey)) {
        Track track = new Track(trackFileName, trackPos, trackCanonicalPath, isSelected);
        track.getTrackIdMap().put("path", trackCanonicalPath);
        this.numOfTracks++;
        this.numOfTracksSelected++;
        this.tracks.put(insertKey, track);
    }
    return insertKey;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private HashMap<String, Object> getMediumInfoFlat() {
    HashMap<String, Object> retMap = new HashMap<>();
    retMap.put("type", "medium");
    retMap.put("mediumFolderName", mediumFolderName);
    retMap.put("mediumCanonicalPath", mediumCanonicalPath);
    retMap.put("mediumNumber", mediumNumber);
    retMap.put("isSelected", isSelected);
    retMap.put("numOfTracks", numOfTracks);
    retMap.put("numOfTracksSelected", numOfTracksSelected);
    retMap.put("mediumIdMap", mediumIdMap);
    retMap.put("resolveBaseMap", resolveBaseMap);
    return retMap;
}

//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

public String getMediumFolderName() {
    return mediumFolderName;
}

//-----------------------------------------------------------------------------

public void setMediumFolderName(String mediumFolderName) {
    this.mediumFolderName = mediumFolderName;
}

//-----------------------------------------------------------------------------

public String getMediumCanonicalPath() {
    return mediumCanonicalPath;
}

//-----------------------------------------------------------------------------

public void setMediumCanonicalPath(String mediumCanonicalPath) {
    this.mediumCanonicalPath = mediumCanonicalPath;
}

//-----------------------------------------------------------------------------

public Integer getMediumNumber() {
    return mediumNumber;
}

//-----------------------------------------------------------------------------

public void setMediumNumber(Integer mediumNumber) {
    this.mediumNumber = mediumNumber;
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

public Boolean getSelected() {
    return isSelected;
}

//-----------------------------------------------------------------------------

public void setSelected(Boolean selected) {
    isSelected = selected;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getMediumIdMap() {
return mediumIdMap;
}

//-----------------------------------------------------------------------------

public void setMediumIdMap(HashMap<String, String> mediumIdMap) {
    this.mediumIdMap = mediumIdMap;
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

public TreeMap<Long, Track> getTracks() {
    return tracks;
}

//-----------------------------------------------------------------------------

public void setTracks(TreeMap<Long, Track> tracks) {
    this.tracks = tracks;
}

//-----------------------------------------------------------------------------

}
