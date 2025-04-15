package application.controller;

import java.io.IOException;


import application.model.DataStore;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.scene.control.ListView;


/**
 * Controller for the Admin view.
 *
 * Allows the admin user to manage application users:
 * 
 *   View all users
 *   Create new users
 *   Delete existing users (excluding "admin" and "stock")
 *   Log out of the admin interface
 */

public class AdminController {

    //UI Components
    /** ListView component that displays the list of usernames. */
    @FXML private ListView<String> userListView;
    /** TextField input for creating a new user. */
    @FXML private TextField newUserField;

   /**
     * Called automatically after the FXML components are loaded.
     * Loads and displays the user list.
     */
    @FXML
    private void initialize(){
        loadUsers();
    }


    /**
     * Loads the list of users from the data store and updates the ListView.
     */
    private void loadUsers(){
        ObservableList<String> users = FXCollections.observableArrayList(DataStore.getUserList());
        userListView.setItems(users);
    }
    
    /**
     * Handles the creation of a new user.
     * Validates input, ensures uniqueness, and updates the UI.
     *
     */
    @FXML
    public void handleCreateUser(ActionEvent event) {
        String newUser = newUserField.getText().trim();

        //Validate input
        if(newUser.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Username cannot be empty!").showAndWait();
            return;
        }
        if(DataStore.userExists(newUser)){
            new Alert(Alert.AlertType.ERROR, "The user already exists.").showAndWait();
            return;
        }

        //create and save user
        User user = new User(newUser);
        DataStore.saveUser(user);

        //refresh user list
        loadUsers();
    }
    
    /**
     * Handles the deletion of a selected user.
     * Prevents deletion of reserved users like "admin" and "stock".
     *
     */
    @FXML
    public void handleDeleteUser(ActionEvent event) {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if(selected == null) {
            new Alert(Alert.AlertType.ERROR, "No users selected.").showAndWait();
            return;
        }
        if(selected.equals("admin") || selected.equals("stock")){
            new Alert(Alert.AlertType.ERROR, "Cannot delete admin or stock user.").showAndWait();
            return;
        }
        DataStore.deleteUser(selected);
        loadUsers();
    }

   /**
     * Handles logout from the admin panel and returns to the login screen.
     *
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) userListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo Album - Login");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
