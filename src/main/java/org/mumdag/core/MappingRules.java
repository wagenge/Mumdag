package org.mumdag.core;

//-----------------------------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.mumdag.core.OutputXmlDoc.NodeAction;
import org.mumdag.core.OutputXmlDoc.NodeStatus;

//-----------------------------------------------------------------------------

public class MappingRules {

//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */	
private static MappingRules instance = null;	
private HashMap<String, HashMap<String, String>> mappingRules = new HashMap<>();


//=============================================================================	
/*
* 	CONSTRUCTOR METHODS (public)
*/	
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
private MappingRules() {	}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized MappingRules getInstance() {
	if (instance == null) {
		instance = new MappingRules();
	}
	return instance;
}

//=============================================================================	
/*
* 	GETTER/SETTER/UPDATER METHODS (public)	
*/
//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public void updateMappingRules(String filePath, String scraperId, String ruleType) throws IOException {
	BufferedReader  bfr = new BufferedReader(new FileReader(new File(filePath)));

    String line;
    while ((line = bfr.readLine()) != null) {
        if (!line.startsWith("#") && !line.isEmpty()) {
        	//split key and value ('=')
        	String[] pair = line.trim().split("=", 2);
            //split left side ('.') key for outer HashMap and key for inner HashMap
        	String key = pair[0].trim();
            String[] anotherPair = key.trim().split("\\.");
            //create key (scraperId.anotherPair[0])
            String outerKey = scraperId + "." + anotherPair[0].trim();
            String innerKey = anotherPair[1].trim();
            String innerValue = pair[1].trim();
        	HashMap<String, String> innerMap = new HashMap<>();
            //check if key already exists
            //	if yes, get inner HashMap and update it with the given inner key
            if(mappingRules.containsKey(outerKey)) {
            	innerMap = mappingRules.get(outerKey);
            }
            // else take the empty innerMap and put the standard type entry
            else {
            	innerMap.put("type", ruleType);
            }

            //update/add inner map for given outer key
        	innerMap.put(innerKey, innerValue);
        	mappingRules.put(outerKey, innerMap);
        }
    }
    bfr.close();
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public HashMap<String, String> getMappingRule(String scraperId, String ruleName) {
	return this.mappingRules.get(scraperId+"."+ruleName);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public String getMappingRuleElement(String scraperId, String ruleName, String element) {
	return this.mappingRules.get(scraperId+"."+ruleName).get(element);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public HashMap<NodeStatus, NodeAction> getCopyBehavior(String scraperId, String ruleName) {
	HashMap<NodeStatus, NodeAction> copyBehavior = new HashMap<>();
	
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("A_EMPTY_NODE_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("A_EMPTY_NODE_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("A_EMPTY_NODE_EXISTS")));
	}
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("NO_EMPTY_NODE_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("NO_EMPTY_NODE_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("NO_EMPTY_NODE_EXISTS")));
	}
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("NODE_WITH_SAME_ATTRIBUTE_NAME_EXISTS")));
	}
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("NODE_WITH_SAME_ATTRIBUTE_VALUE_EXISTS")));
	}
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("NODE_WITH_SAME_CONTENT_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("NODE_WITH_SAME_CONTENT_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("NODE_WITH_SAME_CONTENT_EXISTS")));
	}
	if(this.mappingRules.get(scraperId+"."+ruleName).containsKey("SAME_NODE_EXISTS")) {
		copyBehavior.put(NodeStatus.valueOf("SAME_NODE_EXISTS"), 
				 NodeAction.valueOf(this.mappingRules.get(scraperId+"."+ruleName).get("SAME_NODE_EXISTS")));
	}
	
	return copyBehavior;
}

//-----------------------------------------------------------------------------

public HashMap<String, HashMap<String, String>> getMappingRules() {	return mappingRules;	}

//-----------------------------------------------------------------------------

public void setMappingRules(HashMap<String, HashMap<String, String>> mappingRules) {	this.mappingRules = mappingRules;	}

//-----------------------------------------------------------------------------

}
