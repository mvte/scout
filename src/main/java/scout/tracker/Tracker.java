package scout.tracker;

import scout.model.URLType;
import scout.model.UserModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A tracker object that tracks the price of an item and determines if the price has changed.
 */
public abstract class Tracker implements Serializable {

    public static final List<URLType> SUPPORTED_TRACKERS = List.of(URLType.AMAZON);
    public static final int PRICE_NEVER_CHANGED = -100001;
    public static final int PRICE_NOT_FOUND = -200001;
    public static final String ITEM_NAME_NOT_FOUND = "item name not found";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.46";
    String url;
    String itemName;
    URLType urlType;
    ArrayList<UserModel> users;
    double currentPrice;
    double newPrice;
    long lastPriceChange;



    /**
     * Connects to a product webpage or API and parses the price. Use priceChanged() to get price change information
     * instead of this.
     * @return the price of the product
     */
     abstract double parsePrice();

     abstract String parseItemName();

    /**
     * Calculates the price delta between the last price and the current price. If a change is detected,
     * currentPrice will be updated.
     * @return the change in price
     */
    private double calculatePriceDelta() {
        newPrice = parsePrice();
        double diff = newPrice - currentPrice;
        System.out.println("price diff: " + diff);

        if(diff <= 0.001 && diff >= -0.001) {
            return 0;
        }


        if(currentPrice == PRICE_NOT_FOUND) {
            return newPrice;
        }

        return diff;
    }

    public double getPriceDifference() {
        return newPrice - currentPrice;
    }

    public int getPercentageDifference() {
        if(currentPrice == PRICE_NOT_FOUND && newPrice == PRICE_NOT_FOUND) {
            return 0;
        }
        if(currentPrice == PRICE_NOT_FOUND) {
            return 100;
        }

        return (int)((newPrice - currentPrice) / currentPrice * 100);
    }

    public boolean priceChanged() {
        return calculatePriceDelta() != 0;
    }

    public double getPriceChange() {
        return newPrice - currentPrice;
    }

    /**
     * Calculates the time since the last price change.
     * @return the largest denomination of time since the price last changed.
     */
    public String getLastPriceChange() {
        if(lastPriceChange == PRICE_NEVER_CHANGED) {
            return "`unknown`";
        }

        long timeSinceChange = System.currentTimeMillis() - lastPriceChange;
        long seconds = timeSinceChange / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if(days > 0) {
            return String.format("`%d days ago`", days);
        } else if(hours > 0) {
            return String.format("`%d hours ago`", hours);
        } else if(minutes > 0) {
            return String.format("`%d minutes ago`", minutes);
        } else {
            return String.format("`%d seconds ago`", seconds);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getItemName() {
        return itemName;
    }

    public URLType getUrlType() {
        return urlType;
    }

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getCurrentPriceString() {
        if(currentPrice == PRICE_NOT_FOUND) {
            return "`unknown`";
        }
        return String.format("`$%.2f`", currentPrice);
    }

    public String getNewPriceString() {
        if(newPrice == PRICE_NOT_FOUND) {
            return "`unknown`";
        }
        return String.format("`$%.2f`", newPrice);
    }

    public void notifyAllUsers() {
        System.out.println("notifying users");
        for(UserModel user : users) {
            user.notifyUser(this);
        }
    }

    public void update() {
        lastPriceChange = System.currentTimeMillis();
        currentPrice = newPrice;
    }

    public void addUser(UserModel user) {
        users.add(user);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Tracker)) {
            return false;
        }

        return ((Tracker)obj).getUrl().equals(url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void removeUser(UserModel user) {
        users.remove(user);
    }

    public void debugPriceChange() {
        newPrice = currentPrice + 150;
    }
}
