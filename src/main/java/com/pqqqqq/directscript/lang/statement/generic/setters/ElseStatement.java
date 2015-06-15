package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.internal.setters.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that only executes if the 'if' statements above are all false
 */
public class ElseStatement extends Termination {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"} else"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[0];
    }

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line associatedLine = line.getLinkedLine();
        checkNotNull(associatedLine, "Unknown termination sequence");

        if (scriptInstance.getCause() != Causes.COMPILE) {
            Result statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult == null) {
                return Result.success();
            }

            Optional<Object> result = statementResult.getResult();
            checkState(result.isPresent(), "Reverse lookup for the if statement at line " + associatedLine.getAbsoluteNumber() + " failed");

            Boolean bool = (Boolean) result.get();
            if (!bool) {
                scriptInstance.setSkipLines(false); // Turn off skipping lines for this
                String trimBeginning = line.getLine().substring(6);
                Line truncatedLine = new Line(line.getAbsoluteNumber(), line.getAbsoluteNumber(), trimBeginning, false);
                if (truncatedLine.getStatement() != null) {
                    return truncatedLine.toContex(scriptInstance).run();
                }
            } else {
                scriptInstance.setSkipLines(true); // Skip lines if previously one was true
            }
        }

        return Result.success();
    }
}
