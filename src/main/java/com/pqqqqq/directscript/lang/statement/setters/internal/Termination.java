package com.pqqqqq.directscript.lang.statement.setters.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

/**
 * Created by Kevin on 2015-06-05.
 */
@Statement(identifiers = "}", executionTime = Statement.ExecutionTime.ALWAYS, useBrackets = false)
public class Termination implements IStatement {

    public StatementResult run(Line.LineContainer lineContainer) {
        ScriptInstance scriptInstance = lineContainer.getScriptInstance();
        Line line = lineContainer.getLine();

        Line associatedLine = scriptInstance.getScript().lookupStartingLine(line);
        if (associatedLine == null) {
            throw new IllegalStateException("Unknown termination sequence");
        }

        IStatement iStatement = associatedLine.getIStatement();
        if (scriptInstance.getCause() == Causes.COMPILE) {
            if (iStatement instanceof ScriptDeclaration) { // Script declaration
                return StatementResult.success();
            }
        } else {
            StatementResult statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult != null) { // Basically if the line was run
                scriptInstance.setSkipLines(false); // Ensure that skipping lines is off
                return StatementResult.success();
            }
        }

        return StatementResult.failure();
    }
}
