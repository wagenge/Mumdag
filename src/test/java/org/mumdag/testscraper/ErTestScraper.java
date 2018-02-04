package org.mumdag.testscraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mumdag.model.index.Medium;
import org.mumdag.model.index.MusicIndex;

public class ErTestScraper {

	private static final Logger log = LogManager.getLogger(ErTestScraper.class);
	private MusicIndex mi = MusicIndex.getInstance();

	public ErTestScraper() {

	}

	
	public void setArtistInfo() {
		log.info("current entry index={}", mi.getCurrentEntryId());
		mi.setArtistId("mbid", String.valueOf(mi.getCurrentEntryId()));
	}

	public void setReleaseGroupInfo() {
		log.info("current entry index={}", mi.getCurrentEntryId());
        mi.setReleaseGroupId("mbid", String.valueOf(mi.getCurrentEntryId()));
	}

	public void setReleaseInfo() {
		log.info("current entry index={}", mi.getCurrentEntryId());
        mi.setReleaseId("mbid", String.valueOf(mi.getCurrentEntryId()));
	}

    public void setMediumInfo() {
        log.info("current entry index={}", mi.getCurrentEntryId());
        mi.setMediumId("mbid", "discNo", String.valueOf(((Medium)mi.getCurrentEntry()).getMediumNumber()));
    }

	public void setTrackInfo() {
		log.info("current entry index={}", mi.getCurrentEntryId());
        mi.setTrackId("mbid", String.valueOf(mi.getCurrentEntryId()));
	}

}