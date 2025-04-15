package application.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Represents a photo imported by the user.
 * Contains metadata such as file path, caption, last modified date,
 * and a collection of associated tags.
 * 
 * Tags are stored as a mapping from tag IDs to a list of values,
 * allowing support for both single- and multi-value tags.
 * 
 * Implements Serializable to allow saving to disk.
 * 
 */
public class Photo implements Serializable {

    private long id;
    private String filePath;
    private String caption;
    private long lastModified;
    private TreeMap<Long, ArrayList<String>> tags;

    /**
     * Constructs a new Photo instance with the given ID and file path.
     * Captures the last modified timestamp from the file.
     *
     */
    public Photo(long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
        this.caption = "";

        File file = new File(filePath);
        if (file.exists()) {
            this.lastModified = file.lastModified();
        } else {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        this.tags = new TreeMap<>();
    }

    /**
     * Returns the photo's unique ID.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the file path of the photo.
     * 
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the photo's caption.
     * 
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption for the photo.
     * 
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns the photo's last modified date as a LocalDateTime.
     * This is treated as the photo's "taken" date.
     * 
     */
    public LocalDateTime getPhotoDate() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.of("GMT"));
    }

    /**
     * Returns a formatted string with file path, caption, and date.
     * 
     */
    public String getDetails() {
        return "Path: " + filePath + "\nTitle: " + caption + "\nDate: " + getPhotoDate();
    }

    /**
     * Returns the photo's tags.
     */
    public TreeMap<Long, ArrayList<String>> getTags() {
        return tags;
    }

    /**
     * Returns a user-friendly label for the photo.
     * Uses caption if available, otherwise the filename.
     * 
     */
    @Override
    public String toString() {
        return caption.isEmpty() ? new File(filePath).getName() : caption;
    }
}
