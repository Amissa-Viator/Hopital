package task.hospital;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import task.database.entity.ExamDetails;
import task.database.entity.Patients;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FindExam {
    @FXML
    private Button findBtn;
    @FXML
    private ChoiceBox<String> patientsBox;
    private List<Patients> patients = new ArrayList<>();
    private List<ExamDetails> exams = new ArrayList<>();
    private int selectedPatientId;
    private Stage stage;
    @FXML
    void initialize() {
        readInfo();
        patientsBox.setOnAction(event -> {
            String selected = patientsBox.getValue();
            if (selected != null) {
                for(Patients choice: patients) {
                    String sentence = choice.getName() + " " + choice.getLastName();
                    if(sentence.equals(selected)) {
                        selectedPatientId = choice.getId();
                    }
                }
            }
        });
        findBtn.setOnAction(event -> handleFind());
    }
    private void readInfo() {
        try {
            String sql = "SELECT * FROM patients";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            patientsBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("pname");
                String surname = resultSet.getString("plast_name");
                int age = resultSet.getInt("age");
                Patients patient = new Patients(id, name, surname, age);
                patients.add(patient);
                patientsBox.getItems().add(name+ " " + surname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void handleFind() {
        String sql = "SELECT c.id AS card_id, le.id AS exam_id, le.data, le.type, le.results, le.unit_of_measurement, le.normal_values, d.last_name AS doctor_last_name " +
                "FROM card c " +
                "JOIN laboratory_examinations le ON c.id_labexam = le.id " +
                "JOIN doctors d ON le.id_doctor = d.id " +
                "WHERE c.id_patient = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, selectedPatientId);
            ResultSet resultSet = statement.executeQuery();
            exams.clear();
            while (resultSet.next()) {
                String data = resultSet.getString("data");
                String type = resultSet.getString("type");
                int results = resultSet.getInt("results");
                String unitOfMeasurement = resultSet.getString("unit_of_measurement");
                int normalValues = resultSet.getInt("normal_values");
                String doctorLastName = resultSet.getString("doctor_last_name");
                ExamDetails exam = new ExamDetails();
                exam.setExaminationData(data);
                exam.setExaminationResults(results);
                exam.setExaminationType(type);
                exam.setExaminationNormalValues(normalValues);
                exam.setDoctorLastName(doctorLastName);
                exam.setExaminationUnitOfMeasurement(unitOfMeasurement);
                exams.add(exam);
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
            controller.displayList(exams);
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
