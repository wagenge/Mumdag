package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.LocalMusicIndexer;
import org.mumdag.model.index.Artist;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.ListUtils;
import org.mumdag.utils.MapUtils;
import org.mumdag.utils.PropertyHandler;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

//-----------------------------------------------------------------------------

public class LocalMusicScraperTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS (public)
 */

//DOC:	nok
@Test(dataProvider = "data_writeMusicBrainzArtistId_ok")
public void test_writeMusicBrainzArtistId_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeMusicBrainzArtistId_ok() {
    return new Object[][]{
           new Object[]{"01 - one group, empty base - Air",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                        MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                            "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-01-base.xml",
                            "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-01-exp.xml",
                            "ids::10000000000-writeMusicBrainzArtistId"))),
            },
            new Object[]{"02 - two groups, emtpy bases - Air, Animal Collective",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Arrays.asList(
                        MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                            "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-02a-base.xml",
                            "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-02a-exp.xml",
                            "ids::10000000000-writeMusicBrainzArtistId")),
                        MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                            "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-02b-base.xml",
                            "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-02b-exp.xml",
                            "ids::20000000000-writeMusicBrainzArtistId"))
                    ),
            },
           new Object[]{"03 - one group, non empty base - Air",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId"))),
            },
            new Object[]{"04 - one group, wrong base - Air",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzArtistId/writeMusicBrainzArtistId_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeMusicBrainzReleaseGroupId_ok")
public void test_writeMusicBrainzReleaseGroupId_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeMusicBrainzReleaseGroupId_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, two albums (one with special chars), empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseGroupId"))),
            },
            new Object[]{"02 - one group, two albums (one with special chars), non-empty base (artist, release group) - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseGroupId"))),
            },
            new Object[]{"03 - one group, two albums (one with special chars), non-empty base (artist) - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseGroupId/writeMusicBrainzReleaseGroupId_ok-03-exp.xml",
                                    "ids::10000010000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseGroupId"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeMusicBrainzReleaseId_ok")
public void test_writeMusicBrainzReleaseId_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeMusicBrainzReleaseId_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId"))),
            },
            new Object[]{"02 - one group, two albums with one release each, empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000020000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseId"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, non-empty base(artist, release group) - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-03-exp.xml",
                                    "ids::10000010000-writeMusicBrainzReleaseId"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, non-empty base(artist, release group, release) - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-04-exp.xml",
                                    "ids::10000010000-writeMusicBrainzReleaseId"))),
            },
            new Object[]{"05 - one group, one group, one album with one release, non-empty base(artist, release group, wrong release) - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-05-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-05-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzReleaseId/writeMusicBrainzReleaseId_ok-05-exp.xml",
                                    "ids::10000010000-writeMusicBrainzReleaseId"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeMediumDiscNo_ok")
public void test_writeMediumDiscNo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeMediumDiscNo_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, one medium, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId,10000010100-writeMediumDiscNo"))),
            },
            new Object[]{"02 - one group, one group, one album with one release, two media, empty base - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, two media, non-empty base (artist) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, two media, non-empty base (artist, release group) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"05 - one group, one group, one album with one release, two media, non-empty base (artist, release group, release) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-05-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-05-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-05-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"06 - one group, one group, one album with one release, two media, non-empty base (artist, release group, release, media #0) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-06-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-06-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-06-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"07 - one group, one group, one album with one release, two media, non-empty base (artist, release group, release, media #1) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-07-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-07-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-07-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
            new Object[]{"08 - one group, one group, one album with one release, two media, non-empty base (artist, release group, release, media #1+2) - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-08-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-08-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMediumDiscNo/writeMediumDiscNo_ok-08-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010200-writeMediumDiscNo"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeMusicBrainzTrackId_ok")
public void test_writeMusicBrainzTrackId_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeMusicBrainzTrackId_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, one medium, one track, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId"))),
            },
            new Object[]{"02 - one group, two albums with one release each, one medium each, one track, empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId," +
                                            "10000020000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseId," +
                                            "10000020100-writeMediumDiscNo,10000020101-writeMusicBrainzTrackId"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, two media, each one track, empty base - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId," +
                                            "10000010200-writeMediumDiscNo,10000010201-writeMusicBrainzTrackId"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, one medium, one track, full base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId"))),
            },
            new Object[]{"05 - one group, one group, one album with one release, one medium, one track, full base without release group and track - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-05-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-05-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeMusicBrainzTrackId/writeMusicBrainzTrackId_ok-05-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeTrackAudioInfo_ok")
public void test_writeTrackAudioInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeTrackAudioInfo_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, one medium, one track, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId," +
                                            "10000010101-writeTrackAudioInfo"))),
            },
            new Object[]{"02 - one group, two albums with one release each, one medium each, one track, empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo," +
                                            "10000020000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseId," +
                                            "10000020100-writeMediumDiscNo,10000020101-writeMusicBrainzTrackId,10000020101-writeTrackAudioInfo"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, two media, each one track, empty base - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo," +
                                            "10000010200-writeMediumDiscNo,10000010201-writeMusicBrainzTrackId,10000010201-writeTrackAudioInfo"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, one medium, one track, full base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackAudioInfo/writeTrackAudioInfo_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeTrackFileInfo_ok")
public void test_writeTrackFileInfo_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeTrackFileInfo_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, one medium, one track, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo"))),
            },
            new Object[]{"02 - one group, two albums with one release each, one medium each, one track, empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo," +
                                            "10000020000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseId," +
                                            "10000020100-writeMediumDiscNo,10000020101-writeMusicBrainzTrackId,10000020101-writeTrackAudioInfo,10000020101-writeTrackFileInfo"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, two media, each one track, empty base - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo,10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo," +
                                            "10000010200-writeMediumDiscNo,10000010201-writeMusicBrainzTrackId,10000010201-writeTrackAudioInfo,10000010201-writeTrackFileInfo"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, one medium, one track, full base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackFileInfo/writeTrackFileInfo_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo"))),
            },
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeTrackTags_ok")
public void test_writeTrackTags_ok(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", fileListPath, basePath, testConfigs);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeTrackTags_ok() {
    return new Object[][]{
            new Object[]{"01 - one group, one group, one album with one release, one medium, one track, empty base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-01-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-01-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-01-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackTags(ARTIST|GENRE)"))),
            },
            new Object[]{"02 - one group, two albums with one release each, one medium each, one track, empty base - Air/Premier Symptômes, Moon Safari",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-02-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-02-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-02-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo,10000010101-writeTrackTags(ARTIST|GENRE)" +
                                            "10000020000-writeMusicBrainzReleaseGroupId,10000020000-writeMusicBrainzReleaseId," +
                                            "10000020100-writeMediumDiscNo," +
                                            "10000020101-writeMusicBrainzTrackId,10000020101-writeTrackAudioInfo,10000020101-writeTrackFileInfo,10000020101-writeAllTrackTags"))),
            },
            new Object[]{"03 - one group, one group, one album with one release, two media, each one track, empty base - Animal Collective/Spirit They're Gone, Spirit They've Vanished ¦ Danse Manatee",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-03-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Animal Collective/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-03-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-03-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId,10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo,10000010101-writeTrackTags(ARTIST|GENRE)" +
                                            "10000010200-writeMediumDiscNo," +
                                            "10000010201-writeMusicBrainzTrackId,10000010201-writeTrackAudioInfo,10000010201-writeTrackFileInfo,10000010201-writeAllTrackTags"))),
            },
            new Object[]{"04 - one group, one group, one album with one release, one medium, one track, full base - Air/Premier Symptômes",
                    "./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-04-list.txt",
                    "./src/test/resources/LocalMusicScraperTest/_fileInput/",
                    Collections.singletonList(
                            MapUtils.array2Map("::", Arrays.asList("artistFolder::./src/test/resources/LocalMusicScraperTest/_fileInput/A/Air/",
                                    "base::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-04-base.xml",
                                    "exp::./src/test/resources/LocalMusicScraperTest/writeTrackTags/writeTrackTags_ok-04-exp.xml",
                                    "ids::10000000000-writeMusicBrainzArtistId," +
                                            "10000010000-writeMusicBrainzReleaseGroupId,10000010000-writeMusicBrainzReleaseId," +
                                            "10000010100-writeMediumDiscNo," +
                                            "10000010101-writeMusicBrainzTrackId,10000010101-writeTrackAudioInfo,10000010101-writeTrackFileInfo,10000010101-writeTrackTags(ARTIST|GENRE)"))),
            },
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
    log = LogManager.getLogger(LocalMusicScraperTest.class);
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
public void destroyLocalMusicScraperInstanceBefore() throws Exception {
    Field instance = LocalMusicScraper.class.getDeclaredField("instance");
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
public void destroyLocalMusicScraperInstanceAfter() throws Exception {
    Field instance = LocalMusicScraper.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
}


//=============================================================================
/*
 * 	METHODS FOR TEST PREPARATION, EXECUTION AND VALIDATION (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
private void prepareExecuteValidateTest(String testDesc, String oknok, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
    //prepare test
    // - for each artist config => copy base file to artist folder
    // - read file list and build local music index
    prepareTest(testDesc, fileListPath, basePath, testConfigs);

    //build music index
    MusicIndex mi = MusicIndex.getInstance();
    log.info("Building music index ... {} artists and {} tracks found", mi.getNumOfArtists(), mi.getNumOfTracks());

    //execute and validate test
    for(HashMap<String, String> testConfig : testConfigs) {
        executeTest(testConfig, mi);
        validateTest(testDesc, oknok, testConfig, mi);
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
private void prepareTest(String testDesc, String fileListPath, String basePath, List<HashMap<String, String>> testConfigs) {
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
            Assert.fail("No exception expected copying while the base xyml file.\n" + "Error: " + ex.getMessage());
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
private void executeTest(HashMap<String, String> testConfig, MusicIndex mi) {
    List<String> ids = new ArrayList<>(Arrays.asList(testConfig.get("ids").split(",")));
    for(String idPlusMethod : ids) {
        String[] idPlusMethodArr = idPlusMethod.split("-");
        Long entryId = Long.valueOf(idPlusMethodArr[0]);
        String calledMethod = idPlusMethodArr[1];

        LocalMusicScraper lms = LocalMusicScraper.getInstance();
        TreeMap<Long, HashMap<String, Object>> fmi =  mi.getFlatMusicIndex();
        for (Map.Entry<Long, HashMap<String, Object>> entry : fmi.entrySet()) {
            if(entry.getKey().equals(entryId)) {
                mi.setCurrentEntryId(entryId);
                //execute correct scraper method
                if(calledMethod.equals("writeMusicBrainzArtistId")) {
                    lms.writeMusicBrainzArtistId();
                } else if(calledMethod.equals("writeMusicBrainzReleaseGroupId")) {
                    lms.writeMusicBrainzReleaseGroupId();
                } else if(calledMethod.equals("writeMusicBrainzReleaseId")) {
                    lms.writeMusicBrainzReleaseId();
                } else if(calledMethod.equals("writeMediumDiscNo")) {
                    lms.writeMediumDiscNo();
                } else if(calledMethod.equals("writeMusicBrainzTrackId")) {
                    lms.writeMusicBrainzTrackId();
                } else if(calledMethod.equals("writeTrackAudioInfo")) {
                    lms.writeTrackAudioInfo();
                } else if(calledMethod.equals("writeTrackFileInfo")) {
                    lms.writeTrackFileInfo();
                } else if(calledMethod.contains("writeTrackTags")) {
                    String[] parameterStrArray = StringUtils.substringBetween(calledMethod, "(", ")").split("\\|");
                    Object[] parameterArray = Arrays.stream(parameterStrArray).toArray(Object[]::new);
                    lms.writeTrackTags(parameterArray);
                } else if(calledMethod.equals("writeAllTrackTags")) {
                    lms.writeAllTrackTags();
                } else {
                    log.error("Entry {}: Test for method '{}' not yet implemented", entryId, calledMethod);
                    fail("Entry " + entryId +": Test for method '" + calledMethod + "' not yet implemented!");
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void validateTest(String testDesc, String oknok, HashMap<String, String> testConfig, MusicIndex mi) {
    //set correct absolut path for testfiles
    String pathStr = testConfig.get("exp");
    String testNo = StringUtils.substringBetween(pathStr, "-", "-");
    Path path = Paths.get(pathStr);
    String testPath = path.getParent().toAbsolutePath().toString();
    String parentFolderName =  path.getParent().getFileName().toString();

    Artist artist = mi.getCurrentArtist();
    artist.writeFullXmlDocument(testPath, parentFolderName + "_" + oknok + "-" + testNo + "-out.xml");
    String xmlString = artist.getXmlDocumentAsString();
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