package org.mumdag.model.index;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.mumdag.model.xml.ArtistXml;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//-----------------------------------------------------------------------------

public class Artist {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */

private static final Logger log = LogManager.getLogger(org.mumdag.model.index.Artist.class);

private String artistName = "";
private String artistFolderName;
private String artistCanonicalPath;
private Boolean isSelected;
private Integer numOfAlbums = 0;
private Integer numOfAlbumsSelected = 0;
private Integer numOfTracks = 0;
private Integer numOfTracksSelected = 0;
private ArtistXml artistXml = null;
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

    if(this.artistXml == null) {
        try {
            String artistXmlTemplatePath = PropertyHandler.getInstance().getValue("ArtistXmlModel.templatePath");
            String artistMetadataPath = artistCanonicalPath + PropertyHandler.getInstance().getValue("Mumdag.metadataFolder") +
                    File.separator + PropertyHandler.getInstance().getValue("Mumdag.metadataFile");
            Path path = Paths.get(artistMetadataPath);
            if(Files.exists(path) && Files.isRegularFile(path)) {
                Document artistXmlDoc = XmlUtils.createXmlDoc(artistMetadataPath);
                Document artistXmlTemplate = XmlUtils.createXmlDoc(artistXmlTemplatePath);
                Node releaseGroupList = artistXmlDoc.getElementsByTagName("ReleaseGroupList").item(0);
                while (releaseGroupList.hasChildNodes()) {
                    releaseGroupList.removeChild(releaseGroupList.getFirstChild());
                }
                this.artistXml = new ArtistXml(artistXmlDoc, artistXmlTemplate);
            }
            else {
                this.artistXml = new ArtistXml(artistXmlTemplatePath, artistXmlTemplatePath);
            }
            XmlUtils.setNodeAttributeByXPath(this.artistXml.getXmlDoc(), "/Artist", "path", artistCanonicalPath);
        } catch (Exception ex) {
            //ToDo: error handling
        }
    }
}


