package application.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 照片类，保存文件路径、标题和拍摄日期
 */
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
    
    /**
     * 将文件最后修改时间转为 LocalDateTime 类型
     * @return 照片拍摄时间
     */
    public LocalDateTime getPhotoDate(){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
    }
    
    /**
     * 返回照片详细信息（文件路径、标题、日期）
     * @return 详细信息字符串
     */
    public String getDetails(){
        String details = "路径：" + filePath + "\n标题：" + caption + "\n日期：" + getPhotoDate();
        return details;
    }
    
    @Override
    public String toString(){
        return caption.isEmpty() ? new File(filePath).getName() : caption;
    }
}

