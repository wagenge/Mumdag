package org.mumdag;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.ExecutionRules;
import org.mumdag.core.LocalMusicIndexer;
import org.mumdag.model.index.Artist;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.MapUtils;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class MumdagTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS (public)
 */

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_complexTest_ok")
public void test_complexTest_ok(String testDesc, String confFile, List<HashMap<String, String>> expRes)  {
    log.info("{} ... started", testDesc);

    //prepare test
    try {
        for (HashMap<String, String> expResItem : expRes) {
            String artistMetadataXmlFileStr = expResItem.get("artistFolder") + ".metadata/artist-local.xml";
            Path artistMetadataXmlFile = Paths.get(artistMetadataXmlFileStr);
            if (Files.exists(artistMetadataXmlFile)) {
                Files.delete(artistMetadataXmlFile);
            }
            String artistBaseXmlFileStr = expResItem.get("base");
            Path artistBaseXmlFile = Paths.get(artistBaseXmlFileStr);
            if (Files.exists(artistBaseXmlFile)) {
                   Files.copy(artistBaseXmlFile, artistMetadataXmlFile);
            }
        }
    } catch (Exception ex) {
        log.error("{} ... failed\nError: ", testDesc, ex.getMessage());
    }

    // Reading the local music files and building the index
    try {
        String startPath = PropertyHandler.getInstance(confFile).getValue("LocalMusicIndexer.startPath");
        log.info("Building artist index started ...  start reading at {}", startPath);
        LocalMusicIndexer lmi = new LocalMusicIndexer(startPath);
        lmi.buildIndex();
    } catch (Exception e) {
        e.printStackTrace();
    }

    MusicIndex mi = MusicIndex.getInstance();
    log.info("Building music index finished ... {} artists and {} tracks found", mi.getNumOfArtists(), mi.getNumOfTracks());

    // Reading the execution rules
    String executionRulesFilePath;
    ExecutionRules er;
    try {
        executionRulesFilePath = PropertyHandler.getInstance(confFile).getValue("ExecutionRules.rulesFileName");
        er = ExecutionRules.getInstance(executionRulesFilePath);
        log.info("Loading ExecutionRules, {} rule sets found", er.getNumberOfRuleSets());
    } catch (Exception ex) {
        log.error("{}: {} ... Mumdag exited",  ex.getClass().getSimpleName(), ex.getMessage());
        ex.printStackTrace();
        return;
    }

    TreeMap<Long, HashMap<String, Object>> fmi =  mi.getFlatMusicIndex();
    int i = 1;
    Long currArtistEntryKey = 10000000000L;
    Long artistEntryKey = 10000000000L;
    for (Map.Entry<Long, HashMap<String, Object>> entry : fmi.entrySet()) {
        Long entryKey = entry.getKey();
        artistEntryKey = (entryKey / 10000000000L) * 10000000000L;
        HashMap<String, Object> entryValue = entry.getValue();
        mi.setCurrentEntryId(entryKey);
        log.info("#{}: {} ({}) => ... start executing rules (artistKey = {})", i, String.valueOf(entryKey), entryValue.get("type"), artistEntryKey);
        i++;
        er.executeRules(entryValue);

        if(currArtistEntryKey < artistEntryKey) {
            Artist currArtist = (Artist) (mi.findEntry(currArtistEntryKey));
            String currArtistPath = currArtist.getArtistCanonicalPath();
            currArtist.writeFullXmlDocument(currArtistPath + "/.metadata", "artist-local-test.xml");
            currArtistEntryKey = (entryKey / 10000000000L) * 10000000000L;
        }
    }

    // write model from last artist
    Artist prevArtist =(Artist)(mi.findEntry(artistEntryKey));
    String artistPath = prevArtist.getArtistCanonicalPath();
    prevArtist.writeFullXmlDocument(artistPath + "/.metadata", "artist-local-test.xml");

    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_complexTest_ok() {
    return new Object[][] {
            new Object[] {
                    "01 - ...",
                    "./src/test/resources/MumdagTest/complexTest/complexTest-ok-1.properties",
                    Arrays.asList(
                        MapUtils.array2Map("::", Arrays.asList("artistFolder::../../_testdata/_localFilesInput/A/Air/",
                                                             "base::./src/test/resources/MumdagTest/complexTest/complexTest-ok-1a-base.xml",
                                                             "exp::./src/test/resources/MumdagTest/complexTest/complexTest-ok-1a-exp.xml")),
                        MapUtils.array2Map("::", Arrays.asList("artistFolder::../../_testdata/_localFilesInput/A/Animal Collective/",
                                                             "base::./src/test/resources/MumdagTest/complexTest/complexTest-ok-1b-base.xml",
                                                             "exp::./src/test/resources/MumdagTest/complexTest/complexTest-ok-1b-exp.xml")))
                        }
    };
}


//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(MumdagTest.class);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@BeforeMethod
public void destroyPropertyHandlerInstanceBefore() throws Exception {
    Field instance = PropertyHandler.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@BeforeMethod
public void destroyExecutionRulesInstanceBefore() throws Exception {
    Field instance = ExecutionRules.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@AfterMethod
public void destroyPropertyHandlerInstanceAfter() throws Exception {
    Field instance = PropertyHandler.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@AfterMethod
public void destroyExecutionRulesInstanceAfter() throws Exception {
    Field instance = ExecutionRules.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

}