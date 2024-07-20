package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.Category;
import task.database.entity.Diagnosis;
import task.database.entity.Medicine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddMedicine {
    @FXML
    private Button addBtn;

    @FXML
    private ChoiceBox<String> categoryBox;

    @FXML
    private TextField comFld;

    @FXML
    private ChoiceBox<String> diagnosisBox;

    @FXML
    private TextField formFld;

    @FXML
    private TextField infoFld1;

    @FXML
    private TextField medicineFld;
    private int selectedCategoryId;
    private int selectedDiagnosisId;
    private List<Diagnosis> diagnosis = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private int medicineId;
    private Stage stage;
    private boolean isUpdate = false;

    @FXML
    void initialize() {
        readInfo();
        diagnosisBox.setOnAction(event -> {
            String selectedDiagnosis = diagnosisBox.getValue();
            if (selectedDiagnosis != null) {
                for(Diagnosis choice: diagnosis) {
                    if(choice.getDiagnosis().equals(selectedDiagnosis)) {
                        selectedDiagnosisId = choice.getId();
                    }
                }
            }
        });
        categoryBox.setOnAction(event -> {
            String selectedCategory = categoryBox.getValue();
            if (selectedCategory != null) {
                for(Category choice: categories) {
                    if(choice.getNameOfCategory().equals(selectedCategory)) {
                        selectedCategoryId = choice.getId();
                    }
                }
            }
        });
        addBtn.setOnAction(event -> handleInsert());
    }

    private void readInfo() {
        try {
            String sql = "SELECT * FROM diagnosis";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            diagnosisBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String diagnosisName = resultSet.getString("diagnosis");
                Diagnosis newDiagnosis = new Diagnosis();
                newDiagnosis.setId(id);
                newDiagnosis.setDiagnosis(diagnosisName);
                diagnosis.add(newDiagnosis);
                diagnosisBox.getItems().add(diagnosisName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            String sql = "SELECT * FROM category";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            categoryBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String categoryName = resultSet.getString("name_of_category");
                Category newCategory = new Category();
                newCategory.setId(id);
                newCategory.setNameOfCategory(categoryName);
                categories.add(newCategory);
                categoryBox.getItems().add(categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void handleInsert() {
        String newMedicine = medicineFld.getText();
        String newComposition = comFld.getText();
        String newForm = formFld.getText();
        String newInfo = infoFld1.getText();
        if(newMedicine.isEmpty() || newComposition.isEmpty() || newForm.isEmpty() || newInfo.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(newMedicine, newComposition, newForm,newInfo);
            } else {
                update(newMedicine, newComposition, newForm,newInfo);
            }
        }
    }

    private void insert(String medicineName, String composition, String form, String info) {
        String insertMedicine = "INSERT INTO medicine (name,composition,release_form,dosage,id_category) VALUES (?,?,?,?,?)";
        String insertConnection = "INSERT INTO diagnosis_medicine (id_medicine, id_diagnosis) VALUES (?, ?)";

        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertMedicine, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, medicineName);
            insertState.setString(2, composition);
            insertState.setString(3, form);
            insertState.setString(4, info);
            insertState.setInt(5, selectedCategoryId);
            insertState.executeUpdate();
            var generatedKeys = insertState.getGeneratedKeys();
            int idMedicine;
            if (generatedKeys.next()) {
                idMedicine = generatedKeys.getInt("id");
            } else {
                throw new SQLException("Creating medicine failed, no ID obtained.");
            }
            PreparedStatement statement = Main.CONNECTION.prepareStatement(insertConnection, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, idMedicine);
            statement.setInt(2, selectedDiagnosisId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    void close() {
        stage.close();
    }

    private void update(String medicineName, String composition, String form, String info) {
        String updateMedicineQuery = "UPDATE medicine SET name=?, composition=?,release_form=?,dosage=?,id_category=? WHERE id=?";
        String updateConnection = "UPDATE diagnosis_medicine SET id_diagnosis=? WHERE id_medicine=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateMedicineQuery);
            statement.setString(1, medicineName);
            statement.setString(2, composition);
            statement.setString(3, form);
            statement.setString(4, info);
            statement.setInt(5, selectedCategoryId);
            statement.setInt(6, medicineId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateConnection);
            statement.setInt(1, selectedDiagnosisId);
            statement.setInt(2, medicineId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public void setIdMedicine(int idMedicine) {
        this.medicineId = idMedicine;
    }

    public void setTextField(String medicine, String composition, String form, String dosage) {
        medicineFld.setText(medicine);
        comFld.setText(composition);
        formFld.setText(form);
        infoFld1.setText(dosage);
    }

}
