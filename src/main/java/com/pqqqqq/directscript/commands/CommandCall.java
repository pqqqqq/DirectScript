package com.pqqqqq.directscript.commands;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * Created by Kevin on 2015-06-18.
 */
public class CommandCall implements CommandExecutor {
    private DirectScript plugin;

    private CommandCall(DirectScript plugin) {
        this.plugin = plugin;
    }

    public static CommandSpec build(DirectScript plugin) {
        return CommandSpec.builder().executor(new CommandCall(plugin)).description(Texts.of(TextColors.AQUA, "Calls the execution of a script")).permission("directscript.call")
                .arguments(GenericArguments.string(Texts.of("ScriptName")), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("Arguments")))).build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        String scriptName = commandContext.<String>getOne("ScriptName").get();
        Optional<String> argumentsOptional = commandContext.getOne("Arguments");
        String arguments = (argumentsOptional.isPresent() ? argumentsOptional.get() : "");

        Optional<Script> scriptOptional = Lang.instance().getScript(scriptName);
        if (!scriptOptional.isPresent()) {
            commandSource.sendMessage(Texts.of(TextColors.RED, "Unknown script: ", TextColors.WHITE, scriptName));
        } else {
            Script script = scriptOptional.get();

            ScriptInstance scriptInstance = ScriptInstance.builder().script(script).cause(Causes.CALL).eventVar("Arguments", arguments.split(" ")).build(); // TODO: Caused by the sender is they're a player?
            scriptInstance.execute();

            commandSource.sendMessage(Texts.of(TextColors.GREEN, "Script ran successfully."));
        }

        return CommandResult.success();
    }
}
