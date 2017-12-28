package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class XmlUtilsTest {

private static final Logger log = LogManager.getLogger(XmlUtilsTest.class);

//=============================================================================
/*
 * 	TEST METHODS INCLUDING DATAPROVIDER (public)
 */

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_resolveXpathString_varargs_ok")
public void test_resolveXpathString_varargs_ok(String testDesc, String xpath, String paramStr, String expRes) {
    log.info("{} ... started", testDesc);
    String [] params = paramStr.split("\\|\\|");
    String res = XmlUtils.resolveXpathString(xpath, params);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_resolveXpathString_varargs_ok() {
    return new Object[][] {
        new Object[] {"01 - xpath, zero params",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "",
                        "/my/xpath[_with_]/different[_parameters_]"},
        new Object[] {"02 - xpath, one param",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::@key='value'",
                        "/my/xpath[@key='value']/different[_parameters_]"},
        new Object[] {"03 - xpath, one param, two occasions",
                        "/my/xpath[_with_]/different[_with_]",
                        "_with_::@key='value'",
                        "/my/xpath[@key='value']/different[@key='value']"},
        new Object[] {"04 - xpath, one empty param",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::",
                        "/my/xpath/different[_parameters_]"},
        new Object[] {"05 - xpath, two params",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::@key='value'||_parameters_::@key2='value2'",
                        "/my/xpath[@key='value']/different[@key2='value2']"},
        new Object[] {"06 - xpath, two params, one is wrong",
                        "/my/xpath[_with2_]/different[_parameters_]",
                        "_with_::@key='value'||_parameters_::@key2='value2'",
                        "/my/xpath[_with2_]/different[@key2='value2']"},
        new Object[] {"07 - xpath, two params, one is wrong seperated",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_:@key='value'||_parameters_::@key2='value2'",
                        "/my/xpath[_with_]/different[@key2='value2']"},
        new Object[] {"08 - xpath, two params, one is wrong seperated",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::@key::'value'||_parameters_::@key2='value2'",
                        "/my/xpath[_with_]/different[@key2='value2']"},
        new Object[] {"09 - xpath, two params, first is empty",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "||_parameters_::@key2='value2'",
                        "/my/xpath[_with_]/different[@key2='value2']"},
        new Object[] {"10 - xpath, two params, second is empty",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::@key='value'||",
                        "/my/xpath[@key='value']/different[_parameters_]"},
        new Object[] {"11 - xpath, two params, both are empty",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "||",
                        "/my/xpath[_with_]/different[_parameters_]"},
        new Object[] {"12 - xpath, two params, first with empty value",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::||_parameters_::@key2='value2'",
                        "/my/xpath/different[@key2='value2']"},
        new Object[] {"13 - xpath, two params, second with empty value",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::@key='value'||_parameters_::",
                        "/my/xpath[@key='value']/different"},
        new Object[] {"14 - xpath, two params, both empty values",
                        "/my/xpath[_with_]/different[_parameters_]",
                        "_with_::||_parameters_::",
                        "/my/xpath/different"},
        new Object[] {"15 - xpath empty",
                        "",
                        "_with_:@key='value'||_parameters_::@key2='value2'",
                        ""},
        new Object[] {"16 - xpath null",
                        null,
                        "_with_:@key='value'||_parameters_::@key2='value2'",
                        null},
    };
}

//-----------------------------------------------------------------------------

//DOC:			nok
//ASSERTION:	ok
@Test(dataProvider = "data_resolveXpathString_map_ok")
public void test_resolveXpathString_map_ok(String testDesc, String xpath, String paramStr, String expRes) {
    log.info("{} ... started", testDesc);
    HashMap<String, String> resolveMap = new HashMap<>();
    String [] params = paramStr.split("\\|\\|");
    for (String param : params) {
        if (StringUtils.isNotEmpty(param)) {
            String[] parts = param.split("::");
            if (parts.length == 2) {
                resolveMap.put(parts[0], parts[1]);
            } else if (parts.length == 1) {
                resolveMap.put(parts[0], "");
            }
        }
    }
    String res = XmlUtils.resolveXpathString(xpath, resolveMap);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

@DataProvider
public Object[][] data_resolveXpathString_map_ok() {
    List<Object[]> result = Lists.newArrayList();
    result.addAll(Arrays.asList(data_resolveXpathString_varargs_ok()));
    result.addAll(Arrays.asList(data_resolveXpathString_map2_ok()));
    return result.toArray(new Object[result.size()][]);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_resolveXpathString_map2_ok() {
    return new Object[][] {
        new Object[] {"17 - xpath, three params, one text param",
                        "/my/xpath[_with_]/different[_parameters_]/and/text() = '_text_'",
                        "_with_::@key='value'||_parameters_::@key2='value2'||_text_::value3",
                        "/my/xpath[@key='value']/different[@key2='value2']/and/text() = 'value3'"},
        new Object[] {"18 - xpath, three params, text param empty",
                        "/my/xpath[_with_]/different[_parameters_]/and/text() = '_text_'",
                        "_with_::@key='value'||_parameters_::@key2='value2'||_text_::",
                        "/my/xpath[@key='value']/different[@key2='value2']/and/contains(., '')"},
    };
}

//-----------------------------------------------------------------------------

@Test
public void testRemovePredicatesFromXpath() throws Exception {
}

@Test
public void testGetNodeTextByXPath() throws Exception {
}

@Test
public void testGetNodeByXPath() throws Exception {
}

@Test
public void testGetNodeAttributeTextByXPath() throws Exception {
}

//-----------------------------------------------------------------------------

}