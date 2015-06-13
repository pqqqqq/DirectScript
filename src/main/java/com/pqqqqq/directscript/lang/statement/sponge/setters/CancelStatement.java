package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.util.command.CommandResult;

/**
 * Created by Kevin on 2015-06-09.
 */
public class CancelStatement extends SpongeStatement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"cancel"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("CancelCondition").optional().build()
        };
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

        ((Cancellable) event).setCancelled(ctx.getLiteral(0, true).getBoolean()); // Default true

        // Specific cancel stuff
        if (event instanceof CommandEvent) {
            ((CommandEvent) event).setResult(CommandResult.success());
        }

        return Result.success();
    }
}
