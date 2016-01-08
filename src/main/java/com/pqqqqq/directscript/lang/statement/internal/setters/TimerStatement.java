package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * Represents a timer delay statement
 * @see com.pqqqqq.directscript.lang.trigger.cause.Cause.TimerCause
 */
public class TimerStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("timer")
            .executionTime(ExecutionTime.COMPILE)
            .arguments(Arguments.of(GenericArguments.withName("TimerDelay")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        ctx.getScript().getCauseData().setTimerDelay(ctx.getLiteral("TimerDelay").getNumber().longValue());
        return Result.success();
    }
}
