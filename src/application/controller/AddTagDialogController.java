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

/**
 * Controller for the Add Tag dialog.
 * 
 * Allows users to:
 * 
 *     View tags available for the user but not yet added to the photo
 *     Create a new tag type (with optional multi-value support)
 *     Delete unused tags
 *     dd one or more values to a tag and assign it to the photo
 * 
 * 
 */
public class AddTagDialogController {
    /** ListView to display available tags for this user. */
    @FXML
    private ListView<String> tagListView;
    /** TextField for entering a new tag name. */    
    @FXML
    private TextField tagNameField;
    /** Checkbox to specify whether a new tag supports multiple values. */
    @FXML
    private CheckBox allowMultiValueCheckBox;
    /** TextField for entering values for the selected tag. */
    @FXML
    private TextField tagValueField;

    /** Current user using the application. */
    private User user;
    /** Photo to which tags are being added. */
    private Photo photo;

    /** List of tags available for the user. */
    private ArrayList<Tag> tagList = new ArrayList<>();;
    
    /** Observable list to display tag names in the ListView. */    
    private ObservableList<String> tags = FXCollections.observableArrayList();

    /**
     * Initializes the controller after FXML components have been loaded.
     * Binds the ListView to the observable list of tags.
     */    
    @FXML
    public void initialize() {
        // Initialize the ListView with the current tags
        tagListView.setItems(tags);
    }
    /**
     * Sets the user and photo references, and loads the tags that can be added to the photo.
     *
     */
    void setUserAndPhoto(User user, Photo photo) {
        this.user = user;
        this.photo = photo;
        refreshTagList();
    }


    /**
     * Refreshes the tag ListView by loading tags owned by the user
     * that are not yet used by the selected photo.
     */
    void refreshTagList() {
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
    /**
     * Handles the action to create a new tag type.
     * Validates uniqueness and adds it to the user's tag collection.
     *
     */
    
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
    /**
     * Handles the action to delete a selected tag.
     * A tag can only be deleted if it is not used by any photo.
     *
     */
    @FXML
    public void handleDeleteTag(ActionEvent event) {
        // Get the selected tag from the ListView
        int selectedIndex = tagListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No tag selected.");
            alert.showAndWait();
            return;
        }
        
        Tag selectedTag = tagList.get(selectedIndex);
        
        // Check if any other photo has this tag
        for (Photo p : user.getPhotos().values()) {
            if (p.getTags().containsKey(selectedTag.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This tag is used by another photo. Cannot delete.");
                alert.showAndWait();
                return;
            }
        }
        
        // Remove the tag from the ListView and the tag list
        tags.remove(selectedIndex);
        tagList.remove(selectedIndex);
        user.getTags().remove(selectedTag.getId());
    }
    /**
     * Handles the action to assign one or more values to a selected tag,
     * and adds the tag with its value(s) to the current photo.
     *
     */
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
     * Handles the action to close the Add Tag dialog window.
     *
     */
    @FXML
    public void handleClose(ActionEvent event) {
        // Close the current window/stage
        Stage stage = (Stage) tagNameField.getScene().getWindow();
        stage.close();
    }
}
