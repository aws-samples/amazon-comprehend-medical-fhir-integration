package com.amazonaws.lab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;


import com.google.gson.Gson;

import ca.uhn.fhir.context.FhirContext;

public class CMUtil {
	static final Logger log = LogManager.getLogger(CMUtil.class);
	public CMData readCMOutput(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		StringBuilder result = new StringBuilder("");
		File file = new File(classLoader.getResource(fileName).getFile()); 	
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.debug("The result Data : "+result.toString());
		
		CMData cmData = new Gson().fromJson(result.toString(), CMData.class);
		
		
		ArrayList<Entity> entList = cmData.getEntities();

		cmData.setRawCMOutput(result.toString());
		return cmData;
		//System.out.println("The CM Data : "+cmData.getEntities());
	}
	
	public static CMData parseCMOutput(String cmTextOut) {
		CMData cmData = new Gson().fromJson(cmTextOut, CMData.class);
		
		cmData.setRawCMOutput(cmTextOut);
		
		return cmData;
		
	}
	
	public static void main(String []args) throws Exception {
		String patRefId = UUID.randomUUID().toString();
		CMUtil util = new CMUtil();
		String cmJson = "{\n" + 
				"  \"entities\": [\n" + 
				"    {\n" + 
				"      \"id\": 0,\n" + 
				"      \"beginOffset\": 33,\n" + 
				"      \"endOffset\": 35,\n" + 
				"      \"score\": 0.9999453,\n" + 
				"      \"text\": \"59\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"AGE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 1,\n" + 
				"      \"beginOffset\": 123,\n" + 
				"      \"endOffset\": 131,\n" + 
				"      \"score\": 0.99999833,\n" + 
				"      \"text\": \"02/19/08\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"DATE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 3,\n" + 
				"      \"beginOffset\": 212,\n" + 
				"      \"endOffset\": 219,\n" + 
				"      \"score\": 0.9969548,\n" + 
				"      \"text\": \"chronic\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"ACUITY\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 4,\n" + 
				"      \"beginOffset\": 220,\n" + 
				"      \"endOffset\": 228,\n" + 
				"      \"score\": 0.99824274,\n" + 
				"      \"text\": \"tinnitus\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.537067\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 16,\n" + 
				"      \"beginOffset\": 236,\n" + 
				"      \"endOffset\": 240,\n" + 
				"      \"score\": 0.9966658,\n" + 
				"      \"text\": \"left\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 17,\n" + 
				"      \"beginOffset\": 241,\n" + 
				"      \"endOffset\": 244,\n" + 
				"      \"score\": 0.9957877,\n" + 
				"      \"text\": \"ear\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 5,\n" + 
				"      \"beginOffset\": 259,\n" + 
				"      \"endOffset\": 271,\n" + 
				"      \"score\": 0.96172416,\n" + 
				"      \"text\": \"hearing loss\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SYMPTOM\",\n" + 
				"          \"score\": 0.6525445\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 6,\n" + 
				"      \"beginOffset\": 272,\n" + 
				"      \"endOffset\": 286,\n" + 
				"      \"score\": 0.7779872,\n" + 
				"      \"text\": \"for many years\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"ACUITY\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 7,\n" + 
				"      \"beginOffset\": 314,\n" + 
				"      \"endOffset\": 326,\n" + 
				"      \"score\": 0.8936007,\n" + 
				"      \"text\": \"dyslipidemia\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.40890643\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.41687036\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 2,\n" + 
				"      \"beginOffset\": 348,\n" + 
				"      \"endOffset\": 354,\n" + 
				"      \"score\": 0.9970095,\n" + 
				"      \"text\": \"niacin\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 8,\n" + 
				"      \"beginOffset\": 367,\n" + 
				"      \"endOffset\": 378,\n" + 
				"      \"score\": 0.9964463,\n" + 
				"      \"text\": \"hemorrhoids\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.5564304\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 9,\n" + 
				"      \"beginOffset\": 384,\n" + 
				"      \"endOffset\": 394,\n" + 
				"      \"score\": 0.49522293,\n" + 
				"      \"text\": \"occasional\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"ACUITY\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 18,\n" + 
				"      \"beginOffset\": 395,\n" + 
				"      \"endOffset\": 403,\n" + 
				"      \"score\": 0.57622993,\n" + 
				"      \"text\": \"external\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 10,\n" + 
				"      \"beginOffset\": 395,\n" + 
				"      \"endOffset\": 412,\n" + 
				"      \"score\": 0.6590975,\n" + 
				"      \"text\": \"external bleeding\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SYMPTOM\",\n" + 
				"          \"score\": 0.79117626\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 11,\n" + 
				"      \"beginOffset\": 442,\n" + 
				"      \"endOffset\": 455,\n" + 
				"      \"score\": 0.37607545,\n" + 
				"      \"text\": \"last 6 months\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"ACUITY\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 12,\n" + 
				"      \"beginOffset\": 477,\n" + 
				"      \"endOffset\": 491,\n" + 
				"      \"score\": 0.9812107,\n" + 
				"      \"text\": \"concha bullosa\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.6811055\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 19,\n" + 
				"      \"beginOffset\": 499,\n" + 
				"      \"endOffset\": 503,\n" + 
				"      \"score\": 0.9964132,\n" + 
				"      \"text\": \"left\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 20,\n" + 
				"      \"beginOffset\": 504,\n" + 
				"      \"endOffset\": 511,\n" + 
				"      \"score\": 0.981312,\n" + 
				"      \"text\": \"nostril\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 21,\n" + 
				"      \"beginOffset\": 525,\n" + 
				"      \"endOffset\": 528,\n" + 
				"      \"score\": 0.68136096,\n" + 
				"      \"text\": \"ENT\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 13,\n" + 
				"      \"beginOffset\": 552,\n" + 
				"      \"endOffset\": 568,\n" + 
				"      \"score\": 0.9421524,\n" + 
				"      \"text\": \"septal deviation\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.42075184\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"SYMPTOM\",\n" + 
				"          \"score\": 0.47659907\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 14,\n" + 
				"      \"beginOffset\": 753,\n" + 
				"      \"endOffset\": 760,\n" + 
				"      \"score\": 0.7568253,\n" + 
				"      \"text\": \"illness\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.8166307\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 15,\n" + 
				"      \"beginOffset\": 764,\n" + 
				"      \"endOffset\": 770,\n" + 
				"      \"score\": 0.97756106,\n" + 
				"      \"text\": \"injury\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SYMPTOM\",\n" + 
				"          \"score\": 0.60779715\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.93765074\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 29,\n" + 
				"      \"beginOffset\": 864,\n" + 
				"      \"endOffset\": 868,\n" + 
				"      \"score\": 0.99243623,\n" + 
				"      \"text\": \"died\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.9696091\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 30,\n" + 
				"      \"beginOffset\": 875,\n" + 
				"      \"endOffset\": 877,\n" + 
				"      \"score\": 0.993504,\n" + 
				"      \"text\": \"MI\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.98300654\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 25,\n" + 
				"      \"beginOffset\": 885,\n" + 
				"      \"endOffset\": 887,\n" + 
				"      \"score\": 0.999987,\n" + 
				"      \"text\": \"67\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"AGE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 31,\n" + 
				"      \"beginOffset\": 893,\n" + 
				"      \"endOffset\": 897,\n" + 
				"      \"score\": 0.99505204,\n" + 
				"      \"text\": \"COPD\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.97489446\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 26,\n" + 
				"      \"beginOffset\": 936,\n" + 
				"      \"endOffset\": 938,\n" + 
				"      \"score\": 0.95869595,\n" + 
				"      \"text\": \"88\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"AGE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 36,\n" + 
				"      \"beginOffset\": 969,\n" + 
				"      \"endOffset\": 973,\n" + 
				"      \"score\": 0.96581995,\n" + 
				"      \"text\": \"lung\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 32,\n" + 
				"      \"beginOffset\": 969,\n" + 
				"      \"endOffset\": 980,\n" + 
				"      \"score\": 0.3556925,\n" + 
				"      \"text\": \"lung cancer\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.9747093\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 22,\n" + 
				"      \"beginOffset\": 969,\n" + 
				"      \"endOffset\": 990,\n" + 
				"      \"score\": 0.7957806,\n" + 
				"      \"text\": \"lung cancer resection\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"PROCEDURE_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 33,\n" + 
				"      \"beginOffset\": 1034,\n" + 
				"      \"endOffset\": 1038,\n" + 
				"      \"score\": 0.9928958,\n" + 
				"      \"text\": \"died\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.9652965\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 27,\n" + 
				"      \"beginOffset\": 1046,\n" + 
				"      \"endOffset\": 1048,\n" + 
				"      \"score\": 0.7329232,\n" + 
				"      \"text\": \"20\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"AGE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 34,\n" + 
				"      \"beginOffset\": 1059,\n" + 
				"      \"endOffset\": 1068,\n" + 
				"      \"score\": 0.9939826,\n" + 
				"      \"text\": \"pneumonia\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.9713571\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 23,\n" + 
				"      \"beginOffset\": 1192,\n" + 
				"      \"endOffset\": 1207,\n" + 
				"      \"score\": 0.35737222,\n" + 
				"      \"text\": \"weight training\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TREATMENT_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 28,\n" + 
				"      \"beginOffset\": 1272,\n" + 
				"      \"endOffset\": 1281,\n" + 
				"      \"score\": 0.86449265,\n" + 
				"      \"text\": \"physician\",\n" + 
				"      \"category\": \"PROTECTED_HEALTH_INFORMATION\",\n" + 
				"      \"type\": \"PROFESSION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 37,\n" + 
				"      \"beginOffset\": 1362,\n" + 
				"      \"endOffset\": 1378,\n" + 
				"      \"score\": 0.8874339,\n" + 
				"      \"text\": \"gastrointestinal\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 38,\n" + 
				"      \"beginOffset\": 1380,\n" + 
				"      \"endOffset\": 1395,\n" + 
				"      \"score\": 0.9388143,\n" + 
				"      \"text\": \"cardiopulmonary\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 39,\n" + 
				"      \"beginOffset\": 1397,\n" + 
				"      \"endOffset\": 1410,\n" + 
				"      \"score\": 0.92832434,\n" + 
				"      \"text\": \"genitourinary\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 40,\n" + 
				"      \"beginOffset\": 1414,\n" + 
				"      \"endOffset\": 1429,\n" + 
				"      \"score\": 0.5807244,\n" + 
				"      \"text\": \"musculoskeletal\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 35,\n" + 
				"      \"beginOffset\": 1414,\n" + 
				"      \"endOffset\": 1444,\n" + 
				"      \"score\": 0.64687026,\n" + 
				"      \"text\": \"musculoskeletal symptomatology\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SYMPTOM\",\n" + 
				"          \"score\": 0.86179745\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.86637676\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 24,\n" + 
				"      \"beginOffset\": 1490,\n" + 
				"      \"endOffset\": 1510,\n" + 
				"      \"score\": 0.30136412,\n" + 
				"      \"text\": \"PHYSICAL EXAMINATION\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 114,\n" + 
				"      \"beginOffset\": 1512,\n" + 
				"      \"endOffset\": 1519,\n" + 
				"      \"score\": 0.99957365,\n" + 
				"      \"text\": \"GENERAL\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 59,\n" + 
				"      \"beginOffset\": 1532,\n" + 
				"      \"endOffset\": 1537,\n" + 
				"      \"score\": 0.9796053,\n" + 
				"      \"text\": \"alert\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9150654\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 60,\n" + 
				"      \"beginOffset\": 1539,\n" + 
				"      \"endOffset\": 1547,\n" + 
				"      \"score\": 0.996287,\n" + 
				"      \"text\": \"oriented\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.94517696\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 61,\n" + 
				"      \"beginOffset\": 1559,\n" + 
				"      \"endOffset\": 1564,\n" + 
				"      \"score\": 0.98810786,\n" + 
				"      \"text\": \"acute\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"ACUITY\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 62,\n" + 
				"      \"beginOffset\": 1565,\n" + 
				"      \"endOffset\": 1573,\n" + 
				"      \"score\": 0.98768115,\n" + 
				"      \"text\": \"distress\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9726757\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9560833\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 63,\n" + 
				"      \"beginOffset\": 1579,\n" + 
				"      \"endOffset\": 1607,\n" + 
				"      \"score\": 0.5254649,\n" + 
				"      \"text\": \"excellent cognitive function\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8997733\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 41,\n" + 
				"      \"beginOffset\": 1609,\n" + 
				"      \"endOffset\": 1620,\n" + 
				"      \"score\": 0.8874825,\n" + 
				"      \"text\": \"VITAL SIGNS\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 42,\n" + 
				"      \"beginOffset\": 1626,\n" + 
				"      \"endOffset\": 1632,\n" + 
				"      \"score\": 0.99600714,\n" + 
				"      \"text\": \"height\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.9921582,\n" + 
				"          \"relationshipScore\": 0.99999845,\n" + 
				"          \"id\": 43,\n" + 
				"          \"beginOffset\": 1636,\n" + 
				"          \"endOffset\": 1637,\n" + 
				"          \"text\": \"6\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_UNIT\",\n" + 
				"          \"score\": 0.9489062,\n" + 
				"          \"relationshipScore\": 0.9999989,\n" + 
				"          \"id\": 44,\n" + 
				"          \"beginOffset\": 1638,\n" + 
				"          \"endOffset\": 1642,\n" + 
				"          \"text\": \"feet\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.9908415,\n" + 
				"          \"relationshipScore\": 0.99999964,\n" + 
				"          \"id\": 45,\n" + 
				"          \"beginOffset\": 1643,\n" + 
				"          \"endOffset\": 1644,\n" + 
				"          \"text\": \"2\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_UNIT\",\n" + 
				"          \"score\": 0.99740016,\n" + 
				"          \"relationshipScore\": 0.99999857,\n" + 
				"          \"id\": 46,\n" + 
				"          \"beginOffset\": 1645,\n" + 
				"          \"endOffset\": 1651,\n" + 
				"          \"text\": \"inches\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 47,\n" + 
				"      \"beginOffset\": 1653,\n" + 
				"      \"endOffset\": 1659,\n" + 
				"      \"score\": 0.99876297,\n" + 
				"      \"text\": \"weight\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.9999553,\n" + 
				"          \"relationshipScore\": 0.999992,\n" + 
				"          \"id\": 48,\n" + 
				"          \"beginOffset\": 1663,\n" + 
				"          \"endOffset\": 1668,\n" + 
				"          \"text\": \"181.2\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 49,\n" + 
				"      \"beginOffset\": 1670,\n" + 
				"      \"endOffset\": 1684,\n" + 
				"      \"score\": 0.9988973,\n" + 
				"      \"text\": \"blood pressure\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.99766326,\n" + 
				"          \"relationshipScore\": 0.9999999,\n" + 
				"          \"id\": 50,\n" + 
				"          \"beginOffset\": 1688,\n" + 
				"          \"endOffset\": 1694,\n" + 
				"          \"text\": \"126/80\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.99576604,\n" + 
				"          \"relationshipScore\": 0.9614466,\n" + 
				"          \"id\": 51,\n" + 
				"          \"beginOffset\": 1713,\n" + 
				"          \"endOffset\": 1719,\n" + 
				"          \"text\": \"122/78\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 115,\n" + 
				"      \"beginOffset\": 1702,\n" + 
				"      \"endOffset\": 1707,\n" + 
				"      \"score\": 0.9240282,\n" + 
				"      \"text\": \"right\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 116,\n" + 
				"      \"beginOffset\": 1708,\n" + 
				"      \"endOffset\": 1711,\n" + 
				"      \"score\": 0.93378705,\n" + 
				"      \"text\": \"arm\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 117,\n" + 
				"      \"beginOffset\": 1727,\n" + 
				"      \"endOffset\": 1731,\n" + 
				"      \"score\": 0.98647517,\n" + 
				"      \"text\": \"left\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 118,\n" + 
				"      \"beginOffset\": 1732,\n" + 
				"      \"endOffset\": 1735,\n" + 
				"      \"score\": 0.98282707,\n" + 
				"      \"text\": \"arm\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 52,\n" + 
				"      \"beginOffset\": 1737,\n" + 
				"      \"endOffset\": 1747,\n" + 
				"      \"score\": 0.962323,\n" + 
				"      \"text\": \"pulse rate\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.99482274,\n" + 
				"          \"relationshipScore\": 0.99999964,\n" + 
				"          \"id\": 53,\n" + 
				"          \"beginOffset\": 1751,\n" + 
				"          \"endOffset\": 1753,\n" + 
				"          \"text\": \"68\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.42661715,\n" + 
				"          \"relationshipScore\": 0.99999535,\n" + 
				"          \"id\": 54,\n" + 
				"          \"beginOffset\": 1758,\n" + 
				"          \"endOffset\": 1765,\n" + 
				"          \"text\": \"regular\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 55,\n" + 
				"      \"beginOffset\": 1771,\n" + 
				"      \"endOffset\": 1783,\n" + 
				"      \"score\": 0.92337286,\n" + 
				"      \"text\": \"respirations\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.99689996,\n" + 
				"          \"relationshipScore\": 0.9999944,\n" + 
				"          \"id\": 56,\n" + 
				"          \"beginOffset\": 1788,\n" + 
				"          \"endOffset\": 1790,\n" + 
				"          \"text\": \"16\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 119,\n" + 
				"      \"beginOffset\": 1792,\n" + 
				"      \"endOffset\": 1796,\n" + 
				"      \"score\": 0.99676347,\n" + 
				"      \"text\": \"SKIN\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 64,\n" + 
				"      \"beginOffset\": 1798,\n" + 
				"      \"endOffset\": 1802,\n" + 
				"      \"score\": 0.9864846,\n" + 
				"      \"text\": \"Warm\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.93584734\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 65,\n" + 
				"      \"beginOffset\": 1807,\n" + 
				"      \"endOffset\": 1810,\n" + 
				"      \"score\": 0.9825053,\n" + 
				"      \"text\": \"dry\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9587904\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 66,\n" + 
				"      \"beginOffset\": 1824,\n" + 
				"      \"endOffset\": 1830,\n" + 
				"      \"score\": 0.9954218,\n" + 
				"      \"text\": \"pallor\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.95167565\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.97841597\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 67,\n" + 
				"      \"beginOffset\": 1832,\n" + 
				"      \"endOffset\": 1840,\n" + 
				"      \"score\": 0.9996183,\n" + 
				"      \"text\": \"cyanosis\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9861287\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9928877\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 68,\n" + 
				"      \"beginOffset\": 1844,\n" + 
				"      \"endOffset\": 1851,\n" + 
				"      \"score\": 0.99635327,\n" + 
				"      \"text\": \"icterus\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97291034\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9881132\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 120,\n" + 
				"      \"beginOffset\": 1853,\n" + 
				"      \"endOffset\": 1858,\n" + 
				"      \"score\": 0.9957826,\n" + 
				"      \"text\": \"HEENT\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 121,\n" + 
				"      \"beginOffset\": 1860,\n" + 
				"      \"endOffset\": 1878,\n" + 
				"      \"score\": 0.94684595,\n" + 
				"      \"text\": \"Tympanic membranes\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 69,\n" + 
				"      \"beginOffset\": 1860,\n" + 
				"      \"endOffset\": 1885,\n" + 
				"      \"score\": 0.8963449,\n" + 
				"      \"text\": \"Tympanic membranes benign\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9296392\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 122,\n" + 
				"      \"beginOffset\": 1891,\n" + 
				"      \"endOffset\": 1898,\n" + 
				"      \"score\": 0.9946406,\n" + 
				"      \"text\": \"pharynx\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 70,\n" + 
				"      \"beginOffset\": 1891,\n" + 
				"      \"endOffset\": 1908,\n" + 
				"      \"score\": 0.36297113,\n" + 
				"      \"text\": \"pharynx is benign\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9239894\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 123,\n" + 
				"      \"beginOffset\": 1910,\n" + 
				"      \"endOffset\": 1922,\n" + 
				"      \"score\": 0.9817141,\n" + 
				"      \"text\": \"Nasal mucosa\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 71,\n" + 
				"      \"beginOffset\": 1910,\n" + 
				"      \"endOffset\": 1932,\n" + 
				"      \"score\": 0.7462211,\n" + 
				"      \"text\": \"Nasal mucosa is intact\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.95483375\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 124,\n" + 
				"      \"beginOffset\": 1934,\n" + 
				"      \"endOffset\": 1940,\n" + 
				"      \"score\": 0.9942198,\n" + 
				"      \"text\": \"Pupils\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 72,\n" + 
				"      \"beginOffset\": 1945,\n" + 
				"      \"endOffset\": 1950,\n" + 
				"      \"score\": 0.99614954,\n" + 
				"      \"text\": \"round\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.96994567\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 73,\n" + 
				"      \"beginOffset\": 1952,\n" + 
				"      \"endOffset\": 1959,\n" + 
				"      \"score\": 0.89836526,\n" + 
				"      \"text\": \"regular\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.91164035\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 74,\n" + 
				"      \"beginOffset\": 1965,\n" + 
				"      \"endOffset\": 1970,\n" + 
				"      \"score\": 0.9904715,\n" + 
				"      \"text\": \"equal\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9545493\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 75,\n" + 
				"      \"beginOffset\": 1972,\n" + 
				"      \"endOffset\": 2015,\n" + 
				"      \"score\": 0.6092623,\n" + 
				"      \"text\": \"reacting equally to light and accommodation\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.93301195\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 76,\n" + 
				"      \"beginOffset\": 2017,\n" + 
				"      \"endOffset\": 2027,\n" + 
				"      \"score\": 0.9367076,\n" + 
				"      \"text\": \"EOM intact\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9368575\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 125,\n" + 
				"      \"beginOffset\": 2029,\n" + 
				"      \"endOffset\": 2034,\n" + 
				"      \"score\": 0.8890398,\n" + 
				"      \"text\": \"Fundi\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 77,\n" + 
				"      \"beginOffset\": 2029,\n" + 
				"      \"endOffset\": 2052,\n" + 
				"      \"score\": 0.5118913,\n" + 
				"      \"text\": \"Fundi reveal flat discs\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8579577\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 78,\n" + 
				"      \"beginOffset\": 2058,\n" + 
				"      \"endOffset\": 2071,\n" + 
				"      \"score\": 0.74736106,\n" + 
				"      \"text\": \"clear margins\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8380668\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 79,\n" + 
				"      \"beginOffset\": 2073,\n" + 
				"      \"endOffset\": 2091,\n" + 
				"      \"score\": 0.83438605,\n" + 
				"      \"text\": \"Normal vasculature\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.89270955\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 126,\n" + 
				"      \"beginOffset\": 2080,\n" + 
				"      \"endOffset\": 2091,\n" + 
				"      \"score\": 0.3823152,\n" + 
				"      \"text\": \"vasculature\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 80,\n" + 
				"      \"beginOffset\": 2096,\n" + 
				"      \"endOffset\": 2107,\n" + 
				"      \"score\": 0.99784184,\n" + 
				"      \"text\": \"hemorrhages\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97343045\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.98683745\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 81,\n" + 
				"      \"beginOffset\": 2109,\n" + 
				"      \"endOffset\": 2117,\n" + 
				"      \"score\": 0.9926595,\n" + 
				"      \"text\": \"exudates\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.930022\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9750851\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 82,\n" + 
				"      \"beginOffset\": 2121,\n" + 
				"      \"endOffset\": 2135,\n" + 
				"      \"score\": 0.99786466,\n" + 
				"      \"text\": \"microaneurysms\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.95145285\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9918724\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 127,\n" + 
				"      \"beginOffset\": 2140,\n" + 
				"      \"endOffset\": 2147,\n" + 
				"      \"score\": 0.98051375,\n" + 
				"      \"text\": \"thyroid\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 83,\n" + 
				"      \"beginOffset\": 2140,\n" + 
				"      \"endOffset\": 2159,\n" + 
				"      \"score\": 0.9474965,\n" + 
				"      \"text\": \"thyroid enlargement\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9600563\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.97346514\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 84,\n" + 
				"      \"beginOffset\": 2173,\n" + 
				"      \"endOffset\": 2188,\n" + 
				"      \"score\": 0.999084,\n" + 
				"      \"text\": \"lymphadenopathy\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97937554\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9900096\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 128,\n" + 
				"      \"beginOffset\": 2190,\n" + 
				"      \"endOffset\": 2195,\n" + 
				"      \"score\": 0.99901116,\n" + 
				"      \"text\": \"LUNGS\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 85,\n" + 
				"      \"beginOffset\": 2197,\n" + 
				"      \"endOffset\": 2233,\n" + 
				"      \"score\": 0.9758647,\n" + 
				"      \"text\": \"Clear to percussion and auscultation\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9254809\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 86,\n" + 
				"      \"beginOffset\": 2235,\n" + 
				"      \"endOffset\": 2254,\n" + 
				"      \"score\": 0.8738434,\n" + 
				"      \"text\": \"Normal sinus rhythm\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.95803446\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 129,\n" + 
				"      \"beginOffset\": 2242,\n" + 
				"      \"endOffset\": 2247,\n" + 
				"      \"score\": 0.7653121,\n" + 
				"      \"text\": \"sinus\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 87,\n" + 
				"      \"beginOffset\": 2259,\n" + 
				"      \"endOffset\": 2273,\n" + 
				"      \"score\": 0.8579774,\n" + 
				"      \"text\": \"premature beat\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9380419\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9580646\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 88,\n" + 
				"      \"beginOffset\": 2275,\n" + 
				"      \"endOffset\": 2281,\n" + 
				"      \"score\": 0.99797183,\n" + 
				"      \"text\": \"murmur\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9691786\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.989368\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 89,\n" + 
				"      \"beginOffset\": 2283,\n" + 
				"      \"endOffset\": 2285,\n" + 
				"      \"score\": 0.982749,\n" + 
				"      \"text\": \"S3\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9295464\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.91882193\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 90,\n" + 
				"      \"beginOffset\": 2289,\n" + 
				"      \"endOffset\": 2291,\n" + 
				"      \"score\": 0.9943955,\n" + 
				"      \"text\": \"S4\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97584915\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9789979\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 130,\n" + 
				"      \"beginOffset\": 2293,\n" + 
				"      \"endOffset\": 2298,\n" + 
				"      \"score\": 0.9669685,\n" + 
				"      \"text\": \"Heart\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 91,\n" + 
				"      \"beginOffset\": 2293,\n" + 
				"      \"endOffset\": 2325,\n" + 
				"      \"score\": 0.93506193,\n" + 
				"      \"text\": \"Heart sounds are of good quality\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8092147\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 131,\n" + 
				"      \"beginOffset\": 2345,\n" + 
				"      \"endOffset\": 2353,\n" + 
				"      \"score\": 0.9313635,\n" + 
				"      \"text\": \"carotids\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 92,\n" + 
				"      \"beginOffset\": 2355,\n" + 
				"      \"endOffset\": 2363,\n" + 
				"      \"score\": 0.522752,\n" + 
				"      \"text\": \"femorals\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.42382148\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 132,\n" + 
				"      \"beginOffset\": 2355,\n" + 
				"      \"endOffset\": 2363,\n" + 
				"      \"score\": 0.5416371,\n" + 
				"      \"text\": \"femorals\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 93,\n" + 
				"      \"beginOffset\": 2365,\n" + 
				"      \"endOffset\": 2379,\n" + 
				"      \"score\": 0.6738096,\n" + 
				"      \"text\": \"dorsalis pedis\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.89500165\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 133,\n" + 
				"      \"beginOffset\": 2365,\n" + 
				"      \"endOffset\": 2379,\n" + 
				"      \"score\": 0.9599527,\n" + 
				"      \"text\": \"dorsalis pedis\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 134,\n" + 
				"      \"beginOffset\": 2385,\n" + 
				"      \"endOffset\": 2394,\n" + 
				"      \"score\": 0.9844247,\n" + 
				"      \"text\": \"posterior\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 135,\n" + 
				"      \"beginOffset\": 2395,\n" + 
				"      \"endOffset\": 2412,\n" + 
				"      \"score\": 0.20021509,\n" + 
				"      \"text\": \"tibial pulsations\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 94,\n" + 
				"      \"beginOffset\": 2395,\n" + 
				"      \"endOffset\": 2422,\n" + 
				"      \"score\": 0.48964113,\n" + 
				"      \"text\": \"tibial pulsations are brisk\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8951829\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 95,\n" + 
				"      \"beginOffset\": 2424,\n" + 
				"      \"endOffset\": 2429,\n" + 
				"      \"score\": 0.96392006,\n" + 
				"      \"text\": \"equal\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.92917645\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 96,\n" + 
				"      \"beginOffset\": 2435,\n" + 
				"      \"endOffset\": 2441,\n" + 
				"      \"score\": 0.7901101,\n" + 
				"      \"text\": \"active\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8307514\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 136,\n" + 
				"      \"beginOffset\": 2442,\n" + 
				"      \"endOffset\": 2453,\n" + 
				"      \"score\": 0.99553996,\n" + 
				"      \"text\": \"bilaterally\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 137,\n" + 
				"      \"beginOffset\": 2455,\n" + 
				"      \"endOffset\": 2462,\n" + 
				"      \"score\": 0.9987355,\n" + 
				"      \"text\": \"ABDOMEN\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 97,\n" + 
				"      \"beginOffset\": 2464,\n" + 
				"      \"endOffset\": 2470,\n" + 
				"      \"score\": 0.959451,\n" + 
				"      \"text\": \"Benign\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.92757046\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 98,\n" + 
				"      \"beginOffset\": 2479,\n" + 
				"      \"endOffset\": 2487,\n" + 
				"      \"score\": 0.99938095,\n" + 
				"      \"text\": \"guarding\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97529197\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.98472416\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 99,\n" + 
				"      \"beginOffset\": 2489,\n" + 
				"      \"endOffset\": 2497,\n" + 
				"      \"score\": 0.99900216,\n" + 
				"      \"text\": \"rigidity\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9674214\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9906301\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 100,\n" + 
				"      \"beginOffset\": 2499,\n" + 
				"      \"endOffset\": 2509,\n" + 
				"      \"score\": 0.99849916,\n" + 
				"      \"text\": \"tenderness\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9525129\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.97917616\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 101,\n" + 
				"      \"beginOffset\": 2511,\n" + 
				"      \"endOffset\": 2515,\n" + 
				"      \"score\": 0.9938658,\n" + 
				"      \"text\": \"mass\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.94482076\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9805109\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 102,\n" + 
				"      \"beginOffset\": 2519,\n" + 
				"      \"endOffset\": 2531,\n" + 
				"      \"score\": 0.9993199,\n" + 
				"      \"text\": \"organomegaly\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9839511\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9884547\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 138,\n" + 
				"      \"beginOffset\": 2533,\n" + 
				"      \"endOffset\": 2543,\n" + 
				"      \"score\": 0.99695516,\n" + 
				"      \"text\": \"NEUROLOGIC\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 103,\n" + 
				"      \"beginOffset\": 2545,\n" + 
				"      \"endOffset\": 2559,\n" + 
				"      \"score\": 0.67517793,\n" + 
				"      \"text\": \"Grossly intact\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9353806\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 139,\n" + 
				"      \"beginOffset\": 2561,\n" + 
				"      \"endOffset\": 2572,\n" + 
				"      \"score\": 0.99810094,\n" + 
				"      \"text\": \"EXTREMITIES\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 104,\n" + 
				"      \"beginOffset\": 2574,\n" + 
				"      \"endOffset\": 2580,\n" + 
				"      \"score\": 0.63049495,\n" + 
				"      \"text\": \"Normal\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.6682751\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 140,\n" + 
				"      \"beginOffset\": 2582,\n" + 
				"      \"endOffset\": 2584,\n" + 
				"      \"score\": 0.9991054,\n" + 
				"      \"text\": \"GU\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 141,\n" + 
				"      \"beginOffset\": 2586,\n" + 
				"      \"endOffset\": 2595,\n" + 
				"      \"score\": 0.85820735,\n" + 
				"      \"text\": \"Genitalia\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 105,\n" + 
				"      \"beginOffset\": 2586,\n" + 
				"      \"endOffset\": 2602,\n" + 
				"      \"score\": 0.78722364,\n" + 
				"      \"text\": \"Genitalia normal\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9263724\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 106,\n" + 
				"      \"beginOffset\": 2617,\n" + 
				"      \"endOffset\": 2633,\n" + 
				"      \"score\": 0.77974397,\n" + 
				"      \"text\": \"inguinal hernias\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.97334427\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.9880085\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 107,\n" + 
				"      \"beginOffset\": 2650,\n" + 
				"      \"endOffset\": 2661,\n" + 
				"      \"score\": 0.95238143,\n" + 
				"      \"text\": \"hemorrhoids\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.78098124\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 142,\n" + 
				"      \"beginOffset\": 2669,\n" + 
				"      \"endOffset\": 2679,\n" + 
				"      \"score\": 0.99685585,\n" + 
				"      \"text\": \"anal canal\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 143,\n" + 
				"      \"beginOffset\": 2685,\n" + 
				"      \"endOffset\": 2693,\n" + 
				"      \"score\": 0.9935247,\n" + 
				"      \"text\": \"prostate\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 108,\n" + 
				"      \"beginOffset\": 2685,\n" + 
				"      \"endOffset\": 2702,\n" + 
				"      \"score\": 0.49404392,\n" + 
				"      \"text\": \"prostate is small\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.9281409\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 109,\n" + 
				"      \"beginOffset\": 2711,\n" + 
				"      \"endOffset\": 2736,\n" + 
				"      \"score\": 0.44318628,\n" + 
				"      \"text\": \"normal to mildly enlarged\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.8683149\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 110,\n" + 
				"      \"beginOffset\": 2760,\n" + 
				"      \"endOffset\": 2771,\n" + 
				"      \"score\": 0.50163394,\n" + 
				"      \"text\": \"symmetrical\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.5069501\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 111,\n" + 
				"      \"beginOffset\": 2792,\n" + 
				"      \"endOffset\": 2812,\n" + 
				"      \"score\": 0.6331762,\n" + 
				"      \"text\": \"palpable abnormality\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.91142726\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.94973266\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 112,\n" + 
				"      \"beginOffset\": 2826,\n" + 
				"      \"endOffset\": 2837,\n" + 
				"      \"score\": 0.94192415,\n" + 
				"      \"text\": \"rectal mass\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.93691975\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"name\": \"NEGATION\",\n" + 
				"          \"score\": 0.98388505\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 113,\n" + 
				"      \"beginOffset\": 2843,\n" + 
				"      \"endOffset\": 2870,\n" + 
				"      \"score\": 0.6687549,\n" + 
				"      \"text\": \"stool is Hemoccult negative\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"SIGN\",\n" + 
				"          \"score\": 0.7930287\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 57,\n" + 
				"      \"beginOffset\": 2852,\n" + 
				"      \"endOffset\": 2861,\n" + 
				"      \"score\": 0.5783809,\n" + 
				"      \"text\": \"Hemoccult\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"TEST_VALUE\",\n" + 
				"          \"score\": 0.9819834,\n" + 
				"          \"relationshipScore\": 0.9999962,\n" + 
				"          \"id\": 58,\n" + 
				"          \"beginOffset\": 2862,\n" + 
				"          \"endOffset\": 2870,\n" + 
				"          \"text\": \"negative\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 167,\n" + 
				"      \"beginOffset\": 2944,\n" + 
				"      \"endOffset\": 2956,\n" + 
				"      \"score\": 0.9799412,\n" + 
				"      \"text\": \"Dyslipidemia\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.95865184\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 168,\n" + 
				"      \"beginOffset\": 2961,\n" + 
				"      \"endOffset\": 2969,\n" + 
				"      \"score\": 0.9770297,\n" + 
				"      \"text\": \"Tinnitus\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.8854053\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 170,\n" + 
				"      \"beginOffset\": 2971,\n" + 
				"      \"endOffset\": 2975,\n" + 
				"      \"score\": 0.9914403,\n" + 
				"      \"text\": \"left\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"DIRECTION\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 171,\n" + 
				"      \"beginOffset\": 2976,\n" + 
				"      \"endOffset\": 2979,\n" + 
				"      \"score\": 0.9713737,\n" + 
				"      \"text\": \"ear\",\n" + 
				"      \"category\": \"ANATOMY\",\n" + 
				"      \"type\": \"SYSTEM_ORGAN_SITE\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 169,\n" + 
				"      \"beginOffset\": 2984,\n" + 
				"      \"endOffset\": 2995,\n" + 
				"      \"score\": 0.9761238,\n" + 
				"      \"text\": \"Hemorrhoids\",\n" + 
				"      \"category\": \"MEDICAL_CONDITION\",\n" + 
				"      \"type\": \"DX_NAME\",\n" + 
				"      \"traits\": [\n" + 
				"        {\n" + 
				"          \"name\": \"DIAGNOSIS\",\n" + 
				"          \"score\": 0.8885601\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 147,\n" + 
				"      \"beginOffset\": 3027,\n" + 
				"      \"endOffset\": 3033,\n" + 
				"      \"score\": 0.99934393,\n" + 
				"      \"text\": \"niacin\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.9463427,\n" + 
				"          \"relationshipScore\": 0.99999595,\n" + 
				"          \"id\": 148,\n" + 
				"          \"beginOffset\": 3034,\n" + 
				"          \"endOffset\": 3041,\n" + 
				"          \"text\": \"1000 mg\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.9903593,\n" + 
				"          \"relationshipScore\": 0.9996793,\n" + 
				"          \"id\": 149,\n" + 
				"          \"beginOffset\": 3042,\n" + 
				"          \"endOffset\": 3056,\n" + 
				"          \"text\": \"in the morning\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.99031067,\n" + 
				"          \"relationshipScore\": 0.9999341,\n" + 
				"          \"id\": 150,\n" + 
				"          \"beginOffset\": 3058,\n" + 
				"          \"endOffset\": 3064,\n" + 
				"          \"text\": \"500 mg\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.95317966,\n" + 
				"          \"relationshipScore\": 0.9999951,\n" + 
				"          \"id\": 151,\n" + 
				"          \"beginOffset\": 3065,\n" + 
				"          \"endOffset\": 3072,\n" + 
				"          \"text\": \"at noon\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.9946239,\n" + 
				"          \"relationshipScore\": 0.99992955,\n" + 
				"          \"id\": 152,\n" + 
				"          \"beginOffset\": 3078,\n" + 
				"          \"endOffset\": 3085,\n" + 
				"          \"text\": \"1000 mg\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.99318427,\n" + 
				"          \"relationshipScore\": 0.9978333,\n" + 
				"          \"id\": 153,\n" + 
				"          \"beginOffset\": 3086,\n" + 
				"          \"endOffset\": 3100,\n" + 
				"          \"text\": \"in the evening\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 154,\n" + 
				"      \"beginOffset\": 3102,\n" + 
				"      \"endOffset\": 3109,\n" + 
				"      \"score\": 0.99977964,\n" + 
				"      \"text\": \"aspirin\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.9768488,\n" + 
				"          \"relationshipScore\": 0.99932015,\n" + 
				"          \"id\": 155,\n" + 
				"          \"beginOffset\": 3110,\n" + 
				"          \"endOffset\": 3115,\n" + 
				"          \"text\": \"81 mg\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.9994349,\n" + 
				"          \"relationshipScore\": 0.9985424,\n" + 
				"          \"id\": 156,\n" + 
				"          \"beginOffset\": 3116,\n" + 
				"          \"endOffset\": 3121,\n" + 
				"          \"text\": \"daily\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 157,\n" + 
				"      \"beginOffset\": 3123,\n" + 
				"      \"endOffset\": 3136,\n" + 
				"      \"score\": 0.9958332,\n" + 
				"      \"text\": \"multivitamins\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 158,\n" + 
				"      \"beginOffset\": 3138,\n" + 
				"      \"endOffset\": 3147,\n" + 
				"      \"score\": 0.99997103,\n" + 
				"      \"text\": \"vitamin E\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.9938373,\n" + 
				"          \"relationshipScore\": 0.99998736,\n" + 
				"          \"id\": 159,\n" + 
				"          \"beginOffset\": 3148,\n" + 
				"          \"endOffset\": 3157,\n" + 
				"          \"text\": \"400 units\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.99849796,\n" + 
				"          \"relationshipScore\": 0.99999285,\n" + 
				"          \"id\": 160,\n" + 
				"          \"beginOffset\": 3158,\n" + 
				"          \"endOffset\": 3163,\n" + 
				"          \"text\": \"daily\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 161,\n" + 
				"      \"beginOffset\": 3169,\n" + 
				"      \"endOffset\": 3178,\n" + 
				"      \"score\": 0.9999584,\n" + 
				"      \"text\": \"vitamin C\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": [],\n" + 
				"      \"attributes\": [\n" + 
				"        {\n" + 
				"          \"type\": \"DOSAGE\",\n" + 
				"          \"score\": 0.9940942,\n" + 
				"          \"relationshipScore\": 0.9999976,\n" + 
				"          \"id\": 162,\n" + 
				"          \"beginOffset\": 3179,\n" + 
				"          \"endOffset\": 3185,\n" + 
				"          \"text\": \"500 mg\",\n" + 
				"          \"traits\": []\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"type\": \"FREQUENCY\",\n" + 
				"          \"score\": 0.99887866,\n" + 
				"          \"relationshipScore\": 0.9999368,\n" + 
				"          \"id\": 163,\n" + 
				"          \"beginOffset\": 3186,\n" + 
				"          \"endOffset\": 3191,\n" + 
				"          \"text\": \"daily\",\n" + 
				"          \"traits\": []\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 164,\n" + 
				"      \"beginOffset\": 3209,\n" + 
				"      \"endOffset\": 3217,\n" + 
				"      \"score\": 0.95413023,\n" + 
				"      \"text\": \"lycopene\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 165,\n" + 
				"      \"beginOffset\": 3219,\n" + 
				"      \"endOffset\": 3227,\n" + 
				"      \"score\": 0.9917276,\n" + 
				"      \"text\": \"selenium\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 166,\n" + 
				"      \"beginOffset\": 3233,\n" + 
				"      \"endOffset\": 3241,\n" + 
				"      \"score\": 0.6625878,\n" + 
				"      \"text\": \"flaxseed\",\n" + 
				"      \"category\": \"MEDICATION\",\n" + 
				"      \"type\": \"GENERIC_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 144,\n" + 
				"      \"beginOffset\": 3274,\n" + 
				"      \"endOffset\": 3278,\n" + 
				"      \"score\": 0.55067325,\n" + 
				"      \"text\": \"labs\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 145,\n" + 
				"      \"beginOffset\": 3312,\n" + 
				"      \"endOffset\": 3333,\n" + 
				"      \"score\": 0.9695389,\n" + 
				"      \"text\": \"fasting lipid profile\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"id\": 146,\n" + 
				"      \"beginOffset\": 3338,\n" + 
				"      \"endOffset\": 3341,\n" + 
				"      \"score\": 0.90974385,\n" + 
				"      \"text\": \"ALT\",\n" + 
				"      \"category\": \"TEST_TREATMENT_PROCEDURE\",\n" + 
				"      \"type\": \"TEST_NAME\",\n" + 
				"      \"traits\": []\n" + 
				"    }\n" + 
				"  ],\n" + 
				"  \"unmappedAttributes\": [],\n" + 
				"  \"sdkResponseMetadata\": {\n" + 
				"    \"metadata\": {\n" + 
				"      \"AWS_REQUEST_ID\": \"047cd0da-7bfa-11e9-8500-7fc0bd9b0e3a\"\n" + 
				"    }\n" + 
				"  },\n" + 
				"  \"sdkHttpMetadata\": {\n" + 
				"    \"httpHeaders\": {\n" + 
				"      \"Connection\": \"keep-alive\",\n" + 
				"      \"Content-Length\": \"31083\",\n" + 
				"      \"Content-Type\": \"application/x-amz-json-1.1\",\n" + 
				"      \"Date\": \"Tue, 21 May 2019 18:55:38 GMT\",\n" + 
				"      \"x-amzn-RequestId\": \"047cd0da-7bfa-11e9-8500-7fc0bd9b0e3a\"\n" + 
				"    },\n" + 
				"    \"httpStatusCode\": 200\n" + 
				"  }\n" + 
				"}";
		CMData data = util.parseCMOutput(cmJson);
		log.debug("The data :"+data.getEntities());
		List<Entity> entList = data.getEntities();
		for(Entity ent:entList) {
			log.debug("The data "+ent.getCategory());
		}
		//CMData cmData = util.readCMOutput("cm-output-gen-med.json");
		//CMData cmData = util.readCMOutput("cm-output-abd-pain.json");
		//FHIRResourceBuilder fhirBuilder = new FHIRResourceBuilder();
		//fhirBuilder.buildResource(cmData,patRefId);
		//util.readFHIRPayload("condition.json");
		//util.readBundleFHIRPayload("Monroe732_Larkin917_68b1c58d-41cd-4855-a100-8206eb1b61b5.json");

	}
	
