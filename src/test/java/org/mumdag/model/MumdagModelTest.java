package org.mumdag.model;

//-----------------------------------------------------------------------------

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.MappingRules;
import org.mumdag.utils.MapListUtils;
import org.mumdag.utils.PropertyHandler;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
@Test(dataProvider = "data_writeArtistNames_ok")
public void test_writeArtistNames_ok(String testDesc, String calledMethod, String configFilePath, List<String> insertInfoList, List<String> additionalInfoList, String copyRule,
                                    String scraperName, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);

    String propKeyRawTemplatePath = "OutputXmlDoc.rawTemplatePath";
    String rawTemplateFilePath = "";
    try {
        propKeyRawTemplatePath = "OutputXmlDoc.rawTemplatePath";
        rawTemplateFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKeyRawTemplatePath);
        PropertyHandler.getInstance().addPropertiesFromFile("./src/main/resources/configFiles/"+scraperName+".properties", "mubz");
        String scraperId = PropertyHandler.getInstance().getValue(scraperName+".Scraper.id");
        String mappingRulesFilePath = PropertyHandler.getInstance().getValue(scraperName+".mappingRulesFilePath");
        String mappingRulesType = PropertyHandler.getInstance().getValue(scraperName+".mappingRulesType");
        MappingRules mappingRules = MappingRules.getInstance();
        mappingRules.updateMappingRules(mappingRulesFilePath, scraperId, mappingRulesType);
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Probably config file '" + configFilePath + "' or property keys '" + propKeyRawTemplatePath + "' not found!\n" + "Error: " + ex.getMessage());
    }
    //prepare parameters for the
    HashMap<String, Object> insertInfo = MapListUtils.createObjMap(insertInfoList);
    if(additionalInfoList != null && additionalInfoList.size() > 0) {
        insertInfo.put("additionalInfo", additionalInfoList);
    }

    // Generate an empty document with a given template file
    MumdagModel mm;
    OutputXmlDoc oxd = null;
    try {
        mm = new MumdagModel();
        oxd = mm.getMmdgModel();
        oxd.createOutputXmlDoc();
        oxd.overwriteTemplateXmlDoc(rawTemplateFilePath);
        if(calledMethod.equals("writeArtistName")) {
            mm.writeArtistName(insertInfo, copyRule, scraperName);
        }
        else {
            mm.writeArtistAlias(insertInfo, copyRule, scraperName);
        }
        oxd.writeOutputDocToFile(artistPath, "writeArtistNames_ok-" + testDesc.substring(0, 2) + "-out.xml");
    } catch (Exception ex) {
        log.error("{}... failed", testDesc);
        fail("No exception expected. Probably config file '" + configFilePath + "' or property keys '" + propKeyRawTemplatePath + "' not found!\n" + "Error: " + ex.getMessage());
    }

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
                    fail("No exception expected while comparing the xml strings.\n" + "Error: " + ex.getMessage());
                }
                assertThat(xmlString).isXmlEqualTo(expXmlString);
                break;
            default:
                fail("Xpath validation type '" + valType + "' unknown. Expecting 'attr', 'node' or 'xml'");
        }
    }
    log.info("{} ... finished successfully!", testDesc);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:	nok
@DataProvider
public Object[][] data_writeArtistNames_ok() {
    return new Object[][]{
        new Object[]{"01 - writeArtistName - group, dissolved, alias with special chars - The Beatles",
                        "writeArtistName",
                        "./src/test/resources/MumdagModelTest/writeArtistNames/writeArtistNames_ok-01.properties",
                        Arrays.asList("name::The Beatles", "sortName::Beatles, The", "nameType::Name", "unid::b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"),
                        null,
                        "ArtistName",
                        "MusicBrainz",
                        "./src/test/resources/MumdagModelTest/writeArtistNames/",
                        Arrays.asList("|xml||./src/test/resources/MumdagModelTest/writeArtistNames/writeArtistNames_ok-01-exp.xml")},
        new Object[]{"11 - writeArtistAlias - group, dissolved, alias with special chars - The Beatles",
                        "writeArtistAlias",
                        "./src/test/resources/MumdagModelTest/writeArtistNames/writeArtistNames_ok-11.properties",
                        Arrays.asList("name::fab four", "sortName::fab four", "nameType::Alias", "unid::b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"),
                        Arrays.asList("type=Search hint", "type-id=1937e404-b981-3cb7-8151-4c86ebfc8d8e"),
                        "ArtistAliasName",
                        "MusicBrainz",
                        "./src/test/resources/MumdagModelTest/writeArtistNames/",
                        Arrays.asList("|xml||./src/test/resources/MumdagModelTest/writeArtistNames/writeArtistNames_ok-11-exp.xml")},
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