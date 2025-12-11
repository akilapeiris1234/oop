package medicare.model;

import java.util.Objects;

/** Stores patient identity and medical details. */
public class Patient extends Person {
    private String nicOrPassport;
    private String gender;
    private int age;
    private String medicalHistory;

    public Patient(String fullName, String nicOrPassport, String gender, int age, String contactNumber, String address,
            String medicalHistory) {
        super(fullName, contactNumber, address);
        this.nicOrPassport = nicOrPassport;
        this.gender = gender;
        this.age = age;
        this.medicalHistory = medicalHistory;
    }

    public String getNicOrPassport() {
        return nicOrPassport;
    }

    public void setNicOrPassport(String nicOrPassport) {
        this.nicOrPassport = nicOrPassport;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Patient patient = (Patient) obj;
        return Objects.equals(getId(), patient.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

