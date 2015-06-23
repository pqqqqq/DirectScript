package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * Represents a timer delay statement
 */
public class TimerStatement extends Statement {

    public TimerStatement() {
        super(Syntax.builder()
                .identifiers("timer")
                .executionTime(ExecutionTime.COMPILE)
                .arguments(Arguments.of(Argument.from("TimerDelay")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        ctx.getScript().getCauseData().setTimerDelay(ctx.getLiteral("TimerDelay").getNumber().longValue());
        return Result.success();
    }
}
