package scout.commands;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ping extends Command {

    public Ping(CommandCategory category) {
		super(category);
    }

    public void handle(MessageReceivedEvent event, List<String> args) {
		JDA bot = event.getJDA();
		
		bot.getRestPing().queue( (time) ->
	    	event.getChannel().sendMessageFormat("pong! `%d ms`", time).queue()
		);
		
		
	}

	public String getName() {
		return "ping";
	}

	public String getHelp() {
		return "displays \"pong!\" along with the maestro's ping to discord's servers";
	}
	

}
