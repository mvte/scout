package scout.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;
import scout.commands.CommandCategory;

import java.util.List;

public class Initialize extends Command {

    public Initialize(CommandCategory category) {
        super(category);
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            return;
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
