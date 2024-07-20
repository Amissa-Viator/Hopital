package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.Diagnosis;
import task.database.entity.Doctors;
import task.database.entity.Patients;
import task.database.entity.Specializations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddCard {
    @FXML
    private Button addBtn;
    @FXML
    private TextField dataField;
    @FXML
    private ChoiceBox<String> diagnosisBox;
    @FXML
    private ChoiceBox<String> doctorBox;
    @FXML
    private ChoiceBox<String> patientBox;
    private List<Doctors> doctors = new ArrayList<>();
    private List<Patients> patients = new ArrayList<>();
    private List<Diagnosis> diagnosisList = new ArrayList<>();
    private Integer id;
    private int selectedDoctorId;
    private int selectedPatientId;
    private int selectedDiagnosisId;
    private Stage stage;
    private boolean isUpdate = false;

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void initialize() {
        readInfo();
        doctorBox.setOnAction(event -> {
            String selectedDoctor = doctorBox.getValue();
            if (selectedDoctor != null) {
                for(Doctors choice: doctors) {
                    String sentence = choice.getName() + " " + choice.getLastName();
                    if(sentence.equals(selectedDoctor)) {
                        selectedDoctorId = choice.getId();
                    }
                }
            }
        });
        patientBox.setOnAction(event -> {
            String selectedPatient = patientBox.getValue();
            if (selectedPatient != null) {
                for(Patients choice: patients) {
                    String sentence = choice.getName() + " " + choice.getLastName();
                    if(sentence.equals(selectedPatient)) {
                        selectedPatientId = choice.getId();
                    }
                }
            }
        });
        diagnosisBox.setOnAction(event -> {
            String selectedDiagnosis = diagnosisBox.getValue();
            if (selectedDiagnosis != null) {
                for(Diagnosis choice: diagnosisList) {
                    if(choice.getDiagnosis().equals(selectedDiagnosis)) {
                        selectedDiagnosisId = choice.getId();
                    }
                }
            }
        });

        addBtn.setOnAction(event -> handleInsert());
    }

    private void readInfo() {
        try {
            String sql = "SELECT * FROM doctors";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            doctorBox.getItems().clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("last_name");
                int specialization = resultSet.getInt("id_specialization");
                Doctors doctor = new Doctors(id, name, surname, specialization);
                doctors.add(doctor);
                doctorBox.getItems().add(name+" " +surname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            String sql = "SELECT * FROM patients";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            patientBox.getItems().clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("pname");
                String surname = resultSet.getString("plast_name");
                int age = resultSet.getInt("age");
                Patients patient = new Patients(id, name, surname, age);
                patients.add(patient);
                patientBox.getItems().add(name+" " +surname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            String sql = "SELECT * FROM diagnosis";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            diagnosisBox.getItems().clear();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String diagnosisName = resultSet.getString("diagnosis");
                Diagnosis diagnosisObj = new Diagnosis();
                diagnosisObj.setDiagnosis(diagnosisName);
                diagnosisObj.setId(id);
                diagnosisList.add(diagnosisObj);
                diagnosisBox.getItems().add(diagnosisName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    private void handleInsert() {
        String data = dataField.getText();
        if(data.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(data);
            } else {
                update(data);
            }
        }
    }

    private void insert(String newData) {
        String insertQuery = "INSERT INTO diagnosis_card (id_doctor, id_diagnosis, id_card, data) VALUES (?, ?, ?, ?)";
        int idCard = getCardIdByPatientId();
        if(idCard!=-1) {
            try{
                PreparedStatement statement = Main.CONNECTION.prepareStatement(insertQuery);
                statement.setInt(1, selectedDoctorId);
                statement.setInt(2, selectedDiagnosisId);
                statement.setInt(3, idCard);
                statement.setString(4, newData);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Mistake");
        }
        close();
    }
    private int getCardIdByPatientId() {
        String query = "SELECT id FROM card WHERE id_patient = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(query);
            statement.setInt(1, selectedPatientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return -1;
    }
    void close() {
        stage.close();
    }

    private void update(String newData) {
        String updateQuery = "UPDATE diagnosis_card SET id_doctor=?, id_diagnosis=?, id_card=?, data=? WHERE id=?";
        int idCard = getCardIdByPatientId();
        if(idCard!=-1) {
            try{
                PreparedStatement statement = Main.CONNECTION.prepareStatement(updateQuery);
                statement.setInt(1, selectedDoctorId);
                statement.setInt(2, selectedDiagnosisId);
                statement.setInt(3, idCard);
                statement.setString(4, newData);
                statement.setInt(5, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Mistake");
        }
        close();
    }

}
