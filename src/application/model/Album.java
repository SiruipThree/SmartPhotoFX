package application.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private long id;
    private String name;
    private ArrayList<Long> photoIds;
    
    public Album(long id, String name) {
        this.id = id;
        this.name = name;
        this.photoIds = new ArrayList<>();
    }

    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Long> getPhotoIds(){
        return photoIds;
    }
    
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
        
    @Override
    public String toString() {
        return name;
    }

    public boolean containsPhoto(long photoId) {
        return photoIds.contains(photoId);
    }
    
    public void addPhoto(long photoId) {
        if (!photoIds.contains(photoId)) {
            photoIds.add(photoId);
        }
    }
    
    public void removePhoto(long photoId) {
        photoIds.remove(photoId);
    }
    
}
