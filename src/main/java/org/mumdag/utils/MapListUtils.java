package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.commons.lang3.ArrayUtils;
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
public static ArrayList<String> createInfoList(ArrayList<String> infoStringList, String... infoStrings) {
    ArrayList<String> retList = new ArrayList<>();
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

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> createMap(String... mapString) {
    return createMap(Arrays.asList(mapString));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> createMap(List<String> mapList) {
    HashMap<String, String> retMap = new HashMap<>();
    if(mapList != null) {
        for (String mapStr: mapList) {
            String[] keyValPair = mapStr.split("::");
            if(keyValPair.length == 2) {
                //put the extracted value to the map
                retMap.put(keyValPair[0], keyValPair[1]);
            }
            else if(keyValPair.length == 1) {
                //put the key with an empty value into the map
                retMap.put(keyValPair[0], "");
            }
        }
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, Object> createObjMap(String... mapString) {
    return createObjMap(Arrays.asList(mapString));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, Object> createObjMap(List<String> mapList) {
    HashMap<String, Object> retMap = new HashMap<>();
    if(mapList != null) {
        for (String mapStr: mapList) {
            String[] keyValPair = mapStr.split("::");
            if(keyValPair.length == 2) {
                //put the extracted value to the map
                retMap.put(keyValPair[0], (Object)keyValPair[1]);
            }
            else if(keyValPair.length == 1) {
                //put the key with an empty value into the map
                retMap.put(keyValPair[0], (Object)"");
            }
        }
    }
    return retMap;
}

//-----------------------------------------------------------------------------

}
