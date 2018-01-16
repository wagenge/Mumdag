package org.mumdag.core;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.mumdag.utils.XmlUtilsTest;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class MappingRulesTest {

private static Logger log = null;

//=============================================================================	
/*
 * 	TEST METHODS (public)
 */


//DOC:			nok  
//ASSERTION:	ok
@Test(dataProvider = "data_updateMappingRules_ok")
public void test_updateMappingRules_ok(String testDesc, String configFilePath, String scraperString, String expRes) {
    log.info("{}... started", testDesc);

	MappingRules mappingRules;
	String[] scraperList = scraperString.split("\\|");

	mappingRules = MappingRules.getInstance();

    for (String scraperListEntry : scraperList) {
		try {
			String scraperId = PropertyHandler.getInstance(configFilePath).getValue(scraperListEntry+".scraperId");
			String mappingRulesFilePath = PropertyHandler.getInstance().getValue(scraperListEntry+".mappingRulesFilePath");
			String mappingRulesType = PropertyHandler.getInstance().getValue(scraperListEntry+".mappingRulesType");
			mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);
		} catch (Exception ex) {
            log.error("{}... failed", testDesc);
            fail("No exception expected. Probably config file " + configFilePath + " not found!\n" + "Error: " + ex.getMessage());
		}
	}

	HashMap<String, HashMap<String, String>> mr = mappingRules.getMappingRules();
    String res = mr.toString();
    assertThat(res).isEqualTo(expRes);

    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok  
@DataProvider
public Object[][] data_updateMappingRules_ok() {
	return new Object[][] {
		new Object[] {"01 - two mapping rule maps, both correct",
		                "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_ok-1.properties",
		                "MumdagModel|MusicBrainzScraper",
                        "{mmdg.ArtistNode={abs=/Artist[_arid_], subType=node, type=xpath}, " +
                            "mmdg.ArtistId={abs=/Artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistId={abs=/metadata/artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistName={abs=/metadata/artist[_arid_]/name, subType=text, type=xpath}}"},
        new Object[] {"02 - two mapping rule maps, one with corrupt line (without '.')",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_ok-2.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "{mmdg.ArtistNode={subType=node, type=xpath}, " +
                            "mmdg.ArtistId={abs=/Artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistId={abs=/metadata/artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistName={abs=/metadata/artist[_arid_]/name, subType=text, type=xpath}}"},
        new Object[] {"03 - two mapping rule maps, one with corrupt line (without '=')",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_ok-3.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "{mmdg.ArtistNode={subType=node, type=xpath}, " +
                            "mmdg.ArtistId={abs=/Artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistId={abs=/metadata/artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistName={abs=/metadata/artist[_arid_]/name, subType=text, type=xpath}}"},
        new Object[] {"04 - two mapping rule maps, one with corrupt line ('=' before '.')",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_ok-4.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "{mmdg.ArtistNode={subType=node, type=xpath}, " +
                            "mmdg.ArtistId={abs=/Artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistId={abs=/metadata/artist/@_arid_, subType=attribute, type=xpath}, " +
                            "mubz.ArtistName={abs=/metadata/artist[_arid_]/name, subType=text, type=xpath}}"},
    };
}

//-----------------------------------------------------------------------------

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_updateMappingRules_nok")
public void test_updateMappingRules_nok(String testDesc, String configFilePath, String scraperString, String expResStr) {
	log.info("{} ... started", testDesc);
    String[] expResList = expResStr.split("\\|\\|");
    MappingRules mappingRules;
	String[] scraperList = scraperString.split("\\|");

	mappingRules = MappingRules.getInstance();

    for (String scraperListEntry : scraperList) {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            String scraperId = PropertyHandler.getInstance(configFilePath).getValue(scraperListEntry+".scraperId");
            String mappingRulesFilePath = PropertyHandler.getInstance().getValue(scraperListEntry+".mappingRulesFilePath");
            String mappingRulesType = PropertyHandler.getInstance().getValue(scraperListEntry+".mappingRulesType");
            mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);
        }).withStackTraceContaining(expResList[0]).withStackTraceContaining(expResList[1]);
	}
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_updateMappingRules_nok() {
    return new Object[][] {
        new Object[] {"01 - rule file not found, FileNotFoundException",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_nok-1.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "java.io.FileNotFoundException||Das System kann die angegebene Datei nicht finden"},
        new Object[] {"02 - scraperId not found, Exception",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_nok-2.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "Exception||Parameter scraperId not found!"},
        new Object[] {"03 - ruleType not found, Exception",
                        "./src/test/resources/MappingRulesTest/updateMappingRules/config_updateMappingRules_nok-3.properties",
                        "MumdagModel|MusicBrainzScraper",
                        "Exception||Parameter ruleType not found!"},
    };
}

