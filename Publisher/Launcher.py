import time

from PayloadGen import getpayload, get_zip
from Publisher import pub

#generate initial data
zip_map = get_zip()


#uncomment to create local test files

patient_list, hospital_list, vax_list = getpayload(zip_map, 100)

text_file = open("patient_data.json", "w")
n = text_file.write(patient_list)
text_file.close()

text_file = open("hospital_data.json", "w")
n = text_file.write(hospital_list)
text_file.close()

text_file = open("vax_data.json", "w")
n = text_file.write(vax_list)
text_file.close()



while True:

    patient_list, hospital_list, vax_list = getpayload(zip_map, 10)

    for x in range(1,20):
        pub(str(x), patient_list, 'patient_list')
        pub(str(x), hospital_list, 'hospital_list')
        pub(str(x), vax_list, 'vax_list')

    time.sleep(5)
