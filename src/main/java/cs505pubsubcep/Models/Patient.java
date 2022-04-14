package cs505pubsubcep.Models;

import java.util.List;

public class Patient {

    /*

    testing_id: id of testing facility
    patient_mrn: patient medical record number (UUID)

    patient_name: name of patient

    patient_zipcode: zipcode of patient

    patient_status: positive = 1, negative = 0

    contact_list: list of patient_mrns that are known to have been in contact

    event_list: list of event_id that the person visited

    */
    private int testing_id;
    private String patient_mrn;
    private String patient_name;
    private int patient_zipcode;
    private int patient_status;
    private List<String> contact_list;
    private List<String> event_list;

    @Override
    public String toString() {
        return "Patient{" +
                "testing_id=" + testing_id +
                ", patient_mrn='" + patient_mrn + '\'' +
                ", patient_name='" + patient_name + '\'' +
                ", patient_zipcode=" + patient_zipcode +
                ", patient_status=" + patient_status +
                ", contact_list=" + contact_list +
                ", event_list=" + event_list +
                '}';
    }

    public Patient(int testing_id, String patient_mrn, String patient_name, int patient_zipcode, int patient_status, List<String> contact_list, List<String> event_list) {
        this.testing_id = testing_id;
        this.patient_mrn = patient_mrn;
        this.patient_name = patient_name;
        this.patient_zipcode = patient_zipcode;
        this.patient_status = patient_status;
        this.contact_list = contact_list;
        this.event_list = event_list;
    }

    public int getTesting_id() {
        return testing_id;
    }

    public void setTesting_id(int testing_id) {
        this.testing_id = testing_id;
    }

    public String getPatient_mrn() {
        return patient_mrn;
    }

    public void setPatient_mrn(String patient_mrn) {
        this.patient_mrn = patient_mrn;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public int getPatient_zipcode() {
        return patient_zipcode;
    }

    public void setPatient_zipcode(int patient_zipcode) {
        this.patient_zipcode = patient_zipcode;
    }

    public int getPatient_status() {
        return patient_status;
    }

    public void setPatient_status(int patient_status) {
        this.patient_status = patient_status;
    }

    public List<String> getContact_list() {
        return contact_list;
    }

    public void setContact_list(List<String> contact_list) {
        this.contact_list = contact_list;
    }

    public List<String> getEvent_list() {
        return event_list;
    }

    public void setEvent_list(List<String> event_list) {
        this.event_list = event_list;
    }
}
