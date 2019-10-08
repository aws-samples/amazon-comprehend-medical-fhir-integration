# Reinvent -20109 - HLC 401 - Lab Instructions

## Part 1 - Build FHIR Interface

**Pre-requisites : **The workshop assumes that the participants have some basic understanding of AWS services such as AWS Lambda, IAM, DynamoDB and the Java programming language.  The account user should have admin access to create IAM roles.

Note: The workshop uses us-east-1 region for all the services that are deployed as part of the workshop but participants can use any of the US regions of their choice. 

1. Log on to the AWS management console and change your region from the top right corner to US East(N. Virgina) or any US region of your choice .
2. Click on services and search for cloud9. Cloud9 service is a browser based built-in IDE desktop to write code, run CLI commands or create container images. It has a pre-configured AWS CLI and provides a linux terminal to run commands.
3. Create a new environment and call it as FHIRDesktop. **Use m4.large type. Leave the other settings as default.**

[Image: Image.jpg]

## Download source code 

1. Go to the terminal window at the lower pane and checkout the source code from git hub using the following command:
2.  `git clone https://github.com/mithun008/FHIRServer.git`
3. Make sure that required folders are present by navigating through the directory FHIRServer directory. There should be a resources and src folder and also a pom.xml file.

[Image: Image.jpg]
## Setup the environment

1. Locate the terminal window on the lower pane of the screen.
2. Change directory to FHIRServer/resources by running `cd FHIRServer/resources `and look for the file setupEnv.sh
3. Change the file to executable by running the following command:

`chmod u+x setupEnv.sh`

1. Run the script by executing following command. **Answer y when prompted for permission to download some of the packages.**

.`/setupEnv.sh`
The script would upgrade the jdk to 1.8. By default, cloud9 comes with jdk 1.7. It will also set the default jdk as 1.8. It would also install maven required for building the code. A package to beautify json output is also installed.

1. Run the following commands to set the JAVA_HOME to jdk 1.8

`echo 'export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk' >> ~/.bashrc`
`echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc`
`source ~/.bashrc`

## S3 Bucket to upload Lambda jar file

We will create the S3 bucket in this step which will be used later as part of the cloudformation package command. The lambda jar file created in the next steps in uploaded to this S3 bucket.


1. Run the following command on terminal to create a S3 bucket. You will need to pick a ***unique name*** like fhir-code-bucket-<<user initials>> for the bucket otherwise the command will throw an error.

`aws s3 mb s3://<<REPLACE_BUCKET_NAME>>`

**Keep a note of this bucket name as it will be used in the later steps.**

1. Go back to FHIRServer directory by running `cd ..`
2. Build the source code by running `mvn install`

The above command would download all the required libraries to compile the source and build a single jar file with all the required dependencies. The output jar can be found under target/ directory as FHIRServer-0.0.1-SNAPSHOT.jar file.
[Image: Image.jpg]
## Deploy FHIR Interface code

Go to the resources folder and check the file FHIRService-dev-swagger-apigateway.yaml. This file is the SAM(Serverless Application Model) template that will be used to deploy the generated jar as a lambda function along with other resources like DynamoDB table, API gateway resources, Cognito user pool and S3 bucket to store FHIR payloads.The SAM template will template would be transformed into a cloudformation template which can be used to deploy the resources.

