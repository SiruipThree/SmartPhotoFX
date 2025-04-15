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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;


/**
 * Controller for the main user screen after login. 
 * Handles displaying albums, creating, deleting, renaming, and opening albums.
 * Also supports logging out and searching photos.
 */
public class MainController {
    @FXML
    private ListView<Album> albumListView;
    
    @FXML
    private Label albumDetailsLabel;
    
    private User user;

    /**
     * Sets the current user and populates the album list.
     */
    public void setUser(User user){
        this.user = user;
        refreshAlbumList();
    }
    
    /**
     * Initializes the controller. Binds album selection to details label.
     */
    @FXML
    private void initialize() {
        //When an album is selected, display its details
        albumListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null) {
                albumDetailsLabel.setText(newV.getDetails(user));
            }
        });
    }
    
    /**
     * Refreshes the album list from the user's data.
     */
    private void refreshAlbumList(){
        ObservableList<Album> albums = FXCollections.observableArrayList(user.getAlbums().values());
        albumListView.setItems(albums);
    }
    
    /**
     * Handles creating a new album via dialog input.
     */
    @FXML
    public void handleCreateAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Please enter the album name:");
        dialog.showAndWait().ifPresent(name -> {
            if(name.trim().isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Filename can't be empty.").showAndWait();
                return;
            }

            //Ensure album name is unique
            boolean hasAlbum = user.getAlbums().values().stream().anyMatch(album -> album.getName().equals(name));
            if(hasAlbum){
                new Alert(Alert.AlertType.ERROR, "Album with this name already exists.").showAndWait();
                return;
            }
            //Create and store the new album
            Album album = new Album(user.allocAlbumId(), name);
            user.getAlbums().put(album.getId(), album);
            DataStore.saveUser(user);
            refreshAlbumList();
        });
    }
    
    /**
     * Handles deleting the selected album.
     */
    @FXML
    public void handleDeleteAlbum(ActionEvent event) {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "No albums selected.").showAndWait();
            return;
        }
        user.getAlbums().remove(selected.getId());
        DataStore.saveUser(user);
        refreshAlbumList();
    }
    
    /**
     * Opens the selected album in a new window.
     */
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
            controller.setUserAndAlbum(user, selected);
            Stage stage = new Stage();
            stage.setTitle("Album: " + selected.getName());
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(albumListView.getScene().getWindow());
            stage.showAndWait();
            refreshAlbumList();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Logs out the current user and returns to the login screen.
     */
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

    /**
     * Opens the search dialog window to allow the user to search photos.
     */
    @FXML
    public void handleSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/searchdialog.fxml"));
            Scene scene = new Scene(loader.load());
            SearchDialogController controller = loader.getController();
            controller.setUser(user);
            Stage stage = new Stage();
            stage.setTitle("Search Photos");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(albumListView.getScene().getWindow());
            stage.showAndWait();
            refreshAlbumList();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles renaming the selected album via input dialog.
     */
    @FXML
    public void handleRenameAlbum(ActionEvent event) {
        Album selected = albumListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "No albums selected!").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Please enter the new album name:");
        dialog.getEditor().setText(selected.getName());
        dialog.setContentText("New Album Name:");
        dialog.showAndWait().ifPresent(newName -> {
            if (newName.isBlank()) {
                return;
            }
            if (newName.equals(selected.getName())) {
                return;
            }
            boolean hasAlbum = user.getAlbums().values().stream().anyMatch(album -> album.getName().equals(newName));
            if(hasAlbum){
                new Alert(Alert.AlertType.ERROR, "Album with this name already exists.").showAndWait();
                return;
            }
            selected.setName(newName);
            refreshAlbumList();
        });
    }
}
