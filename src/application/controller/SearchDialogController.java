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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.util.ArrayList;

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
        // Check date range selection, either both or none should be selected.
        if (enableDateCheckBox.isSelected() && (startDatePicker.getValue() == null || endDatePicker.getValue() == null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both start and end dates.");
            alert.showAndWait();
            return;
        }
        // Store the selected date range.
        LocalDate startDate = enableDateCheckBox.isSelected() ? startDatePicker.getValue() : null;
        LocalDate endDate = enableDateCheckBox.isSelected() ? endDatePicker.getValue() : null;
        // Validate date range.
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Start date cannot be after end date.");
            alert.showAndWait();
            return;
        }
        // Check tag combo box if enabled
        if (enableTag1CheckBox.isSelected() && (tagComboBox1.getValue() == null || tagValueField1.getText().trim().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a tag and enter a value for the first tag.");
            alert.showAndWait();
            return;
        }
        if (enableTag2CheckBox.isSelected() && (tagComboBox2.getValue() == null || tagValueField2.getText().trim().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a tag and enter a value for the second tag.");
            alert.showAndWait();
            return;
        }
        // Store the selected tag indexes and values.
        String tag1 = enableTag1CheckBox.isSelected() ? tagComboBox1.getValue() : null;
        String tagValue1 = enableTag1CheckBox.isSelected() ? tagValueField1.getText().trim() : null;
        String tag2 = enableTag2CheckBox.isSelected() ? tagComboBox2.getValue() : null;
        String tagValue2 = enableTag2CheckBox.isSelected() ? tagValueField2.getText().trim() : null;
        long tag1Index = -1;
        long tag2Index = -1;
        if (tag1 != null) {
            for (int i = 0; i < tagList.size(); i++) {
                if (tagList.get(i).getName().equals(tag1)) {
                    tag1Index = i;
                    break;
                }
            }
        }
        if (tag2 != null) {
            for (int i = 0; i < tagList.size(); i++) {
                if (tagList.get(i).getName().equals(tag2)) {
                    tag2Index = i;
                    break;
                }
            }
        }
        boolean isAnd = relationComboBox.getValue().equals("AND");
        // Perform the search based on the selected criteria.
        resultListView.getItems().clear();
        for (Photo photo : user.getPhotos().values()) {
            boolean matchesDate = true;
            boolean matchesTag1 = true;
            boolean matchesTag2 = true;
            // Check date range if enabled.
            if (startDate != null && endDate != null) {
                LocalDate photoDate = photo.getPhotoDate().toLocalDate();
                matchesDate = !photoDate.isBefore(startDate) && !photoDate.isAfter(endDate);
            }
            // Check first tag condition if enabled.
            if (tag1Index != -1) {
                Tag tag = tagList.get((int) tag1Index);
                matchesTag1 = matchTagSingleCriteria(photo, tag, tagValue1);
            }
            // Check second tag condition if enabled.
            if (tag2Index != -1) {
                Tag tag = tagList.get((int) tag2Index);
                matchesTag2 = matchTagSingleCriteria(photo, tag, tagValue2);
            }
            // Merge tag results based on the selected relation if both conditions are enabled.
            boolean matchesTags = true;
            if (enableTag1CheckBox.isSelected() && enableTag2CheckBox.isSelected()) {
                matchesTags = isAnd ? (matchesTag1 && matchesTag2) : (matchesTag1 || matchesTag2);
            } else if (enableTag1CheckBox.isSelected()) {
                matchesTags = matchesTag1;
            } else if (enableTag2CheckBox.isSelected()) {
                matchesTags = matchesTag2;
            }
            
            boolean matchesAll = matchesDate && matchesTags;
            if (matchesAll) {
                resultListView.getItems().add(photo);
            }
        }
    }
}
