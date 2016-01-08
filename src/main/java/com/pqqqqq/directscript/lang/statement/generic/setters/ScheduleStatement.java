package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

/**
 * Created by Kevin on 2015-12-18.
 * A statement that reinforces a schedule for its block code
 */
public class ScheduleStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("schedule", "task")
            .suffix("{")
            .arguments(Arguments.empty())
            .arguments(Arguments.of(GenericArguments.withName("Delay")))
            .arguments(Arguments.of(GenericArguments.withName("Delay"), ",", GenericArguments.withName("Interval")))
            .arguments(Arguments.of(GenericArguments.withName("Delay"), ",", GenericArguments.withName("Interval"), ",", GenericArguments.withName("Asynchronous")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("Schedule statements must have internal blocks."));
        boolean async = ctx.getLiteral("Asynchronous", false).getBoolean();
        Literal delay = ctx.getLiteral("Delay");
        Literal interval = ctx.getLiteral("Literal");

        Task.Builder builder = DirectScript.instance().getGame().getScheduler().createTaskBuilder().execute(internalBlock.toRunnable(ctx.getScriptInstance()));
        if (async) {
            builder.async();
        }

        if (!delay.isEmpty()) {
            long time = Utilities.getFormattedTime(delay.getString());
            if (time > 0) {
                builder.delay(time, TimeUnit.MICROSECONDS);
            }
        }

        if (!interval.isEmpty()) {
            long time = Utilities.getFormattedTime(interval.getString());
            if (time > 0) {
                builder.interval(time, TimeUnit.MICROSECONDS);
            }
        }

        builder.submit(DirectScript.instance()); // Run task
        return Result.success();
    }
}
