package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import task.database.entity.Specializations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddSpecialization {
    @FXML
    private Button addBtn;

    @FXML
    private TextField name;
    private Stage stage;
    private boolean isUpdate = false;
    private String newName = null;
    private int id;
    private String query = null;

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @FXML
    void initialize() {
        addBtn.setOnAction(event -> handleInsert());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private void handleInsert() {
        newName = name.getText();
        if(newName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else {
            actions();
        }
    }

    private void actions() {
        if(!isUpdate) {
            query = "INSERT INTO specializations (name_of_specialization) VALUES (?)";
        } else {
            query = "UPDATE specializations SET name_of_specialization=? WHERE id=?";
        }
        insert();
    }

    private void insert() {
        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, newName);
            if (isUpdate) {
                insertState.setInt(2, id);
            }
            insertState.executeUpdate();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setTextField(int sendId, String nameOfS) {
        id = sendId;
        name.setText(nameOfS);
    }
}
