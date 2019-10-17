# Reinvent -20109 - HLC 401 - Lab Instructions - Master

**Pre-requisites : **The workshop assumes that the participants have some basic understanding of AWS services such as AWS Lambda, IAM, DynamoDB and the Java programming language.  The account user should have admin access to create IAM roles.

Note: The workshop uses us-east-1 region for all the services that are deployed as part of the workshop but participants can use any of the US regions of their choice.

The workshop is divided into two parts. The first part of the workshop focuses on building a FHIR interface. It will be a RESTful endpoint which enables loading and retrieval of FHIR resources. The second part will show how Amazon Comprehend Medical can be used to extract clinical entities from HL7 V2 and FHIR messages, and load them into FHIR repository created in part 1 of the workshop.

## Part 1 - Build FHIR Interface

[Image: image.png]
1. Log on to the AWS management console and change your region from the top right corner to US East(N. Virgina) or any US region of your choice .
2. Click on services and search for cloud9. Cloud9 service is a browser based built-in IDE desktop to write code, run CLI commands or create container images. It has a pre-configured AWS CLI and provides a linux terminal to run commands.
3. Create a new environment and call it as FHIRDesktop. **Use m4.large type. Leave the other settings as default.**

[Image: Image.jpg]

## Download source code 

1. Go to the terminal window at the lower pane and checkout the source code from git hub using the following command:

```
 `git clone https://github.com/mithun008/FHIRServer.git`
```

1. Make sure that required folders are present by navigating through the directory FHIRServer directory. There should be a resources and src folder and also a pom.xml file.

[Image: Image.jpg]
## Setup the environment

1. Locate the terminal window on the lower pane of the screen.
2. Change directory to FHIRServer/resources.

```
cd FHIRServer/resources/
```

1. Run the script by executing following command.** Please answer ‘y’ when prompted for permission to download some of the packages.**

```
. .`/setupEnv.sh`
```

The script would upgrade the jdk to 1.8. By default, cloud9 comes with jdk 1.7. It will also set the default jdk as 1.8. It would also install maven required for building the code. A package to beautify json output is also installed.

## S3 Bucket to upload Lambda jar file

We will create the S3 bucket in this step which will be used later as part of the cloudformation package command. The lambda jar file created in the next steps in uploaded to this S3 bucket.


1. Run the following command on terminal to create a S3 bucket. You will need to pick a ***unique name*** like fhir-code-bucket-<<user initials>> for the bucket otherwise the command will throw an error.

```
`aws s3 mb s3://<<REPLACE_BUCKET_NAME>>`
```


**Keep a note of this bucket name as it will be used in the later steps.**

1. Go back to FHIRServer directory by running `cd ..`
2. Build the source code by running `mvn clean install`

The above command would download all the required libraries to compile the source and build a single jar file with all the required dependencies. The output jar can be found under target/ directory as FHIRServer-0.0.1-SNAPSHOT.jar file.
[Image: Image.jpg]
## Deploy FHIR Interface code

Go to the resources folder and check the file FHIRService-dev-swagger-apigateway.yaml. This file is the SAM(Serverless Application Model) template that will be used to deploy the generated jar as a lambda function along with other resources like DynamoDB table, API gateway resources, Cognito user pool and S3 bucket to store FHIR payloads.The SAM template will template would be transformed into a cloudformation template which can be used to deploy the resources.

***The AWS Serverless Application Model (AWS SAM) is a model to define serverless applications. AWS SAM is natively supported by AWS CloudFormation and defines simplified syntax for expressing serverless resources. The specification currently covers APIs, Lambda functions and Amazon DynamoDB tables. SAM is available under Apache 2.0 for AWS partners and customers to adopt and extend within their own toolsets. For details on the specification, see the [AWS Serverless Application Model](https://github.com/awslabs/serverless-application-model).***

1. Go to **resources** directory under FHIR server in the terminal window. Run `cd resources/`
2. Run the following command to package the SAM template into a cloudformation template. Replace the S3 bucket name with the bucket name that was created in earlier step.

```
`aws cloudformation package --template-file FHIRService-dev-swagger-apigateway.yaml --output-template-file serverless-output.yaml --s3-bucket <<Replace the S3 bucket name>>`
```


***Note : If you face any difficulties in copy pasting the above command, please edit command #1 in commands.txt file under the resources folder and paste it to the terminal window.***
[Image: Image.jpg]The output should look like as shown above in the screenshot. The command uploads the jar file to S3 bucket and generates a cloudformation template file serverless-output.yaml file which will be used to do the actual deployment of resources.

**Tip : **Feel free to open the serverless-output.yaml file in cloud9 IDE to check the contents.


1. Run the following command to deploy the stack using cloudformation. **Use your own stack name like FHIRDemoService and use it in the below command.**

```
`aws cloudformation deploy --template-file \
/home/ec2-user/environment/FHIRServer/resources/serverless-output.yaml \
--stack-name <<Enter Stack Name>> --capabilities CAPABILITY_IAM`
```


***Note : If you face any difficulties in copy pasting the above command, please edit command #2 in commands.txt file under the resources folder and paste it to the terminal window.***

The command will wait until all the resources are created. Once, the command is finished and stack creation is complete, navigate to API gateway and lambda services to check the services that have been deployed by the cloudformation template.


1. Run the following command to get the API Gateway endpoint for sending the FHIR requests. **Make sure to replace the stack name that you used. Note down the value as API_END_POINT.**

```
aws cloudformation describe-stacks \
    --stack-name <<REPLACE_STACK_NAME>> \
    --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`][OutputValue]' \
    --output text
```

