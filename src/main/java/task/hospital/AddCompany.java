package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.Medicine;
import task.database.entity.Specializations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddCompany {
    @FXML
    private TextField addressFld;
    @FXML
    private Button addBtn;
    @FXML
    private TextField companyFld;

    @FXML
    private TextField infoFld;

    @FXML
    private ChoiceBox<String> medicineBox;
    private int selectedMedicineId;
    private List<Medicine> medicine = new ArrayList<>();
    private int companyId;
    private Stage stage;
    private boolean isUpdate = false;

    @FXML
    void initialize() {
        readInfo();
        medicineBox.setOnAction(event -> {
            String selectedMedicine = medicineBox.getValue();
            if (selectedMedicine != null) {
                for(Medicine choice: medicine) {
                    if(choice.getName().equals(selectedMedicine)) {
                        selectedMedicineId = choice.getId();
                    }
                }
            }
        });
        addBtn.setOnAction(event -> handleInsert());
    }

    private void readInfo() {
        try {
            String sql = "SELECT id, name FROM medicine";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            medicineBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String medicineName = resultSet.getString("name");
                Medicine newMedicine = new Medicine();
                newMedicine.setId(id);
                newMedicine.setName(medicineName);
                medicine.add(newMedicine);
                medicineBox.getItems().add(medicineName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void handleInsert() {
        String newName = companyFld.getText();
        String newAddress = addressFld.getText();
        String newInfo = infoFld.getText();
        if(newName.isEmpty() || newAddress.isEmpty() || newInfo.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(newName, newAddress, newInfo);
            } else {
                update(newName, newAddress, newInfo);
            }
        }
    }

    private void insert(String companyName, String address, String info) {
        String insertCompany = "INSERT INTO pharmaceutical_company (name, address, info) VALUES (?,?,?)";
        String insertConnection = "INSERT INTO company_medicine (id_medicine, id_company) VALUES (?, ?)";

        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertCompany, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, companyName);
            insertState.setString(2, address);
            insertState.setString(3, info);
            insertState.executeUpdate();
            var generatedKeys = insertState.getGeneratedKeys();
            int idCompany;
            if (generatedKeys.next()) {
                idCompany = generatedKeys.getInt("id");
            } else {
                throw new SQLException("Creating comapny failed, no ID obtained.");
            }
            PreparedStatement statement = Main.CONNECTION.prepareStatement(insertConnection, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, selectedMedicineId);
            statement.setInt(2, idCompany);
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

    private void update(String companyName, String address, String info) {
        String updateCompanyQuery = "UPDATE pharmaceutical_company SET name=?, address=?, info=? WHERE id=?";
        String updateConnection = "UPDATE company_medicine SET id_medicine=? WHERE id_company=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateCompanyQuery);
            statement.setString(1, companyName);
            statement.setString(2, address);
            statement.setString(3, info);
            statement.setInt(4, companyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateConnection);
            statement.setInt(1, selectedMedicineId);
            statement.setInt(2, companyId);
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

    public void setIdCompany(int idCompany) {
        this.companyId = idCompany;
    }
    public void setTextField(String companyName, String address, String info) {
        companyFld.setText(companyName);
        addressFld.setText(address);
        infoFld.setText(info);
    }
}
