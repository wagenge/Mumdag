package org.mumdag.model;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.MappingRules;
import org.mumdag.utils.MapListUtils;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtilsTest;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

//-----------------------------------------------------------------------------

public class MumdagModelTest {

private static Logger log = null;


//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */
/*
//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_resolveXpathString_varargs_ok")
public void test_writeArtistName(String testDesc, String configFilePath, List<String> insertInfoList, String copyRule, String scraperName, String artistPath, String expRes) throws Exception {
    log.info("{} ... started", testDesc);

    String propKeyRawTemplatePath = "OutputXmlDoc.rawTemplatePath";
    PropertyHandler.getInstance().addPropertiesFromFile("./src/main/resources/configFiles/MusicBrainz.properties", "mubz");
    String scraperId = PropertyHandler.getInstance().getValue("MusicBrainz.Scraper.id");
    String mappingRulesFilePath = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesFilePath");
    String mappingRulesType = PropertyHandler.getInstance().getValue("MusicBrainz.mappingRulesType");
    MappingRules mappingRules = MappingRules.getInstance();
    mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);

    //prepare parameters for the
    HashMap<String, Object> insertInfo = MapListUtils.createObjMap(insertInfoList);

    // Generate an empty document with a given template file
    String rawTemplateFilePath = "";
    MumdagModel mm = null;
    OutputXmlDoc oxd = null;
    try {
        mm = new MumdagModel();
        oxd = mm.getMmdgModelXml();
        oxd.createOutputXmlDoc();
        rawTemplateFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKeyRawTemplatePath);
        oxd.overwriteTemplateXmlDoc(rawTemplateFilePath);
        mm.writeArtistName(insertInfo, copyRule, scraperName);
        oxd.writeOutputDocToFile(artistPath, "writeArtistName_ok-" + testDesc.substring(0, 2) + "-out.xml");
    } catch (Exception ex) {
        fail("No exception expected. Probably config file '" + configFilePath + "' or property keys '" + propKeyRawTemplatePath + "' not found!\n" + "Error: " + ex.getMessage());
    }

    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_resolveXpathString_varargs_ok() {
    return new Object[][]{
            new Object[]{"01 - ...",
                    Arrays.asList("name::The Beatles", "sortName::Beatles, The", "nameType::name", "unid::123abc"),
                    "ArtistName",
                    "MusicBrainz",
                    "..."},
    };
}

*/
//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
log = LogManager.getLogger(MumdagModelTest.class);
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