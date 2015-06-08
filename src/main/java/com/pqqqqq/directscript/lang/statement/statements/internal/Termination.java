package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.statements.generic.ElseStatement;
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

        IStatement iStatement = associatedLine.getIStatement();
        if (scriptInstance.getCause() == Causes.COMPILE) {
            if (iStatement instanceof ScriptDeclaration) { // Script declaration
                return StatementResult.success();
            }
        } else {
            if (iStatement instanceof IfStatement || iStatement instanceof ElseStatement) {
                scriptInstance.setSkipLines(false);
                return StatementResult.success();
            }
        }

        return StatementResult.failure();
    }
}