***The AWS Serverless Application Model (AWS SAM) is a model to define serverless applications. AWS SAM is natively supported by AWS CloudFormation and defines simplified syntax for expressing serverless resources. The specification currently covers APIs, Lambda functions and Amazon DynamoDB tables. SAM is available under Apache 2.0 for AWS partners and customers to adopt and extend within their own toolsets. For details on the specification, see the [AWS Serverless Application Model](https://github.com/awslabs/serverless-application-model).***

1. Go to **resources** directory under FHIR server in the terminal window. Run `cd resources/`
2. Run the following command to package the SAM template into a cloudformation template. Replace the S3 bucket name with the bucket name that was created in earlier step.

`aws cloudformation package --template-file FHIRService-dev-swagger-apigateway.yaml --output-template-file serverless-output.yaml --s3-bucket <<Replace the S3 bucket name>>`

***Note : If you face any difficulties in copy pasting the above command, please edit command #1 in commands.txt file under the resources folder and paste it to the terminal window.***
[Image: Image.jpg]The output should look like as shown above in the screenshot. The command uploads the jar file to S3 bucket and generates a cloudformation template file serverless-output.yaml file which will be used to do the actual deployment of resources.

Feel free to open the serverless-output.yaml file in cloud9 IDE to check the contents.


1. Run the following command to deploy the stack using cloudformation. **Use your own stack name like FHIRDemoService and use it in the below command.**

`aws cloudformation deploy --template-file /home/ec2-user/environment/FHIRServer/resources/serverless-output.yaml --stack-name <<Enter Stack Name>> --capabilities CAPABILITY_IAM`

***Note : If you face any difficulties in copy pasting the above command, please edit command #2 in commands.txt file under the resources folder and paste it to the terminal window.***

The command will wait until all the resources are created. Once, the command finished and stack creation is complete, navigate to API gateway and lambda services to check the services that have been deployed by the cloudformation template.

Go to the AWS management console and search for API Gateway service. API Gateway should have the resources deployed as shown below:
[Image: Image.jpg]

Go to Lambda service and Lambda deployment should be listed as below:
[Image: image.png]
### Capability Statement

Capability statement for the FHIR server can be accessed without any authorization. It shows the various interactions that will be supported by the FHIR server. 

1. Navigate to API Gateway and click on the <<Your Stack Name>> link and go under Dashboard link on the left side. It should show a link on the right side. Copy the link and save it in a document. It will be referred as the **API_END_POINT** from here on.** Please make a note of it. It will be used in part -2 of the lab as well.**

[Image: image.png]
1. Go to the cloud9 terminal window again and run the following command:

`curl -H "Accept: application/fhir+json" <<API_END_POINT>>metadata | jq`

The output should be similar to as shown below in screenshot:
[Image: image.png]
## Cognito Setup

In this section we will create a user in Cognito user pool and provision a user for sending an authorized request to API Gateway. Amazon Cognito lets you add user sign-up, sign-in, and access control to your web and mobile apps quickly and easily. Amazon Cognito provides solutions to control access to AWS resources from your app. You can define roles and map users to different roles so your app can access only the resources that are authorized for each user.


1. Search for cognito in the services search bar on main page.
2. Click on Manage User Pools and it should show the user pools that have been created so far. There should be an entry for the stack name that was used **like** FHIRWorkshop.

[Image: Image.jpg]
1. Click on the FHIRWorkshop(or the stack name that was used) link from the user pool. 
2. Go to Users and groups under General settings.

[Image: image.png]
1. Click on create user button to create a new user.
2. Provide a username which will be used to authenticate against the user pool. Also, provide a temporary password. 

**Make sure to uncheck the box for “Send an invitation to this new user?” and also make sure to check the box “Mark email as verified?”**
[Image: Image.jpg]

Click on Create user to submit.
It should now show as below with status as FORCE_CHANGE_PASSWORD
[Image: Image.jpg]
1. Go to General settings and note down the Pool id. It will be used as part of a CLI command.

[Image: image.png]
1. Click on App client settings under App integration. Note down the ID.

[Image: Image.jpg]We will now authenticate against the pool that has been created.

1. Run the following command to authenticate the user from the cloud9 terminal.

`aws cognito-idp admin-initiate-auth --user-pool-id <<user-pool-id>> --client-id <<app-client-id>> --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters 'USERNAME=<<username>>,PASSWORD="<<Temp password>>"'`

***Note : If you face any difficulties in copy pasting the above command, please edit command #3 in commands.txt file under the resources folder and paste it to the terminal window.***

The output for the command should show as below:
[Image: image.png]
1. Note down the value shown for the session tag.
2. Run the following command to respond to the auth challenge with a new password.

`aws cognito-idp admin-respond-to-auth-challenge --user-pool-id <<user-pool-id>> --client-id <<app-client-id>> --challenge-name NEW_PASSWORD_REQUIRED --challenge-response 'USERNAME=<<userid>>,NEW_PASSWORD=<<new password>>' --session '<<session tag value>>'`

***Note : If you face any difficulties in copy pasting the above command, please edit command #4 in commands.txt file under the resources folder and paste it to the terminal window.***

The output should show as below:
[Image: image.png]**Note: Keep the cognito userid and password  saved in a notepad as it will be used in part 2 of the lab.**

Note down the value in the **IDToken** tag. The value is a JWT token and can be debugged at [jwt.io](http://jwt.io/)
The value will be used as part of the requests that are sent to API gateway for the RESTful services that have been deployed.

We are now ready to send requests to our FHIR server.

Lets test a GET patient request first to make sure there is no data loaded.

1. Go to the cloud9 terminal window again and run the following command:

`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Patient | jq`
The output should show as below:
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

user name : fhir-cm-workshop-user-at-681921237057
password : x2leDp58r8IdUO9PBlFPOgcMtByv1vdCC+6q0oPtTVQ=

```
git clone https://git-codecommit.us-west-2.amazonaws.com/v1/repos/FHIR-CM-Integration
```

1. Change directory to FHIR-CM-Integration/resources.
2. Run the following command:

```
chmod u+x setupenv.sh
. ./setupenv.sh
```

The above command will download maven, set the environment variables, download jdk 1.8 and remove jdk 1.7. It would also point the JAVA_HOME to jdk 1.8. Answer yes to any of the prompts for download.

## Generate FHIR test data

In this section we will populate the FHIR repository with test data generated using Synthea. It is a synthetic patient generator that models the medical history of synthetic patients.More details can be found [here](https://github.com/synthetichealth/synthea/blob/master/README.md).

1. Run the following command to go back to environment directory:

```
cd /home/ec2-user/environment
```

1. Download the synthea utility zip by running the following commands:

```
`wget https://s3-us-west-2.amazonaws.com/fhir-code-bucket-777/synthea.zip`
`tar -xvf synthea.zip`
```

1. Delete any existing files from synthea/output/fhir_stu3 directory by running following command:

```
rm synthea/output/fhir_stu3/*
```

1. Run the following commands to build synthea and generate FHIR test data. It will take a few minutes to build synthea.

```
`cd synthea`
`./run_synthea`
```

The tool will generate a FHIR resources file under **synthea/output/fhir_stu3** directory. The patient name would be shown in the output of the command.
[Image: image.png]
1. Open the file in cloud9 editor. 
2. We will note down some of the key fields of the patient record.
    1. Look for the field “**Medical Record Number**” and get the corresponding value for it.
    2. Look for the field “name” in the Patient resource. It should be few lines below the Medical Record Number. Note down the value for “**given**” tag under “name” and “**family**” under “name”.
    3. Look for “**gender**” field and note it down.
    4. Look for “**birthDate**” and note down the value.
    5. Look for “address” tag and note down the “**city**” value.

The above data will be used as part of the one of the scenarios.

## Building the  FHIR-CM-Integration project

1. Run the following from terminal:

```
cd /home/ec2-user/environment/FHIR-CM-Integration/
```

1. Next, build the project by running the following:

```
mvn install
```

The build process will create the jar file that will be deployed as the lambda functions to process the hl7 and FHIR resource files, call comprehend medical api's and make API calls to an existing FHIR repository.


## Load patient data

We will now load the patient record to an existing FHIR repository. 

1. Go to **resources** folder under **FHIR-CM-Integration**
2. Copy the generated file located under **/home/ec2-user/environment/synthea/output/fhir_stu3 **from the synthea utility to the **test-data/master-patient** folder under **resources**. The generated file should look like as below:


Use the below command to copy the file:

```
cp /home/ec2-user/environment/synthea/output/fhir_stu3/<<file name of the data file>> test-data/master-patient
```

1. Open the data file and make a note of the patient id in the file. It will be shown as the tag value as shown below:

"resource": {
 "resourceType": *"Patient"*,
 "id": *"**19fba9bf-516e-4d1c-9cf6-522c85765fe3**"*,
Make a note of the highlighted value. It will be referred to as the **PATIENT_ID**.

1. Run the following command to get a cognito id token. ***User id and password were created in the part 1 of the lab**.*

`aws cognito-idp initiate-auth --client-id 5obgic32lccfpqeef66ai1s0v --auth-flow USER_PASSWORD_AUTH --auth-parameters 'USERNAME=<<username>>,PASSWORD="Master123!"'`

1. Copy the ID Token value from the output of the command.
2. Open the file **post-test-data.sh under resources** and replace the id token value with copied token value. Replace the API_END_POINT with the value shown in the beginning of the workshop document.
3. Run the following command to load the master patient record in the FHIR repository.

```
`chmod u+x post-test-data.sh`
./post-test-data.sh
```

The output should be similar to below:

HTTP/2 201
date: Sun, 26 May 2019 02:50:10 GMT
content-type: application/fhir+json
content-length: 1093
x-amzn-requestid: f1d24d32-7f60-11e9-bda8-0b2c3832c35d
x-amz-apigw-id: aRTBKH_IPHcF_lg=
x-amzn-trace-id: Root=1-5ce9fed4-452f8ff5ef4e82a1ec476734;Sampled=1

{"resourceType":"OperationOutcome","id":"e2b00487-efff-48a2-a864-3c29c23ef48b","meta":{"versionId":"1"},"text":{"status":"generated","div":"<div xmlns=\"[http://www.w3.org/1999/xhtml\](http://www.w3.org/1999/xhtml/)"><h1>Operation Outcome</h1><table border=\"\\&quot;0\\&quot;\"><tr><td style=\"\\&quot;font-weight: bold;\\&quot;\">INFORMATION</td><td>[]</td><td><pre>Successfully created resource \\\"Patient/e2b00487-efff-48a2-a864-3c29c23ef48b/_history/1\\\" in 36ms</pre></td>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t</tr>\\n\\t\\t\\t<tr>\\n\\t\\t\\t\\t<td style=\"\\&quot;font-weight: bold;\\&quot;\">INFORMATION</td>\\n\\t\\t\\t\\t<td>[]</td>\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\t<td><pre>No issues detected during validation</pre></td>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t</tr>\\n\\t\\t</table>\\n\\t</div>"},"issue":[{"severity":"information","code":"informational","diagnostics":"Successfully created resource Bundle/e2b00487-efff-48a2-a864-3c29c23ef48b/_history/1"},{"severity":"information","code":"informational","diagnostics":"No issues detected during validation"}]}./test-data/master-patient-data/Emory494_Greenholt190_d598bdf7-a7fe-43ee-9409-58167420d8e8.json

1. Validate the data by running the following command:

`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Patient/<<PATIENT_ID>> | jq`

1. Get the conditions for the patient by running the following command

`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Condition?patient-ref-id=<<PATIENT_ID>> | jq`

**Make a note of the number of conditions for the patient. We will be adding additional conditions extracted from the clinical notes. It is possible that there were no conditions loaded previously.**

## Deploy the project code

We will now deploy the workshop code using a SAM(Serverless Access Model) template. More details about SAM can be found [here](https://docs.aws.amazon.com/serverless-application-model/index.html).

1. Go to resources folder under FHIR-CM-Integration
2. Create a S3 bucket to store the packaged artifacts for deployment or you can use the one that was previously created. Give it a unique name. Use the following command:

```
aws s3 mb s3://<<FHIR_CODE_BUCKET>>
```

1. Run the following command to package the code and other artifacts:

`aws cloudformation package --template-file FHIR-CM-Integration.yaml --output-template-file serverless-output.yaml --s3-bucket <<FHIR_CODE_BUCKET>>`

1. Run the following command to deploy the SAM template. You can choose any stack name like FHIR-CM-Integration.

`aws cloudformation deploy --template-file /home/ec2-user/environment/FHIR-CM-Integration/resources/serverless-output.yaml --stack-name <<STACK_NAME>> --capabilities CAPABILITY_IAM`

1. Login to aws console and go to cloudfromation service. Look for the template <<STACK_NAME>>. Under outputs, there will be the name of a S3 bucket that was created by the template. Note down the bucket name. It will be referred to as DATA_INPUT_BUCKET.
    

## HL7 Message Flow

In this part of the lab, we will upload an HL7 file which has OBX segment containing clinical notes. The deployed project would extract the notes from the HL7 message, lookup the patient from FHIR repository and enrich the record with the medical conditions extracted from the clinical notes in HL7.

1. Go to the resources/test-data folder under FHIR-CM-Integration by running following command from terminal.

`cd ~/environment/FHIR-CM-Integration/resources/test-data/`

1. Open the mdm.txt file in the editor. Take a backup of the file in case needed later.
2. Look for the place holders and replace them with the values copied from the data file generated using synthea. **Make sure date of birth is entered as YYYYMMdd, gender as M/F.**
3. The final file should look similar to below:

MSH|^~\&|Epic|Epic|||20160510071633||MDM^T02|12345|D|2.3
PID|1||68b1c58d-41cd-4855-a100-8206eb1b61b5^^^^EPI||Larkin917^Monroe732^J^^MR.^||19720817|M||AfrAm|377 _Kuhic_ Station Unit 91^^_Sturbridge_^MA^01507^US^^^DN |DN|(608)123-9998|(608)123-5679||S||18273652|123-45-9999||||^^^WI^^
PV1|||^^^CARE HEALTH SYSTEMS^^^^^||||||1173^MATTHEWS^JAMES^A^^^||||||||||||
TXA||CN||20160510071633|1173^MATTHEWS^JAMES^A^^^|||||||^^12345|||||PA|
OBX|1|TX|||Clinical summary: Based on the information provided, the patient likely has viral _sinusitis_ commonly called a head cold.
OBX|2|TX|||Diagnosis: Viral _Sinusitis_
OBX|3|TX|||Diagnosis ICD: J01.90
OBX|4|TX|||Prescription: _benzonatate_ (_Tessalon_ _Perles_) 100mg oral tablet 30 tablets, 5 days supply. Take one to two tablets by mouth three times a day as needed. _disp_. 30. Refills: 0, Refill as needed: no, Allow substitutions: yes

1. Upload the file to s3 folder using the command below:

```
aws s3 cp mdm.txt s3://<<DATA_INPUT_BUCKET>>/input/hl7/mdm.txt
```


The above command should trigger the step functions. You can monitor the progress of the step functions by login to the console and going to the step functions service. Click on the step function that is deployed as part of the workshop. It would show the progress of the various steps.
[Image: image.png]
1. Run the below command again to lookup the conditions for the patient:

`curl -H "Accept: application/fhir+json" -H "Authorization:<<ID Token>>" <<API_END_POINT>>Condition?patient-ref-id=<<PATIENT_ID>> | jq`

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


 
 
 
 
 
