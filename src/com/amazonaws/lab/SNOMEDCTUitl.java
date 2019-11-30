package com.amazonaws.lab;

import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jayway.jsonpath.JsonPath;

public class SNOMEDCTUitl {
	static final Logger log = LogManager.getLogger(SNOMEDCTUitl.class);
	/** The base url. */
	private static final String baseUrl = "https://browser.ihtsdotools.org/snowstorm/snomed-ct/v2/browser/MAIN";

	/** The edition. */
	private static final String edition = "en-edition";

	/** The version. */
	private static final String version = "20180131";


	private static String getUrl() {
		return baseUrl + "/" + edition + "/v" + version;
	}
	public static void main(String []args) throws Exception{
		final Client client = ClientBuilder.newClient();
		//&limit=50&term=left%20knee&conceptActive=true&lang=english&skipTo=0&returnLimit=100
		// SNOMED CT call for condition
		WebTarget target = client
				.target(baseUrl + "/descriptions?"
						+ "&limit=50&term="
						+ URLEncoder.encode("Left foot" , "UTF-8").replaceAll("\\+",
								"%20")
						+"&conceptActive=true&lang=english&skipTo=0&returnLimit=100");
						//+ "&limit=50&searchMode=partialMatching"
						//+ "&lang=english&statusFilter=activeOnly&skipTo=0" + "&returnLimit=100&normalize=true");

		Response response = target.request(MediaType.APPLICATION_JSON).get();
		String resultString = response.readEntity(String.class);
		log.debug("The result string : " + resultString);
		List<String> list = JsonPath.read(resultString, "$..conceptId");
		log.debug("The concept id "+list.get(0));
	}

}
