for entry in "/home/ec2-user/environment/synthea/output/fhir_stu3"/*.json
do
  # include the cognito id token and make the endpoint as a parameter
  curl -H "Content-Type: application/fhir+json" -H "Authorization: <<IDToken>>" -i --data "@$entry" <<API_END_POINT>>Bundle
  echo "$entry"
done