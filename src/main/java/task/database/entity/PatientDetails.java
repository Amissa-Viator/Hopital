package task.database.entity;

public class PatientDetails {
    private Integer id;
    private String name;
    private String lastName;
    private Integer age;
    private String ageGroup;

    public PatientDetails(Integer id, String name, String lastName, Integer age, String ageGroup) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.ageGroup = ageGroup;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
