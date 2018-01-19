package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;  // main one

import java.lang.reflect.Field;
import java.util.HashMap;

//-----------------------------------------------------------------------------

public class PropertyHandlerTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS (public)
 */

//DOC:	nok
@Test
public void test_getInstance_withoutParam_ok() {
    log.info("... started");
    String templatePath = "";
    try {
        templatePath = PropertyHandler.getInstance().getValue("OutputXmlDoc.templatePath");
    } catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Probably default config file not found!\n" + "Error: " + ex.getMessage());
    }
    assertThat(templatePath).isEqualTo("./src/main/resources/templates/artist-full.xml");
    log.info("... finished successfully!");
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test (dataProvider = "data_getInstance_withParam_ok")
public void test_getInstance_withParam_ok(String testDesc, String confPath, String propId, String propKey, String expRes) {
    log.info("{} ... started", testDesc);
    String templatePath = "";
    try {
        if(StringUtils.isEmpty(propId)) {
            templatePath = PropertyHandler.getInstance(confPath).getValue(propKey);
        }
        else {
            templatePath = PropertyHandler.getInstance(confPath, propId).getValue(propKey);
        }
    } catch (Exception ex) {
        log.error("{} ... failed", testDesc);
        fail("No exception expected. Probably config file '" + confPath + "' not found!\n" + "Error: " + ex.getMessage());
    }
    assertThat(templatePath).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getInstance_withParam_ok() {
    return new Object[][] {
        new Object[] {"01 - without propId",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties",
                        "",
                        "PropertyHandlerTests.testKey",
                        "testValue"},
        new Object[] {"02 - with propId",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties",
                        "conf1",
                        "PropertyHandlerTests.testKey",
                        "testValue"},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test (dataProvider = "data_getInstance_withParam_nok")
public void test_getInstance_withParam_nok(String testDesc, String confPath, String propKey, String expResStr) {
    log.info("{} ... started", testDesc);
    String[] expResList = expResStr.split("\\|\\|");
    if(expResList.length != 2) {
        log.error("{} ... failed", testDesc);
        fail("Validation String '" + expResStr + "' expected to have two parts separated by an '|'");
    }
    assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
        PropertyHandler.getInstance(confPath).getValue(propKey);
    }).withStackTraceContaining(expResList[0]).withStackTraceContaining(expResList[1]);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getInstance_withParam_nok() {
    return new Object[][] {
        new Object[] {"01 - FileNotFoundException",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config0.properties",
                        "PropertyHandlerTests.testKey",
                        "java.io.FileNotFoundException||Das System kann die angegebene Datei nicht finden"},
        new Object[] {"02 - KeyNotFoundException",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties",
                        "PropertyHandlerTests.wrongTestKey",
                        "java.security.InvalidParameterException||Missing value for key PropertyHandlerTests.wrongTestKey!"},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test
public void test_getAllValues_ok() {
    log.info("... started");
    HashMap<String, String> res = new HashMap<>();
    try {
        res = PropertyHandler.getInstance("./src/test/resources/PropertyHandlerTest/getAllValues/config4.properties").getAllValues();
    } catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Probably default config file not found!\n" + "Error: " + ex.getMessage());
    }
    assertThat(res.toString()).isEqualTo("{PropertyHandlerTests3.testKey3=testValue3,"+
            " PropertyHandlerTests.testKey=testValue,"+
            " PropertyHandlerTests2.testKey2=testValue2}");
    log.info("... finished successfully!");
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test (dataProvider = "data_containsKey_ok")
public void test_containsKey_ok(String testDesc, String confPath, String propKey, Boolean expRes) {
    log.info("{} ... started", testDesc);
    Boolean res = false;
    try {
        res = PropertyHandler.getInstance(confPath).containsKey(propKey);
    } catch (Exception ex) {
        log.error("{} ... failed", testDesc);
        fail("No exception expected. Probably config file '" + confPath + "' not found!\n" + "Error: " + ex.getMessage());
    }
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_containsKey_ok() {
    return new Object[][] {
        new Object[] {"01 - Key found",
                    "./src/test/resources/PropertyHandlerTest/containsKey/config1.properties",
                    "PropertyHandlerTests.testKey",
                    true},
        new Object[] {"02 - Key not found",
                        "./src/test/resources/PropertyHandlerTest/containsKey/config1.properties",
                        "PropertyHandlerTests.wrongTestKey",
                        false},
        new Object[] {"03 - Key null",
                        "./src/test/resources/PropertyHandlerTest/containsKey/config1.properties",
                        null,
                        false},
        new Object[] {"04 - Key empty",
                        "./src/test/resources/PropertyHandlerTest/containsKey/config1.properties",
                        "",
                        false},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test (dataProvider = "data_addPropertiesFromFile_ok")
public void test_addPropertiesFromFile_ok(String testDesc, String confPathStr, String propKeyStr, String propIdStr, String expResStr) {
    log.info("{} ... started", testDesc);
    String[] confPaths = confPathStr.split("\\|\\|");
    String[] propKeys = propKeyStr.split("\\|\\|");
    if(propKeys.length != 2) {
        log.error("... failed");
        fail("Property Keys String '" + propKeyStr + "' expected to have two parts separated by an '||'");
    }
    String[] propIds =  propIdStr.split("\\|\\|");
    String[] expRes =  expResStr.split("\\|\\|");
    if(expRes.length != 2) {
        log.error("... failed");
        fail("Expected Result String '" + expResStr + "' expected to have two parts separated by an '||'");
    }
    String propValue1 = "";
    String propValue2 = "";
    try {
        propValue1 = PropertyHandler.getInstance(confPaths[0]).getValue(propKeys[0]);
        for(int i = 1; i < confPaths.length; i++) {
            PropertyHandler.getInstance().addPropertiesFromFile(confPaths[i], propIds[i-1]);
        }
        propValue2 = PropertyHandler.getInstance().getValue(propKeys[1]);
    } catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Probably config files '" + StringUtils.join(confPaths, "' or '") + "' not found!\n" + "Error: " + ex.getMessage());
    }
    assertThat(propValue1).isEqualTo(expRes[0]);
    assertThat(propValue2).isEqualTo(expRes[1]);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_addPropertiesFromFile_ok() {
    return new Object[][] {
        new Object[] {"01 - ok",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties||" +
                                "./src/test/resources/PropertyHandlerTest/getInstance/config2.properties",
                        "PropertyHandlerTests.testKey||PropertyHandlerTests2.testKey2",
                        "config2",
                        "testValue||testValue2"},
        new Object[] {"02 - overwrite",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties||" +
                                "./src/test/resources/PropertyHandlerTest/getInstance/config2.properties||" +
                                "./src/test/resources/PropertyHandlerTest/getInstance/config3.properties",
                        "PropertyHandlerTests.testKey||PropertyHandlerTests2.testKey2",
                        "config2||config3",
                        "testValue||testValue3"},
        new Object[] {"03 - duplicate",
                        "./src/test/resources/PropertyHandlerTest/getInstance/config1.properties||" +
                                "./src/test/resources/PropertyHandlerTest/getInstance/config2.properties||" +
                                "./src/test/resources/PropertyHandlerTest/getInstance/config3.properties",
                        "PropertyHandlerTests.testKey||PropertyHandlerTests2.testKey2",
                        "config2||config2",
                        "testValue||testValue2"},
    };
}


//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(PropertyHandlerTest.class);
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