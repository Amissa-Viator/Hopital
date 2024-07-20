package task.hospital;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import task.database.entity.Diagnosis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import task.database.entity.DoctorDetails;
import task.database.entity.Doctors;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindHospitals {
    @FXML
    private ChoiceBox<String> doctorsBox;
    private List<Doctors> doctors = new ArrayList<>();
    private List<DoctorDetails> info = new ArrayList<>();
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
        findBtn.setOnAction(event -> handleFind());
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

    private void handleFind() {
        String sql = "SELECT d.name AS doctor_name, d.last_name AS doctor_last_name, " +
                "h.h_name AS hospital_name, h.address AS hospital_address " +
                "FROM doctors d " +
                "JOIN hospital_doctor hd ON d.id = hd.id_doctor " +
                "JOIN hospitals h ON hd.id_hospital = h.id " +
                "WHERE d.id = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, selectedDoctorId);
            ResultSet resultSet = statement.executeQuery();
            info.clear();
            while (resultSet.next()) {
                String doctorName = resultSet.getString("doctor_name");
                String doctorLastName = resultSet.getString("doctor_last_name");
                String hospitalName = resultSet.getString("hospital_name");
                String hospitalAddress = resultSet.getString("hospital_address");
                DoctorDetails doctor = new DoctorDetails(doctorName,doctorLastName,hospitalName,hospitalAddress);
                info.add(doctor);
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
