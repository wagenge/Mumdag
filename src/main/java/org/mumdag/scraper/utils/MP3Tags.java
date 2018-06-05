package org.mumdag.scraper.utils;

//-----------------------------------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.Id3FieldType;
import org.mumdag.scraper.LocalMusicScraper;


//-----------------------------------------------------------------------------

public class MP3Tags {
	
//=============================================================================	
/*
* 	CLASS ATTRIBUTES (private)
*/	
	
private static final Logger log = LogManager.getLogger(MP3Tags.class);

MP3File mp3File = null;

private ID3v24Tag mp3TagId3v24 = null;
private ID3v1Tag mp3TagId3v1 = null;
private MP3AudioHeader mp3AudioHeader = null;


//=============================================================================	
/*
* 	CONSTRUCTOR METHODS INCLUDING INSTANTIATION METHODS (public)
*/

public MP3Tags(String filePath) throws IOException {
	try {
		this.mp3File = (MP3File)AudioFileIO.read(new File(filePath));
		
		if(mp3File.hasID3v1Tag()) {
			this.mp3TagId3v1 = mp3File.getID3v1Tag();
		}
		
		if(mp3File.hasID3v2Tag()) {
			this.mp3TagId3v24 = mp3File.getID3v2TagAsv24();
		}
		this.mp3AudioHeader = mp3File.getMP3AudioHeader();
		
	} catch (CannotReadException e) {
		e.printStackTrace();
	} catch (TagException e) {
		e.printStackTrace();
	} catch (ReadOnlyFileException e) {
		e.printStackTrace();
	} catch (InvalidAudioFrameException e) {
		e.printStackTrace();
	}		
}


//=============================================================================	
/*
* 	GETTER/SETTER/WRITER METHODS (public)
*/

public String getID3FirstTextFieldFromMp3(String id3Tag) {
	String id3TagValue = "";
	
	if(mp3TagId3v24 != null) {
		FieldKey id3TagKey =  FieldKey.valueOf(id3Tag);
		if(mp3TagId3v24.hasField(id3TagKey)) {
			id3TagValue = mp3TagId3v24.getFirst(id3TagKey);
		}
	}
	return id3TagValue;
}

//-----------------------------------------------------------------------------

public HashMap<String, List<String>> getID3TextFieldsFromMp3(String id3Tag) {
	HashMap<String, List<String>> retId3TagValues = new HashMap<String, List<String>>();
	List<String> id3TagValues = null;
	
	if(mp3TagId3v24 != null) {
		FieldKey id3TagKey =  FieldKey.valueOf(id3Tag);
		ID3v24FieldKey id3v24TagKey =  ID3v24FieldKey.valueOf(id3Tag);
		if(mp3TagId3v24.hasField(id3TagKey) && id3v24TagKey.getFieldType() != Id3FieldType.BINARY) {
			id3TagValues = mp3TagId3v24.getAll(id3TagKey);
			retId3TagValues.put(id3v24TagKey.getFieldName(), id3TagValues);
		}
	}
	return retId3TagValues;
}

//-----------------------------------------------------------------------------

public HashMap<String, List<String>> getID3AllTextFieldsFromMp3() {
	HashMap<String, List<String>> retId3TagValues = new HashMap<String, List<String>>();
	List<String> id3TagValues = null;
	ID3v24FieldKey id3v24TagKey = null;
	
	if(mp3TagId3v24 != null) {
		FieldKey[] id3TagKeys =  FieldKey.values();
		for(FieldKey id3TagKey : id3TagKeys) {
			String keyName = id3TagKey.name();
			try {
				id3v24TagKey =  ID3v24FieldKey.valueOf(keyName);
			}
			catch (IllegalArgumentException e) {
				log.warn("getID3AllTextFieldsFromMp3: keyName={} not found", keyName);
			}  
			if(mp3TagId3v24.hasField(id3TagKey) && id3v24TagKey.getFieldType() != Id3FieldType.BINARY) {
				id3TagValues = mp3TagId3v24.getAll(id3TagKey);
				retId3TagValues.put(id3v24TagKey.getFieldName(), id3TagValues);
			}
		} 
	}
	return retId3TagValues;
}

//-----------------------------------------------------------------------------

public Integer getFieldCount() {

	return this.mp3TagId3v24.getFieldCount();
}

//-----------------------------------------------------------------------------

public Integer getFieldCountIncSubfields() {
	
	return this.mp3TagId3v24.getFieldCountIncludingSubValues();
}

//-----------------------------------------------------------------------------

public String getTagNameForFieldName(String fieldName) {
	String tagName = ""; 
	ID3v24FieldKey id3v24TagKey = null;
	
	FieldKey[] id3TagKeys =  FieldKey.values();
	for(FieldKey id3TagKey : id3TagKeys) {
		String keyName = id3TagKey.name();
		try {
			id3v24TagKey =  ID3v24FieldKey.valueOf(keyName);
		}
		catch (IllegalArgumentException e) {
			log.warn("getTagNameForFieldName: keyName={} not found", keyName);
		}
		if(fieldName.equals(id3v24TagKey.getFieldName())) {
			tagName = keyName;
			break;
		}
	}
	return tagName;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getAudioHeaderInformation() {
	HashMap<String, String> retMap = new HashMap<String, String>();
	
	retMap.put("BitRate", mp3AudioHeader.getBitRate());
	retMap.put("IsVariableBitRate", String.valueOf(mp3AudioHeader.isVariableBitRate()));
	retMap.put("BitsPerSample", String.valueOf(mp3AudioHeader.getBitsPerSample()));
	retMap.put("Channels", mp3AudioHeader.getChannels());
	retMap.put("Emphasis", mp3AudioHeader.getEmphasis());
	retMap.put("Encoder", mp3AudioHeader.getEncoder());
	retMap.put("IsLossless", String.valueOf(mp3AudioHeader.isLossless()));
	retMap.put("EncodingType", mp3AudioHeader.getEncodingType());
	retMap.put("MpegVersion", mp3AudioHeader.getMpegVersion());
	retMap.put("MpegLayer", mp3AudioHeader.getMpegLayer());
	retMap.put("TrackLength", String.valueOf(mp3AudioHeader.getTrackLength()));
	retMap.put("PreciseTrackLength", String.valueOf(mp3AudioHeader.getPreciseTrackLength()));
	retMap.put("SampleRate", mp3AudioHeader.getSampleRate());
	retMap.put("IsCopyrighted", String.valueOf(mp3AudioHeader.isCopyrighted()));
	retMap.put("IsOriginal", String.valueOf(mp3AudioHeader.isOriginal()));
	
	return retMap;
}

//-----------------------------------------------------------------------------

public void writeTagToFile(String filePath) {
	File outputFile = new File(filePath);
	
  try {
  	this.mp3File.extractID3v2TagDataIntoFile(outputFile);
  } catch (Exception e) {
      System.err.println("Unable to extract tag");
  }
}


//=============================================================================	
/*
* 	GETTER/SETTER METHODS FOR CLASS VARIABLES (public)
*/


public MP3File getMp3File() { return mp3File; }

//-----------------------------------------------------------------------------

public void setMp3File(MP3File mp3File) { this.mp3File = mp3File; }

//-----------------------------------------------------------------------------

public ID3v24Tag getMp3TagId3v24() { return mp3TagId3v24; }

//-----------------------------------------------------------------------------

public void setMp3TagId3v24(ID3v24Tag mp3TagId3v24) { this.mp3TagId3v24 = mp3TagId3v24; }

//-----------------------------------------------------------------------------

public ID3v1Tag getMp3TagId3v1() { return mp3TagId3v1; }

//-----------------------------------------------------------------------------

public void setMp3TagId3v1(ID3v1Tag mp3TagId3v1) { this.mp3TagId3v1 = mp3TagId3v1; }

//-----------------------------------------------------------------------------

public MP3AudioHeader getMp3AudioHeader() { return mp3AudioHeader; }

//-----------------------------------------------------------------------------

public void setMp3AudioHeader(MP3AudioHeader mp3AudioHeader) { this.mp3AudioHeader = mp3AudioHeader; }

//-----------------------------------------------------------------------------

}