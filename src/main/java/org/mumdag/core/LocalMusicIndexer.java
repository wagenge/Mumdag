package org.mumdag.core;

//-----------------------------------------------------------------------------

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.RegexUtils;

import java.io.File;
import java.util.*;

//-----------------------------------------------------------------------------

public class LocalMusicIndexer {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(LocalMusicIndexer.class);
private MusicIndex musicIndex = MusicIndex.getInstance();
private String startPath;


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public LocalMusicIndexer(String startPath) {
    this.startPath = startPath;
}


//=============================================================================
/*
 * 	PUBLIC METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void buildIndex() {
   try {
       String[] musicFileExtensions = StringUtils.stripAll(PropertyHandler.getInstance().getValue("LocalMusicIndexer.musicFileExtensions").split(","));
       File dir = new File(this.startPath);
        log.info("Getting all files in '{}' ... including those in subdirectories", dir.getCanonicalPath());
        List<File> files = (List<File>) FileUtils.listFiles(dir, musicFileExtensions, true);
        for (File file : files) {
            HashMap<String, Object> indexMap = extractIndexInfoFromPath(file.getCanonicalPath().replaceAll("\\\\", "/").replaceAll("//", "/"));
            musicIndex.addEntry(indexMap);
        }
        musicIndex.calcNumOfTracks();
        musicIndex.calcNumOfTracksSelected();
        musicIndex.createFlatIndex();
        log.info("Number of Artists = {}", musicIndex.getNumOfArtists());
        log.info("Number of Entries = {}", musicIndex.getFlatMusicIndex().size());
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void buildIndex(List<String> fileList) {
    try {
        log.info("Getting all files from list");
        for (String filePath : fileList) {
            HashMap<String, Object> indexMap = extractIndexInfoFromPath(filePath.replaceAll("\\\\", "/").replaceAll("//", "/"));
            musicIndex.addEntry(indexMap);
        }
        musicIndex.calcNumOfTracks();
        musicIndex.calcNumOfTracksSelected();
        musicIndex.createFlatIndex();
        log.info("Number of Artists = {}", musicIndex.getNumOfArtists());
        log.info("Number of Entries = {}", musicIndex.getFlatMusicIndex().size());
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public TreeMap<Long, HashMap<String, Object>> getFlatMusicIndex() {
    return musicIndex.createFlatIndex();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer getNumOfTracks() {
    return musicIndex.calcNumOfTracks();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Integer getNumOfTracksSelected() {
    return musicIndex.calcNumOfTracksSelected();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void calcResolvBaseMaps(String key) {
    musicIndex.calcResolvBaseMaps(key);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
private HashMap<String, Object> extractIndexInfoFromPath(String path) {
    HashMap<String, Object> retMap = new HashMap<>();

    String[] folders = path.split("/");
    retMap.put("trackCanonicalPath", path);
    String trackFileName = folders[folders.length-1];
    retMap.put("trackFileName", trackFileName);

    String albumFolderName =  folders[folders.length-2];
    retMap.put("albumFolderName", albumFolderName);
    retMap.put("mediumFolderName", albumFolderName);
    String artistFolderName = folders[folders.length-3];
    String albumCanonicalPath = path.substring(0, path.indexOf(trackFileName));
    retMap.put("albumCanonicalPath", albumCanonicalPath);
    retMap.put("mediumCanonicalPath", albumCanonicalPath);

    retMap.put("artistFolderName", artistFolderName);
    String artistCanonicalPath = path.substring(0, path.indexOf(albumFolderName));
    retMap.put("artistCanonicalPath", artistCanonicalPath);

    List<String> regExPatterns = new ArrayList<>();
    regExPatterns.add("\\A(?<trackNum>[0-9]{1,2})[- ._]{3}(?<trackName>.*)\\.(?<trackExtension>mp3|m4a|flac)\\Z");
    regExPatterns.add("\\A(?<mediumNumber>[0-9]{1,2})[- ._]{1}(?<trackNum>[0-9]{1,2})[- ._]{3}(?<trackName>.*)\\.(?<trackExtension>mp3|m4a|flac)\\Z");

    for(String regex : regExPatterns) {
        HashMap<String, String> groupMap = RegexUtils.extractGroups(trackFileName, regex);
        //convert the string-string map to a string-object map
        retMap.putAll((HashMap<String,Object>)(Map)groupMap);
        //convert certain fields to integer field
        if(retMap.containsKey("trackNum")) {
            retMap.put("trackPos", Integer.valueOf((String)retMap.get("trackNum")));
        }
        if(retMap.containsKey("mediumNumber")) {
            retMap.put("mediumNumber", Integer.valueOf((String)retMap.get("mediumNumber")));
        }
        //if no mediumNumber field is in the map, add one with the value '1'
        else {
            retMap.put("mediumNumber", 1);
        }
        //if the regex matches we stop checking other regexes
        if(groupMap.size() > 0) {
            break;
        }
    }
    return retMap;
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

public MusicIndex getMusicIndex() {
    return musicIndex;
}

//-----------------------------------------------------------------------------

public void setMusicIndex(MusicIndex musicIndex) {
    this.musicIndex = musicIndex;
}

//-----------------------------------------------------------------------------

public String getStartPath() {
    return startPath;
}

//-----------------------------------------------------------------------------

public void setStartPath(String startPath) {
    this.startPath = startPath;
}

//-----------------------------------------------------------------------------

}

