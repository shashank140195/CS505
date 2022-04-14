package cs505pubsubcep.Models;

public class Hospital {
    /*
    hospital_id: id of testing facility
    patient_mrn: patient medical record number (UUID)

    patient_name: name of patient

    patient_status: in-paitent = 1, icu = 2, vent =3
    */

    private int hospital_id;
    private String patient_mrn;
    private String patient_name;
    private int patient_status;

    @Override
    public String toString() {
        return "Hospital{" +
                "hospital_id=" + hospital_id +
                ", patient_mrn='" + patient_mrn + '\'' +
                ", patient_name='" + patient_name + '\'' +
                ", patient_status=" + patient_status +
                '}';
    }

    public Hospital(int hospital_id, String patient_mrn, String patient_name, int patient_status) {
        this.hospital_id = hospital_id;
        this.patient_mrn = patient_mrn;
        this.patient_name = patient_name;
        this.patient_status = patient_status;
    }

    public int getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(int hospital_id) {
        this.hospital_id = hospital_id;
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

    public int getPatient_status() {
        return patient_status;
    }

    public void setPatient_status(int patient_status) {
        this.patient_status = patient_status;
    }
}
