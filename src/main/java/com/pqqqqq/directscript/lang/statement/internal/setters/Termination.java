package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

/**
 * Created by Kevin on 2015-06-05.
 */
public class Termination extends Statement {

    @Override
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.ALWAYS;
    }

    @Override
    public boolean doesUseBrackets() {
        return false;
    }

    @Override
    public String getSplitString() {
        return " ";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"}"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[0];
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line associatedLine = scriptInstance.getScript().lookupStartingLine(line);
        if (associatedLine == null) {
            throw new IllegalStateException("Unknown termination sequence");
        }

        Statement statement = associatedLine.getStatement();
        if (scriptInstance.getCause() == Causes.COMPILE) {
            if (statement instanceof ScriptDeclaration) { // Script declaration
                return Result.success();
            }
        } else {
            Result statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult != null) { // Basically if the line was run
                scriptInstance.setSkipLines(false); // Ensure that skipping lines is off
                return Result.success();
            }
        }

        return Result.failure();
    }
}
