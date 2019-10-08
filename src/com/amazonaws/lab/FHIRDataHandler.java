package com.amazonaws.lab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import ca.uhn.fhir.context.FhirContext;

public class FHIRDataHandler {
	static final Logger log = LogManager.getLogger(FHIRDataHandler.class);

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
	
	private static FhirContext fhirContext = FhirContext.forDstu3();
	
	public Map<String, String> handleRequest(Map<String, String> map, Context context) {
		// check file type - HL7 or FHIR
		String fileType = map.get("DataType");
		
		String s3Bucket = map.get("S3Bucket");
		
		String fileKey = map.get("InputFile");
		// Get S3 object as string, then process it
		String fhirInput = s3Client.getObjectAsString(s3Bucket, fileKey);
		DocumentReference docRef = fhirContext.newJsonParser().parseResource(DocumentReference.class, fhirInput);
		String patientRef = docRef.getSubject().getReference();
		
		ArrayList<String> notesList = this.getNotesFromDocument(docRef);
		
		PatientInfo patInfo = new PatientInfo();
		patInfo.setPatientId(patientRef);

	
		patInfo.setNotes(notesList);
		
		String json = new Gson().toJson(patInfo);
		
		putS3ObjectContentAsString(map.get("S3Bucket"), "processing/unstructuredtext/" + map.get("FileName"),
				json.toString());
		
		// Create our output response back to the state machine
		Map<String, String> output = new HashMap<>();
		output.put("S3Bucket", map.get("S3Bucket"));
		output.put("FileName", map.get("FileName"));
		output.put("InputFile", map.get("InputFile"));
		output.put("DataType", map.get("DataType"));
		output.put("UnstructuredText", "processing/unstructuredtext/" + map.get("FileName"));
		return output;
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
	
	public String putS3ObjectContentAsString(String bucketName, String key, String content) {
		try {
			s3Client.putObject(bucketName, key, content);
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			e.printStackTrace();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}
		return "Done";
	}
	
	private ArrayList<String> getNotesFromBundle(Bundle bundle) {
		ArrayList<String> notesList = new ArrayList<String>();
		List<BundleEntryComponent> list = bundle.getEntry();

		for(BundleEntryComponent entry : list) {
			String fhirType = entry.getResource().fhirType();
			
			if(fhirType.equals(ResourceType.DOCUMENTREFERENCE.getDisplay())) {
				DocumentReference docRef = (DocumentReference)entry.getResource();
				log.debug("The document data "+docRef.getDescription());
				List<DocumentReferenceContentComponent> attList = docRef.getContent();
				StringBuffer buffer = new StringBuffer();
				for(DocumentReferenceContentComponent attach:attList) {
					byte[] notesBytes = attach.getAttachment().getData();
					log.debug("The provider clinical :"+new String(notesBytes));
					buffer.append(new String(notesBytes));
				}
				
				notesList.add(buffer.toString());
			}
		}
		return notesList;
		
	}
	
	private ArrayList<String> getNotesFromDocument(DocumentReference docRef) {
		ArrayList<String> notesList = new ArrayList<String>();
			
		log.debug("The document data "+docRef.getDescription());
		List<DocumentReferenceContentComponent> attList = docRef.getContent();
		StringBuffer buffer = new StringBuffer();
		for(DocumentReferenceContentComponent attach:attList) {
			byte[] notesBytes = attach.getAttachment().getData();
			log.debug("The provider clinical :"+new String(notesBytes));
			buffer.append(new String(notesBytes));
		}
		
		notesList.add(buffer.toString());
		return notesList;
		
	}
}
