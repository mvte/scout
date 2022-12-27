package scout.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Represents a command invocation. Every command must implement this interface,
 * and then be registered in the command manager.
 */
public abstract class Command {

    protected CommandCategory category;

    public Command(CommandCategory category) {
        this.category = category;
    }

    /**
     * Defines behavior for a command.
     * @param event, contains all context for the message (guild, user, member, etc.)
     * @param args, contains all arguments of the command omitting the first prefixed command
     */
    public abstract void handle(MessageReceivedEvent event, List<String> args);

    /**
     * Gets the name of the command. Will be used by CommandManager to identify when a command
     * is called.
     * @return the name of the command
     */
    public abstract String getName();

    /**
     * Returns the help message when .help [command] is called
     * @return help message
     */
    public abstract String getHelp();

    /**
     * Returns the list of aliases a certain command has. Allows the same command to be invoked with other arguments.
     * Please ensure that no two commands have duplicate aliases.
     * @return the list of aliases for a command
     */
    public List<String> getAliases() {
        return List.of();
    }

    /**
     * Returns the category of a command. This is used to organize commands in the .help command.
     * @return the category of a command
     */
    CommandCategory getCategory() {
        return category;
    }

}
