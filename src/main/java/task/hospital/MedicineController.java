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
import task.database.entity.CompanyDetails;
import task.database.entity.DoctorDetails;
import task.database.entity.MedicineDetails;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicineController {

    @FXML
    private TableColumn<MedicineDetails, Void> actCol;
    @FXML
    private Button operatorsBtn;
    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private Button companiesBtn;

    @FXML
    private TableColumn<MedicineDetails, String> compositionCol;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableColumn<MedicineDetails, String> diagnosisCol;

    @FXML
    private TableView<MedicineDetails> medicineTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private TableColumn<MedicineDetails, String> dosageCol;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<MedicineDetails, String> formCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<MedicineDetails, String> medicineCol;
    @FXML
    private TableColumn<MedicineDetails,String> categoryCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;
    private Stage stage;
    private ObservableList<MedicineDetails> medicineList;

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
    private void handleExamBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "examination.fxml");
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
    private void handleConnectBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "appointment.fxml");
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
        List<MedicineDetails> medicine = new ArrayList<>();
        try {
            String sql = "SELECT d.id AS diagnosisId, d.diagnosis, " +
                    "m.id AS medicineId, m.name, m.composition, m.release_form, m.dosage, m.id_category, " +
                    "c.name_of_category AS categoryName " +
                    "FROM diagnosis_medicine dm " +
                    "JOIN diagnosis d ON dm.id_diagnosis = d.id " +
                    "JOIN medicine m ON dm.id_medicine = m.id " +
                    "JOIN category c ON m.id_category = c.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                MedicineDetails details = new MedicineDetails();
                details.setDiagnosisId(resultSet.getInt("diagnosisId"));
                details.setDiagnosis(resultSet.getString("diagnosis"));
                details.setMedicineId(resultSet.getInt("medicineId"));
                details.setName(resultSet.getString("name"));
                details.setComposition(resultSet.getString("composition"));
                details.setReleaseForm(resultSet.getString("release_form"));
                details.setDosage(resultSet.getString("dosage"));
                details.setIdCategory(resultSet.getInt("id_category"));
                details.setCategoryName(resultSet.getString("categoryName"));

                medicine.add(details);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        medicineList = FXCollections.observableArrayList(medicine);
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
        diagnosisCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiagnosis()));
        medicineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        compositionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComposition()));
        dosageCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDosage()));
        formCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReleaseForm()));
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName()));

        medicineTable.setItems(medicineList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<MedicineDetails, Void>, TableCell<MedicineDetails, Void>> cellFactory = new Callback<TableColumn<MedicineDetails, Void>, TableCell<MedicineDetails, Void>>() {
            @Override
            public TableCell<MedicineDetails, Void> call(final TableColumn<MedicineDetails, Void> param) {
                final TableCell<MedicineDetails, Void> cell = new TableCell<MedicineDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            MedicineDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            MedicineDetails info = getTableView().getItems().get(getIndex());
                            handleDeleteMedicine(info);
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
    void handleDeleteMedicine(MedicineDetails info) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Can't delete it because this statement has connections");
        alert.showAndWait();
        refreshView();
    }

    @FXML
    void handleEditInfo(MedicineDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addMedicine.fxml"));
            Parent parent = loader.load();
            AddMedicine controller = loader.getController();
            controller.setUpdate(true);
            controller.setIdMedicine(info.getMedicineId());
            controller.setTextField(info.getName(), info.getComposition(), info.getReleaseForm(),info.getDosage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addMedicine.fxml"));
            Parent parent = loader.load();
            AddMedicine controller = loader.getController();
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
            medicineList.clear();
            getAllInfo();
            medicineTable.setItems(medicineList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

}
