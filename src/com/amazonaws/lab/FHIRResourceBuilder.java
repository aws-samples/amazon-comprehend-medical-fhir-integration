package com.amazonaws.lab;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContextComponent;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.google.common.net.HttpHeaders;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import ca.uhn.fhir.context.FhirContext;


public class FHIRResourceBuilder {

	static final Logger log = LogManager.getLogger(FHIRResourceBuilder.class);
	/** The base url. */
	private final String baseUrl = "https://browser.ihtsdotools.org/snowstorm/snomed-ct/v2/browser/MAIN";

	/** The edition. */
	private final String edition = "en-edition";

	/** The version. */
	private final String version = "20180131";

	private final boolean invokeSNOMED = true;

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

	static final String MEDIA_TYPE_FHIR_JSON = "application/fhir+json";

	// private static final String FHIR_API_ENDPOINT =
	// System.getenv("FHIR_API_ENDPOINT");
	private static final String FHIR_API_ENDPOINT = System.getenv("FHIR_API_ENDPOINT");

	private static FhirContext fhirContext = FhirContext.forDstu3();
	
	private static String COGNITO_SECRET_NAME =  System.getenv("COGNITO_SECRET_NAME");
	
	private static String timeZoneId = System.getenv("FHIR_TIME_ZONE");
	
	
	private AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

