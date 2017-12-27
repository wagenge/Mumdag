package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.util.ArrayList;
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
	List<String> infoStringList = new ArrayList<>();
	
    for(int i = 0; i < infoStrings.length; i++){
    	infoStringList.add(infoStrings[i]);
    }
	return infoStringList;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static List<String> createInfoList(List<String> infoStringList, String... infoStrings) {

  for(int i = 0; i < infoStrings.length; i++){
  	infoStringList.add(infoStrings[i]);
  }
	return infoStringList;
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
