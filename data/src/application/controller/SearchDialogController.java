package application.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import application.model.Album;
import application.model.Photo;
import application.model.Tag;
import application.model.User;

public class SearchDialogController {
    
    @FXML
    private CheckBox enableDateCheckBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox enableTag1CheckBox;
    @FXML
    private ComboBox<String> tagComboBox1;
    @FXML
    private TextField tagValueField1;
    @FXML
    private CheckBox enableTag2CheckBox;
    @FXML
    private ComboBox<String> tagComboBox2;
    @FXML
    private TextField tagValueField2;
    @FXML
    private ComboBox<String> relationComboBox;
    @FXML
    private ListView<Photo> resultListView;
    @FXML
    private TextField newAlbumNameField;

    User user;
    ArrayList<Tag> tagList;

    @FXML
    public void initialize() {
        // Disable date pickers by default
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
        enableDateCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            startDatePicker.setDisable(!isSelected);
            endDatePicker.setDisable(!isSelected);
        });
        
        // Initialize relation combo box with values.
        relationComboBox.setItems(FXCollections.observableArrayList("AND", "OR"));
        relationComboBox.setValue("AND");
        
        // Initialize result list view with an empty list.
        resultListView.setCellFactory(listView -> new ListCell<Photo>() {
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

                container.setAlignment(Pos.CENTER);
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

        tagComboBox1.setDisable(true);
        tagValueField1.setDisable(true);
        tagComboBox2.setDisable(true);
        tagValueField2.setDisable(true);
        enableTag2CheckBox.setDisable(true);
        relationComboBox.setDisable(true);
        enableTag1CheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            tagComboBox1.setDisable(!isSelected);
            tagValueField1.setDisable(!isSelected);
            if (isSelected) {
                enableTag2CheckBox.setDisable(false);
            } else {
                enableTag2CheckBox.setSelected(false);
                tagComboBox2.setDisable(true);
                tagValueField2.setDisable(true);
            }
        });
        enableTag2CheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            tagComboBox2.setDisable(!isSelected);
            tagValueField2.setDisable(!isSelected);
            relationComboBox.setDisable(!isSelected);
        });
    }

    public void setUser(User user) {
        this.user = user;
        // Initialize tag combo boxes with tag names.
        tagList = new ArrayList<>(user.getTags().values());
        ObservableList<String> allTags = FXCollections.observableArrayList(tagList.stream()
                .map(tag -> tag.getName())
                .toArray(String[]::new));
        tagComboBox1.setItems(allTags);
        tagComboBox2.setItems(allTags);
    }
    
    /**
     * Called when the 'Save as Album' button is pressed.
     */
    @FXML
    public void handleSaveAsAlbum(ActionEvent event) {
        String albumName = newAlbumNameField.getText().trim();
        if (albumName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Album name cannot be empty.");
            alert.showAndWait();
            return;
        }
        // Check if the album name already exists.
        if (user.getAlbums().values().stream().anyMatch(album -> album.getName().equalsIgnoreCase(albumName))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Album name already exists. Please choose a different name.");
            alert.showAndWait();
            return;
        }
        // Create the new album.
        Album album = new Album(user.allocAlbumId(), albumName);
        album.getPhotoIds().addAll(resultListView.getItems().stream()
                .map(Photo::getId)
                .toList());
        user.getAlbums().put(album.getId(), album);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Search results saved as album: " + albumName);
        alert.showAndWait();
    }

    private boolean matchTagSingleCriteria(Photo photo, Tag tag, String tagValue) {
        // Check if the photo has a tag with the specified name and one of its values matches the specified value.
        if (photo.getTags().containsKey(tag.getId())) {
            ArrayList<String> values = photo.getTags().get(tag.getId());
            for (String value : values) {
                if (value.equalsIgnoreCase(tagValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    @FXML
public void handleSearch(ActionEvent event) {
    resultListView.getItems().clear();

    // --- Check if searching by date ---
    boolean usingDate = enableDateCheckBox.isSelected();
    LocalDate startDate = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();

    if (usingDate) {
        if (startDate == null || endDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select both start and end dates.").showAndWait();
            return;
        }
        if (startDate.isAfter(endDate)) {
            new Alert(Alert.AlertType.ERROR, "Start date cannot be after end date.").showAndWait();
            return;
        }
    }

    // --- Check if searching by tag(s) ---
    boolean usingTag1 = enableTag1CheckBox.isSelected();
    boolean usingTag2 = enableTag2CheckBox.isSelected();
    boolean usingTags = usingTag1 || usingTag2;

    if (!usingDate && !usingTags) {
        new Alert(Alert.AlertType.ERROR, "Please select at least one search option.").showAndWait();
        return;
    }

    if (usingDate && usingTags) {
        new Alert(Alert.AlertType.ERROR, "Cannot search by both date and tag(s) together.").showAndWait();
        return;
    }

    String tagName1 = tagComboBox1.getValue();
    String tagValue1 = tagValueField1.getText().trim();
    String tagName2 = tagComboBox2.getValue();
    String tagValue2 = tagValueField2.getText().trim();

    Tag tag1 = null, tag2 = null;
    if (usingTag1) {
        if (tagName1 == null || tagValue1.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please provide tag name and value for the first tag.").showAndWait();
            return;
        }
        tag1 = tagList.stream().filter(t -> t.getName().equals(tagName1)).findFirst().orElse(null);
    }

    if (usingTag2) {
        if (tagName2 == null || tagValue2.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please provide tag name and value for the second tag.").showAndWait();
            return;
        }
        tag2 = tagList.stream().filter(t -> t.getName().equals(tagName2)).findFirst().orElse(null);
    }

    boolean isAnd = relationComboBox.getValue().equals("AND");

    // --- Start filtering photos ---
    for (Photo photo : user.getPhotos().values()) {
        // Date filter

        // System.out.println("Photo: " + photo.getFilePath() + " Date: " + photo.getPhotoDate().toLocalDate());

        if (usingDate) {
            LocalDate photoDate = photo.getPhotoDate().toLocalDate();
            if (photoDate.isBefore(startDate) || photoDate.isAfter(endDate)) {
                continue;
            }
            resultListView.getItems().add(photo);
            continue;
        }

        // Tag filter
        boolean matchesTag1 = false;
        boolean matchesTag2 = false;

        if (tag1 != null) matchesTag1 = matchTagSingleCriteria(photo, tag1, tagValue1);
        if (tag2 != null) matchesTag2 = matchTagSingleCriteria(photo, tag2, tagValue2);

        boolean matches = false;
        if (usingTag1 && usingTag2) {
            matches = isAnd ? (matchesTag1 && matchesTag2) : (matchesTag1 || matchesTag2);
        } else if (usingTag1) {
            matches = matchesTag1;
        } else if (usingTag2) {
            matches = matchesTag2;
        }

        if (matches) {
            resultListView.getItems().add(photo);
        }
    }

    if (resultListView.getItems().isEmpty()) {
        new Alert(Alert.AlertType.INFORMATION, "No photos match the given criteria.").showAndWait();
    }
}

}