	/**
	 * Method to build FHIR resources from CM output. It takes a transaction id to
	 * extract structured and unstructured data from DynamoDB.
	 * 
	 */
	public Map<String, String> handleRequest(Map<String, String> map, Context context) throws Exception {
		/**
		 * The following steps can be used: 1. Extract records from DynamoDB. 2.
		 * Identify the patient, observation and document reference resources. 3.
		 */

		/**
		 * sample data set "input": 
		 * { "S3Bucket":
		 * "fhir-cm-integration-healthinfobucket-n9y1hwni4kfd", 
		 * "UnstructuredText":
		 * "processing/unstructuredtext/mdm-02.txt-b7ea2ab0-e38f-4ee4-bad4-9bfc7c0a1fb0",
		 * "FileName": "mdm-02.txt-b7ea2ab0-e38f-4ee4-bad4-9bfc7c0a1fb0", 
		 * "DataType":
		 * "hl7", 
		 * "InputFile":
		 * "processing/input/hl7/mdm-02.txt-b7ea2ab0-e38f-4ee4-bad4-9bfc7c0a1fb0" }
		 */

		// check file type - HL7 or FHIR
		String fileType = map.get("DataType");
		String cmOutputKey = map.get("CMOutput");
		String s3Bucket = map.get("S3Bucket");
		
		String fileKey = map.get("InputFile");
		String unstrTextKey = map.get("UnstructuredText");
		
		Map<String,String> response = new HashMap<String,String>();
		

		if (fileType.equals("fhir")) {
			
			String fhirContent = s3Client.getObjectAsString(s3Bucket, fileKey);
			//parse the document as DocumentRefernce 
			//get the patient id
			//get the patient from FHIR repo
			//add the observation and update.
			
			//Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, fhirContent);
			//Patient pat = this.getPatientResourceFromBundle(bundle);
			
			DocumentReference document = fhirContext.newJsonParser().parseResource(DocumentReference.class,fhirContent);
			DocumentReferenceContextComponent refCom = document.getContext();
			String onsetTime = "";
			if(refCom!=null && refCom.getPeriod() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
				sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
				onsetTime = sdf.format(refCom.getPeriod().getStart());
			}
			log.debug("The onset time : "+onsetTime);
			String patientRef = document.getSubject().getReferenceElement().getIdPart().substring(9);
			
			// get CM Output

			String cmContent = s3Client.getObjectAsString(s3Bucket, cmOutputKey);
			CMData cmData = CMUtil.parseCMOutput(cmContent);
			
			log.debug("The patient id : "+patientRef);
			ArrayList<Condition> condList = this.buildResource(cmData, patientRef,onsetTime);
			
			Patient pat = this.getPatientFromFHIRRepo(patientRef);
			
			Bundle bundle = new Bundle();
			bundle.setType(BundleType.COLLECTION);
			BundleEntryComponent patComp = new BundleEntryComponent();
			// cond.setSubject(value)
			patComp.setResource(pat);
			//patComp.setFullUrl("http://hapi.fhir.org/baseDstu3/Patient/" + pat.getId());
			
			//THIS IS IMPORTANT 
			patComp.setFullUrl("urn:uuid:"+pat.getIdElement().getIdPart());
			log.debug("The ID part : "+patComp.getFullUrl() + "-"+pat.getIdElement().getIdPart());
			//add the additional Condition resource
			bundle.addEntry(patComp);
			
			for (Condition cond : condList) {
				BundleEntryComponent bundComp = new BundleEntryComponent();
				// cond.setSubject(value)
				bundComp.setResource(cond);
				
				bundComp.setFullUrl("http://hapi.fhir.org/baseDstu3/Condition/" + cond.getId());
				//add the additional Condition resource
				bundle.addEntry(bundComp);
			}
			//send the updated bundle to the fhir server
			String patJson = fhirContext.newJsonParser().encodeResourceToString(bundle);
			log.debug("The updated bundle entry : "+ patJson);
			
			//update the patient record with the additional Condition resources
			updatePatientInFHIRRepo(patJson);				
			
			response.put("Status", FHIRLoadResponseCodes.SUCCESS);
			

		} else if (fileType.equals("hl7")) {
			// implement code to build FHIR resource from the HL7 unstructured data
			// get the Patient based on identifiers

			// call the FHIR server to get the Patient resource based on the MRN and other
			// search fields
			String hl7UnsData = s3Client.getObjectAsString(s3Bucket, unstrTextKey);
			log.debug("The unstructured text "+hl7UnsData);
			// get the search fields. last Name, first Name, birth date, mrn
			Object document = Configuration.builder().build().jsonProvider().parse(hl7UnsData);
			String dateOfBirth = JsonPath.read(document, "$.dateOfBirth");
			String lastName = JsonPath.read(document, "$.lastName");
			String firstName = JsonPath.read(document, "$.firstName");
			String gender = JsonPath.read(document, "$.gender");
			String mrnVal = JsonPath.read(document, "$.MRN");
			String addressCity = JsonPath.read(document, "$.addressCity");
			String activityDateTime = JsonPath.read(document,"$.activityDateTime");
			

			Bundle patBundle = getPatientBundleFromFHIRRepo(gender, dateOfBirth, lastName, firstName, mrnVal, addressCity);
			// create a Bundle with the Patient and the condition resources
			List<BundleEntryComponent> patList = patBundle.getEntry();
			Patient pat = null;
			if(patList.size()>0) {
				log.debug("Patient found.. ");
				//assuming we will only get one patient
				BundleEntryComponent entry = patList.get(0);
				log.debug("The full entry URL from patient search : "+entry.getFullUrl());
				pat = (Patient) entry.getResource();
				// get CM Output

				String cmContent = s3Client.getObjectAsString(s3Bucket, cmOutputKey);
				log.debug("Comprehend medical output : "+cmContent);
				//need to wrap it with Entity tag.. I am not using the CM objects for Entities to filter out the negations.
				//TO DO : use the CM entity object model to make it more efficient.

				
				CMData cmData = CMUtil.parseCMOutput(cmContent);
				log.debug("The patient id :"+pat.getId());
				//ArrayList<Condition> condList = this.buildResource(cmData, pat.getId());
				ArrayList<Condition> condList = this.buildResource(cmData, entry.getFullUrl(),activityDateTime);
				//ArrayList<BundleEntryComponent> entryList = new ArrayList<>();

				for (Condition cond : condList) {
					BundleEntryComponent bundComp = new BundleEntryComponent();
					// cond.setSubject(value)
					bundComp.setResource(cond);
					bundComp.setFullUrl("http://hapi.fhir.org/baseDstu3/Condition/" + cond.getId());
					//entryList.add(bundComp);
					// append the additional entries derived from CM
					patBundle.addEntry(bundComp);

				}
				//patBundle.setEntry(entryList);
				//List<BundleEntryComponent> oldEntList = bundle.getEntry();
				//oldEntList.addAll(entryList);
				String patJson = fhirContext.newJsonParser().encodeResourceToString(patBundle);
				log.debug("The updated bundle entry : "+ patJson);
				
				//update the patient record with the additional Condition resources
				updatePatientInFHIRRepo(patJson);
				response.put("Status", FHIRLoadResponseCodes.SUCCESS);
				
			}else {
				log.debug("Patient not found!");
				response.put("Status", FHIRLoadResponseCodes.PATIENT_NOT_FOUND);
			}

		}
	

		return response;

	}

