package org.mumdag.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.index.Track;
import org.mumdag.utils.PropertyHandler;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static org.testng.Assert.*;

public class LocalMusicIndexerTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS (public)
 */

@Test(dataProvider = "data_buildIndex_ok")
public void test_buildIndex(String testDesc, String startPath, List<Integer> expRes) {
    log.info("{}... started", testDesc);
    LocalMusicIndexer lmi = new LocalMusicIndexer(startPath);
    lmi.buildIndex();
    Integer numOfTracks = lmi.getNumOfTracks();
    Integer numOfTracksSelected = lmi.getNumOfTracksSelected();
    TreeMap<Long, HashMap<String, Object>> flatMusicIndex = lmi.getFlatMusicIndex();
    assertEquals(lmi.getMusicIndex().getNumOfArtists(), expRes.get(0));
    assertEquals(flatMusicIndex.size(), (int)expRes.get(1));
    assertEquals(numOfTracks, expRes.get(2));
    assertEquals(numOfTracksSelected, expRes.get(2));

    lmi.calcResolvBaseMaps("path");

    lmi.getMusicIndex().findEntry(10000000000L);
    Track tr = ((Track)lmi.getMusicIndex().findEntry(10000050110L));
    tr.setSelected(false);
    numOfTracksSelected = lmi.getNumOfTracksSelected();
    assertEquals(numOfTracksSelected, expRes.get(3));

    TreeMap<Long, HashMap<String, Object>> flatMusicIndex_new = lmi.getFlatMusicIndex();
    assertEquals(flatMusicIndex_new.size(), (int)expRes.get(4));
    log.info("{}... finished", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_buildIndex_ok() {
    return new Object[][] {
            new Object[]{"01 - ...",
                    "../../_testdata/_localFilesInput/",
                    Arrays.asList(3, 454, 388, 387, 453)},
    };
}

//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(LocalArtistIndexTest.class);
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
@AfterMethod
public void destroyPropertyHandlerInstanceAfter() throws Exception {
    Field instance = PropertyHandler.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

}