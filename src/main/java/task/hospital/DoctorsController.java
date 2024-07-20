package task.hospital;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import task.database.entity.Diagnosis;
import task.database.entity.DoctorDetails;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorsController {

    @FXML
    private TableColumn<DoctorDetails, Void> actCol;
    @FXML
    private Button operatorsBtn;
    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private TableColumn<DoctorDetails, String> addressCol;

    @FXML
    private Button companiesBtn;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<DoctorDetails> doctorsTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<DoctorDetails, String> hospitalCol;

    @FXML
    private TableColumn<DoctorDetails, String> lastNameCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<DoctorDetails, String> nameCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;

    @FXML
    private TableColumn<DoctorDetails, String> specializationCol;

    private ObservableList<DoctorDetails> doctorsList;
    private Stage stage;

    @FXML
    private void handleCompaniesBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "companies.fxml");
    }

    @FXML
    private void handleSpecializationBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "specialization.fxml");
    }

    @FXML
    private void handlePatientsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "patients.fxml");
    }

    @FXML
    private void handleDoctorpatientBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "appointment.fxml");
    }

    @FXML
    private void handleCardsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "card.fxml");
    }

    @FXML
    private void handleExamBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "examination.fxml");
    }

    @FXML
    private void handleMedicineBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "medicine.fxml");
    }

    @FXML
    private void handleDiagnosisBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "diagnosis.fxml");
    }
    @FXML
    private void handleOperatorsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "operators.fxml");
    }
    // Метод для зміни сцени
    private void changeScene(ActionEvent event, String fxmlFile) throws Exception {
        Parent newSceneRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
        Scene newScene = new Scene(newSceneRoot);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(newScene);
        window.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void getAllInfo() throws SQLException {
        List<DoctorDetails> doctors = new ArrayList<>();
        try {
            String sql = "SELECT doctors.id, doctors.name, doctors.last_name, specializations.name_of_specialization, hospitals.h_name, hospitals.address " +
                    "FROM doctors " +
                    "LEFT JOIN specializations ON doctors.id_specialization = specializations.id " +
                    "LEFT JOIN hospital_doctor ON doctors.id = hospital_doctor.id_doctor " +
                    "LEFT JOIN hospitals ON hospital_doctor.id_hospital = hospitals.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("last_name");
                String specialization = resultSet.getString("name_of_specialization");
                String hospital = resultSet.getString("h_name");
                String address = resultSet.getString("address");

                DoctorDetails doctorInfo = new DoctorDetails(id, name, lastName, specialization, hospital, address);
                doctors.add(doctorInfo);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        doctorsList = FXCollections.observableArrayList(doctors);
    }

    void read() {
        try {
           getAllInfo();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
         read();
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        specializationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialization()));
        hospitalCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHospitalName()));
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHospitalAddress()));

        doctorsTable.setItems(doctorsList);
        addButtonToTable();

    }

    private void addButtonToTable() {
        Callback<TableColumn<DoctorDetails, Void>, TableCell<DoctorDetails, Void>> cellFactory = new Callback<TableColumn<DoctorDetails, Void>, TableCell<DoctorDetails, Void>>() {
            @Override
            public TableCell<DoctorDetails, Void> call(final TableColumn<DoctorDetails, Void> param) {
                final TableCell<DoctorDetails, Void> cell = new TableCell<DoctorDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            DoctorDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            DoctorDetails info = getTableView().getItems().get(getIndex());
                            handleDeleteElement(info);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(editButton, deleteButton);
                            buttons.setSpacing(10);
                            setGraphic(buttons);
                        }
                    }
                };
                return cell;
            }
        };

        actCol.setCellFactory(cellFactory);
    }

    @FXML
    void handleEditInfo(DoctorDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addDoctor.fxml"));
            Parent parent = loader.load();
            AddDoctor controller = loader.getController();
            controller.setUpdate(true);
            controller.setTextField(info.getId(), info.getName(), info.getLastName(),info.getHospitalName(), info.getHospitalAddress());
            Stage newStage = new Stage();
            controller.setStage(newStage);
            newStage.setScene(new Scene(parent));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(stage);
            newStage.initStyle(StageStyle.UTILITY);
            newStage.setOnHidden(e -> refreshView());
            newStage.showAndWait();
        } catch (IOException ex) {
            System.out.println("Error");
        }
    }

    @FXML
    void handleDeleteElement(DoctorDetails doctor) {
        boolean isExist = checkConnections(doctor.getId());
        if(isExist) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't delete it because this statement has connections");
            alert.showAndWait();
        } else {
            String sql = "DELETE FROM doctors WHERE id = ?";
            try {
                PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
                statement.setInt(1, doctor.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        refreshView();
    }
    private boolean checkConnections(int doctorId) {
        return existsInDiagnosisCard(doctorId) || existsInDoctorPatient(doctorId) || existsInLaboratoryExamination(doctorId);
    }

    private boolean existsInDiagnosisCard(int doctorId) {
        String sql = "SELECT COUNT(*) AS count FROM diagnosis_card WHERE id_doctor = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsInDoctorPatient(int doctorId) {
        String sql = "SELECT COUNT(*) AS count FROM doctor_patient WHERE id_doctor = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsInLaboratoryExamination(int doctorId) {
        String sql = "SELECT COUNT(*) AS count FROM laboratory_examination WHERE id_doctor = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
   void addNew(MouseEvent event) {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("addDoctor.fxml"));
           Parent parent = loader.load();
           AddDoctor controller = loader.getController();

           Stage newStage = new Stage();
           controller.setStage(newStage);

           newStage.setScene(new Scene(parent));
           newStage.initModality(Modality.APPLICATION_MODAL);
           newStage.initOwner(stage);
           newStage.initStyle(StageStyle.UTILITY);
           newStage.setOnHidden(e -> refreshView());
           newStage.showAndWait();
       } catch (IOException ex) {
           System.out.println("Error");
       }
   }

    @FXML
    void refreshView() {
        try{
            doctorsList.clear();
            getAllInfo();
            doctorsTable.setItems(doctorsList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

    @FXML
    void searchView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findHospitals.fxml"));
            Parent parent = loader.load();
            FindHospitals controller = loader.getController();
            Stage newStage = new Stage();
            controller.setStage(newStage);
            newStage.setScene(new Scene(parent));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(stage);
            newStage.initStyle(StageStyle.UTILITY);
            newStage.showAndWait();
        } catch (IOException ex) {
            System.out.println("Error");
        }
    }
}
