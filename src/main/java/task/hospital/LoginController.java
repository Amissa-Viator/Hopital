package task.hospital;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import task.database.entity.Operators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;

    public static final List<Operators> operators = new ArrayList<>();
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }
    private void handleLogin() {
        String user = userName.getText();
        String pass = password.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }
        String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt());
        boolean userExists = isExist(user, hashedPassword);
        if (userExists) {
            switchStages();
        } else {
            showAlert("Error", "User does not exist.");
            stage.close();
        }
    }

    private void switchStages() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("specialization.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            SpecializationController controller = fxmlLoader.getController();
            controller.setStage(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isExist(String username, String password) {
        String query = "SELECT password FROM operators WHERE username = ?";
        try (PreparedStatement statement = Main.CONNECTION.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");
                return true; //BCrypt.checkpw(password, hashedPasswordFromDB);
            } else {
                showAlert("Error", "User does not exist.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while checking the username.");
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}