package com.amazonaws.lab;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClientBuilder;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


public class SendToCM {
    static final Logger log = LogManager.getLogger(SendToCM.class);
    
    private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
    
    public Map<String, String> handleRequest(Map<String, String> map, Context context) {
        // Get UnstructuredText as string from S3 object
        String UnstructuredText = s3Client.getObjectAsString(map.get("S3Bucket"), map.get("UnstructuredText"));
        
        // Parse "UnstucturedText" to get just the notes field
        JsonObject obj = new Gson().fromJson(UnstructuredText, JsonObject.class);
        String notes = obj.get("notes").toString();
        
        // Send "notes" string to Comprehend Medical
        final AWSComprehendMedical client = AWSComprehendMedicalClientBuilder.defaultClient();
        DetectEntitiesRequest request = new DetectEntitiesRequest();
        request.setText(notes);
        
        String resultOutput = "";
        DetectEntitiesResult result = client.detectEntities(request);
        
        //mithun : Made a change to store a well formed json. The default to toString doesnt have the quotes
        //resultOutput = result.getEntities().toString();
        resultOutput = new Gson().toJson(result);
        
        s3Client.putObject(map.get("S3Bucket"), "processing/CMOutput/" + map.get("FileName"), resultOutput);
        
        Map<String, String> output = new HashMap<>();
        output.put("S3Bucket", map.get("S3Bucket"));
        output.put("CMOutput", "processing/CMOutput/" + map.get("FileName"));
        output.put("UnstructuredText", map.get("UnstructuredText"));
        output.put("FileName", map.get("FileName"));
        output.put("InputFile", map.get("InputFile"));
        output.put("DataType", map.get("DataType"));

        return output;
    }
}
