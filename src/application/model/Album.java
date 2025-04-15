package application.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a photo album belonging to a user.
 * Stores a list of photo IDs and provides metadata like name and photo date range.
 * Implements Serializable for persistent storage.
 * 
 */
public class Album implements Serializable {
    /** Unique identifier for the album. */   
    private long id;
    /** Display name of the album. */
    private String name;
    /** List of photo IDs in the album. */
     private ArrayList<Long> photoIds;
    /**
    * Constructs a new album with a given ID and name.
    *
    */ 
    public Album(long id, String name) {
        this.id = id;
        this.name = name;
        this.photoIds = new ArrayList<>();
    }
    /**
     * Returns the album ID.
     *
     */
    public long getId() {
        return id;
    }
    
    /**
     * Returns the name of the album.
     *
     */
    public String getName() {
        return name;
    }
    

    /**
     * Sets a new name for the album.
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of photo IDs in the album.
     *
     */
    public List<Long> getPhotoIds(){
        return photoIds;
    }
    /**
     * Generates and returns album details including size and date range of photos.
     *
     */   
    public String getDetails(User user){
        String details = "Album: " + name + "\nSize: " + photoIds.size();
        if (photoIds.size() > 0){
            // Date range
            long startDate = Long.MAX_VALUE;
            long endDate = Long.MIN_VALUE;
            for (Long id : photoIds) {
                Photo photo = user.getPhoto(id);
                long date = photo.getPhotoDate().toEpochSecond(ZoneOffset.UTC);
                if (date < startDate) {
                    startDate = date;
                }
                if (date > endDate) {
                    endDate = date;
                }
            }
            // Format date range
            String startDateStr = LocalDateTime.ofEpochSecond(startDate, 0, ZoneOffset.UTC).toString();
            String endDateStr = LocalDateTime.ofEpochSecond(endDate, 0, ZoneOffset.UTC).toString();
            details += "\nEarliest Photo: " + startDateStr;
            details += "\nLatest Photo: " + endDateStr;
        }
        return details;
    }
        
    /**
     * Returns the album's name as its string representation.
     *
     */
    @Override
    public String toString() {
        return name;
    }
    /**
     * Checks whether the album contains a photo with the given ID.
     *
     */
    public boolean containsPhoto(long photoId) {
        return photoIds.contains(photoId);
    }
    /**
     * Adds a photo to the album if it is not already included.
     *
     * @param photoId the ID of the photo to add
     */
    public void addPhoto(long photoId) {
        if (!photoIds.contains(photoId)) {
            photoIds.add(photoId);
        }
    }
    /**
     * Removes a photo from the album.
     *
     * @param photoId the ID of the photo to remove
     */
    public void removePhoto(long photoId) {
        photoIds.remove(photoId);
    }
    
}
