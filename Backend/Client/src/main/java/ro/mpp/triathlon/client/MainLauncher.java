package ro.mpp.triathlon.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp.triathlon.TriathlonServicesRpcProxy;
import ro.mpp.triathlon.controller.LoginController;
import ro.mpp.triathlon.services.ITriathlonServices;
//import ro.mpp.triathlon.client.controller.LoginController; // Importul corect

import java.io.InputStream;
import java.util.Properties;

public class MainLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try (InputStream is = MainLauncher.class.getResourceAsStream("/client.properties")) {
            if (is != null) clientProps.load(is);
        }

        String serverIP = clientProps.getProperty("triathlon.server.host", "localhost");
        int serverPort = Integer.parseInt(clientProps.getProperty("triathlon.server.port", "55555"));
        ITriathlonServices serverProxy = new TriathlonServicesRpcProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginCtrl = loader.getController();
        loginCtrl.setService(serverProxy);

        primaryStage.setTitle("Triathlon - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}