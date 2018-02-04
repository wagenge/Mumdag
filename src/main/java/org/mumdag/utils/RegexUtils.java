package org.mumdag.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {

//=============================================================================
/*
 * 	METHODS (public, static)
 */

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> extractGroups(String str, String regex) {
    HashMap<String, String> retMap = new HashMap<>();
    List<String> namedGroups = extractMatches(regex, "\\?\\<([a-zA-Z]{1,})\\>");
    Pattern pattern = Pattern.compile(regex);
    Matcher match = pattern.matcher(str);
    if (match.find()) {
        //put all found groups into the map (key is the stringyfied index)
        for(int i = 0; i < match.groupCount(); i++) {
            retMap.put(String.valueOf(i), match.group(i));
        }
        //put additional all found named groups into the map (key is the named group)
        for(String namedGroup : namedGroups) {
            retMap.put(namedGroup, match.group(namedGroup));
        }
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> extractMatches(String str, String regex) {
    List<String> retList = new ArrayList<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher match = pattern.matcher(str);
    while(match.find()) {
        retList.add(match.group(1));
    }
    return retList;
}

//-----------------------------------------------------------------------------

}
