package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class MapListUtilsTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */

//DOC:	nok
@Test(dataProvider = "data_createInfoList_ok")
public void test_createInfoList_ok(String testDesc, String infoStr, String expRes) {
    log.info("{} ... started", testDesc);
    String [] infos = infoStr.split("\\|\\|");
    if(StringUtils.isEmpty(infoStr)) {
        infos = null;
    }
    List<String> resList = MapListUtils.createInfoList(infos);
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_createInfoList_ok() {
    return new Object[][] {
        new Object[] {"01 - three values",
                        "value1||value2||value3",
                        "value1, value2, value3"},
        new Object[] {"02 - three values, first empty",
                        "||value2||value3",
                        ", value2, value3"},
        new Object[] {"03 - three values, second empty",
                        "value1||||value3",
                        "value1, , value3"},
        new Object[] {"04 - three values, all empty",
                        " |||| ",
                        " , ,  "},
        new Object[] {"05 - empty value",
                        "",
                        ""},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_createInfoList_list_ok")
public void test_createInfoList_list_ok(String testDesc, String infoListStr, String infoStr, String expRes) {
    log.info("{} ... started", testDesc);
    String [] infoListArr = infoListStr.split("\\|\\|");
    List<String> infoList = Arrays.asList(infoListArr);
    String [] infos = infoStr.split("\\|\\|");
    if(StringUtils.isEmpty(infoStr)) {
        infos = null;
    }
    if(StringUtils.isEmpty(infoListStr)) {
        infoList = null;
    }
    List<String> resList = MapListUtils.createInfoList(infoList, infos);
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_createInfoList_list_ok() {
    return new Object[][] {
        new Object[] {"01 - list, three new values",
                        "value1||value2||value3",
                        "value4||value5||value6",
                        "value1, value2, value3, value4, value5, value6"},
        new Object[] {"02 - empty list, three new values",
                        "",
                        "value4||value5||value6",
                        "value4, value5, value6"},
        new Object[] {"03 - list, empty new values",
                        "value1||value2||value3",
                        "",
                        "value1, value2, value3"},
        new Object[] {"04 - empty list, empty new values",
                        "",
                        "",
                        ""},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_createResolveXpathMap_ok")
public void test_createResolveXpathMap_ok(String testDesc, String resolveStr, String expRes) {
    log.info("{} ... started", testDesc);
    String [] infos = resolveStr.split("\\|\\|");
    if(StringUtils.isEmpty(resolveStr)) {
        infos = null;
    }
    HashMap<String, String> resMap = MapListUtils.createResolveXpathMap(infos);
    String res = resMap.toString();
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_createResolveXpathMap_ok() {
    return new Object[][] {
        new Object[] {"01 - three key-value-pairs",
                        "key1||value1||key2||value2||key3||value3",
                        "{key1=value1, key2=value2, key3=value3}"},
        new Object[] {"02 - three key-value-pairs, first value missing",
                        "key1||||key2||value2||key3||value3",
                        "{key1=, key2=value2, key3=value3}"},
        new Object[] {"03 - three key-value-pairs, all values missing",
                        "key1||||key2||||key3||",
                        "{key1=, key2=, key3=null}"},
    };
}


//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(MapListUtilsTest.class);
}

//-----------------------------------------------------------------------------

}