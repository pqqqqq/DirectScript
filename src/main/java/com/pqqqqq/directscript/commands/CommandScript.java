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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by Kevin on 2015-06-29.
 */
public class CommandScript implements CommandExecutor {
    private DirectScript plugin;

    private CommandScript(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandScript(plugin)).description(Texts.of(TextColors.AQUA, "Creates a new script in a script file")).permission("directscript.script")
                .arguments(GenericArguments.string(Texts.of("ScriptFile")), GenericArguments.string(Texts.of("ScriptName")), GenericArguments.optional(GenericArguments.string(Texts.of("Trigger")))).build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String scriptFile = args.<String>getOne("ScriptFile").get();
        String fileName = "/" + (scriptFile.trim().endsWith(".ds") ? scriptFile.trim() : scriptFile.trim() + ".ds");

        String scriptName = args.<String>getOne("ScriptName").get();
        File file = new File(Reader.scriptsFile() + fileName);

        String trigger = args.<String>getOne("Trigger").orNull();

        if (!file.exists()) {
            src.sendMessage(Texts.of(TextColors.RED, "This file doesn't exist. Use ", TextColors.WHITE, "/script file"));
        } else {
            try {
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.newLine();
                bw.newLine();
                bw.write("script(\"" + scriptName + "\") {");
                bw.newLine();

                if (trigger != null) {
                    bw.write("\ttrigger(\"" + trigger + "\")");
                    bw.newLine();
                }

                bw.write("}");
                bw.newLine();

                bw.flush();
                bw.close();

                src.sendMessage(Texts.of(TextColors.GREEN, "Script created successfully"));
            } catch (Exception e) {
                src.sendMessage(Texts.of(TextColors.RED, e.getMessage()));
            }
        }
        return CommandResult.success();
    }
}
