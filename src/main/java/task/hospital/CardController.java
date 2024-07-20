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

public class CardController {

    @FXML
    private TableColumn<CardDetails, Void> actionsCol;
    @FXML
    private Button operatorsBtn;

    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private Button companiesBtn;

    @FXML
    private TableColumn<CardDetails, String> dataCol;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableColumn<CardDetails, String> diagnosisCol;

    @FXML
    private TableView<CardDetails> cardTable;

    @FXML
    private TableColumn<CardDetails, String> docNameCol;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<CardDetails, String> nameCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;

    @FXML
    private TableColumn<CardDetails, String> surnameCol;

    private Stage stage;
    private ObservableList<CardDetails> cardList;

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
        List<CardDetails> cards = new ArrayList<>();
        try {
            String sql = "SELECT " +
                    "dc.id AS dc_id, " +
                    "p.id AS patient_id, " +
                    "p.pname AS patient_name, " +
                    "p.plast_name AS patient_last_name, " +
                    "d.id AS doctor_id, " +
                    "d.last_name AS doctor_last_name, " +
                    "diag.id AS diagnosis_id, " +
                    "diag.diagnosis AS diagnosis, " +
                    "dc.data AS diagnosis_date " +
                    "FROM diagnosis_card dc " +
                    "JOIN patients p ON dc.id_card = p.id " +
                    "JOIN doctors d ON dc.id_doctor = d.id " +
                    "JOIN diagnosis diag ON dc.id_diagnosis = diag.id";


            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                CardDetails details = new CardDetails();
                details.setId(resultSet.getInt("dc_id"));
                details.setIdPatient(resultSet.getInt("patient_id"));
                details.setPatientName(resultSet.getString("patient_name"));
                details.setPatientLastName(resultSet.getString("patient_last_name"));
                details.setIdDoctor(resultSet.getInt("doctor_id"));
                details.setDoctorSurname(resultSet.getString("doctor_last_name"));
                details.setIdDiagnosis(resultSet.getInt("diagnosis_id"));
                details.setDiagnosis(resultSet.getString("diagnosis"));
                details.setData(resultSet.getString("diagnosis_date"));
                cards.add(details);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cardList = FXCollections.observableArrayList(cards);
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
        diagnosisCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiagnosis()));
        dataCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData()));
        docNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorSurname()));

        cardTable.setItems(cardList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<CardDetails, Void>, TableCell<CardDetails, Void>> cellFactory = new Callback<TableColumn<CardDetails, Void>, TableCell<CardDetails, Void>>() {
            @Override
            public TableCell<CardDetails, Void> call(final TableColumn<CardDetails, Void> param) {
                final TableCell<CardDetails, Void> cell = new TableCell<CardDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            CardDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            CardDetails info = getTableView().getItems().get(getIndex());
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
    void handleEditInfo(CardDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCard.fxml"));
            Parent parent = loader.load();
            AddCard controller = loader.getController();
            controller.setUpdate(true);
            controller.setId(info.getId());
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
    void handleDeleteElement(CardDetails info) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCard.fxml"));
            Parent parent = loader.load();
            AddCard controller = loader.getController();
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
            cardList.clear();
            getAllInfo();
            cardTable.setItems(cardList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

}
