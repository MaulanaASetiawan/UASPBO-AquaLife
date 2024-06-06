package com.example.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class logController implements Initializable {

    @FXML
    private Button btnLog;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private  TextField txtUsername;

    @FXML
    private Label txtReg;

    public void log() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        try (Connection conn = dbconn.connect()) {
            String query = "SELECT * FROM akun WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String credsRole = resultSet.getString(4);
                int userId = resultSet.getInt(1);
                showAlert("Login Success, Welcome, " + user + "!",Alert.AlertType.INFORMATION);
                if (credsRole.equals("admin")) {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/admin.css").toExternalForm());
                    Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));

                    Stage stage = (Stage) btnLog.getScene().getWindow();
                    stage.getIcons().add(img);
                    stage.setResizable(false);
                    stage.setTitle("AquaLife");
                    stage.setScene(scene);
                    stage.show();
                } else {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/admin.css").toExternalForm());
                    Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));

                    userController id = fxmlLoader.getController();
                    id.setUserId(userId);

                    Stage stage = (Stage) btnLog.getScene().getWindow();
                    stage.getIcons().add(img);
                    stage.setResizable(false);
                    stage.setTitle("AquaLife");
                    stage.setScene(scene);
                    stage.show();
                }
            } else {
                showAlert("Login Error, Invalid username or password.", Alert.AlertType.ERROR);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Database Error, An error occurred while connecting to the database.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icon.png"))));

        alert.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLog.setOnAction(actionEvent -> {
            log();
        });

        txtReg.setOnMouseClicked(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("reg.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/log.css").toExternalForm());
                Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));

                Stage stage = (Stage) txtReg.getScene().getWindow();
                stage.getIcons().add(img);
                stage.setResizable(false);
                stage.setTitle("AquaLife Registration");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}