package scout;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import scout.sniper.SnipeFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    private final CommandManager manager = new CommandManager();
    public static final long MEMBER_ROLE = 1053226501920800799L;

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // we don't want to respond to other bots or read it if it's a webhook message.
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
        String prefix = Config.get("prefix");

        //hardcode shutdown
        if(event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "shutdown")
                && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            event.getChannel().sendMessage("shutting down").complete();
            System.out.println("shutting down");
            Scout.save();
            event.getJDA().shutdown();
            System.exit(0);
        }

        if(event.getMessage().getContentRaw().startsWith(prefix)) {
            manager.handle(event, prefix);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getComponentId().startsWith("resnipe:")) {
            SnipeFactory snipeFactory = new SnipeFactory();
            String idx = event.getComponentId().substring("resnipe:".length());
            long userID = event.getUser().getIdLong();


            scout.sniper.Snipe snipe = snipeFactory.createSnipe(idx, true);
            if (snipe == null) {
                event.getChannel().sendMessage("something went wrong creating your snipe! check your url?").queue();
                return;
            }
            if(snipe.getUsers().contains(userID)) {
                event.getChannel().sendMessage("you are already sniping this item!").queue();
                return;
            }
            snipe.getUsers().add(userID);

            try {
                Connection conn = DriverManager.getConnection(
                        System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS"));
                String insert = "INSERT INTO snipes VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insert);
                stmt.setLong(1, userID);
                stmt.setString(2, idx);
                stmt.setString(3, snipe.getUrlType().toString());
                stmt.executeUpdate();
            } catch(Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("something went wrong adding your snipe! (").queue();
            }

            event.getChannel().sendMessage("you are now resniping " + snipe.getId()).queue();
            event.deferEdit().queue();
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        System.out.println("member joined: " + event.getMember().getId());
        event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(MEMBER_ROLE))).queue();
    }

}
