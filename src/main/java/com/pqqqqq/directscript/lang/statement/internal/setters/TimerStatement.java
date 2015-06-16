package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * Represents a timer delay statement
 */
public class TimerStatement extends Statement {

    @Override
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.COMPILE;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"timer"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("TimerDelay").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        ctx.getScript().getCauseData().setTimerDelay(ctx.getLiteral(0).getNumber().longValue());
        return Result.success();
    }
}
