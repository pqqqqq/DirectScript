package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"exit"})
public class ExitStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        scriptInstance.setHaltExecution(true);
        return StatementResult.success();
    }
}
