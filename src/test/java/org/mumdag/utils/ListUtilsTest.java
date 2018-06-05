package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class ListUtilsTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */

//DOC:	nok
@Test(dataProvider = "data_array2List_ok")
public void test_array2List_ok(String testDesc, String infoStr, String expRes) {
    log.info("{} ... started", testDesc);

    String [] infos = null;
    if(StringUtils.isNotEmpty(infoStr)) {
        infos = infoStr.split("\\|\\|");
    }

    List<String> resList = ListUtils.array2List(infos);
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_array2List_ok() {
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
        new Object[] {"06 - null value",
                        null,
                        ""},
    };
}

//-----------------------------------------------------------------------------

//DOC:	nok
@Test(dataProvider = "data_array2List_list_ok")
public void test_array2List_list_ok(String testDesc, String infoListStr, String infoStr, String expRes) {
    log.info("{} ... started", testDesc);

    String [] infoListArr;
    List<String> infoList = null;
    String [] infos = null;
    if(StringUtils.isNotEmpty(infoListStr)) {
        infoListArr = infoListStr.split("\\|\\|");
        infoList = Arrays.asList(infoListArr);
    }
    if(StringUtils.isNotEmpty(infoStr)) {
        infos = infoStr.split("\\|\\|");
    }

    List<String> resList = ListUtils.array2List(infoList, infos);
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_array2List_list_ok() {
    return new Object[][] {
        new Object[] {"01 - list, three new values",
                        "value1||value2||value3",
                        "value4||value5||value6",
                        "value1, value2, value3, value4, value5, value6"},
        new Object[] {"02 - empty list, three new values",
                        "",
                        "value4||value5||value6",
                        "value4, value5, value6"},
        new Object[] {"03 - null list, three new values",
                        null,
                        "value4||value5||value6",
                        "value4, value5, value6"},
        new Object[] {"04 - list, empty new values",
                        "value1||value2||value3",
                        "",
                        "value1, value2, value3"},
        new Object[] {"05 - list, null new values",
                        "value1||value2||value3",
                        null,
                        "value1, value2, value3"},
        new Object[] {"06 - empty list, empty new values",
                        "",
                        "",
                        ""},
        new Object[] {"07 - null list, null new values",
                        null,
                        null,
                        ""},
    };
}


//=============================================================================
/*
 * 	ANNOTATED METHODS (public)
 */

@BeforeClass
public static void setLogger() {
    System.setProperty("log4j.configurationFile","./src/test/resources/log4j2-testing.xml");
    log = LogManager.getLogger(ListUtilsTest.class);
}

//-----------------------------------------------------------------------------

}