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
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Please enter the album name:");
        dialog.showAndWait().ifPresent(name -> {
            if(name.trim().isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Filename can't be empty").showAndWait();
                return;
            }
            if(user.hasAlbum(name)){
                new Alert(Alert.AlertType.ERROR, "This Album has been existed").showAndWait();
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
            new Alert(Alert.AlertType.ERROR, "No albums selected.").showAndWait();
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
            new Alert(Alert.AlertType.ERROR, "No albums selected.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/album.fxml"));
            Scene scene = new Scene(loader.load());
            AlbumController controller = loader.getController();
            controller.setAlbum(selected);
            Stage stage = new Stage();
            stage.setTitle("Album: " + selected.getName());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photo Album - Login");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
