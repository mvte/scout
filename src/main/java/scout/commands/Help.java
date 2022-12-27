package scout.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import scout.CommandManager;

import java.util.List;

public class Help extends Command {

    private final CommandManager manager;

    public Help(CommandCategory category, CommandManager manager) {
        super(category);
        this.manager = manager;
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {

        if(args.isEmpty()) {
            buildAndSendHelp(event.getChannel());
        }

    }

    public void buildAndSendHelp(MessageChannel channel)  {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("scout help")
                .setFooter("by mute | github.com/mvte")
                .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());

        for(CommandCategory category : CommandCategory.values()) {
            if(category == CommandCategory.ADMIN) continue;

            StringBuilder sb = new StringBuilder();
            for(Command command :  manager.getCommands()) {
                if(command.getCategory().equals(category)) {
                    sb.append("`").append(command.getName()).append("`, ");
                }
            }
            sb.delete(sb.length() - 2, sb.length());
            eb.addField(category.toString().toLowerCase(), sb.toString(), false);
        }

        channel.sendMessageEmbeds(eb.build()).queue();
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
