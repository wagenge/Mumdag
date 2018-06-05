package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.index.*;
import org.mumdag.scraper.utils.MP3Tags;
import org.mumdag.core.MappingRules;
import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class LocalMusicScraper {
	
//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */

private static LocalMusicScraper instance = null;
private static final Logger log = LogManager.getLogger(LocalMusicScraper.class);
private MusicIndex mi = MusicIndex.getInstance();

private String scraperName;
private String scraperId;
private MappingRules mappingRules;


//=============================================================================	
/*
 * 	CONSTRUCTOR METHODS (private, with public getInstance methods)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private LocalMusicScraper() {
    this.scraperName = "LocalMusic";

    //load MusicBrainz specific properties
    String lclmPropFile = "./src/main/resources/configFiles/LocalMusic.properties";
    try {
        PropertyHandler.getInstance().addPropertiesFromFile(lclmPropFile, "lclm");
        this.scraperId = PropertyHandler.getInstance().getValue(this.scraperName+".Scraper.id");
        String mappingRulesFilePath = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesFilePath");
        String mappingRulesType = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesType");
        this.mappingRules = MappingRules.getInstance();
        this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
    } catch (Exception ex) {
        log.error("could not load property file {}\nError: {}", lclmPropFile, ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private LocalMusicScraper(String lclmPropFile) {
    this.scraperName = "LocalMusic";

    //load LocalMusic specific properties
    try {
        PropertyHandler.getInstance().addPropertiesFromFile(lclmPropFile, "lclm");
        this.scraperId = PropertyHandler.getInstance().getValue(this.scraperName+".Scraper.id");
        String mappingRulesFilePath = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesFilePath");
        String mappingRulesType = PropertyHandler.getInstance().getValue(this.scraperName+".mappingRulesType");
        this.mappingRules = MappingRules.getInstance();
        this.mappingRules.updateMappingRules(mappingRulesFilePath, this.scraperId, mappingRulesType);
    } catch (Exception ex) {
        log.error("could not load property file {}\nError: {}", lclmPropFile, ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized LocalMusicScraper getInstance() {
    if (instance == null) {
        instance = new LocalMusicScraper();
    }
    return instance;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized LocalMusicScraper getInstance(String lclmPropFile) {
    if (instance == null) {
        instance = new LocalMusicScraper(lclmPropFile);
    }
    return instance;
}


//=============================================================================	
/*
 * 	SCRAPER METHODS (public)	
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void writeMusicBrainzArtistId() {
	log.info("Writing MB Artist Id ... started");

	//get current artist and the path of its first track
	Artist artist = mi.getCurrentArtist();
    String artistsFirstTrackCanonicalPath = artist.getFirstTrack().getTrackCanonicalPath();
    log.info("artistCanonicalPath={}, artistsFirstTrackCanonicalPath={}", artist.getArtistCanonicalPath(), artistsFirstTrackCanonicalPath);

    //read id3 from firstTrack and extract (mb) artist id
	MP3Tags mp3tags;
    String artistId;
	try {
		mp3tags = new MP3Tags(artistsFirstTrackCanonicalPath);
        artistId = mp3tags.getID3FirstTextFieldFromMp3("MUSICBRAINZ_ARTISTID");
	} catch (Exception ex) {
		log.error("could not extract id3 tag from file {}\nError: {}", artistsFirstTrackCanonicalPath, ex.getMessage());
		return;
	}

	//writing artistId and unique id node (for musicbrainz)
	HashMap<String, String> insertInfo = new HashMap<>();
    insertInfo.put("unid", artistId);
    try {
        artist.writeArtistUniqueId(insertInfo, "ArtistId", this.scraperName, "MusicBrainz", "ONLY_UNID");
    } catch (Exception ex) {
        log.error("could not write artist unique id {} \nError: {}", artistId, ex.getMessage());
        return;
    }
	log.info("Writing MB Artist Id ... finished");
}

//-----------------------------------------------------------------------------

void writeMusicBrainzReleaseGroupId() {
	log.info("Writing MB ReleaseGroup Id ... started");

    Album album = (Album) mi.getCurrentEntry();
    String albumCanonicalPath = album.getAlbumCanonicalPath();
    Track albumsFirstTrack = album.getFirstTrack();
    String albumsFirstTrackCanonicalPath = albumsFirstTrack.getTrackCanonicalPath();
    log.info("albumCanonicalPath={}, albumsFirstTrackCanonicalPath={}", albumCanonicalPath, albumsFirstTrackCanonicalPath);

    //read id3 from firstTrack and extract (mb) release group id
	MP3Tags mp3tags;
    String releaseGroupId;
	try {
		mp3tags = new MP3Tags(albumsFirstTrackCanonicalPath);
        releaseGroupId = mp3tags.getID3FirstTextFieldFromMp3("MUSICBRAINZ_RELEASE_GROUP_ID");
	} catch (Exception ex) {
		log.error("could not extract id3 tag from file {}\nError: {}", albumsFirstTrackCanonicalPath, ex.getMessage());
		return;
	}

	//writing releaseGroupId and unique id node (for musicbrainz)
	HashMap<String, String> insertInfo = new HashMap<>();
    insertInfo.put("unid", releaseGroupId);
    try {
        album.writeReleaseGroupUniqueId(insertInfo,"ReleaseGroupId", this.scraperName, "MusicBrainz", "ONLY_UNID");
    } catch (Exception ex) {
        log.error("could not write release group unique id {} \nError: {}", releaseGroupId, ex.getMessage());
        return;
    }
	log.info("Writing MB ReleaseGroup Id ... finished");
}

//-----------------------------------------------------------------------------

void writeMusicBrainzReleaseId() {
	log.info("Writing MB Release Id ... started");

	Album album = (Album) mi.getCurrentEntry();
    String albumCanonicalPath = album.getAlbumCanonicalPath();
    Track albumsFirstTrack = album.getFirstTrack();
    String albumsFirstTrackCanonicalPath = albumsFirstTrack.getTrackCanonicalPath();
    log.info("albumCanonicalPath={}, albumsFirstTrackCanonicalPath={}", albumCanonicalPath, albumsFirstTrackCanonicalPath);

	//read id3 from firstTrack and extract (mb) release id
	MP3Tags mp3tags;
	String releaseId;
	try {
		mp3tags = new MP3Tags(albumsFirstTrackCanonicalPath);
        releaseId = mp3tags.getID3FirstTextFieldFromMp3("MUSICBRAINZ_RELEASEID");
	} catch (Exception ex) {
		log.error("could not extract id3 tag from file {}\nError: {}", albumsFirstTrackCanonicalPath, ex.getMessage());
		return;
	}

	//writing releaseId and unique id node (for musicbrainz)
	HashMap<String, String> insertInfo = new HashMap<>();
	insertInfo.put("unid", releaseId);
	try {
        album.writeReleaseUniqueId(insertInfo,"ReleaseId", this.scraperName, "MusicBrainz", "ONLY_UNID");
	} catch (Exception ex) {
		log.error("could not write release unique id {} \nError: {}", releaseId, ex.getMessage());
		return;
	}
	log.info("Writing MB Release Id ... finished");
}

//-----------------------------------------------------------------------------

void writeMediumDiscNo() {
	log.info("Writing Medium DiscNo ... started");

    Medium medium = (Medium) mi.getCurrentEntry();
    Integer mediumNumber = medium.getMediumNumber();
    Track mediumsFirstTrack = medium.getFirstTrack();
    String mediumsFirstTrackCanonicalPath = mediumsFirstTrack.getTrackCanonicalPath();
    log.info("mediumNumber={}, mediumsFirstTrackCanonicalPath={}", mediumNumber, mediumsFirstTrackCanonicalPath);

    //read id3 from firstTrack and extract medium disc no
    MP3Tags mp3tags;
    String discNo;
    String discTotal;
    try {
        mp3tags = new MP3Tags(mediumsFirstTrackCanonicalPath);
        discNo = mp3tags.getID3FirstTextFieldFromMp3("DISC_NO");
        discTotal = mp3tags.getID3FirstTextFieldFromMp3("DISC_TOTAL");
    } catch (Exception ex) {
        log.error("could not extract id3 tag from file {}\nError: {}", mediumsFirstTrackCanonicalPath, ex.getMessage());
        return;
    }

    //writing medium disc no
    HashMap<String, String> insertInfo = new HashMap<>();
    insertInfo.put("discNo", discNo);
    insertInfo.put("discTotal", discTotal);
    try {
        medium.writeMediumDiscNo(insertInfo,"MediumDiscNo", this.scraperName, "MusicBrainz", "ALL_ATTRIBUTES");
    } catch (Exception ex) {
        log.error("could not write medium disc no {} \nError: {}", discNo, ex.getMessage());
        return;
    }
    log.info("Writing Medium DiscNo ... finished");
}

//-----------------------------------------------------------------------------

void writeMusicBrainzTrackId() {
	log.info("Writing MB Track Id ... started");

    Track track = (Track) mi.getCurrentEntry();
    Integer trackPosition = track.getTrackPos();
    String trackCanonicalPath = track.getTrackCanonicalPath();
    log.info("trackPosition={}, trackCanonicalPath={}", trackPosition, trackCanonicalPath);

    //read id3 from firstTrack and extract (mb) release id
    MP3Tags mp3tags;
    String trackId;
    try {
        mp3tags = new MP3Tags(trackCanonicalPath);
        trackId = mp3tags.getID3FirstTextFieldFromMp3("MUSICBRAINZ_TRACK_ID");
    } catch (Exception ex) {
        log.error("could not extract id3 tag from file {}\nError: {}", trackCanonicalPath, ex.getMessage());
        return;
    }

    //writing trackId and unique id node (for musicbrainz)
    HashMap<String, String> insertInfo = new HashMap<>();
    insertInfo.put("unid", trackId);
    try {
        track.writeTrackUniqueId(insertInfo,"TrackId", this.scraperName, "MusicBrainz", "ONLY_UNID");
    } catch (Exception ex) {
        log.error("could not write track unique id {} \nError: {}", trackId, ex.getMessage());
    }
}
	
//-----------------------------------------------------------------------------

void writeTrackTags(Object[] tags) {
	log.info("Writing Tags ... started");

	boolean allTags = false;
    Track track = (Track) mi.getCurrentEntry();
    String trackCanonicalPath = track.getTrackCanonicalPath();
    log.info("trackCanonicalPath={}", trackCanonicalPath);

    //read id3 from firstTrack and extract (mb) release id
    MP3Tags mp3tags;
    try {
        mp3tags = new MP3Tags(trackCanonicalPath);
    } catch (Exception ex) {
        log.error("could not extract id3 tag from file {}\nError: {}", trackCanonicalPath, ex.getMessage());
        return;
    }

    for(Object tag : tags) {
		String tagName = ((String)tag).trim();
		if(tagName.equals("ALL")) {
			allTags = true;
		}
		List<String> tagValues;
		HashMap<String, List<String>> tagFieldAndValues = null;

        try {
            if(allTags) {
                tagFieldAndValues = mp3tags.getID3AllTextFieldsFromMp3();
            }
            else {
                tagFieldAndValues = mp3tags.getID3TextFieldsFromMp3(tagName);
            }
        }
        catch (Exception e) {
            log.info("tag {} not exisiting in track {}\nError: {}", tagName, trackCanonicalPath, e.getMessage());
        }

        if(tagFieldAndValues != null) {
            for (Map.Entry<String, List<String>> entry : tagFieldAndValues.entrySet()) {
                String trackFieldName = entry.getKey();
                if (allTags) {
                    tagName = mp3tags.getTagNameForFieldName(trackFieldName);
                }
                tagValues = entry.getValue();

                if (tagValues != null && tagValues.size() > 0) {
                    for(String tagValue : tagValues) {
                        log.info("would write tag {}={} for track {}", tagName, tagValue, trackCanonicalPath);

                        //preparing album specific information (nodes/elements text)
                        HashMap<String, String> insertInfo = new HashMap<>();
                        insertInfo.put("tag", tagName + "=" + tagValue);
                        insertInfo.put("fieldName", trackFieldName);
                        try {
                            track.writeTrackTag(insertInfo, "TrackTag", this.scraperName);
                        } catch (Exception ex) {
                            log.error("Could not write tag {}={} for track {}", tagName, tagValue, trackCanonicalPath);
                        }
                    }
                } else {
                    log.info("tag {} not existing in track {}", tagName, trackCanonicalPath);
                }
            }
        }
	}
}

//-----------------------------------------------------------------------------

void writeAllTrackTags() {
	log.info("Writing All Tags ... started");
	String[] tags = new String[1];
	tags[0] = "ALL";
	writeTrackTags(tags);
}

//-----------------------------------------------------------------------------

void writeTrackFileInfo() {
	log.info("Writing FileInfo ... started");
    Track track = (Track) mi.getCurrentEntry();
    String trackCanonicalPath = track.getTrackCanonicalPath();
    String artistCanonicalPath = mi.getCurrentArtist().getArtistCanonicalPath();
    String trackFileName = track.getTrackName();
    Path artistPath = Paths.get(artistCanonicalPath);
    Path trackPath = Paths.get(trackCanonicalPath);
    String trackRelativePath = "./"+artistPath.relativize(trackPath).toString().replaceAll("\\\\", "/");
    log.info("trackCanonicalPath={}", trackCanonicalPath);

    try {
        track.writeTrackFileInfo(trackCanonicalPath, trackRelativePath, trackFileName,
                trackFileName.substring(trackFileName.lastIndexOf(".")+1), FileUtils.sizeOf(new File(trackCanonicalPath)));
    } catch (Exception ex) {
        log.error("could not write track file info for track '{}' \nError: {}", track.getTrackName(), ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

void writeTrackAudioInfo() {
	log.info("Writing audio info ... started");

    Track track = (Track) mi.getCurrentEntry();
    String trackCanonicalPath = track.getTrackCanonicalPath();
    log.info("trackCanonicalPath={}", trackCanonicalPath);

    //read id3 from firstTrack and extract (mb) release id
    MP3Tags mp3tags;
    HashMap<String, String> insertInfo;
    try {
        mp3tags = new MP3Tags(trackCanonicalPath);
        insertInfo = mp3tags.getAudioHeaderInformation();
    } catch (Exception ex) {
        log.error("could not extract id3 tag from file {}\nError: {}", trackCanonicalPath, ex.getMessage());
        return;
    }

    try {
        track.writeTrackAudioInfo(insertInfo, "MP3AudioHeader");
    } catch (Exception ex) {
        log.error("could not write track audio info for track '{}' \nError: {}", track.getTrackName(), ex.getMessage());
    }
}

//-----------------------------------------------------------------------------
	
}