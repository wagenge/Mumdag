package org.mumdag.core;

//-----------------------------------------------------------------------------

import java.io.File;
import java.io.FileNotFoundException;
//import java.lang.reflect.Constructor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.utils.PropertyHandler;
import org.mumdag.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

//-----------------------------------------------------------------------------

public class ExecutionRules {

//=============================================================================	
/*
* 	CLASS ATTRIBUTES (private)
*/	
private static final Logger log = LogManager.getLogger(ExecutionRules.class);

private static ExecutionRules instance = null;	

private Document 				 ruleSetXml = null;
private HashMap <String, Object> ruleSetMap = new HashMap <>();
private HashMap <String, Object> varMap = new HashMap <>();
private String					 scraperPackage;

	
//=============================================================================	
/*
* 	CONSTRUCTOR METHODS (public)
*/		

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private ExecutionRules() throws Exception {
	String executionRulesFilePath = PropertyHandler.getInstance().getValue("ExecutionRules.rulesFileName");
	this.scraperPackage = PropertyHandler.getInstance().getValue("ExecutionRules.scraperPackage");
	
	Path path = Paths.get(executionRulesFilePath);
	if(Files.exists(path) && Files.isRegularFile(path)) {
		createRuleSetXml(executionRulesFilePath);	
		createRuleSetMap();
	}
	else {
		throw new FileNotFoundException("Execution rules file '" + executionRulesFilePath + "' not found! Change value of the Property 'ExecutionRules.rulesFileName'");
	}
}
	
//-----------------------------------------------------------------------------	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private ExecutionRules(String executionRulesFilePath) throws Exception {
	this.scraperPackage = PropertyHandler.getInstance().getValue("ExecutionRules.scraperPackage");
	
	Path path = Paths.get(executionRulesFilePath);
	if(Files.exists(path) && Files.isRegularFile(path)) {
		createRuleSetXml(executionRulesFilePath);	
		createRuleSetMap();
	}
	else {
		throw new FileNotFoundException("Execution rules file '" + executionRulesFilePath + "' not found!");
	}
}
	
//-----------------------------------------------------------------------------	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static synchronized ExecutionRules getInstance() throws Exception {
	if (instance == null) {
		instance = new ExecutionRules();
	}
	return instance;
}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
public static synchronized ExecutionRules getInstance(String executionRulesFilePath) throws Exception {
	if (instance == null) {
		instance = new ExecutionRules(executionRulesFilePath);
	}
	return instance;
}

	
//=============================================================================	
/*
* 	RULE EXECUTION METHODS (public)
*/

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
@SuppressWarnings("unchecked")
public void executeRules(HashMap<String, Object> entry) {
    //get the entryType
    String entryType = (String)entry.get("type");

	//iterates over all rules sets (aka all scraper)
	Iterator<Entry <String, Object>> rulesSetIt = this.ruleSetMap.entrySet().iterator();
 	while (rulesSetIt.hasNext()) {
 		Class<?> scraperClass;
	  	Constructor<?> classCons = null;
	  	Object classObj = null;
	  	Entry<String, Object> rulesSetPair = rulesSetIt.next();

	  	log.info("Load RuleSet {} ... ", rulesSetPair.getKey());
	  	try {
			// find scraper class, get the correct constructor and get a new instance (created by this constructor)
			scraperClass = Class.forName(this.scraperPackage + "." + rulesSetPair.getKey().toString());
			classCons = scraperClass.getDeclaredConstructor();
			//classCons = scraperClass.getDeclaredConstructor(new Class[] {String.class, String.class, OutputXmlDoc.class});
			classObj = classCons.newInstance();
		} catch (Exception ex) {
	  		log.error("could not find class {}",rulesSetPair.getKey().toString());
	  		break;
		}
		log.info("Load RuleSet {} ... successfully", rulesSetPair.getKey());
	      
		//iterates over all actions
		TreeMap<Integer, HashMap<String, String>> actionList;
		actionList = (TreeMap<Integer, HashMap<String, String>>)rulesSetPair.getValue();
		log.info("Load ActionList ... {} actions found", actionList.size());
		Iterator<?> actionListIt = actionList.entrySet().iterator();
		while (actionListIt.hasNext()) {
			Entry<?, ?> actionListPair = (Entry<?, ?>)actionListIt.next();
			HashMap<String, Object> action;
			action = (HashMap<String, Object>)actionListPair.getValue();
			String actionMethod = (String)action.get("actionMethod");
			String returnVar = (String)action.get("returnVar");
			String[] paramList = (String[])action.get("paramList");
			String ruleName = (String)action.get("ruleName");

			//select the correct actions for the current entry type (artist scraper for artist type, e.g.)
			if(ruleName.toLowerCase().equals(entryType.toLowerCase())) {
                log.info("Action #{} {} ... started ... ", actionListPair.getKey(), actionMethod);
                try {
                    //paramList is null
                    if (paramList == null || paramList.length == 0) {
                        Method m = scraperClass.getDeclaredMethod(actionMethod);
                        Object returnVal = m.invoke(classObj);
                        if (returnVar != null && returnVar.length() > 0) {
                            varMap.put(returnVar, returnVal);
                        }
                    }
                    //paramList is not null and contains some parameters
                    else {
                        Class<?>[] argClasses = {Object[].class};
                        Method m = scraperClass.getDeclaredMethod(actionMethod, argClasses);
                        //convert the paramList into an object array
                        Object[] args = obtainParams(paramList);
                        //call the declared method with the given arguments (as object array) and save the return value into an Object variable called 'returnVal'
                        Object returnVal = m.invoke(classObj, args);
                        //save the returnVal - if not null or empty in the variable map
                        if (returnVar != null && returnVar.length() > 0) {
                            varMap.put(returnVar, returnVal);
                        }
                    }
                } catch (Exception ex) {
                    log.error("could not find method{}", actionMethod);
                    break;
                }
                log.info("Action #{} {} ... finished", actionListPair.getKey(), actionMethod);
            }
            else {
                log.info("Action {} ... skipped", actionMethod);
            }
		}
 	}
}

	
//=============================================================================	
/*
* 	GETTER/SETTER METHODS	
*/
	
