package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
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
public class CommandReload implements CommandExecutor {
    private DirectScript plugin;

    private CommandReload(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandReload(plugin)).description(Texts.of(TextColors.AQUA, "Reloads the config and scripts.")).permission("directscript.reload").build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Lang.instance().reloadScripts();
        commandSource.sendMessage(Texts.of(TextColors.AQUA, "Scripts/config reloaded."));
        return CommandResult.success();
    }
}
