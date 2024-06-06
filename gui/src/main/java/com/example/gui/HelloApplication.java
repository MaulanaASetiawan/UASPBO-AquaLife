package com.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(HelloApplication.class.getResource("log.fxml")));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(HelloApplication.class.getResource("/stylesheet/log.css").toExternalForm());
            Image img = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/assets/icon.png")));

            stage.getIcons().add(img);
            stage.setResizable(false);
            stage.setTitle("AquaLife");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}