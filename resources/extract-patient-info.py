import json
import sys

filename = sys.argv[1]
 
print(filename) 
 
with open (filename) as json_file:
    data = json.load(json_file)
 
    patient_resources = [ x for x in data['entry'] if x['resource']['resourceType'] == 'Patient']
    patient = patient_resources[0]['resource']
     
    #mrn
    identifiers = patient['identifier']
    mrn_id = [ x for x in identifiers if 'type' in x and 'text' in x['type'] and x['type']['text'] == 'Medical Record Number' ]
    print ('')
    print('Medical Record Number : ' + mrn_id[0]['value'])
    #name
    print('Given Name : ' + patient['name'][0]['given'][0])
    print('Family Name : ' + patient['name'][0]['family'])
    #gender
    print('Gender : ' + patient['gender'])
    #birthDate
    print('Birthdate : ' + patient['birthDate'])
    #address - note city
    print('City : ' + patient['address'][0]['city'])
    print('Patient ID : ' + patient['id'])
    print ('')