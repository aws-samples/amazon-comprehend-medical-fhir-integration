package com.amazonaws.lab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClientBuilder;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
import com.google.gson.Gson;

public class SendToCMUtil {
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
		
        // Send "notes" string to Comprehend Medical
        final AWSComprehendMedical client = AWSComprehendMedicalClientBuilder.defaultClient();
        DetectEntitiesRequest request = new DetectEntitiesRequest();
        request.setText(result.toString());
        DetectEntitiesResult resultCM = client.detectEntities(request);
        log.debug("The default toString "+resultCM);
        log.debug(new Gson().toJson(resultCM));
        
		return null;
		//System.out.println("The CM Data : "+cmData.getEntities());
	}
	public static void main(String []args) {
		SendToCMUtil util = new SendToCMUtil();
		util.readCMOutput("medical-note.txt");
	}

}
