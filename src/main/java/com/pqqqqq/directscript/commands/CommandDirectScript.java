package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

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
                .child(CommandReload.build(plugin), "reload").build();
    }

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        commandSource.sendMessage(Texts.of(TextColors.GREEN, DirectScript.NAME, TextColors.WHITE, " V", DirectScript.VERSION, TextColors.GREEN, " created by: ", TextColors.WHITE, DirectScript.AUTHORS));
        commandSource.sendMessage(Texts.of(TextColors.RED, "/script <reload>"));
        return CommandResult.success();
    }
}
