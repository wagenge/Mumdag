package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.LocalMusicIndexer;
import org.mumdag.model.index.Album;
import org.mumdag.model.index.Artist;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.ListUtils;
import org.testng.Assert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

//-----------------------------------------------------------------------------

class MusicBrainzScraperUtils {

private static Logger log = LogManager.getLogger(MusicBrainzScraperUtils.class);

//=============================================================================
/*
 * 	METHODS FOR TEST PREPARATION, EXECUTION AND VALIDATION (private)
 */
//ERROR HANDLING:	nok
//DOC:				nok
void prepareExecuteValidateTest(String testDesc, String oknok, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    //prepare test
    // - for each artist config => copy base file to artist folder
    // - read file list and build local music index
    prepareTest(testDesc, fileListPath, basePath, testConfigs);

    //build music index
    MusicIndex mi = MusicIndex.getInstance();
    log.info("Building music index ... {} artists and {} tracks found", mi.getNumOfArtists(), mi.getNumOfTracks());

    //execute and validate test
    for(HashMap<String, String> testConfig : testConfigs) {
        String result = executeTest(testDesc, testConfig, mi);
        validateTest(testDesc, oknok, testConfig, result, mi);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
void prepareTest(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    //for each artist config => copy base file to artist folder
    for(HashMap<String, String> testConfig : testConfigs) {
        try {
            String artistMetadataXmlFileStr = testConfig.get("artistFolder") + ".metadata/artist-local.xml";
            Path artistMetadataXmlFile = Paths.get(artistMetadataXmlFileStr);
            if (Files.exists(artistMetadataXmlFile)) {
                Files.delete(artistMetadataXmlFile);
            }
            String artistBaseXmlFileStr = testConfig.get("base");
            Path artistBaseXmlFile = Paths.get(artistBaseXmlFileStr);
            if (Files.exists(artistBaseXmlFile)) {
                Files.copy(artistBaseXmlFile, artistMetadataXmlFile);
            }
        } catch (Exception ex) {
            log.error("{} ... failed\nError: ", testDesc, ex.getMessage());
            Assert.fail("No exception expected copying while the base xyml file.\nError: " + ex.getMessage());
        }
    }

    //read fileList
    List<String> fileList = ListUtils.file2List(fileListPath);
    fileList.replaceAll(x -> x.replaceAll("_basePath_", basePath));
    fileList.replaceAll(x -> x.replaceAll("\\\\", "/"));

    // Reading the local music files and building the index
    log.info("Building artist index from list");
    LocalMusicIndexer lmi = new LocalMusicIndexer(null);
    lmi.buildIndex(fileList);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
private Object[] prepareTestInput(String testDesc, String inputFilePath) {
    // read the xml file and stores it in an object array
    String infoXml = "";
    try {
        if(StringUtils.isNotEmpty(inputFilePath)) {
            infoXml = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        }
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Probably the xml file '" + inputFilePath + "' is missing!\n" + "Error: " + ex.getMessage());
    }
    Object[] infoObj = new Object[2];
    if(inputFilePath == null) {
        infoObj[0] = null;
    }
    else if(inputFilePath.length() == 0) {
        infoObj[0] = "";
    }
    infoObj[0] = infoXml;
    return infoObj;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
String executeTest(String testDesc, HashMap<String, String> testConfig, MusicIndex mi) {
    List<String> ids = new ArrayList<>(Arrays.asList(testConfig.get("ids").split(",")));
    Object[] inputObj = prepareTestInput(testDesc, testConfig.get("mbInput"));
    String result = "";
    for(String idPlusMethod : ids) {
        String[] idPlusMethodArr = idPlusMethod.split("_");
        Long entryId = Long.valueOf(idPlusMethodArr[0]);
        String calledMethod = idPlusMethodArr[1];
        String id = idPlusMethodArr[2];
        String idName = id.substring(0, id.indexOf("="));
        String idValue = id.substring(id.indexOf("=")+1, id.length());
        String albumType = "";
        if(idPlusMethodArr.length == 4) {
            albumType = idPlusMethodArr[3];
        }

        TreeMap<Long, HashMap<String, Object>> fmi =  mi.getFlatMusicIndex();
        for (Map.Entry<Long, HashMap<String, Object>> entry : fmi.entrySet()) {
            if(entry.getKey().equals(entryId)) {
                mi.setCurrentEntryId(entryId);
                Object currentEntry = mi.getCurrentEntry();
                if(currentEntry instanceof Artist) {
                    mi.getCurrentArtist().addArtistId(idName, idValue);
                } else if(currentEntry instanceof Album) {
                    if(albumType.equals("rg")) {
                        mi.getCurrentAlbum().addReleaseGroupId(idName, idValue);
                    } else {
                        mi.getCurrentAlbum().addReleaseId(idName, idValue);
                    }
                }
                //execute correct scraper method
                switch (calledMethod) {
                    case "requestArtistInfo":
                        result = MusicBrainzScraper.getInstance().requestArtistInfo();
                        break;
                    case "writeArtistInfo":
                        MusicBrainzScraper.getInstance().writeArtistInfo(inputObj);
                        break;
                    case "writeArtistName":
                        MusicBrainzScraper.getInstance().writeArtistName(inputObj);
                        break;
                    case "writeArtistAlias":
                        MusicBrainzScraper.getInstance().writeArtistAlias(inputObj);
                        break;
                    case "writeArtistTypeAndGender":
                        MusicBrainzScraper.getInstance().writeArtistTypeAndGender(inputObj);
                        break;
                    case "writeArtistDate":
                        MusicBrainzScraper.getInstance().writeArtistDate(inputObj);
                        break;
                    case "writeArtistPlace":
                        MusicBrainzScraper.getInstance().writeArtistPlace(inputObj);
                        break;
                    case "writeArtistTags":
                        MusicBrainzScraper.getInstance().writeArtistTags(inputObj);
                        break;
                    case "writeArtistUrls":
                        MusicBrainzScraper.getInstance().writeArtistUrls(inputObj);
                        break;
                    case "writeArtistRating":
                        MusicBrainzScraper.getInstance().writeArtistRating(inputObj);
                        break;
                   case "writeArtistIpiAndIsni":
                       MusicBrainzScraper.getInstance().writeArtistIpiIsni(inputObj);
                        break;
                    case "writeArtistCredits":
                        MusicBrainzScraper.getInstance().writeArtistCredits(inputObj);
                        break;
                    case "writeArtistDisambiguation":
                        MusicBrainzScraper.getInstance().writeArtistDisambiguation(inputObj);
                        break;
                    case "writeArtistAnnotation":
                        MusicBrainzScraper.getInstance().writeArtistAnnotation(inputObj);
                        break;
                    case "requestReleaseGroupInfo":
                        result = MusicBrainzScraper.getInstance().requestReleaseGroupInfo();
                        break;
                    case "writeReleaseGroupInfo":
                        MusicBrainzScraper.getInstance().writeReleaseGroupInfo(inputObj);
                        break;
                    case "writeReleaseGroupTitle":
                        MusicBrainzScraper.getInstance().writeReleaseGroupTitle(inputObj);
                        break;
                    case "writeReleaseGroupAlias":
                        MusicBrainzScraper.getInstance().writeReleaseGroupAlias(inputObj);
                        break;
                    case "writeReleaseGroupType":
                        MusicBrainzScraper.getInstance().writeReleaseGroupType(inputObj);
                        break;
                    case "writeReleaseGroupTypeFromRels":
                        MusicBrainzScraper.getInstance().writeReleaseGroupTypeFromRels(inputObj);
                        break;
                    case "writeReleaseGroupDate":
                        MusicBrainzScraper.getInstance().writeReleaseGroupDate(inputObj);
                        break;
                    case "writeReleaseGroupTags":
                        MusicBrainzScraper.getInstance().writeReleaseGroupTags(inputObj);
                        break;
                    case "writeReleaseGroupUrls":
                        MusicBrainzScraper.getInstance().writeReleaseGroupUrls(inputObj);
                        break;
                    case "writeReleaseGroupRating":
                        MusicBrainzScraper.getInstance().writeReleaseGroupRating(inputObj);
                        break;
                    case "writeReleaseGroupArtistCredits":
                        MusicBrainzScraper.getInstance().writeReleaseGroupArtistCredits(inputObj);
                        break;
                    case "writeReleaseGroupDisambiguation":
                        MusicBrainzScraper.getInstance().writeReleaseGroupDisambiguation(inputObj);
                        break;
                    case "writeReleaseGroupAnnotation":
                        MusicBrainzScraper.getInstance().writeReleaseGroupAnnotation(inputObj);
                        break;
                    default:
                        log.error("Entry {}: Test for method '{}' not yet implemented", entryId, calledMethod);
                        fail("Entry " + entryId +": Test for method '" + calledMethod + "' not yet implemented!");
                }
            }
        }
    }
    return result;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
void validateTest(String testDesc, String oknok, HashMap<String, String> testConfig, String xmlString, MusicIndex mi) {
    //set correct absolut path for testfiles
    String pathStr = testConfig.get("exp");
    String testNo = StringUtils.substringBetween(pathStr, "-", "-");
    Path path = Paths.get(pathStr);
    String testPath = path.getParent().toAbsolutePath().toString();
    String parentFolderName =  path.getParent().getFileName().toString();
    String outputFileName = testPath + "/" + parentFolderName + "_" + oknok + "-" + testNo + "-out.xml";

    if(StringUtils.isEmpty(xmlString)) {
        Artist artist = mi.getCurrentArtist();
        artist.writeFullXmlDocument(testPath, parentFolderName + "_" + oknok + "-" + testNo + "-out.xml");
        xmlString = artist.getXmlDocumentAsString();
    } else {
        try {
            Files.write(Paths.get(outputFileName), xmlString.getBytes());
        } catch (Exception ex) {
            log.error("Could not write xmlString to file '{}'\nError: ", testPath, outputFileName, ex.getMessage());
            Assert.fail("No exception expected while writing result to file " + outputFileName + "\nError: " + ex.getMessage());
        }
    }
    String expXmlString = "";
    try {
        expXmlString = FileUtils.readFileToString(new File(testConfig.get("exp")), "UTF-8");
    } catch (Exception ex) {
        log.error("{} ... failed\nError: ", testDesc, ex.getMessage());
        Assert.fail("No exception expected while comparing the xml strings.\n" + "Error: " + ex.getMessage());
    }
    assertThat(xmlString).isXmlEqualTo(expXmlString);
}

//-----------------------------------------------------------------------------

}
