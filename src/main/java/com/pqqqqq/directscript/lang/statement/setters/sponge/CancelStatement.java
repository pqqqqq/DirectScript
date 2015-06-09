package com.pqqqqq.directscript.lang.statement.setters.sponge;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.util.command.CommandResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(prefix = "@", identifiers = {"CANCEL"})
public class CancelStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        Optional<Cancellable> eventOptional = scriptInstance.getCancellable();
        if (!eventOptional.isPresent()) {
            return StatementResult.failure();
        }

        Cancellable event = eventOptional.get();
        event.setCancelled(line.sequenceArg(scriptInstance, 0).getBoolean());

        // Specific cancel stuff
        if (event instanceof CommandEvent) {
            ((CommandEvent) event).setResult(CommandResult.success());
        }

        return StatementResult.success();
    }
}
