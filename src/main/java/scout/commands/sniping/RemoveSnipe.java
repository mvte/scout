package scout.commands.sniping;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import scout.sniper.SnipeChecker;
import scout.sniper.SnipeFactory;
import scout.model.URLType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RemoveSnipe extends Command {

    public RemoveSnipe(CommandCategory category) {
        super(category);
    }
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();

        if(args.size() != 1) {
            channel.sendMessage("incorrect number of arguments provided").queue();
            return;
        }
        if(URLType.getURLType(args.get(0)) == null) {
            channel.sendMessage("invalid argument").queue();
            return;
        }

        UserModel user = UserModelDatabase.getInstance().getUser(event.getAuthor().getIdLong());
        SnipeFactory sf = new SnipeFactory();
        scout.sniper.Snipe s = sf.createSnipe(args.get(0));

        scout.sniper.Snipe realSnipe;
        if((realSnipe = SnipeChecker.getInstance().getSnipe(s)) == null) {
            channel.sendMessage("this snipe does not exist").queue();
            return;
        }
        if(!realSnipe.getUsers().contains(user)) {
            System.out.println(s);
            System.out.println(realSnipe);
            channel.sendMessage("you are not sniping this item").queue();
            return;
        }

        realSnipe.getUsers().remove(user);
        user.getSnipes().remove(realSnipe);
        SnipeChecker.getInstance().clearEmptySnipes();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("successfully removed snipe request")
                .addField(realSnipe.getItemName(), "this item was removed from your requests", false)
                .setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl());

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "removesnipe";
    }

    @Override
    public String getHelp() {
        return "removes a snipe given the link that was provided to create it";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rs");
    }
}
