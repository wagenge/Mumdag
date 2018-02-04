package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//-----------------------------------------------------------------------------

public class MusicIndex {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static MusicIndex instance = null;

private Integer numOfArtists = 0;
private Integer numOfArtistsSelected = 0;
private Integer numOfTracks = 0;
private Integer numOfTracksSelected = 0;
private TreeMap<Long, Artist> artists = new TreeMap<>();
private HashMap<String, Integer> artistNumbers = new HashMap<>();
private Integer lastArtistNumber = 0;
private TreeMap<Long, HashMap<String, Object>> flatMusicIndex = new TreeMap<>();
private Long currentEntryId;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
private MusicIndex() {
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized MusicIndex getInstance() {
    if (instance == null) {
        instance = new MusicIndex();
    }
    return instance;
}


//=============================================================================
/*
 * PUBLIC METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Long addEntry(HashMap<String, Object> indexInfo) {
    Long insertKeyArtist = addArtist((String)indexInfo.get("artistFolderName"),
            (String)indexInfo.get("artistCanonicalPath"), true);
    return this.artists.get(insertKeyArtist).addEntry(insertKeyArtist, indexInfo);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Object getCurrentEntry() {
    return findEntry(this.currentEntryId);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public TreeMap<Long, HashMap<String, Object>> createFlatIndex() {
    TreeMap<Long, HashMap<String, Object>> retMap = new TreeMap<>();
    for (Map.Entry<Long, Artist> entry : artists.entrySet()) {
        Long artistKey = entry.getKey();
        Artist artist = entry.getValue();
        retMap.putAll(artist.getFlatIndex(artistKey));
    }
    this.flatMusicIndex = retMap;
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer calcNumOfTracks() {
    Integer numOfTracks = 0;
    for (Map.Entry<Long, Artist> entry : artists.entrySet()) {
        Artist artist = entry.getValue();
        numOfTracks = numOfTracks +(artist.calcNumOfTracks());
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
    for (Map.Entry<Long, Artist> entry : artists.entrySet()) {
        Artist artist = entry.getValue();
        numOfTracksSelected = numOfTracksSelected +(artist.calcNumOfTracksSelected());
    }
    this.numOfTracksSelected = numOfTracksSelected;
    return numOfTracksSelected;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key) {
    for (Map.Entry<Long, Artist> entry : artists.entrySet()) {
        Artist artist = entry.getValue();
        artist.calcResolvBaseMaps(key);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Object findEntry(Long key) {
    if(artists.containsKey(key)) {
        return artists.get(key);
    }
    else {
        for(Artist artist : artists.values()) {
            if(artist.findEntry(key) != null) {
                return artist.findEntry(key);
            }
        }
    }
    return null;
}


//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void updateEntry(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Artist) {
        ((Artist)obj).getArtistIdMap().put(key, value);
    }else if(obj instanceof Album) {
        ((Album)obj).getReleasegroupIdMap().put(key, value);
    }else if(obj instanceof Medium) {
        ((Medium)obj).getMediumIdMap().put(key, value);
    }else if(obj instanceof Track) {
            ((Track)obj).getTrackIdMap().put(key, value);
    }
    calcResolvBaseMaps(key);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setArtistId(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Artist) {
        ((Artist) obj).getArtistIdMap().put(key, value);
        calcResolvBaseMaps(key);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setReleaseGroupId(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Album) {
        ((Album)obj).getReleasegroupIdMap().put(key, value);
        calcResolvBaseMaps(key);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setReleaseId(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Album) {
        ((Album)obj).getReleaseIdMap().put(key, value);
        calcResolvBaseMaps(key);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setMediumId(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Medium) {
        ((Medium)obj).getMediumIdMap().put(key, value);
        calcResolvBaseMaps(key);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setMediumId(String mediumId, String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Medium) {
        ((Medium)obj).getMediumIdMap().put(key, value);
        calcResolvBaseMaps(mediumId);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void setTrackId(String key, String value) {
    Object obj = findEntry(this.currentEntryId);
    if(obj instanceof Track) {
        ((Track)obj).getTrackIdMap().put(key, value);
        calcResolvBaseMaps(key);
    }
}

//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:  nok (keybaseValue)
private Long addArtist(String artistFolderName, String artistCanonicalPath, Boolean isSelected) {
    Integer artistNumber;
    if(artistNumbers.containsKey(artistFolderName)) {
        artistNumber = artistNumbers.get(artistFolderName);
    }
    else {
        artistNumber = lastArtistNumber+1;
        lastArtistNumber = artistNumber;
        artistNumbers.put(artistFolderName, artistNumber);
    }

    Long insertKey = artistNumber*10000000000L;
    if(!this.artists.containsKey(insertKey)) {
        Artist artist = new Artist(artistFolderName, artistCanonicalPath, isSelected);
        artist.getArtistIdMap().put("path", artistCanonicalPath);
        this.numOfArtists++;
        this.numOfArtistsSelected++;
        this.artists.put(insertKey, artist);
    }
    return insertKey;
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

public Integer getNumOfArtists() {
    return numOfArtists;
}

//-----------------------------------------------------------------------------

public void setNumOfArtists(Integer numOfArtists) {
    this.numOfArtists = numOfArtists;
}

//-----------------------------------------------------------------------------

public Integer getNumOfArtistsSelected() {
    return numOfArtistsSelected;
}

//-----------------------------------------------------------------------------

public void setNumOfArtistsSelected(Integer numOfArtistsSelected) {
    this.numOfArtistsSelected = numOfArtistsSelected;
}

//-----------------------------------------------------------------------------

public TreeMap<Long, Artist> getArtists() {
    return artists;
}

//-----------------------------------------------------------------------------

public void setArtists(TreeMap<Long, Artist> artists) {
    this.artists = artists;
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

public HashMap<String, Integer> getArtistNumbers() {
    return artistNumbers;
}

//-----------------------------------------------------------------------------

public void setArtistNumbers(HashMap<String, Integer> artistNumbers) {
    this.artistNumbers = artistNumbers;
}

//-----------------------------------------------------------------------------

public Integer getLastArtistNumber() {
    return lastArtistNumber;
}

//-----------------------------------------------------------------------------

public void setLastArtistNumber(Integer lastArtistNumber) {
    this.lastArtistNumber = lastArtistNumber;
}

//-----------------------------------------------------------------------------

public TreeMap<Long, HashMap<String, Object>> getFlatMusicIndex() {
    return flatMusicIndex;
}

//-----------------------------------------------------------------------------

public void setFlatMusicIndex(TreeMap<Long, HashMap<String, Object>> flatMusicIndex) {
    this.flatMusicIndex = flatMusicIndex;
}

//-----------------------------------------------------------------------------

public Long getCurrentEntryId() {
    return currentEntryId;
}

//-----------------------------------------------------------------------------

public void setCurrentEntryId(Long currentEntryId) {
    this.currentEntryId = currentEntryId;
}

//-----------------------------------------------------------------------------

}
