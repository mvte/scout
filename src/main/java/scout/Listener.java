package scout;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(MEMBER_ROLE)).queue();
    }

}
