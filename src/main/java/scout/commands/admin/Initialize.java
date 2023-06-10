package scout.commands.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.URLType;
import scout.sniper.Snipe;
import scout.tracker.Tracker;

import java.util.List;

public class Initialize extends Command {

    private static final String LINE_BREAK = "──────────────────────────────────────";

    public Initialize(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if(event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }

        event.getMessage().delete().queue();

        String channelName = event.getChannel().getName();
        switch(channelName) {
            case "welcome":
                initializeWelcome(event);
                break;
            case "announcements":
                initializeAnnouncements(event);
                break;
            case "how-to":
                initializeHowTo(event);
                break;
            default:
                return;
        }

        //send message to owner
        event.getGuild().retrieveOwner()
                .queue(owner -> owner.getUser().openPrivateChannel()
                .queue(c -> c.sendMessage("initialized " + channelName + " channel")
                .queue()));
    }

    private void initializeWelcome(MessageReceivedEvent event) {
        String sniperInfo = "i can notify you whenever a product is in stock. all you need is a link to the product. currently," +
                "i support sniping for the following websites: " + getSniperWebsites();
        String courseInfo = "if you're a rutgers student, i can also notify you whenever a course opens up. all you need is the" +
                "section index. for example, `04530` is the index for a section of cs111.";
        String trackerInfo = "i can notify you whenever a product's price changes. all you need is a link to the product. currently," +
                "i support tracking for the following websites: " + getTrackerWebsites();
        String howInfo = "depending on the product, i either scrape information directly from the url you provide or i'll access the website's api. " +
                "then, i'll periodically check the product's price/availability. snipes are checked every second, and trackers are checked every 5 minutes. ";
        String sourceCodeInfo = "if you _really_ wanna know how i work, i'm open source! you can check out my code [here](https://github.com/mvte/scout)";
        String supportInfo = "i'm still in development, so i might not be perfect. if you have any questions, feel free to ask in general. " +
                "if you find any bugs, please report them to any of our admins. if you have any suggestions, feel free to suggest them in general.";


        EmbedBuilder welcomeEb = new EmbedBuilder();
        welcomeEb.setTitle("welcome")
                .setThumbnail(event.getGuild().getIconUrl())
                .setDescription("we hope you enjoy your stay")
                .setFooter("by mute | github.com/mvte");

        EmbedBuilder infoEb = new EmbedBuilder();
        infoEb.setTitle("info")
                .setThumbnail(event.getGuild().getIconUrl())
                .setDescription("here's what i do")
                .addField("sniper", sniperInfo, false)
                .addField("course sniper", courseInfo, false)
                .addField("tracker", trackerInfo, false);

        EmbedBuilder howEb = new EmbedBuilder();
        howEb.setTitle("how do i work?")
                .setDescription(howInfo)
                .addField("source code", sourceCodeInfo, false)
                .addField("support", supportInfo, false);

        sendAll(event, welcomeEb, infoEb, howEb);
    }

    private String getSniperWebsites() {
        StringBuilder websites = new StringBuilder();
        for(URLType url : Snipe.SUPPORTED_SNIPES) {
            websites.append(url.name().toLowerCase()).append(", ");
        }

        return websites.substring(0, websites.length() - 2);
    }

    private String getTrackerWebsites() {
        StringBuilder websites = new StringBuilder();
        for(URLType url : Tracker.SUPPORTED_TRACKERS) {
            websites.append(url.name().toLowerCase()).append(", ");
        }
        return websites.substring(0, websites.length() - 2);
    }

    private void initializeAnnouncements(MessageReceivedEvent event) {
        //TODO
    }

    private void initializeHowTo(MessageReceivedEvent event) {
        //TODO
    }

    private void sendAll(MessageReceivedEvent event, EmbedBuilder... ebs) {
        for(EmbedBuilder eb : ebs) {
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            event.getChannel().sendMessage(LINE_BREAK).queue();
        }
    }

    @Override
    public String getName() {
        return "initialize";
    }

    @Override
    public String getHelp() {
        return "admin only: sends messages to info channels";
    }

}
