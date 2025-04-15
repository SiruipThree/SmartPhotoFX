package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.model.Album;
import application.model.Photo;
import application.model.Tag;
import application.model.User;

/**
 * Controller for the photo dialog screen that allows users to:
 * - View photo details
 * - Edit tags (add, remove, modify)
 * - Navigate through photos in the album
 * - Move or copy a photo between albums
 * 
 * This is a modal window launched from within an album.
 * 
 */
public class PhotoDialogController {

    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Label detailLabel;
    @FXML
    private ListView<String> tagListView;

    private User user;
    private Album album;
    private Photo photo;
    private int photoIndex;

    /**
     * Initializes the image view bindings to preserve ratio and size.
     */
    @FXML
    public void initialize() {
        imageView.fitWidthProperty().bind(stackPane.widthProperty());
        imageView.fitHeightProperty().bind(stackPane.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    /**
     * Sets up the photo information to display in the dialog.
     * 
     */
    public void setupInfo(User user, Album album, Photo photo, int photoIndex) {
        this.user = user;
        this.album = album;
        this.photo = photo;
        this.photoIndex = photoIndex;
        String imagePath = photo.getFilePath();
        Image image = new Image("file:" + imagePath);
        imageView.setImage(image);
        String details = "Caption: " + photo.getCaption() + "\n" +
                        "Date: " + photo.getPhotoDate() + "\n";
        detailLabel.setText(details);
        // update window caption
        Stage stage = (Stage) stackPane.getScene().getWindow();
        if (stage != null) {
            stage.setTitle(photo.getCaption() + " (" + (photoIndex + 1) + "/" + album.getPhotoIds().size() + ")");
        }
        refreshTagList();
    }

    /**
     * Refreshes the list of tags displayed for the photo.
     */
    private void refreshTagList() {
        tagListView.getItems().clear();
        photo.getTags().forEach((tagId, tagValues) -> tagListView.getItems().add(user.getTag(tagId).getName() + ": " + String.join(", ", tagValues)));
    }
    
        /**
     * Opens a dialog to add a new tag to the photo.
     */
    @FXML
    private void handleAddTag(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/addtagdialog.fxml"));
            Scene scene = new Scene(loader.load());
            AddTagDialogController controller = loader.getController();
            controller.setUserAndPhoto(user, photo);
            Stage stage = new Stage();
            stage.setTitle("Add tag: ");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(stackPane.getScene().getWindow());
            stage.showAndWait();
            refreshTagList();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
        /**
     * Deletes the selected tag from the photo.
     */
    @FXML
    private void handleDeleteTag(ActionEvent event){

        int selectedIndex = tagListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            //No tag selected, show an error message
            new Alert(Alert.AlertType.ERROR, "No tag selected.").showAndWait();
            return;
        }
        // Get the tag ID from the selected item and remove
        long tagId = (long) photo.getTags().keySet().toArray()[selectedIndex];
        photo.getTags().remove(tagId);
        // Refresh the tag list view
        refreshTagList();
    }
    
        /**
     * Opens a dialog to edit the value of the selected tag.
     */
    @FXML
    private void handleEditTag(ActionEvent event){
        // Check selection
        int selectedIndex = tagListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            // No tag selected, show an error message
            new Alert(Alert.AlertType.ERROR, "No tag selected.").showAndWait();
            return;
        }
        // Show a prompt to enter the new tag value
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Tag Value");
        dialog.setHeaderText("Enter new value for the tag:");
        dialog.setContentText("New value:");
        ArrayList<String> currentValues = new ArrayList<>(photo.getTags().values()).get(selectedIndex);
        long tagId = (long) photo.getTags().keySet().toArray()[selectedIndex];
        Tag tag = user.getTag(tagId);
        String currentValue = String.join(", ", currentValues);
        dialog.getEditor().setText(currentValue);
        // show modal dialog
        dialog.initOwner(stackPane.getScene().getWindow());
        dialog.showAndWait().ifPresent(newValue -> {
            if (newValue.trim().isEmpty()) {
                return;
            }
            // Split the values
            String[] newValues = newValue.split(",");
            newValues = Arrays.stream(newValues).map(value -> value.trim()).filter(value -> !value.isEmpty()).toArray(String[]::new);

            if (!tag.isMultiValue() && newValues.length > 1) {
                new Alert(Alert.AlertType.ERROR, "This tag does not support multiple values.").showAndWait();
                return;
            }
            
            photo.getTags().put(tagId, new ArrayList<String>(List.of(newValues)));

            String newDisplayValue = String.join(", ", newValues);
            tagListView.getItems().set(selectedIndex, tag.getName() + ": " + newDisplayValue);
        });
    }
    
        /**
     * Navigates to the previous photo in the album.
     */
    @FXML
    private void handlePrevious(ActionEvent event){
        if (photoIndex > 0) {
            photoIndex--;
            long photoId = album.getPhotoIds().get(photoIndex);
            Photo photo = user.getPhotos().get(photoId);
            setupInfo(user, album, photo, photoIndex);
        } else {
            new Alert(Alert.AlertType.INFORMATION, "This is the first image.").showAndWait();
        }
    }
    
        /**
     * Navigates to the next photo in the album.
     */
    @FXML
    private void handleNext(ActionEvent event){
        if (photoIndex < album.getPhotoIds().size() - 1) {
            photoIndex++;
            long photoId = album.getPhotoIds().get(photoIndex);
            Photo photo = user.getPhotos().get(photoId);
            setupInfo(user, album, photo, photoIndex);
        } else {
            new Alert(Alert.AlertType.INFORMATION, "This is the last image.").showAndWait();
        }
    }
    
    /**
     * Moves the photo to another album specified by the user.
     */
    @FXML
    private void handleMoveToAlbum(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Enter the name of the destination album:");
        dialog.setContentText("Album Name:");
        dialog.initOwner(stackPane.getScene().getWindow());
    
        dialog.showAndWait().ifPresent(albumName -> {
            albumName = albumName.trim();
            if (albumName.isEmpty()) return;
    
            Album destAlbum = user.getAlbumByName(albumName);
            if (destAlbum == null) {
                new Alert(Alert.AlertType.ERROR, "Album not found.").showAndWait();
                return;
            }
    
            if (destAlbum.containsPhoto(photo.getId())) {
                new Alert(Alert.AlertType.INFORMATION, "This photo already exists in the selected album.").showAndWait();
                return;
            }
    
            destAlbum.addPhoto(photo.getId());
            album.removePhoto(photo.getId());
    
            new Alert(Alert.AlertType.INFORMATION, "Photo moved to album: " + albumName).showAndWait();
    
            // update photo index and UI
            if (album.getPhotoIds().isEmpty()) {
                ((Stage) stackPane.getScene().getWindow()).close();
            } else {
                if (photoIndex >= album.getPhotoIds().size()) {
                    photoIndex = album.getPhotoIds().size() - 1;
                }
                long newPhotoId = album.getPhotoIds().get(photoIndex);
                Photo newPhoto = user.getPhotos().get(newPhotoId);
                setupInfo(user, album, newPhoto, photoIndex);
            }
        });
    }    
    
    /**
     * Copies the photo to another album specified by the user.
     * 
     */
    @FXML
    private void handleCopyToAlbum(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Enter the name of the destination album:");
        dialog.setContentText("Album Name:");
        dialog.initOwner(stackPane.getScene().getWindow());
    
        dialog.showAndWait().ifPresent(albumName -> {
            albumName = albumName.trim();
            if (albumName.isEmpty()) return;
    
            Album destAlbum = user.getAlbumByName(albumName);
            if (destAlbum == null) {
                new Alert(Alert.AlertType.ERROR, "Album not found.").showAndWait();
                return;
            }
    
            if (destAlbum.containsPhoto(photo.getId())) {
                new Alert(Alert.AlertType.INFORMATION, "This photo already exists in the selected album.").showAndWait();
                return;
            }
    
            destAlbum.addPhoto(photo.getId());
            new Alert(Alert.AlertType.INFORMATION, "Photo copied to album: " + albumName).showAndWait();
        });
    }
    
    
}
