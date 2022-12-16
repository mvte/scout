package scout;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Scout {
    public static JDA bot;
    public static String prefix = Config.get("PREFIX");
    public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private Scout() throws LoginException, SQLException {

        bot = JDABuilder.createDefault(Config.get("token"))
                .setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Listener())
                .build();
    }


    public static void main(String[] args) throws Exception {
        new Scout();
    }

}
