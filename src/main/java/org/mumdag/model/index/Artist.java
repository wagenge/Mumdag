package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import org.mumdag.model.MumdagModel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//-----------------------------------------------------------------------------

public class Artist {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */

private String artistName = "";
private String artistFolderName;
private String artistCanonicalPath;
private Boolean isSelected;
private Integer numOfAlbums = 0;
private Integer numOfAlbumsSelected = 0;
private Integer numOfTracks = 0;
private Integer numOfTracksSelected = 0;
private MumdagModel mmdgModel;
private HashMap<String, String> artistIdMap = new HashMap<>();
private HashMap<String, HashMap<String, String>> resolveBaseMap = new HashMap<>();

private TreeMap<Long, Album> albums = new TreeMap<>();
private HashMap<String, Integer> albumNumbers = new HashMap<>();
private Integer lastAlbumNumber = 0;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Artist(String artistFolderName, String artistCanonicalPath, Boolean isSelected) {
    this.artistFolderName = artistFolderName;
    this.artistCanonicalPath = artistCanonicalPath;
    this.isSelected = isSelected;
}


//=============================================================================
/*
 * PUBLIC METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Long addEntry(Long keyBase, HashMap<String, Object> indexInfo) {
    Long insertKeyAlbum = addAlbum(keyBase, (String)indexInfo.get("albumFolderName"),
                                             (String)indexInfo.get("albumCanonicalPath"), true);
    return this.albums.get(insertKeyAlbum).addEntry(insertKeyAlbum, indexInfo);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public TreeMap<Long, HashMap<String, Object>> getFlatIndex(Long artistKey) {
    TreeMap<Long, HashMap<String, Object>> retMap = new TreeMap<>();
    retMap.put(artistKey, getArtistInfoFlat());
    for (Map.Entry<Long, Album> entry : albums.entrySet()) {
        Long albumKey = entry.getKey();
        Album album = entry.getValue();
        retMap.putAll(album.getFlatIndex(albumKey));
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracks() {
    Integer numOfTracks = 0;
    for (Map.Entry<Long, Album> entry : albums.entrySet()) {
        Album album = entry.getValue();
        numOfTracks = numOfTracks +(album.calcNumOfTracks());
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
    for (Map.Entry<Long, Album> entry : albums.entrySet()) {
        Album album = entry.getValue();
        numOfTracksSelected = numOfTracksSelected +(album.calcNumOfTracksSelected());
    }
    this.numOfTracksSelected = numOfTracksSelected;
    return numOfTracksSelected;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key) {
    if(artistIdMap.containsKey(key)) {
        HashMap<String, String> innerMap = new HashMap<>();
        innerMap.put("artist", artistIdMap.get(key));
        this.resolveBaseMap.put(key, innerMap);
        HashMap<String, HashMap<String, String>> lowerMap = new HashMap<>(this.resolveBaseMap);
        for (Map.Entry<Long, Album> entry : albums.entrySet()) {
            Album album = entry.getValue();
            album.calcResolvBaseMaps(key, lowerMap);
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Object findEntry(Long key) {
    if(albums.containsKey(key)) {
        return albums.get(key);
    }
    else {
        for(Album album : albums.values()) {
            if(album.findEntry(key) != null) {
                return album.findEntry(key);
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
private Long addAlbum(Long keyBase, String albumFolderName, String albumCanonicalPath, Boolean isSelected) {
    Integer albumNumber;
    if(albumNumbers.containsKey(albumFolderName)) {
        albumNumber = albumNumbers.get(albumFolderName);
    }
    else {
        albumNumber = lastAlbumNumber+1;
        lastAlbumNumber = albumNumber;
        albumNumbers.put(albumFolderName, albumNumber);
    }

    Long insertKey = keyBase + (albumNumber*10000);
    if(!this.albums.containsKey(insertKey)) {
        Album album = new Album(albumFolderName, albumCanonicalPath, isSelected);
        album.getReleasegroupIdMap().put("path", albumCanonicalPath);
        album.getReleaseIdMap().put("path", albumCanonicalPath);
        this.numOfAlbums++;
        this.numOfAlbumsSelected++;
        this.albums.put(insertKey, album);
    }
    return insertKey;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private HashMap<String, Object> getArtistInfoFlat() {
    HashMap<String, Object> retMap = new HashMap<>();
    retMap.put("type", "artist");
    retMap.put("artistName", artistName);
    retMap.put("artistFolderName", artistFolderName);
    retMap.put("artistCanonicalPath", artistCanonicalPath);
    retMap.put("isSelected", isSelected);
    retMap.put("numOfAlbums", numOfAlbums);
    retMap.put("numOfAlbumsSelected", numOfAlbumsSelected);
    retMap.put("numOfTracks", numOfTracks);
    retMap.put("numOfTracksSelected", numOfTracksSelected);
    retMap.put("mumdagModel", mmdgModel);
    retMap.put("artistIdMap", artistIdMap);
    retMap.put("resolveBaseMap", resolveBaseMap);
    return retMap;
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

public String getArtistName() {
    return artistName;
}

//-----------------------------------------------------------------------------

public void setArtistName(String artistName) {
    this.artistName = artistName;
}

//-----------------------------------------------------------------------------

public String getArtistFolderName() {
    return artistFolderName;
}

//-----------------------------------------------------------------------------

public void setArtistFolderName(String artistFolderName) {
    this.artistFolderName = artistFolderName;
}

//-----------------------------------------------------------------------------

public String getArtistCanonicalPath() {
    return artistCanonicalPath;
}

//-----------------------------------------------------------------------------

public void setArtistCanonicalPath(String artistCanonicalPath) {
    this.artistCanonicalPath = artistCanonicalPath;
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

public Integer getNumOfAlbums() {
    return numOfAlbums;
}

//-----------------------------------------------------------------------------

public void setNumOfAlbums(Integer numOfAlbums) {
    this.numOfAlbums = numOfAlbums;
}

//-----------------------------------------------------------------------------

public Integer getNumOfAlbumsSelected() {
    return numOfAlbumsSelected;
}

//-----------------------------------------------------------------------------

public void setNumOfAlbumsSelected(Integer numOfAlbumsSelected) {
    this.numOfAlbumsSelected = numOfAlbumsSelected;
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

public MumdagModel getMmdgModel() {
    return mmdgModel;
}

//-----------------------------------------------------------------------------

public void setMmdgModel(MumdagModel mmdgModel) {
    this.mmdgModel = mmdgModel;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getArtistIdMap() {
    return artistIdMap;
}

//-----------------------------------------------------------------------------

public void setArtistIdMap(HashMap<String, String> artistIdMap) {
    this.artistIdMap = artistIdMap;
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

public TreeMap<Long, Album> getAlbums() {
    return albums;
}

//-----------------------------------------------------------------------------

public void setAlbums(TreeMap<Long, Album> albums) {
    this.albums = albums;
}

//-----------------------------------------------------------------------------

}
