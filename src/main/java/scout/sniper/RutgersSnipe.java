package scout.sniper;

import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;
import scout.model.RutgersCourseDatabase;
import scout.model.RutgersSection;
import scout.model.URLType;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class RutgersSnipe extends Snipe {

    /** open sections endpoint */
    private static final String openSections = "https://sis.rutgers.edu/soc/api/openSections.json?year=2023&term=1&campus=NB";
    /** pre-filled register form */
    private static final String register = "https://sims.rutgers.edu/webreg/editSchedule.htm?login=cas&semesterSelection=12023&indexList=%s";

    private String index;

    public RutgersSnipe(String url) {
        this.index = url;
        this.url = String.format(register, index);
        this.itemName = parseItemName();
        this.users = new ArrayList<>();
        this.urlType = URLType.RUTGERS;
    }

    /**
     *
     * @return true if in stock
     */
    @Override
    public boolean inStock() {
        try {
            URL request = new URL(openSections);
            HttpURLConnection con = (HttpURLConnection) request.openConnection();

            Scanner read = new Scanner(new GZIPInputStream(con.getInputStream()));
            String inline = "";
            while(read.hasNext()) {
                inline += read.nextLine();
            }
            read.close();

            JSONParser parse = new JSONParser();
            JSONArray data = (JSONArray) parse.parse(inline);

            return data.contains(index);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String parseItemName() {
        RutgersSection rs = RutgersCourseDatabase.getInstance().getSection(index);
        if(rs == null) {
            return "ITEM_NAME_NOT_FOUND";
        }

        return String.format("%s | section %s | %s", rs.getIndex(), rs.getSection(), rs.getTitle()).toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RutgersSnipe)) {
            return false;
        }

        return this.index.equals(((RutgersSnipe)obj).index);
    }

    @Override
    public int hashCode() {
        return index.hashCode();
    }

    public static void main(String[] args) {
        System.out.println(RutgersCourseDatabase.getInstance().loadFromEndpoint());
        RutgersSnipe rutgersSnipe = new RutgersSnipe("15534");

        System.out.println(rutgersSnipe.getItemName());
        System.out.println(rutgersSnipe.inStock());
    }
}