	private Patient getPatientResourceFromBundle(Bundle bundle) {
		List<BundleEntryComponent> bundCompList = bundle.getEntry();
		Patient pat = null;
		for (BundleEntryComponent comp : bundCompList) {
			Resource res = comp.getResource();

			if (res.fhirType() == ResourceType.PATIENT.getDisplay()) {
				pat = (Patient) res;
				break;
			}
		}
		return pat;
	}
	private Patient getPatientFromFHIRRepo(String patientId) throws ParseException {
		
		log.debug("Input values :" + patientId);



		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(FHIR_API_ENDPOINT).path("Patient").path(patientId);
				
		
		//set Authentication header
		String idToken = getCognitoIDToken();

		Invocation.Builder invocationBuilder = target.request("application/fhir+json");
		invocationBuilder.header(HttpHeaders.AUTHORIZATION, idToken);

		Response response = invocationBuilder.get();
		
		String patData = response.readEntity(String.class);
		log.debug("The FHIR response : "+patData);
		Patient patient = fhirContext.newJsonParser().parseResource(Patient.class, patData);
		return patient;

	}
	
	private Bundle getPatientBundleFromFHIRRepo(String gender, String birthDate, String lastName, String firstName,
			String mrnVal, String addressCity) throws ParseException {
		
		log.debug("Input values :" + gender + "-" + birthDate + "-" + lastName + "-" + firstName + "-" + mrnVal+ "-" + addressCity);

		// format date of birth
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String fhirBirthDate = sdf.format(new SimpleDateFormat("yyyymmdd").parse(birthDate));

		String fhirGender = (gender.equals("M") ? "male" : "female");

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(FHIR_API_ENDPOINT).path("Patient").path("_search")
				.queryParam("gender", fhirGender).queryParam("birthDate", fhirBirthDate)
				.queryParam("lastName", lastName).queryParam("firstName", firstName)
				.queryParam("address-city", addressCity).queryParam("MRN", mrnVal);
		
		//set Authentication header
		String idToken = getCognitoIDToken();

		Invocation.Builder invocationBuilder = target.request("application/fhir+json");
		invocationBuilder.header(HttpHeaders.AUTHORIZATION, idToken);

		Response response = invocationBuilder.get();
		
		String bundleData = response.readEntity(String.class);
		log.debug("The FHIR response : "+bundleData);
		Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, bundleData);
		return bundle;

	}
	private void updatePatientInFHIRRepo(String requestJSON) throws ParseException {
		
		log.debug("Input values :" + requestJSON);

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(FHIR_API_ENDPOINT).path("Bundle");
		
		//set Authentication header
		String idToken = getCognitoIDToken();

		Invocation.Builder invocationBuilder = target.request("application/fhir+json");
		
		invocationBuilder.header(HttpHeaders.AUTHORIZATION, idToken);
		invocationBuilder.header(HttpHeaders.CONTENT_TYPE, "application/fhir+json");
				
		
		Response response = invocationBuilder.post(javax.ws.rs.client.Entity.entity(requestJSON, "application/fhir+json"));
		
		String bundleData = response.readEntity(String.class);
		log.debug("The FHIR response : "+bundleData);
	

	}
	public String getCognitoIDToken() {

	
		AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
		
		AWSSecretsManager client = clientBuilder.build();
		
		GetSecretValueRequest secValReq = new GetSecretValueRequest()
				.withSecretId(COGNITO_SECRET_NAME);
		
		GetSecretValueResult secValRes = client.getSecretValue(secValReq);
		
		String cognitoProps = secValRes.getSecretString();
		
		log.debug("The secret value : "+secValRes.getSecretString());
		String userName = JsonPath.read(cognitoProps, "$.username");
		String password = JsonPath.read(cognitoProps, "$.password");
		String cognitoClientid = JsonPath.read(cognitoProps, "$.client-id");
		
		Map<String,String> initialParams = new HashMap<String,String>();
		log.debug("The user id : "+userName);
		initialParams.put("USERNAME", userName);
		initialParams.put("PASSWORD", password);
		

		InitiateAuthRequest initiateAuth = new InitiateAuthRequest()
				.withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
				.withAuthParameters(initialParams)
				.withClientId(cognitoClientid);
		

		//AdminInitiateAuthResult initialResponse = cognitoClient.initiateAuth(initiateAuth);
		
		InitiateAuthResult authRes = cognitoClient.initiateAuth(initiateAuth);
		return authRes.getAuthenticationResult().getIdToken();
	}
	
	public static void main(String []args) {
		FHIRResourceBuilder bldr = new FHIRResourceBuilder();
		//log.debug("The token is : "+bldr.getCognitoIDToken());
		String cognitoProps = "{\n" + 
				"    \"username\": \"cm-user\",\n" + 
				"    \"password\": \"Master123!\",\n" + 
				"    \"client-id\": \"5alfhbgd8plgvdv4i3e5abpu08\"\n" + 
				"}";
		
		//log.debug("The secret value : "+secValRes.getSecretString());

		String docRef = "{\n" + 
				"	\"resourceType\": \"DocumentReference\",\n" + 
				"	\"id\": \"19fba9bf-516e-4d1c-9af6-522c86788fe3\",\n" + 
				"	\"meta\": {\n" + 
				"		\"versionId\": \"1\",\n" + 
				"		\"lastUpdated\": \"2019-03-24T17:41:54.335+00:00\",\n" + 
				"		\"security\": [\n" + 
				"			{\n" + 
				"				\"system\": \"http://hl7.org/fhir/v3/Confidentiality\",\n" + 
				"				\"code\": \"R\"\n" + 
				"			}\n" + 
				"		]\n" + 
				"	},\n" + 
				"	\"status\": \"current\",\n" + 
				"	\"type\": {\n" + 
				"		\"coding\": [\n" + 
				"			{\n" + 
				"				\"system\": \"http://clinfhir.com/docs\",\n" + 
				"				\"code\": \"builderDoc\"\n" + 
				"			}\n" + 
				"		]\n" + 
				"	},\n" + 
				"	\"subject\": {\n" + 
				"		\"reference\": \"urn:uuid:<<REPLACE_WITH_PATIENT_ID>>\"\n" + 
				"	},\n" + 
				"	\"class\": {\n" + 
				"		\"coding\": [\n" + 
				"			{\n" + 
				"				\"system\": \"http://clinfhir.com/fhir/CodeSystem/LibraryCategories\",\n" + 
				"				\"code\": \"default\",\n" + 
				"				\"display\": \"Default\"\n" + 
				"			}\n" + 
				"		]\n" + 
				"	},\n" + 
				"	\"indexed\": \"2019-03-24T17:41:53+00:00\",\n" + 
				"	\"author\": [\n" + 
				"		{\n" + 
				"			\"display\": \"dummy@outlook.com\"\n" + 
				"		}\n" + 
				"	],\n" + 
				"	\"description\": \"ProviderClinicalNotes\",\n" + 
				"	\"content\": [\n" + 
				"		{\n" + 
				"			\"attachment\": {\n" + 
				"				\"data\": \"SElTVE9SWSBPRiBQUkVTRU5UIElMTE5FU1M6IFRoaXMgNTkteWVhci1vbGQgd2hpdGUgbWFsZSBpcyBzZWVuIGZvciBjb21wcmVoZW5zaXZlIGFubnVhbCBoZWFsdGggbWFpbnRlbmFuY2UgZXhhbWluYXRpb24gb24gMDIvMTkvMDgsIGFsdGhvdWdoIHRoaXMgcGF0aWVudCBpcyBpbiBleGNlbGxlbnQgb3ZlcmFsbCBoZWFsdGguIE1lZGljYWwgcHJvYmxlbXMgaW5jbHVkZSBjaHJvbmljIHRpbm5pdHVzIGluIHRoZSBsZWZ0IGVhciB3aXRoIG1vZGVyYXRlIGhlYXJpbmcgbG9zcyBmb3IgbWFueSB5ZWFycyB3aXRob3V0IGFueSByZWNlbnQgY2hhbmdlLCBkeXNsaXBpZGVtaWEgd2VsbCBjb250cm9sbGVkIHdpdGggbmlhY2luLCBoaXN0b3J5IG9mIGhlbW9ycmhvaWRzIHdpdGggb2NjYXNpb25hbCBleHRlcm5hbCBibGVlZGluZywgYWx0aG91Z2ggbm8gcHJvYmxlbXMgaW4gdGhlIGxhc3QgNiBtb250aHMsIGFuZCBhbHNvIGhpc3Rvcnkgb2YgY29uY2hhIGJ1bGxvc2Egb2YgdGhlIGxlZnQgbm9zdHJpbCwgZm9sbG93ZWQgYnkgRU5UIGFzc29jaWF0ZWQgd2l0aCBzbGlnaHQgc2VwdGFsIGRldmlhdGlvbi4gVGhlcmUgYXJlIG5vIG90aGVyIG1lZGljYWwgcHJvYmxlbXMuIEhlIGhhcyBubyBzeW1wdG9tcyBhdCB0aGlzIHRpbWUgYW5kIHJlbWFpbnMgaW4gZXhjZWxsZW50IGhlYWx0aC4NCg0KUEFTVCBNRURJQ0FMIEhJU1RPUlk6IE90aGVyd2lzZSBub25jb250cmlidXRvcnkuIFRoZXJlIGlzIG5vIG9wZXJhdGlvbiwgc2VyaW91cyBpbGxuZXNzIG9yIGluanVyeSBvdGhlciB0aGFuIGFzIG5vdGVkIGFib3ZlLg0KDQpBTExFUkdJRVM6IFRoZXJlIGFyZSBubyBrbm93biBhbGxlcmdpZXMuDQoNCkZBTUlMWSBISVNUT1JZOiBGYXRoZXIgZGllZCBvZiBhbiBNSSBhdCBhZ2UgNjcgd2l0aCBDT1BEIGFuZCB3YXMgYSBoZWF2eSBzbW9rZXIuIEhpcyBtb3RoZXIgaXMgODgsIGxpdmluZyBhbmQgd2VsbCwgc3RhdHVzIHBvc3QgbHVuZyBjYW5jZXIgcmVzZWN0aW9uLiBUd28gYnJvdGhlcnMsIGxpdmluZyBhbmQgd2VsbC4gT25lIHNpc3RlciBkaWVkIGF0IGFnZSAyMCBtb250aHMgb2YgcG5ldW1vbmlhLg0KDQpTT0NJQUwgSElTVE9SWTogVGhlIHBhdGllbnQgaXMgbWFycmllZC4gV2lmZSBpcyBsaXZpbmcgYW5kIHdlbGwuIEhlIGpvZ3Mgb3IgZG9lcyBDcm9zcyBDb3VudHJ5IHRyYWNrIDUgdGltZXMgYSB3ZWVrLCBhbmQgd2VpZ2h0IHRyYWluaW5nIHR3aWNlIHdlZWtseS4gTm8gc21va2luZyBvciBzaWduaWZpY2FudCBhbGNvaG9sIGludGFrZS4gSGUgaXMgYSBwaHlzaWNpYW4gaW4gYWxsZXJneS9pbW11bm9sb2d5Lg0KDQpSRVZJRVcgT0YgU1lTVEVNUzogT3RoZXJ3aXNlIG5vbmNvbnRyaWJ1dG9yeS4gSGUgaGFzIG5vIGdhc3Ryb2ludGVzdGluYWwsIGNhcmRpb3B1bG1vbmFyeSwgZ2VuaXRvdXJpbmFyeSBvciBtdXNjdWxvc2tlbGV0YWwgc3ltcHRvbWF0b2xvZ3kuIE5vIHN5bXB0b21zIG90aGVyIHRoYW4gYXMgZGVzY3JpYmVkIGFib3ZlLg0KDQpQSFlTSUNBTCBFWEFNSU5BVElPTjoNCkdFTkVSQUw6IEhlIGFwcGVhcnMgYWxlcnQsIG9yaWVudGVkLCBhbmQgaW4gbm8gYWN1dGUgZGlzdHJlc3Mgd2l0aCBleGNlbGxlbnQgY29nbml0aXZlIGZ1bmN0aW9uLiBWSVRBTCBTSUdOUzogSGlzIGhlaWdodCBpcyA2IGZlZXQgMiBpbmNoZXMsIHdlaWdodCBpcyAxODEuMiwgYmxvb2QgcHJlc3N1cmUgaXMgMTI2LzgwIGluIHRoZSByaWdodCBhcm0sIDEyMi83OCBpbiB0aGUgbGVmdCBhcm0sIHB1bHNlIHJhdGUgaXMgNjggYW5kIHJlZ3VsYXIsIGFuZCByZXNwaXJhdGlvbnMgYXJlIDE2LiBTS0lOOiBXYXJtIGFuZCBkcnkuIFRoZXJlIGlzIG5vIHBhbGxvciwgY3lhbm9zaXMgb3IgaWN0ZXJ1cy4gSEVFTlQ6IFR5bXBhbmljIG1lbWJyYW5lcyBiZW5pZ24uIFRoZSBwaGFyeW54IGlzIGJlbmlnbi4gTmFzYWwgbXVjb3NhIGlzIGludGFjdC4gUHVwaWxzIGFyZSByb3VuZCwgcmVndWxhciwgYW5kIGVxdWFsLCByZWFjdGluZyBlcXVhbGx5IHRvIGxpZ2h0IGFuZCBhY2NvbW1vZGF0aW9uLiBFT00gaW50YWN0LiBGdW5kaSByZXZlYWwgZmxhdCBkaXNjcyB3aXRoIGNsZWFyIG1hcmdpbnMuIE5vcm1hbCB2YXNjdWxhdHVyZS4gTm8gaGVtb3JyaGFnZXMsIGV4dWRhdGVzIG9yIG1pY3JvYW5ldXJ5c21zLiBObyB0aHlyb2lkIGVubGFyZ2VtZW50LiBUaGVyZSBpcyBubyBseW1waGFkZW5vcGF0aHkuIExVTkdTOiBDbGVhciB0byBwZXJjdXNzaW9uIGFuZCBhdXNjdWx0YXRpb24uIE5vcm1hbCBzaW51cyByaHl0aG0uIE5vIHByZW1hdHVyZSBiZWF0LCBtdXJtdXIsIFMzIG9yIFM0LiBIZWFydCBzb3VuZHMgYXJlIG9mIGdvb2QgcXVhbGl0eSBhbmQgaW50ZW5zaXR5LiBUaGUgY2Fyb3RpZHMsIGZlbW9yYWxzLCBkb3JzYWxpcyBwZWRpcywgYW5kIHBvc3RlcmlvciB0aWJpYWwgcHVsc2F0aW9ucyBhcmUgYnJpc2ssIGVxdWFsLCBhbmQgYWN0aXZlIGJpbGF0ZXJhbGx5LiBBQkRPTUVOOiBCZW5pZ24gd2l0aG91dCBndWFyZGluZywgcmlnaWRpdHksIHRlbmRlcm5lc3MsIG1hc3Mgb3Igb3JnYW5vbWVnYWx5LiBORVVST0xPR0lDOiBHcm9zc2x5IGludGFjdC4gRVhUUkVNSVRJRVM6IE5vcm1hbC4gR1U6IEdlbml0YWxpYSBub3JtYWwuIFRoZXJlIGFyZSBubyBpbmd1aW5hbCBoZXJuaWFzLiBUaGVyZSBhcmUgbWlsZCBoZW1vcnJob2lkcyBpbiB0aGUgYW5hbCBjYW5hbC4gVGhlIHByb3N0YXRlIGlzIHNtYWxsLCBpZiBhbnkgbm9ybWFsIHRvIG1pbGRseSBlbmxhcmdlZCB3aXRoIGRpc2NyZXRlIG1hcmdpbnMsIHN5bW1ldHJpY2FsIHdpdGhvdXQgc2lnbmlmaWNhbnQgcGFscGFibGUgYWJub3JtYWxpdHkuIFRoZXJlIGlzIG5vIHJlY3RhbCBtYXNzLiBUaGUgc3Rvb2wgaXMgSGVtb2NjdWx0IG5lZ2F0aXZlLg0KDQpJTVBSRVNTSU9OOg0KMS4gQ29tcHJlaGVuc2l2ZSBhbm51YWwgaGVhbHRoIG1haW50ZW5hbmNlIGV4YW1pbmF0aW9uLg0KMi4gRHlzbGlwaWRlbWlhLg0KMy4gVGlubml0dXMsIGxlZnQgZWFyLg0KNC4gSGVtb3JyaG9pZHMuDQoNClBMQU46IEF0IHRoaXMgdGltZSwgY29udGludWUgbmlhY2luIDEwMDAgbWcgaW4gdGhlIG1vcm5pbmcsIDUwMCBtZyBhdCBub29uLCBhbmQgMTAwMCBtZyBpbiB0aGUgZXZlbmluZzsgYXNwaXJpbiA4MSBtZyBkYWlseTsgbXVsdGl2aXRhbWluczsgdml0YW1pbiBFIDQwMCB1bml0cyBkYWlseTsgYW5kIHZpdGFtaW4gQyA1MDAgbWcgZGFpbHkuIENvbnNpZGVyIGFkZGluZyBseWNvcGVuZSwgc2VsZW5pdW0sIGFuZCBmbGF4c2VlZCB0byBoaXMgcmVnaW1lbi4gQWxsIGFwcHJvcHJpYXRlIGxhYnMgd2lsbCBiZSBvYnRhaW5lZCB0b2RheS4gRm9sbG93dXAgZmFzdGluZyBsaXBpZCBwcm9maWxlIGFuZCBBTFQgaW4gNiBtb250aHMuIA==\"\n" + 
				"			}\n" + 
				"		}\n" + 
				"	],\n" + 
				"	\"context\": {\n" + 
				"		\"period\": {\n" + 
				"			\"start\": \"2004-12-23T08:00:00+11:00\",\n" + 
				"			\"end\": \"2004-12-23T08:01:00+11:00\"\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n" ;
				
		
		DocumentReference document = fhirContext.newJsonParser().parseResource(DocumentReference.class,docRef);
		String patientRef = document.getSubject().getReferenceElement().getIdPart();
		int index = patientRef.indexOf("urn:uuid:");
		log.debug("The id "+patientRef.substring(8));
		
		Date date = document.getContext().getPeriod().getStart();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		log.debug(sdf.format(date));
	}

	/**
	 * Returns the url.
	 *
	 * @return the url
	 */
	private String getUrl() {
		return baseUrl + "/" + edition + "/v" + version;
	}

	public ArrayList<Condition> buildResource(CMData cmData, String patientRefId, String activityDatetime) throws Exception {
		String cmJSON = cmData.getRawCMOutput();

		log.debug("The CM json : "+cmJSON);
		Object document = Configuration.builder().build().jsonProvider().parse(cmJSON);
		ArrayList<Condition> condList = new ArrayList<Condition>();

		List<Integer> condIdList = JsonPath.read(document,
				"$..entities[?(@.category == 'MEDICAL_CONDITION' && @.type =='DX_NAME' && @.traits[0].name!='NEGATION' && @.traits[1].name!='NEGATION')].id");

		// List<Integer> condIdList =
		// JsonPath.read(document, "$..Entities[?(@.Category == 'MEDICAL_CONDITION')
		// ].Id");

		for (Integer entityId : condIdList) {
			//log.debug("The entity id is " + entityId);

			EntitySearchResultDTO dto = getConditionAndBodySite(entityId.toString(), cmData);
			String condition = dto.getCondText();
			String bodyText = dto.getBodySite();
			//log.debug("The enity text is " + dto.getCondText());
			//log.debug("The body text is " + dto.getBodySite());

			if (invokeSNOMED) {
				final Client client = ClientBuilder.newClient();

				// SNOMED CT call for condition

				WebTarget target = client
						.target(baseUrl + "/descriptions?"
								+ "&limit=50&term="
								+ URLEncoder.encode(condition == null ? "" : condition, "UTF-8").replaceAll("\\+",
										"%20")
								+"&conceptActive=true&lang=english&skipTo=0&returnLimit=100");

				Response response = target.request(MediaType.APPLICATION_JSON).get();
				String resultString = response.readEntity(String.class);
				log.debug("The result string : " + resultString);
				//log.debug(JsonPath.read(resultString, "$..matches[0].conceptId").toString());
				//List<String> list = JsonPath.read(resultString, "$..matches[0].conceptId");
				List<String> list = JsonPath.read(resultString, "$..conceptId");
				String condNnomedCode = "";
				if (list != null && list.size() > 0) {
					condNnomedCode = list.get(0);
				}

				log.debug("The condition snomed code : " + condNnomedCode);
				String bodySnomedCode = "";
				if (bodyText != null) {
					// SNOMED CT call for body site
					target = client.target(getUrl() + "/descriptions?query="
							+ URLEncoder.encode(bodyText == null ? "" : bodyText, "UTF-8").replaceAll("\\+", "%20")
							+ "&limit=50&searchMode=partialMatching" + "&lang=english&statusFilter=activeOnly&skipTo=0"
							+ "&returnLimit=100&normalize=true");

					response = target.request(MediaType.APPLICATION_JSON).get();
					resultString = response.readEntity(String.class);
					//log.debug("The result string : " + resultString);
					//log.debug(JsonPath.read(resultString, "$..matches[0].conceptId").toString());
					list = JsonPath.read(resultString, "$..matches[0].conceptId");

					if (list != null && list.size() > 0) {
						bodySnomedCode = list.get(0);
					}

					//log.debug("The body snomed code : " + bodySnomedCode);
				}

				VelocityEngine ve = new VelocityEngine();
				ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
				ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
				
				ve.init();
				Template template = ve.getTemplate("condition.template");

				VelocityContext context = new VelocityContext();
				context.put("Id", UUID.randomUUID().toString());
				context.put("code", condNnomedCode);
				context.put("display", condition);
				context.put("text", condition);
				context.put("onsetDateTime",activityDatetime!=null?activityDatetime:"");
				/* now render the template into a StringWriter */
				StringWriter writer = new StringWriter();
				template.merge(context, writer);

				Condition condResource = FhirContext.forDstu3().newJsonParser().parseResource(Condition.class,
						writer.toString());
				condResource.setSubject(new Reference(patientRefId));

				condResource.addBodySite()
						.addCoding(new Coding().setCode(bodySnomedCode != null ? bodySnomedCode : null));

				condResource.setId(UUID.randomUUID().toString());
				condResource.setClinicalStatus(ConditionClinicalStatus.ACTIVE);
				condResource.setVerificationStatus(ConditionVerificationStatus.CONFIRMED);

				//log.debug("The conditon FHIR resource : " + condResource.getSubject().getReference());
				//log.debug("The conditon FHIR JSON : " + writer.toString());
				condList.add(condResource);

			}
		}

		return condList;

	}

	private EntitySearchResultDTO getConditionAndBodySite(String condEntityId, CMData cmData) {
		List<Entity> entityList = cmData.getEntities();
		boolean idFound = false;
		int index = 0;
		int entListSize = entityList.size();

		String condText;
		String bodySite;
		EntitySearchResultDTO dto = new EntitySearchResultDTO();
		// need a better way to do this.. not very efficient

		while (!idFound) {
			Entity ent = entityList.get(index);
			if (ent.getId().equals(condEntityId)) {
				idFound = true;

				condText = ent.getText();

				dto.setCondText(condText);
				// now look for body site in the last two indexes and next two indexes
				if (index >= 2) {
					Entity tempEnt1 = entityList.get(index - 1);
					Entity tempEnt2 = entityList.get(index - 2);
					if (tempEnt1.getType().equals("SYSTEM_ORGAN_SITE")) {
						bodySite = tempEnt1.getText();
						dto.setBodySite(bodySite);
					} else if (tempEnt2.getType().equals("SYSTEM_ORGAN_SITE")) {
						bodySite = tempEnt2.getText();
						dto.setBodySite(bodySite);
					} else if (index < (entListSize - 2)) {
						tempEnt1 = entityList.get(index + 1);
						tempEnt2 = entityList.get(index + 2);
						if (tempEnt1.getType().equals("SYSTEM_ORGAN_SITE")) {
							bodySite = tempEnt1.getText();
							dto.setBodySite(bodySite);
						} else if (tempEnt2.getType().equals("SYSTEM_ORGAN_SITE")) {
							bodySite = tempEnt2.getText();
							dto.setBodySite(bodySite);

						}
					}
				}
			} else {
				index++;
			}
		}
		return dto;

	}
}
