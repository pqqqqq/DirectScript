package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Reader;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;

/**
 * Created by Kevin on 2015-06-29.
 */
public class CommandFile implements CommandExecutor {
    private DirectScript plugin;

    private CommandFile(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandFile(plugin)).description(Text.of(TextColors.AQUA, "Creates a new script file")).permission("directscript.file")
                .arguments(GenericArguments.string(Text.of("ScriptName"))).build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String scriptName = args.<String>getOne("ScriptName").get();
        String fileName = "/" + (scriptName.trim().endsWith(".ds") ? scriptName.trim() : scriptName.trim() + ".ds");

        try {
            File file = new File(Reader.scriptsFile() + fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();

            src.sendMessage(Text.of(TextColors.GREEN, "File created successfully"));
        } catch (Exception e) {
            src.sendMessage(Text.of(TextColors.RED, e.getMessage()));
        }
        return CommandResult.success();
    }
}
