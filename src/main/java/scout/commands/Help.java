package scout.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Help implements Command {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {

    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
