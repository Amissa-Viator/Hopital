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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import task.database.entity.AppointmentDetails;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorPatientController {

    @FXML
    private TableColumn<AppointmentDetails, Void> actCol;

    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private Button companiesBtn;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<AppointmentDetails> appointmentTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<AppointmentDetails, String> lastNameCol;

    @FXML
    private TableColumn<AppointmentDetails, String> lastNamePCol;
    @FXML
    private TableColumn<AppointmentDetails, String> dataCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<AppointmentDetails, String> nameCol;

    @FXML
    private TableColumn<AppointmentDetails, String> pNameCol;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;
    private ObservableList<AppointmentDetails> appointmentList;
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
        List<AppointmentDetails> appointments = new ArrayList<>();
        try {
            String sql = "SELECT doctor_patient.id, doctor_patient.id_doctor, doctor_patient.data, doctor_patient.id_patient, patients.pname, patients.plast_name, doctors.name, doctors.last_name " +
                    "FROM doctor_patient " +
                    "LEFT JOIN doctors ON doctor_patient.id_doctor = doctors.id " +
                    "LEFT JOIN patients ON doctor_patient.id_patient = patients.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                Integer idDoctor = resultSet.getInt("id_doctor");
                Integer idPatient = resultSet.getInt("id_patient");
                String dataValue = resultSet.getString("data");
                String docName = resultSet.getString("name");
                String docLastName = resultSet.getString("last_name");
                String pName = resultSet.getString("pname");
                String pLastName = resultSet.getString("plast_name");

                AppointmentDetails connectInfo = new AppointmentDetails(id, docName, docLastName, pName, pLastName);
                connectInfo.setIdDoctor(idDoctor);
                connectInfo.setIdPatient(idPatient);
                connectInfo.setData(dataValue);
                appointments.add(connectInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        appointmentList = FXCollections.observableArrayList(appointments);
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
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorFirstName()));
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorLastName()));
        pNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientFirstName()));
        lastNamePCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientLastName()));
        dataCol.setCellValueFactory(cellData -> {
            String data = cellData.getValue().getData();
            if (data == null) {
                return new SimpleStringProperty("N/A");
            } else {
                return new SimpleStringProperty(data);
            }
        });
        appointmentTable.setItems(appointmentList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<AppointmentDetails, Void>, TableCell<AppointmentDetails, Void>> cellFactory = new Callback<TableColumn<AppointmentDetails, Void>, TableCell<AppointmentDetails, Void>>() {
            @Override
            public TableCell<AppointmentDetails, Void> call(final TableColumn<AppointmentDetails, Void> param) {
                final TableCell<AppointmentDetails, Void> cell = new TableCell<AppointmentDetails, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            AppointmentDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            AppointmentDetails info = getTableView().getItems().get(getIndex());
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
    void handleEditInfo(AppointmentDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addAppointment.fxml"));
            Parent parent = loader.load();
            AddAppointment controller = loader.getController();
            controller.setUpdate(true);
            controller.setId(info.getId(), info.getIdDoctor(), info.getIdPatient());
            if(info.getData() != null) {
                controller.setData(info.getData());
            }
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
    void handleDeleteElement(AppointmentDetails info) {
        int idToDelete = info.getId();
        String sql = "DELETE FROM doctor_patient WHERE id = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, idToDelete);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshView();
    }

    @FXML
    void addNew(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addAppointment.fxml"));
            Parent parent = loader.load();
            AddAppointment controller = loader.getController();
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
            appointmentList.clear();
            getAllInfo();
            appointmentTable.setItems(appointmentList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

    @FXML
    void searchView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("findRecord.fxml"));
            Parent parent = loader.load();
            FindRecord controller = loader.getController();
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
