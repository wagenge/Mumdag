package org.mumdag.core;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import java.lang.reflect.Field;

import org.mumdag.utils.PropertyHandler;

//-----------------------------------------------------------------------------

public class ExecutionRulesTests {

private static Logger log = null;
	
//=============================================================================	
/*
 * 	TEST METHODS (public)
 */
/*
//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_complexTest_ok")
public void test_complexTest_ok(String testDesc, String confFile) {
    log.info("{} ... started", testDesc);
    // Reading the local music files and building the index
    try {
        String startPath = PropertyHandler.getInstance(confFile).getValue("LocalMusicIndexer.startPath");
        log.info("Building artist index started ...  start reading at {}", startPath);
        LocalMusicIndexer lmi = new LocalMusicIndexer(startPath);
        lmi.buildIndex();
    } catch (Exception e) {
        e.printStackTrace();
    }
    MusicIndex mi = MusicIndex.getInstance();
    log.info("Building music index finished ... {} artists and {} tracks found", mi.getNumOfArtists(), mi.getNumOfTracks());

    // Reading the execution rules
    String executionRulesFilePath;
    ExecutionRules er;
    try {
        executionRulesFilePath = PropertyHandler.getInstance(confFile).getValue("ExecutionRules.rulesFileName");
        er = ExecutionRules.getInstance(executionRulesFilePath);
        log.info("Loading ExecutionRules, {} rule sets found", er.getNumberOfRuleSets());
    } catch (Exception ex) {
        log.error("{}: {} ... Mumdag exited",  ex.getClass().getSimpleName(), ex.getMessage());
        ex.printStackTrace();
        return;
    }

    TreeMap<Long, HashMap<String, Object>> fmi =  mi.getFlatMusicIndex();
    int i = 1;
    for (Map.Entry<Long, HashMap<String, Object>> entry : fmi.entrySet()) {
        Long entryKey = entry.getKey();
        HashMap<String, Object> entryValue = entry.getValue();
        mi.setCurrentEntryId(entryKey);
        log.info("#{}: {} ({}) => {} ... start executing rules", i, String.valueOf(entryKey), entryValue.get("type"));
        i++;
        er.executeRules(entryValue);
    }
    log.info("{} ... finished successfully!", testDesc);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:	nok
@DataProvider
public Object[][] data_complexTest_ok() {
    return new Object[][] {
            new Object[] {
                    "01 - ...",
                    "./src/test/resources/ExecutionRulesTest/complex/complex-ok-1.properties"},
    };
}
*/
//-----------------------------------------------------------------------------

/*
//DOC:			nok  
//ASSERTION:	ok
@Test(priority=1)
public void UnitTest_getInstance_ok() {
	String configFilePath = "./src/test/resources/ExecutionRulesTests/getInstance/config_getInstance_ok.properties";
	String propKey = "ExecutionRules.rulesFileName";
	ExecutionRules er = null;
	try {
		String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
		er = ExecutionRules.getInstance(executionRulesFilePath);
	} catch (Exception ex) {
		fail("No exception expected. Probably config file '" + configFilePath + "' or property key '" + propKey + "' not found!\n" + "Error: " + ex.getMessage());
	}
	
	assertThat(er.getNumberOfRuleSets()).isEqualTo(2);
	assertThat(er.getNumberOfActions()).isEqualTo(11);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test(priority=2)
public void UnitTest_getInstanceWithoutParams_ok() {
	ExecutionRules er = null;
	try {
		er = ExecutionRules.getInstance();
	} catch (Exception ex) {
		fail("No exception expected. Probably config file './src/main/resources/config.properties' or property key 'ExecutionRules.rulesFileName' not found!\n" + "Error: " + ex.getMessage());
	}
	
	assertThat(er.getNumberOfRuleSets()).isGreaterThan(1);
	assertThat(er.getNumberOfActions()).isGreaterThan(4);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test (priority=3)
public void UnitTest_getInstance_nok_FileNotFound() {
	String configFilePath = "./src/test/resources/ExecutionRulesTests/getInstance/config_getInstance_nok_FileNotFound.properties";
	String propKey = "ExecutionRules.rulesFileName";
	assertThatThrownBy(() -> { 
		String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
		ExecutionRules er = ExecutionRules.getInstance(executionRulesFilePath);
		System.out.println("# of rules: " + er.getNumberOfRuleSets());
		}).isInstanceOf(FileNotFoundException.class);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test (priority=4)
public void UnitTest_getInstance_nok_KeyNotFound() {
	String configFilePath = "./src/test/resources/ExecutionRulesTests/getInstance/config_getInstance_nok_KeyNotFound.properties";
	String propKey = "ExecutionRules.rulesFileName";
	assertThatThrownBy(() -> { 
		String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
		ExecutionRules er = ExecutionRules.getInstance(executionRulesFilePath);
		System.out.println("# of rules: " + er.getNumberOfRuleSets());
		}).isInstanceOf(InvalidParameterException.class).hasStackTraceContaining("Missing value for key ExecutionRules.rulesFileName!");
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test (priority=11, dataProvider = "configFiles_createRuleSetXml_nok")
public void UnitTest_createRuleSetXml_nok(String configFilePath) {
	String propKey = "ExecutionRules.rulesFileName";
	assertThatThrownBy(() -> { 
		String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
		ExecutionRules er = ExecutionRules.getInstance(executionRulesFilePath);
		System.out.println("# of rules: " + er.getNumberOfRuleSets());
		}).isInstanceOf(SAXParseException.class);
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test (priority=12, dataProvider = "configFiles_createRuleSetMap_nok_WrongTags")
public void UnitTest_createRuleSetMap_nok_WrongTags(String configFilePath, int expNumOfRules, int expNumOfActions, boolean isAssertion) {
	String propKey = "ExecutionRules.rulesFileName";
	if(isAssertion) {
		assertThatThrownBy(() -> { 
		String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
		ExecutionRules er = ExecutionRules.getInstance(executionRulesFilePath);
		System.out.println("# of rules: " + er.getNumberOfRuleSets());
		assertThat(er.getNumberOfRuleSets()).isEqualTo(expNumOfRules);
		assertThat(er.getNumberOfActions()).isEqualTo(expNumOfActions);
		}).isInstanceOf(Exception.class);
	}
	else {
		ExecutionRules er = null;
		try {
			String executionRulesFilePath = PropertyHandler.getInstance(configFilePath).getValue(propKey);
			er = ExecutionRules.getInstance(executionRulesFilePath);
		} catch (Exception ex) {
			fail("No exception expected. Probably config file '" + configFilePath + "' or property key '" + propKey + "' not found!\n" + "Error: " + ex.getMessage());
		}
		
		assertThat(er.getNumberOfRuleSets()).isEqualTo(expNumOfRules);
		assertThat(er.getNumberOfActions()).isEqualTo(expNumOfActions);
	}
}
*/
//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
@Test (priority=21, dataProvider = "configFiles_executeRules_ok")
public void UnitTest_executeRules_ok(String configFilePath, String validationString) {
	// Generate an empty document with a given template file
	String templateFilePath = "";
	OutputXmlDoc oxd = null;
	try {
		templateFilePath = PropertyHandler.getInstance(configFilePath).getValue("OutputXmlDoc.templatePath");
		oxd = new OutputXmlDoc(templateFilePath);
		oxd.createOutputXmlDoc();
	} catch (Exception ex) {
		ex.printStackTrace();
		fail("No exception expected. Probably template file '" + templateFilePath + "' or property key '" + "OutputXmlDoc.templatePath" + "' not found!\n" + "Error: " + ex.getMessage());;
	}

	
	// Reading the execution rules
	String executionRulesFilePath = "";
	ExecutionRules er = null;
	try {
		executionRulesFilePath = PropertyHandler.getInstance().getValue("ExecutionRules.rulesFileName");
		er = ExecutionRules.getInstance(executionRulesFilePath);
	} catch (Exception ex) {
		fail("No exception expected. Probably rules file '" + executionRulesFilePath + "' or property key '" + "ExecutionRules.rulesFileName" + "' not found!\n" + "Error: " + ex.getMessage());
	}
	//execute rules
    try {
		oxd = er.executeRules("name", ".", oxd);
	} catch (Exception ex) {
		fail("No exception expected. Probably wrong methods in rules file '" + executionRulesFilePath + "'\n" + "Error: " + ex.getMessage());
	}
    
    //extraction the validation information from data provider
    String[] validationList = validationString.split("¦");
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
    	else if (valType.equals("ret")) {
    		String varValue = (String)er.getVar(valAttrName);
    	    assertThat(varValue).isEqualTo(expValue);
    	}
    	else {
    		fail("Xpath validation type '" + valType + "' unknow. Expecting 'attr', 'node' or 'ret'");
    	}
    }
}
*/

