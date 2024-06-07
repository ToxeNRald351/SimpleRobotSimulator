/**
 * @author Anton Havlovskyi
 * Ovladač pro scénu plnění parametrů pole.
 */
package ija.ija2023.project;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class createFieldController {

    @FXML
    TextField colsNumberField;

    @FXML
    TextField rowsNumberField;

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }

    // switchToNewField
    @FXML
    private void switchToNewField(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("test.fxml"));
            Parent root = loader.load();
            fieldController controller = loader.getController();

            int width;
            try {
                width = Integer.parseInt(colsNumberField.getText());
            } catch (java.lang.NumberFormatException e) {
                width = 10;
            }
            int height;
            try {
                height = Integer.parseInt(rowsNumberField.getText());
            } catch (java.lang.NumberFormatException e) {
                height = 10;
            }
            width = width > 20 ? 20 : width;
            height = height > 20 ? 20 : height;

            controller.createChessBoard(width, height); // Call method in Scene2Controller

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}