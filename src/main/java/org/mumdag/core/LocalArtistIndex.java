package org.mumdag.core;

//-----------------------------------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.OutputXmlDoc;
import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class LocalArtistIndex {
	
//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(OutputXmlDoc.class);

private Map<String, String> artistList = new TreeMap<>();


//=============================================================================
/*
 * 	CONSTRUCTOR METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public LocalArtistIndex(String startPath) throws Exception {
	int artistFolderDepth = Integer.parseInt(PropertyHandler.getInstance().getValue("LocalArtistIndex.artistFolderDepth"));
	String[] musicFileExtensions = StringUtils.stripAll(PropertyHandler.getInstance().getValue("LocalArtistIndex.musicFileExtensions").split(","));
	File dir = new File(startPath);

	try {
		log.info("Getting all files in '{}' ... including those in subdirectories", dir.getCanonicalPath());
		//String[] extensions = new String[] { "mp3" };
		List<File> files = (List<File>) FileUtils.listFiles(dir, musicFileExtensions, true);
		int artistIndex = 0;
		for (File file : files) {
			String trackCanonicalPath = file.getCanonicalPath().replace('\\', '/');
			int indexOfArtistFolderEnd = StringUtils.lastOrdinalIndexOf(trackCanonicalPath, "/", artistFolderDepth);
			int indexOfArtistFolderStart = StringUtils.lastOrdinalIndexOf(trackCanonicalPath, "/", artistFolderDepth+1);

			String artistName = trackCanonicalPath.substring(indexOfArtistFolderStart+1, indexOfArtistFolderEnd);
			String artistCanonicalPath = trackCanonicalPath.substring(0, indexOfArtistFolderEnd);

			if(!artistList.containsKey(artistName)) {
                artistIndex++;
				artistList.put(artistName, artistCanonicalPath);
                log.info("Artist #{} '{}' found at folder '{}'", artistIndex, artistName, artistCanonicalPath);
			}
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Map<String, String> getArtistList() {
	return this.artistList;
}

//-----------------------------------------------------------------------------
	
}