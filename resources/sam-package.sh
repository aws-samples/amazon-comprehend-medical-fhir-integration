echo on
aws cloudformation package --template-file FHIR-CM-Integration.yaml --output-template-file serverless-output.yaml --s3-bucket fhir-code-bucket