### Capability Statement

A Capability Statement documents a set of capabilities (behaviors) of a FHIR Server for a particular version of FHIR that may be used as a statement of actual server functionality or a statement of required or desired server implementation.Capability statement for the FHIR server can be accessed without any authorization. 

1. Go to the cloud9 terminal window again and run the following command:

```
`curl -H "Accept: application/fhir+json" <<API_END_POINT>>metadata | jq`
```


The output should be similar to as shown below in screenshot:
[Image: image.png]
## Cognito Setup

In this section we will create a user in Cognito user pool and provision a user for sending an authorized request to API Gateway. Amazon Cognito lets you add user sign-up, sign-in, and access control to your web and mobile apps quickly and easily. Amazon Cognito provides solutions to control access to AWS resources from your app. You can define roles and map users to different roles so your app can access only the resources that are authorized for each user.


1. Run the following command to get the **UserPoolId and ClientId**. **Make sure to replace the stack name that you used. The first output value will be UserPoolId and second one will be ClientId.**
2. aws cloudformation describe-stacks \
        --stack-name <<REPLACE_STACK_NAME>> \
        --query 'Stacks[].Outputs[?OutputKey==`UserPoolId` || OutputKey==`UserPoolClient`][OutputValue]' \
        --output text



1. Go to **resources** folder and **edit provision-user.py**. Replace the **POOL ID** and **CLIENT ID **tags with values captured above. **There is one UserPoolId variable entry and three ClientId variable entries that have to be edited in the provision-user.py file.  Make sure to save the provision-user.py file before running the provision command below.  Go to File at the top of the AWS Cloud9 window and hit Save.**

1. Run the provision-user.py by executing the below command. It will create an user in cognito user pool. The user name is **workshopuser** and password will be **Master123!.**

```
python provision-user.py
```

