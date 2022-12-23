package scout.commands.tracking;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.Scout;
import scout.commands.Command;
import scout.model.URLType;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import scout.tracker.Tracker;
import scout.tracker.TrackerChecker;
import scout.tracker.TrackerFactory;

import java.util.List;

public class Track implements Command {

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        long userId = event.getAuthor().getIdLong();

        if(args.size() != 1) {
            channel.sendMessage("incorrect amount of arguments. use the help command for usage").queue();
            return;
        }
        if(Tracker.SUPPORTED_TRACKERS.contains(URLType.getURLType(args.get(0)))) {
            channel.sendMessage("scout doesn't support tracking for this url. currently, we only support " +
                    "amazon items.").queue();
            return;
        }

        Tracker tracker;
        if((tracker = TrackerFactory.createTracker(args.get(0))) == null) {
            channel.sendMessage("something went wrong creating your tracker, try again").queue();
            return;
        }

        UserModel user = UserModelDatabase.getInstance().getUser(userId);
        if(!user.addTracker(tracker)) {
            channel.sendMessage("you are already tracking this item!").queue();
            return;
        }

        if(TrackerChecker.getInstance().addTracker(tracker)) {
            tracker.addUser(user);
        } else {
            TrackerChecker.getInstance().getTracker(tracker).addUser(user);
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("successfully added tracker request")
                .setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
                .addField(tracker.getItemName(), "you will be notified when this item's price has changed", false)
                .setFooter(event.getAuthor().getName())
                .setTimestamp(java.time.Instant.now());
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "track";
    }

    @Override
    public String getHelp() {
        return "scout will notify you when an item's price has changed. currently, scout only supports tracking amazon links\n" +
                "usage: `!track <url>`";
    }
}