//-----------------------------------------------------------------------------
/*
//DOC:			nok  
//ASSERTION:	ok
//TESTS:		ok
@Test (priority=22, dataProvider = "configFiles_executeRules_nok")
public void UnitTest_executeRules_nok(String configFilePath, String validationString) {
	String[] valParamList = validationString.split("\\|");
	if(valParamList.length != 2) {
		fail("Validation String '" + validationString + "' expected to have two parts separated by an '|'");
	}
	
	assertThatExceptionOfType(Exception.class).isThrownBy(() -> { 
		// Generate an empty document with a given template file
		String templateFilePath = "";
		OutputXmlDoc oxd = null;
		try {
			templateFilePath = PropertyHandler.getInstance(configFilePath).getValue("OutputXmlDoc.templatePath");
			oxd = new OutputXmlDoc(templateFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("No exception expected. Probably template file '" + templateFilePath + "' or property key '" + "OutputXmlDoc.templatePath" + "' not found!\n" + "Error: " + ex.getMessage());;
		}
		oxd.createOutputXmlDoc();
		
		// Reading the execution rules
		String executionRulesFilePath = "";
		ExecutionRules er = null;
		try {
			executionRulesFilePath = PropertyHandler.getInstance().getValue("ExecutionRules.rulesFileName");
			er = ExecutionRules.getInstance(executionRulesFilePath);
		} catch (Exception ex) {
			fail("No exception expected. Probably rules file '" + executionRulesFilePath + "' or property key '" + "ExecutionRules.rulesFileName" + "' not found!\n" + "Error: " + ex.getMessage());
		}
		//execute rules
		oxd = er.executeRules("name", ".", oxd);
	}).withStackTraceContaining(valParamList[0]).withStackTraceContaining(valParamList[1]);
}
*/


