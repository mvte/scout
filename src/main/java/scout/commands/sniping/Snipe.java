package scout.commands.sniping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import scout.Scout;
import scout.commands.Command;
import scout.commands.CommandCategory;
import scout.sniper.SnipeFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Snipe extends Command {

	public Snipe(CommandCategory category) {
		super(category);
	}

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		SnipeFactory snipeFactory = new SnipeFactory();
		long userId = event.getAuthor().getIdLong();

		if(args.size() != 1) {
			channel.sendMessage("incorrect amount of arguments. use the help command for usage").queue();
			return;
		}

		scout.sniper.Snipe snipe = snipeFactory.createSnipe(args.get(0), true);
		if(snipe == null) {
			channel.sendMessage("something went wrong creating your snipe! check your url?").queue();
			return;
		}

		long userID = event.getAuthor().getIdLong();
		if(snipe.getUsers().contains(userID)) {
			channel.sendMessage("you are already sniping this item!").queue();
			return;
		}

		try {
			Connection conn = DriverManager.getConnection(
					System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASS"));
			String insert = "INSERT INTO snipes VALUES (?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setLong(1, userID);
			stmt.setString(2, args.get(0));
			stmt.setString(3, snipe.getUrlType().toString());
			stmt.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
			channel.sendMessage("something went wrong adding your snipe! (").queue();
		}

		snipe.getUsers().add(userID);

		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("successfully added snipe request")
				.setThumbnail(Scout.bot.getSelfUser().getAvatarUrl())
				.addField(snipe.getItemName(), "you will be notified when this item is available", false)
				.setFooter(event.getAuthor().getName())
				.setTimestamp(java.time.Instant.now());
		channel.sendMessageEmbeds(eb.build()).queue();
	}

	@Override
	public String getName() {
		return "snipe";
	}

	@Override
	public String getHelp() {
		return "the bot will send you a message when an item you want is in stock. currently, this bot only supports " +
				"sniping rutgers courses \nusage: `.snipe <link/code>`";
	}
	
}
