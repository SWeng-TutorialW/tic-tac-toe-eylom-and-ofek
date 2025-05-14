package il.cshaifasweng.OCSFMediatorExample.client;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ConnectController {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Button connectButton;

    @FXML
    private void initialize() {
        connectButton.setOnAction(event -> connectToServer());
    }

    @FXML
    private void connectToServer() {
        String ip = ipField.getText();
        String portStr = portField.getText();

        try {
            int port = Integer.parseInt(portStr);
            // Attempt to connect (replace with your real connection logic)
            System.out.println("Connecting to " + ip + " on port " + port);
            try{
                SimpleClient.initialize(ip, port);
                SimpleClient.getClient().openConnection();
                this.switchToPrimary();}
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            //showAlert("Success", "Attempted to connect to server at " + ip + ":" + port);
        } catch (NumberFormatException e) {
            showAlert("Error", "Port must be a number.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

}