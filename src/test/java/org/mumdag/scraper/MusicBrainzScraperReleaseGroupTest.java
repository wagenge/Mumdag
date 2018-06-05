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

public class MusicBrainzScraperReleaseGroupTest extends MusicBrainzScraperUtils {

private static Logger log = null;


//=============================================================================
/*
 * 	TEST METHODS FOR RELEASE GROUPS (public)
 */
//01.1 - Beatles / White Album
//01.2 - Beatles / Yellow Submarine Songtrack
//02.1 - Animal Collective / Strawberry Jam
//02.2 - Animal Collective / Peacebone
//03.1 - Madonna / Rebel Heart
//03.2 - Madonna / Rebel Heart Tour
//04 - Joplin, Janis
//05.1 - Prince / Purple Rain
//06 - Madness
//07.1 - Air / Talkie Walkie
//08.1 - Kiss / Monster
//09.1 - West, Kanye - The Life of Pablo
//10.1 - Coldplay - Mylo Xyloto

//DOC:	nok
@Test(dataProvider = "data_requestReleaseGroupInfo_ok")
public void test_requestReleaseGroupInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_requestReleaseGroupInfo_ok() {
    return new Object[][]{
            new Object[]{"01a - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/requestReleaseGroupInfo/requestReleaseGroupInfo_ok-01.1a-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/requestReleaseGroupInfo/requestReleaseGroupInfo_ok-01.1a-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/requestReleaseGroupInfo/requestReleaseGroupInfo_ok-01.1a-exp.xml",
                                    "ids::10000010000_requestReleaseGroupInfo_mbid=055be730-dcad-31bf-b550-45ba9c202aa3")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupTitle_ok")
public void test_writeReleaseGroupTitle_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupTitle_ok() {
    return new Object[][]{
            new Object[]{"01.1 - aliases / primary / disambiguation /  - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTitle/writeReleaseGroupTitle_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "mbid::b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTitle/writeReleaseGroupTitle_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTitle/writeReleaseGroupTitle_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTitle_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupAlias_ok")
public void test_writeReleaseGroupAlias_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupAlias_ok() {
    return new Object[][]{
            new Object[]{"01.1 - aliases / primary / disambiguation /  - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAlias/writeReleaseGroupAlias_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "mbid::b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAlias/writeReleaseGroupAlias_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAlias/writeReleaseGroupAlias_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupAlias_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupType_ok")
public void test_writeReleaseGroupType_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupType_ok() {
    return new Object[][]{
            new Object[]{"01.1 - aliases / primary / disambiguation - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupType_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
            new Object[]{"01.2 - secondary list - The Beatles, Yellow Submarine Songtrack",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.2-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Yellow Submarine Songtrack - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.2-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupType/writeReleaseGroupType_ok-01.2-exp.xml",
                                    "ids::10000010000_writeReleaseGroupType_mbid=9430dcc6-2164-4bec-bc33-9948f30063f7_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupTypeFromRels_ok")
public void test_writeReleaseGroupTypeFromRels_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupTypeFromRels_ok() {
    return new Object[][]{
            new Object[]{"01.1 - cover / mashes up / included in - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTypeFromRels_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
            new Object[]{"02.1 - 2x single - Animal Collective, Strawberry Jam",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Strawberry Jam - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTypeFromRels_mbid=89e4f726-2b6f-3f15-992e-b52105f53899_rg")))},
            new Object[]{"02.2 - single from - Animal Collective, Peacebone",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.2-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Animal Collective/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Peacebone - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.2-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-02.2-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTypeFromRels_mbid=1c893242-cfd8-3f02-822c-41cccf8c3a73_rg")))},
            new Object[]{"03.1 - remix - Madonna, Rebel Heart",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-03.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Rebel Heart - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-03.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-03.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTypeFromRels_mbid=ab88ca0b-da32-4e01-ae9b-198b0b7b8681_rg")))},
            new Object[]{"08.1 - series rels / tour in support of - Kiss, Monster",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-08.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/K/Kiss/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Kiss - Monster - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-08.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTypeFromRels/writeReleaseGroupTypeFromRels_ok-08.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTypeFromRels_mbid=28c5684e-59e1-400c-81fb-1ceb3d0fdd18_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupDate_ok")
public void test_writeReleaseGroupDate_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupDate_ok() {
    return new Object[][]{
            new Object[]{"01.1 - first release date - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDate/writeReleaseGroupDate_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDate/writeReleaseGroupDate_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDate/writeReleaseGroupDate_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupDate_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupTags_ok")
public void test_writeReleaseGroupTags_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupTags_ok() {
    return new Object[][]{
            new Object[]{"01.1 - some tags - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTags/writeReleaseGroupTags_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTags/writeReleaseGroupTags_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupTags/writeReleaseGroupTags_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupTags_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupUrls_ok")
public void test_writeReleaseGroupUrls_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupUrls_ok() {
    return new Object[][]{
            new Object[]{"01.1 - standard urls - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupUrls_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
            new Object[]{"03.1 - double discogs - Madonna, Rebel Heart",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Rebel Heart - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupUrls_mbid=ab88ca0b-da32-4e01-ae9b-198b0b7b8681_rg")))},
            new Object[]{"03.2 - double allmusic - Madonna, Rebel Heart Tour",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.2-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/M/Madonna/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Rebel Heart Tour - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.2-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-03.2-exp.xml",
                                    "ids::10000010000_writeReleaseGroupUrls_mbid=452a5dcd-6176-4e16-9e1e-ab45a395bb35_rg")))},
            new Object[]{"05.1 - imdb - Prince, Purple Rain",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-05.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/P/Prince/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Purple Rain - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-05.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupUrls/writeReleaseGroupUrls_ok-05.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupUrls_mbid=b93a7c47-a6d4-33f2-9034-53fdd991f4ba_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupRating_ok")
public void test_writeReleaseGroupRating_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupRating_ok() {
    return new Object[][]{
            new Object[]{"01.1 - rating - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupRating/writeReleaseGroupRating_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupRating/writeReleaseGroupRating_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupRating/writeReleaseGroupRating_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupRating_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupArtistCredits_ok")
public void test_writeReleaseGroupArtistCredits_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupArtistCredits_ok() {
    return new Object[][]{
            new Object[]{"07.1 - 2x a&r- Air, Talkie Walkie",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-07.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/A/Air/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Air - Talkie Walkie - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-07.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-07.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupArtistCredits_mbid=c636f6e8-0fe6-3470-b5af-b31a0ebed0bb_rg")))},
            new Object[]{"09.1 - 2x creative director- Kanye West, The Life of Pablo",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-09.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/W/West, Kanye/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/West, Kanye - The Life of Pablo - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-09.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupArtistCredits/writeReleaseGroupArtistCredits_ok-09.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupArtistCredits_mbid=8c18657a-6338-490d-a952-897663596b96_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupDisambiguation_ok")
public void test_writeReleaseGroupDisambiguation_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupDisambiguation_ok() {
    return new Object[][]{
            new Object[]{"01.1 - rating - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDisambiguation/writeReleaseGroupDisambiguation_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDisambiguation/writeReleaseGroupDisambiguation_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupDisambiguation/writeReleaseGroupDisambiguation_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupDisambiguation_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupAnnotation_ok")
public void test_writeReleaseGroupAnnotation_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupAnnotation_ok() {
    return new Object[][]{
            new Object[]{"10.1 - annotation - Coldplay, Mylo Xyloto",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAnnotation/writeReleaseGroupAnnotation_ok-10.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/C/Coldplay/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Coldplay - Mylo Xyloto - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAnnotation/writeReleaseGroupAnnotation_ok-10.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupAnnotation/writeReleaseGroupAnnotation_ok-10.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupAnnotation_mbid=61b7b6cd-86a6-4728-9478-b58c93bca8fd_rg")))},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeReleaseGroupInfo_ok")
public void test_writeReleaseGroupInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeReleaseGroupInfo_ok() {
    return new Object[][]{
            new Object[]{"01.1 - rating - The Beatles, The White Album",
                    "./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupInfo/writeReleaseGroupInfo_ok-01.1-list.txt",
                    "./src/test/resources/MusicBrainzScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/MusicBrainzScraperTest/_fileInput/B/Beatles, The/",
                                    "mbInput::./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - White Album - ReleaseGroup.xml",
                                    "base::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupInfo/writeReleaseGroupInfo_ok-01.1-base.xml",
                                    "exp::./src/test/resources/MusicBrainzScraperTest/writeReleaseGroupInfo/writeReleaseGroupInfo_ok-01.1-exp.xml",
                                    "ids::10000010000_writeReleaseGroupInfo_mbid=055be730-dcad-31bf-b550-45ba9c202aa3_rg")))},
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
    log = LogManager.getLogger(MusicBrainzScraperReleaseGroupTest.class);
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
