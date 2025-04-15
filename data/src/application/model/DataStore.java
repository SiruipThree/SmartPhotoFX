package application.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String DATA_DIR = "data";
    
    public static void saveUser(User user) {
        try {
            File dir = new File(DATA_DIR);
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(DATA_DIR + "/" + user.getUsername() + ".user.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
            // rename tmp file to final file
            File tmpFile = new File(DATA_DIR + "/" + user.getUsername() + ".user.tmp");
            File finalFile = new File(DATA_DIR + "/" + user.getUsername() + ".user");
            if(finalFile.exists()){
                finalFile.delete();
            }
            tmpFile.renameTo(finalFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static User loadUser(String username) {
        try {
            File file = new File(DATA_DIR + "/" + username + ".user");
            if(file.exists()){
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                User user = (User) ois.readObject();
                ois.close();
                return user;
            }
        } catch(IOException | ClassNotFoundException e) {
        }
        return null;
    }
    
    public static User loadStockUser() {
        return loadUser("stock");
    }
    
    public static void deleteUser(String username) {
        File file = new File(DATA_DIR + "/" + username + ".user");
        if(file.exists()){
            file.delete();
        }
    }
    
    public static boolean userExists(String username) {
        File file = new File(DATA_DIR + "/" + username + ".user");
        return file.exists();
    }
    
    public static List<String> getUserList(){
        List<String> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if(dir.exists()){
            File[] files = dir.listFiles();
            if(files != null){
                for(File file: files){
                    if(file.getName().endsWith(".user")){
                        String name = file.getName().replace(".user", "");
                        users.add(name);
                    }
                }
            }
        }
        return users;
    }
}
