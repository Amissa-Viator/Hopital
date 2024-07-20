package task.hospital;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import task.database.entity.Diagnosis;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindMedicalInfo {

    @FXML
    private ChoiceBox<String> diagnosisBox;
    @FXML
    private Button findBtn;
    private Stage stage;
    private List<Diagnosis> diagnosisList = new ArrayList<>();
    private List<MedicalInfo> info = new ArrayList<>();

    private int selectedDiagnosisId;

    @FXML
    void initialize() {
        readInfo();
        diagnosisBox.setOnAction(event -> {
            String selected = diagnosisBox.getValue();
            if (selected != null) {
                for(Diagnosis choice: diagnosisList) {
                    if(choice.getDiagnosis().equals(selected)) {
                        selectedDiagnosisId = choice.getId();
                    }
                }
            }
        });
        findBtn.setOnAction(event -> handleFind());
    }

    private void readInfo() {
        try {
            String sql = "SELECT * FROM diagnosis";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            diagnosisBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("diagnosis");
                Diagnosis newDiagnosis = new Diagnosis();
                newDiagnosis.setId(id);
                newDiagnosis.setDiagnosis(name);
                diagnosisList.add(newDiagnosis);
                diagnosisBox.getItems().add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void handleFind() {
        String sql = "SELECT m.name AS medicine_name, pc.name AS company_name, pc.address AS company_address " +
                "FROM diagnosis_medicine dm " +
                "JOIN medicine m ON dm.id_medicine = m.id " +
                "JOIN company_medicine cm ON m.id = cm.id_medicine " +
                "JOIN pharmaceutical_company pc ON cm.id_company = pc.id " +
                "WHERE dm.id_diagnosis = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, selectedDiagnosisId);
            ResultSet resultSet = statement.executeQuery();
            info.clear();
            while (resultSet.next()) {
                String medicineName = resultSet.getString("medicine_name");
                String company = resultSet.getString("company_name");
                String address = resultSet.getString("company_address");
                MedicalInfo medicine = new MedicalInfo(company, address,medicineName);
                info.add(medicine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        show();
    }


    private void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("show.fxml"));
            Parent parent = loader.load();
            ShowController controller = loader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);
            controller.displayList(info);
            newStage.setScene(new Scene(parent));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(stage);
            newStage.initStyle(StageStyle.UTILITY);
            newStage.showAndWait();
            Stage currentStage = (Stage) findBtn.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            System.out.println("Error");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
