module task.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires fontawesomefx;
    requires java.sql;
    requires jbcrypt;

    opens task.hospital to javafx.fxml;
    exports task.hospital;
}