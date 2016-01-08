package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Kevin on 2015-11-15.
 */
public class CommandPublicVariables implements CommandExecutor {
    private DirectScript plugin;

    private CommandPublicVariables(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandPublicVariables(plugin)).description(Text.of(TextColors.AQUA, "Public variable manipulation")).permission("directscript.public-variables")
                .arguments(GenericArguments.string(Text.of("Action")), GenericArguments.optional(GenericArguments.string(Text.of("VariableName"))), GenericArguments.optional(GenericArguments.string(Text.of("Type")))).build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Lang lang = Lang.instance();
        String action = commandContext.<String>getOne("Action").get();
        Optional<String> variableName = commandContext.<String>getOne("VariableName");

        switch (action.toLowerCase()) {
            case "add":
            case "set":
                if (!commandContext.hasAny("VariableName")) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Additions require the name of the variable to proceed."));
                } else {
                    Optional<Literal.Types> typesOptional = Optional.empty();
                    if (commandContext.hasAny("Type")) {
                        typesOptional = Literal.Types.fromName(commandContext.<String>getOne("Type").get());
                    }

                    lang.addVariable(new Variable(variableName.get(), lang, Literal.Literals.EMPTY, typesOptional));
                    commandSource.sendMessage(Text.of(TextColors.GREEN, "Addition successful."));
                }

                break;
            case "delete":
            case "remove":
                if (!commandContext.hasAny("VariableName")) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Removals require the name of the variable to proceed."));
                } else {
                    if (lang.removeVariable(variableName.get())) {
                        commandSource.sendMessage(Text.of(TextColors.GREEN, "Removal successful."));
                    } else {
                        commandSource.sendMessage(Text.of(TextColors.RED, "Could not find this variable."));
                    }
                }

                break;
            case "reset":
                if (!commandContext.hasAny("VariableName")) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Resets require the name of the variable to proceed."));
                } else {
                    Optional<Variable> variable = lang.getVariable(variableName.get());
                    if (variable.isPresent()) {
                        variable.get().setDatum(Literal.Literals.EMPTY);
                        commandSource.sendMessage(Text.of(TextColors.GREEN, "Reset successful."));
                    } else {
                        commandSource.sendMessage(Text.of(TextColors.RED, "Could not find this variable."));
                    }
                }

                break;
            case "clearall":
                lang.clear();
                commandSource.sendMessage(Text.of(TextColors.GREEN, "Clear successful."));
                break;
            default:
                commandSource.sendMessage(Text.of(TextColors.RED, "Unknown actions. Valid actions are: add/remove/reset/clearall"));
                break;
        }

        return CommandResult.success();
    }
}
