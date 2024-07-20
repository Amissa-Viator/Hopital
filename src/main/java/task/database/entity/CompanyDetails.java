package task.database.entity;

public class CompanyDetails {
    private Integer idMed;
    private Integer idCompany;
    private String medicine;
    private String companyName;
    private String address;
    private String info;

    public CompanyDetails(Integer idMed, Integer idCompany, String medicine, String companyName, String address, String info) {
        this.idMed = idMed;
        this.idCompany = idCompany;
        this.medicine = medicine;
        this.companyName = companyName;
        this.address = address;
        this.info = info;
    }

    public Integer getIdMed() {
        return idMed;
    }

    public void setIdMed(Integer idMed) {
        this.idMed = idMed;
    }

    public Integer getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Integer idCompany) {
        this.idCompany = idCompany;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
