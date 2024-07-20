package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.Specializations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddDoctor {

    @FXML
    private Button addBtn;

    @FXML
    private TextField address;

    @FXML
    private TextField hospitalName;

    @FXML
    private TextField name;

    @FXML
    private ChoiceBox<String> specialBox;
    private int selectedSpecializationId;
    @FXML
    private TextField surname;
    private Integer id;
    private Stage stage;
    private List<Specializations> specializationsList = new ArrayList<>();
    private boolean isUpdate = false;

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @FXML
    void initialize() {
        readSpecializations();

        // Обробник події вибору елементу з ChoiceBox
        specialBox.setOnAction(event -> {
            String selectedSpecialization = specialBox.getValue();
            if (selectedSpecialization != null) {
                for(Specializations choice: specializationsList) {
                    if(choice.getNameOfSpecialization().equals(selectedSpecialization)) {
                        selectedSpecializationId = choice.getId();
                    }
                }
            }
        });
        addBtn.setOnAction(event -> handleInsert());
    }

    private void readSpecializations() {
        try {
            String sql = "SELECT * FROM specializations";
            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            specialBox.getItems().clear();

            // Додавання спеціалізацій до ChoiceBox
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String specializationName = resultSet.getString("name_of_specialization");
                Specializations specialization = new Specializations(id, specializationName);
                specializationsList.add(specialization);
                specialBox.getItems().add(specializationName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private void handleInsert() {
        String newName = name.getText();
        String newSurname = surname.getText();
        String newHospitalName = hospitalName.getText();
        String newAddress = address.getText();
        if(newName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            if(!isUpdate) {
                insert(newName, newSurname, newHospitalName, newAddress);
            } else {
                update(newName, newSurname, newHospitalName, newAddress);
            }
        }
    }

    private void insert(String name, String surname, String hospital, String address) {
        String insertDoctor = "INSERT INTO doctors (name, last_name, id_specialization) VALUES (?,?,?)";
        String insertHospitalQuery = "INSERT INTO hospitals (h_name, address) VALUES (?, ?)";
        String insertHospitalDoctorQuery = "INSERT INTO hospital_doctor (id_doctor, id_hospital) VALUES (?, ?)";

        try {
            // Вставка запису в таблицю doctors і отримання згенерованого id
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertDoctor, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, name);
            insertState.setString(2, surname);
            insertState.setInt(3, selectedSpecializationId);
            insertState.executeUpdate();

            var generatedKeys = insertState.getGeneratedKeys();
            int idDoctor;
            if (generatedKeys.next()) {
                idDoctor = generatedKeys.getInt("id"); // Отримання згенерованого id лікаря
            } else {
                throw new SQLException("Creating doctor failed, no ID obtained.");
            }

            PreparedStatement statement = Main.CONNECTION.prepareStatement(insertHospitalQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, hospital);
            statement.setString(2, address);
            statement.executeUpdate();

            var generatedHospitalKeys = statement.getGeneratedKeys();
            int idHospital;
            if (generatedHospitalKeys.next()) {
                idHospital = generatedHospitalKeys.getInt("id"); // Отримання згенерованого id лікарні
            } else {
                throw new SQLException("Creating hospital failed, no ID obtained.");
            }

            PreparedStatement hospitalDoctorStatement = Main.CONNECTION.prepareStatement(insertHospitalDoctorQuery);
            hospitalDoctorStatement.setInt(1, idDoctor);
            hospitalDoctorStatement.setInt(2, idHospital);
            hospitalDoctorStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        close();
    }

    void close() {
        stage.close();
    }

    private void update(String name, String surname, String hospital, String address) {
        String updateDoctorQuery = "UPDATE doctors SET name=?, last_name=?, id_specialization=? WHERE id=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(updateDoctorQuery);
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setInt(3, selectedSpecializationId);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        int hospitalId = getHospitalIdForDoctor(id);
        if (hospitalId != -1) {
            String updateHospitalQuery = "UPDATE hospitals SET h_name=?, address=? WHERE id=?";
            try {
                PreparedStatement statement = Main.CONNECTION.prepareStatement(updateHospitalQuery);
                statement.setString(1, hospital);
                statement.setString(2, address);
                statement.setInt(3, hospitalId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        close();
    }

    private int getHospitalIdForDoctor(int doctorId) {
        String query = "SELECT id_hospital FROM hospital_doctor WHERE id_doctor=?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(query);
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_hospital");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void setTextField(int sendId, String doctorName, String doctorSurname, String hospital, String hospitalAddress) {
        id = sendId;
        name.setText(doctorName);
        surname.setText(doctorSurname);
        hospitalName.setText(hospital);
        address.setText(hospitalAddress);
    }
}
