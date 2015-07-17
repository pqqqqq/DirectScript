package com.pqqqqq.directscript.commands;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Reader;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

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
        return CommandSpec.builder().executor(new CommandFile(plugin)).description(Texts.of(TextColors.AQUA, "Creates a new script file")).permission("directscript.file")
                .arguments(GenericArguments.string(Texts.of("ScriptName"))).build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String scriptName = args.<String>getOne("ScriptName").get();
        String fileName = "/" + (scriptName.trim().endsWith(".ds") ? scriptName.trim() : scriptName.trim() + ".ds");

        try {
            File file = new File(Reader.scriptsFile() + fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();

            src.sendMessage(Texts.of(TextColors.GREEN, "File created successfully"));
        } catch (Exception e) {
            src.sendMessage(Texts.of(TextColors.RED, e.getMessage()));
        }
        return CommandResult.success();
    }
}