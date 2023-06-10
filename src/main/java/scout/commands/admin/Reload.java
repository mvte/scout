package scout.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.model.RutgersCourseDatabase;

import java.util.List;

public class Reload extends Command {

    public Reload(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();

        try {
            assert event.getMember() != null;
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                channel.sendMessage("you do not have permission to use this command").queue();
                return;
            }
        } catch (AssertionError e) {
            return;
        }

        channel.sendMessage("reloading...").queue();
        RutgersCourseDatabase rutgersCourseDatabase = RutgersCourseDatabase.getInstance();

//        double time = rutgersCourseDatabase.load();
//        if(time == RutgersCourseDatabase.LOAD_FAILED) {
//            channel.sendMessage("failed to load courses").queue();
//        } else {
//            channel.sendMessage("loaded courses in " + time + " seconds").queue();
//        }
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getHelp() {
        return "admin only: reloads the course database";
    }
}
