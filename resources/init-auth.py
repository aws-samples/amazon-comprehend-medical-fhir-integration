import boto3
import sys

client = boto3.client('cognito-idp')

response = client.initiate_auth(
    AuthFlow='USER_PASSWORD_AUTH',
    AuthParameters={
        'USERNAME': 'workshopuser',
        'PASSWORD': 'Master123!'
    },

    ClientId=sys.argv[1]
)

sessionid = response['AuthenticationResult']['IdToken']
print(sessionid)