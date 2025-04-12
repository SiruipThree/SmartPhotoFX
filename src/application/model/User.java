package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    private String username;
    private List<Album> albums;
    
    public User(String username){
        this.username = username;
        this.albums = new ArrayList<>();
    }
    
    public String getUsername() {
        return username;
    }
    
    public List<Album> getAlbums(){
        return albums;
    }
    
    public boolean hasAlbum(String albumName){
        return albums.stream().anyMatch(a -> a.getName().equalsIgnoreCase(albumName));
    }
    
    @Override
    public String toString(){
        return username;
    }
}
