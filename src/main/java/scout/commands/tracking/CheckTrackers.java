package scout.commands.tracking;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.Scout;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import scout.tracker.Tracker;
import scout.tracker.TrackerChecker;

import java.util.List;

public class CheckTrackers extends Command {
    public CheckTrackers(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        UserModel user = UserModelDatabase.getInstance().getUser(event.getAuthor().getIdLong());
        User jdaUser = Scout.bot.getUserById(user.getId());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("your trackers")
                .setThumbnail(jdaUser.getAvatarUrl())
                .setFooter(jdaUser.getName())
                .setTimestamp(java.time.Instant.now());

        for(Tracker t : TrackerChecker.getInstance().getUserTrackers(user)) {
            String priceFound = t.getCurrentPrice() == -2 ? "PRICE_NOT_FOUND" : String.format("$%.2f", t.getCurrentPrice());
            String statusMessage = String.format("[%s](%s)%n last price change: %s", priceFound, t.getUrl(), t.getLastPriceChange());
            eb.addField(t.getItemName(), statusMessage, false);
        }

        try {
            jdaUser.openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessageEmbeds(eb.build()))
                    .queue();
            if(event.isFromGuild())
                channel.sendMessage("your tracker list has been sent to your dms").queue();
        } catch(NullPointerException e) {
            channel.sendMessage("your dms are closed. please open them to receive your tracker list and other notifications from scout").queue();
        }
    }

    @Override
    public String getName() {
        return "checktrackers";
    }

    @Override
    public String getHelp() {
        return "gives list of trackers. note: if the price says PRICE_NOT_FOUND, it means scout was unable to find the price of the item. " +
                "this is usually because the item is either out of stock or the price is not displayed on the page. don't worry, scout will " +
                "notify you when the price is found";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ct");
    }
}
