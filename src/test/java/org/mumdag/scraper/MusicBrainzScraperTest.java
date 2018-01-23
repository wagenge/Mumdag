package org.mumdag.scraper;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.Mumdag;
import org.mumdag.model.MumdagModel;
import org.mumdag.model.MumdagModelTest;
import org.testng.Assert;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
/*
//DOC:	nok  
@Test(dataProvider = "Data_writeArtistInfo_ok")
public void UnitTest_writeArtistInfo_ok(String logStr, String configFilePath, String sourceXmlFile, String artistName, String artistPath, String validationString) {
	String propKeyTemplatePath = "OutputXmlDoc.templatePath";
	String propKeyRawTemplatePath = "OutputXmlDoc.rawTemplatePath";
	
	// Generate an empty document with a given template file
	String templateFilePath = "";
	String rawTemplateFilePath = "";
	OutputXmlDoc oxd = null;
	try {
		templateFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKeyTemplatePath);
		oxd = new OutputXmlDoc(templateFilePath);
		oxd.createOutputXmlDoc();
		rawTemplateFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKeyRawTemplatePath);
		oxd.overwriteTemplateXmlDoc(rawTemplateFilePath);
	} catch (Exception ex) {
		fail("No exception expected. Probably config file '" + configFilePath + "' or property keys '" + propKeyTemplatePath + "', '" + propKeyRawTemplatePath + "' not found!\n" + "Error: " + ex.getMessage());
	}
	
	// read the xml file and stores it in an object array
	String artistInfoXml = "";
	try {
		artistInfoXml = new String(Files.readAllBytes(Paths.get(sourceXmlFile)));
	} catch (Exception ex) {
		fail("No exception expected. Probably the xml file '" + sourceXmlFile + "' is missing!\n" + "Error: " + ex.getMessage());
	}
	Object[] artistInfoObj = new Object[1];
	artistInfoObj[0] = artistInfoXml;
	
	
	// Generate an MB scraper object and execute the writeArtistInfo method
	MusicBrainzScraper mbs = null; 
	try {
		mbs = new MusicBrainzScraper(artistName, artistPath, oxd);
		mbs.writeMusicBrainzArtistInfo(artistInfoObj);
		oxd = mbs.getOxd();
		oxd.writeOutputDocToFile(artistPath, "writeArtistInfo_ok-"+logStr.substring(0, 2)+"-out.xml");
	} catch (Exception ex) {
		fail("No exception expected. Probably one or more property where missing!\n" + "Error: " + ex.getMessage());
	}
	
	//extraction the validation information from data provider
	String[] validationList = validationString.split("�");
	for(int i = 0; i < validationList.length; i++) {
		String valString = validationList[i];
		String[] valParamList = valString.split("\\|");
		String valXpath = valParamList[0];
		String valType = valParamList[1];
		String valAttrName = valParamList[2];
		String expValue = valParamList[3];

		//validate
		if(valType.equals("attr")) {
			String attrValue = oxd.getNodeAttributeTextByXPath(valXpath, valAttrName);
			assertThat(attrValue).isEqualTo(expValue);
		}
		else if (valType.equals("node")) {
			String nodeValue = oxd.getNodeTextByXPath(valXpath);
			assertThat(nodeValue).isEqualTo(expValue);
		}
		else if (valType.equals("xml")) {
			String xmlString = "";
			String expXmlString = "";
			try {
				xmlString = oxd.getOutputDocAsString();
				expXmlString = FileUtils.readFileToString(new File(expValue), "UTF-8");
			} catch (Exception ex) {
				fail("No exception expected while comparing the xml strings.\n" + "Error: " + ex.getMessage());
			}
			assertThat(xmlString).isXmlEqualTo(expXmlString);
		}		
		else {
			fail("Xpath validation type '" + valType + "' unknow. Expecting 'attr', 'node' or 'xml'");
		}
	}
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] Data_writeArtistInfo_ok() {
	return new Object[][] {
		new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-01.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Beatles, The - Artist.xml",
						"The Beatles", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-01-exp.xml"},
		new Object[] {"02 - group, active, alias without type attr - Animal Collective",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-02.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Animal Collective - Artist.xml",
						"Animal Collective", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-02-exp.xml"},
		new Object[] {"03 - female, alive, alias with type attr - Madonna",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-03.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Madonna - Artist.xml",
						"Madonna", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-03-exp.xml"},
		new Object[] {"04 - female, dead - Janis Joplin",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-04.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Joplin, Janis - Artist.xml",
						"Janis Joplin", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-04-exp.xml"},
		new Object[] {"05 - male, dead, disambiguation, multiple discogs urls - Prince",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-05.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Prince - Artist.xml",
						"Prince", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-05-exp.xml"},
		new Object[] {"06 - band, ended, disambiguation, multiple members at different times - Madness",
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-06.properties", 
						"./src/test/resources/MusicBrainzScraperTests/_mbInputs/Madness - Artist.xml",
						"Madness", 
						"./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/",
						"|xml||./src/test/resources/MusicBrainzScraperTests/WriteArtistInfo/writeArtistInfo_ok-06-exp.xml"},
	};
}
*/
//-----------------------------------------------------------------------------

