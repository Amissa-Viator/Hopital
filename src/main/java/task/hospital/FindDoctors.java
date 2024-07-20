package task.hospital;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.StageStyle;
import task.database.entity.Doctors;
import task.database.entity.Specializations;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindDoctors {
    @FXML
    private Button findBtn;
    @FXML
    private ChoiceBox<String> specialBox;
    private Stage stage;
    private List<Specializations> specializationsList = new ArrayList<>();
    private List<Doctors> doctors = new ArrayList<>();
    private int selectedSpecialization;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    void initialize() {
        readInfo();
        specialBox.setOnAction(event -> {
            String selectedSpec = specialBox.getValue();
            if (selectedSpec != null) {
                for(Specializations choice: specializationsList) {
                    if(choice.getNameOfSpecialization().equals(selectedSpec)) {
                        selectedSpecialization = choice.getId();
                    }
                }
            }
        });
        findBtn.setOnAction(event -> handleFind());
    }

    private void readInfo() {
        try {
            String sql = "SELECT * FROM specializations";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            specialBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name_of_specialization");
                Specializations specialization = new Specializations(id,name);
                specializationsList.add(specialization);
                specialBox.getItems().add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void handleFind() {
        String sql = "SELECT * FROM doctors WHERE id_specialization=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, selectedSpecialization);
            ResultSet resultSet = statement.executeQuery();
            doctors.clear();
            while (resultSet.next()) {
                int doctorId = resultSet.getInt("id");
                String doctorName = resultSet.getString("name");
                String doctorLastName = resultSet.getString("last_name");
                Doctors doctor = new Doctors(doctorId, doctorName, doctorLastName);
                doctors.add(doctor);
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
            controller.displayList(doctors);
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
}
