package application.model;

import java.io.Serializable;
import java.util.HashMap;

 public class User implements Serializable{
    private String username;
    private long nextAlbumId;
    private long nextTagId;
    private long nextPhotoId;
    private HashMap<Long, Album> albumMap;
    private HashMap<Long, Tag> tagMap;
    private HashMap<Long, Photo> photoMap;
    private HashMap<String, Long> filePathToPhotoId = new HashMap<>();
    
    public User(String username) {
        this.username = username;
        this.albumMap = new HashMap<>();
        this.tagMap = new HashMap<>();
        this.photoMap = new HashMap<>();
        this.nextAlbumId = 1;
        this.nextTagId = 1;
        this.nextPhotoId = 1;

        // Add some default tags.
        Tag location = new Tag(allocTagId(), "location", false);
        Tag person = new Tag(allocTagId(), "person", true);
        tagMap.put(location.getId(), location);
        tagMap.put(person.getId(), person);
    }

    public long allocAlbumId() {
        return nextAlbumId++;
    }

    public long allocTagId() {
        return nextTagId++;
    }

    public long allocPhotoId() {
        return nextPhotoId++;
    }
    
    public String getUsername() {
        return username;
    }

    public Photo getPhoto(long id) {
        return photoMap.get(id);
    }

    public Album getAlbum(long id) {
        return albumMap.get(id);
    }

    public Tag getTag(long id) {
        return tagMap.get(id);
    }
    
    public HashMap<Long, Album> getAlbums(){
        return albumMap;
    }

    public HashMap<Long, Tag> getTags(){
        return tagMap;
    }

    public HashMap<Long, Photo> getPhotos(){
        return photoMap;
    }

    @Override
    public String toString(){
        return username;
    }

    public Album getAlbumByName(String name) {
        for (Album album : albumMap.values()) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

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