//-----------------------------------------------------------------------------
/*
//DOC:	nok
@DataProvider
public Object[][] configFiles_createRuleSetXml_nok() {
    return new Object[][] {
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetXml/config_createRuleSetXml_nok_CorruptXml-1.properties"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetXml/config_createRuleSetXml_nok_CorruptXml-2.properties"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetXml/config_createRuleSetXml_nok_CorruptXml-3.properties"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetXml/config_createRuleSetXml_nok_CorruptXml-4.properties"}
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
//TESTS: missing some Tests (noActionNo, empty Method, Missing ReturnVal and ParamList)
@DataProvider
public Object[][] configFiles_createRuleSetMap_nok_WrongTags() {
    return new Object[][] {
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-MappingRules.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-RuleSet.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-RuleSetMissed.properties", 1, 1, false},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-Rule.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-RuleMissed.properties", 2, 6, false},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-Action.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-ActionMissed.properties", 2, 6, false},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-NoRuleSetName.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-NoRuleNo.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-NoActionNo.properties", 0, 0, true},
            new Object[] {"./src/test/resources/ExecutionRulesTests/createRuleSetMap/config_createRuleSetMap_nok_WrongTag-NoMethodName.properties", 0, 0, true}
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] configFiles_executeRules_ok() {
    return new Object[][] {
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-noReturn-1.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-noReturn-2.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-noReturn-3.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-noReturn-4.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-singleParam-noReturn-1.properties",
                    "/Artist/ArtistName|attr|name|TestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-singleParam-noReturn-2.properties",
                    "/Artist/ArtistName|attr|name|TestArtist"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-withReturn-1.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist¦|ret|$SortName$|TestTestArtist, The"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-noParam-withReturn-2.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestTestArtist¦|ret|$SName$|TestTestArtist, The"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-paramList-noReturn-1.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestArtist¦/Artist/ArtistName/SortName/text()|node||TestArtist, The"} ,
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-paramList-noReturn-2.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestArtist¦/Artist/ArtistName/SortName/text()|node||TestArtist, The"} ,
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-singleParam-withReturn-1.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestArtist¦|ret|$SortName$|TestArtist, The"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-oneAction-singleParam-withReturn-2.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestArtist¦|ret|$SortName$|TestArtist, The"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-twoActions-singleParam-withReturn-paramTransfer-1.properties",
                    "/Artist/ArtistName/Name/text()|node||The TestArtist¦/Artist/ArtistName/SortName/text()|node||TestArtist, The¦|ret|$SortName$|TestArtist, The"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_ok-twoActions-singleParam-withReturn-paramTransfer-2.properties",
                    "/Artist/ArtistName/Name/text()|node||The Testartist¦/Artist/ArtistName/SortName/text()|node||Testartist, The¦|ret|$SortName$|Testartist, The"},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] configFiles_executeRules_nok() {
    return new Object[][] {
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-NoSuchMethodException-1.properties",
                    "NoSuchMethodException|getDeclaredMethod"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-NoSuchMethodException-2.properties",
                    "NoSuchMethodException|getDeclaredMethod"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-NoSuchMethodException-3.properties",
                    "NoSuchMethodException|getDeclaredConstructor"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-ClassNotFoundException-1.properties",
                    "ClassNotFoundException|org.mumdag.testscraper.OtherTestScraper"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-ClassNotFoundException-2.properties",
                    "ClassNotFoundException|org.mumdag.otherhelper.ErTestScraper"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-InstantiationException-1.properties",
                    "InstantiationException|Constructor.newInstance"},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-IllegalAccessException-1.properties",
                    "IllegalAccessException|Class org.mumdag.core.ExecutionRules can not access a member of class org.mumdag.testscraper.ErTestScraperWithPrivateMethod with modifiers \"private\""},
            new Object[] {"./src/test/resources/ExecutionRulesTests/executeRules/executeRules_nok-InvocationTargetException-1.properties",
                    "InvocationTargetException|Caused by: java.lang.NullPointerException"},
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
    log = LogManager.getLogger(ExecutionRulesTests.class);
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
public void destroyExecutionRulesInstanceBefore() throws Exception {
	Field instance = ExecutionRules.class.getDeclaredField("instance");
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
public void destroyExecutionRulesInstanceAfter() throws Exception {
	Field instance = ExecutionRules.class.getDeclaredField("instance");
	instance.setAccessible(true);
	instance.set(null, null);
}

//-----------------------------------------------------------------------------

}