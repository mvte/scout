package scout.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class RutgersCourseDatabase {

    private static final String coursesEndPoint = "https://sis.rutgers.edu/soc/api/courses.json?year=2023&term=9&campus=NB";
    private static final String openSectionsEndPoint = "https://sis.rutgers.edu/soc/api/openSections.json?year=2023&term=9&campus=NB";
    private static RutgersCourseDatabase INSTANCE;
    private static final boolean DEBUG = true;
    private JSONArray openSectionsList;

    private RutgersCourseDatabase() {
        System.out.println("-= building rutgers course database =-");

        try {
            Connection con = DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS"));
            System.out.println("successfully connected to database ");

            System.out.println("creating table if not exists");
            Statement makeTable = con.createStatement();

            String s = "CREATE TABLE IF NOT EXISTS rudb (id VARCHAR(10), section VARCHAR(10), title VARCHAR(255), PRIMARY KEY (id))";
            makeTable.executeUpdate(s);

            double time = load(con);
            System.out.println("loaded courses from endpoint in " + time + " seconds");

            System.out.println("-= successfully built rutgers course database =-");
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("-= could not connect to database, please restart scout =-");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-= could not build database, please restart scout =-");
        }
    }

    public static RutgersCourseDatabase getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RutgersCourseDatabase();
        }
        return INSTANCE;
    }

    public RutgersSection getSection(String index) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/scout", "root", "micaela");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rudb WHERE id = ?");
            ps.setString(1, index);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return new RutgersSection(rs.getString("title"), rs.getString("section"), rs.getString("id"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("could not find course in database");
        }

        return null;
    }

    public boolean containsSection(String index) {
        return false;
    }

    /**
     * Parses courses Rutgers SOC API and loads relevant information into the database.
     * @return the time taken to load from API into database
     */
    public double load(Connection con) throws Exception {
        long start = System.currentTimeMillis();

        JSONArray raw = retrieve();
        if(raw == null) {
            throw new Exception("could not retrieve courses from endpoint");
        }
        System.out.println("successfully retrieved courses");

        if(!build(raw, con)) {
            throw new Exception("could not build database from retrieved JSON");
        }

        return (System.currentTimeMillis() - start)/1000.0;
    }

    /**
     * Makes a connection to the Rutgers SOC API and attempts to parse the data into a JSON Array.
     * @return the parsed JSON array from the API call
     */
    private JSONArray retrieve() {
        try{
            URL request = new URL(coursesEndPoint);
            HttpURLConnection con = (HttpURLConnection) request.openConnection();

            Scanner read = new Scanner(new GZIPInputStream(con.getInputStream()));

            String inline = "";
            while(read.hasNext()) {
                inline += read.nextLine();
            }
            read.close();

            JSONParser parse = new JSONParser();
            return (JSONArray) parse.parse(inline);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts specific information about all the classes
     * @param arr the retrieved json array from the rutgers database
     * @param con the connection to the database
     * @return true if the database was successfully built
     */
    private boolean build(JSONArray arr, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO rudb VALUES(?, ?, ?)");
        for(Object obj : arr) {
            JSONObject course = (JSONObject)obj;

            String title = (String)course.get("title");

            for(Object obj2 : (JSONArray)course.get("sections")) {
                JSONObject section = (JSONObject)obj2;

                String sectionNo = (String)section.get("number");
                String id = (String)section.get("index");

                ps.clearParameters();
                ps.setString(1, id);
                ps.setString(2, sectionNo);
                ps.setString(3, title);

                try {
                    ps.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e) {
                    continue;
                }
            }
        }

        return true;
    }

    public void parseOpenSections() {
        try {
            URL request = new URL(openSectionsEndPoint);
            HttpURLConnection con = (HttpURLConnection) request.openConnection();

            Scanner read = new Scanner(new GZIPInputStream(con.getInputStream()));
            String inline = "";
            while (read.hasNext()) {
                inline += read.nextLine();
            }
            read.close();

            JSONParser parse = new JSONParser();
            openSectionsList = (JSONArray) parse.parse(inline);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("could not parse open sections");
        }
    }

    public boolean isOpen(String index) {
        return openSectionsList.contains(index);
    }


    /** unit testing **/
    public static void main(String[] args) {
        RutgersCourseDatabase rudb = RutgersCourseDatabase.getInstance();

        RutgersSection section = rudb.getSection("07382");
        System.out.println(section.getSection() + " " + section.getTitle() + " " + section.getIndex());
    }

}
