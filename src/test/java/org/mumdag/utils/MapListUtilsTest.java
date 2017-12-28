package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class MapListUtilsTest {

private static final Logger log = LogManager.getLogger(MapListUtilsTest.class);

//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */

//-----------------------------------------------------------------------------

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

@Test
public void testCreateResolveXpathMap() throws Exception {
}

//-----------------------------------------------------------------------------

}