package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.MapUtils;
import org.mumdag.utils.PropertyHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import java.lang.reflect.Field;
import java.util.*;

//-----------------------------------------------------------------------------

public class MusicBrainzScraperArtistTest extends MusicBrainzScraperUtils {

private static Logger log = null;

//=============================================================================	
/*
 * 	TEST METHODS FOR ARTISTS (public)
 */
//01 - Beatles
//02 - Animal Collective
//03 - Madonna
//04 - Joplin, Janis
//05 - Prince
//06 - Madness
//07 - Air
//09 - West, Kanye

//DOC:	nok
@Test(dataProvider = "data_requestArtistInfo_ok")
public void test_requestArtistInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_requestArtistInfo_ok() {
    return new Object[][]{
            new Object[]{"01a - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01a-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01a-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01a-exp.xml",
                                    "ids::10000000000_requestArtistInfo_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"01b - group, dissolved, alias with special chars, two mbids in UniqueIdList (MUBZ/BBC)  - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01b-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01b-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/requestArtistInfo/requestArtistInfo_ok-01b-exp.xml",
                                    "ids::10000000000_requestArtistInfo_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistName_ok")
public void test_writeArtistName_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistName_ok() {
    return new Object[][]{
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistName_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"02a - group, not dissolved - Animal Collective",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02a-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02a-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02a-exp.xml",
                                    "ids::10000000000_writeArtistName_mbid=0c751690-c784-4a4f-b1e4-c1de27d47581")))},
            new Object[]{"02b - group, not dissolved - Animal Collective - name already existing -> update",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02b-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02b-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02b-exp.xml",
                                    "ids::10000000000_writeArtistName_mbid=0c751690-c784-4a4f-b1e4-c1de27d47581")))},
            new Object[]{"02c - group, not dissolved - Animal Collective - alias name existing -> add",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02c-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02c-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02c-exp.xml",
                                    "ids::10000000000_writeArtistName_mbid=0c751690-c784-4a4f-b1e4-c1de27d47581")))},
    };
}

//-----------------------------------------------------------------------------
/*
//DOC:	nok
@Test(dataProvider = "data_writeArtistName_nok")
public void test_writeArtistName_nok(String testDesc, String configFilePath, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
//    try {
//        PropertyHandler.getInstance(configFilePath);
//    } catch (Exception ex) {
//        log.error("{}... failed", testDesc);
//        fail("No exception expected. Probably config file '" + configFilePath + "' not found!\n" + "Error: " + ex.getMessage());
//    }
//    prepareExecuteValidateTest(testDesc, "nok", baseXmlFile, sourceXmlFile, "writeArtistName", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
    }
    */
//-----------------------------------------------------------------------------
/*
@DataProvider
public Object[][] data_writeArtistName_nok() {
    return new Object[][]{
            new Object[]{"01 - unid null",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-01.properties",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", null,
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-01-exp.xml")},
            new Object[]{"02 - unid empty",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-02.properties",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-02-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-02-exp.xml")},
            new Object[]{"03 - xml null",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-03.properties",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-03-base.xml",
                    null,
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-03-exp.xml")},
            new Object[]{"04 - xml empty",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-04.properties",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-04-base.xml",
                    "",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-04-exp.xml")},
            new Object[]{"05 - MusicBrainz.rules corrupt (ArtistName)",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-05.properties",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-05-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_nok-05-exp.xml")},
    };
}
*/
//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistAlias_ok")
public void test_writeArtistAlias_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistAlias_ok() {
	return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistAlias_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
	};
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistTypeAndGender_ok")
public void test_writeArtistTypeAndGender_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistTypeAndGender_ok() {
	return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistTypeAndGender_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"04a - person, female, dead - Joplin, Janis",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04a-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04a-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04a-exp.xml",
                                    "ids::10000000000_writeArtistTypeAndGender_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
            new Object[]{"04b - person, female, dead, type and gender existing - Joplin, Janis",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04b-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04b-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-04b-exp.xml",
                                    "ids::10000000000_writeArtistTypeAndGender_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
	};
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistPlace_ok")
public void test_writeArtistPlace_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistPlace_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistPlace_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-05-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-05-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistPlace/writeArtistPlace_ok-05-exp.xml",
                                    "ids::10000000000_writeArtistPlace_mbid=070d193a-845c-479f-980e-bef15710653e")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistDate_ok")
public void test_writeArtistDate_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistDate_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistDate_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"02 - group, not dissolved - Animal Collective",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-02-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-02-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-02-exp.xml",
                                    "ids::10000000000_writeArtistDate_mbid=0c751690-c784-4a4f-b1e4-c1de27d47581")))},
            new Object[]{"04 - person, female, dead - Joplin, Janis",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-04-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-04-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistDate/writeArtistDate_ok-04-exp.xml",
                                    "ids::10000000000_writeArtistDate_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistTags_ok")
public void test_writeArtistTags_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistTags_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTags/writeArtistTags_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistTags/writeArtistTags_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistTags/writeArtistTags_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistTags_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
    };
}


