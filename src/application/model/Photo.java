package application.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TreeMap;

public class Photo implements Serializable {
    private long id;
    private String filePath;
    private String caption;
    private long lastModified;
    private TreeMap<Long, ArrayList<String>> tags;

    public Photo(long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
        this.caption = "";
        File file = new File(filePath);
        if(file.exists()){
            this.lastModified = file.lastModified();
        } else {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        this.tags = new TreeMap<>();
    }

    public long getId() {
        return id;
    }
    
    public String getFilePath(){
        return filePath;
    }
    
    public String getCaption(){
        return caption;
    }
    
    public void setCaption(String caption){
        this.caption = caption;
    }
    
    public LocalDateTime getPhotoDate(){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.of("GMT"));
    }
    
    public String getDetails(){
        String details = "Path: " + filePath + "\nTitle: " + caption + "\nDate: " + getPhotoDate();
        return details;
    }

    public TreeMap<Long, ArrayList<String>> getTags() {
        return tags;
    }
    
    @Override
    public String toString(){
        return caption.isEmpty() ? new File(filePath).getName() : caption;
    }
}