The output would look like as the screenshot below. **Copy the output value as it will be used as the IDToken in next step.**
**Keep the cognito userid and password  saved in a notepad as it will be used in part 2 of the lab. **The ID token is a JWT token which is used by cognito to authorize users. More details about JWT can be found at [jwt.io](http://jwt.io/)
[Image: image.png]*You can confirm the user creation by going to users and groups under general settings for the user pool. You may need to refresh the page if it does not show up immediately.*

We are now ready to send requests to our FHIR server. Lets test a GET patient request first to make sure there is no data loaded.

1. Go to the cloud9 terminal window again and run the following command:

```
`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Patient | jq`
```

The outo tput should show as below:
[Image: image.png]The following text should be shown as part of the output.

`{"resourceType":"Bundle","id":"e4679c49-37af-4478-91d8-4741a293a975","type":"searchset","total":0,"link":[{"relation":"self"}]}`

We have now created a FHIR interface. In the next part of the lab, we will focus on loading test data and integrating it with comprehend medical.

## Part -2 - Integrate Comprehend Medical

This section of the lab will focus on integrating Comprehend Medical with the FHIR interface.


## Architecture & Use cases

[Image: FHIRWorkshop.png]

1. Run the following command to go back to the environment directory.

```
cd ~/environment/
```

1. Run the following command to download code for FHIR Comprehend Medical integration. The git credentials to use are as below:

```
git clone https://github.com/aws-samples/amazon-comprehend-medical-fhir-integration.git
```

## Load data to FHIR repository

We will now load the patient record to an existing FHIR repository. 

1. Go to test-data folder by running following command.

```
cd amazon-comprehend-medical-fhir-integration/test-data/master-patient-data/
```

1. Run the following command to extract the required info for the workshop.

```
python ~/environment/amazon-comprehend-medical-fhir-integration/resources/extract-patient-info.py \
 ~/environment/amazon-comprehend-medical-fhir-integration/test-data/master-patient-data/patient-bundle.json
```

The output would be similar to the screenshot below. **Copy the output in a notepad to use in later step.**
[Image: image.png]
1. Go to **resources** folder under **amazon-comprehend-medical-fhir-integration**

```
cd ~/environment/amazon-comprehend-medical-fhir-integration/resources/
```

1. Open the file **post-test-data.sh in the editor under resources.Enable wrap lines from the view option to have a better view.** **Replace the API_END_POINT with the value generated in Part 1 of the workshop. Also replace the id token with the copied and saved token value from Part 1 of the workshop.  Save the updated file post-test-data.sh before running the next step. **
2. Run the following command to load the master patient record in the FHIR repository.

```
`chmod u+x post-test-data.sh`
./post-test-data.sh
```

The output should be similar to below:
[Image: image.png]

1. Validate the data by running the following command. Patient ID was received from the extract info script in step # 7 of generate test data.

```
`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Patient/<<PATIENT_ID>> | jq`
```

1. Get the conditions for the patient by running the following command

```
`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Condition?patient-ref-id=<<PATIENT_ID>> | jq`
```


**Make a note of the number of conditions for the patient. We will be adding additional conditions extracted from the clinical notes. It is possible that there were no conditions loaded previously.**

## Building  amazon-comprehend-medical-fhir-integration project

1. Run the following from terminal:

```
cd /home/ec2-user/environment/amazon-comprehend-medical-fhir-integration/
```

1. Next, build the project by running the following:

```
mvn clean install
```

The build process will create the jar file that will be deployed as the lambda functions to process the hl7 and FHIR resource files, call comprehend medical api's and make API calls to an existing FHIR repository.



## Deploy the project code

We will now deploy the workshop code using a SAM(Serverless Access Model) template. More details about SAM can be found [here](https://docs.aws.amazon.com/serverless-application-model/index.html).

1. Go to **resources** folder under **amazon-comprehend-medical-fhir-integration**
2. Open FHIR-CM-Integration.yaml and replace the <<REPLACE_CLIENT_ID>> with the cognito client id from part 1 of the workshop and <<REPLACE_API_ENDPOINT>> with API_END_POINT from part 1. **Save the file.**

1. Change directory to resources folder.
2. `cd /home/ec2-user/environment/amazon-comprehend-medical-fhir-integration/resources`
3. Run the following command to package the code and other artifacts. Use the same bucket name from part-1 of the lab.

```
`aws cloudformation package --template-file FHIR-CM-Integration.yaml --output-template-file serverless-output.yaml --s3-bucket <<FHIR_CODE_BUCKET>>`
```

1. Run the following command to deploy the SAM template. You can choose any stack name like FHIR-CM-Integration.Specify a secret name for your Secrets Manager.

```
`aws cloudformation deploy ``--``template``-``file ``/``home``/``ec2``-``user``/``environment``/``amazon``-``comprehend``-``medical``-``fhir``-``integration``/``resources``/``serverless``-``output``.``yaml ``--``stack``-``name ``<<``STACK_NAME``>>`` ``--``capabilities CAPABILITY_IAM --parameter-overrides CognitoSecretName=<<REPLACE_SECRET_NAME>>`
```

1. Get DATA_INPUT_BUCKET by running the following command:

```
aws cloudformation describe-stacks \
    --stack-name <<REPLACE_STACK_NAME>> \
    --query 'Stacks[].Outputs[?OutputKey==`BucketName`][OutputValue]' \
    --output text

```

## HL7 Message Flow

In this part of the lab, we will upload an HL7 file which has OBX segment containing clinical notes. The deployed project would extract the notes from the HL7 message, lookup the patient from FHIR repository and enrich the record with the medical conditions extracted from the clinical notes in HL7.

1. Go to the resources/test-data folder under FHIR-CM-Integration by running following command from terminal.

```
`cd ``~``/environment/amazon-comprehend-medical-fhir-integration/test-data`
```

1. Open the mdm.txt file under test-data directory. It is populated based on test data that was used as part of this lab. It represents the HL7 message for the same patient loaded in FHIR repository.

1. Upload the file to s3 folder using the command below. It would trigger the flow to extract the data from OBX segments, run it through Comprehend Medical, extract the Condition resources and enrich data in FHIR repository.

```
aws s3 cp mdm.txt s3://<<DATA_INPUT_BUCKET>>/input/hl7/mdm.txt
```


The above command should trigger the step functions. You can monitor the progress of the step functions by login to the console and going to the step functions service. Click on the step function that is deployed as part of the workshop. It would show the progress of the various steps.
[Image: image.png]
1. Run the below command again to lookup the conditions for the patient:

```
`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Condition?patient-ref-id=<<PATIENT_ID>> | jq`
```


Look for the conditions again. It should show the additional conditions that were extracted from the hl7 message. This completes the first part of the lab.

## Optional - FHIR Message flow

1. Run the following command to change directory to resources directory.

```
`cd /home/ec2-user/environment/FHIR-CM-Integration/resources`
```

1. Look for the file FHIR-DocRef.json and open it in the editor.
2. Edit the file and look for the tag *REPLACE_WITH_PATIENT_ID*
3. Replace it with the patient id used in scenario 1. The purpose is to extract condition resources from clinical notes embedded in the data tag of the resource and load it as Condition resources for the patient.
4. Run the following command to upload the file to trigger the workflow:

`aws s3 cp test-data/FHIR-DocRef.json s3://<<DATA_INPUT_BUKCET>>/input/fhir/FHIR-DocRef.json`

1. Check the step functions console to monitor the progress of the work flow.
2. Once the step functions is completed, run the following command to get the Conditions for the patient. It should now be updated with the new conditions that were loaded.

`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Condition?patient-ref-id=<<PATIENT_ID>> | jq`
It should now show the additional Conditions that were loaded from the notes in the DocumentReference resource.


 
 

