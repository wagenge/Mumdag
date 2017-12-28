package org.mumdag.utils;

//-----------------------------------------------------------------------------

import java.io.FileInputStream;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

//-----------------------------------------------------------------------------

public class PropertyHandler{

//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */	

private static PropertyHandler instance = null;
private Properties properties;
private HashSet<String> propIdMap = new HashSet<>();


//=============================================================================	
/*
* 	CONSTRUCTOR METHODS INCLUDING INSTANTIATION METHODS (public)
*/	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
private PropertyHandler() throws Exception {
	FileInputStream file = new FileInputStream("./src/main/resources/configFiles/config.properties");
	this.properties = new Properties();
	this.properties.load(file);
	this.propIdMap.add("config");
}

//-----------------------------------------------------------------------------
 
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
private PropertyHandler(String fileName) throws Exception {
	FileInputStream file = new FileInputStream(fileName);
	this.properties = new Properties();
	this.properties.load(file);
	this.propIdMap.add("config");
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
//IMPLEMENTATION:	nok, implement according getInstance method
private PropertyHandler(String fileName, String propId) throws Exception {
	FileInputStream file = new FileInputStream(fileName);
	this.properties = new Properties();
	this.properties.load(file);
	this.propIdMap.add(propId);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized PropertyHandler getInstance() throws Exception {
    if (instance == null) {
        instance = new PropertyHandler();
    }
    return instance;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				nok
public static synchronized PropertyHandler getInstance(String fileName) throws Exception {
    if (instance == null) {
        instance = new PropertyHandler(fileName);
    }
    return instance;
}


//=============================================================================	
/*
* 	GETTER/SETTER METHODS (public)
*/

//ERROR HANDLING:	ok (nok: implement own Exception)
//DOC:				nok
//TEST:				ok
public String getValue(String propKey) {
	String retVal = this.properties.getProperty(propKey);
	if(retVal == null) {
		throw new InvalidParameterException(MessageFormat.format("Missing value for key {0}!", propKey));
	}
	return retVal;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok (nok: implement own Exception)
//DOC:				nok
//TEST:				nok
public HashMap<String, String> getAllValues() {
	HashMap<String, String> retMap = new HashMap<>();
	for(String key : this.properties.stringPropertyNames()) {
		  String value = properties.getProperty(key);
		  retMap.put(key, value);
	}
	return retMap;
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok (nok: implement own Exception)
//DOC:				nok
//TEST:				nok
public boolean containsKey(String key) {
	return this.properties.containsKey(key);
}

//-----------------------------------------------------------------------------

//ERROR HANDLING:	nok 
//DOC:				nok
//TEST:				nok
public void addPropertiesFromFile(String fileName, String propId) throws Exception {
	if(!this.propIdMap.contains(propId)) {
		FileInputStream file = new FileInputStream(fileName);
		this.properties.load(file);
		this.propIdMap.add(propId);
	}
}

//-----------------------------------------------------------------------------

}
