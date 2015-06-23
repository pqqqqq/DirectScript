package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that only executes if the 'if' statements above are all false
 */
public class ElseStatement extends Statement {

    public ElseStatement() {
        super(Syntax.builder()
                .identifiers("} else")
                .suffix("{")
                .brackets()
                .build());
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line associatedLine = line.getOpeningBrace();
        checkNotNull(associatedLine, "Unknown termination sequence");

        if (scriptInstance.isRuntime()) {
            Result statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult == null) {
                return null;
            }

            Optional<Object> result = statementResult.getResult();
            checkState(result.isPresent(), "Reverse lookup for the if statement at line " + associatedLine.getAbsoluteNumber() + " failed");

            Boolean bool = (Boolean) result.get();
            if (!bool) {
                scriptInstance.setSkipLines(false); // Turn off skipping lines for this
                String trimBeginning = line.getLine().substring(6);
                Line truncatedLine = new Line(line.getAbsoluteNumber(), line.getScriptNumber(), trimBeginning.trim());

                if (truncatedLine.isRunnable()) {
                    return truncatedLine.toContex(scriptInstance).run();
                }
            } else {
                scriptInstance.setSkipLines(true); // Skip lines if previously one was true
                return Result.builder().success().result(true).build(); // Also build this one as true
            }
        }

        return null;
    }
}
