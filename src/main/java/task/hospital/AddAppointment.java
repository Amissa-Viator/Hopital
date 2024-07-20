package task.hospital;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import task.database.entity.AgeGroup;
import task.database.entity.Doctors;
import task.database.entity.Patients;

public class AddAppointment {

    @FXML
    private Button addBtn;

    @FXML
    private ChoiceBox<String> doctorBox;
    @FXML
    private TextField dataFld;
    @FXML
    private ChoiceBox<String> patientBox;
    private int selectedDoctorId;
    private int selectedPatientId;
    private Integer idChosen;
    private Integer idDoctor;
    private Integer idPatient;
    private Stage stage;
    private boolean isUpdate = false;
    private List<Doctors> doctorsList = new ArrayList<>();
    private List<Patients> patientsList = new ArrayList<>();
    public void setUpdate(boolean update) {
        isUpdate = update;
    }
    public void setId(int id, int idDoctor, int idPatient) {
        this.idChosen = id;
        this.idDoctor = idDoctor;
        this.idPatient = idPatient;
    }
    public void setData(String data) {
        dataFld.setText(data);
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
                for(Doctors choice: doctorsList) {
                    String info = choice.getName() + " " + choice.getLastName();
                    if(info.equals(selectedDoctor)) {
                        selectedDoctorId = choice.getId();
                    }
                }
            }
        });
        patientBox.setOnAction(event -> {
            String selectedPatient = patientBox.getValue();
            if (selectedPatient != null) {
                for(Patients choice: patientsList) {
                    String info = choice.getName() + " " + choice.getLastName();
                    if(info.equals(selectedPatient)) {
                        selectedPatientId = choice.getId();
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
                String lastName= resultSet.getString("last_name");
                int specialization = resultSet.getInt("id_specialization");
                Doctors doctor = new Doctors(id,name,lastName,specialization);
                doctorsList.add(doctor);
                doctorBox.getItems().add(name+" " +lastName);
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
                String pname = resultSet.getString("pname");
                String plastName= resultSet.getString("plast_name");
                int age = resultSet.getInt("age");
                Patients patient = new Patients(id,pname,plastName, age);
                patientsList.add(patient);
                patientBox.getItems().add(pname+" " +plastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void handleInsert() {
        String data = dataFld.getText();
        if(data.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if (!isUpdate) {
                insert(data);
            } else {
                update(data);
            }
        }
    }
    private void insert(String data) {
        String insertInfo = "INSERT INTO doctor_patient (id_doctor, id_patient, data) VALUES (?,?,?)";
        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertInfo, Statement.RETURN_GENERATED_KEYS);
            insertState.setInt(1, selectedDoctorId);
            insertState.setInt(2, selectedPatientId);
            insertState.setString(3, data);
            insertState.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    void close() {
        stage.close();
    }

    private void update(String data) {
        String updatePatientQuery = "UPDATE doctor_patient SET id_doctor=?, id_patient=?, data=? WHERE id=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updatePatientQuery);
            statement.setInt(1, selectedDoctorId);
            statement.setInt(2, selectedPatientId);
            statement.setString(3, data);
            statement.setInt(4, idChosen);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

}
