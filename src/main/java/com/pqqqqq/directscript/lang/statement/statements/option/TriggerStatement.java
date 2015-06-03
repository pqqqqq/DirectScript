package com.pqqqqq.directscript.lang.statement.statements.option;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "@", identifiers = { "TRIGGER" }, compileTime = true)
public class TriggerStatement implements IStatement<Trigger> {

    public StatementResult<Trigger> run(ScriptInstance scriptInstance, Line line) {
        Trigger.Builder triggerBuilder = Trigger.builder().script(scriptInstance.getScript());

        for (int i = 1; i < line.getWords().length; i++) {
            String causeString = line.getLiteral(scriptInstance, i).getString();
            Optional<Cause> cause = Causes.getCause(causeString);

            if (!cause.isPresent()) {
                throw new IllegalArgumentException("Unknown cause: " + causeString);
            }

            triggerBuilder.cause(cause.get());
        }

        return StatementResult.<Trigger>builder().success().result(triggerBuilder.build()).build();
    }
}