//=============================================================================
/*
 * METHODS TO BUILD AND MANAGE INDEX (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Long addEntry(Long keyBase, HashMap<String, Object> indexInfo) {
    Long insertKeyAlbum = addAlbum(keyBase, (String)indexInfo.get("artistCanonicalPath"), (String)indexInfo.get("albumFolderName"),
                                             (String)indexInfo.get("albumCanonicalPath"), true);
    return this.albums.get(insertKeyAlbum).addEntry(insertKeyAlbum, indexInfo);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void addArtistId(String key, String value) {
    getArtistIdMap().put(key, value);
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

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public Track getFirstTrack() {
    if(albums.values().stream().findFirst().isPresent()) {
        Album firstArtist = albums.values().stream().findFirst().get();
        return firstArtist.getFirstTrack();
    }
    else {
        return null;
    }
}



//=============================================================================
/*
 * METHODS TO GET ARTIST INFO FROM THE MODEL (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getArtistWSURL(String unidAttrName, String scraperName) throws Exception {
    String srcAttriName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.srcAttrName");
    return this.artistXml.getArtistWSURL(unidAttrName, srcAttriName, scraperName);
}


//=============================================================================
/*
 * METHODS TO WRITE ARTIST INFO INTO THE MODEL (public)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void writeArtistUniqueId(HashMap<String, String> insertInfo, String copyRule, String origScraperName, String scraperName, String writeNodeAttributes) throws Exception {
    this.artistXml.writeArtistUniqueId(insertInfo, copyRule, origScraperName, scraperName, writeNodeAttributes);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeArtistName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeArtistAlias(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	ok
// DOC:				nok
// TEST:			nok
public void writeArtistTypeAndGender(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("type")) {
        throw new Exception("Wrong parameters! attribute 'type' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistTypeAndGender(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtisPlace(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("type")) {
        throw new Exception("Wrong parameters! attribute 'type' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("event")) {
        throw new Exception("Wrong parameters! attribute 'event' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("seq")) {
        throw new Exception("Wrong parameters! attribute 'seq' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistPlace(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("type")) {
        throw new Exception("Wrong parameters! attribute 'type' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistDate(insertInfo, dateType, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistTag(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("tag")) {
        throw new Exception("Wrong parameters! attribute 'tag' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistTag(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistUrls(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("url")) {
        throw new Exception("Wrong parameters! attribute 'url' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistUrls(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistRating(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("rating")) {
        throw new Exception("Wrong parameters! attribute 'rating' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistRating(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCredit(String artistCreditId, String copyRule, String scraperName) throws Exception {
    if(StringUtils.isEmpty(artistCreditId)) {
        throw new Exception("Wrong parameters! attribute 'artistCreditId' should be not null!");
    }
    this.artistXml.writeArtistCredit(artistCreditId, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCreditUniqueId(HashMap<String, String> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistCreditUniqueId(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCreditName(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("name")) {
        throw new Exception("Wrong parameters! attribute 'name' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistCreditName(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCreditDate(HashMap<String, Object> insertInfo, String dateType, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("date")) {
        throw new Exception("Wrong parameters! attribute 'date' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistCreditDate(insertInfo, dateType, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistCreditRole(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        throw new Exception("Wrong parameters! attribute 'unid' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("role")) {
        throw new Exception("Wrong parameters! attribute 'role' is missing in the insertInfo map!");
    }
    if(!insertInfo.containsKey("roleType")) {
        throw new Exception("Wrong parameters! attribute 'roleType' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistCreditRole(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			nok
public void writeArtistDisambiguation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("disambiguation")) {
        throw new Exception("Wrong parameters! attribute 'disambiguation' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistDisambiguation(insertInfo, copyRule, scraperName);
}

//-----------------------------------------------------------------------------

// ERROR HANDLING:	nok
// DOC:				nok
// TEST:			partly ok, missing properties, parameters and entries of the insertInfo map are not checked
public void writeArtistAnnotation(HashMap<String, Object> insertInfo, String copyRule, String scraperName) throws Exception {
    if(!insertInfo.containsKey("unid")) {
        String idAttrName = PropertyHandler.getInstance().getValue(scraperName + ".Scraper.idAttrName");
        insertInfo.put("unid", this.artistIdMap.get(idAttrName));
    }
    if(!insertInfo.containsKey("annotation")) {
        throw new Exception("Wrong parameters! attribute 'annotation' is missing in the insertInfo map!");
    }
    this.artistXml.writeArtistAnnotation(insertInfo, copyRule, scraperName);
}


//=============================================================================
/*
 * 	HELPER METHODS (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
//PARAMETRIZATION:  nok (keybaseValue)
private Long addAlbum(Long keyBase, String artistFoldername, String albumFolderName, String albumCanonicalPath, Boolean isSelected) {
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
        Album album = new Album(artistFoldername, albumFolderName, albumCanonicalPath, isSelected);
        album.getReleaseGroupIdMap().put("path", albumCanonicalPath);
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
    retMap.put("artistIdMap", artistIdMap);
    retMap.put("resolveBaseMap", resolveBaseMap);
    return retMap;
}

//-----------------------------------------------------------------------------

public void writeFullXmlDocument(String filePath, String fileName) {
    try {
        XmlUtils.writeOutputDocToFile(getFullXmlDocument(), filePath, fileName);
    } catch (Exception ex) {
        log.error("could not write full xml document for artist {}! \nError: {}", this.artistFolderName, ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

public String getXmlDocumentAsString() {
    String retStr = "";
    try {
        Document doc = getFullXmlDocument();
        doc.getDocumentElement().normalize();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        retStr = writer.toString();
    } catch (Exception ex) {
        log.error("could not write xml to string\nError: {}", ex.getMessage());
    }
    return retStr;
}

//-----------------------------------------------------------------------------

private Document getFullXmlDocument() throws Exception {
    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document retXmlDoc = dBuilder.newDocument();
    Node artistRoot = this.getArtistXml().getXmlDoc().getDocumentElement();

    Node copiedRoot = retXmlDoc.importNode(artistRoot, true);
    retXmlDoc.appendChild(copiedRoot);
    for (Map.Entry<Long, Album> entry : albums.entrySet()) {
        Album album = entry.getValue();
        album.getFullXmlDocument(retXmlDoc);
    }
    return retXmlDoc;
}


//=============================================================================
/*
 * 	GETTER/SETTER/ADDER METHODS (public)
 */


public String getArtistCanonicalPath() {
    return artistCanonicalPath;
}

//-----------------------------------------------------------------------------

public Integer getNumOfTracks() {
    return numOfTracks;
}

//-----------------------------------------------------------------------------

public HashMap<String, String> getArtistIdMap() {
    return artistIdMap;
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

public ArtistXml getArtistXml() {
    return artistXml;
}

//-----------------------------------------------------------------------------

}
