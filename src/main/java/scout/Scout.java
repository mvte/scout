package scout;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import scout.model.RutgersCourseDatabase;
import scout.model.UserModelDatabase;
import scout.sniper.SnipeChecker;
import scout.tracker.TrackerChecker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Scout {
    public static JDA bot;
    public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static Thread snipe_thread = new Thread(SnipeChecker.getInstance()::run);

    private Scout() {
        bot = JDABuilder.createDefault(Config.get("token"))
                .setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Listener())
                .build();
        load();
        snipe_thread.start();
    }

    private void load() {
        TrackerChecker.getInstance();
        SnipeChecker.getInstance();
        RutgersCourseDatabase.getInstance().loadFromEndpoint();
        UserModelDatabase.getInstance().loadFromFile();
    }

    public static void save() {
        TrackerChecker.getInstance().saveTrackers();
        SnipeChecker.getInstance().saveSnipes();
        RutgersCourseDatabase.getInstance().saveToFile();
        UserModelDatabase.getInstance().saveToFile();
    }


    public static void main(String[] args) {
        new Scout();
    }

}
