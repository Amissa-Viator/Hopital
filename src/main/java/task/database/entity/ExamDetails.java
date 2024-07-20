package task.database.entity;

public class ExamDetails {
    private int idPatient;
    private int idExam;
    private int idDoctor;
    private String patientName;
    private String patientLastName;
    private String examinationData;
    private String examinationType;
    private int examinationResults;
    private String examinationUnitOfMeasurement;
    private int examinationNormalValues;
    private String doctorLastName;

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public int getIdExam() {
        return idExam;
    }

    public void setIdExam(int idExam) {
        this.idExam = idExam;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public String getExaminationData() {
        return examinationData;
    }

    public void setExaminationData(String examinationData) {
        this.examinationData = examinationData;
    }

    public String getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(String examinationType) {
        this.examinationType = examinationType;
    }

    public int getExaminationResults() {
        return examinationResults;
    }

    public void setExaminationResults(int examinationResults) {
        this.examinationResults = examinationResults;
    }

    public String getExaminationUnitOfMeasurement() {
        return examinationUnitOfMeasurement;
    }

    public void setExaminationUnitOfMeasurement(String examinationUnitOfMeasurement) {
        this.examinationUnitOfMeasurement = examinationUnitOfMeasurement;
    }

    public int getExaminationNormalValues() {
        return examinationNormalValues;
    }

    public void setExaminationNormalValues(int examinationNormalValues) {
        this.examinationNormalValues = examinationNormalValues;
    }

    public String getDoctorLastName() {
        return doctorLastName;
    }

    public void setDoctorLastName(String doctorLastName) {
        this.doctorLastName = doctorLastName;
    }
}