//DOC:	nok  
@Test(dataProvider = "data_writeArtistName_ok")
public void test_writeArtistName_ok(String testDesc, String configFilePath, String sourceXmlFile, String artistName, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);

    //prepare test
    // - generate an empty document with a given template file
    // - read the xml file and stores it in an object array
    HashMap<String, Object> prepTestmap = prepareTest(testDesc, configFilePath, sourceXmlFile);
    MumdagModel mm = (MumdagModel)prepTestmap.get("model");
    Object[] inputObj = (Object[])prepTestmap.get("input");

    //execute test
    executeTest(testDesc, artistName, artistPath, "writeArtistName", mm, inputObj);

    //validate test results
    validateTest(testDesc, mm, expRes);

    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistName_ok() {
    return new Object[][]{
        new Object[]{"01 - group, dissolved, alias with special chars - The Beatles",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01.properties",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-inp.xml",
                        "Beatles, The",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-01-exp.xml")},
		new Object[] {"02 - group, not dissolved - Animal Collective",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02.properties",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02-inp.xml",
						"Animal Collective",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-02-exp.xml")},
		new Object[] {"03 - group, not dissolved - Animal Collective - name already existing -> update",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-03.properties",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-03-inp.xml",
						"Animal Collective",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-03-exp.xml")},
		new Object[] {"04 - group, not dissolved - Animal Collective - alias name existing -> add",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-04.properties",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-04-inp.xml",
						"Animal Collective",
						"./src/test/resources/MusicBrainzScraperTest/writeArtistName/",
                        Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistName/writeArtistName_ok-04-exp.xml")},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistAlias_ok")
public void test_writeArtistAlias_ok(String testDesc, String configFilePath, String sourceXmlFile, String artistName, String artistPath, List<String> expRes) {
    log.info("{} ... started", testDesc);

    //prepare test
    // - generate an empty document with a given template file
    // - read the xml file and stores it in an object array
    HashMap<String, Object> prepTestmap = prepareTest(testDesc, configFilePath, sourceXmlFile);
    MumdagModel mm = (MumdagModel)prepTestmap.get("model");
    Object[] inputObj = (Object[])prepTestmap.get("input");

    //execute the test
    executeTest(testDesc, artistName, artistPath, "writeArtistAlias", mm, inputObj);

    //validate test results
    validateTest(testDesc, mm, expRes);

    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistAlias_ok() {
	return new Object[][] {
        new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01.properties",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-inp.xml",
                        "Beatles, The",
                        "./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/",
                        Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistAlias/writeArtistAlias_ok-01-exp.xml")},

	};
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_writeArtistTypeAndGender_ok")
public void test_writeArtistTypeAndGender_ok(String testDesc, String configFilePath, String sourceXmlFile, String artistName, String artistPath, List<String> expRes) {
	log.info("{} ... started", testDesc);

    //prepare test
    // - generate an empty document with a given template file
    // - read the xml file and stores it in an object array
    HashMap<String, Object> prepTestmap = prepareTest(testDesc, configFilePath, sourceXmlFile);
    MumdagModel mm = (MumdagModel)prepTestmap.get("model");
    Object[] inputObj = (Object[])prepTestmap.get("input");

    //execute the test
    executeTest(testDesc, artistName, artistPath, "writeArtistTypeAndGender", mm, inputObj);

    //validate test results
    validateTest(testDesc, mm, expRes);

	log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_writeArtistTypeAndGender_ok() {
	return new Object[][] {
			new Object[] {"01 - group, dissolved, alias with special chars - The Beatles",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01.properties",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-inp.xml",
					"Beatles, The",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-01-exp.xml")},
			new Object[] {"02 - person, female, dead - Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-02.properties",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-02-inp.xml",
					"Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-02-exp.xml")},
			new Object[] {"03 - person, female, dead, type and gender existing - Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-03.properties",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-03-inp.xml",
					"Joplin, Janis",
					"./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/",
					Arrays.asList("|xml||./src/test/resources/MusicBrainzScraperTest/writeArtistTypeAndGender/writeArtistTypeAndGender_ok-03-exp.xml")},
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


//=============================================================================
/*
 * 	METHODS FOR TEST PREPARATION AND EXECUTION (private)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private HashMap<String, Object> prepareTest(String testDesc, String configFilePath, String sourceXmlFile) {
	HashMap<String, Object> retMap = new HashMap<>();
    String propKeyRawTemplatePath = "OutputXmlDoc.rawTemplatePath";
	String rawTemplateFilePath = "";
	try {
		rawTemplateFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKeyRawTemplatePath);
	} catch (Exception ex) {
		log.error("{}... failed", testDesc);
		fail("No exception expected. Probably config file '" + configFilePath + "' or property key '" + propKeyRawTemplatePath + "' not found!\n" + "Error: " + ex.getMessage());
	}

	// Generate an empty document with a given template file
	MumdagModel mm = null;
	OutputXmlDoc oxd = null;
	try {
		mm = new MumdagModel();
		oxd = mm.getMmdgModel();
		oxd.overwriteTemplateXmlDoc(rawTemplateFilePath);
	} catch (Exception ex) {
		log.error("{}... failed", testDesc);
		fail("No exception expected. Problems instantiating MumdagModel and/or OutputXmlDoc!\n" + "Error: " + ex.getMessage());
	}
	retMap.put("model", mm);

	// read the xml file and stores it in an object array
	String infoXml = "";
	try {
		infoXml = new String(Files.readAllBytes(Paths.get(sourceXmlFile)));
	} catch (Exception ex) {
		log.error("{}... failed", testDesc);
		fail("No exception expected. Probably the xml file '" + sourceXmlFile + "' is missing!\n" + "Error: " + ex.getMessage());
	}
	Object[] infoObj = new Object[1];
	infoObj[0] = infoXml;
    retMap.put("input", infoObj);

	return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private void executeTest(String testDesc, String artistName, String artistPath, String calledMethod, MumdagModel mm, Object[] inputObj) {
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
            default:
                fail("Test for method '" + calledMethod + "' not yet implemented!");
        }
        mm.writeOutputDocToFile(artistPath, calledMethod + "_ok-" + testDesc.substring(0, 2) + "-out.xml");
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
