package application.controller;

import application.Photos;
import application.model.DataStore;
import application.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        if(username.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "User name cannot be empty!");
            alert.showAndWait();
            return;
        }
        User user = DataStore.loadUser(username);
        if(user == null) {
            if(username.equals("admin")){
                user = new User(username);
            } else if(username.equals("stock")){
                user = DataStore.loadStockUser();
                if (user == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Stock user not found!");
                    alert.showAndWait();
                    return;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "User not found!");
                alert.showAndWait();
                return;
            }
            DataStore.saveUser(user);
        }
        
        try {
            Stage stage = Photos.getPrimaryStage();
            if(username.equals("admin")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/admin.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setTitle("Photo Album - Admin Mode");
                stage.setScene(scene);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/main.fxml"));
                Scene scene = new Scene(loader.load());
                MainController controller = loader.getController();
                controller.setUser(user);
                stage.setTitle("Photo Album - " + username);
                stage.setScene(scene);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