	public void readBundleFHIRPayload(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		StringBuilder result = new StringBuilder("");
		File file = new File(classLoader.getResource(fileName).getFile()); 	
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.debug("The bundle : "+result.toString());
		Bundle bundle = 
				FhirContext.forDstu3().newJsonParser().parseResource(Bundle.class, result.toString());
		List<BundleEntryComponent> list = bundle.getEntry();
		
		String patientId = null;
		//Patient patient = null;
		String patientFullUrl = null;
		for(BundleEntryComponent entry : list) {
			
			String fhirType = entry.getResource().fhirType();
			//System.out.println(entry.getResource().fhirType());
			
			if(fhirType.equals(ResourceType.DOCUMENTREFERENCE.getDisplay())) {
				DocumentReference docRef = (DocumentReference)entry.getResource();
				log.debug("The document data "+docRef.getDescription());
				List<DocumentReferenceContentComponent> attList = docRef.getContent();
				for(DocumentReferenceContentComponent attach:attList) {
					byte[] notesBytes = attach.getAttachment().getData();
					log.debug("The provider clinical :"+new String(notesBytes));
				}
				
			}
		}


		
	}
	
	public void readFHIRPayload(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		StringBuilder result = new StringBuilder("");
		File file = new File(classLoader.getResource(fileName).getFile()); 	
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("The result Data : "+result.toString());
		
		Condition condition = FhirContext.forDstu3().newJsonParser().parseResource(Condition.class, result.toString());
		log.debug("The category : "+condition.getCategory().get(0).getCoding().get(0).getSystem());
		
		Coding coding = new Coding();
		coding.setSystem("http://standardhealthrecord.org/shr/condition/vs/ConditionCategoryVS");
		coding.setCode("disease");
		coding.setDisplay("Disease");
		ArrayList codingList = new ArrayList<Coding>();
		//codingList
		
		
		
	}

}
