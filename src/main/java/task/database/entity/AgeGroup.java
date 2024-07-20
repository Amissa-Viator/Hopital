package task.database.entity;

public class AgeGroup {
    private Integer id;
    private String ageCategory;

    public AgeGroup(Integer id, String ageCategory) {
        this.id = id;
        this.ageCategory = ageCategory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(String ageCategory) {
        this.ageCategory = ageCategory;
    }
}
