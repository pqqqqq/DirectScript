package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that defines a script's {@link Trigger} and {@link Cause}s
 */
public class TriggerStatement extends Statement<Trigger> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("trigger")
            .executionTime(ExecutionTime.COMPILE)
            .arguments(Arguments.of(GenericArguments.withName("TriggerArray")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Trigger> run(Context ctx) {
        Trigger.Builder triggerBuilder = Trigger.builder().script(ctx.getScriptInstance().getScript());
        List<Literal> triggers = ctx.getLiteral("TriggerArray").getArray();

        for (Literal trigger : triggers) {
            String causeString = trigger.getString();
            Optional<Cause> cause = Causes.getCause(causeString);

            checkState(cause.isPresent(), "Unknown cause: " + causeString);
            checkState(cause.get() != Causes.COMPILE, "The Compile cause cannot be used at runtime");
            triggerBuilder.cause(cause.get());
        }

        return Result.<Trigger>builder().success().result(triggerBuilder.build()).build();
    }
}
