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

/**
 * 登录控制器，处理用户登录操作
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        if(username.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "用户名不能为空！");
            alert.showAndWait();
            return;
        }
        // 尝试从数据存储加载用户
        User user = DataStore.loadUser(username);
        if(user == null) {
            // 对于 admin 和 stock 用户单独处理
            if(username.equals("admin")){
                user = new User(username);
            } else if(username.equals("stock")){
                user = DataStore.loadStockUser();
                if(user == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "未找到 stock 用户数据！");
                    alert.showAndWait();
                    return;
                }
            } else {
                // 新建普通用户
                user = new User(username);
            }
            DataStore.saveUser(user);
        }
        
        // 根据用户类型切换不同界面
        try {
            Stage stage = Photos.getPrimaryStage();
            if(username.equals("admin")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setTitle("Photo Album - Admin Mode");
                stage.setScene(scene);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
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

