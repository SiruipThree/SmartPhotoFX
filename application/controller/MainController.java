package application.controller;

import application.model.Album;
import application.model.User;
import application.model.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * 普通用户主界面的控制器
 */
public class MainController {
    @FXML
    private ListView<Album> albumListView;
    
    @FXML
    private Label albumDetailsLabel;
    
    private User user;
    
    public void setUser(User user){
        this.user = user;
        refreshAlbumList();
    }
    
    @FXML
    private void initialize() {
        albumListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null) {
                albumDetailsLabel.setText(newV.getDetails());
            }
        });
    }
    
    private void refreshAlbumList(){
        ObservableList<Album> albums = FXCollections.observableArrayList(user.getAlbums());
        albumListView.setItems(albums);
    }
    
    @FXML
    public void handleCreateAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("创建相册");
        dialog.setHeaderText("请输入相册名称：");
        dialog.showAndWait().ifPresent(name -> {
            if(name.trim().isEmpty()){
                new Alert(Alert.AlertType.ERROR, "相册名不能为空！").showAndWait();
                return;
            }
            if(user.hasAlbum(name)){
                new Alert(Alert.AlertType.ERROR, "该相册已存在。").showAndWait();
                return;
            }
            Album album = new Album(name);
            album.setOwner(user);
            user.getAlbums().add(album);
            DataStore.saveUser(user);
            refreshAlbumList();
        });
    }
    
    @FXML
    public void handleDeleteAlbum(ActionEvent event) {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "未选择相册！").showAndWait();
            return;
        }
        user.getAlbums().remove(selected);
        DataStore.saveUser(user);
        refreshAlbumList();
    }
    
    @FXML
    public void handleOpenAlbum(ActionEvent event) {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "未选择相册！").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/album.fxml"));
            Scene scene = new Scene(loader.load());
            AlbumController controller = loader.getController();
            controller.setAlbum(selected);
            Stage stage = new Stage();
            stage.setTitle("相册: " + selected.getName());
            stage.setScene(scene);
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleLogout(ActionEvent event) {
        DataStore.saveUser(user);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo Album - Login");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
