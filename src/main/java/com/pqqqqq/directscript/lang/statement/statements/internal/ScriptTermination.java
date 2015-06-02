package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "#", identifiers = { "ENDSCRIPT", "endscript" })
public class ScriptTermination implements IStatement {

    public StatementResult run(Line line) {
        return StatementResult.success();
    }
}
