package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that defines a script's {@link Trigger} and {@link Cause}s
 */
public class TriggerStatement extends Statement<Trigger> {

    public TriggerStatement() {
        super(Syntax.builder()
                .identifiers("trigger")
                .executionTime(ExecutionTime.COMPILE)
                .arguments(Arguments.of(Argument.from("TriggerArray")))
                .build());
    }

    @Override
    public Result<Trigger> run(Context ctx) {
        Trigger.Builder triggerBuilder = Trigger.builder().script(ctx.getScriptInstance().getScript());
        List<LiteralHolder> triggers = ctx.getLiteral("TriggerArray").getArray();

        for (LiteralHolder trigger : triggers) {
            String causeString = trigger.getData().getString();
            Optional<Cause> cause = Causes.getCause(causeString);

            checkState(cause.isPresent(), "Unknown cause: " + causeString);
            checkState(cause.get() != Causes.COMPILE, "The Compile cause cannot be used at runtime");
            triggerBuilder.cause(cause.get());
        }

        return Result.<Trigger>builder().success().result(triggerBuilder.build()).build();
    }
}
