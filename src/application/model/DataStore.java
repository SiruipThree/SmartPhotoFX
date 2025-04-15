package application.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for saving, loading, and managing user data via serialization.
 * All user files are stored in the "data" directory.
 * Each user is saved to a file named &lt;username&gt;.user
 *
 * Used throughout the application for persistence and retrieval of user accounts.
 * 
 */
public class DataStore {

    private static final String DATA_DIR = "data";

    /**
     * Saves the user object to disk using serialization.
     * Data is first written to a temporary file, then renamed to the final name.
     *
     */
    public static void saveUser(User user) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(DATA_DIR + "/" + user.getUsername() + ".user.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();

            File tmpFile = new File(DATA_DIR + "/" + user.getUsername() + ".user.tmp");
            File finalFile = new File(DATA_DIR + "/" + user.getUsername() + ".user");

            if (finalFile.exists()) {
                finalFile.delete();
            }
            tmpFile.renameTo(finalFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a User object from disk based on the username.
     *
     */
    public static User loadUser(String username) {
        try {
            File file = new File(DATA_DIR + "/" + username + ".user");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                User user = (User) ois.readObject();
                ois.close();
                return user;
            }
        } catch (IOException | ClassNotFoundException e) {
            // Silent fail: returns null
        }
        return null;
    }

    /**
     * Loads the special "stock" user.
     *
     */
    public static User loadStockUser() {
        return loadUser("stock");
    }

    /**
     * Deletes the user file for the given username.
     *
     */
    public static void deleteUser(String username) {
        File file = new File(DATA_DIR + "/" + username + ".user");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Checks if a user file exists for the given username.
     *
     */
    public static boolean userExists(String username) {
        File file = new File(DATA_DIR + "/" + username + ".user");
        return file.exists();
    }

    /**
     * Returns a list of all saved usernames found in the data directory.
     *
     */
    public static List<String> getUserList() {
        List<String> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".user")) {
                        String name = file.getName().replace(".user", "");
                        users.add(name);
                    }
                }
            }
        }
        return users;
    }
}
