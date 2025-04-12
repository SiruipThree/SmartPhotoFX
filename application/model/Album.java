package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 相册类，保存相册名称、照片列表
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Photo> photos;
    // transient 表示该字段不参与序列化，加载后会通过程序设置
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
    
    /**
     * 返回相册的基本信息字符串（可扩展显示日期范围等信息）
     * @return 相册信息字符串
     */
    public String getDetails(){
        String details = "相册：" + name + "\n照片数量：" + photos.size();
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
