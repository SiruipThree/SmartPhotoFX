package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of the Photo Album application.
 * Launches the JavaFX application, starting with the login screen.
 * 
 * This class also stores a static reference to the primary stage,
 * allowing other controllers (e.g. LoginController) to access it as needed.
 * 
 * This class must include the {@code main} method to start the app.
 * 
 */
public class Photos extends Application {

    private static Stage primaryStage;

    /**
     * Starts the JavaFX application.
     * Loads the login screen as the first scene.
     *
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Photo Album - Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Provides global access to the application's primary stage.
     * 
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Main method to launch the application.
     *
     */
    public static void main(String[] args) {
        launch(args);
    }
}
