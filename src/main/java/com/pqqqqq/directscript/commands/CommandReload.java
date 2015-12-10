package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
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
        plugin.getConfig().load();
        Lang.instance().reloadScripts();

        commandSource.sendMessage(Texts.of(TextColors.AQUA, "Scripts/config reloaded."));
        return CommandResult.success();
    }
}
