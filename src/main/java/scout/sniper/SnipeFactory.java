package scout.sniper;

import scout.model.RutgersCourseDatabase;
import scout.model.URLType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SnipeFactory {


    public Snipe createSnipe(String urlString) {
        URLType url = URLType.getURLType(urlString);
        if(url == null || !validateProduct(urlString))
            return null;

        Snipe snipe;
        switch(url) {
            case RUTGERS:
                snipe = new RutgersSnipe(urlString);
                break;
            default:
                return null;
        }

        return snipe;
    }

    public Snipe createSnipe(String urlString, boolean addToChecker) {
        Snipe snipe = createSnipe(urlString);
        if(snipe == null) {
            return null;
        }
        if(!addToChecker) {
            return snipe;
        }

        if(SnipeChecker.getInstance().addSnipe(snipe))
            return snipe;
        else
            return SnipeChecker.getInstance().getSnipe(snipe);
    }

    /**
     * Ensures that a product is valid before creating.
     * @param productId the product id to validate
     * @return true if the product is valid, false otherwise
     */
    private boolean validateProduct(String productId) {
        if(productId.length() == 5) {
            return validateRutgers(productId);
        }

        return false;
    }

    private boolean validateRutgers(String productId) {
        try {
            Connection con = DriverManager.getConnection(
                System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS"));
            String query = "SELECT * FROM rudb WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
