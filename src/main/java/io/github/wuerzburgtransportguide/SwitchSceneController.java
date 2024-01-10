package io.github.wuerzburgtransportguide;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SwitchSceneController {

    @FXML Button startButton;

    public void switchSceneToRoute() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("pages/route/route.fxml"));
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(new Scene(root, 800, 500));
    }
}
