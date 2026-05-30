package ro.mpp.triathlon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.services.ITriathlonServices;

import java.io.IOException;

public class LoginController {
    private ITriathlonServices service;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void setService(ITriathlonServices service) {
        this.service = service;
    }

    @FXML
    public void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Câmpuri goale", "Vă rugăm să introduceți username și parola!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
            Parent root = loader.load();
            MainController mainCtrl = loader.getController();

            Referee referee = service.login(user, pass, mainCtrl);

            if (referee != null) {
                mainCtrl.setService(service, referee);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Triathlon - Arbitru: " + referee.getName());

                stage.setOnCloseRequest(event -> {
                    mainCtrl.handleLogout();
                });

                stage.show();

                usernameField.getScene().getWindow().hide();
            }

        } catch (IOException e) {
            showAlert("Eroare FXML", "Nu s-a putut încărca interfața principală (main-view.fxml). Verificați path-ul!");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Eroare Autentificare", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}