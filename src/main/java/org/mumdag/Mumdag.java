package org.mumdag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.OutputXmlDoc;
import org.mumdag.utils.PropertyHandler;


public class Mumdag {

	private static final Logger log = LogManager.getLogger(Mumdag.class);
	
	public static void main(String[] args) {
		log.info("Mumdag started");

		// Generate an empty document with a given template file
		String templateFilePath;
		OutputXmlDoc oxd = null;
		try {
			templateFilePath = PropertyHandler.getInstance("./src/main/resources/config.properties").getValue("OutputXmlDoc.templatePath");
			oxd = new OutputXmlDoc(templateFilePath);
			log.info("Empty output document created from template '{}'", templateFilePath);
		} catch (Exception ex) {
			log.error("{}: {} ... Mumdag exited",  ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
			return;
		}


/*		// Reading the execution rules
		String executionRulesFilePath = "";
		ExecutionRules er = null;
		try {
			executionRulesFilePath = PropertyHandler.getInstance().getValue("ExecutionRules.rulesFileName");
			er = ExecutionRules.getInstance(executionRulesFilePath);
			log.info("Loading ExecutionRules, {} rule sets found", er.getNumberOfRuleSets());
		} catch (Exception ex) {
			log.error("{}: {} ... Mumdag exited",  ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		// Extract the artists-(folders) for a given starting path.
		//	The depth of the artist folder is configured in the config.properties   
		String startPath;
		Map<String, String> artistList = null;
		try {
			startPath = PropertyHandler.getInstance().getValue("LocalArtistIndex.startPath");
			log.info("Building artist index started ...  start reading at {}", startPath);
			LocalArtistIndex lai = new LocalArtistIndex(startPath);
			artistList = lai.getArtistList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Building artist index finsihed ... {} artists found", artistList.size());
			
		int i = 1;	
		for (Map.Entry<String, String> artist : artistList.entrySet()) {			
		    String artistName = artist.getKey();
		    String artistCanonicalPath = artist.getValue();

		    log.info("#{}: {} ({}) ... start executing rules", i, artistName, artistCanonicalPath);
			
		    try {
		    	oxd.createOutputXmlDoc();
				oxd = er.executeRules(artistName, artistCanonicalPath, oxd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    oxd.writeOutputDocToFile(artistCanonicalPath + "/.metadata/", "test.xml");
		    log.info("#{}: {} ({}) ... executing rules finished", i, artistName, artistCanonicalPath);
		    i++;
		}
*/
		log.info("Mumdag finished");
	}

}