//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistUrls_ok")
public void test_writeArtistUrls_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistUrls_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistUrls_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"02 - group, active, alias without type attr - Animal Collective",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-02-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-02-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-02-exp.xml",
                                    "ids::10000000000_writeArtistUrls_mbid=79239441-bfd5-4981-a70c-55c3f15c1287")))},
            new Object[]{"03 - female, alive, multiple isni/ipi - Madonna",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-03-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-03-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-03-exp.xml",
                                    "ids::10000000000_writeArtistUrls_mbid=79239441-bfd5-4981-a70c-55c3f15c1287")))},
            new Object[]{"04 - person, female, dead - Joplin, Janis",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-04-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-04-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-04-exp.xml",
                                    "ids::10000000000_writeArtistUrls_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
            new Object[]{"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-05-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-05-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-05-exp.xml",
                                    "ids::10000000000_writeArtistUrls_mbid=070d193a-845c-479f-980e-bef15710653e")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistRating_ok")
public void test_writeArtistRating_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistRating_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistRating/writeArtistRating_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistRating/writeArtistRating_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistRating/writeArtistRating_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistRating_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistIpiAndIsni_ok")
public void test_writeArtistIpiAndIsni_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistIpiAndIsni_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistIpiAndIsni_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"03 - female, alive, multiple isni/ipi - Madonna",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-03-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-03-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-03-exp.xml",
                                    "ids::10000000000_writeArtistIpiAndIsni_mbid=79239441-bfd5-4981-a70c-55c3f15c1287")))},
            new Object[]{"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-05-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-05-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-05-exp.xml",
                                    "ids::10000000000_writeArtistIpiAndIsni_mbid=070d193a-845c-479f-980e-bef15710653e")))},
            new Object[]{"09 - male, multiple IPI/ISNI - Kanye West",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-09-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/W/West, Kanye/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/West, Kanye - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-09-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-09-exp.xml",
                                    "ids::10000000000_writeArtistIpiAndIsni_mbid=164f0d73-1234-4e2c-8743-d77bf2191051")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistCredits_ok")
public void test_writeArtistCredits_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistCredits_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistCredits_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"06 - band, ended, disambiguation, multiple members at different times - Madness",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-06-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madness/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madness - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-06-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-06-exp.xml",
                                    "ids::10000000000_writeArtistCredits_mbid=5f58803e-8c4c-478e-8b51-477f38483ede")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistDisambiguation_ok")
public void test_writeArtistDisambiguation_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistDisambiguation_ok() {
    return new Object[][] {
            new Object[]{"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/writeArtistDisambiguation_ok-05-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/writeArtistDisambiguation_ok-05-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/writeArtistDisambiguation_ok-05-exp.xml",
                                    "ids::10000000000_writeArtistDisambiguation_mbid=070d193a-845c-479f-980e-bef15710653e")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistAnnotation_ok")
public void test_writeArtistAnnotation_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistAnnotation_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistAnnotation_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"03 - female, alive, multiple isni/ipi - Madonna",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-03-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-03-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-03-exp.xml",
                                    "ids::10000000000_writeArtistAnnotation_mbid=79239441-bfd5-4981-a70c-55c3f15c1287")))},
            new Object[]{"04 - person, female, dead - Joplin, Janis",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-04-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-04-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-04-exp.xml",
                                    "ids::10000000000_writeArtistAnnotation_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistInfo_ok")
public void test_writeArtistInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistInfo_ok() {
    return new Object[][] {
            new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-01-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-01-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-01-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d")))},
            new Object[]{"02 - group, active, alias without type attr - Animal Collective",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-02-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-02-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-02-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=0c751690-c784-4a4f-b1e4-c1de27d47581")))},
            new Object[]{"03 - female, alive, alias with type attr - Madonna",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-03-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-03-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-03-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=79239441-bfd5-4981-a70c-55c3f15c1287")))},
            new Object[]{"04 - female, dead - Janis Joplin",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-04-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/J/Joplin, Janis/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-04-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-04-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=76c9a186-75bd-436a-85c0-823e3efddb7f")))},
            new Object[]{"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-05-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-05-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-05-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=070d193a-845c-479f-980e-bef15710653e")))},
            new Object[]{"06 - band, ended, disambiguation, multiple members at different times - Madness",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-06-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madness/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madness - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-06-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-06-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=5f58803e-8c4c-478e-8b51-477f38483ede")))},
            new Object[]{"09 - male, disambiguation, multiple IPI/ISNI - West, Kanye",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-09-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/W/West, Kanye/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/West, Kanye - Artist.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-09-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-09-exp.xml",
                                    "ids::10000000000_writeArtistInfo_mbid=164f0d73-1234-4e2c-8743-d77bf2191051")))},
    };
}


//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */
//DOC:	nok
@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(MusicBrainzScraperArtistTest.class);
    //setLogger("MusicBrainzScraperArtistTest.class");
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
public void destroyMusicIndexInstanceBefore() throws Exception {
    Field instance = MusicIndex.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@BeforeMethod
public void destroyMusicBrainzScraperInstanceBefore() throws Exception {
    Field instance = MusicBrainzScraper.class.getDeclaredField("instance");
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
public void destroyMusicIndexInstanceAfter() throws Exception {
    Field instance = MusicIndex.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@AfterMethod
public void destroyMusicBrainzScraperInstanceAfter() throws Exception {
    Field instance = MusicBrainzScraper.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}

//-----------------------------------------------------------------------------

}
