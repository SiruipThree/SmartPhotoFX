package application.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a user in the photo application.
 * Each user can have multiple albums, photos, and tags.
 * Manages unique ID allocation and photo import logic.
 * 
 * Implements Serializable to enable data persistence.
 * 
 * Default tag types include:
 * - location (single value)
 * - person (multi-value)
 * 
 */
 public class User implements Serializable{
    private String username;
    private long nextAlbumId;
    private long nextTagId;
    private long nextPhotoId;
    private HashMap<Long, Album> albumMap;
    private HashMap<Long, Tag> tagMap;
    private HashMap<Long, Photo> photoMap;
    private HashMap<String, Long> filePathToPhotoId = new HashMap<>();
    
        /**
     * Constructs a new User with the specified username.
     * Initializes maps and adds default tag types.
     *
     */
        public User(String username) {
        this.username = username;
        this.albumMap = new HashMap<>();
        this.tagMap = new HashMap<>();
        this.photoMap = new HashMap<>();
        this.nextAlbumId = 1;
        this.nextTagId = 1;
        this.nextPhotoId = 1;

        // Add default tag types
        Tag location = new Tag(allocTagId(), "location", false);
        Tag person = new Tag(allocTagId(), "person", true);
        tagMap.put(location.getId(), location);
        tagMap.put(person.getId(), person);
    }

    /**
     * Allocates a new unique ID for an album.
     * 
     */
    public long allocAlbumId() {
        return nextAlbumId++;
    }

    /**
     * Allocates a new unique ID for a tag.
     * 
     * @return the next available tag ID
     */
    public long allocTagId() {
        return nextTagId++;
    }

    /**
     * Allocates a new unique ID for a photo.
     * 
     */
    public long allocPhotoId() {
        return nextPhotoId++;
    }

    /**
     * Returns the username.
     * 
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns a photo given its ID.
     * 
     */
    public Photo getPhoto(long id) {
        return photoMap.get(id);
    }

    /**
     * Returns an album given its ID.
     * 
     */
    public Album getAlbum(long id) {
        return albumMap.get(id);
    }

    /**
     * Returns a tag given its ID.
     * 
     */
    public Tag getTag(long id) {
        return tagMap.get(id);
    }

    /**
     * Returns all albums for the user.
     * 
     */
    public HashMap<Long, Album> getAlbums() {
        return albumMap;
    }

    /**
     * Returns all tags for the user.
     * 
     */
    public HashMap<Long, Tag> getTags() {
        return tagMap;
    }

    /**
     * Returns all photos for the user.
     * 
     */
    public HashMap<Long, Photo> getPhotos() {
        return photoMap;
    }

    /**
     * Returns the username as a string representation.
     * 
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Finds an album by its name (case-insensitive).
     * 
     */
    public Album getAlbumByName(String name) {
        for (Album album : albumMap.values()) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Imports a photo into the user's collection using the file path.
     * If the photo already exists (by file path), returns the existing one.
     *
     */
    public Photo importPhoto(String filePath) {
        if (filePathToPhotoId.containsKey(filePath)) {
            long existingId = filePathToPhotoId.get(filePath);
            return photoMap.get(existingId);
        }

        long newId = allocPhotoId();
        Photo newPhoto = new Photo(newId, filePath);
        photoMap.put(newId, newPhoto);
        filePathToPhotoId.put(filePath, newId);
        return newPhoto;
    }
}