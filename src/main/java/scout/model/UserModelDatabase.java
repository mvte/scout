package scout.model;

import scout.Scout;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UserModelDatabase {

    private static String filename = "users.ser";
    private HashMap<Long, UserModel> users;

    private static UserModelDatabase INSTANCE;

    private UserModelDatabase() {
        this.users = new HashMap<Long, UserModel>();
    }

    public static UserModelDatabase getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserModelDatabase();
        }

        return INSTANCE;
    }

    public UserModel getUser(long id) {
        return users.get(id);
    }

    public boolean addUserIfNotExist(UserModel userModel) {
        if(users.containsKey(userModel.getId()))
            return false;

        return users.put(userModel.getId(), userModel) == null;
    }

    public boolean loadFromFile() {
        if(!new File(filename).exists())
            return false;

        try(
                FileInputStream fin = new FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fin))
        {
            users = (HashMap<Long, UserModel>)in.readObject();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveToFile() {
        try(
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))
        {
            oos.writeObject(users);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
