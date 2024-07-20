package task.hospital;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import task.database.entity.Specializations;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.text.Collator;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class SpecializationController {
    @FXML
    private FontAwesomeIcon addIcon;
    @FXML
    private FontAwesomeIcon sortIcon;
    @FXML
    private FontAwesomeIcon searchIcon;
    @FXML
    private Button companiesBtn;
    @FXML
    private Button diagnosisBtn;
    @FXML
    private Button diagnosisCardBtn;
    @FXML
    private Button doctorpatientBtn;
    @FXML
    private Button doctorsBtn;
    @FXML
    private Button examBtn;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button medicineBtn;
    @FXML
    private Button patientsBtn;
    @FXML
    private FontAwesomeIcon refreshIcon;
    @FXML
    private TableView<Specializations> specializationTable;
    @FXML
    private TableColumn<Specializations, Integer> idColumn;
    @FXML
    private TableColumn<Specializations, String> nameColumn;
    @FXML
    private TableColumn<Specializations, Void> actCol;
    private ObservableList<Specializations> specializationList;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void getAllSpecializations() throws SQLException {
        String query = "SELECT * FROM specializations";
        Statement stmt = Main.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Specializations specialization = new Specializations(rs.getInt("id"), rs.getString("name_of_specialization"));
            Main.specializations.add(specialization);
        }
        specializationList = FXCollections.observableArrayList(Main.specializations);
    }

    void read() {
        try {
            getAllSpecializations();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void initialize() {
        read();
       idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
       nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameOfSpecialization()));

       specializationTable.setItems(specializationList);
       addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Specializations, Void>, TableCell<Specializations, Void>> cellFactory = new Callback<TableColumn<Specializations, Void>, TableCell<Specializations, Void>>() {
            @Override
            public TableCell<Specializations, Void> call(final TableColumn<Specializations, Void> param) {
                final TableCell<Specializations, Void> cell = new TableCell<Specializations, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            Specializations specialization = getTableView().getItems().get(getIndex());
                            handleEditSpecialization(specialization);
                        });
                        deleteButton.setOnAction(event -> {
                            Specializations specialization = getTableView().getItems().get(getIndex());
                            handleDeleteSpecialization(specialization);
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
    void handleEditSpecialization(Specializations specialization) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addSpec.fxml"));
            Parent parent = loader.load();
            AddSpecialization controller = loader.getController();
            controller.setUpdate(true);
            controller.setTextField(specialization.getId(), specialization.getNameOfSpecialization());
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
    void handleDeleteSpecialization(Specializations specialization) {
        boolean doctorsExist = checkDoctorsWithSpecialization(specialization.getId());
        if(doctorsExist) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Can't delete it because this statement has connections");
            alert.showAndWait();
        } else {
            String sql = "DELETE FROM specializations WHERE id = ?";
            try {
                PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
                statement.setInt(1, specialization.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        refreshView();
    }
    private boolean checkDoctorsWithSpecialization(int specializationId) {
        try {
            String sql = "SELECT COUNT(*) AS count FROM doctors WHERE id_specialization = ?";
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, specializationId);
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
    private void handleCompaniesBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "companies.fxml");
    }

    @FXML
    private void handleDiagnosisBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "diagnosis.fxml");
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

    @FXML
    void addNew(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addSpec.fxml"));
            Parent parent = loader.load();
            AddSpecialization controller = loader.getController();

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
            specializationList.clear();
            Main.specializations.clear();
            getAllSpecializations();
            specializationTable.setItems(specializationList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

    @FXML
    void sortList(MouseEvent event) {
        Collator collator = Collator.getInstance(new Locale("uk", "UA"));
        specializationList.sort((s1, s2) -> collator.compare(s1.getNameOfSpecialization(), s2.getNameOfSpecialization()));
        specializationTable.setItems(specializationList);
    }
    @FXML
    void searchView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findDoctor.fxml"));
            Parent parent = loader.load();
            FindDoctors controller = loader.getController();
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

