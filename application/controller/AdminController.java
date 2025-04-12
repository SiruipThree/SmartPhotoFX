package application.controller;

import application.model.DataStore;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * 管理员界面的控制器，用于管理所有用户
 */
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
            new Alert(Alert.AlertType.ERROR, "用户名不能为空！").showAndWait();
            return;
        }
        if(DataStore.userExists(newUser)){
            new Alert(Alert.AlertType.ERROR, "用户已存在。").showAndWait();
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
            new Alert(Alert.AlertType.ERROR, "未选择用户。").showAndWait();
            return;
        }
        if(selected.equals("admin") || selected.equals("stock")){
            new Alert(Alert.AlertType.ERROR, "不能删除 admin 或 stock 用户。").showAndWait();
            return;
        }
        DataStore.deleteUser(selected);
        loadUsers();
    }
}
