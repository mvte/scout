package scout.sniper;

import scout.model.URLType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Snipe {
    //TODO: check out why addUser() is not being used?

    public static final List<URLType> SUPPORTED_SNIPES = List.of(URLType.RUTGERS);
    public static final String ITEM_NAME_NOT_FOUND = "item name not found";

    String url;
    String itemName;
    ArrayList<Long> users;
    URLType urlType;

    public abstract boolean inStock();

    public void addUser(long userID) {
        users.add(userID);
    }

    public String getUrl() {
        return url;
    }

    public String getItemName() {
        return itemName;
    }

    public ArrayList<Long> getUsers() {
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
