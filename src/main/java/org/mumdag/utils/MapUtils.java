package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//-----------------------------------------------------------------------------

public final class MapUtils {

//=============================================================================	
/*
 * 	METHODS (public, static)
 */
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
public static void mergeMaps(HashMap<String, Object> map1, HashMap<String, Object> map2) {
    for(String key : map2.keySet()) {
        if(key.endsWith(".list")) {
            List<String> tmpList = (List<String>)map2.get(key);
            for(String listEntry : tmpList) {
                putKvPairsToMap(key.substring(0, key.lastIndexOf(".list")), listEntry, map1);
            }
        } else {
            putKvPairsToMap(key, (String)map2.get(key), map1);
        }
    }
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static HashMap<String, String> array2Map(String... resolveXpathStrings) {
    HashMap<String, String> resolveXpathMap = new HashMap<>();
    if (resolveXpathStrings != null) {
        for (int i = 0; i < resolveXpathStrings.length; i = i + 2) {
            if (i < resolveXpathStrings.length - 1) {
                resolveXpathMap.put(resolveXpathStrings[i], resolveXpathStrings[i + 1]);
            } else {
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
public static HashMap<String, String> array2Map(String delimiter, String... mapString) {
    return array2Map(delimiter, Arrays.asList(mapString));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, String> array2Map(String delimiter, List<String> mapList) {
    HashMap<String, String> retMap = new HashMap<>();
    if (mapList != null) {
        for (String mapStr : mapList) {
            String[] keyValPair = mapStr.split(delimiter);
            if (keyValPair.length == 2) {
                //put the extracted value to the map
                retMap.put(keyValPair[0], keyValPair[1]);
            } else if (keyValPair.length == 1) {
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
public static HashMap<String, Object> array2ObjMap(String delimiter, String... mapString) {
    return array2ObjMap(delimiter, Arrays.asList(mapString));
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static HashMap<String, Object> array2ObjMap(String delimiter, List<String> mapList) {
    HashMap<String, Object> retMap = new HashMap<>();
    if (mapList != null) {
        for (String mapStr : mapList) {
            String[] keyValPair = mapStr.split(delimiter);
            if (keyValPair.length == 2) {
                //put the extracted value to the map
                retMap.put(keyValPair[0], (Object) keyValPair[1]);
            } else if (keyValPair.length == 1) {
                //put the key with an empty value into the map
                retMap.put(keyValPair[0], (Object) "");
            }
        }
    }
    return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
@SuppressWarnings("unchecked")
public static void putKvPairsToMap(String key, String value, HashMap<String, Object> map) {
    if(map.containsKey(key)) {
        if(map.containsKey(key+".list")) {
            List<String> tmpList = (List<String>) map.get(key+".list");
            if(!tmpList.contains(value)) {
                tmpList.add(value);
                map.put(key + ".list", tmpList);
            }
        } else {
            String mapValue = (String)map.get(key);
            if(!value.equals(mapValue)) {
                List<String> newTmpList = new ArrayList<>();
                newTmpList.add((String) map.get(key));
                newTmpList.add(value);
                map.put(key + ".list", newTmpList);
            }
        }
    }
    map.put(key, value);
}

//-----------------------------------------------------------------------------

}
