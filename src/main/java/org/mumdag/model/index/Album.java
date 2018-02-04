package org.mumdag.model.index;

//-----------------------------------------------------------------------------

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
private HashMap<String, String> releasegroupIdMap = new HashMap<>();
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
public Album(String albumFolderName, String albumCanonicalPath, Boolean isSelected) {
    this.albumFolderName = albumFolderName;
    this.albumCanonicalPath = albumCanonicalPath;
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
    Long insertKeyMedium = addMedium(keyBase, (String)indexInfo.get("mediumFolderName"), (Integer)indexInfo.get("mediumNumber"),
                                              (String)indexInfo.get("mediumCanonicalPath"), true);
    return this.media.get(insertKeyMedium).addEntry(insertKeyMedium, indexInfo);
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
    if(releasegroupIdMap.containsKey(key) && releaseIdMap.containsKey(key) && upperMap.containsKey(key)) {
        HashMap<String, String> innerMap = new HashMap<>();
        innerMap.put("releaseGroup", releasegroupIdMap.get(key));
        innerMap.put("release", releaseIdMap.get(key));
        innerMap.putAll(upperMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
        HashMap<String, HashMap<String, String>> lowerMap = new HashMap<>(this.resolveBaseMap);
        for (Map.Entry<Long, Medium> entry : media.entrySet()) {
            Medium medium = entry.getValue();
            medium.calcResolvBaseMaps(key, lowerMap);
        }
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


//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:  nok (keybaseValue)
private Long addMedium(Long keyBase, String mediumFolderName, Integer mediumNumber, String mediumCanonicalPath, Boolean isSelected) {
    Long insertKey = keyBase + (mediumNumber*100);
    if(!this.media.containsKey(insertKey)) {
        Medium medium = new Medium(mediumFolderName, mediumNumber, mediumCanonicalPath, isSelected);
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
    retMap.put("releasegroupIdMap", releasegroupIdMap);
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

public HashMap<String, String> getReleasegroupIdMap() {
    return releasegroupIdMap;
}

//-----------------------------------------------------------------------------

public void setReleasegroupIdMap(HashMap<String, String> releasegroupIdMap) {
    this.releasegroupIdMap = releasegroupIdMap;
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

}
