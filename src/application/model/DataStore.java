package application.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class DataStore {
    private static final String DATA_DIR = "data";
    
    public static void saveUser(User user) {
        try {
            File dir = new File(DATA_DIR);
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(DATA_DIR + "/" + user.getUsername() + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(Album album: user.getAlbums()){
                album.setOwner(user);
            }
            oos.writeObject(user);
            oos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static User loadUser(String username) {
        try {
            File file = new File(DATA_DIR + "/" + username + ".ser");
            if(file.exists()){
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                User user = (User) ois.readObject();
                ois.close();
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
    
    public static User loadStockUser() {
        return loadUser("stock");
    }
    
    public static void deleteUser(String username) {
        File file = new File(DATA_DIR + "/" + username + ".ser");
        if(file.exists()){
            file.delete();
        }
    }
    
    public static boolean userExists(String username) {
        File file = new File(DATA_DIR + "/" + username + ".ser");
        return file.exists();
    }
    
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
