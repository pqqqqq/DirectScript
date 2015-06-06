package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.statements.generic.IfStatement;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

/**
 * Created by Kevin on 2015-06-05.
 */
@Statement(identifiers = "}", executionTime = Statement.ExecutionTime.ALWAYS)
public class Termination implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        Line associatedLine = scriptInstance.getScript().lookupStartingLine(line);
        if (associatedLine == null) {
            throw new IllegalStateException("Unknown termination sequence");
        }

        Optional<IStatement> iStatementOptional = associatedLine.getIStatement();
        if (!iStatementOptional.isPresent()) {
            throw new IllegalStateException("Unknown state, interface statement is not present in starting line");
        }

        IStatement iStatement = iStatementOptional.get();
        if (scriptInstance.getCause() == Causes.COMPILE) {
            if (iStatement instanceof ScriptDeclaration) { // Script declaration
                return StatementResult.success();
            }
        } else {
            if (iStatement instanceof IfStatement) { // If statement
                StatementResult ifResult = scriptInstance.getResultOf(associatedLine);
                Optional<Object> result = ifResult.getResult();

                if (!result.isPresent()) {
                    throw new IllegalStateException("Reverse lookup for the if statement at line " + associatedLine.getAbsoluteNumber() + " failed");
                }

                Boolean bool = (Boolean) result.get();
                if (!bool) {
                    scriptInstance.setSkipLines(false);
                }
            }
        }

        return StatementResult.failure();
    }
}
