package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.MumdagModel;
import org.testng.Assert;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mumdag.model.OutputXmlDoc;
import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class MusicBrainzScraperTest {

private static Logger log = null;

//=============================================================================	
/*
 * 	TEST METHODS (public)
 */	

//DOC:	nok
@Test(dataProvider = "data_writeArtistName_ok")
public void test_writeArtistName_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistName", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistName_ok() {
    return new Object[][]{
        new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-base.xml",
                        "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                        "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-exp.xml")},
		new Object[] {"02 - group, not dissolved - Animal Collective",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02-base.xml",
                        "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
						"Animal Collective", "0c751690-c784-4a4f-b1e4-c1de27d47581",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02-exp.xml")},
		new Object[] {"03 - group, not dissolved - Animal Collective - name already existing -> update",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-03-base.xml",
                        "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
						"Animal Collective", "0c751690-c784-4a4f-b1e4-c1de27d47581",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-03-exp.xml")},
		new Object[] {"04 - group, not dissolved - Animal Collective - alias name existing -> add",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-04-base.xml",
                        "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
						"Animal Collective", "0c751690-c784-4a4f-b1e4-c1de27d47581",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-04-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok

    @Test(dataProvider = "data_writeArtistName_nok")
    public void test_writeArtistName_nok(String testDesc, String configFilePath, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
        log.info("{} ... started", testDesc);
        try {
            PropertyHandler.getInstance(configFilePath);
        } catch (Exception ex) {
            log.error("{}... failed", testDesc);
            fail("No exception expected. Probably config file '" + configFilePath + "' not found!\n" + "Error: " + ex.getMessage());
        }
        prepareExecuteValidateTest(testDesc, "nok", baseXmlFile, sourceXmlFile, "writeArtistName", artistName, unid, artistPath, expRes);
        log.info("{} ... finished successfully!", testDesc);
    }
//-----------------------------------------------------------------------------

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

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistAlias_ok")
public void test_writeArtistAlias_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistAlias", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistAlias_ok() {
	return new Object[][] {
          new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-base.xml",
                "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/",
                Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-exp.xml")},
	};
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistTypeAndGender_ok")
public void test_writeArtistTypeAndGender_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
	log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistTypeAndGender", artistName, unid, artistPath, expRes);
	log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistTypeAndGender_ok() {
	return new Object[][] {
			new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
					"Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-exp.xml")},
			new Object[] {"02 - person, female, dead - Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-02-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
					"Joplin, Janis", "76c9a186-75bd-436a-85c0-823e3efddb7f",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-02-exp.xml")},
			new Object[] {"03 - person, female, dead, type and gender existing - Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-03-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
					"Joplin, Janis", "76c9a186-75bd-436a-85c0-823e3efddb7f",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-03-exp.xml")},
	};
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistUrls_ok")
public void test_writeArtistUrls_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistUrls", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistUrls_ok() {
    return new Object[][] {
        new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-01-base.xml",
                        "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                        "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/",
                        Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistUrls/writeArtistUrls_ok-01-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistIpiAndIsni_ok")
public void test_writeArtistIpiAndIsni_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistIpiAndIsni", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistIpiAndIsni_ok() {
    return new Object[][] {
        new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-01-base.xml",
                "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/",
                Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-01-exp.xml")},
        new Object[] {"02 - female, alive, multiple isni/ipi - Madonna",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-02-base.xml",
                "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                "Madonna", "79239441-bfd5-4981-a70c-55c3f15c1287",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/",
                Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistIpiAndIsni/writeArtistIpiAndIsni_ok-02-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistPeriod_ok")
public void test_writeArtistPeriod_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistPeriod", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistPeriod_ok() {
    return new Object[][] {
        new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/writeArtistPeriod_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/writeArtistPeriod_ok-01-exp.xml")},
        new Object[] {"02 - female, dead - Janis Joplin",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/writeArtistPeriod_ok-02-base.xml",
                "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                    "Janis Joplin", "76c9a186-75bd-436a-85c0-823e3efddb7f",
                "./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistPeriod/writeArtistPeriod_ok-02-exp.xml")},
    };
}


//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistCredits_ok")
public void test_writeArtistCredits_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistCredits", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistCredits_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-01-exp.xml")},
            new Object[] {"02 - band, ended, disambiguation, multiple members at different times - Madness",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-02-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madness - Artist.xml",
                    "Madness", "5f58803e-8c4c-478e-8b51-477f38483ede",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistCredits/writeArtistCredits_ok-02-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistTags_ok")
public void test_writeArtistTags_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistTags", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistTags_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTags/writeArtistTags_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistTags/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTags/writeArtistTags_ok-01-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistRating_ok")
public void test_writeArtistRating_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistRating", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistRating_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistRating/writeArtistRating_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistRating/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistRating/writeArtistRating_ok-01-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistAnnotation_ok")
public void test_writeArtistAnnotation_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistAnnotation", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistAnnotation_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistAnnotation/writeArtistAnnotation_ok-01-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistDisambiguation_ok")
public void test_writeArtistDisambiguation_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistDisambiguation", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistDisambiguation_ok() {
    return new Object[][] {
            new Object[] {"01 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/writeArtistDisambiguation_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                    "Prince", "070d193a-845c-479f-980e-bef15710653e",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistDisambiguation/writeArtistDisambiguation_ok-01-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistArea_ok")
public void test_writeArtistArea_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistArea", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistArea_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistArea/writeArtistArea_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistArea/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistArea/writeArtistArea_ok-01-exp.xml")},
            new Object[] {"02 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistArea/writeArtistArea_ok-02-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                    "Prince", "070d193a-845c-479f-980e-bef15710653e",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistArea/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistArea/writeArtistArea_ok-02-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistInfo_ok")
public void test_writeArtistInfo_ok(String testDesc, String baseXmlFile, String sourceXmlFile, String artistName, String unid, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);
    prepareExecuteValidateTest(testDesc, "ok", baseXmlFile, sourceXmlFile, "writeArtistInfo", artistName, unid, artistPath, expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistInfo_ok() {
    return new Object[][] {
            new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-01-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Beatles, The - Artist.xml",
                    "Beatles, The", "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-01-exp.xml")},
            new Object[] {"02 - group, active, alias without type attr - Animal Collective",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-02-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Animal Collective - Artist.xml",
                    "Animal Collective", "0c751690-c784-4a4f-b1e4-c1de27d47581",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-02-exp.xml")},
            new Object[] {"03 - female, alive, alias with type attr - Madonna",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-03-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madonna - Artist.xml",
                    "Madonna", "79239441-bfd5-4981-a70c-55c3f15c1287",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-03-exp.xml")},
            new Object[] {"04 - female, dead - Janis Joplin",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-04-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Joplin, Janis - Artist.xml",
                    "Janis Joplin", "76c9a186-75bd-436a-85c0-823e3efddb7f",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-04-exp.xml")},
            new Object[] {"05 - male, dead, disambiguation, multiple discogs urls - Prince",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-05-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Prince - Artist.xml",
                    "Prince", "070d193a-845c-479f-980e-bef15710653e",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-05-exp.xml")},
            new Object[] {"06 - band, ended, disambiguation, multiple members at different times - Madness",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-06-base.xml",
                    "./src/test/resources/MusicBrainzScraperTest/_mbInputs/Madness - Artist.xml",
                    "Madness", "5f58803e-8c4c-478e-8b51-477f38483ede",
                    "./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/",
                    Collections.singletonList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistInfo/writeArtistInfo_ok-06-exp.xml")},
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
    log = LogManager.getLogger(MusicBrainzScraperTest.class);
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


//=============================================================================
/*
 * 	METHODS FOR TEST PREPARATION, EXECUTION AND VALIDATION (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
private void prepareExecuteValidateTest(String testDesc, String oknok, String baseXmlFile, String sourceXmlFile, String method,
                                        String artistName, String unid, String artistPath, List<String> expRes) {
    //prepare test
    // - generate an empty document with a given template file
    // - read the xml file and stores it in an object array
    HashMap<String, Object> prepTestmap = prepareTest(testDesc, baseXmlFile, sourceXmlFile, unid);
    MumdagModel mm = (MumdagModel)prepTestmap.get("model");
    Object[] inputObj = (Object[])prepTestmap.get("input");

    //execute the test
    executeTest(testDesc, oknok, artistName, artistPath, method, mm, inputObj);

    //validate test results
    validateTest(testDesc, mm, expRes);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
private HashMap<String, Object> prepareTest(String testDesc, String baseXmlFile, String sourceXmlFile, String unid) {
	HashMap<String, Object> retMap = new HashMap<>();

	// Generate an empty document with a given template file
	MumdagModel mm = null;
	//OutputXmlDoc oxd;
	try {
		mm = new MumdagModel(baseXmlFile);
		//oxd = mm.getMmdgModel();
		//oxd.overwriteTemplateXmlDoc(rawTemplateFilePath);
	} catch (Exception ex) {
		log.error("{}... failed", testDesc);
		fail("No exception expected. Problems instantiating MumdagModel and/or OutputXmlDoc!\n" + "Error: " + ex.getMessage());
	}
	retMap.put("model", mm);

	// read the xml file and stores it in an object array
	String infoXml = "";
	try {
	    if(StringUtils.isNotEmpty(sourceXmlFile)) {
            infoXml = new String(Files.readAllBytes(Paths.get(sourceXmlFile)));
        }
	} catch (Exception ex) {
		log.error("{}... failed", testDesc);
		fail("No exception expected. Probably the xml file '" + sourceXmlFile + "' is missing!\n" + "Error: " + ex.getMessage());
	}
	Object[] infoObj = new Object[2];
	infoObj[0] = unid;
	if(sourceXmlFile == null) {
        infoObj[1] = null;
    }
    else if(sourceXmlFile.length() == 0) {
        infoObj[1] = "";
    }
    infoObj[1] = infoXml;
    retMap.put("input", infoObj);

	return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
private void executeTest(String testDesc, String oknok, String artistName, String artistPath, String calledMethod, MumdagModel mm, Object[] inputObj) {
    // Generate an MB scraper object and execute the writeArtistInfo method
    MusicBrainzScraper mbs;
    try {
        mbs = new MusicBrainzScraper(artistName, artistPath, mm);
        switch (calledMethod) {
            case "writeArtistName":
                mbs.writeMusicBrainzArtistName(inputObj);
                break;
            case "writeArtistAlias":
                mbs.writeMusicBrainzArtistAlias(inputObj);
                break;
            case "writeArtistTypeAndGender":
                mbs.writeMusicBrainzArtistTypeAndGender(inputObj);
                break;
            case "writeArtistCredits":
                mbs.writeMusicBrainzArtistCredits(inputObj);
                break;
            case "writeArtistUrls":
                mbs.writeMusicBrainzArtistUrls(inputObj);
                break;
            case "writeArtistIpiAndIsni":
                mbs.writeMusicBrainzArtistIpiIsni(inputObj);
                break;
            case "writeArtistPeriod":
                mbs.writeMusicBrainzArtistPeriod(inputObj);
                break;
            case "writeArtistTags":
                mbs.writeMusicBrainzArtistTags(inputObj);
                break;
            case "writeArtistRating":
                mbs.writeMusicBrainzArtistRating(inputObj);
                break;
            case "writeArtistAnnotation":
                mbs.writeMusicBrainzArtistAnnotation(inputObj);
                break;
            case "writeArtistArea":
                mbs.writeMusicBrainzArtistAreaList(inputObj);
                break;
            case "writeArtistDisambiguation":
                mbs.writeMusicBrainzArtistDisambiguation(inputObj);
                break;
            case "writeArtistInfo":
                mbs.writeMusicBrainzArtistInfo(inputObj);
                break;
            default:
                fail("Test for method '" + calledMethod + "' not yet implemented!");
        }
        mm.writeOutputDocToFile(artistPath, calledMethod + "_" + oknok + "-" + testDesc.substring(0, 2) + "-out.xml");
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Problems executing scraper method!\n" + "Error: " + ex.getMessage());
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void validateTest(String testDesc, MumdagModel mm, List<String> expRes) {
    OutputXmlDoc oxd = mm.getMmdgModel();

    //extraction the validation information from data provider
    for(String valString: expRes) {
        String[] valParamList = valString.split("\\|");
        String valXpath = valParamList[0];
        String valType = valParamList[1];
        String valAttrName = valParamList[2];
        String expValue = valParamList[3];

        //validate
        switch (valType) {
            case "attr":
                String attrValue = oxd.getNodeAttributeTextByXPath(valXpath, valAttrName);
                assertThat(attrValue).isEqualTo(expValue);
                break;
            case "node":
                String nodeValue = oxd.getNodeTextByXPath(valXpath);
                assertThat(nodeValue).isEqualTo(expValue);
                break;
            case "xml":
                String xmlString = "";
                String expXmlString = "";
                try {
                    xmlString = oxd.getOutputDocAsString();
                    expXmlString = FileUtils.readFileToString(new File(expValue), "UTF-8");
                } catch (Exception ex) {
                    log.error("{}... failed", testDesc);
                    Assert.fail("No exception expected while comparing the xml strings.\n" + "Error: " + ex.getMessage());
                }
                assertThat(xmlString).isXmlEqualTo(expXmlString);
                break;
            default:
                Assert.fail("Xpath validation type '" + valType + "' unknown. Expecting 'attr', 'node' or 'xml'");
        }
    }
}

//-----------------------------------------------------------------------------

}
