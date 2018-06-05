package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XpathUtils {

//=============================================================================
/*
 * 	CLASS ATTRIBUTES (private)
 */
private static final Logger log = LogManager.getLogger(XmlUtils.class);


//=============================================================================
/*
 * 	METHODS FOR XPATH (public, static)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> createResolveMap(String... resolveStrings) {
    HashMap<String, String> resolveMap = new HashMap<>();
    if(resolveStrings != null) {
        for (int i = 0; i < resolveStrings.length; i++) {
            String resolveStr = resolveStrings[i];
            String[] keyValPair = resolveStr.split("::");
            if(keyValPair.length == 2) {
                //put the original resolveInfo (e.g. _idAttr_ -> mbid=123abc or _arname_ -> Beatles, The)
                resolveMap.put(keyValPair[0], keyValPair[1]);

                //put the check resolveInfo
                String keyCheck = keyValPair[0].substring(0, keyValPair[0].length()-1) + "Check_";
                String valCheck = "";
                // case 1: _idAttrCheck_ -> @mbid='123abc'
                if(keyValPair[1].contains("=")) {
                    valCheck = "@" + keyValPair[1].replaceFirst("=", "='") + "'";
                }
                // case 2: _arname_ -> 'Beatles, The'
                else {
                    valCheck = keyValPair[1];
                }
                resolveMap.put(keyCheck, valCheck);

                //put the attrName resolveInfo (e.g. _idAttrName_ -> @mbid)
                String keyName = keyValPair[0].substring(0, keyValPair[0].length()-1) + "Name_";
                // case 1: _idAttrName_ -> @mbid
                if(keyValPair[1].contains("=")) {
                    String valName = "@" + keyValPair[1].substring(0, keyValPair[1].indexOf('='));
                    resolveMap.put(keyName, valName);
                }
            }
            else if(keyValPair.length == 1) {
                //put the empty resolveInfo (e.g. _idAttr_ -> ''
                resolveMap.put(keyValPair[0], "");

                //put the check resolveInfo
                String keyCheck = keyValPair[0].substring(0, keyValPair[0].length()-1) + "Check_";
                resolveMap.put(keyCheck, "");

                //put the attrName resolveInfo (e.g. _idAttrName_ -> @mbid)
                String keyName = keyValPair[0].substring(0, keyValPair[0].length()-1) + "Name_";
                resolveMap.put(keyName, "");
            }
        }
    }
    return resolveMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static String resolveXpathString(String xpath, String... replacementStrings) {
    if(StringUtils.isEmpty(xpath)) {
        return xpath;
    }
    for (String replacementString : replacementStrings) {
        if(StringUtils.isNotEmpty(replacementString)) {
            String[] parts = replacementString.split("::");
            if (parts.length == 2) {
                String replKey = parts[0];
                String replVal = parts[1];
                xpath = xpath.replaceAll(replKey, replVal);
            } else if (parts.length == 1) {
                String replKey = parts[0];
                int idxOfReplKey = xpath.indexOf(replKey);
                if (idxOfReplKey != -1) {
                    xpath = xpath.substring(0, idxOfReplKey - 1) + xpath.substring(idxOfReplKey + replKey.length() + 1, xpath.length());
                }
            }
        }
    }
    return xpath;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static String resolveXpathString(String xpath, HashMap<String, String> resolveXpathInfos) {
    String patternStr = "(\\[|'| )(_[a-zA-Z]{3,}_)(\\]|'| )";
    return resolveXpathString(xpath, resolveXpathInfos, patternStr);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				ok (implicite with test_resolveXpathString_map_ok()
private static String resolveXpathString(String xpath, HashMap<String, String> resolveXpathInfos, String patternStr) {
    if(StringUtils.isEmpty(xpath)) {
        return xpath;
    }
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(xpath);
    while (matcher.find()) {
        String replaceStr = matcher.group(2);
        String strPrefix = matcher.group(1);
        String strPostfix = matcher.group(3);
        //search in the resolveXpathInfo map with the key (replaceStr), but without the leading and closing '_'
        String newReplacementStr = replaceStr.substring(1, replaceStr.length()-1);
        String replacementStr = resolveXpathInfos.get(newReplacementStr);

        //if nothing found in the resolveXpathInfo map, try it with the key including the leading and closing '_'
        if(replacementStr == null) {
            replacementStr = resolveXpathInfos.get(replaceStr);
        }

        //if a replacement string is found and it is not an empty string
        if(StringUtils.isNotEmpty(replacementStr)) {
            //case: [id='1234'] ==> [@id='1234']
            if(replacementStr.contains("=") && !(replacementStr.charAt(0) == '@')) {
                replacementStr = "@" + replacementStr;
            }
            //case: [id] ==> [@id]
            else if(!replacementStr.contains("=") && !(replacementStr.charAt(0) == '@') && replacementStr.matches("[a-zA-Z]+") &&
                    strPrefix.equals("[") && strPostfix.equals("]")) {
                replacementStr = "@" + replacementStr;
            }
            xpath = xpath.replaceAll(replaceStr, replacementStr);
        }
        //if a replacement string is found (_arid_, _arname_) but it is an empty string ("")
        // case 1: abc[_arid]/def ==> abc/def
        // case 2: abc/text() = '_arname_' ==> abc/contains(., '')
        else if(replacementStr != null && replacementStr.length() == 0) {
            //case 1
            if(matcher.group(1).equals("[") && matcher.group(3).equals("]")) {
                int idxOfReplStr = xpath.indexOf(replaceStr);
                xpath = xpath.substring(0, idxOfReplStr-1) + xpath.substring(idxOfReplStr+replaceStr.length()+1, xpath.length());
            }
            //case 2
            else if(matcher.group(1).equals("'") && matcher.group(3).equals("'")) {
                int idxOfReplStr = xpath.indexOf(replaceStr);
                int idxOfPrevSlash = xpath.lastIndexOf('/', idxOfReplStr)+1;
                int idxOfReplStrEnd = idxOfReplStr + replaceStr.length()+1;
                StringBuilder bld = new StringBuilder(xpath);
                bld.replace(idxOfPrevSlash, idxOfReplStrEnd, "contains(., '')");
                xpath = bld.toString();
            }
        }
        //if no replacement string is found
        else {
            log.warn("cannot find entry for '{}' in the resolveXpathInfos HashMap!", replaceStr);
        }
    }
    log.info("xpath='{}'", xpath);

    return xpath;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static String removePredicatesFromXpath(String xpath) {
    String retStr = "";
    if(StringUtils.isNotEmpty(xpath)) {
        retStr = xpath.replaceAll("(\\[.*?])", "");
    }
    return retStr;
}

}
