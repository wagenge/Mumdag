package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.StringUtils;

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

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> createInfoList(String... infoStrings) {
	if(infoStrings != null && infoStrings.length > 0) {
	    return Arrays.asList(infoStrings);
    }
    else {
	    return new ArrayList<>();
    }
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

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> createResolveXpathMap(String... resolveXpathStrings) {
	HashMap<String, String> resolveXpathMap = new HashMap<>();
	
  for(int i = 0; i < resolveXpathStrings.length; i=i+2){
	  resolveXpathMap.put(resolveXpathStrings[i], resolveXpathStrings[i+1]);
  }
	return resolveXpathMap;
}

//-----------------------------------------------------------------------------

}
