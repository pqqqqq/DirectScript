package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.env.Variable;
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

    @Override
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.COMPILE;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"trigger"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("TriggerArray").build()
        };
    }

    @Override
    public Result<Trigger> run(Context ctx) {
        Trigger.Builder triggerBuilder = Trigger.builder().script(ctx.getScriptInstance().getScript());
        List<Variable> triggers = ctx.getLiteral(0).getArray();

        for (Variable trigger : triggers) {
            String causeString = trigger.getData().getString();
            Optional<Cause> cause = Causes.getCause(causeString);

            checkState(cause.isPresent(), "Unknown cause: " + causeString);
            triggerBuilder.cause(cause.get());
        }

        return Result.<Trigger>builder().success().result(triggerBuilder.build()).build();
    }
}
