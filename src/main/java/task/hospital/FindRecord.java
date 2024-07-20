package task.hospital;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import task.database.entity.DoctorDetails;
import task.database.entity.Doctors;
import javafx.scene.control.TextField;
import task.database.entity.Patients;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindRecord {
    @FXML
    private TextField dataFld;
    private List<Doctors> doctors = new ArrayList<>();
    private List<Patients> patients = new ArrayList<>();
    @FXML
    private ChoiceBox<String> doctorsBox;
    private int selectedDoctorId;
    @FXML
    private Button findBtn;
    private Stage stage;
    @FXML
    void initialize() {
        readInfo();
        doctorsBox.setOnAction(event -> {
            String selected = doctorsBox.getValue();
            if (selected != null) {
                for(Doctors choice: doctors) {
                    String sentence = choice.getName() + " " + choice.getLastName();
                    if(sentence.equals(selected)) {
                        selectedDoctorId = choice.getId();
                    }
                }
            }
        });
        findBtn.setOnAction(event -> check());
    }
    private void readInfo() {
        try {
            String sql = "SELECT * FROM doctors";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            doctorsBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("last_name");
                int specialization = resultSet.getInt("id_specialization");
                Doctors doctor = new Doctors(id, name, surname, specialization);
                doctors.add(doctor);
                doctorsBox.getItems().add(name+ " " + surname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void check() {
        String data = dataFld.getText();
        if(data.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            handleFind(data);
        }
    }
    private void handleFind(String data) {
        String sql = "SELECT p.id AS patient_id, p.pname AS patient_name, p.plast_name AS patient_last_name, p.age AS patient_age " +
                "FROM patients p " +
                "JOIN doctor_patient dp ON p.id = dp.id_patient " +
                "WHERE dp.id_doctor = ? AND dp.data = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, selectedDoctorId);
            statement.setString(2, data);
            ResultSet resultSet = statement.executeQuery();
            patients.clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("patient_id");
                String name = resultSet.getString("patient_name");
                String lastName = resultSet.getString("patient_last_name");
                int age = resultSet.getInt("patient_age");
                Patients patient = new Patients(id, name, lastName,age);
                patients.add(patient);
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
            controller.displayList(patients);
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
