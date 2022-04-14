package cs505pubsubcep.Models;

public class Vaccination {

    /*
    vaccination_id: id of testing facility
    patient_mrn: patient medical record number (UUID)

    patient_name: name of patient
     */
    private int vaccination_id;
    private String patient_mrn;
    private String patient_name;

    @Override
    public String toString() {
        return "Vaccination{" +
                "vaccination_id=" + vaccination_id +
                ", patient_mrn='" + patient_mrn + '\'' +
                ", patient_name='" + patient_name + '\'' +
                '}';
    }

    public Vaccination(int vaccination_id, String patient_mrn, String patient_name) {
        this.vaccination_id = vaccination_id;
        this.patient_mrn = patient_mrn;
        this.patient_name = patient_name;
    }

    public int getVaccination_id() {
        return vaccination_id;
    }

    public void setVaccination_id(int vaccination_id) {
        this.vaccination_id = vaccination_id;
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
}