public Integer getNumberOfRuleSets() { return this.ruleSetMap.size(); }
	
//-----------------------------------------------------------------------------	

@SuppressWarnings("unchecked")
public Integer getNumberOfActions() {
    TreeMap<Integer, HashMap<String, String>> actionList;
	int numOfActions = 0;
	//iterates over all rules sets
	Iterator<Entry<String, Object>> rulesSetIt = this.ruleSetMap.entrySet().iterator();
 	while (rulesSetIt.hasNext()) {
	      Entry<?, ?> rulesSetPair = rulesSetIt.next();
	      actionList = (TreeMap<Integer, HashMap<String, String>>)rulesSetPair.getValue();	      
	      numOfActions = numOfActions + actionList.size();  
 	}
	return numOfActions;
}

//-----------------------------------------------------------------------------	

public Document getRuleSetXml() { return ruleSetXml; }
	
//-----------------------------------------------------------------------------	

public void setRuleSetXml(Document ruleSetXml) { this.ruleSetXml = ruleSetXml; }

//-----------------------------------------------------------------------------	

public Object getVar(String varName) {	return this.varMap.get(varName);	}
	
	
//=============================================================================	
/*
* 	HELPER METHODS (private)
*/	

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private void createRuleSetXml(String executionRulesFilePath) throws Exception {
	File ruleSetFile = new File(executionRulesFilePath);
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	this.setRuleSetXml(dBuilder.parse(ruleSetFile));
}
	
//-----------------------------------------------------------------------------	

