package scout.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Support extends Command {

	public Support(CommandCategory category) {
		super(category);
	}

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("support")
				.setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl())
				.setDescription("help me maintain maestro for years to come. your support is never required, but always appreciated :D")
				.addField("cash app", "$janmzn", true)
				.addField("venmo", "@janmzn", true)
				.addField("paypal", "@jansbankaccount", true)
				.addField("support?", "if you wanted technical support, add my discord: mute#9597", false)
				.setFooter("by mute | https://github.com/mvte");
		
		channel.sendMessageEmbeds(eb.build()).queue();
	}

	@Override
	public String getName() {
		return "support";
	}

	@Override
	public String getHelp() {
		return "provides ways to support maestro!";
	}
	

}
