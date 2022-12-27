package scout.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.UserModel;
import scout.model.UserModelDatabase;
import scout.tracker.Tracker;
import scout.tracker.TrackerFactory;

import java.util.List;

public class ForceNotify extends Command {
    public ForceNotify(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.getChannel().sendMessage("you do not have permission to use this command").queue();
            return;
        }

        MessageChannel channel = event.getChannel();
        long userId = event.getAuthor().getIdLong();

        if(args.size() != 1) {
            channel.sendMessage("incorrect amount of arguments. use the help command for usage").queue();
            return;
        }

        Tracker tracker;
        if((tracker = TrackerFactory.createTracker(args.get(0))) == null) {
            channel.sendMessage("something went wrong creating your tracker! check your url?").queue();
            return;
        }

        UserModel user = UserModelDatabase.getInstance().getUser(userId);
        tracker.addUser(user);

        tracker.debugPriceChange();
        tracker.notifyAllUsers();

        tracker.removeUser(user);
    }

    @Override
    public String getName() {
        return "fn";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
