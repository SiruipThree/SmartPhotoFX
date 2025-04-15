package application.controller;

import application.model.Album;
import application.model.Photo;
import application.model.User;
import application.model.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ListView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AlbumController {

    //UI Elements
    @FXML
    private ListView<Photo> photoListView;
    @FXML
    private Label photoDetailsLabel;
    
    private Album album;
    private User user;
    
    //Sets user and album and loads photo list
    public void setUserAndAlbum(User user, Album album){
        this.user = user;
        this.album = album;
        refreshPhotoList();
    }
    
    //Sets up ListView rendering and selection listener
    @FXML
    private void initialize(){
        //Update label when a new photo is selected
        photoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(newV != null) {
                photoDetailsLabel.setText(newV.getDetails());
            }
        });

        //Customize ListView cells to show photo thumbnails and captions
        photoListView.setCellFactory(listView -> new ListCell<Photo>() {
            private final ImageView imageView = new ImageView();
            private final Label titleLabel = new Label();
            private final VBox container = new VBox(5);  // 垂直间距5

            {
                imageView.setFitWidth(120);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setClip(new Rectangle(120, 90));

                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(120);
                titleLabel.setStyle("-fx-alignment: center; -fx-font-size: 12px;");

                container.setAlignment(Pos.CENTER);  // 图片和文字居中
                container.getChildren().addAll(imageView, titleLabel);
            }

            //Update content of each cell
            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        Image image = new Image(new FileInputStream(photo.getFilePath()), 120, 90, true, true);
                        imageView.setImage(image);
                    } catch (FileNotFoundException e) {
                        imageView.setImage(null);
                    }
                    titleLabel.setText(photo.getCaption());
                    setGraphic(container);
                    setText(null);
                }
            }
        });
    }
    
    //Refresh the ListView with photos from the album
    private void refreshPhotoList(){
        List<Photo> photoList = album.getPhotoIds().stream().map(id -> user.getPhoto(id)).toList();
        ObservableList<Photo> photos = FXCollections.observableArrayList(photoList);
        photoListView.setItems(photos);
    }
    
    //Handles adding a new photo to the album
    @FXML
    public void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image_file", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if(file != null) {
            // check if the file is already in the album
            if (album.getPhotoIds().stream().anyMatch(id -> user.getPhoto(id).getFilePath().equals(file.getAbsolutePath()))) {
                new Alert(Alert.AlertType.ERROR, "This photo is already in the album.").showAndWait();
                return;
            }
            Photo photo = user.importPhoto(file.getAbsolutePath());

            TextInputDialog captionDialog = new TextInputDialog();
            captionDialog.setTitle("Adding photo");
            captionDialog.setHeaderText("Please input the caption");
            // set default text to file name
            String fileName = file.getName();
            String defaultCaption = fileName.substring(0, fileName.lastIndexOf('.'));
            captionDialog.setContentText("Caption:");
            captionDialog.getEditor().setText(defaultCaption);
            Optional<String> caption = captionDialog.showAndWait();
            caption.ifPresentOrElse(cap -> photo.setCaption(cap.isBlank() ? "Unnamed" : cap), () -> photo.setCaption("Unnamed"));

            album.getPhotoIds().add(photo.getId());
            DataStore.saveUser(user);
            refreshPhotoList();
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "No file selected!").showAndWait();
        }
    }
    
    //Handles deleting a photo from the album
    @FXML
    public void handleDeletePhoto(ActionEvent event) {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "No photos selected!").showAndWait();
            return;
        }
        List<Long> photoIds = album.getPhotoIds();
        photoIds.remove(selected.getId());
        DataStore.saveUser(user);
        refreshPhotoList();
    }
    
    //Handles the "Back" button to close the current window
    @FXML
    public void handleBack(ActionEvent event) {
        photoListView.getScene().getWindow().hide();
    }

    //Handles renaming a photo's caption
    @FXML
    public void handleRename(ActionEvent event) {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "No photos selected!").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.getCaption());
        dialog.setTitle("Rename Photo");
        dialog.setHeaderText("Please enter the new caption:");
        dialog.getEditor().setText(selected.getCaption());
        dialog.setContentText("New Caption:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newCaption -> {
            if (newCaption.isBlank()) {
                return;
            }
            selected.setCaption(newCaption);
            DataStore.saveUser(user);
            refreshPhotoList();
        });
    }

    //Opens the selected photo in a dialog view with caption and navigation
    @FXML
    public void handleOpenPhoto(ActionEvent event) {
        Photo selected = photoListView.getSelectionModel().getSelectedItem();
        int photoIndex = photoListView.getSelectionModel().getSelectedIndex();
        if(selected == null){
            new Alert(Alert.AlertType.ERROR, "No photos selected!").showAndWait();
            return;
        }
        // Show a photo dialog
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/photodialog.fxml"));
            Scene scene = new Scene(loader.load());
            PhotoDialogController controller = loader.getController();
            controller.setupInfo(user, album, selected, photoListView.getSelectionModel().getSelectedIndex());
            Stage stage = new Stage();
            stage.setTitle(selected.getCaption() + " (" + (photoIndex + 1) + "/" + album.getPhotoIds().size() + ")");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(photoListView.getScene().getWindow());
            stage.showAndWait();
            //Refresh in case any changes were made in the dialog
            refreshPhotoList();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

