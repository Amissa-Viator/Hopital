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
import task.database.entity.CardDetails;
import task.database.entity.DoctorDetails;
import task.database.entity.ExamDetails;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LabexamController {

    @FXML
    private TableColumn<ExamDetails, Void> actionsCol;

    @FXML
    private FontAwesomeIcon addIcon;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button companiesBtn;

    @FXML
    private TableColumn<ExamDetails, String> dataCol;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<ExamDetails> examTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<ExamDetails, Integer> resultCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<ExamDetails, Integer> normCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;

    @FXML
    private TableColumn<ExamDetails, String> surnameCol;

    @FXML
    private TableColumn<ExamDetails, String> typeCol;

    @FXML
    private TableColumn<ExamDetails, String> unitCol;
    @FXML
    private TableColumn<ExamDetails, String> doctorCol;
    @FXML
    private TableColumn<ExamDetails, String> nameCol;
    private Stage stage;
    private ObservableList<ExamDetails> examList;

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
    private void handleDoctorsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "doctors.fxml");
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
        List<ExamDetails> exams = new ArrayList<>();
        try {
            String sql = "SELECT " +
                    "p.id AS patient_id, " +
                    "p.pname AS patient_name, " +
                    "p.plast_name AS patient_last_name, " +
                    "le.id AS exam_id, " +
                    "le.data AS examination_data, " +
                    "le.type AS examination_type, " +
                    "le.results AS examination_results, " +
                    "le.unit_of_measurement AS examination_unit_of_measurement, " +
                    "le.normal_values AS examination_normal_values, " +
                    "d.id AS doctor_id, " +
                    "d.last_name AS doctor_last_name " +
                    "FROM card c " +
                    "JOIN patients p ON c.id_patient = p.id " +
                    "JOIN laboratory_examinations le ON c.id_labexam = le.id " +
                    "JOIN doctors d ON le.id_doctor = d.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                ExamDetails details = new ExamDetails();
                details.setIdPatient(resultSet.getInt("patient_id"));
                details.setIdExam(resultSet.getInt("exam_id"));
                details.setIdDoctor(resultSet.getInt("doctor_id"));
                details.setPatientName(resultSet.getString("patient_name"));
                details.setPatientLastName(resultSet.getString("patient_last_name"));
                details.setExaminationData(resultSet.getString("examination_data"));
                details.setExaminationType(resultSet.getString("examination_type"));
                details.setExaminationResults(resultSet.getInt("examination_results"));
                details.setExaminationUnitOfMeasurement(resultSet.getString("examination_unit_of_measurement"));
                details.setExaminationNormalValues(resultSet.getInt("examination_normal_values"));
                details.setDoctorLastName(resultSet.getString("doctor_last_name"));
                exams.add(details);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        examList = FXCollections.observableArrayList(exams);
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
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientName()));
        surnameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientLastName()));
        dataCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExaminationData()));
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExaminationType()));
        resultCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getExaminationResults()).asObject());
        normCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getExaminationNormalValues()).asObject());
        unitCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExaminationUnitOfMeasurement()));
        doctorCol.setCellValueFactory(cellData -> {
            String doctorLastName = cellData.getValue().getDoctorLastName();
            return new SimpleStringProperty(doctorLastName != null ? doctorLastName : "N/A");
        });

        examTable.setItems(examList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<ExamDetails, Void>, TableCell<ExamDetails, Void>> cellFactory = new Callback<TableColumn<ExamDetails, Void>, TableCell<ExamDetails, Void>>() {
            @Override
            public TableCell<ExamDetails, Void> call(final TableColumn<ExamDetails, Void> param) {
                final TableCell<ExamDetails, Void> cell = new TableCell<ExamDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            ExamDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            ExamDetails info = getTableView().getItems().get(getIndex());
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
        actionsCol.setCellFactory(cellFactory);
    }

    @FXML
    void handleEditInfo(ExamDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addExam.fxml"));
            Parent parent = loader.load();
            AddExam controller = loader.getController();
            controller.setUpdate(true);
            controller.setId(info.getIdExam());
            controller.setTextField(info.getExaminationType(), info.getExaminationData(), info.getExaminationResults(),info.getExaminationNormalValues(), info.getExaminationUnitOfMeasurement());
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
    void handleDeleteElement(ExamDetails info) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Can't delete it because this statement has connections");
        alert.showAndWait();
        refreshView();
    }
    @FXML
    void addNew(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addExam.fxml"));
            Parent parent = loader.load();
            AddExam controller = loader.getController();
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
            examList.clear();
            getAllInfo();
            examTable.setItems(examList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }
    @FXML
    void searchView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findPatient.fxml"));
            Parent parent = loader.load();
            FindExam controller = loader.getController();
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
