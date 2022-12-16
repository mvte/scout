package scout.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Represents a command invocation. Every command must implement this interface,
 * and then be registered in the command manager.
 */
public interface Command {

    CommandCategory category = CommandCategory.BASIC;

    /**
     * Defines behavior for a command.
     * @param event, contains all context for the message (guild, user, member, etc.)
     * @param args, contains all arguments of the command omitting the first prefixed command
     */
    void handle(MessageReceivedEvent event, List<String> args);

    /**
     * Gets the name of the command. Will be used by CommandManager to identify when a command
     * is called.
     * @return the name of the command
     */
    String getName();

    /**
     * Returns the help message when .help [command] is called
     * @param prefix the server's prefix
     * @return help message
     */
    String getHelp(String prefix);

    /**
     * Returns the list of aliases a certain command has. Allows the same command to be invoked with other arguments.
     * Please ensure that no two commands have duplicate aliases.
     * @return the list of aliases for a command
     */
    default List<String> getAliases() {
        return List.of();
    }

}
