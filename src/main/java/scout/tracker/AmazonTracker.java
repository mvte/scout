package scout.tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class AmazonTracker extends Tracker {

    private final String asin;

    public AmazonTracker(String url) {
        super(url);
        this.asin = parseAsin();
    }

    private String parseAsin() {
        String[] split = url.split("/");
        return split[5].substring(0,10);
    }

    @Override
    double parsePrice() {
        long start = System.currentTimeMillis();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get();
            Element priceWhole = doc.selectFirst("span.a-price-whole");
            Element priceFraction = doc.selectFirst("span.a-price-fraction");

            if(priceWhole == null || priceFraction == null) {
                return PRICE_NOT_FOUND;
            }
            String priceStr = String.format("%s%s", priceWhole.text(), priceFraction.text());

            System.out.println("parsed price in " + (System.currentTimeMillis() - start) + " ms");
            return Double.parseDouble(priceStr.replaceAll(",", ""));
        } catch(Exception e) {
            e.printStackTrace();
            return PRICE_NOT_FOUND;
        }
    }

    @Override
    String parseItemName() {
        try {
            Document doc = Jsoup
                    .connect(url)
                    .userAgent(USER_AGENT)
                    .get();
            Element title = doc.selectFirst("span#productTitle");

            if(title != null) {
                return title.text();
            }
            return ITEM_NAME_NOT_FOUND;
        } catch(Exception e) {
            e.printStackTrace();
            return ITEM_NAME_NOT_FOUND;
        }
    }

    public String getAsin() {
        return asin;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AmazonTracker)) {
            return false;
        }

        return asin.equals(((AmazonTracker)obj).getAsin());
    }

    @Override
    public int hashCode() {
        return asin.hashCode();
    }

    public static void main(String[] args) {
    String url = "https://www.amazon.com/2022-Apple-MacBook-Laptop-chip/dp/B0B3C5H787/ref=sr_1_3?crid=JRU255GG2SQK&keywords=macbook&qid=1671402065&sprefix=macboo%2Caps%2C102&sr=8-3&ufe=app_do%3Aamzn1.fos.765d4786-5719-48b9-b588-eab9385652d5&th=1";
    AmazonTracker tracker = new AmazonTracker(url);

    System.out.println(tracker.currentPrice);
    System.out.println(tracker.getItemName());
    }

}
