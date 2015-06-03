package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "!", identifiers = { "SCRIPT" }, compileTime = true)
public class ScriptDeclaration implements IStatement<String> {

    public StatementResult<String> run(ScriptInstance scriptInstance, Line line) {
        Literal scriptName = line.sequence(scriptInstance, 0);

        if (scriptName.isEmpty() || !scriptName.isString()) {
            throw new IllegalArgumentException(line.getArg(0) + " is not a string");
        }

        return StatementResult.<String>builder().success().result(scriptName.getString()).build();
    }
}
