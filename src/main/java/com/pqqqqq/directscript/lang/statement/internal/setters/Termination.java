package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-05.
 * A statement that terminates any blocked code
 */
public class Termination extends Statement {

    public Termination() {
        super(Syntax.builder()
                .identifiers("}")
                .brackets()
                .executionTime(ExecutionTime.ALWAYS)
                .build());
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line associatedLine = line.getOpeningBrace();
        if (associatedLine == null) {
            throw new IllegalStateException("Unknown termination sequence");
        }

        Statement statement = associatedLine.getStatement();
        if (!scriptInstance.isRuntime()) {
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

        return Result.success();
    }
}
