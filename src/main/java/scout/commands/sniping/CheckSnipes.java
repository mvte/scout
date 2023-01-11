package scout.commands.sniping;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import scout.Scout;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.sniper.SnipeChecker;

import java.util.List;

public class CheckSnipes extends Command {

    public CheckSnipes(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        UserModel user = UserModelDatabase.getInstance().getUser(event.getAuthor().getIdLong());
        User jdaUser = Scout.bot.getUserById(user.getId());

        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("your snipes")
            .setThumbnail(jdaUser.getAvatarUrl())
            .setFooter(jdaUser.getName())
            .setTimestamp(java.time.Instant.now());

        for(scout.sniper.Snipe s : SnipeChecker.getInstance().getUserSnipes(user)) {
            eb.addField(s.getItemName(), "[link](" + s.getUrl() + ")", false);
        }

        try {
            jdaUser.openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessageEmbeds(eb.build()))
                    .queue();
            if(event.isFromGuild())
                channel.sendMessage("your snipe list has been sent to your dms").queue();
        } catch(NullPointerException e) {
            channel.sendMessage("your dms are closed. please open them to receive your snipe list and other notifications from scout").queue();
        }

    }

    @Override
    public String getName() {
        return "checksnipes";
    }

    @Override
    public String getHelp() {
        return "gives list of snipes";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cs");
    }
}
