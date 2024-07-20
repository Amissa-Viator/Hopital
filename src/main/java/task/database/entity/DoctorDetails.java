package task.database.entity;

public class DoctorDetails {
    private Integer id;
    private String name;
    private String lastName;
    private String specialization;
    private String hospitalName;
    private String hospitalAddress;

    public DoctorDetails(String name, String lastName,String hospitalName, String hospitalAddress){
        this.name = name;
        this.lastName = lastName;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
    };
    public DoctorDetails(Integer id, String name, String lastName, String specialization, String hospitalName, String hospitalAddress) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.specialization = specialization;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }
}
