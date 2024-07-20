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
import task.database.entity.Specializations;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompanyConroller {

    @FXML
    private TableColumn<CompanyDetails, Void> actionCol;
    @FXML
    private Button operatorsBtn;
    @FXML
    private FontAwesomeIcon addIcon;

    @FXML
    private TableColumn<CompanyDetails, String> addressCol;

    @FXML
    private Button companiesBtn;

    @FXML
    private TableColumn<CompanyDetails, String> companyCol;

    @FXML
    private Button diagnosisBtn;

    @FXML
    private Button diagnosisCardBtn;

    @FXML
    private TableView<CompanyDetails> companiesTable;

    @FXML
    private Button doctorpatientBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button examBtn;

    @FXML
    private TableColumn<CompanyDetails, String> infoCol;

    @FXML
    private Button medicineBtn;

    @FXML
    private TableColumn<CompanyDetails, String> medicineColumn;

    @FXML
    private Button patientsBtn;

    @FXML
    private FontAwesomeIcon refreshIcon;
    private ObservableList<CompanyDetails> companiesList;
    private Stage stage;

    @FXML
    private void handleCardBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "card.fxml");
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void getAllInfo() throws SQLException {
        List<CompanyDetails> companies = new ArrayList<>();
        try {
            String sql = "SELECT m.id AS medicine_id, m.name AS medicine_name, " +
                    "pc.id AS company_id, pc.name AS company_name, pc.address AS company_address, pc.info AS company_info " +
                    "FROM company_medicine cm " +
                    "JOIN medicine m ON cm.id_medicine = m.id " +
                    "JOIN pharmaceutical_company pc ON cm.id_company = pc.id";

            Statement statement = Main.CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Integer medicineId = resultSet.getInt("medicine_id");
                String medicineName = resultSet.getString("medicine_name");
                Integer companyId = resultSet.getInt("company_id");
                String companyName = resultSet.getString("company_name");
                String companyAddress = resultSet.getString("company_address");
                String companyInfo = resultSet.getString("company_info");

                CompanyDetails companyDetails = new CompanyDetails(medicineId,companyId, medicineName, companyName, companyAddress, companyInfo);
                companies.add(companyDetails);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        companiesList = FXCollections.observableArrayList(companies);
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
        medicineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicine()));
        companyCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompanyName()));
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        infoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInfo()));

        companiesTable.setItems(companiesList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<CompanyDetails, Void>, TableCell<CompanyDetails, Void>> cellFactory = new Callback<TableColumn<CompanyDetails, Void>, TableCell<CompanyDetails, Void>>() {
            @Override
            public TableCell<CompanyDetails, Void> call(final TableColumn<CompanyDetails, Void> param) {
                final TableCell<CompanyDetails, Void> cell = new TableCell<CompanyDetails, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Del");

                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            CompanyDetails info = getTableView().getItems().get(getIndex());
                            handleEditInfo(info);
                        });
                        deleteButton.setOnAction(event -> {
                            CompanyDetails info = getTableView().getItems().get(getIndex());
                            handleDeleteCompanies(info);
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
        actionCol.setCellFactory(cellFactory);
    }

    @FXML
    void handleEditInfo(CompanyDetails info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCompany.fxml"));
            Parent parent = loader.load();
            AddCompany controller = loader.getController();
            controller.setUpdate(true);
            controller.setIdCompany(info.getIdCompany());
            controller.setTextField(info.getCompanyName(),info.getAddress(),info.getInfo());
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
    void handleDeleteCompanies(CompanyDetails info) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCompany.fxml"));
            Parent parent = loader.load();
            AddCompany controller = loader.getController();
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
            companiesList.clear();
            getAllInfo();
            companiesTable.setItems(companiesList);
        } catch(SQLException ex) {
            System.out.println(ex);
        }
    }

}
