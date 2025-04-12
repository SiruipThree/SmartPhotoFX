package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户类，保存用户名及其相册列表
 */
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
    
    /**
     * 判断该用户中是否已存在指定名称的相册
     * @param albumName 相册名
     * @return 存在则返回 true，否则 false
     */
    public boolean hasAlbum(String albumName){
        return albums.stream().anyMatch(a -> a.getName().equalsIgnoreCase(albumName));
    }
    
    @Override
    public String toString(){
        return username;
    }
}
