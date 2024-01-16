package scout;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import scout.commands.*;
import scout.commands.admin.Initialize;
import scout.commands.admin.Reload;
import scout.commands.sniping.CheckSnipes;
import scout.commands.sniping.RemoveSnipe;
import scout.commands.sniping.Snipe;
import static scout.commands.CommandCategory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new Help(BASIC, this));
        addCommand(new Hello(BASIC));
        addCommand(new Ping(BASIC));
        addCommand(new Support(BASIC));
        addCommand(new Coinflip(BASIC));
        addCommand(new Snipe(SNIPER));
        addCommand(new CheckSnipes(SNIPER));
        addCommand(new RemoveSnipe(SNIPER));
        addCommand(new Reload(ADMIN));
        addCommand(new Initialize(ADMIN));
    }

    /**
     * Registers a command to the command manager.
     * @param cmd The command to be added
     */
    private void addCommand(Command cmd) {
        // if the command already exists, then we don't want to add it to the command list.
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

        // the command being invoked
        String invoke = split[0].toLowerCase();
        Command cmd = this.getCommand(invoke);

        if (cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            cmd.handle(event, args);
        }

    }
}
