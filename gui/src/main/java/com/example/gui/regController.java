package com.example.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class regController implements Initializable {

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private Button btnReg;

    @FXML
    private Label txtLog;

    private static Connection conn = dbconn.connect();
    private static PreparedStatement pstmt;

    private void conn() {
        if (conn == null) {
            System.out.println("Koneksi Gagal");
            System.exit(1);
        }
    }

    private boolean isUsernameExists(String username) throws SQLException {
        String query = "SELECT * FROM akun WHERE username = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }


    private void add() throws SQLException{
        try {
            conn();
            String user = txtUsername.getText();
            String pass = txtPassword.getText();
            String role = "user";

            if(!txtPassword.getText().equals(txtConfPassword.getText())){
                showAlert("Password tidak sama", Alert.AlertType.ERROR);
                txtPassword.clear();
                txtConfPassword.clear();
                return;
            }

            if(user.isEmpty() || pass.isEmpty()){
                showAlert("Username dan Password tidak boleh kosong", Alert.AlertType.ERROR);
                return;
            }

            if(pass.length() < 5){
                showAlert("Username dan Password minimal 5 karakter", Alert.AlertType.ERROR);
                return;
            }

            if (isUsernameExists(user)) {
                showAlert("Username sudah terdaftar", Alert.AlertType.ERROR);
                txtUsername.clear();
                txtPassword.clear();
                txtConfPassword.clear();
                return;
            }

            pstmt = conn.prepareStatement("INSERT INTO akun (username, password, role) VALUES (?, ?, ?)");
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            pstmt.setString(3, role);
            pstmt.executeUpdate();


            showAlert("Registrasi Berhasil", Alert.AlertType.INFORMATION);
            txtUsername.clear();
            txtPassword.clear();
            backtoLog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backtoLog(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("log.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/log.css").toExternalForm());
            Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));

            Stage stage = (Stage) btnReg.getScene().getWindow();
            stage.getIcons().add(img);
            stage.setResizable(false);
            stage.setTitle("AquaLife");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
        btnReg.setOnAction(actionEvent -> {
            try {
                add();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        txtLog.setOnMouseClicked(mouseEvent -> {
            try {
                backtoLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
