package application.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * 数据存储工具类，提供用户数据的保存、加载和删除功能
 */
public class DataStore {
    private static final String DATA_DIR = "data";
    
    /**
     * 保存一个用户数据到磁盘
     * @param user 要保存的用户
     */
    public static void saveUser(User user) {
        try {
            File dir = new File(DATA_DIR);
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(DATA_DIR + "/" + user.getUsername() + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // 保存之前确保每个相册都设置所属用户
            for(Album album: user.getAlbums()){
                album.setOwner(user);
            }
            oos.writeObject(user);
            oos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据用户名加载用户数据
     * @param username 用户名
     * @return 对应的 User 对象，若不存在则返回 null
     */
    public static User loadUser(String username) {
        try {
            File file = new File(DATA_DIR + "/" + username + ".ser");
            if(file.exists()){
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                User user = (User) ois.readObject();
                ois.close();
                // 设置每个相册的 owner 字段
                for(Album album: user.getAlbums()){
                    album.setOwner(user);
                }
                return user;
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 专门加载 stock 用户数据
     * @return stock 用户的 User 对象
     */
    public static User loadStockUser() {
        return loadUser("stock");
    }
    
    /**
     * 删除指定用户名的用户数据
     * @param username 用户名
     */
    public static void deleteUser(String username) {
        File file = new File(DATA_DIR + "/" + username + ".ser");
        if(file.exists()){
            file.delete();
        }
    }
    
    /**
     * 检查指定用户是否存在
     * @param username 用户名
     * @return 存在返回 true，否则 false
     */
    public static boolean userExists(String username) {
        File file = new File(DATA_DIR + "/" + username + ".ser");
        return file.exists();
    }
    
    /**
     * 获取系统中所有用户的用户名列表
     * @return 用户名列表
     */
    public static List<String> getUserList(){
        List<String> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if(dir.exists()){
            File[] files = dir.listFiles();
            if(files != null){
                for(File file: files){
                    if(file.getName().endsWith(".ser")){
                        String name = file.getName().replace(".ser", "");
                        users.add(name);
                    }
                }
            }
        }
        return users;
    }
}
