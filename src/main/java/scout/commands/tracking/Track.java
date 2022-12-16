package scout.commands.tracking;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.commands.Command;

import java.util.List;

public class Track implements Command {

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {

    }

    @Override
    public String getName() {
        return "track";
    }

    @Override
    public String getHelp(String prefix) {
        return null;
    }
}
