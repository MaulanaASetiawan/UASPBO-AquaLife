module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.gui to javafx.fxml;
    exports com.example.gui;
}