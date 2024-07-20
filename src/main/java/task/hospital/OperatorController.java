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
import task.database.entity.Operators;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OperatorController {
    @FXML
    private FontAwesomeIcon addIcon;
    @FXML
    private Button operatorsBtn;
    @FXML
    private Button companiesBtn;
    @FXML
    private Button diagnosisBtn;
    @FXML
    private Button specializationBtn;
    @FXML
    private Button diagnosisCardBtn;
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
    @FXML
    private TableView<Operators> operatorTable;
    @FXML
    private TableColumn<Operators, String> username;
    @FXML
    private TableColumn<Operators, String> password;
    @FXML
    private TableColumn<Operators, Void> actCol;
    private ObservableList<Operators> operatorsList;
    private List<Operators> operators = new ArrayList<>();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void getAll() throws SQLException {
        List<Operators> operators = new ArrayList<>();
        String query = "SELECT * FROM operators";
        Statement stmt = Main.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            int id = rs.getInt("id");
            String user = rs.getString("username");
            String password = rs.getString("password");
            Operators operator = new Operators();
            operator.setPassword(password);
            operator.setUsername(user);
            operator.setId(id);
            operators.add(operator);
        }
        operatorsList = FXCollections.observableArrayList(operators);
    }

    void read() {
        try {
            getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void initialize() {
        read();
        username.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        password.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        operatorTable.setItems(operatorsList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Operators, Void>, TableCell<Operators, Void>> cellFactory = new Callback<TableColumn<Operators, Void>, TableCell<Operators, Void>>() {
            @Override
            public TableCell<Operators, Void> call(final TableColumn<Operators, Void> param) {
                final TableCell<Operators, Void> cell = new TableCell<Operators, Void>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    {
                        editButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        deleteButton.setStyle("-fx-background-color: #28A9EA; -fx-text-fill: #ffffff");
                        editButton.setOnAction(event -> {
                            Operators operator = getTableView().getItems().get(getIndex());
                            handleEditSpecialization(operator);
                        });
                        deleteButton.setOnAction(event -> {
                            Operators operator = getTableView().getItems().get(getIndex());
                            handleDeleteSpecialization(operator);
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
    void handleEditSpecialization(Operators operator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addOperator.fxml"));
            Parent parent = loader.load();
            AddOperator controller = loader.getController();
            controller.setUpdate(true);
            controller.setTextField(operator.getId(), operator.getUsername(), operator.getPassword());
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
    void handleDeleteSpecialization(Operators operator) {
        String sql = "DELETE FROM operators WHERE id = ?";
        try {
            PreparedStatement statement = Main.CONNECTION.prepareStatement(sql);
            statement.setInt(1, operator.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        refreshView();
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
    private void handleSpecBtnAction(ActionEvent event) throws Exception {
        changeScene(event, "specialization.fxml");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addOperator.fxml"));
            Parent parent = loader.load();
            AddOperator controller = loader.getController();

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
            operatorsList.clear();
            getAll();
            operatorTable.setItems(operatorsList);
        } catch(SQLException ex) {

        }
    }

}
