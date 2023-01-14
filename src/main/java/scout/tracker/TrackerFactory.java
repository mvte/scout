package scout.tracker;

import scout.model.URLType;

public class TrackerFactory {
    //TODO: why is createTracker not being used?

    public static Tracker createTracker(String url) {
        URLType urlType = URLType.getURLType(url);

        if(urlType == null)
            return null;

        Tracker tracker;
        switch(urlType) {
            case AMAZON:
                tracker = new AmazonTracker(url);
                break;
            case NEWEGG:
                tracker = new NeweggTracker(url);
                break;
            default:
                return null;
        }

        return tracker;
    }

    public static Tracker createTracker(String url, boolean addToChecker) {
        Tracker tracker = createTracker(url);
        if(tracker == null)
            return null;
        if(!addToChecker) {
            return tracker;
        }

        if(TrackerChecker.getInstance().addTracker(tracker))
            return tracker;
        else
            return TrackerChecker.getInstance().getTracker(tracker);
    }

}

