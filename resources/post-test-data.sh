for entry in "/home/ec2-user/environment/amazon-comprehend-medical-fhir-integration/test-data/master-patient-data"/*.json
do
  # include the cognito id token and make the endpoint as a parameter
  curl -H "Content-Type: application/fhir+json" -H "Authorization: <<IDToken>>" -i --data "@$entry" <<API_END_POINT>>Bundle
  echo "$entry"
done