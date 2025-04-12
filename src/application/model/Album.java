package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Photo> photos;
    private transient User owner;
    
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Photo> getPhotos(){
        return photos;
    }
    
    public String getDetails(){
        String details = "Album" + name + "\nNumberOfPhoto: " + photos.size();
        return details;
    }
    
    public User getOwner(){
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
