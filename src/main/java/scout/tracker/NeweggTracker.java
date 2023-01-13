package scout.tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class NeweggTracker extends Tracker {

    private String number;

    public NeweggTracker(String url) {
        super(url);
        this.number = parseNumber();
    }

    @Override
    public double parsePrice() {
        long start = System.currentTimeMillis();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get();
            Element price = doc.selectFirst("li.price-current");
            if(price == null) {
                return PRICE_NOT_FOUND;
            }
            String priceStr = price.text().replaceAll("[^0-9.]", "");
            System.out.println(priceStr);

            System.out.println("parsed price in " + (System.currentTimeMillis() - start) + " ms");
            return Double.parseDouble(priceStr.replaceAll(",", ""));
        } catch(Exception e) {
            e.printStackTrace();
            return PRICE_NOT_FOUND;
        }
    }

    @Override
    public String parseItemName() {
        try {
            Document doc = Jsoup
                    .connect(url)
                    .userAgent(USER_AGENT)
                    .get();
            Element title = doc.selectFirst("h1.product-title");

            if(title != null) {
                return title.text();
            }
            return ITEM_NAME_NOT_FOUND;
        } catch(Exception e) {
            e.printStackTrace();
            return ITEM_NAME_NOT_FOUND;
        }
    }

    private String parseNumber() {
        String[] split = url.split("/");
        int end = split[5].contains("?") ? split[5].indexOf("?") : split[5].length();
        return split[5].substring(0, end);
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NeweggTracker)) {
            return false;
        }

        return ((NeweggTracker)obj).getNumber().equals(getNumber());
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    public static void main(String[] args) {
        NeweggTracker neweggTracker = new NeweggTracker("https://www.newegg.com/oloy-64gb-288-pin-ddr4-sdram/p/N82E16820821212?Item=N82E16820821212&cm_sp=Homepage_SS-_-P1_20-821-212-_-01122023");
        System.out.println(neweggTracker.parseItemName());
        System.out.println(neweggTracker.getNumber());

    }
}