//PARAMETRIZING:	ok
//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private void createRuleSetMap() throws Exception {
	XPath xPath =  XPathFactory.newInstance().newXPath();
	Document doc = this.getRuleSetXml();

	// get all rule set names
	String xpathRuleSetNames = XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleSetName"), "_rs_::");
	NodeList ruleSetNames;
	ruleSetNames = (NodeList)xPath.compile(xpathRuleSetNames).evaluate(doc, XPathConstants.NODESET);
	if(ruleSetNames.getLength() == 0 ) {
		log.error("No mapping rules found under xpath '{}'!", xpathRuleSetNames);
		throw new Exception(MessageFormat.format("No mapping rules found under xpath '{0}'!", xpathRuleSetNames));
	}
	
	for(int a = 0; a < ruleSetNames.getLength(); a++) {
		String ruleSetName = ruleSetNames.item(a).getTextContent();
		if(ruleSetName.length() == 0) {
			log.error("No name set for rule set #{}!", a);
			throw new Exception(MessageFormat.format("No name set for rule set #{} !", a));
		}
		
		//action list for each rule set
		TreeMap<Integer, HashMap<String, Object>> actionList = new TreeMap<>();
		// sequence number for each action per rule set
		int actionSeqNr = 0; 
		
		// get all rule numbers for each rule set
		String xpathRuleNo = XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleRuleNo"),
				"_rs_::" + "@name='" + ruleSetName + "'", "_rl_::");
		NodeList ruleNos;
		ruleNos = (NodeList)xPath.compile(xpathRuleNo).evaluate(doc, XPathConstants.NODESET);
		if(ruleNos.getLength() == 0 ) {
			log.error("No mapping rules found under xpath '{}'!", xpathRuleNo);
			throw new Exception(MessageFormat.format("No mapping rules found under xpath '{0}'!", xpathRuleNo));
		}
		for(int b = 0; b < ruleNos.getLength(); b++) {
			String ruleNo = ruleNos.item(b).getTextContent();
			if(ruleNo.length() == 0) {
				log.error("No number set for rule #{}.{} !", a, b);
				throw new Exception(MessageFormat.format("No number set for rule #{0}.{1} !", a, b));
			}			
			
			// get all action numbers for each rule
			String xpathActionNo = XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleActionNo"),
					"_rs_::@name='" + ruleSetName + "'", "_rl_::@no='" + ruleNo + "'", "_ac_::");
			NodeList actionNos;
			actionNos = (NodeList)xPath.compile(xpathActionNo).evaluate(doc, XPathConstants.NODESET);
			if(actionNos.getLength() == 0 ) {
				log.error("No actions found under xpath '{}'!", xpathActionNo);
				throw new Exception(MessageFormat.format("No actions found under xpath '{0}'!", xpathActionNo));
			}
			for(int c = 0; c < actionNos.getLength(); c++) {
				String ruleName;
				String actionMethod;
				String returnVar;
				String paramString;
				String actionNo = actionNos.item(c).getTextContent();
				if(actionNo.length() == 0) {
					log.error("No number set for action #{}.{}.{} !", a, b, c);
					throw new Exception(MessageFormat.format("No number set for action #{0}.{1}.{2} !", a, b, c));
				}
				
				actionSeqNr++;
				// get Method/ReturnVar/ParamList for each action
				String xpathRuleName = XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleRuleName"),
						"_rs_::" + "@name='" + ruleSetName + "'", "_rl_::@no='" + ruleNo + "'");
				String xpathMethod =XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleActionMethod"),
						"_rs_::@name='" + ruleSetName + "'", "_rl_::@no='" + ruleNo + "'", "_ac_::@no='" + actionNo + "'");
				String xpathReturnVar = XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleActionReturnVar"),
						"_rs_::@name='" + ruleSetName + "'", "_rl_::@no='" + ruleNo + "'", "_ac_::@no='" + actionNo + "'");
				String xpathParamList =XmlUtils.resolveXpathString(PropertyHandler.getInstance().getValue("ExecutionRules.xpath.ruleActionParamList"),
						"_rs_::@name='" + ruleSetName + "'", "_rl_::@no='" + ruleNo + "'", "_ac_::@no='" + actionNo + "'");
				actionMethod = (String)xPath.compile(xpathMethod).evaluate(doc, XPathConstants.STRING);
				if(actionMethod.length() == 0) {
					log.error("No name set for action #{}.{}.{} !", a, b, c);
					throw new Exception(MessageFormat.format("No name set for action #{0}.{1}.{2} !", a, b, c));
				}				
				returnVar = (String)xPath.compile(xpathReturnVar).evaluate(doc, XPathConstants.STRING);
				paramString = (String)xPath.compile(xpathParamList).evaluate(doc, XPathConstants.STRING);
				ruleName = (String)xPath.compile(xpathRuleName).evaluate(doc, XPathConstants.STRING);
				
				String paramListDelimiter = PropertyHandler.getInstance().getValue("ExecutionRules.paramListDelimiter");
				HashMap<String, Object> action = new HashMap<>();
				action.put("actionSeqNr", String.valueOf(actionSeqNr));
				action.put("actionNo", actionNo);
				action.put("actionMethod", actionMethod);
				action.put("returnVar", convertReturnVarString(returnVar));
				action.put("paramList", convertParamStringToArray(paramString, paramListDelimiter));
				action.put("ruleName", ruleName);
				action.put("ruleSetName", ruleSetName);
				action.put("ruleNo", ruleNo);
				actionList.put(actionSeqNr, action);
			}
		}
		this.ruleSetMap.put(ruleSetName, actionList);
	}
}
	
//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private Object[] convertParamStringToArray(String paramString, String delimiter) {
	if(paramString != null && paramString.length() > 0 && !paramString.equals("-")) {
		String[] paramArr = paramString.split(delimiter);
		for(int i = 0; i < paramArr.length; i++) {
			paramArr[i] = paramArr[i].trim();
		}
		return paramArr;
	}
	else {
		return null;
	}
}
	
//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private String convertReturnVarString(String returnVarString) {	
	String returnVar = "";
	if(returnVarString != null && returnVarString.length() > 0) {
		if(returnVarString.startsWith("$") && returnVarString.endsWith("$")) {
			returnVar = returnVarString;
		}
		else if(returnVarString.equals("-")) {
			returnVar = "";
		}
		else {
			returnVar = "$" + returnVarString + "$"; 
			log.warn("Return variables should start and end with an '$' ... change returnVar to {}", returnVar);
		}
	}
	return returnVar;
}
	
//-----------------------------------------------------------------------------

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
private Object[] obtainParams(String[] paramList) {
	Object[] paramArr = new Object[paramList.length];
	
	for(int i = 0; i < paramList.length; i++) {
		String paramStr = paramList[i];
		if(paramStr.isEmpty()) {
			paramArr[i] = paramStr;
		}
		else if(paramStr.startsWith("$") && paramStr.endsWith("$")) {
			paramArr[i] = varMap.get(paramStr);
		}
		else if(paramStr.startsWith("\"") && paramStr.endsWith("\"")) {
			paramArr[i] = paramStr.substring(1,  paramStr.length()-1);
		}
		else if(paramStr.startsWith("'") && paramStr.endsWith("'")) {
			paramArr[i] = paramStr.substring(1,  paramStr.length()-1);
		}else {
			paramArr[i] = paramStr;
		}
	}
	Object[] paramRet = { paramArr };
	return paramRet;
}

//-----------------------------------------------------------------------------

}