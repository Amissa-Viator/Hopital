package task.database.entity;

public class Doctors {
    private Integer id;
    private String name;
    private String lastName;
    private Integer idOfSpecialization;

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

    public Doctors(Integer id, String name, String lastName, Integer idOfSpecialization) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.idOfSpecialization = idOfSpecialization;
    }

    public Doctors(Integer id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getIdOfSpecialization() {
        return idOfSpecialization;
    }

    public void setIdOfSpecialization(Integer idOfSpecialization) {
        this.idOfSpecialization = idOfSpecialization;
    }
}
