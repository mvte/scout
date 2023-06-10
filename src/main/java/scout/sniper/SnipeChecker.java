package scout.sniper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import scout.Scout;
import scout.model.RutgersCourseDatabase;
import scout.model.URLType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.*;

import static java.lang.Thread.*;

/**
 * Singleton class that will check all snipes for stock.
 */
public class SnipeChecker {
    private static SnipeChecker INSTANCE;
    private HashSet<Snipe> snipes;
    private HashSet<CompletableFuture<Boolean>> stock;
    private static int numInstances = 0;

    private SnipeChecker() {
        this.snipes = new HashSet<>();
        this.stock = new HashSet<>();
        numInstances++;
    }

    public static synchronized SnipeChecker getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SnipeChecker();
            System.out.println("num instances: " + numInstances);
        }
        return INSTANCE;
    }

    public void run() {
        CompletableFuture<Void> allFutures;

        for(;;) {
            RutgersCourseDatabase.getInstance().parseOpenSections();
            stock.clear();
            checkSnipes();
            allFutures = CompletableFuture.allOf(stock.toArray(new CompletableFuture[0]));
            try {
                allFutures.get(6, TimeUnit.SECONDS);
                sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Snipe getSnipe(Snipe snipe) {
        for(Snipe s : snipes) {
            if(s.equals(snipe))
                return s;
        }

        return null;
    }

    public boolean addSnipe(Snipe snipe) {
        return snipes.add(snipe);
    }

    public void clearEmptySnipes() {
        snipes.removeIf(s -> s.getUsers().isEmpty());
    }

    private void checkSnipes() {
        for (Snipe snipe : snipes) {
            stock.add(CompletableFuture.supplyAsync(snipe::inStock).thenApply(inStock -> {
                if (inStock) {
                    notifyAllUsers(snipe);
                }
                return inStock;
            }));
        }
    }

    private void notifyAllUsers(Snipe snipe) {
        try {
            removeSnipe(snipe);
        } catch(SQLException e) {
            System.out.println("something went wrong removing snipe: " + snipe.getItemName()
                + ". does it exist?");
            return;
        }

        String stockMessage = snipe instanceof RutgersSnipe ? " **is open!**\n" : " **is in stock!**\n";
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(snipe.getItemName() + stockMessage)
                .setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
                .addField("now's your chance.", "press the button below to go to the item's webpage", false)
                .setTimestamp(java.time.Instant.now());
        ActionRow ar = ActionRow.of(Button.link(snipe.getUrl(), "go to"));

        for(long user : snipe.getUsers()) {
            notifyUser(user, eb, ar);
            System.out.println("notified user " + user + ": " + snipe.getItemName());
        }
    }

    private void notifyUser(long userID, EmbedBuilder eb, ActionRow ar) {
        User jdaUser = Scout.bot.getUserById(userID);
        if(jdaUser == null) {
            System.out.println("user " + userID + " not found in JDA");
            return;
        }
        jdaUser.openPrivateChannel()
            .flatMap(channel -> channel.sendMessageEmbeds(eb.build()).setComponents(ar))
            .queue();
    }

    public ArrayList<Snipe> getUserSnipes(long user) {
        ArrayList<Snipe> userSnipes = new ArrayList<>();
        for(Snipe s : snipes) {
            if(s.getUsers().contains(user)) {
                userSnipes.add(s);
            }
        }
        return userSnipes;
    }

    public void removeSnipe(Snipe snipe) throws SQLException{
        //first delete from database
        Connection conn = DriverManager.getConnection(
            System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS")
        );
        conn.createStatement().executeUpdate(
        "DELETE FROM snipes WHERE product_id = '" + snipe.getUrl() + "'"
        );

        //then remove from snipes
        snipes.remove(snipe);
    }

    /**
     * Load snipes from database and instantiate them.
     */
    public void loadSnipes() {
        System.out.println("-= building snipes hash set =-");
        SnipeFactory factory = new SnipeFactory();
        int c = 0;
        try {
            Connection conn = DriverManager.getConnection(
                System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS")
            );
            var rs = conn.createStatement().executeQuery("SELECT * FROM snipes");
            System.out.println("pulled snipes from database");
            while(rs.next()) {
                long userID = rs.getLong("user_id");
                String productID = rs.getString("product_id");
                URLType type = URLType.valueOf(rs.getString("type"));

                Snipe snipe = factory.createSnipe(productID, true);
                snipe.addUser(userID);
            }
            System.out.println("pulled " + c + " snipes from database, and created " + snipes.size() + " instances");

            conn.close();
        } catch(SQLException e) {
            System.out.println("something went wrong loading snipes from database");
            e.printStackTrace();
        } catch(NullPointerException e) {
            e.printStackTrace();
            System.out.println("a snipe was found to be null");
        }

        System.out.println("-= snipes hash set built =-");
        for(Snipe s : snipes) {
            System.out.println(s.getItemName());
        }
    }
}
