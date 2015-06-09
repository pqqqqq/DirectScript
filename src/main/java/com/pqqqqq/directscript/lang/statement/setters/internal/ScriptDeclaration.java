package com.pqqqqq.directscript.lang.statement.setters.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = {"SCRIPT"}, suffix = "{", executionTime = Statement.ExecutionTime.COMPILE)
public class ScriptDeclaration implements IStatement<String> {

    public StatementResult<String> run(ScriptInstance scriptInstance, Line line) {
        Literal scriptName = line.sequenceArg(scriptInstance, 0);

        if (scriptName.isEmpty() || !scriptName.isString()) {
            throw new IllegalArgumentException(line.getArg(0) + " is not a string");
        }

        return StatementResult.<String>builder().success().result(scriptName.getString()).build();
    }
}
