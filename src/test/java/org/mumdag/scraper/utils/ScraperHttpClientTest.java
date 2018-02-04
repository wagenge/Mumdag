package org.mumdag.scraper.utils;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class ScraperHttpClientTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS (public)
 */


@Test
public void testGetInstance() {
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_run_ok")
public void test_run_ok(String testDesc, String scraperName, String section, String placeholder, String unid, Integer numOfRuns, String expRes) {
    log.info("{} ... started", testDesc);
    HashMap<String, String> resMap;
    try {
        //load MusicBrainz specific properties
        PropertyHandler.getInstance().addPropertiesFromFile("./src/main/resources/configFiles/MusicBrainz.properties", "mubz");
        String baseUrl = PropertyHandler.getInstance().getValue(scraperName+".Scraper.Base.wsUrl");
        String url = PropertyHandler.getInstance().getValue(scraperName+".Scraper."+section+".wsUrl").replaceAll(placeholder, unid);

        ScraperHttpClient scraperHttpClient = ScraperHttpClient.getInstance();
        for(int i = 0; i < numOfRuns; i++) {
            resMap = scraperHttpClient.run(baseUrl + url);
            assertThat(resMap.get("rspCode")).isEqualTo(expRes);
        }
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected!\n" + "Error: " + ex.getMessage());
    }

    //validate


    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_run_ok() {
    return new Object[][]{
            new Object[]{"01 - MusicBrainz Url Artist - The Beatles",
                    "MusicBrainz",
                    "Artist",
                    "_arid_",
                    "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    30,
                    "200"},
            new Object[]{"01 - MusicBrainz Url ReleaseGroup - Animal Collective/Hollinndagain",
                    "MusicBrainz",
                    "ReleaseGroup",
                    "_rgid_",
                    "d65b5cb7-6ea6-3eb8-abc6-b7bdd42f0e69",
                    30,
                    "200"},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_run_nok")
public void test_run_nok(String testDesc, String url, String expRes) {
    log.info("{} ... started", testDesc);
    HashMap<String, String> resMap;
    try {
        ScraperHttpClient scraperHttpClient = ScraperHttpClient.getInstance();
        resMap = scraperHttpClient.run(url);
        assertThat(resMap.get("rspCode")).isEqualTo(expRes);
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected!\n" + "Error: " + ex.getMessage());
    }
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_run_nok() {
    return new Object[][]{
            new Object[]{"01 - 400, bad request (https://musicbrainz.org/ws/2/artist/123)",
                    "https://musicbrainz.org/ws/2/artist/123",
                    "400"},
            new Object[]{"02 - 404, page not found (https://musicbrainz.org/ws/2/artist/0c751690-c784-4a4f-b1e4-c1de27d47582)",
                    "https://musicbrainz.org/ws/2/artist/0c751690-c784-4a4f-b1e4-c1de27d47582",
                    "404"},
            new Object[]{"03 - 500, wrong url (www.example.com)",
                    "www.example.com",
                    "500"},
            new Object[]{"04 - 500, bad gateway (https://musicbrainz.org/ws/2/release-groub)",
                    "https://musicbrainz.org/ws/2/release-groub",
                    "500"},

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
    log = LogManager.getLogger(ScraperHttpClientTest.class);
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