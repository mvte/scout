package scout.tracker;

import scout.model.URLType;

public class TrackerFactory {

    public static Tracker createTracker(String url) {
        URLType urlType = URLType.getURLType(url);

        if(urlType == null)
            return null;

        switch(urlType) {
            case AMAZON:
                return new AmazonTracker(url);
            default:
                return null;
        }
    }

}

