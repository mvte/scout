package scout.commands.tracking;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.Scout;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.URLType;
import scout.tracker.Tracker;
import scout.tracker.TrackerChecker;
import scout.tracker.TrackerFactory;

import java.util.List;

public class Track extends Command {

    public Track(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        long userId = event.getAuthor().getIdLong();

        if(args.size() != 1) {
            channel.sendMessage("incorrect amount of arguments. use the help command for usage").queue();
            return;
        }
        URLType urlType = URLType.getURLType(args.get(0));
        if(urlType != null && !Tracker.SUPPORTED_TRACKERS.contains(urlType)) {
            channel.sendMessage("scout doesn't support tracking for this url. currently, we only support " +
                    "amazon and newegg items.").queue();
            return;
        }

        channel.sendMessage("attempting to create tracker...").queue();

        Tracker tracker;
        if(urlType == null || (tracker = TrackerFactory.createTracker(args.get(0))) == null) {
            channel.sendMessage("something went wrong creating your tracker, try again").queue();
            return;
        }

        long user = event.getAuthor().getIdLong();
        if(TrackerChecker.getInstance().getUserTrackers(user).contains(tracker)) {
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
