package application.controller;

import application.model.DataStore;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {
    @FXML private ListView<String> userListView;
    @FXML private TextField newUserField;

    @FXML
    private void initialize(){
        loadUsers();
    }
    
    private void loadUsers(){
        ObservableList<String> users = FXCollections.observableArrayList(DataStore.getUserList());
        userListView.setItems(users);
    }
    
    @FXML
    public void handleCreateUser(ActionEvent event) {
        String newUser = newUserField.getText().trim();
        if(newUser.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Username cannot be empty!").showAndWait();
            return;
        }
        if(DataStore.userExists(newUser)){
            new Alert(Alert.AlertType.ERROR, "The user already exists.").showAndWait();
            return;
        }
        User user = new User(newUser);
        DataStore.saveUser(user);
        loadUsers();
    }
    
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
}
