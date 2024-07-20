package task.hospital;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleIntegerProperty;
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
import task.database.entity.DoctorDetails;
import task.database.entity.PatientDetails;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientController {

    @FXML
    private TableColumn<PatientDetails, Void> actCol;

    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private TableColumn<PatientDetails, Integer> ageCol;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button companiesBtn;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<PatientDetails> patientTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<PatientDetails, String> groupCol;

    @FXML
    private TableColumn<PatientDetails, String> lastNameCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<PatientDetails, String> nameCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;

    private ObservableList<PatientDetails> patientsList;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCompaniesBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "companies.fxml");
    }

    @FXML
    private void handleSpecializationBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "specialization.fxml");
    }

    @FXML
    private void handleDiagnosisCardBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "card.fxml");
    }

    @FXML
    private void handleDoctorpatientBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "appointment.fxml");
    }

    @FXML
    private void handleDoctorsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "doctors.fxml");
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

    public void getAllInfo() throws SQLException {
        List<PatientDetails> patients = new ArrayList<>();
        try {
            String sql = "SELECT patients.id, patients.pname, patients.plast_name, patients.age, age_group.age_category " +
                    "FROM patients " +
                    "LEFT JOIN card ON patients.id = card.id_patient " +
                    "LEFT JOIN age_group ON card.id_age_category = age_group.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("pname");
                String lastName = resultSet.getString("plast_name");
                Integer age = resultSet.getInt("age");
                String age_group = resultSet.getString("age_category");

                PatientDetails patientInfo = new PatientDetails(id, name, lastName, age, age_group);
                patients.add(patientInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        patientsList = FXCollections.observableArrayList(patients);
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
        ageCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        groupCol.setCellValueFactory(cellData -> {String group = cellData.getValue().getAgeGroup();
        return new SimpleStringProperty(group != null ? group : "N/A");});
        patientTable.setItems(patientsList);
        addButtonToTable();

    }

    private void addButtonToTable() {
        Callback<TableColumn<PatientDetails, Void>, TableCell<PatientDetails, Void>> cellFactory = new Callback<TableColumn<PatientDetails, Void>, TableCell<PatientDetails, Void>>() {
            @Override
            public TableCell<PatientDetails, Void> call(final TableColumn<PatientDetails, Void> param) {
                final TableCell<PatientDetails, Void> cell = new TableCell<PatientDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            PatientDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            PatientDetails info = getTableView().getItems().get(getIndex());
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
    void handleDeleteElement(PatientDetails patient) {
        boolean isExist = checkConnections(patient.getId());
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
                statement.setInt(1, patient.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        refreshView();
    }
    private boolean checkConnections(int patientId) {
        return existsInDoctorPatient(patientId) || existsInCard(patientId);
    }

    private boolean existsInDoctorPatient(int patientId) {
        String sql = "SELECT COUNT(*) AS count FROM doctor_patient WHERE id_patient = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, patientId);
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

    private boolean existsInCard(int patientId) {
        String sql = "SELECT COUNT(*) AS count FROM card WHERE id_patient = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, patientId);
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
    void handleEditInfo(PatientDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addPatient.fxml"));
            Parent parent = loader.load();
            AddPatient controller = loader.getController();
            controller.setUpdate(true);
            controller.setTextField(info.getId(), info.getName(), info.getLastName(),info.getAge());
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
    void addNew(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addPatient.fxml"));
            Parent parent = loader.load();
            AddPatient controller = loader.getController();

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
            patientsList.clear();
            getAllInfo();
            patientTable.setItems(patientsList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

}
