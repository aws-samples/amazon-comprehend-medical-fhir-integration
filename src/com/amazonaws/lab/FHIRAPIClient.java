package com.amazonaws.lab;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class FHIRAPIClient implements RequestHandler<String, String>{
	static final Logger log = LogManager.getLogger(FHIRAPIClient.class);
	static final String MEDIA_TYPE_FHIR_JSON = "application/fhir+json";
	//private static final String FHIR_API_ENDPOINT = System.getenv("FHIR_API_ENDPOINT");
	private static final String FHIR_API_ENDPOINT = "https://hk22oyafuc.execute-api.us-west-2.amazonaws.com/Prod/";

    @Override
    public String handleRequest(String input, Context context) {
    	Client client = ClientBuilder.newClient();
    	WebTarget target = client.target(FHIR_API_ENDPOINT);
    	WebTarget resourceWebTarget = target.path("metadata");
    	
    	Form form = new Form();
    	//get the Id token from Cognito API
    	form.param("Authorization", "foo");
    	 
    	Invocation.Builder invocationBuilder =
    			resourceWebTarget.request("application/fhir+json");
    	
    	//invocationBuilder.header("Content-Type", "application/fhir+json");
    	//invocationBuilder.accept("application/json");
    	
    	Response response = invocationBuilder.get();
    	
    	log.debug("The response data : \n"+response.readEntity(String.class));
    	
    	//target.request(MEDIA_TYPE_FHIR_JSON)
    	//    .post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE),
    	//        MyJAXBBean.class);
    	return null;
    }
    public static void main(String []args) {
    	FHIRAPIClient client = new FHIRAPIClient();
    	client.handleRequest("test", null);
    }
    
    

}
