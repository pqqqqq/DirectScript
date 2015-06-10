package com.pqqqqq.directscript.lang.statement.setters.sponge;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.util.command.CommandResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(prefix = "@", identifiers = {"cancel"})
public class CancelStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        Optional<Event> eventOptional = line.getScriptInstance().getEvent();
        if (!eventOptional.isPresent()) {
            return StatementResult.failure();
        }

        Event event = eventOptional.get();
        if (!(event instanceof Cancellable)) {
            return StatementResult.failure();
        }

        ((Cancellable) event).setCancelled(line.getLiteral(0).or(true).getBoolean()); // Default true

        // Specific cancel stuff
        if (event instanceof CommandEvent) {
            ((CommandEvent) event).setResult(CommandResult.success());
        }

        return StatementResult.success();
    }
}
