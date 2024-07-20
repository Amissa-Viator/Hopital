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
import task.database.entity.Diagnosis;
import task.database.entity.Specializations;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiagnosisController {

    @FXML
    private FontAwesomeIcon addIcon;
    @FXML
    private FontAwesomeIcon sortIcon;
    @FXML
    private FontAwesomeIcon searchIcon;
    @FXML
    private Button companiesBtn;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<Diagnosis> diagnosisTable;
    @FXML
    private TableColumn<Diagnosis, Integer> idColumn;
    @FXML
    private TableColumn<Diagnosis, String> diagnosisCol;
    @FXML
    private TableColumn<Diagnosis, Void> actCol;
    @FXML
    private Button doctorpatientBtn;
    @FXML
    private Button doctorsBtn;
    @FXML
    private Button examBtn;
    @FXML
    private Button medicineBtn;
    @FXML
    private Button patientsBtn;
    @FXML
    private FontAwesomeIcon refreshIcon;
    private ObservableList<Diagnosis> diagnosisList;
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
    private void handlePatientsBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "patients.fxml");
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

    public void getAllDiagnosis() throws SQLException {
        String query = "SELECT * FROM diagnosis";
        List<Diagnosis> diagnosisArray = new ArrayList<>();
        Statement stmt = Main.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setId(rs.getInt("id"));
            diagnosis.setDiagnosis(rs.getString("diagnosis"));
            diagnosisArray.add(diagnosis);
        }
        diagnosisList = FXCollections.observableArrayList(diagnosisArray);
    }

    void collect() {
        try {
            getAllDiagnosis();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void initialize() {
        collect();
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        diagnosisCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiagnosis()));

        diagnosisTable.setItems(diagnosisList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Diagnosis, Void>, TableCell<Diagnosis, Void>> cellFactory = new Callback<TableColumn<Diagnosis, Void>, TableCell<Diagnosis, Void>>() {
            @Override
            public TableCell<Diagnosis, Void> call(final TableColumn<Diagnosis, Void> param) {
                final TableCell<Diagnosis, Void> cell = new TableCell<Diagnosis, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            Diagnosis diagnosis = getTableView().getItems().get(getIndex());
                            handleEditDiagnosis(diagnosis);
                        });
                        deleteButton.setOnAction(event -> {
                            Diagnosis diagnosis = getTableView().getItems().get(getIndex());
                            handleDeleteElement(diagnosis);
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
    void handleEditDiagnosis(Diagnosis diagnosis) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addDiagnosis.fxml"));
            Parent parent = loader.load();
            AddDiagnosis controller = loader.getController();
            controller.setUpdate(true);
            controller.setTextField(diagnosis.getId(), diagnosis.getDiagnosis());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addDiagnosis.fxml"));
            Parent parent = loader.load();
            AddDiagnosis controller = loader.getController();

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
    void handleDeleteElement(Diagnosis diagnosis) {
        boolean isExist = checkConnections(diagnosis.getId());
        if(isExist) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't delete it because this statement has connections");
            alert.showAndWait();
        } else {
            String sql = "DELETE FROM diagnosis WHERE id = ?";
            try {
                PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
                statement.setInt(1, diagnosis.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        refreshView();
    }
    private boolean checkConnections(int diagnosisId) {
        try {
            String sql = "SELECT COUNT(*) AS count FROM diagnosis_card dc JOIN diagnosis_medicine dm ON dc.id_diagnosis = dm.id_diagnosis WHERE dc.id_diagnosis = ?";
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, diagnosisId);
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
    void refreshView() {
        try{
            diagnosisList.clear();
            getAllDiagnosis();
            diagnosisTable.setItems(diagnosisList);
        } catch(SQLException ex) {

        }
    }

    @FXML
    void sortList(MouseEvent event) {
        Collator collator = Collator.getInstance(new Locale("uk", "UA"));
        diagnosisList.sort((s1, s2) -> collator.compare(s1.getDiagnosis(), s2.getDiagnosis()));
        diagnosisTable.setItems(diagnosisList);
    }
    @FXML
    void searchView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findInfo.fxml"));
            Parent parent = loader.load();
            FindMedicalInfo controller = loader.getController();
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
