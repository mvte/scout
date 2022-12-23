package scout.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Coinflip implements Command {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		
		double rand = Math.random();
		
		String result = rand > 0.5 ? "heads" : "tails";
		
		channel.sendMessage(result).queue();
		
	}

	@Override
	public String getName() {
		return "coinflip";
	}

	@Override
	public String getHelp() {
		return "flips a coin";
	}
	
	@Override 
	public List<String> getAliases() {
		return List.of("flipcoin", "cf", "fc");
	}
	
}
