package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//-----------------------------------------------------------------------------

public class XmlUtilsTest {

private static Logger log = null;

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

@Test(dataProvider = "data_removePredicatesFromXpath_ok")
public void test_removePredicatesFromXpath_ok(String testDesc, String xpath, String expRes) {
    log.info("{} ... started", testDesc);
    String res = XmlUtils.removePredicatesFromXpath(xpath);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_removePredicatesFromXpath_ok() {
    return new Object[][] {
        new Object[] {"01 - normal xpath, two predicates",
                        "/my/xpath[_with_]/different[_parameters_]/and/text() = '_text_'",
                        "/my/xpath/different/and/text() = '_text_'"},
        new Object[] {"02 - normal xpath, two predicates, one empty",
                        "/my/xpath[_with_]/different[]/and/text() = '_text_'",
                        "/my/xpath/different/and/text() = '_text_'"},
        new Object[] {"03 - normal xpath, no predicates",
                        "/my/xpath/different/and/text() = '_text_'",
                        "/my/xpath/different/and/text() = '_text_'"},
        new Object[] {"04 - empty xpath",
                        "",
                        ""},
        new Object[] {"05 - xpath null",
                        null,
                        ""},
    };
}

//-----------------------------------------------------------------------------

@Test(dataProvider = "data_getNodeTextByXPath_ok")
public void test_getNodeTextByXPath_ok(String testDesc, String xmlFile, String xpath, String expRes) {
    log.info("{} ... started", testDesc);
    List<String> resList = new ArrayList<>();
    try {
        String xmlStr = new String(Files.readAllBytes(Paths.get(xmlFile)));
        resList = XmlUtils.getNodeTextByXPath(xmlStr, xpath);
    }
    catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Maybe file '" + xmlFile + "' not found!\n" + "Error: " + ex.getMessage());
    }
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getNodeTextByXPath_ok() {
    return new Object[][] {
        new Object[] {"01 - xml ok, xpath ok, one node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-01-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/name/text()",
                        "The Beatles"},
        new Object[] {"02 - xml ok, xpath ok, one node text found, text utf8",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-02-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/alias-list/alias[@locale='ru']/text()",
                        "Битлз"},
        new Object[] {"03 - xml ok, xpath ok, many nodes' text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-03-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/alias-list/alias[@locale]/text()",
                        "Beatles, Los Beatles, The Beatles, Битлз, ザ・ビートルズ, 披头士, 披頭四, 더 비틀즈"},
        new Object[] {"04 - xml ok, xpath ok, one attribute text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-04-inp.xml",
                        "/metadata/artist/rating/@votes-count",
                        "67"},
        new Object[] {"05 - xml ok, xpath with slash, one node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-05-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/name/text()/",
                        "The Beatles"},
        new Object[] {"06 - xml ok, xpath ok, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-06-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/names/text()/",
                        ""},
        new Object[] {"07 - xml ok, xpath empty, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-07-inp.xml",
                        "",
                        ""},
        new Object[] {"08 - xml ok, xpath null, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-08-inp.xml",
                        null,
                        ""},
        new Object[] {"09 - xml ok, xpath wrong, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-09-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/name/texts()",
                        ""},
        new Object[] {"10 - xml empty, xpath ok, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-10-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/name/text()",
                        ""},
        new Object[] {"11 - xml corrupt, xpath ok, no node text found",
                        "./src/test/resources/XmlUtilsTest/getNodeTextByXPath/getNodeTextByXPath_ok-11-inp.xml",
                        "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/name/text()",
                        ""},
    };
}

//-----------------------------------------------------------------------------

@Test(dataProvider = "data_getNodeByXPath_ok")
public void test_getNodeByXPath_ok(String testDesc, String xmlFile, String xpath, String childNodeNames, String expRes) {
    log.info("{} ... started", testDesc);
    List<String> resList = new ArrayList<>();
    try {
        String xmlStr = new String(Files.readAllBytes(Paths.get(xmlFile)));
        resList = XmlUtils.getNodeByXPath(xmlStr, xpath, childNodeNames);
    }
    catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Maybe file '" + xmlFile + "' not found!\n" + "Error: " + ex.getMessage());
    }
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getNodeByXPath_ok() {
    return new Object[][] {
            new Object[] {"01 - xml ok, xpath ok, child list ok, 1 node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-01-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "country",
                    "country=GB"},
            new Object[] {"02 - xml ok, xpath with slash, child list ok, 1 node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-02-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/",
                    "country",
                    "country=GB"},
            new Object[] {"03 - xml ok, xpath ok, child list ok, 1 complex node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-03-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "area",
                    "name=United Kingdom,sort-name=United Kingdom,iso-3166-1-code=GB"},
            new Object[] {"04 - xml ok, xpath ok, child list ok, 14 nodes found, text utf8",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-04-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/alias-list",
                    "alias",
                    "alias=B,alias=Be,alias=Beat,alias=Beatles,alias=Beetles,alias=Los Beatles,alias=The Beatles,alias=The Savage Young Beatles,alias=fab four,"+
                            "alias=Битлз,alias=ザ・ビートルズ,alias=披头士,alias=披頭四,alias=더 비틀즈"},
            new Object[] {"05 - xml ok, xpath ok, child list ok with 2 items, 2 complex nodes found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-05-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "area, begin-area",
                    "name=United Kingdom,sort-name=United Kingdom,iso-3166-1-code=GB,name=Liverpool,sort-name=Liverpool,iso-3166-2-code=GB-LIV"},
            new Object[] {"06 - xml ok, xpath ok, child list ok, no nodes found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-06-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "wrong-tag",
                    ""},
            new Object[] {"07- xml ok, xpath ok, child list ok one child found one child not found, 1 complex node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-07-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "wrong-tag, area",
                    "name=United Kingdom,sort-name=United Kingdom,iso-3166-1-code=GB"},
            new Object[] {"08- xml ok, xpath ok, child empty, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-08-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "",
                    ""},
            new Object[] {"09- xml ok, xpath ok, child null, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-09-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    null,
                    ""},
            new Object[] {"10 - xml ok, xpath with slash, child list ok, 1 complex node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-10-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/",
                    "area",
                    "name=United Kingdom,sort-name=United Kingdom,iso-3166-1-code=GB"},
            new Object[] {"11 - xml ok, xpath empty, child list ok, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-11-inp.xml",
                    "",
                    "area",
                    ""},
            new Object[] {"12 - xml ok, xpath null, child list ok, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-12-inp.xml",
                    null,
                    "area",
                    ""},
            new Object[] {"13 - xml ok, xpath wrong, child list ok, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-13-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']/texts()",
                    "area",
                    ""},
            new Object[] {"14 - xml empty, xpath ok, child list ok, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-14-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "area",
                    ""},
            new Object[] {"15 - xml corrupt, xpath ok, child list ok, no node found",
                    "./src/test/resources/XmlUtilsTest/getNodeByXPath/getNodeByXPath_ok-15-inp.xml",
                    "/metadata/artist[@id='b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d']",
                    "area",
                    ""},
    };
}

//-----------------------------------------------------------------------------

@Test(dataProvider = "data_getNodeAttributeTextByXPath_ok")
public void test_getNodeAttributeTextByXPath_ok(String testDesc, String xmlFile, String xpath, String attrName, String expRes) {
    log.info("{} ... started", testDesc);
    List<String> resList = new ArrayList<>();
    try {
        String xmlStr = new String(Files.readAllBytes(Paths.get(xmlFile)));
        if(StringUtils.isNotEmpty(attrName)) {
            resList = XmlUtils.getNodeAttributeTextByXPath(xmlStr, xpath, attrName);
        }
        else {
            resList = XmlUtils.getNodeAttributeTextByXPath(xmlStr, xpath);
        }
    }
    catch (Exception ex) {
        log.error("... failed");
        fail("No exception expected. Maybe file '" + xmlFile + "' not found!\n" + "Error: " + ex.getMessage());
    }
    String res = String.join(", ", resList);
    assertThat(res).isEqualTo(expRes);
    log.info("{} ... finished successfully!", testDesc);
}

//-----------------------------------------------------------------------------

//DOC:	nok
@DataProvider
public Object[][] data_getNodeAttributeTextByXPath_ok() {
    return new Object[][] {
        new Object[] {"01 - xml ok, xpath ok, attribute name ok, 1 attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-01-inp.xml",
                        "/metadata/artist",
                        "id",
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"},
        new Object[] {"02 - xml ok, xpath ok, attribute name ok, many node with attributes found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-02-inp.xml",
                        "/metadata/artist/alias-list/alias",
                        "sort-name",
                        "sort-name=B, sort-name=Be, sort-name=Beat, sort-name=Beatles, sort-name=Beetles, sort-name=Los Beatles, sort-name=Beatles, The, sort-name=Savage Young Beatles, The, sort-name=fab four, sort-name=Битлз, sort-name=ビートルズ (ザ), sort-name=披头士, sort-name=披頭四, sort-name=더 비틀즈"},
        new Object[] {"03 - xml ok, xpath ok, attribute name ok, no attributes found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-03-inp.xml",
                        "/metadata/artist",
                        "ids",
                        ""},
        new Object[] {"04 - xml ok, xpath ok, attribute empty, all attributes of this node found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-04-inp.xml",
                        "/metadata/artist",
                        "",
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d, type=Group, type-id=e431f5f6-b5d2-343d-8b36-72607fffb74b"},
        new Object[] {"05 - xml ok, xpath ok, attribute null, all attributes of this node found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-05-inp.xml",
                        "/metadata/artist",
                        null,
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d, type=Group, type-id=e431f5f6-b5d2-343d-8b36-72607fffb74b"},
        new Object[] {"06 - xml ok, xpath ok, two attributes, two attributes of this node found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-06-inp.xml",
                        "/metadata/artist",
                        "id, type",
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d, type=Group"},
        new Object[] {"07 - xml ok, xpath ok, two attributes, one attribute of this node found and one not",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-07-inp.xml",
                        "/metadata/artist",
                        "id, types",
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"},
        new Object[] {"08 - xml ok, xpath ok - found many nodes, attribute ok, one attribute found for all nodes",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-08-inp.xml",
                        "/metadata/artist/relation-list[@target-type='url']/relation[@type='lyrics']/target",
                        "id",
                        "id=7ccb2b9e-88f7-4030-bdde-f3eb81d16aa5, id=5f3d83eb-6ad6-4db7-a6ed-d4f437db91c6, id=b1e3f0b8-eee0-4ced-87b9-d7088481ae10, id=28f763a2-ad86-4adc-8a4a-ef5c1a666861"},
        new Object[] {"09 - xml ok, xpath ok - found many nodes, attribute empty, get all attributes of all nodes",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-09-inp.xml",
                        "/metadata/artist/relation-list[@target-type='url']/relation[@type='lyrics']",
                        "",
                        "type=lyrics, type-id=e4d73442-3762-45a8-905c-401da65544ed, type=lyrics, type-id=e4d73442-3762-45a8-905c-401da65544ed, "+
                            "type=lyrics, type-id=e4d73442-3762-45a8-905c-401da65544ed, "+
                            "type=lyrics, type-id=e4d73442-3762-45a8-905c-401da65544ed"},
        new Object[] {"10 - xml ok, xpath with slash, attribute name ok, 1 attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-10-inp.xml",
                        "/metadata/artist/",
                        "id",
                        "id=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"},
        new Object[] {"11 - xml ok, xpath empty, attribute name ok, no attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-11-inp.xml",
                        "",
                        "id",
                        ""},
        new Object[] {"12 - xml ok, xpath null, attribute name ok, no attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-12-inp.xml",
                        null,
                        "id",
                        ""},
        new Object[] {"13 - xml ok, xpath wrong, attribute name ok, no attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-13-inp.xml",
                        "/metadata/artist/attr()",
                        "id",
                        ""},
        new Object[] {"14 - xml empty, xpath ok, attribute name ok, no attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-14-inp.xml",
                        "/metadata/artist/",
                        "id",
                        ""},
        new Object[] {"15 - xml corrupt, xpath ok, attribute name ok, no attribute found",
                        "./src/test/resources/XmlUtilsTest/getNodeAttributeTextByXPath/getNodeAttributeTextByXPath_ok-15-inp.xml",
                        "/metadata/artist/",
                        "id",
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
    log = LogManager.getLogger(XmlUtilsTest.class);
}

//-----------------------------------------------------------------------------

}