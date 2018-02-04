package org.mumdag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.core.LocalMusicIndexer;
import org.mumdag.model.index.MusicIndex;
import org.mumdag.utils.PropertyHandler;

import java.util.HashMap;
import java.util.TreeMap;


public class Mumdag {

	private static final Logger log = LogManager.getLogger(Mumdag.class);
	
	public static void main(String[] args) {
		log.info(" Mumdag started");

/*		// Generate an empty document with a given template file
		String templateFilePath;
		OutputXmlDoc oxd = null;
		try {
			templateFilePath = PropertyHandler.getInstance("./src/main/resources/configFiles/config.properties").getValue("OutputXmlDoc.templatePath");
			oxd = new OutputXmlDoc(templateFilePath);
			log.info(" Empty output document created from template '{}'", templateFilePath);
		} catch (Exception ex) {
			log.error(" {}: {} ... Mumdag exited",  ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
			return;
		}
*/

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
*/
		// Extract the artists-(folders) for a given starting path.
		//	The depth of the artist folder is configured in the config.properties   
		String startPath;
		//Map<String, String> artistList = null;
		//Integer artistListSize = 0;
		try {
			startPath = PropertyHandler.getInstance().getValue("LocalMusicIndexer.startPath");
			log.info("Building artist index started ...  start reading at {}", startPath);
			//LocalArtistIndex lai = new LocalArtistIndex(startPath);
			LocalMusicIndexer lmi = new LocalMusicIndexer(startPath);
			lmi.buildIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
        MusicIndex mi = MusicIndex.getInstance();
        log.info("Building music index finished ... {} artists and {} tracks found", mi.getNumOfArtists(), mi.getNumOfTracks());

		TreeMap<Long, HashMap<String, Object>> fmi =  mi.getFlatMusicIndex();

		//if(artistList != null) {
		//	artistListSize = artistList.size();
		//}

			
/*		int i = 1;
		for (Map.Entry<Long, HashMap<String, Object>> entry : fmi.entrySet()) {
		    Long entryKey = entry.getKey();
		    HashMap<String, Object> entryValue = entry.getValue();

		    String entryName = "";
		    switch ((String)entryValue.get("type")) {
                case "artist":
                    entryName = (String)entryValue.get("artistFolderName");
                    break;
                case "album":
                    entryName = (String)entryValue.get("albumFolderName");
                    break;
                case "medium":
                    entryName = String.valueOf(entryValue.get("mediumNumber"));
                    break;
                case "track":
                    entryName = (String)entryValue.get("trackFileName");
                    break;
		    }

            log.info("#{}: {} ({}) => {} ... start executing rules", i, String.valueOf(entryKey), entryValue.get("type"), entryName);

		    //log.info("#{}: {} ({}) ... start executing rules", i, artistName, artistCanonicalPath);
			
		    //try {
		    //	oxd.createOutputXmlDoc();
			//	oxd = er.executeRules(artistName, artistCanonicalPath, oxd);
			//} catch (Exception e) {
			//	e.printStackTrace();
			//}
		    //oxd.writeOutputDocToFile(artistCanonicalPath + "/.metadata/", "test.xml");
		    //log.info("#{}: {} ({}) ... executing rules finished", i, artistName, artistCanonicalPath);
		    i++;
		}*/

		log.info(" Mumdag finished");
	}

}
