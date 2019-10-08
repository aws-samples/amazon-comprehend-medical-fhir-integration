package com.amazonaws.lab;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;


public class HealthDataRouter {
    static final Logger log = LogManager.getLogger(HealthDataRouter.class);
    
    public Map<String, String> handleRequest(Map<String, String> map, Context context) {
        // Parse InputFile to determine initial prefix path: i.e. '/processing/input/hl7/' or '/processing/input/fhir/'
        String myS3key = map.get("InputFile");
        log.debug("S3 Key: " + myS3key);

        String[] keyPaths = myS3key.split("/");
        String dataType = "";
        
        log.debug("Array length: " + keyPaths.length);
        if (keyPaths.length >= 3) {
            if (keyPaths[2] != "") {
                log.debug("Found: " + keyPaths[2]);
                dataType = keyPaths[2];
            }
        } 

        Map<String, String> output = new HashMap<>();
        output.put("S3Bucket", map.get("S3Bucket"));
        output.put("FileName", map.get("FileName"));
        output.put("InputFile", map.get("InputFile"));
        output.put("DataType", dataType);

        return output;
    }
}
