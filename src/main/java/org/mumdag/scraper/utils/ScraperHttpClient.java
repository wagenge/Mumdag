package org.mumdag.scraper.utils;

//-----------------------------------------------------------------------------

import java.util.HashMap;

//-----------------------------------------------------------------------------

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//-----------------------------------------------------------------------------

public class ScraperHttpClient {

//=============================================================================	
/*
 * 	CLASS ATTRIBUTES (private)
 */	
private static final Logger log = LogManager.getLogger(ScraperHttpClient.class);

private static ScraperHttpClient instance = null;	

private OkHttpClient client;
private HashMap<String, String> httpHeaders = new HashMap<>();
private Long lastCallTime;
private Integer numOfCalls;
private Integer numOfSuccessfulCalls;
private Integer waitTime;


//=============================================================================	
/*
* 	CONSTRUCTOR METHODS (public)
*/

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
private ScraperHttpClient() {
	this.client = new OkHttpClient();
	this.lastCallTime = 0L;
	this.numOfCalls = 0;
	this.numOfSuccessfulCalls = 0;
	this.httpHeaders.put("User-Agent", "Mumdag/0.0.9 (wagenge@gmail.com)");
	this.waitTime = 100;
}

//-----------------------------------------------------------------------------	

//ERROR HANDLING:	nok
//DOC:				nok
//TEST:				nok
public static synchronized ScraperHttpClient getInstance() {
	if (instance == null) {
		instance = new ScraperHttpClient();
	}
	return instance;
}


//=============================================================================	
/*
* 	PUBLIC METHODS (public)
*/

//ERROR HANDLING:	ok
//DOC:				nok
//TEST:				ok
//PARAMERTIZATION:  nok, (waitTime)
public HashMap<String, String> run(String url) {
	Response response = null;
    Request request;
	HashMap<String, String> responseMap = new HashMap<>();

	try {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.headers(Headers.of(this.httpHeaders));
        request = requestBuilder.build();
    }
    catch(Exception ex) {
        responseMap.put("rspBody", ex.getMessage());
        responseMap.put("rspCode", "500");
        return responseMap;
    }

	int retry = 0;
	while(retry < 10) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastCallTime < this.waitTime) {
            long sleepTime = this.waitTime - (currentTimeMillis - this.lastCallTime);
            log.info("currentTimeMillis='{}', lastCallTime='{}', sleepTime='{}', waitTime='{}'", currentTimeMillis, this.lastCallTime, sleepTime, this.waitTime);
            try {
                Thread.sleep(sleepTime);
            } catch (Exception ex) {
                log.warn("exception while trying to sleep!\nError: ", ex.getMessage());
            }
            log.info("ready sleeping");
        }

        try {
            response = this.client.newCall(request).execute();
            this.lastCallTime = System.currentTimeMillis();
            this.numOfCalls++;

            log.info("retry='{}', rspCode='{}", retry, response.code());
            if (response.code() == 200) {
                responseMap.put("rspBody", response.body().string());
                responseMap.put("rspCode", String.valueOf(response.code()));
                this.numOfSuccessfulCalls++;
                break;
            } else if (response.code() == 503) {
                retry++;
                this.waitTime = this.waitTime + (100 * retry);
            } else {
                responseMap.put("rspBody", response.body().string());
                responseMap.put("rspCode", String.valueOf(response.code()));
                break;
            }
        } catch (Exception ex) {
            if(response != null && response.code() != 200 || response == null) {
                responseMap.put("rspBody", ex.getMessage());
                responseMap.put("rspCode", "500");
                return responseMap;
            }
        }
    }

    if (responseMap.get("rspCode").equals("200")) {
        this.waitTime = 100;
    } else {
        log.warn("could not get any response!");
    }
	return responseMap;
}


//=============================================================================
/*
 * 	GETTER/SETTER METHODS (public)
 */

public Integer getNumOfCalls() {
    return numOfCalls;
}

//-----------------------------------------------------------------------------

public void setNumOfCalls(Integer numOfCalls) {
    this.numOfCalls = numOfCalls;
}

//-----------------------------------------------------------------------------

public Integer getNumOfSuccessfulCalls() {
    return numOfSuccessfulCalls;
}

//-----------------------------------------------------------------------------

public void setNumOfSuccessfulCalls(Integer numOfSuccessfulCalls) {
    this.numOfSuccessfulCalls = numOfSuccessfulCalls;
}

//-----------------------------------------------------------------------------

}
