package scout.sniper;

import scout.model.RutgersCourseDatabase;
import scout.model.URLType;

public class SnipeFactory {


    public Snipe createSnipe(String urlString) {
        URLType url = URLType.getURLType(urlString);
        if(url == null)
            return null;

        Snipe snipe;
        switch(url) {
            case BEST_BUY:
                snipe = new BestBuySnipe(urlString);
                break;
            case GAMESTOP:
                snipe = new GameStopSnipe(urlString);
                break;
            case RUTGERS:
                if(RutgersCourseDatabase.getInstance().containsSection(urlString)) {
                    snipe = new RutgersSnipe(urlString);
                    break;
                }
            default:
                return null;
        }

        return snipe;
    }

    public Snipe createSnipe(String urlString, boolean addToChecker) {
        Snipe snipe = createSnipe(urlString);

        if(!addToChecker) {
            return snipe;
        }

        if(SnipeChecker.getInstance().addSnipe(snipe))
            return snipe;
        else
            return SnipeChecker.getInstance().getSnipe(snipe);
    }
}
