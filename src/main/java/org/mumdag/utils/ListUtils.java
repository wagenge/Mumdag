package org.mumdag.utils;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//-----------------------------------------------------------------------------

public final class ListUtils {

private static final Logger log = LogManager.getLogger(ListUtils.class);

//=============================================================================	
/*
 * 	METHODS (public, static)
 */

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static List<String> array2List(String... infoStrings) {
    List<String> retList = new ArrayList<>();
    if (infoStrings != null && infoStrings.length > 0) {
        retList = Arrays.asList(infoStrings);
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
static List<String> array2List(List<String> infoStringList, String... infoStrings) {
    List<String> retList = new ArrayList<>();
    if (infoStringList != null) {
        retList.addAll(infoStringList);
    }
    if (infoStrings != null && infoStrings.length > 0) {
        retList.addAll(Arrays.asList(infoStrings));
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static ArrayList<String> array2List(ArrayList<String> infoStringList, String... infoStrings) {
    ArrayList<String> retList = new ArrayList<>();
    if (infoStringList != null) {
        retList.addAll(infoStringList);
    }
    if (infoStrings != null && infoStrings.length > 0) {
        retList.addAll(Arrays.asList(infoStrings));
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> file2List(String path) {
    List<String> retList = new ArrayList<>();
    try {
        retList = Files.lines(Paths.get(path)).collect(Collectors.toList());
    } catch (Exception ex) {
        log.error("could not create list from file {} \nError: {}", path, ex.getMessage());
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> map2ListWithBlacklist(HashMap<String, Object> map, String... blacklist) {
    List<String> retList = new ArrayList<>();
    List<String> blacklistList = Arrays.asList(blacklist);

    for (String key : map.keySet()) {
        if (!blacklistList.contains(key)) {
            Object objVal = map.get(key);
            if(objVal instanceof String) {
                String val = (String) objVal;
                retList.add(key + "=" + val);
            }
        }
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static void concatenateStringToListEntries(List<String> list, String toConcatenate) {
    List<String> tmpList = new ArrayList<>();
    for (String listEntry : list) {
        tmpList.add(listEntry+toConcatenate);
    }
    list.clear();
    list.addAll(tmpList);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<HashMap<String, Object>> consolidateListOfMaps(List<HashMap<String, Object>> listOfMaps, String consolidationKey) {
    List<HashMap<String, Object>> retList = new ArrayList<>();
    HashMap<String, HashMap<String, Object>> tmpMapOfMaps = new HashMap<>();

    //iterate over list
    for (HashMap<String, Object> map : listOfMaps) {
        if(map.containsKey(consolidationKey)) {
            String consolidationKeyVal = (String) map.get(consolidationKey);
            //if entry is not in hashmap (key is given variable, e.g. id or name)
            if(!tmpMapOfMaps.containsKey(consolidationKeyVal)) {
                //then put entry in the under the given key
                tmpMapOfMaps.put(consolidationKeyVal, map);
            } else {
                //  else merge entries
                HashMap<String, Object> tmpMap = tmpMapOfMaps.get(consolidationKeyVal);
                MapUtils.mergeMaps(tmpMap, map);
                tmpMapOfMaps.put(consolidationKeyVal, tmpMap);
                //      put all entries in the map which are not already in the map
                //      make a list for entries already existing
            }
        }
    }

    for(String tmpKey : tmpMapOfMaps.keySet()) {
        retList.add(tmpMapOfMaps.get(tmpKey));
    }
    return retList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<HashMap<String, Object>> list2ListMap(List<String> list, String key) {
    List<HashMap<String, Object>> retList = new ArrayList<>();
    if (list != null) {
        for (String listEntry : list) {
            HashMap<String, Object> tmpMap = new HashMap<>();
            tmpMap.put(key, listEntry);
            retList.add(tmpMap);
        }
    }
    return retList;
}

//-----------------------------------------------------------------------------

public static List<String> addMapToInfoList(List<String> infoList, HashMap<String, String> infoMap, String delimiter, String quotas) {
    List<String> retList = new ArrayList<>(infoList);
    for (Map.Entry<String, String> entry : infoMap.entrySet()) {
        String entryKey = entry.getKey();
        String entryValue = entry.getValue();
        String infoToAdd = entryKey + delimiter + quotas + entryValue + quotas;
        retList.add(infoToAdd);
    }
    return retList;
}

//-----------------------------------------------------------------------------

}