package scout;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import scout.model.RutgersCourseDatabase;
import scout.sniper.SnipeChecker;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Scout {
    /*
    TODO:
    - migrate all database instances (rutgers course database, user model database, and
        hashset instances in TrackerChecker/SnipeChecker to an actual database
    - put in a docker container

     */
    public static JDA bot;
    public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static Thread snipe_thread = new Thread(SnipeChecker.getInstance()::run);

    private Scout() throws SQLException {
        bot = JDABuilder.createDefault(Config.get("token"))
                .setStatus(OnlineStatus.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .addEventListeners(new Listener())
                .build();
        Guild homeGuild = bot.getGuildById(System.getenv("HOME_GUILD"));
        if(homeGuild != null) {
            homeGuild.loadMembers();
        }
        load();
    }

    private void load() {
        try {
            Connection con = DriverManager.getConnection(
                    System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS"));
            String s1 = "CREATE TABLE IF NOT EXISTS snipes (" +
                    "user_id BIGINT, " +
                    "product_id VARCHAR(32), " +
                    "type VARCHAR(32)," +
                    "primary key (user_id, product_id, type))";
            Statement createSnipes = con.createStatement();
            createSnipes.executeUpdate(s1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("could not initialize tables, please restart");
        }

        SnipeChecker.getInstance().loadSnipes();
        RutgersCourseDatabase.getInstance();

        snipe_thread.start();
    }

    public static void save() {

    }


    public static void main(String[] args) {
        try {
            new Scout();
        } catch (Exception e) {
            System.out.println("Bot could not be started");
            e.printStackTrace();
        }
    }

}
