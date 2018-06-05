package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class MapUtilsTest {

private static Logger log = null;

//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */
//DOC:	nok
@Test(dataProvider = "data_array2Map_ok")
public void test_array2Map_ok(String testDesc, String resolveStr, String expRes) {
    log.info("{} ... started", testDesc);
    String [] infos = resolveStr.split("\\|\\|");
    if(StringUtils.isEmpty(resolveStr)) {
        infos = null;
    }
    HashMap<String, String> resMap = MapUtils.array2Map(infos);
    String res = resMap.toString();
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_array2Map_ok() {
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
    log = LogManager.getLogger(MapUtilsTest.class);
}

//-----------------------------------------------------------------------------

}