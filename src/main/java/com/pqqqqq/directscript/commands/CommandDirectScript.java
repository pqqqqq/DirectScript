package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Kevin on 2015-06-02.
 */
public class CommandDirectScript implements CommandExecutor {
    private DirectScript plugin;

    private CommandDirectScript(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandDirectScript(plugin)).description(Texts.of(TextColors.AQUA, "Main plugin command"))
                .child(CommandReload.build(plugin), "reload").child(CommandCall.build(plugin), "call", "run").child(CommandFile.build(plugin), "file").child(CommandScript.build(plugin), "script")
                .child(CommandPublicVariables.build(plugin), "publicvariables", "publicvars", "pubvars", "vars", "pv").build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        commandSource.sendMessage(Texts.of(TextColors.GREEN, DirectScript.NAME, TextColors.WHITE, " V", DirectScript.VERSION, TextColors.GREEN, " created by: ", TextColors.WHITE, DirectScript.AUTHORS));
        commandSource.sendMessage(Texts.of(TextColors.RED, "/script <reload|call>"));
        return CommandResult.success();
    }
}
