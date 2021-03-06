package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.command.SendCommandEvent;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that cancels the event that causes the script's trigger, if possible
 */
public class CancelStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("cancel")
            .prefix("@")
            .arguments(Arguments.empty(), Arguments.of(GenericArguments.withName("CancelCondition")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        Optional<Event> eventOptional = ctx.getScriptInstance().getEvent();
        if (!eventOptional.isPresent()) {
            return Result.failure();
        }

        Event event = eventOptional.get();
        if (!(event instanceof Cancellable)) {
            return Result.failure();
        }

        ((Cancellable) event).setCancelled(ctx.getLiteral("CancelCondition", true).getBoolean()); // Default true

        // Specific cancel stuff
        if (event instanceof SendCommandEvent) {
            ((SendCommandEvent) event).setResult(CommandResult.success());
        }

        return Result.success();
    }
}
