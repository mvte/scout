package scout.sniper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import scout.Scout;
import scout.model.UserModel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.*;

import static java.lang.Thread.*;
import static scout.Scout.snipe_thread;

/**
 * Singleton class that will check all snipes for stock.
 */
public class SnipeChecker {
    private static final String filename = "snipes.ser";
    private static SnipeChecker INSTANCE;

    private HashSet<Snipe> snipes;
    private HashSet<CompletableFuture<Boolean>> stock;

    private SnipeChecker() {
        this.snipes = new HashSet<>();
        this.stock = new HashSet<>();
        loadSnipes();
    }

    public static synchronized SnipeChecker getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SnipeChecker();
        }
        return INSTANCE;
    }

    public void run() {
        CompletableFuture<Void> allFutures;

        for(;;) {
            stock.clear();
            checkSnipes();
            allFutures = CompletableFuture.allOf(stock.toArray(new CompletableFuture[0]));
            try {
                allFutures.get(6, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                break;
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
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
//        for(Snipe s : snipes) {
//            Scout.service.submit(() -> {
//                if(s.inStock()) notifyAllUsers(s);
//            });
//        }

        for (Snipe snipe : snipes) {
            stock.add(CompletableFuture.supplyAsync(snipe::inStock).thenApply(inStock -> {
                System.out.println("checking: " + snipe.getItemName());
                if (inStock) {
                    notifyAllUsers(snipe);
                }
                return inStock;
            }));
        }
    }

    private void notifyAllUsers(Snipe snipe) {
        snipes.remove(snipe);
        String stockMessage = snipe instanceof RutgersSnipe ? " **is open!**\n" : " **is in stock!**\n";
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(snipe.getItemName() + stockMessage)
                .setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
                .addField("now's your chance.", "press the button below to go to the item's webpage", false)
                .setTimestamp(java.time.Instant.now());
        ActionRow ar = ActionRow.of(Button.link(snipe.getUrl(), "go to"));

        for(UserModel user : snipe.getUsers()) {
            User jdaUser = Scout.bot.getUserById(user.getId());

            jdaUser.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(eb.setFooter(jdaUser.getName()).build())
                            .setComponents(ar))
                    .queue();
        }
    }

    private void loadSnipes() {
        if(!new File(filename).exists()) {
            return;
        }
        try (
                FileInputStream fin = new FileInputStream(filename);
                ObjectInputStream in = new ObjectInputStream(fin))
        {
            snipes = (HashSet<Snipe>)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSnipes() {
        if(snipes.isEmpty()) {
            return;
        }

        try(
                FileOutputStream fout = new FileOutputStream(filename);
                ObjectOutput oos = new ObjectOutputStream(fout))
        {
            oos.writeObject(snipes);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Snipe> getUserSnipes(UserModel user) {
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
