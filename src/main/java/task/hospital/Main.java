package task.hospital;

import task.database.util.ConnectionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.io.IOException;

public class Main extends Application {
    public static final Connection CONNECTION = ConnectionManager.get();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setScene(scene);
        stage.show();
        LoginController login = fxmlLoader.getController();
        login.setStage(stage);
    }


    public static void main(String[] args) {
        launch();
    }
}