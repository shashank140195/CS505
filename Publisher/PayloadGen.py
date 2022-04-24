import random
import names
import uuid
import json


patientCode = ["0", "1"]
event_master = []
contact_master = []
patient_list = []
possitive_patient_list = []
hospital_list = []
vax_list = []


def get_zip():

    zip_map = dict()
    zipcodesFile = 'hospitals.csv'

    file1 = open(zipcodesFile, 'r')
    kyZipCodes = file1.readlines()

    count = 0
    # Strips the newline character
    for zipLine in kyZipCodes:

        if (count > 0):
            zipLine = zipLine.strip()
            zipSplit = zipLine.split(",")
            zip_map[zipSplit[5]] = zipSplit[0]

        count = count + 1

    return zip_map


def getrandpayload():
    return getpayload()

def getpayload(zip_map, count):

    for n in range(random.randint(1, count)):
        event_master.append(str(uuid.uuid1()))

    for n in range(random.randint(1, count)):
        contact_master.append(str(uuid.uuid1()))

    for contact in contact_master:
        person = getperson(zip_map, count, contact)
        patient_list.append(person)

        if person["patient_status"] == '1':
            possitive_patient_list.append(person)

    for patient in possitive_patient_list:
        hospital = gethospital(zip_map, patient)
        hospital_list.append(hospital)

    for patient in possitive_patient_list:
        if random.randint(0, 1) == 1:
            vax_list.append(getvax(patient))

    patient_list_json = json.dumps(patient_list, indent=4)
    hospital_list_json = json.dumps(hospital_list, indent=4)
    vax_list_json = json.dumps(vax_list, indent=4)

    event_master.clear()
    contact_master.clear()
    patient_list.clear()
    possitive_patient_list.clear()
    hospital_list.clear()
    vax_list.clear()

    return patient_list_json, hospital_list_json, vax_list_json

def getperson(zip_map, count, patient_mrn):
    testing_id = random.randint(1, 10)
    patient_name = names.get_first_name() + ' ' + names.get_last_name()
    patient_zipcode = str(random.choice(list(zip_map.items()))[0])
    patient_status_code = random.choice(patientCode)

    patientRecord = dict()
    patientRecord["testing_id"] = testing_id
    patientRecord["patient_name"] = patient_name
    patientRecord["patient_mrn"] = patient_mrn
    patientRecord["patient_zipcode"] = patient_zipcode
    patientRecord["patient_status"] = patient_status_code
    patientRecord["contact_list"] = random.choices(contact_master, k=random.randint(1, count/2))
    patientRecord["event_list"] = random.choices(event_master, k=random.randint(1, count / 2))

    return patientRecord

def gethospital(zip_map, person):
    hospital_id = zip_map[person['patient_zipcode']]
    patient_name = person['patient_name']
    patient_mrn = person['patient_mrn']
    patient_status = random.randint(1, 3)

    hospitalRecord = dict()
    hospitalRecord['hospital_id'] = hospital_id
    hospitalRecord['patient_name'] = patient_name
    hospitalRecord['patient_mrn'] = patient_mrn
    hospitalRecord['patient_status'] = patient_status

    return hospitalRecord


def getvax(person):

    vaccination_id = random.randint(1, 10)
    patient_name = person['patient_name']
    patient_mrn = person['patient_mrn']

    vaxRecord = dict()
    vaxRecord['vaccination_id'] = vaccination_id
    vaxRecord['patient_name'] = patient_name
    vaxRecord['patient_mrn'] = patient_mrn

    return vaxRecord
