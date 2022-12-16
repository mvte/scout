package scout;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import scout.commands.*;
import scout.commands.sniping.Check;
import scout.commands.sniping.RemoveSnipe;
import scout.commands.tracking.Track;
import scout.commands.sniping.Snipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Help());
        addCommand(new Hello());
        addCommand(new Ping());
        addCommand(new Support());
        addCommand(new Coinflip());
        addCommand(new Snipe());
        addCommand(new RemoveSnipe());
        addCommand(new Check());
        addCommand(new Track());
    }

    /**
     * Registers a command to the command manager.
     * @param cmd The command to be added
     */
    private void addCommand(Command cmd) {
        // If the command already exists, then we don't want to add it to the command list.
        boolean exists = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (exists)
            throw new IllegalArgumentException("this command already exists");

        commands.add(cmd);
    }

    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Gets a registered command from the command list.
     * @param search the getName() String value of a command
     * @return the command, if it exists
     */
    @Nullable
    public Command getCommand(String search) {
        search = search.toLowerCase();

        for (Command cmd : this.commands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search))
                return cmd;
        }

        return null;
    }

    /**
     * Processes the input of a MessageReceivedInput for a command.
     * @param event the MessageReceivedEvent
     * @param prefix the prefix
     */
    void handle(MessageReceivedEvent event, String prefix) {
        // Splits the command into its constituent parts, without the command prefix
        // (the command is the first argument)
        String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        // The command being invoked
        String invoke = split[0].toLowerCase();
        Command cmd = this.getCommand(invoke);

        if (cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            cmd.handle(event, args);
        }

    }
}
