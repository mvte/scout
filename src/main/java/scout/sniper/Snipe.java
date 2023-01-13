package scout.sniper;

import scout.model.URLType;
import scout.model.UserModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Snipe implements Serializable {
    //TODO: check out why addUser() is not being used?

    public static final List<URLType> SUPPORTED_SNIPES = List.of(URLType.BEST_BUY, URLType.RUTGERS, URLType.GAMESTOP);
    public static final String ITEM_NAME_NOT_FOUND = "item name not found";

    String url;
    String itemName;
    ArrayList<UserModel> users;
    URLType urlType;

    public abstract boolean inStock();

    public void addUser(UserModel user) {
        users.add(user);
    }

    public String getUrl() {
        return url;
    }

    public String getItemName() {
        return itemName;
    }

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    public URLType getUrlType() {
        return urlType;
    }

    public abstract String parseItemName();

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Snipe)) {
            return false;
        }

        return ((Snipe)obj).getUrl().equals(url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }


}
