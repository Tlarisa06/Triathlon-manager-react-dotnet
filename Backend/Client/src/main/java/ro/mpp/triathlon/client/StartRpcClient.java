package ro.mpp.triathlon.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp.triathlon.TriathlonServicesRpcProxy;
import ro.mpp.triathlon.controller.LoginController;
import ro.mpp.triathlon.services.ITriathlonServices;


import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private ITriathlonServices server;
    private static int defaultPort = 55555;
    private static String defaultHost = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting Triathlon Client...");

        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClient.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties loaded from resources.");
        } catch (Exception e) {
            System.err.println("Cannot find client.properties " + e);
            System.out.println("Using default settings: localhost:55555");
        }

        String serverIP = clientProps.getProperty("triathlon.server.host", defaultHost);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(clientProps.getProperty("triathlon.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
        }

        server = new TriathlonServicesRpcProxy(serverIP, serverPort);

        initView(primaryStage);
        primaryStage.setTitle("Triathlon - Autentificare Arbitru");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginCtrl = loader.getController();
        loginCtrl.setService(server);

        primaryStage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
