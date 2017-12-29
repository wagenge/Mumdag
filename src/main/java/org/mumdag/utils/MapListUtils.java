package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public final class MapListUtils {

//=============================================================================	
/*
* 	METHODS (public, static)
*/

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> createInfoList(String... infoStrings) {
	List<String> retList = new ArrayList<>();
    if(infoStrings != null && infoStrings.length > 0) {
        retList = Arrays.asList(infoStrings);
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> createInfoList(List<String> infoStringList, String... infoStrings) {
    List<String> retList = new ArrayList<>();
    if(infoStringList != null) {
        retList.addAll(infoStringList);
    }
    if(infoStrings != null && infoStrings.length > 0) {
        retList.addAll(Arrays.asList(infoStrings));
    }
	return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static HashMap<String, String> createResolveXpathMap(String... resolveXpathStrings) {
    HashMap<String, String> resolveXpathMap = new HashMap<>();
    if(resolveXpathStrings != null) {
        for (int i = 0; i < resolveXpathStrings.length; i = i + 2) {
            if(i < resolveXpathStrings.length - 1) {
                resolveXpathMap.put(resolveXpathStrings[i], resolveXpathStrings[i + 1]);
            }
            else {
                resolveXpathMap.put(resolveXpathStrings[i], null);
            }
        }
    }
	return resolveXpathMap;
}

//-----------------------------------------------------------------------------

}
