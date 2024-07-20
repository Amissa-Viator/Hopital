package task.hospital;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import task.database.entity.Doctors;
import task.database.entity.Patients;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddExam {
    @FXML
    private Button addBtn;

    @FXML
    private TextField dataFld;

    @FXML
    private ChoiceBox<String> doctorsBox;

    @FXML
    private TextField normFld;

    @FXML
    private ChoiceBox<String> patientBox;

    @FXML
    private TextField resultFld;

    @FXML
    private TextField typeFld;
    private List<Doctors> doctors = new ArrayList<>();
    private List<Patients> patients = new ArrayList<>();
    @FXML
    private TextField unitsFld;
    private Integer id;
    private int selectedDoctorId;
    private int selectedPatientId;
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
        doctorsBox.setOnAction(event -> {
            String selectedDoctor = doctorsBox.getValue();
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
        addBtn.setOnAction(event -> handleInsert());
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
                doctorsBox.getItems().add(name+" " +surname);
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
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTextField(String type, String data, int results, int normal, String units) {
        typeFld.setText(type);
        dataFld.setText(data);
        resultFld.setText(String.valueOf(results));
        normFld.setText(String.valueOf(normal));
        unitsFld.setText(units);
    }

    private void handleInsert() {
        String data = dataFld.getText();
        int norm = Integer.parseInt(normFld.getText());
        String type = typeFld.getText();
        int result = Integer.parseInt(resultFld.getText());
        String units = unitsFld.getText();
        if(data.isEmpty() || norm==0 || type.isEmpty() || result==0 || units.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(data, norm, type, result, units);
            } else {
                update(data, norm, type, result, units);
            }
        }
    }

    private void insert(String newData, int norm, String type, int result, String unit) {
        String insertQuery = "INSERT INTO laboratory_examinations (data, type, results, normal_values, unit_of_measurement,id_doctor) VALUES (?, ?, ?, ?,?,?)";
        String insert = "INSERT INTO card (id_patient, id_labexam) VALUES (?,?)";
        try{
            PreparedStatement statement = Main.CONNECTION.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newData);
            statement.setString(2, type);
            statement.setInt(3, result);
            statement.setInt(4, norm);
            statement.setString(5, unit);
            statement.setInt(6, selectedDoctorId);
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            int idOfExam;
            if (generatedKeys.next()) {
                idOfExam = generatedKeys.getInt("id");
            } else {
                throw new SQLException("Creating failed, no ID obtained.");
            }
            PreparedStatement insertStatement = Main.CONNECTION.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, selectedPatientId);
            insertStatement.setInt(2, idOfExam);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }
    void close() {
        stage.close();
    }

    private void update(String newData, int norm, String type, int result, String unit) {
        String updateQuery = "UPDATE laboratory_examinations SET data=?, type=?, results=?, normal_values=?, unit_of_measurement=?,id_doctor=? WHERE id=?";
        String updateCard = "UPDATE card SET id_patient=? WHERE id=?";
        try{
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateQuery);
            statement.setString(1, newData);
            statement.setString(2, type);
            statement.setInt(3, result);
            statement.setInt(4, norm);
            statement.setString(5, unit);
            statement.setInt(6, selectedDoctorId);
            statement.setInt(7, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try{
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateCard);
            statement.setInt(1, selectedPatientId);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

}
