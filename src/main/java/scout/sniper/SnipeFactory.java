package scout.sniper;

import scout.model.RutgersCourseDatabase;
import scout.model.URLType;

public class SnipeFactory {

    public Snipe createSnipe(String urlString) {
        URLType url = URLType.getURLType(urlString);
        if(url == null)
            return null;

        switch(url) {
            case AMAZON:
                return new AmazonSnipe(urlString);
            case BEST_BUY:
                return new BestBuySnipe(urlString);
            case GAMESTOP:
                return new GameStopSnipe(urlString);
            case RUTGERS:
                if(RutgersCourseDatabase.getInstance().containsSection(urlString))
                    return new RutgersSnipe(urlString);
            default:
                return null;
        }
    }

}
