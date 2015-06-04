package com.pqqqqq.directscript.lang.statement.statements.generic;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "", identifiers = { "print" })
public class PrintStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        DirectScript.instance().getLogger().info(line.sequenceArg(scriptInstance, 0).getString());
        return StatementResult.success();
    }
}
