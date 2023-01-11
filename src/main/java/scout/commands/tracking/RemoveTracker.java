package scout.commands.tracking;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.URLType;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import scout.tracker.TrackerChecker;
import scout.tracker.TrackerFactory;

import java.util.List;

public class RemoveTracker extends Command {
    public RemoveTracker(CommandCategory category) {
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
        scout.tracker.Tracker tracker = TrackerFactory.createTracker(args.get(0));

        scout.tracker.Tracker realTracker;
        if((realTracker = TrackerChecker.getInstance().getTracker(tracker)) == null) {
            channel.sendMessage("this tracker does not exist").queue();
            return;
        }
        if(!realTracker.getUsers().contains(user)) {
            channel.sendMessage("you are not tracking this item").queue();
            return;
        }

        realTracker.getUsers().remove(user);
        TrackerChecker.getInstance().clearEmptyTrackers();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("successfully removed tracking request")
                .addField(realTracker.getItemName(), "this tracker was removed from your requests", false)
                .setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl());

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "removetracker";
    }

    @Override
    public String getHelp() {
        return "removes a tracker from your list of tracked items. \n usage: .removetracker <url>";
    }

    public List<String> getAliases() {
        return List.of("rt");
    }
}