//-----------------------------------------------------------------------------

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_getMappingRule_ok")
public void test_getMappingRule_ok(String testDesc, String configFilePath, String scraperString, String expScraperId, String expRuleName, String expRes) {
    log.info("{}... started", testDesc);

    String res = "";
    MappingRules mappingRules = MappingRules.getInstance();
    try {
        String scraperId = PropertyHandler.getInstance(configFilePath).getValue(scraperString+".scraperId");
        String mappingRulesFilePath = PropertyHandler.getInstance().getValue(scraperString+".mappingRulesFilePath");
        String mappingRulesType = PropertyHandler.getInstance().getValue(scraperString+".mappingRulesType");
        mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);
        HashMap<String, String> mr = mappingRules.getMappingRule(expScraperId, expRuleName);
        if(mr != null) {
            res = mr.toString();
        }
        assertThat(res).isEqualTo(expRes);
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Probably config file " + configFilePath + " not found!\n" + "Error: " + ex.getMessage());
    }
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getMappingRule_ok() {
    return new Object[][] {
        new Object[] {"01 - one mapping rule file, scraperId ok, ruleName ok, rule found",
                        "./src/test/resources/MappingRulesTest/getMappingRule/config_getMappingRule_ok-1.properties",
                        "MumdagModel", "mmdg", "ArtistNode",
                        "{abs=/Artist[_arid_], subType=node, type=xpath}"},
        new Object[] {"02 - one mapping rule file, scraperId wrong, ruleName ok, rule not found",
                        "./src/test/resources/MappingRulesTest/getMappingRule/config_getMappingRule_ok-2.properties",
                        "MumdagModel", "mmdl", "ArtistNode",
                        ""},
        new Object[] {"03 - one mapping rule file, scraperId null, ruleName ok, rule not found",
                        "./src/test/resources/MappingRulesTest/getMappingRule/config_getMappingRule_ok-3.properties",
                        "MumdagModel", null, "ArtistNode",
                        ""},
        new Object[] {"04 - one mapping rule file, scraperId null, ruleName null, rule not found",
                        "./src/test/resources/MappingRulesTest/getMappingRule/config_getMappingRule_ok-4.properties",
                        "MumdagModel", null, null,
                        ""},
    };
}

//-----------------------------------------------------------------------------

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_getMappingRuleElem_ok")
public void test_getMappingRuleElement_ok(String testDesc, String configFilePath, String scraperString, String expScraperId, String expRuleName, String expRuleElem, String expRes) {
    log.info("{}... started", testDesc);

    MappingRules mappingRules = MappingRules.getInstance();
    try {
        String scraperId = PropertyHandler.getInstance(configFilePath).getValue(scraperString+".scraperId");
        String mappingRulesFilePath = PropertyHandler.getInstance().getValue(scraperString+".mappingRulesFilePath");
        String mappingRulesType = PropertyHandler.getInstance().getValue(scraperString+".mappingRulesType");
        mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);
        String res = mappingRules.getMappingRuleElement(expScraperId, expRuleName, expRuleElem);
        assertThat(res).isEqualTo(expRes);
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Probably config file " + configFilePath + " not found!\n" + "Error: " + ex.getMessage());
    }
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getMappingRuleElem_ok() {
    return new Object[][] {
            new Object[] {"01 - one mapping rule file, scraperId ok, ruleName ok, ruleElem ok, rule found",
                            "./src/test/resources/MappingRulesTest/getMappingRuleElem/config_getMappingRuleElem_ok-1.properties",
                            "MumdagModel", "mmdg", "ArtistNode", "abs",
                            "/Artist[_arid_]"},
    };
}
//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(XmlUtilsTest.class);
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
public void destroyMappingRulesInstanceBefore() throws Exception {
	Field instance = MappingRules.class.getDeclaredField("instance");
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
public void destroyMappingRulesInstanceAfter() throws Exception {
	Field instance = MappingRules.class.getDeclaredField("instance");
	instance.setAccessible(true);
	instance.set(null, null);
}

//-----------------------------------------------------------------------------

}
