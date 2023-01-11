package scout.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import scout.Scout;
import scout.sniper.Snipe;
import scout.tracker.Tracker;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {
    //TODO: notify should only take an embed/message, not the snipe or tracker

    /** unique user id from discord */
    private final long id;
    /** snipe list*/

    public UserModel(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void notifyUser(Snipe snipe) {
        User jdaUser = Scout.bot.getUserById(id);

        String stockMessage = snipe.getUrlType() == URLType.RUTGERS ? " is open!" : " is in stock!";
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(snipe.getItemName() + stockMessage)
                .setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
                .addField("now's your chance.", "press the button below to go to the item's webpage", false)
                .setTimestamp(java.time.Instant.now());
        ActionRow ar = ActionRow.of(Button.link(snipe.getUrl(), "go to"));

        jdaUser.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(eb.setFooter(jdaUser.getName()).build())
                        .setComponents(ar))
                .queue();
    }

    public void notifyUser(Tracker tracker) {
        String priceCompare = String.format("**%s** -> **%s**", tracker.getCurrentPriceString(), tracker.getNewPriceString());

        double delta = tracker.getPriceChange();
        int percentageDiff = tracker.getPercentageDifference();
        String sign = percentageDiff < 0 ? "-" : "+";
        String priceChangeAmount = String.format("%s$%.2f (%d%%)", sign, Math.abs(delta), percentageDiff);

        User jdaUser = Scout.bot.getUserById(id);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("the price of one your tracked items has updated!")
                .setDescription(tracker.getItemName())
                .setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
                .addField(priceCompare, priceChangeAmount, false)
                .setTimestamp(java.time.Instant.now());
        ActionRow ar = ActionRow.of(Button.link(tracker.getUrl(), "go to"));

        try {
            assert jdaUser != null;
            jdaUser.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(eb.build())
                            .setComponents(ar))
                    .queue();
            System.out.println("notified user " + jdaUser.getName() + " of price change for " + tracker.getItemName());
        } catch (AssertionError e) {
            System.out.println("user not found");
        }
    }

    @Override
    public boolean equals(@NotNull Object o) {
        if(!(o instanceof UserModel))
            return false;

        return ((UserModel)o).getId() == id;
    }

    @Override
    public int hashCode() {
        return (int)id;
    }
}
