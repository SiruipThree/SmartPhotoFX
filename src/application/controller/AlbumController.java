package application.controller;

import application.model.Album;
import application.model.Photo;
import application.model.User;
import application.model.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class AlbumController {
    @FXML
    private ListView<Photo> photoListView;
    
    @FXML
    private Label photoDetailsLabel;
    
    private Album album;
    private User user;
    
    public void setUserAndAlbum(User user, Album album){
        this.user = user;
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
    
    private void refreshPhotoList(){
        List<Photo> photoList = album.getPhotoIds().stream().map(id -> user.getPhoto(id)).toList();
        ObservableList<Photo> photos = FXCollections.observableArrayList(photoList);
        photoListView.setItems(photos);
    }
    
    @FXML
    public void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image_file", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if(file != null) {
            Photo photo = new Photo(user.allocPhotoId(), file.getAbsolutePath());
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
            user.getPhotos().put(photo.getId(), photo);
            album.getPhotoIds().add(photo.getId());
            DataStore.saveUser(user);
            refreshPhotoList();
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "No file selected!").showAndWait();
        }
    }
    
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
    
    @FXML
    public void handleBack(ActionEvent event) {
        photoListView.getScene().getWindow().hide();
    }

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
}

