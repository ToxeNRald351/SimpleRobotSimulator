/**
 * @author Anton Havlovskyi
 * Ovladač hlavního menu.
 */
package ija.ija2023.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainMenuController {

    @FXML
    private void switchToCreateFieldScene() throws IOException {
        App.setRoot("createField");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void loadSaveFile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("test.fxml"));
            Parent root = loader.load();
            fieldController controller = loader.getController();
            controller.loadFromFile(event); // Call method in Scene2Controller

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
