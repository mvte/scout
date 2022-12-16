package scout.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Hello implements Command {

    public void handle(MessageReceivedEvent event, List<String> args) {
        MessageChannel channel = event.getChannel();
        User user = event.getAuthor();
        String greet;
        if(event.isFromGuild()) {
            greet = "hello ***" + event.getGuild().getMember(user).getEffectiveName() + "***, how are you :D";
        }
        else {
            greet = "hello ***" + user.getName() + "***, how are you :D";
        }
        channel.sendMessage(greet).queue();

    }

    public String getName() {
        return "hello";
    }

    public String getHelp(String prefix) {
        return "say hello to scout!";
    }

}
