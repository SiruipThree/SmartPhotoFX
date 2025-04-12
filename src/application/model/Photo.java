package application.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filePath;
    private String caption;
    private long lastModified;
    
    public Photo(String filePath){
        this.filePath = filePath;
        this.caption = "";
        File file = new File(filePath);
        if(file.exists()){
            this.lastModified = file.lastModified();
        } else {
            this.lastModified = System.currentTimeMillis();
        }
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
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
    }
    
    public String getDetails(){
        String details = "Path: " + filePath + "\nTitle: " + caption + "\nDate: " + getPhotoDate();
        return details;
    }
    
    @Override
    public String toString(){
        return caption.isEmpty() ? new File(filePath).getName() : caption;
    }
}

