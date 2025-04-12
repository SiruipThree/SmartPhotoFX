package application.controller;

import application.model.Album;
import application.model.Photo;
import application.model.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

public class AlbumController {
    @FXML
    private ListView<Photo> photoListView;
    
    @FXML
    private Label photoDetailsLabel;
    
    private Album album;
    
    public void setAlbum(Album album){
        this.album = album;
        refreshPhotoList();
    }
    
    @FXML
    private void initialize(){
        photoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null) {
                photoDetailsLabel.setText(newV.getDetails());
            }
        });
    }
    
    private void refreshPhotoList(){
        ObservableList<Photo> photos = FXCollections.observableArrayList(album.getPhotos());
        photoListView.setItems(photos);
    }
    
    @FXML
    public void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choose a photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image_file", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if(file != null) {
            Photo photo = new Photo(file.getAbsolutePath());
            TextInputDialog captionDialog = new TextInputDialog();
            captionDialog.setTitle("adding photo");
            captionDialog.setHeaderText("please input the caption");
            captionDialog.showAndWait().ifPresent(caption -> photo.setCaption(caption));
            album.getPhotos().add(photo);
            DataStore.saveUser(album.getOwner());
            refreshPhotoList();
        }
    }
    
    @FXML
    public void handleDeletePhoto(ActionEvent event) {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "haven chosen a photo!").showAndWait();
            return;
        }
        album.getPhotos().remove(selected);
        DataStore.saveUser(album.getOwner());
        refreshPhotoList();
    }
    
    @FXML
    public void handleBack(ActionEvent event) {
        photoListView.getScene().getWindow().hide();
    }
}

