package application.controller;

import java.util.ArrayList;

import application.model.Photo;
import application.model.Tag;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTagDialogController {

    @FXML
    private ListView<String> tagListView;
    
    @FXML
    private TextField tagNameField;
    
    @FXML
    private CheckBox allowMultiValueCheckBox;
    
    @FXML
    private TextField tagValueField;

    private User user;
    private Photo photo;

    private ArrayList<Tag> tagList = new ArrayList<>();;
    
    // Observable list for storing tag strings
    private ObservableList<String> tags = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Initialize the ListView with the current tags
        tagListView.setItems(tags);
    }

    void setUserAndPhoto(User user, Photo photo) {
        this.user = user;
        this.photo = photo;

        // We only show the tags that this user has but this photo doesn't have
        tags.clear();
        tagList.clear();
        for (Tag tag : user.getTags().values()) {
            if (!photo.getTags().containsKey(tag.getId())) {
                tags.add(tag.getName() + (tag.isMultiValue() ? " (multi-value)" : ""));
                tagList.add(tag);
            }
        }
    }
    
    @FXML
    public void handleNewTag(ActionEvent event) {
        String name = tagNameField.getText().trim();
        boolean allowMulti = allowMultiValueCheckBox.isSelected();
        
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Tag Name must not be empty.");
            alert.showAndWait();
            return;
        }

        // Check if the tag already exists
        for (Tag tag : user.getTags().values()) {
            if (tag.getName().equals(name)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tag with this name already exists (either for user or for this photo).");
                alert.showAndWait();
                return;
            }
        }

        // Add the new tag to the tag list
        tags.add(name + (allowMulti ? " (multi-value)" : ""));
        Tag newTag = new Tag(user.allocTagId(), name, allowMulti);
        user.getTags().put(newTag.getId(), newTag);
        tagList.add(newTag);
        
        // Clear input fields and reset the checkbox
        tagNameField.clear();
        allowMultiValueCheckBox.setSelected(false);
    }

    @FXML
    public void handleAddTagValue(ActionEvent event) {
        String value = tagValueField.getText().trim();
        // Split the values
        String[] values = value.split(",");
        // Filter the empty values
        values = java.util.Arrays.stream(values).map(v -> v.trim()).filter(v -> !v.isEmpty()).toArray(String[]::new);
        if (values.length == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Tag Value must not be empty.");
            alert.showAndWait();
            return;
        }

        // Get the selected tag from the ListView
        int selectedIndex = tagListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No tag selected.");
            alert.showAndWait();
            return;
        }
        
        Tag selectedTag = tagList.get(selectedIndex);
        
        // Add the value to the photo's tags
        photo.getTags().put(selectedTag.getId(), new ArrayList<String>(java.util.Arrays.asList(values)));
        
        // End dialog
        Stage stage = (Stage) tagValueField.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Handle the "Close" button action to close the dialog window.
     */
    @FXML
    public void handleClose(ActionEvent event) {
        // Close the current window/stage
        Stage stage = (Stage) tagNameField.getScene().getWindow();
        stage.close();
    }
}
