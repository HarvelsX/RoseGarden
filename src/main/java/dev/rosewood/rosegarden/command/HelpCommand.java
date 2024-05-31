package dev.rosewood.rosegarden.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

public class HelpCommand extends BaseRoseCommand {

    private final BaseRoseCommand parent;
    private final CommandInfo commandInfo;

    public HelpCommand(RosePlugin rosePlugin, BaseRoseCommand parent, CommandInfo commandInfo) {
        super(rosePlugin);

        this.parent = parent;
        this.commandInfo = commandInfo;
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return this.commandInfo;
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        AbstractLocaleManager localeManager = this.rosePlugin.getManager(AbstractLocaleManager.class);

        ArgumentsDefinition argumentsDefinition = this.parent.getCommandArguments();
        if (argumentsDefinition.size() != 1)
            throw new IllegalStateException("Help command parent must have exactly 1 argument.");

        Argument argument = argumentsDefinition.get(0);
        if (!(argument instanceof Argument.SubCommandArgument subCommandArgument))
            throw new IllegalStateException("Help command parent must have a subcommand argument.");

        localeManager.sendCommandMessage(context.getSender(), "command-help-title");
        for (RoseCommand command : subCommandArgument.subCommands()) {
            String descriptionKey = command.getDescriptionKey();
            if (!command.canUse(context.getSender()) || descriptionKey == null)
                continue;

            StringPlaceholders stringPlaceholders = StringPlaceholders.of(
                    "cmd", context.getCommandLabel().toLowerCase(),
                    "subcmd", command.getName().toLowerCase(),
                    "args", command.getParametersString(context),
                    "desc", localeManager.getLocaleMessage(descriptionKey)
            );

            localeManager.sendSimpleCommandMessage(context.getSender(), "command-help-list-description" + (command.getCommandArguments().size() == 0 ? "-no-args" : ""), stringPlaceholders);
        }

        this.sendCustomHelpMessage(context);
    }

    protected void sendCustomHelpMessage(CommandContext context) {
        // Provides no default behavior
    }

}
