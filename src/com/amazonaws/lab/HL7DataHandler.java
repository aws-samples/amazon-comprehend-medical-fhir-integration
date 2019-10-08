package com.amazonaws.lab;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.message.MDM_T02;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageStringIterator;

public class HL7DataHandler {
	static final Logger log = LogManager.getLogger(HL7DataHandler.class);

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
	private static String timeZoneId = System.getenv("FHIR_TIME_ZONE");

	private String HL7input;

	/*
	 * /* The HapiContext holds all configuration and provides factory methods for
	 * obtaining all sorts of HAPI objects, e.g. parsers.
	 */
	HapiContext hapiContext = new DefaultHapiContext();

	/**
	 * This method is called by the step functions
	 * 
	 * @param input
	 * @param context
	 * @return
	 */

	public Map<String, String> handleRequest(Map<String, String> map, Context context) {
		// Get S3 object as string, then process it
		HL7input = getS3ObjectContentAsString(map.get("S3Bucket"), map.get("InputFile"));
		log.debug("HL7input: " + HL7input);

		String json = null;
		try {
			/*
			 * A Parser is used to convert between string representations of messages and
			 * instances of HAPI's "Message" object. In this case, we are using a
			 * "GenericParser", which is able to handle both XML and ER7 (pipe & hat)
			 * encodings.
			 */
			Parser p = hapiContext.getGenericParser();
			Message hapiMsg;
			// The parse method performs the actual parsing
			hapiMsg = p.parse(HL7input);

			MDM_T02 mdmMsg = (MDM_T02) hapiMsg;
			// MSH msh = mdmMsg.getMSH();
			PID patId = mdmMsg.getPID();
			//send in a format which can be used in the template
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
			String activityDateTime = (mdmMsg.getTXA().getActivityDateTime() != null
					? sdf.format(mdmMsg.getTXA().getActivityDateTime().getTimeOfAnEvent().getValueAsDate())
					: null);
			mdmMsg.getTXA().getActivityDateTime().getTimeOfAnEvent().getValueAsDate();
			ArrayList<String> notes = new ArrayList<>();

			List<OBX> obxList = mdmMsg.getOBXAll();
			for (OBX obx : obxList) {
				Varies[] dataList = obx.getObservationValue();
				// log.debug("The observation value :"+obx.getObservationValue() + "-- "+data);
				for (Varies data : dataList) {
					log.debug("The observation value :" + data.getData());
					notes.add(data.getData().toString());
				}
			}

			PatientInfo patInfo = new PatientInfo();
			patInfo.setLastName(patId.getPatientName()[0].getFamilyName().getValue());
			patInfo.setFirstName(patId.getPatientName()[0].getGivenName().getValue());
			patInfo.setMiddleInitial(patId.getPatientName()[0].getMiddleInitialOrName().getValue());
			patInfo.setDateOfBirth(patId.getDateOfBirth().getTs1_TimeOfAnEvent().getValue());
			patInfo.setActivityDateTime(activityDateTime);

			patInfo.setAddressCity(patId.getPatientAddress(0).getCity().getValue());
			patInfo.setGender(patId.getSex().getValue());
			patInfo.setMRN(patId.getPid3_PatientIDInternalID(0).getID().getValue());

			// NotesData notesData = new NotesData();
			// notesData.setKey("Notes");
			// notesData.setNotes(notes);
			patInfo.setNotes(notes);
			json = new Gson().toJson(patInfo);
			// log.debug("The data "+data.length);

		} catch (HL7Exception exp) {
			log.error("Unable to parse message " + exp.getMessage());
		}

		// Save the output in a "processing" folder in the S3 bucket
		log.debug("Output S3 path: " + map.get("S3Bucket") + "processing/unstructuredtext/" + map.get("FileName"));
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

	public String parseHL7Payload(String HL7input) {
		String json = null;
		try {
			/*
			 * A Parser is used to convert between string representations of messages and
			 * instances of HAPI's "Message" object. In this case, we are using a
			 * "GenericParser", which is able to handle both XML and ER7 (pipe & hat)
			 * encodings.
			 */
			Parser p = hapiContext.getGenericParser();
			Message hapiMsg;
			// The parse method performs the actual parsing
			hapiMsg = p.parse(HL7input);

			MDM_T02 mdmMsg = (MDM_T02) hapiMsg;
			// MSH msh = mdmMsg.getMSH();
			PID patId = mdmMsg.getPID();
			String activityDateTime = (mdmMsg.getTXA().getActivityDateTime() != null
					? mdmMsg.getTXA().getActivityDateTime().getTimeOfAnEvent().getValueAsDate().toString()
					: null);
			mdmMsg.getTXA().getActivityDateTime().getTimeOfAnEvent().getValueAsDate();
			ArrayList<String> notes = new ArrayList<>();

			List<OBX> obxList = mdmMsg.getOBXAll();
			for (OBX obx : obxList) {
				Varies[] dataList = obx.getObservationValue();
				// log.debug("The observation value :"+obx.getObservationValue() + "-- "+data);
				for (Varies data : dataList) {
					log.debug("The observation value :" + data.getData());
					notes.add(data.getData().toString());
				}
			}

			PatientInfo patInfo = new PatientInfo();
			patInfo.setLastName(patId.getPatientName()[0].getFamilyName().getValue());
			patInfo.setFirstName(patId.getPatientName()[0].getGivenName().getValue());
			patInfo.setMiddleInitial(patId.getPatientName()[0].getMiddleInitialOrName().getValue());
			patInfo.setDateOfBirth(patId.getDateOfBirth().getTs1_TimeOfAnEvent().getValue());
			patInfo.setActivityDateTime(activityDateTime);
			patInfo.setAddressCity(patId.getPatientAddress(0).getCity().getValue());
			patInfo.setGender(patId.getSex().getValue());
			patInfo.setMRN(patId.getPid3_PatientIDInternalID(0).getID().getValue());

			log.debug("The patient city : " + patId.getPatientAddress(0).getCity().getValue());
			log.debug("The patient gender : " + patId.getSex().getValue());
			log.debug("The MRN is : "+patId.getPid3_PatientIDInternalID(0).getID().getValue());

			// NotesData notesData = new NotesData();
			// notesData.setKey("Notes");
			// notesData.setNotes(notes);
			patInfo.setNotes(notes);
			json = new Gson().toJson(patInfo);

		} catch (HL7Exception exp) {
			log.error("Unable to parse message " + exp.getMessage());
		}
		return json;
	}

	public String getS3ObjectContentAsString(String bucketName, String key) {
		try {
			if (key.startsWith("/")) {
				key = key.substring(1);
			}
			if (key.endsWith("/")) {
				key = key.substring(0, key.length());
			}

			try (InputStream is = s3Client.getObject(bucketName, key).getObjectContent()) {
				BufferedInputStream buffStream = new BufferedInputStream(is);
				Hl7InputStreamMessageStringIterator iter = new Hl7InputStreamMessageStringIterator(buffStream);
				String hl7Msg = (iter.hasNext() ? iter.next() : "");

				return hl7Msg;
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
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

	public static void main(String[] args) {
		/**
		
		String hl7 = "MSH|^~\\&|Epic|Epic|||20160510071633||MDM^T02|12345|D|2.3\r"
				+ "PID|1||12983718^^^^EPI||Thomas^Mike^J^^MR.^||19500101|M||AfrAm|506 S. HAMILTON AVE^^MADISON^WI^53505^US^^^DN |DN|(608)123-9998|(608)123-5679||S||18273652|123-45-9999||||^^^WI^^\r"
				+ "PV1|||^^^CARE HEALTH SYSTEMS^^^^^||||||1173^MATTHEWS^JAMES^A^^^||||||||||||\r"
				+ "TXA||CN||20160510071633|1173^MATTHEWS^JAMES^A^^^|||||||^^12345|||||PA|\r"
				+ "OBX|1|TX|||Clinical summary: Based on the information provided, the patient likely has viral sinusitis commonly called a head cold.\r"
				+ "OBX|2|TX|||Diagnosis: Viral Sinusitis\r" + "OBX|3|TX|||Diagnosis ICD: J01.90\r"
				+ "OBX|4|TX|||Prescription: benzonatate (Tessalon Perles) 100mg oral tablet 30 tablets, 5 days supply. Take one to two tablets by mouth three times a day as needed. disp. 30. Refills: 0, Refill as needed: no, Allow substitutions: yes";
		*/
		String hl7= "MSH|^~\\&|Epic|Epic|||20160510071633||MDM^T02|12345|D|2.3\r" + 
				"PID|1||68b1c58d-41cd-4855-a100-8206eb1b61b5^^^^EPI||Larkin917^Monroe732^J^^MR.^||19720817|M||AfrAm|377 Kuhic Station Unit 91^^Sturbridge^MA^01507^US^^^DN |DN|(608)123-9998|(608)123-5679||S||18273652|123-45-9999||||^^^WI^^\r" + 
				"PV1|||^^^CARE HEALTH SYSTEMS^^^^^||||||1173^MATTHEWS^JAMES^A^^^||||||||||||\r" + 
				"TXA||CN||20160510071633|1173^MATTHEWS^JAMES^A^^^|||||||^^12345|||||PA|\r" + 
				"OBX|1|TX|||Clinical summary: Based on the information provided, the patient likely has viral sinusitis commonly called a head cold.\r" + 
				"OBX|2|TX|||Diagnosis: Viral Sinusitis\r" + 
				"OBX|3|TX|||Diagnosis ICD: J01.90\r" + 
				"OBX|4|TX|||Prescription: benzonatate (Tessalon Perles) 100mg oral tablet 30 tablets, 5 days supply. Take one to two tablets by mouth three times a day as needed. disp. 30. Refills: 0, Refill as needed: no, Allow substitutions: yes\r" ; 
				
		log.debug("The hl7 : \n " + hl7);

		HL7DataHandler handler = new HL7DataHandler();
		log.debug("The JSON Object : " + handler.parseHL7Payload(hl7));

	}

}
