package task.hospital;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddOperator {
    @FXML
    private Button addBtn;

    @FXML
    private TextField nameFld;

    @FXML
    private TextField passwordFld;
    private Stage stage;
    private int id;
    private boolean isUpdate = false;
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
        String user = nameFld.getText();
        String pass = passwordFld.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        }
        else {
            if(!isUpdate) {
                insert(user, pass);
            } else {
               update(user,pass);
            }
        }
    }


    private void insert(String name, String newPassword) {
        String query = "INSERT INTO operators (username, password) VALUES (?,?)";
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, name);
            insertState.setString(2, hashedPassword);
            insertState.executeUpdate();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void update(String name, String newPassword) {
        String query = "UPDATE operators SET username=?, password=? WHERE id=?";
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, name);
            insertState.setString(2, hashedPassword);
            insertState.setInt(3, id);
            insertState.executeUpdate();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setTextField(int id, String user, String password) {
        this.id = id;
        nameFld.setText(user);
        passwordFld.setText(password);
    }

}
