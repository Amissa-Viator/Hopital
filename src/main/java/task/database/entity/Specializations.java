package task.database.entity;

public class Specializations {
    private Integer id;
    private String nameOfSpecialization;

    public Specializations(Integer id, String nameOfSpecialization) {
        this.id = id;
        this.nameOfSpecialization = nameOfSpecialization;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameOfSpecialization() {
        return nameOfSpecialization;
    }

    public void setNameOfSpecialization(String nameOfSpecialization) {
        this.nameOfSpecialization = nameOfSpecialization;
    }
}
