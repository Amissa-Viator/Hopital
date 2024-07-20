package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddDiagnosis {
    @FXML
    private Button addBtn;

    @FXML
    private TextField diagnosisField;

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
        newName = diagnosisField.getText();
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
            query = "INSERT INTO diagnosis (diagnosis) VALUES (?)";
        } else {
            query = "UPDATE diagnosis SET diagnosis=? WHERE id=?";
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
        diagnosisField.setText(nameOfS);
    }

}
