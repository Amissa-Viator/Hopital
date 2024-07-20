package task.hospital;

public class MedicalInfo {
    private String company;
    private String address;
    private String medicine;

    public MedicalInfo(String company, String address, String medicine) {
        this.company = company;
        this.address = address;
        this.medicine = medicine;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }
}
